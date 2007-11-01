package org.kuali.module.budget.service;

import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.dao.BudgetConstructionDao;


/**
 * This defines the methods a BudgetDocumentService must implement
 */
public interface BudgetDocumentService {

    /**
     * This method...
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao);

}