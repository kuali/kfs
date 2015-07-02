package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.dataaccess.GeneralLedgerDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.GeneralLedgerService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
public class GeneralLedgerServiceImpl implements GeneralLedgerService {
    protected GeneralLedgerDao generalLedgerDao;
    protected AccountService accountService;

    @Override
    public Map<String, Object> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("chartOfAccountsCode", chartOfAccountsCode);
        result.put("accountNumber", accountNumber);

        final Account account = getAccountService().getByPrimaryId(chartOfAccountsCode, accountNumber);
        result.put("accountName", account.getAccountName());

        final List<Map<String, Object>> accountBalances = getGeneralLedgerDao().lookupAccountBalancesByConsolidation(fiscalYear, chartOfAccountsCode, accountNumber);
        accountBalances.forEach(accountBalance -> {
           accountBalance.put("usedPercentOfBudget",calculateUsedPercentOfBudget((double) accountBalance.get("budget"), (double) accountBalance.get("spent"), (double)accountBalance.get("allocated")));
        });
        result.put("accountBalances",accountBalances);
        Map<String, Object> total = sumAccountBalances(accountBalances);
        total.put("chartOfAccountsCode", chartOfAccountsCode);
        total.put("accountNumber", accountNumber);
        total.put("usedPercentOfBudget", calculateUsedPercentOfBudget((double) total.get("budget"), (double) total.get("spent"), (double)total.get("allocated")));
        result.put("total", total);

        return result;
    }

    protected Map<String, Object> sumAccountBalances(List<Map<String, Object>> accountBalances) {
        Map<String, Object> sum = createEmptyAccountBalanceMap();
        for (Map<String, Object> acctBal : accountBalances) {
            sum = addAccountBalances(sum, acctBal);
        }
        return sum;
    }

    protected Map<String, Object> addAccountBalances(Map<String, Object> a, Map<String, Object> b) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("budget", sumFields("budget", a, b));
        result.put("spent", sumFields("spent", a, b));
        result.put("allocated", sumFields("allocated", a, b));
        result.put("balance", sumFields("balance", a, b));
        return result;
    }

    protected Map<String, Object> createEmptyAccountBalanceMap() {
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("budget", 0.0);
        result.put("spent", 0.0);
        result.put("allocated", 0.0);
        result.put("balance", 0.0);
        return result;
    }

    protected double sumFields(String fieldName, Map<String, Object> a, Map<String, Object> b) {
        if (!a.containsKey(fieldName) || ObjectUtils.isNull(a.get(fieldName))) {
            if (!b.containsKey(fieldName) || ObjectUtils.isNull(b.get(fieldName))) {
                return 0.0;
            }
            return (double)b.get(fieldName);
        } else {
            if (!b.containsKey(fieldName) || ObjectUtils.isNull(b.get(fieldName))) {
                return 0.0;
            }
            return (double)a.get(fieldName) + (double)b.get(fieldName);
        }
    }

    protected Double calculateUsedPercentOfBudget(double budget, double actuals, double encumbrance) {
        if (budget == 0.0) {
            return null;
        }
        final double usedPercent = ((actuals + encumbrance)/budget)*100.0;
        return new Double(usedPercent);
    }

    public GeneralLedgerDao getGeneralLedgerDao() {
        return generalLedgerDao;
    }

    public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
        this.generalLedgerDao = generalLedgerDao;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
