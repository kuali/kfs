package org.kuali.kfs.coa.service;

import java.util.List;
import java.util.Map;

public interface GeneralLedgerService {
    List<Map<String, Object>> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber);
}
