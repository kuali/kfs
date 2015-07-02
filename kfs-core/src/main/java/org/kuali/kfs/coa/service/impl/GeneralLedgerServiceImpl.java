package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.dataaccess.GeneralLedgerDao;
import org.kuali.kfs.coa.service.GeneralLedgerService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public class GeneralLedgerServiceImpl implements GeneralLedgerService {
    protected GeneralLedgerDao generalLedgerDao;

    @Override
    public List<Map<String, Object>> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber) {
        return getGeneralLedgerDao().lookupAccountBalancesByConsolidation(fiscalYear, chartOfAccountsCode, accountNumber);
    }

    public GeneralLedgerDao getGeneralLedgerDao() {
        return generalLedgerDao;
    }

    public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
        this.generalLedgerDao = generalLedgerDao;
    }
}
