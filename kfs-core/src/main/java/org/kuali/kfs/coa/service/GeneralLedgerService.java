package org.kuali.kfs.coa.service;

import java.util.Map;

public interface GeneralLedgerService {
    Map<String, Object> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber);
}
