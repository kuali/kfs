package org.kuali.kfs.sys.batch;

import java.util.Date;


/**
 * StopBatchContainerStep triggers the BatchContainerStep to shut itself down.
 *
 */
public class StopBatchContainerStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StopBatchContainerStep.class);

    /**
     * The BatchContainerStep recognizes the name of this Step and exits without executing this method.
     */
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		
		LOG.info("Stopping Job: "+ jobName +", Date: "+ jobRunDate);
		return true;
	}
}
