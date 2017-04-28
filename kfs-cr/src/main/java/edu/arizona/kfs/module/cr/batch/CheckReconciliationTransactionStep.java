package edu.arizona.kfs.module.cr.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.cr.batch.service.CheckReconciliationTransactionBatchService;

/**
 * GeneralLedgerTransactionStep
 */
public class CheckReconciliationTransactionStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationTransactionStep.class);

    private CheckReconciliationTransactionBatchService checkReconciliationTransactionBatchService;

    public void setCheckReconciliationTransactionBatchService(CheckReconciliationTransactionBatchService checkReconciliationTransactionBatchService) {
        this.checkReconciliationTransactionBatchService = checkReconciliationTransactionBatchService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        LOG.info("Started CheckReconciliationTransactionStep: " + jobRunDate.toString());
        boolean success = true;
        try {
            success &= checkReconciliationTransactionBatchService.initializeServiceObjects();
            if (success) {
                success &= checkReconciliationTransactionBatchService.processStoppedPayments();
            }
            if (success) {
                success &= checkReconciliationTransactionBatchService.processCancelledPayments();
            }
            if (success) {
                success &= checkReconciliationTransactionBatchService.processStalePayments();
            }
            if (success) {
                success &= checkReconciliationTransactionBatchService.processVoidedPayments();
            }
            LOG.info("Completed CheckReconciliationTransactionStep: " + (new Date()).toString());
            return success;
        } catch (Exception e) {
            LOG.error("Error performing CheckReconciliationTransactionStep: " + e.getMessage());
            return false;
        }
    }
}
