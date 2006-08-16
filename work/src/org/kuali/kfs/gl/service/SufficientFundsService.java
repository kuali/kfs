package org.kuali.module.gl.service;

import java.util.List;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.bo.Transaction;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface SufficientFundsService {

    /**
     * This method checks for sufficient funds on a single document
     * 
     * @param transactionalDocument document
     * @return true if has sufficient funds, false if not
     */
    public boolean checkSufficientFunds(TransactionalDocument transactionalDocument);

    /**
     * This operation derives the acct_sf_finobj_cd which is used to populate the General Ledger Pending entry
     * table, so that later we can do Suff Fund checking against that entry
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
