/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.LaborConstants.BenefitExpenseTransfer;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The class is the front-end for the balance inquiry of Ledger Balance For Benefit Expense Transfer processing.
 */
public class LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceForExpenseTransferLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl.class);
    private static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private OptionsService optionsService;


    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LOG.debug("Start getSearchResults()");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String fiscalYearString = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        SystemOptions options = this.getOptions(fiscalYearString);

        if (ObjectUtils.isNull(options)) {
            return new CollectionIncomplete(new ArrayList(), new Long(0));
        }
        
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());
        fieldValues.put(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE, BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE);

        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, options.getActualFinancialBalanceTypeCd());
        Collection actualBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false, getEncumbranceBalanceTypes(fieldValues), false), Constant.NO_PENDING_ENTRY);

        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        Collection effortBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false, getEncumbranceBalanceTypes(fieldValues), false), Constant.NO_PENDING_ENTRY);

        List<String> consolidationKeyList = getConsolidationKeyList();
        Collection<LedgerBalance> consolidatedBalances = ConsolidationUtil.consolidateA2Balances(actualBalances, effortBalances, options.getActualFinancialBalanceTypeCd(), consolidationKeyList);
        this.resetFieldValues(consolidatedBalances);

        Integer recordCount = getBalanceService().getBalanceRecordCount(fieldValues, true, getEncumbranceBalanceTypes(fieldValues), false);
        Long actualSize = OJBUtility.getResultActualSize(consolidatedBalances, recordCount, fieldValues, new LedgerBalance());

        return buildSearchResultList(consolidatedBalances, actualSize);
    }

    // reset the values for the specified fields
    private void resetFieldValues(Collection<LedgerBalance> consolidatedBalances) {
        for (LedgerBalance ledgerBalance : consolidatedBalances) {
            ledgerBalance.setEmplid(null);
            ledgerBalance.setPositionNumber(null);
        }
    }

    // get the consolidation key field names
    private List<String> getConsolidationKeyList() {
        List<String> consolidationKeyList = new ArrayList<String>();
        consolidationKeyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        consolidationKeyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationKeyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationKeyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        return consolidationKeyList;
    }

    /**
     * get the Options object for the given fiscal year
     * 
     * @param fiscalYearString the given fiscal year
     * @return the Options object for the given fiscal year
     */
    private SystemOptions getOptions(String fiscalYearString) {
        SystemOptions options;
        if (fiscalYearString == null) {
            options = optionsService.getCurrentYearOptions();
        }
        else {
            Integer fiscalYear = Integer.valueOf(fiscalYearString.trim());
            options = optionsService.getOptions(fiscalYear);
        }
        return options;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReadOnlyFieldsList()
     */
    @Override
    public List<String> getReadOnlyFieldsList() {
        List<String> readonlyList = super.getReadOnlyFieldsList();
        if (readonlyList == null) {
            readonlyList = new ArrayList<String>();
        }
        AccountService accountService = SpringContext.getBean(AccountService.class);
        if (!accountService.accountsCanCrossCharts()) readonlyList.add(CHART_OF_ACCOUNTS_CODE);
        return readonlyList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        // TODO Auto-generated method stub
        AccountService accountService = SpringContext.getBean(AccountService.class);
        if (!accountService.accountsCanCrossCharts()) fieldValues.remove(CHART_OF_ACCOUNTS_CODE);
        super.validateSearchParameters(fieldValues);
    }

 
 
}
