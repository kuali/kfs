/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Helper service class for Agency lookup
 */
public class AgencyLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyLookupableHelperServiceImpl.class);

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;

    /***
     * This method was overridden to add custom action components in Agency lookup
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);

        if (!CollectionUtils.isEmpty(htmlDataList)) {
            htmlDataList.add(getAgencyAwardLookupUrl(businessObject));
        }

        return htmlDataList;
    }


    /**
     * This method adds a link to the look up FOR the awards associated with a given Agency.
     *
     * @param bo
     * @return
     */
    private AnchorHtmlData getAgencyAwardLookupUrl(BusinessObject bo) {
        Agency agency = (Agency) bo;

        String baseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.LOOKUP_ACTION;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Award.class.getName());
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(CGPropertyConstants.AgencyFields.AGENCY_NUMBER, agency.getAgencyNumber());

        String href = UrlFactory.parameterizeUrl(baseUrl, parameters);

        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, "Awards");
    }

    /**
     * Ignore fields that are specific to the Contracts & Grants Billing (CGB) enhancement
     * if CGB is disabled.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        List<String> lookupFieldNames = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldNames = getBusinessObjectMetaDataService().getLookupableFieldNames(
                    getBusinessObjectClass());
        }
        if (lookupFieldNames == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        List<String> lookupFieldAttributeList = new ArrayList();
        for (String lookupFieldName: lookupFieldNames) {
            if (!getFieldsToIgnore().contains(lookupFieldName)) {
                lookupFieldAttributeList.add(lookupFieldName);
            }
        }

        // construct field object for each search attribute
        List fields = new ArrayList();

        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(),
                    getBusinessObjectClass());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        int numCols = getBusinessObjectDictionaryService().getLookupNumberOfColumns(this.getBusinessObjectClass());

        this.rows = FieldUtils.wrapFields(fields, numCols);
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return list of fields to ignore
     */
    protected List<String> getFieldsToIgnore() {
        List<String> fieldsToIgnore = new ArrayList<String>();

        if (!getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            fieldsToIgnore.add(CGPropertyConstants.CUSTOMER_NUMBER);
        }

        return fieldsToIgnore;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }
}
