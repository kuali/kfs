/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.web.lookupable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.DummyBusinessObject;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.AccountBalanceByLevelInquirableImpl;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class AccountBalanceByLevelLookupableImpl extends KualiLookupableImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByLevelLookupableImpl.class);

    private AccountBalanceService accountBalanceService;

    public void setAccountBalanceService(AccountBalanceService abs) {
        accountBalanceService = abs;
    }

    /**
     * Returns the inquiry url for a field if one exist.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new AccountBalanceByLevelInquirableImpl()).getInquiryUrl(bo, propertyName);
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

        String costShareOption = (String) fieldValues.get("dummyBusinessObject.costShareOption");
        String pendingEntryOption = (String) fieldValues.get("dummyBusinessObject.pendingEntryOption");
        String consolidationOption = (String) fieldValues.get("dummyBusinessObject.consolidationOption");
        boolean isCostShareExcluded = Constant.COST_SHARE_EXCLUDE.equals(costShareOption);
        int pendingEntryCode = AccountBalanceService.PENDING_NONE;
        if ( "Approved".equals(pendingEntryOption) ) {
            pendingEntryCode = AccountBalanceService.PENDING_APPROVED;
        } else if ( "All".equals(pendingEntryOption) ) {
            pendingEntryCode = AccountBalanceService.PENDING_ALL;
        }
        boolean isConsolidated = Constant.CONSOLIDATION.equals(consolidationOption);

        String chartOfAccountsCode = (String) fieldValues.get("chartOfAccountsCode");
        String accountNumber = (String) fieldValues.get("accountNumber");
        String subAccountNumber = (String) fieldValues.get("subAccountNumber");
        String financialConsolidationObjectCode = (String) fieldValues.get("financialObject.financialObjectLevel.financialConsolidationObjectCode");

        // Dashes means no sub account number
        if (Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(subAccountNumber)) {
            subAccountNumber = "";
        }

        String ufy = (String) fieldValues.get("universityFiscalYear");

        // TODO Deal with invalid numbers
        Integer universityFiscalYear = new Integer(Integer.parseInt(ufy));

        List results = accountBalanceService.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntryCode);

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
