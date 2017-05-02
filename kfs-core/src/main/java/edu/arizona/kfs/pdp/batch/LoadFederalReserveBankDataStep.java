package edu.arizona.kfs.pdp.batch;

import java.util.Date;

import edu.arizona.kfs.sys.KFSConstants;

public class LoadFederalReserveBankDataStep extends org.kuali.kfs.pdp.batch.LoadFederalReserveBankDataStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadFederalReserveBankDataStep.class);

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		LOG.debug("execute() started");
		//for the moment this job will be deprecated as new functionality will be addressed on UAF-3059 per epic specs
		throw new RuntimeException(KFSConstants.DEPRECATED_PDP_LOAD_FEDERAL_RESERVE_BANK_DATA_STEP_JOB);
		
	}

}
