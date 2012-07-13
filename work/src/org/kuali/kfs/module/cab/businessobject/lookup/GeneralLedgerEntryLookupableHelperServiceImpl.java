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
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class overrides the base getActionUrls method
 */
public class GeneralLedgerEntryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerEntryLookupableHelperServiceImpl.class);
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, "KFS-CAB");
        permissionDetails.put(KimConstants.AttributeConstants.ACTION_CLASS, "CapitalAssetInformationAction");

        if (!SpringContext.getBean(IdentityManagementService.class).isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, null)) {
            return super.getEmptyActionUrls();
        }

        GeneralLedgerEntry entry = (GeneralLedgerEntry) bo;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        if (entry.isActive()) {
            AnchorHtmlData processLink = new AnchorHtmlData("../cabCapitalAssetInformation.do?methodToCall=process&" + CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER + "=" + entry.getGeneralLedgerAccountIdentifier(), "process", "process");
            processLink.setTarget(entry.getGeneralLedgerAccountIdentifier().toString());
            anchorHtmlDataList.add(processLink);
        }
        else {
            List<GeneralLedgerEntryAsset> generalLedgerEntryAssets = entry.getGeneralLedgerEntryAssets();
            if (!generalLedgerEntryAssets.isEmpty()) {
                for (GeneralLedgerEntryAsset generalLedgerEntryAsset : generalLedgerEntryAssets) {
                    AnchorHtmlData viewDocLink = new AnchorHtmlData("../cabCapitalAssetInformation.do?methodToCall=viewDoc&" + "documentNumber" + "=" + generalLedgerEntryAsset.getCapitalAssetManagementDocumentNumber(), "viewDoc", generalLedgerEntryAsset.getCapitalAssetManagementDocumentNumber());
                    viewDocLink.setTarget(generalLedgerEntryAssets.get(0).getCapitalAssetManagementDocumentNumber());
                    anchorHtmlDataList.add(viewDocLink);
                }
            }
            else {
                anchorHtmlDataList.add(new AnchorHtmlData("", "n/a", "n/a"));
            }
        }
        return anchorHtmlDataList;
    }

    /**
     * This method will remove all PO related transactions from display on GL results
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // update status code from user input value to DB value.
        updateStatusCodeCriteria(fieldValues);

        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty()) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = null;
        List<GeneralLedgerEntry> newList = new ArrayList<GeneralLedgerEntry>();
        for (BusinessObject businessObject : searchResults) {
            GeneralLedgerEntry entry = (GeneralLedgerEntry) businessObject;
            if (!CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
                if (!CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                    newList.add(entry);
                }
                else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                    Map<String, String> cmKeys = new HashMap<String, String>();
                    cmKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, entry.getDocumentNumber());
                    // check if CAB PO document exists, if not included
                    Collection<PurchasingAccountsPayableDocument> matchingCreditMemos = businessObjectService.findMatching(PurchasingAccountsPayableDocument.class, cmKeys);
                    if (matchingCreditMemos == null || matchingCreditMemos.isEmpty()) {
                        newList.add(entry);
                    }
                }
            }
        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
    }


    /**
     * Update activity status code to the value used in DB. The reason is the value from user input will be 'Y' or 'N'. However,
     * these two status code are now replaced by 'N','E' and 'P'.
     * 
     * @param fieldValues
     */
    protected void updateStatusCodeCriteria(Map<String, String> fieldValues) {
        String activityStatusCode = null;
        if (fieldValues.containsKey(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE)) {
            activityStatusCode = (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        }

        if (KFSConstants.NON_ACTIVE_INDICATOR.equalsIgnoreCase(activityStatusCode)) {
            // not processed in CAMs: 'N'
            fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE, CabConstants.ActivityStatusCode.NEW);
        }
        else if (KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(activityStatusCode)) {
            // processed in CAMs: 'E' or 'P'
            fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE, CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS + SearchOperator.OR.op() + CabConstants.ActivityStatusCode.ENROUTE);
        }

    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
