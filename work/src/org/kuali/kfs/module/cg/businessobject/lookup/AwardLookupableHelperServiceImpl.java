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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsLookupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kew.impl.document.search.DocumentSearchCriteriaBo;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Allows custom handling of Awards within the lookup framework.
 */
public class AwardLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected ContractsAndGrantsLookupService contractsAndGrantsLookupService;
    protected PersonService personService;

    /**
     * This is a intermediate method to call the getSearchResultsHelper() as it is protected.
     *
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public List<? extends BusinessObject> callGetSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return getSearchResultsHelper(fieldValues, unbounded);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director and fund manager objects first
        if (contractsAndGrantsLookupService.setupSearchFields(fieldValues, CGPropertyConstants.LOOKUP_USER_ID_FIELD, CGPropertyConstants.AWARD_LOOKUP_UNIVERSAL_USER_ID_FIELD) &&
                contractsAndGrantsLookupService.setupSearchFields(fieldValues, CGPropertyConstants.LOOKUP_FUND_MGR_USER_ID_FIELD, CGPropertyConstants.AWARD_LOOKUP_FUND_MGR_UNIVERSAL_USER_ID_FIELD)) {
            return super.getSearchResultsHelper(fieldValues, unbounded);
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        if (allowsMaintenanceNewOrCopyAction()) {
            anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }

        // only display invoice lookup URL if CGB is enabled
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            AnchorHtmlData invoiceUrl = getInvoicesLookupUrl(businessObject);
            anchorHtmlDataList.add(invoiceUrl);
        }

        return anchorHtmlDataList;
    }

    /**
     * This method adds a link to the look up FOR the invoices associated with a given Award.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getInvoicesLookupUrl(BusinessObject bo) {
        Award award = (Award) bo;
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "false");
        params.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, getAccountsReceivableModuleBillingService().getContractsGrantsInvoiceDocumentType());
        params.put(CGPropertyConstants.AWARD_INVOICE_LINK_PROPOSAL_NUMBER_PATH, award.getProposalNumber().toString());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DocumentSearchCriteriaBo.class.getName());
        String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);
        return new AnchorHtmlData(url, KFSConstants.SEARCH_METHOD, "View Invoices");
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    public ContractsAndGrantsLookupService getContractsAndGrantsLookupService() {
        return contractsAndGrantsLookupService;
    }

    public void setContractsAndGrantsLookupService(ContractsAndGrantsLookupService contractsAndGrantsLookupService) {
        this.contractsAndGrantsLookupService = contractsAndGrantsLookupService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
