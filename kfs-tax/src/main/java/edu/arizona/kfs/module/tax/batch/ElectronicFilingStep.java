package edu.arizona.kfs.module.tax.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.businessobject.ElectronicFile;
import edu.arizona.kfs.module.tax.businessobject.ElectronicFileException;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.ElectronicFilingService;
import edu.arizona.kfs.module.tax.service.TaxForm1099GeneratorService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;

public class ElectronicFilingStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicFilingStep.class);

    private ElectronicFilingService electronicFilingService;
    private TaxParameterHelperService taxParameterHelperService;
    private TaxForm1099GeneratorService taxForm1099GeneratorService;

    public void setElectronicFilingService(ElectronicFilingService electronicFilingService) {
        this.electronicFilingService = electronicFilingService;
    }

    public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
        this.taxParameterHelperService = taxParameterHelperService;
    }

    public void setTaxForm1099GeneratorService(TaxForm1099GeneratorService taxForm1099GeneratorService) {
        this.taxForm1099GeneratorService = taxForm1099GeneratorService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        try {
            Payer payer = taxParameterHelperService.getDefaultPayer();
            int year = taxParameterHelperService.getTaxYear();
            File folder = taxForm1099GeneratorService.getTaxYearFolder(year);

            ElectronicFile ef = electronicFilingService.getElectronicFile(new Integer(year));

            List<ElectronicFileException> listErrors = ef.validateFile();

            if (listErrors.isEmpty()) {
                ef.writeElectronicFile(folder.getAbsolutePath() + "/ORIG." + payer.getTransCd() + "." + TaxConstants.FILE_TIMESTAMP.format(new java.util.Date()) + GeneralLedgerConstants.BatchFileSystem.TEXT_EXTENSION);
            } else {
                File file = new File(folder.getAbsolutePath() + "/exceptionreport-" + TaxConstants.FILE_TIMESTAMP.format(new java.util.Date()) + KFSConstants.ReportGeneration.CSV_FILE_EXTENSION);
                FileOutputStream fop = new FileOutputStream(file);

                for (ElectronicFileException err : listErrors) {
                    fop.write((err.toCsvString() + "\n").getBytes());
                }
                fop.close();
            }
        } catch (Exception err) {
            LOG.error("ElectronicFilingStep", err);
            return false;

        }

        return true;
    }

}
