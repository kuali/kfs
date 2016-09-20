package edu.arizona.kfs.gl.batch.service;

public interface BudgetAdjustmentCashTransferService {
	/**
     * Reads an incoming file of general ledger transactions, extracts those transaction that qualify as budget adjustment transactions 
     * and saves those transactions to holding table GL_BUDGET_ADJUST_TRN_T.
     */
    public void extractAndSaveBudgetAdjustmentEntries();
    
    /**
     * Reads budget adjustment transactions from holding table GL_BUDGET_ADJUST_TRN_T and generates/builds
     * a file of budget adjustment cash transfer entries for posting to the General Ledger.
     */
    public void generateBudgetAdjustmentCashTransferTransactions();
}
