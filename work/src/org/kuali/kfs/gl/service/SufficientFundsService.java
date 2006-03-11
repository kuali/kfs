package org.kuali.module.gl.service;

import org.kuali.core.document.TransactionalDocument;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface SufficientFundsService {
    // MSA need to be added to APC & an appropriate workgroup
    public static String SUFF_FUNDS_PARAMETERS_NM = "SuffFundsServiceParameters";
    public static String SUFF_FUNDS_GLOBAL_BUDGET_CHECKING_NM = "SUFF_FUNDS_GLOBAL_BUDGET_CHECKING";

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
    public String getSufficientFundsObjectCode(String chartOfAccountsCode, String financialObjectCode,
            String accountSufficientFundsCode, String financialObjectLevelCode);
}
