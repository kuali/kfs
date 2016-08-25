package edu.arizona.kfs.gl.batch;

import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

import edu.arizona.kfs.gl.batch.service.BudgetAdjustmentCashTransferService;

/**
 * A step to generate a file of budget adjustment cash transfer entries, that can then be scrubbed and posted.
 */
public class BudgetAdjustmentGenerationStep extends AbstractWrappedBatchStep {
	protected BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
            	budgetAdjustmentCashTransferService.generateBudgetAdjustmentCashTransferTransactions();
                return true;
            }
        };
    }

    public void setBudgetAdjustmentCashTransferService(BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService) {
        this.budgetAdjustmentCashTransferService = budgetAdjustmentCashTransferService;
    }
}
