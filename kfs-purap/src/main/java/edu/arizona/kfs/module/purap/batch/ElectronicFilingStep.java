package edu.arizona.kfs.module.purap.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import edu.arizona.kfs.module.purap.TaxHelper;
import edu.arizona.kfs.module.purap.document.service.ElectronicFilingService;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;
import edu.arizona.kfs.tax.businessobject.ElectronicFile;
import edu.arizona.kfs.tax.businessobject.ElectronicFileException;
import edu.arizona.kfs.tax.businessobject.Payer;
import edu.arizona.kfs.tax.service.TaxParameterHelperService;

public class ElectronicFilingStep extends AbstractStep {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicFilingStep.class);
	
	private ElectronicFilingService electronicFilingService;
	private ConfigurationService configurationService;
	private TaxReporting1099Service taxReporting1099Service;
	private TaxParameterHelperService taxParameterHelperService;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		try {
			Payer payer = taxReporting1099Service.getDefaultPayer();
			int year = getTaxParameterHelperService().getTaxYear();
			File folder = TaxHelper.getTaxYearFolder(year, configurationService, LOG);
			
			ElectronicFile ef = electronicFilingService.getElectronicFile(new Integer(year));
			
			List<ElectronicFileException> listErrors = ef.validateFile();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
			if (listErrors.isEmpty()) {
				ef.writeElectronicFile(folder.getAbsolutePath() + "/ORIG." + payer.getTransCd() + "." + sdf.format(new java.util.Date()) + ".txt");
			}
			else {
				File file = new File(folder.getAbsolutePath() + "/exceptionreport-" + sdf.format(new java.util.Date()) + ".csv");
				FileOutputStream fop = new FileOutputStream(file);
				
				for(ElectronicFileException err : listErrors) {
					fop.write((err.toCsvString() + "\n").getBytes());
				}
				fop.close();
			}
		}
		catch (Exception err) {
			LOG.error("ElectronicFilingStep", err);
			return false;
			
		}
		
		return true;
	}
	
	public TaxParameterHelperService getTaxParameterHelperService() {
		if (taxParameterHelperService == null) {
			taxParameterHelperService = SpringContext.getBean(TaxParameterHelperService.class);
		}
		return taxParameterHelperService;
	}

	public void setElectronicFilingService(ElectronicFilingService electronicFilingService) {
		this.electronicFilingService = electronicFilingService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setTaxReporting1099Service(TaxReporting1099Service taxReporting1099Service) {
		this.taxReporting1099Service = taxReporting1099Service;
	}
	
	

}
