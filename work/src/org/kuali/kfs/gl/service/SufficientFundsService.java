package org.kuali.module.gl.service;

import java.util.List;

import org.kuali.core.document.FinancialDocument;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.SufficientFundsItem;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface SufficientFundsService {

    /**
     * This method checks for sufficient funds on a single document
     * 
     * @param document document
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(FinancialDocument document);

    /**
     * This method checks for sufficient funds on a list of transactions
     * 
     * @param document document
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions);

    /**
     * This operation derives the acct_sf_finobj_cd which is used to populate the General Ledger Pending entry table, so that later
     * we can do Suff Fund checking against that entry
     * 
     * @param financialObject
     * @param accountSufficientFundsCode
     * @return
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);
}
