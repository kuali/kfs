package edu.arizona.kfs.module.ld.batch;

import java.util.Date;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import edu.arizona.kfs.module.ld.service.EreSweepParameterService;
import edu.arizona.kfs.module.ld.service.EreSweepService;

public class EreSweepStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EreSweepStep.class);
	
	private EreSweepParameterService ereSweepParameterService;
	private EreSweepService ereSweepService;
	
	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter execute() " + System.currentTimeMillis());
		}
		
		String param = KFSConstants.EMPTY_STRING;
		boolean result = false;
		try {
			param = getEreSweepParameterService().getFringeBudgetSweepIndParameters();
		}
		catch (Exception e) {
			throw new RuntimeException("Parameter for Fringe Sweep Step is not set up. Problem Ocurred in execute(): " + e);
		}
		
		if ((param != null) && (param.equalsIgnoreCase(KFSConstants.ACTIVE_INDICATOR))) {
			getEreSweepService().processEreSweep(jobRunDate);
			result = true;
		} else {
			result = false;
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit execute() " + System.currentTimeMillis());
		}
		
		return result;
	}


	public EreSweepParameterService getEreSweepParameterService() {
		return ereSweepParameterService;
	}


	public void setEreSweepParameterService(EreSweepParameterService ereSweepParameterService) {
		this.ereSweepParameterService = ereSweepParameterService;
	}


	public EreSweepService getEreSweepService() {
		return ereSweepService;
	}


	public void setEreSweepService(EreSweepService ereSweepService) {
		this.ereSweepService = ereSweepService;
	}
	
}
