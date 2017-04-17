package edu.arizona.kfs.module.cr.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.cr.batch.service.CheckReconciliationImportBatchService;

/**
 * Check Reconciliation Import Step
 */
public class CheckReconciliationImportStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationImportStep.class);

    private CheckReconciliationImportBatchService checkReconciliationImportBatchService;

    public void setCheckReconciliationImportBatchService(CheckReconciliationImportBatchService checkReconciliationImportBatchService) {
        this.checkReconciliationImportBatchService = checkReconciliationImportBatchService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        boolean success = true;
        LOG.info("Started CheckReconciliationImportStep: " + jobRunDate.toString());
        try {
            success &= checkReconciliationImportBatchService.initializeServiceObjects();
            if (success) {
                success &= checkReconciliationImportBatchService.importPdpPayments();
            }
            if (success) {
                success &= checkReconciliationImportBatchService.processDataFiles();
            }
            LOG.info("Completed CheckReconciliationImportStep: " + (new Date()).toString());
            return success;
        } catch (Exception e) {
            LOG.error("Error performing CheckReconciliationImportStep: " + e.getMessage());
            LOG.error(e.toString());
            return false;
        }
    }

}
