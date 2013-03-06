package org.kuali.kfs.module.ar.batch;

import java.util.Date;

import org.kuali.kfs.module.ar.batch.service.LockboxLoadService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 *
 * @author mramawat
 * FSKD-688 iu-custom
 * Lockbox Load step to load lockbox payment files
 *
 */
public class LockboxLoadStep extends AbstractStep {

	LockboxLoadService lockboxLoadService;


	@Override
    public boolean execute(String jobName, Date jobRunDate)
			throws InterruptedException {

		return lockboxLoadService.loadFile();

	}


	public void setLockboxLoadService(LockboxLoadService lockboxLoadService) {
		this.lockboxLoadService = lockboxLoadService;
	}

}
