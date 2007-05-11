package org.kuali.module.cg.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.service.CfdaUpdateResults;
import org.kuali.module.cg.service.impl.CfdaServiceImpl;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: May 8, 2007
 * Time: 5:42:16 PM
 */
public class CfdaBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CfdaBatchStep.class);

    private CfdaService cfdaService;
    public boolean execute() throws InterruptedException {
        try {
            CfdaUpdateResults results = cfdaService.update();
        } catch(IOException ioe) {
            LOG.warn("Exception while updating CFDA codes.", ioe);
            return false;
        }
        return true;
    }

    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }
}
