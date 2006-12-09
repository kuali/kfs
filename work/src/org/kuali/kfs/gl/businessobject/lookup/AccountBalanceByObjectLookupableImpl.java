/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.web.lookupable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.DummyBusinessObject;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.AccountBalanceByObjectInquirableImpl;
import org.kuali.module.gl.web.inquirable.AccountBalanceInquirableImpl;

public class AccountBalanceByObjectLookupableImpl extends KualiLookupableImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByObjectLookupableImpl.class);

    private AccountBalanceService accountBalanceService;

    public void setAccountBalanceService(AccountBalanceService abs) {
        accountBalanceService = abs;
    }

    /**
     * Returns the inquiry url for a result field.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        if (GLConstants.DummyBusinessObject.LINK_BUTTON_OPTION.equals(propertyName)) {
            return (new AccountBalanceByObjectInquirableImpl()).getInquiryUrl(bo, propertyName);
        }
        return (new AccountBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Uses Lookup Service to provide a basic search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return List found business objects
     */
    public List getSearchResults(Map fieldValues) {
        LOG.debug("getSearchResults() started");

        setBackLocation((String) fieldValues.get(Constants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(Constants.DOC_FORM_KEY));

        BusinessObjectFieldConverter.escapeSingleQuote(fieldValues);

        String costShareOption = (String) fieldValues.get(GLConstants.DummyBusinessObject.COST_SHARE_OPTION);
        String pendingEntryOption = (String) fieldValues.get(GLConstants.DummyBusinessObject.PENDING_ENTRY_OPTION);
        String consolidationOption = (String) fieldValues.get(GLConstants.DummyBusinessObject.CONSOLIDATION_OPTION);
        boolean isCostShareExcluded = Constant.COST_SHARE_EXCLUDE.equals(costShareOption);
        int pendingEntryCode = AccountBalanceService.PENDING_NONE;
        if (GLConstants.PendingEntryOptions.APPROVED.equals(pendingEntryOption)) {
            pendingEntryCode = AccountBalanceService.PENDING_APPROVED;
        }
        else if (GLConstants.PendingEntryOptions.ALL.equals(pendingEntryOption)) {
            pendingEntryCode = AccountBalanceService.PENDING_ALL;
        }
        boolean isConsolidated = Constant.CONSOLIDATION.equals(consolidationOption);

        String chartOfAccountsCode = (String) fieldValues.get(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = (String) fieldValues.get(PropertyConstants.ACCOUNT_NUMBER);
        String subAccountNumber = (String) fieldValues.get(PropertyConstants.SUB_ACCOUNT_NUMBER);
        String financialObjectLevelCode = (String) fieldValues.get(GLConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        String financialReportingSortCode = (String) fieldValues.get(GLConstants.BalanceInquiryDrillDowns.REPORTING_SORT_CODE);

        // Dashes means no sub account number
        if (Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(subAccountNumber)) {
            subAccountNumber = "";
        }

        String ufy = (String) fieldValues.get(PropertyConstants.UNIVERSITY_FISCAL_YEAR);

        // TODO Deal with invalid numbers
        Integer universityFiscalYear = new Integer(Integer.parseInt(ufy));

        List results = accountBalanceService.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Put the search related stuff in the objects
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            AccountBalance ab = (AccountBalance) iter.next();

            DummyBusinessObject dbo = ab.getDummyBusinessObject();
            dbo.setConsolidationOption(consolidationOption);
            dbo.setCostShareOption(costShareOption);
            dbo.setPendingEntryOption(pendingEntryOption);
            dbo.setLinkButtonOption(Constant.LOOKUP_BUTTON_VALUE);
        }
        return new CollectionIncomplete(results, new Long(results.size()));
    }
}