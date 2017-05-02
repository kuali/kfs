package edu.arizona.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderUpdateService;

/**
 * This step will call a service method to insert/update the procurement card holder records from the loaded procurement card holder table.
 */
public class ProcurementCardHolderUpdateStep extends AbstractStep {
    private ProcurementCardHolderUpdateService procurementCardHolderUpdateService;

    public boolean execute(String jobName, Date jobRunDate) {
        return procurementCardHolderUpdateService.updateProcurementCardHolderRecords();
    }

    public void setProcurementCardHolderUpdateService(ProcurementCardHolderUpdateService procurementCardHolderUpdateService) {
        this.procurementCardHolderUpdateService = procurementCardHolderUpdateService;
    }
}
