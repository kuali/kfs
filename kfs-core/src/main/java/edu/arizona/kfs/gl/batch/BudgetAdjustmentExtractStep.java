package edu.arizona.kfs.gl.batch;

import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

import edu.arizona.kfs.gl.batch.service.BudgetAdjustmentCashTransferService;

public class BudgetAdjustmentExtractStep extends AbstractWrappedBatchStep {
	protected BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService;

	@Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
			    budgetAdjustmentCashTransferService.extractAndSaveBudgetAdjustmentEntries();       
			    return true;
            }
        };
    }
    
    public void setBudgetAdjustmentCashTransferService(BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService) {
        this.budgetAdjustmentCashTransferService = budgetAdjustmentCashTransferService;
    }
}
