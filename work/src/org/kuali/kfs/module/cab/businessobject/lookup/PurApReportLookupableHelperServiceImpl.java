/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.sql.Date;
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
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport;
import org.kuali.kfs.module.cab.service.PurchasingAccountsPayableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class PurApReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApReportLookupableHelperServiceImpl.class);

    private PurchasingAccountsPayableReportService purApReportService;

    /**
     * Custom action urls for CAB PurAp lines.
     * 
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List, java.util.List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        GeneralLedgerEntry glEntry = (GeneralLedgerEntry) bo;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, CabConstants.Actions.START);
        parameters.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, glEntry.getReferenceFinancialDocumentNumber());

        String href = UrlFactory.parameterizeUrl(CabConstants.CB_INVOICE_LINE_ACTION_URL, parameters);
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, CabConstants.Actions.START, CabConstants.Actions.PROCESS);
        anchorHtmlDataList.add(anchorHtmlData);
        return anchorHtmlDataList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String purapDocumentIdentifier = getAndRemoveSelectedField(fieldValues, CabPropertyConstants.PurchasingAccountsPayableProcessingReport.PURAP_DOCUMENT_IDENTIFIER);

        String active = getAndRemoveSelectedField(fieldValues, "active");
        // search for GeneralLedgerEntry BO.
        Iterator searchResultIterator = purApReportService.findGeneralLedgers(fieldValues);
        // create PurchasingAccountsPayableProcessingReport search result collection.
        Collection purApReportCollection = buildGlEntrySearchResultCollection(searchResultIterator, active);

        // purapDocumentIdentifier is the attribute in PurchasingAccountsPayableDocument. We need to generate a new lookup for that
        // BO, then join search results with the generalLedgerCollection to get the correct search result collection.
        if (StringUtils.isNotBlank(purapDocumentIdentifier)) {
            // construct the lookup criteria for PurchasingAccountsPayableDocument from user input
            Map<String, String> purapDocumentLookupFields = getPurApDocumentLookupFields(fieldValues, purapDocumentIdentifier);

            Collection purApDocs = purApReportService.findPurchasingAccountsPayableDocuments(purapDocumentLookupFields);

            Map<String, String> purApDocNumbers = buildDocumentNumberMap(purApDocs);

            updatePurApReportCollectionByPurApDocs(purApReportCollection, purApDocNumbers);
        }

        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = Long.valueOf(purApReportCollection.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(purApReportCollection, matchingResultsCount);
    }


    /**
     * Build a HashMap for documentNumbers from the PurchasingAccountsPayableDocument search results
     * 
     * @param purApDocs
     * @return
     */
    private Map<String, String> buildDocumentNumberMap(Collection purApDocs) {
        Map<String, String> purApDocNumbers = new HashMap<String, String>();
        for (Iterator iterator = purApDocs.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableDocument purApdoc = (PurchasingAccountsPayableDocument) iterator.next();
            purApDocNumbers.put(purApdoc.getDocumentNumber(), "");
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
        purapDocumentLookupFields.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER));
        purapDocumentLookupFields.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.REFERENCE_FINANCIAL_DOCUMENT_NUMBER));
        return purapDocumentLookupFields;
    }

    /**
     * Join PurapReportCollection and PurApDocsCollection by documentNumber.
     * 
     * @param purApReportCollection
     * @param purApDocNumbers
     */
    private void updatePurApReportCollectionByPurApDocs(Collection<PurchasingAccountsPayableProcessingReport> purApReportCollection, Map purApDocNumbers) {
        Collection removedReports = new ArrayList<PurchasingAccountsPayableProcessingReport>();

        for (Iterator iterator = purApReportCollection.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableProcessingReport report = (PurchasingAccountsPayableProcessingReport) iterator.next();
            if (!purApDocNumbers.containsKey(report.getDocumentNumber())) {
                removedReports.add(report);
            }

        }
        // remove from report collection
        purApReportCollection.removeAll(removedReports);
    }

    /**
     * Build search result collection as PurchasingAccountsPayableProcessingReport collection.
     * 
     * @param searchResultIterator
     * @return
     */
    private Collection buildGlEntrySearchResultCollection(Iterator searchResultIterator, String active) {
        Collection purApReportCollection = new ArrayList();

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
                newReport.setTransactionDate(columnValues[i] == null ? null : Date.valueOf(columnValues[i].toString()));
                i++;
                newReport.setTransactionLedgerSubmitAmount(columnValues[i] == null ? null : new KualiDecimal(columnValues[i].toString()));
                i++;
                newReport.setActive(KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(columnValues[i].toString())? true: false);
                
                // set report amount
                if (newReport.getTransactionLedgerEntryAmount() != null) {
                    setReportAmount(active, newReport);
                }

                purApReportCollection.add(newReport);
            }
        }
        return purApReportCollection;
    }

    /**
     * set report amount
     * 
     * @param active
     * @param newReport
     */
    private void setReportAmount(String active, PurchasingAccountsPayableProcessingReport newReport) {
        if (KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(active)) {
            // Active: set report amount by transactionLedgerSubmitAmount excluding submitted amount
            KualiDecimal reportAmount = newReport.getAbsAmount();
            if (reportAmount != null && newReport.getTransactionLedgerSubmitAmount() != null) {
                newReport.setReportAmount(reportAmount.subtract(newReport.getTransactionLedgerSubmitAmount()));
            }
            else {
                newReport.setReportAmount(reportAmount);
            }
        }
        else if ((KFSConstants.NON_ACTIVE_INDICATOR.equalsIgnoreCase(active))) {
            // Inactive: set report amount as submitted amount
            newReport.setReportAmount(newReport.getTransactionLedgerSubmitAmount());
        }
        else {
            // both active and inactive: set report amount as transactional amount
            newReport.setReportAmount(newReport.getAbsAmount());
        }
    }

    /**
     * Return and remove the selected field from the user input.
     * 
     * @param fieldValues
     * @param fieldName
     * @return
     */
    private String getAndRemoveSelectedField(Map fieldValues, String fieldName) {
        String fieldValue = null;

        if (fieldValues.containsKey(fieldName)) {
            fieldValue = (String) fieldValues.get(fieldName);
            // truncate the non-property filed
            fieldValues.remove(fieldName);
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


}
