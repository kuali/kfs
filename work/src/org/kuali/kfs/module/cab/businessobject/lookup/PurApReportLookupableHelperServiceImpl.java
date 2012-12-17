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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport;
import org.kuali.kfs.module.cab.service.PurchasingAccountsPayableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
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
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class PurApReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApReportLookupableHelperServiceImpl.class);

    private PurchasingAccountsPayableReportService purApReportService;

    /**
     * Custom action urls for CAB PurAp lines.
     *
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List, java.util.List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, "KFS-CAB");
        permissionDetails.put(KimConstants.AttributeConstants.ACTION_CLASS, "PurApLineAction");

        if (!SpringContext.getBean(IdentityManagementService.class).isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, null)) {
            return super.getEmptyActionUrls();
        }

        GeneralLedgerEntry glEntry = (GeneralLedgerEntry) bo;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, CabConstants.Actions.START);
        if (glEntry.getReferenceFinancialDocumentNumber() != null) {
            parameters.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, glEntry.getReferenceFinancialDocumentNumber());
        }

        String href = UrlFactory.parameterizeUrl(CabConstants.CB_INVOICE_LINE_ACTION_URL, parameters);
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, CabConstants.Actions.START, CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equalsIgnoreCase(glEntry.getActivityStatusCode()) ? CabConstants.Actions.VIEW : CabConstants.Actions.PROCESS);
        anchorHtmlData.setTarget(KFSConstants.NEW_WINDOW_URL_TARGET);
        anchorHtmlDataList.add(anchorHtmlData);
        return anchorHtmlDataList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // purapDocumentIdentifier should query PurchasingAccountsPayableDocument
        String purapDocumentIdentifier = getSelectedField(fieldValues, CabPropertyConstants.PurchasingAccountsPayableProcessingReport.PURAP_DOCUMENT_IDENTIFIER);

        // Get the user selects 'Y' for "processed by CAMs". We will search for all status GL lines. This is because of the partial
        // submitted GL lines when GL is 'N'(new) or 'M'(modified), partial GL lines could submit to CAMs. we should include these
        // lines into the search result.
        String active = getSelectedField(fieldValues, CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        if (KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(active)) {
            fieldValues.remove(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        }
        // search for GeneralLedgerEntry BO.
        Iterator searchResultIterator = purApReportService.findGeneralLedgers(fieldValues);
        // create PurchasingAccountsPayableProcessingReport search result collection.
        List<PurchasingAccountsPayableProcessingReport> purApReportList = buildGlEntrySearchResultCollection(searchResultIterator, active);

        // purapDocumentIdentifier is the attribute in PurchasingAccountsPayableDocument. We need to generate a new lookup for that
        // BO, then join search results with the generalLedgerCollection to get the correct search result collection.
        if (StringUtils.isNotBlank(purapDocumentIdentifier)) {
            // construct the lookup criteria for PurchasingAccountsPayableDocument from user input
            Map<String, String> purapDocumentLookupFields = getPurApDocumentLookupFields(fieldValues, purapDocumentIdentifier);

            Collection purApDocs = purApReportService.findPurchasingAccountsPayableDocuments(purapDocumentLookupFields);

            Map<String, Integer> purApDocNumberMap = buildDocumentNumberMap(purApDocs);

            purApReportList = updatePurApReportListByPurApDocs(purApReportList, purApDocNumberMap);
        }
        else {
            purApReportList = updateResultList(purApReportList);
        }


        return buildSearchResultList(purApReportList);
    }

    /**
     * Get PurapDocumentIdentifier from PurchasingAccountsPayableDocument and add it to the search result.
     *
     * @param purApReportCollection
     */
    private List<PurchasingAccountsPayableProcessingReport> updateResultList(List<PurchasingAccountsPayableProcessingReport> purApReportList) {
        List<PurchasingAccountsPayableProcessingReport> newResultList = new ArrayList<PurchasingAccountsPayableProcessingReport>();
        BusinessObjectService boService = this.getBusinessObjectService();
        Map pKeys = new HashMap<String, String>();

        for (PurchasingAccountsPayableProcessingReport report : purApReportList) {
            pKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, report.getDocumentNumber());
            PurchasingAccountsPayableDocument purApDocument = boService.findByPrimaryKey(PurchasingAccountsPayableDocument.class, pKeys);
            if (ObjectUtils.isNotNull(purApDocument)) {
                report.setPurapDocumentIdentifier(purApDocument.getPurapDocumentIdentifier());
                newResultList.add(report);
            }
            pKeys.clear();
        }
        return newResultList;
    }

    /**
     * Build the search result list.
     *
     * @param purApReportCollection
     * @return
     */
    private List<? extends BusinessObject> buildSearchResultList(List purApReportList) {
        List<? extends BusinessObject> searchResults = null;

        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(PurchasingAccountsPayableProcessingReport.class);
        Long matchingResultsCount = Long.valueOf(purApReportList.size());

        // apply results set limit if necessary
        if (searchResultsLimit > 0 && matchingResultsCount.intValue() > searchResultsLimit.intValue()) {
            searchResults = new CollectionIncomplete(purApReportList.subList(0, searchResultsLimit), matchingResultsCount);
        } else {
            searchResults = new CollectionIncomplete(purApReportList, 0L);
        }

        return searchResults;
    }


    /**
     * Build a HashMap for documentNumbers from the PurchasingAccountsPayableDocument search results
     *
     * @param purApDocs
     * @return
     */
    private Map<String, Integer> buildDocumentNumberMap(Collection purApDocs) {
        Map<String, Integer> purApDocNumbers = new HashMap<String, Integer>();
        for (Iterator iterator = purApDocs.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableDocument purApdoc = (PurchasingAccountsPayableDocument) iterator.next();
            purApDocNumbers.put(purApdoc.getDocumentNumber(), purApdoc.getPurapDocumentIdentifier());
        }
        return purApDocNumbers;
    }

    /**
     * Build lookup fields for PurchasingAccountsPayableDocument lookup.
     *
     * @param fieldValues
     * @param purapDocumentIdentifier
     * @return
     */
    private Map<String, String> getPurApDocumentLookupFields(Map<String, String> fieldValues, String purapDocumentIdentifier) {
        Map<String, String> purapDocumentLookupFields = new HashMap<String, String>();
        purapDocumentLookupFields.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURAP_DOCUMENT_IDENTIFIER, purapDocumentIdentifier);
        purapDocumentLookupFields.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER));
        purapDocumentLookupFields.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.REFERENCE_FINANCIAL_DOCUMENT_NUMBER));
        return purapDocumentLookupFields;
    }

    /**
     * Join PurapReportCollection and PurApDocsCollection by documentNumber and remove from PurapReportCollection for mismatch.
     *
     * @param purApReportCollection
     * @param purApDocNumbers
     */
    private List<PurchasingAccountsPayableProcessingReport> updatePurApReportListByPurApDocs(List<PurchasingAccountsPayableProcessingReport> purApReportList, Map<String, Integer> purApDocNumberMap) {
        List<PurchasingAccountsPayableProcessingReport> newReportsList = new ArrayList<PurchasingAccountsPayableProcessingReport>();

        for (PurchasingAccountsPayableProcessingReport report : purApReportList) {
            if (purApDocNumberMap.containsKey(report.getDocumentNumber())) {
                report.setPurapDocumentIdentifier(purApDocNumberMap.get(report.getDocumentNumber()));
                newReportsList.add(report);
            }
        }
        // remove from report collection
        return newReportsList;
    }

    /**
     * Build search result collection as PurchasingAccountsPayableProcessingReport collection.
     *
     * @param searchResultIterator
     * @return
     */
    private List<PurchasingAccountsPayableProcessingReport> buildGlEntrySearchResultCollection(Iterator searchResultIterator, String activeSelection) {
        List<PurchasingAccountsPayableProcessingReport> purApReportList = new ArrayList();

        while (searchResultIterator.hasNext()) {
            Object glEntry = searchResultIterator.next();

            if (glEntry.getClass().isArray()) {
                int i = 0;
                Object[] columnValues = (Object[]) glEntry;

                PurchasingAccountsPayableProcessingReport newReport = new PurchasingAccountsPayableProcessingReport();

                newReport.setUniversityFiscalYear(new Integer(columnValues[i++].toString()));
                newReport.setUniversityFiscalPeriodCode(columnValues[i++].toString());
                newReport.setChartOfAccountsCode(columnValues[i++].toString());
                newReport.setAccountNumber(columnValues[i++].toString());
                newReport.setFinancialObjectCode(columnValues[i++].toString());
                newReport.setFinancialDocumentTypeCode(columnValues[i++].toString());
                newReport.setDocumentNumber(columnValues[i++].toString());
                newReport.setTransactionDebitCreditCode(columnValues[i] == null ? null : columnValues[i].toString());
                i++;
                newReport.setTransactionLedgerEntryAmount(columnValues[i] == null ? null : new KualiDecimal(columnValues[i].toString()));
                i++;
                newReport.setReferenceFinancialDocumentNumber(columnValues[i] == null ? null : columnValues[i].toString());
                i++;
                newReport.setTransactionDate(columnValues[i] == null ? null : getDate(columnValues[i]));
                i++;
                newReport.setTransactionLedgerSubmitAmount(columnValues[i] == null ? null : new KualiDecimal(columnValues[i].toString()));
                i++;
                newReport.setActivityStatusCode(columnValues[i] == null ? null : columnValues[i].toString());

                if (!excludeFromSearchResults(newReport, activeSelection)) {
                    if (newReport.getActivityStatusCode() != null && newReport.isActive()) {
                        // adjust amount if the activity_status_code is 'N' or 'M'
                        if (newReport.getTransactionLedgerEntryAmount() != null) {
                            setReportAmount(activeSelection, newReport);
                        }
                    }
                    else {
                        // set report amount by transactional Amount
                        newReport.setReportAmount(newReport.getAmount());
                    }
                    purApReportList.add(newReport);
                }
            }
        }
        return purApReportList;
    }

    /**
     * Get the Date instance. Why we need this? Looks OJB returns different type of instance when connect to MySql and Oracle:
     * Oracle returns Date instance while MySql returns TimeStamp instance.
     *
     * @param obj
     * @return
     */
    private Date getDate(Object obj) {
        if (obj instanceof Date) {
            return (Date) obj;
        }
        else if (obj instanceof Timestamp) {
            Timestamp tsp = (Timestamp) obj;
            return new Date(tsp.getTime());
        }
        else {
            return null;
        }
    }

    /**
     * To decide if the the given newReport should be added to the search result collection.
     *
     * @param newReport
     * @param activeSelection
     * @return
     */
    private boolean excludeFromSearchResults(PurchasingAccountsPayableProcessingReport newReport, String activeSelection) {
        // If the user selects processed by CAMs, we should exclude the GL lines which have no submit amount as the search result
        if ((KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(activeSelection) && (newReport.getTransactionLedgerSubmitAmount() == null || newReport.getTransactionLedgerSubmitAmount().isZero()))) {
            return true;
        }
        return false;
    }

    /**
     * set partial commit report amount
     *
     * @param active
     * @param newReport
     */
    private void setReportAmount(String active, PurchasingAccountsPayableProcessingReport newReport) {
        if (KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(active)) {
            // Processed in CAMS: set report amount as submitted amount
            newReport.setReportAmount(newReport.getTransactionLedgerSubmitAmount());
        }
        else if ((KFSConstants.NON_ACTIVE_INDICATOR.equalsIgnoreCase(active))) {
            // Not Processed in CAMS: set report amount by transactionLedgerEntryAmount excluding submitted amount
            KualiDecimal reportAmount = newReport.getAmount();
            if (reportAmount != null && newReport.getTransactionLedgerSubmitAmount() != null) {
                newReport.setReportAmount(reportAmount.subtract(newReport.getTransactionLedgerSubmitAmount()));
            }
            else {
                newReport.setReportAmount(reportAmount);
            }
        }
        else {
            // both processed/non processed: set report amount by transactional amount
            newReport.setReportAmount(newReport.getAmount());
        }
    }


    /**
     * Return and remove the selected field from the user input.
     *
     * @param fieldValues
     * @param fieldName
     * @return
     */
    private String getSelectedField(Map fieldValues, String fieldName) {
        String fieldValue = null;

        if (fieldValues.containsKey(fieldName)) {
            fieldValue = (String) fieldValues.get(fieldName);
        }
        return fieldValue == null ? "" : fieldValue;
    }

    /**
     * Gets the purApReportService attribute.
     *
     * @return Returns the purApReportService.
     */
    public PurchasingAccountsPayableReportService getPurApReportService() {
        return purApReportService;
    }


    /**
     * Sets the purApReportService attribute value.
     *
     * @param purApReportService The purApReportService to set.
     */
    public void setPurApReportService(PurchasingAccountsPayableReportService purApReportService) {
        this.purApReportService = purApReportService;
    }

    @Override
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}
