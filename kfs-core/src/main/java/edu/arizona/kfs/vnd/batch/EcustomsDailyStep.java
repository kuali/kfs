package edu.arizona.kfs.vnd.batch;

import java.util.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;

import edu.arizona.kfs.vnd.service.EcustomsService;

/**
 * UAF-66 MOD-PA7000-02 ECustoms - US Export Compliance
 *
 * This step generates the daily eCustoms data files.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */

public class EcustomsDailyStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(EcustomsDailyStep.class);
    private static transient volatile EcustomsService ecustomsService;

    private EcustomsService getEcustomsService() {
        if (ecustomsService == null) {
            ecustomsService = SpringContext.getBean(EcustomsService.class);
        }
        return ecustomsService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        boolean retval = false;
        try {
            retval = getEcustomsService().createEcustomsDailyFile(jobName, jobRunDate);
        } catch (Exception e) {
            LOG.error("Error when running EcustomsDailyStep: " + e.getMessage());
            throw new InterruptedException(e.getMessage());
            // retval = false;
        }
        return retval;
    }

}