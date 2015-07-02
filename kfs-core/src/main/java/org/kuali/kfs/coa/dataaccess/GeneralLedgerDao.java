package org.kuali.kfs.coa.dataaccess;

import java.util.List;
import java.util.Map;

/**
 * DAO which performs queries associated with insight analytics
 */
public interface GeneralLedgerDao {
    List<Map<String, Object>> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber);
}
