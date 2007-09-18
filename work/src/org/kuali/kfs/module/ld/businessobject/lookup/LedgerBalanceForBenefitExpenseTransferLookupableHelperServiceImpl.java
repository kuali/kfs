/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.labor.web.lookupable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants.BenefitExpenseTransfer;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ConsolidationUtil;
import org.kuali.module.labor.web.inquirable.LedgerBalanceForExpenseTransferInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl.class);

    private OptionsService optionsService;

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new LedgerBalanceForExpenseTransferInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.info("Start getSearchResults()");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String fiscalYearString = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        Options options = this.getOptions(fiscalYearString);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());
        fieldValues.put(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE, BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE);

        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, options.getActualFinancialBalanceTypeCd());
        Collection actualBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false), Constant.NO_PENDING_ENTRY);

        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        Collection effortBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false), Constant.NO_PENDING_ENTRY);

        Collection<LedgerBalance> consolidatedBalances = ConsolidationUtil.consolidateA2Balances(actualBalances, effortBalances, options.getActualFinancialBalanceTypeCd());

        Integer recordCount = getBalanceService().getBalanceRecordCount(fieldValues, true);
        Long actualSize = OJBUtility.getResultActualSize(consolidatedBalances, recordCount, fieldValues, new LedgerBalance());

        return buildSearchResultList(consolidatedBalances, actualSize);
    }

    /**
     * get the Options object for the given fiscal year
     * 
     * @param fiscalYearString the given fiscal year
     * @return the Options object for the given fiscal year
     */
    private Options getOptions(String fiscalYearString) {
        Options options;
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
