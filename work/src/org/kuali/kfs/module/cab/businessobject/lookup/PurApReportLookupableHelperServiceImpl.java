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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
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
import org.springframework.transaction.annotation.Transactional;

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
        // return url = "<a href=\"../" + url + "\">" + CabConstants.Actions.PROCESS + "</a>";
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // TODO: do we need to add this PREQ/CM request Id into selection? Currently, remove it from the user input for simple.
        String purapDocumentIdentifier = getSelectedField(fieldValues, "purapDocumentIdentifier");

        // search for GeneralLedgerEntry BO.
        Iterator searchResultIterator = purApReportService.findGeneralLedgers(fieldValues);
        // create PurchasingAccountsPayableProcessingReport search result collection.
        Collection searchResultsCollection = buildSearchResultCollection(searchResultIterator);

        // TODO: updateAccountAmount


        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = Long.valueOf(searchResultsCollection.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(searchResultsCollection, matchingResultsCount);
    }

    /**
     * Build search result collection as PurchasingAccountsPayableProcessingReport collection.
     * 
     * @param searchResultIterator
     * @return
     */
    private Collection buildSearchResultCollection(Iterator searchResultIterator) {
        Collection generalLedgerEntries = new ArrayList();

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
                newReport.setTransactionLedgerEntryAmount(new KualiDecimal(columnValues[i++].toString()));
                newReport.setReferenceFinancialDocumentNumber(columnValues[i++].toString());
                newReport.setTransactionDate(Date.valueOf(columnValues[i++].toString()));
                newReport.setTransactionDebitCreditCode(columnValues[i++].toString());

                generalLedgerEntries.add(newReport);
            }
        }
        return generalLedgerEntries;
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
