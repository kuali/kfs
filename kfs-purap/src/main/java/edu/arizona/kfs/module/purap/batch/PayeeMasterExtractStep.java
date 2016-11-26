package edu.arizona.kfs.module.purap.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;

public class PayeeMasterExtractStep extends AbstractStep {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeMasterExtractStep.class);

	private TaxReporting1099Service taxReporting1099Service;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		LOG.info("started master payee extraction step of job " + jobName + "at " + new Date());
		return taxReporting1099Service.extractPayees(jobName, jobRunDate);
	}

	public void setTaxReporting1099Service(TaxReporting1099Service taxReporting1099Service) {
		this.taxReporting1099Service = taxReporting1099Service;
	}

}
