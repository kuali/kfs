package edu.arizona.kfs.fp.batch;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderLoadService;

/**
 * This step will call a service method to load the procurement cardholder xml file into the transaction table.
 * Validates the data before the load. Functions performed by this step:
 * 1) Lookup path and filename from APC for the procurement cardholder input file
 * 2) Load the procurement cardholder xml file
 * 3) Parse each holder and validate against the data dictionary
 * 4) Clean FP_PRCRMNT_CARD_HLDR_LD_T from the previous run
 * 5) Load new transactions into FP_PRCRMNT_CARD_HLDR_LD_T
 * 6) Rename input file using the current date (backup) RESTART: All functions performed within a single
 * transaction. Step can be restarted as needed.
 */
public class ProcurementCardHolderLoadStep extends AbstractStep {

    private ProcurementCardHolderLoadService procurementCardHolderLoadService;
    
    /**
     * Controls the procurement cardholder load process.
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        return procurementCardHolderLoadService.loadFiles();
    }

    public void setProcurementCardHolderLoadService(ProcurementCardHolderLoadService procurementCardHolderLoadService) {
        this.procurementCardHolderLoadService = procurementCardHolderLoadService;
    }
}
