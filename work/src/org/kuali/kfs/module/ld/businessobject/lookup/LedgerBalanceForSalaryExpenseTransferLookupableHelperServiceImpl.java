/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.LaborConstants.SalaryExpenseTransfer;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service implementation of LedgerBalanceForSalaryExpenseTransferLookupableHelperService. The class is the front-end for the
 * balance inquiry of Ledger Balance For Salary Expense Transfer processing.
 */
public class LedgerBalanceForSalaryExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceForExpenseTransferLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForSalaryExpenseTransferLookupableHelperServiceImpl.class);

    private OptionsService optionsService;

    /**
     * @see org.kuali.kfs.module.ld.businessobject.lookup.LedgerBalanceForExpenseTransferLookupableHelperServiceImpl#getSearchResults(java.util.Map)
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
        fieldValues.put(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE, SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE);

        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, options.getActualFinancialBalanceTypeCd());
        Collection actualBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false, getEncumbranceBalanceTypes(fieldValues), true), Constant.NO_PENDING_ENTRY);

        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        Collection effortBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false, getEncumbranceBalanceTypes(fieldValues), true), Constant.NO_PENDING_ENTRY);

        List<String> consolidationKeyList = LedgerBalance.getPrimaryKeyList();
        Collection<LedgerBalance> consolidatedBalances = ConsolidationUtil.consolidateA2Balances(actualBalances, effortBalances, options.getActualFinancialBalanceTypeCd(), consolidationKeyList);

        Integer recordCount = getBalanceService().getBalanceRecordCount(fieldValues, true, getEncumbranceBalanceTypes(fieldValues), true);
        Long actualSize = OJBUtility.getResultActualSize(consolidatedBalances, recordCount, fieldValues, new LedgerBalance());

        return buildSearchResultList(consolidatedBalances, actualSize);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        String fiscalYearString = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        SystemOptions options = this.getOptions(fiscalYearString);

        if (ObjectUtils.isNull(options)) {
            DataDictionaryService dictionaryService = SpringContext.getBean(DataDictionaryService.class);
            String label = dictionaryService.getAttributeLabel(LedgerBalance.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, label);
        }
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
}
