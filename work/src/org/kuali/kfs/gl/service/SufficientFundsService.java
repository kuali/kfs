package org.kuali.module.gl.service;

import java.util.List;

import org.kuali.core.document.TransactionalDocument;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface SufficientFundsService {

    /**
     * 
     * preforms sufficient funds checking for transactional documents
     * 
     * @param transactionalDocument
     */
    public boolean checkSufficientFunds(TransactionalDocument transactionalDocument);

    /**
     * fp_sasfc: operation get_sf_object_cd. 1-1...23 this operation derives the acct_sf_finobj_cd which is used to populate the PLE
     * table, so that later we can do Suff Fund checking against that entry
     * 
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @param sufficientFundsObjectCode
     * @param financialObjectLevelCode
     * @return
     */
    public String getSufficientFundsObjectCode(String chartOfAccountsCode, String financialObjectCode, String accountSufficientFundsCode, String financialObjectLevelCode);

    /**
     * returns a list of special financial object codes for use with various sufficient funds related classes
     * 
     * @return
     */
    public List getSpecialFinancialObjectCodes();

    /**
     * returns object code for cash in bank. for use with various sufficient funds related classes
     * 
     * @return
     */
    public String getFinancialObjectCodeForCashInBank();

    /**
     * returns expenditure codes for use with various sufficient funds related classes
     * 
     * @return
     */
    public List getExpenditureCodes();

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);
}
