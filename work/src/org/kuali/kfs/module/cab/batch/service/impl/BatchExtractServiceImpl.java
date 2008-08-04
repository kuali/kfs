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
package org.kuali.kfs.module.cab.batch.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao;
import org.kuali.kfs.module.cab.batch.service.BatchExtractService;
import org.kuali.kfs.module.cab.batch.service.ReconciliationService;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItemBase;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides default implementation of {@link BatchExtractService}
 */
@Transactional
public class BatchExtractServiceImpl implements BatchExtractService {

    protected BusinessObjectService businessObjectService;
    protected ExtractDao extractDao;
    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;

    /**
     * Creates a batch parameters object reading values from configured system parameters
     * 
     * @return BatchParameters
     */
    protected BatchParameters createBatchParameters() {
        BatchParameters parameters = new BatchParameters();
        Timestamp lastRunTime = getLastRunTimestamp();
        parameters.setLastRunTime(lastRunTime);
        parameters.setIncludedFinancialBalanceTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.BALANCE_TYPES));
        parameters.setIncludedFinancialObjectSubTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.OBJECT_SUB_TYPES));
        parameters.setExcludedChartCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS));
        parameters.setExcludedDocTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.DOCUMENT_TYPES));
        parameters.setExcludedFiscalPeriods(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.FISCAL_PERIODS));
        parameters.setExcludedSubFundCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS));
        return parameters;
    }

    /**
     * Retrieves a credit memo document for a specific document number
     * 
     * @param entry GL Line
     * @return CreditMemoDocument
     */
    protected CreditMemoDocument findCreditMemoDocument(Entry entry) {
        CreditMemoDocument creditMemoDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put(CabPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        Collection<CreditMemoDocument> matchingCms = businessObjectService.findMatching(CreditMemoDocument.class, keys);
        if (matchingCms != null && matchingCms.size() == 1) {
            creditMemoDocument = matchingCms.iterator().next();
        }
        return creditMemoDocument;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findElgibleGLEntries()
     */
    public Collection<Entry> findElgibleGLEntries() {
        BatchParameters parameters = createBatchParameters();
        return getExtractDao().findMatchingGLEntries(parameters);
    }

    /**
     * Retrieves a payment request document for a specific document number
     * 
     * @param entry GL Line
     * @return PaymentRequestDocument
     */
    protected PaymentRequestDocument findPaymentRequestDocument(Entry entry) {
        PaymentRequestDocument paymentRequestDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put(CabPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        Collection<PaymentRequestDocument> matchingPreqs = businessObjectService.findMatching(PaymentRequestDocument.class, keys);
        if (matchingPreqs != null && matchingPreqs.size() == 1) {
            paymentRequestDocument = matchingPreqs.iterator().next();
        }
        return paymentRequestDocument;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findPurapPendingGLEntries()
     */
    public Collection<GeneralLedgerPendingEntry> findPurapPendingGLEntries() {
        Collection<GeneralLedgerPendingEntry> purapPendingGLEntries = extractDao.findPurapPendingGLEntries(createBatchParameters());
        return purapPendingGLEntries;
    }

    /**
     * Gets the businessOjectService attribute.
     * 
     * @return Returns the businessOjectService
     */

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Gets the extractDao attribute.
     * 
     * @return Returns the extractDao
     */

    public ExtractDao getExtractDao() {
        return extractDao;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService
     */

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Computes the last run time stamp, if null then it gives yesterday
     * 
     * @return Last run time stamp
     */
    protected Timestamp getLastRunTimestamp() {
        String lastRunTS = parameterService.getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Timestamp lastRunTime;
        Date yesterday = DateUtils.add(dateTimeService.getCurrentDate(), Calendar.DAY_OF_MONTH, -1);
        try {
            lastRunTime = lastRunTS == null ? new Timestamp(yesterday.getTime()) : new Timestamp(DateUtils.parseDate(lastRunTS, new String[] { CabConstants.DATE_FORMAT_TS }).getTime());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return lastRunTime;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService
     */

    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#saveFPLines(java.util.List)
     */
    public void saveFPLines(List<Entry> fpLines) {
        for (Entry fpLine : fpLines) {
            // If entry is not duplicate, non-null and non-zero, then insert into CAB
            ReconciliationService reconciliationService = SpringContext.getBean(ReconciliationService.class);
            if (fpLine.getTransactionLedgerEntryAmount() != null && !fpLine.getTransactionLedgerEntryAmount().isZero() && !reconciliationService.isDuplicateEntry(fpLine)) {
                GeneralLedgerEntry glEntry = new GeneralLedgerEntry(fpLine);
                businessObjectService.save(glEntry);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#savePOLines(java.util.List)
     */
    public void savePOLines(List<Entry> poLines) {
        // This is a list of pending GL entries created after last GL process and Cab Batch extract
        Collection<GeneralLedgerPendingEntry> purapPendingGLEntries = findPurapPendingGLEntries();
        // TODO purapAcctLines should come from PURAP module
        Collection<?> purapAcctLines = null;
        ReconciliationService reconciliationService = SpringContext.getBean(ReconciliationService.class);
        reconciliationService.reconcile(poLines, purapPendingGLEntries, purapAcctLines);
        // for each valid GL entry there is a collection of valid PO Doc and Account Lines
        Set<GlAccountLineGroup> matchedGroups = reconciliationService.getMatchedGroups();
        List<PersistableBusinessObject> saveList = new ArrayList<PersistableBusinessObject>();

        for (GlAccountLineGroup group : matchedGroups) {
            Entry entry = group.getTargetEntry();
            saveList.add(new GeneralLedgerEntry(entry));
            PurchasingAccountsPayableDocument cabPurapDoc = findPurchasingAccountsPayableDocument(entry);
            // if document is found already, update the active flag
            if (ObjectUtils.isNotNull(cabPurapDoc) && !cabPurapDoc.isActive()) {
                cabPurapDoc.setActive(true);
                saveList.add(cabPurapDoc);
            }
            else {
                // If not in CAB already, create a new and insert into CAB
                AccountsPayableDocumentBase apDoc = null;
                if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
                    // find PREQ
                    apDoc = findPaymentRequestDocument(entry);
                }
                else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                    // find CM
                    apDoc = findCreditMemoDocument(entry);
                }
                if (apDoc == null) {
                    // TODO record error message when no matching AP doc is found
                }
                else {
                    cabPurapDoc = createPurchasingAccountsPayableDocument(entry, apDoc);
                    // items is is based on the account lines
                    List<AccountsPayableItemBase> apItems = null;
                    for (AccountsPayableItemBase apItem : apItems) {
                        PurchasingAccountsPayableItemAsset itemAsset = findMatchingPurapAssetItem(cabPurapDoc, apItem);
                        if (itemAsset == null) {
                            itemAsset = createPurchasingAccountsPayableItemAsset(cabPurapDoc, apItem);
                            // TODO, do only if an account line is available for this line item
                            cabPurapDoc.getPurchasingAccountsPayableItemAssets().add(itemAsset);
                        }
                    }
                    saveList.add(cabPurapDoc);
                }
            }
            businessObjectService.save(saveList);
            saveList.clear();
        }
    }

    protected PurchasingAccountsPayableDocument findPurchasingAccountsPayableDocument(Entry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, entry.getDocumentNumber());
        // check if doc is already in CAB
        PurchasingAccountsPayableDocument cabPurapDoc = (PurchasingAccountsPayableDocument) businessObjectService.findByPrimaryKey(PurchasingAccountsPayableDocument.class, primaryKeys);
        return cabPurapDoc;
    }

    protected PurchasingAccountsPayableItemAsset createPurchasingAccountsPayableItemAsset(PurchasingAccountsPayableDocument cabPurapDoc, AccountsPayableItemBase apItem) {
        PurchasingAccountsPayableItemAsset itemAsset;
        // insert new because line is new or already merged or split
        itemAsset = new PurchasingAccountsPayableItemAsset();
        itemAsset.setDocumentNumber(cabPurapDoc.getDocumentNumber());
        itemAsset.setAccountsPayableLineItemIdentifier(apItem.getItemIdentifier());
        itemAsset.setCapitalAssetBuilderLineNumber(1);
        itemAsset.setAccountsPayableItemQuantity(apItem.getItemQuantity());
        itemAsset.setActive(true);
        return itemAsset;
    }

    protected PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Entry entry, AccountsPayableDocumentBase apDoc) {
        PurchasingAccountsPayableDocument cabPurapDoc;
        cabPurapDoc = new PurchasingAccountsPayableDocument();
        cabPurapDoc.setDocumentNumber(entry.getDocumentNumber());
        cabPurapDoc.setPurapDocumentIdentifier(apDoc.getPurapDocumentIdentifier());
        cabPurapDoc.setPurchaseOrderIdentifier(apDoc.getPurchaseOrderIdentifier());
        cabPurapDoc.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
        cabPurapDoc.setActive(true);
        return cabPurapDoc;
    }

    protected PurchasingAccountsPayableItemAsset findMatchingPurapAssetItem(PurchasingAccountsPayableDocument cabPurapDoc, AccountsPayableItemBase apItem) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, cabPurapDoc.getDocumentNumber());
        keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, apItem.getItemIdentifier());
        Collection<PurchasingAccountsPayableItemAsset> matchingItems = businessObjectService.findMatching(PurchasingAccountsPayableItemAsset.class, keys);
        if (matchingItems != null && !matchingItems.isEmpty() && matchingItems.size() == 1) {
            PurchasingAccountsPayableItemAsset itmAsset = matchingItems.iterator().next();
            // if still active and never split or submitted to CAMS
            if (itmAsset.isActive() && itmAsset.getCapitalAssetManagementDocumentNumber() == null) {
                return itmAsset;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#separatePOLines(java.util.List, java.util.List,
     *      java.util.Collection)
     */
    public void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries) {
        for (Entry entry : elgibleGLEntries) {
            if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
                purapLines.add(entry);
            }
            else if (!CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                fpLines.add(entry);
            }
            else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                Map<String, String> fieldValues = new HashMap<String, String>();
                fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, entry.getDocumentNumber());
                // check if vendor credit memo, then include as FP line
                Collection<CreditMemoDocument> matchingCreditMemos = businessObjectService.findMatching(CreditMemoDocument.class, fieldValues);
                for (CreditMemoDocument creditMemoDocument : matchingCreditMemos) {
                    if (creditMemoDocument.getPurchaseOrderIdentifier() == null) {
                        fpLines.add(entry);
                    }
                    else {
                        purapLines.add(entry);
                    }
                }
            }
        }
    }

    /**
     * Sets the businessObjectService attribute.
     * 
     * @param businessObjectService The businessObjectService to set.
     */

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the extractDao attribute.
     * 
     * @param extractDao The extractDao to set.
     */

    public void setExtractDao(ExtractDao cabExtractDao) {
        this.extractDao = cabExtractDao;
    }

    /**
     * Sets the dateTimeService attribute.
     * 
     * @param dateTimeService The dateTimeService to set.
     */

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the parameterService attribute.
     * 
     * @param parameterService The parameterService to set.
     */

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void updateLastExtractTime(Timestamp time) {
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter parameter = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, primaryKeys);
        if (parameter != null) {
            SimpleDateFormat format = new SimpleDateFormat(CabConstants.DATE_FORMAT_TS);
            parameter.setParameterValue(format.format(time));
            businessObjectService.save(parameter);
        }
    }


}
