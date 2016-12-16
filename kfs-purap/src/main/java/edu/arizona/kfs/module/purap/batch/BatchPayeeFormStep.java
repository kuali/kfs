package edu.arizona.kfs.module.purap.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;

public class BatchPayeeFormStep extends AbstractStep {
	
	private TaxReporting1099Service taxReporting1099Service;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		boolean retval = false;
		retval = taxReporting1099Service.generateBatchPayeeForms();
		
		return retval;
	}

	public void setTaxReporting1099Service(TaxReporting1099Service taxReporting1099Service) {
		this.taxReporting1099Service = taxReporting1099Service;
	}
	
}
