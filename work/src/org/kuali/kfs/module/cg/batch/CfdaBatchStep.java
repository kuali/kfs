package org.kuali.module.cg.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CfdaService;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: May 8, 2007
 * Time: 5:42:16 PM
 */
public class CfdaBatchStep extends AbstractStep {

    private CfdaService cfdaService;
    public boolean execute() throws InterruptedException {
        cfdaService.update();
        return true;
    }

    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }
}
