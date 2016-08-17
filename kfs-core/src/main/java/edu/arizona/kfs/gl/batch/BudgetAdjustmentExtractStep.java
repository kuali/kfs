package edu.arizona.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;
import edu.arizona.kfs.gl.batch.service.BudgetAdjustmentCashTransferService;


public class BudgetAdjustmentExtractStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentExtractStep.class);
    protected BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService;
   
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
       
    	budgetAdjustmentCashTransferService.extractAndSaveBudgetAdjustmentEntries();
       
        return true;
    }

    
    public void setBudgetAdjustmentCashTransferService(BudgetAdjustmentCashTransferService budgetAdjustmentCashTransferService) {
        this.budgetAdjustmentCashTransferService = budgetAdjustmentCashTransferService;
    }
}
