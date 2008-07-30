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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Parameter;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.dataaccess.CabExtractDao;
import org.kuali.kfs.module.cab.batch.service.CabBatchExtractService;
import org.kuali.kfs.module.cab.businessobject.AccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CabBatchExtractServiceImpl implements CabBatchExtractService {
    protected CabExtractDao cabExtractDao;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;

    public Collection<Entry> findElgibleGLEntries() {
        BatchParameters parameters = new BatchParameters();
        Timestamp lastRunTime = getLastRunTimestamp();
        parameters.setLastRunTime(lastRunTime);
        parameters.setIncludedFinancialBalanceTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.BALANCE_TYPES));
        parameters.setIncludedFinancialObjectSubTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.OBJECT_SUB_TYPES));
        parameters.setExcludedChartCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS));
        parameters.setExcludedDocTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.DOCUMENT_TYPES));
        parameters.setExcludedFiscalPeriods(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.FISCAL_PERIODS));
        parameters.setExcludedSubFundCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS));
        return getCabExtractDao().findMatchingGLEntries(parameters);
    }

    protected Timestamp getLastRunTimestamp() {
        // String lastRunTS = parameterService.getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class,
        // CabConstants.Parameters.LAST_EXTRACT_TIME);
        String lastRunTS = null;
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

    public CabExtractDao getCabExtractDao() {
        return cabExtractDao;
    }

    public void setCabExtractDao(CabExtractDao cabExtractDao) {
        this.cabExtractDao = cabExtractDao;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void saveFPLines(List<Entry> fpLines) {
        for (Entry fpLine : fpLines) {
            // find matching entry from CB_GL_ENTRY_T
            Map<String, Object> glKeys = new LinkedHashMap<String, Object>();
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR, fpLine.getUniversityFiscalYear());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE, fpLine.getChartOfAccountsCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER, fpLine.getAccountNumber());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.SUB_ACCOUNT_NUMBER, fpLine.getSubAccountNumber());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE, fpLine.getFinancialObjectCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SUB_OBJECT_CODE, fpLine.getFinancialSubObjectCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_BALANCE_TYPE_CODE, fpLine.getFinancialBalanceTypeCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_TYPE_CODE, fpLine.getFinancialObjectTypeCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE, fpLine.getUniversityFiscalPeriodCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, fpLine.getFinancialDocumentTypeCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SYSTEM_ORIGINATION_CODE, fpLine.getFinancialSystemOriginationCode());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, fpLine.getDocumentNumber());
            glKeys.put(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, fpLine.getTransactionLedgerEntrySequenceNumber());
            Collection<GeneralLedgerEntry> matchingEntries = businessObjectService.findMatching(GeneralLedgerEntry.class, glKeys);
            // if not found, insert
            if (matchingEntries == null || matchingEntries.size() == 0) {
                GeneralLedgerEntry glEntry = new GeneralLedgerEntry(fpLine);
                businessObjectService.save(glEntry);
            }
        }
    }

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

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void savePOLines(List<Entry> poLines) {
        // find eligible GL entries using reconciliation
        HashMap<AccountLineGroup, KualiDecimal> glAccountGroup = new HashMap<AccountLineGroup, KualiDecimal>();
        for (Entry entry : poLines) {
            KualiDecimal amount = entry.getTransactionLedgerEntryAmount();
            if (amount != null) {
                if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
                    // negate the amount
                    amount = amount.multiply(new KualiDecimal(-1));
                }
                glAccountGroup.put(new AccountLineGroup(entry), amount);
            }
        }

        for (Entry entry : poLines) {
            PaymentRequestDocument paymentRequestDocument = null;
            CreditMemoDocument creditMemoDocument = null;
            if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
                // find PREQ
                paymentRequestDocument = findPaymentRequestDocument(entry);
            }
            else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                // find CM
                creditMemoDocument = findCreditMemoDocument(entry);
            }
            if (paymentRequestDocument == null && creditMemoDocument == null) {
                // TODO handle error
            }
            else if (paymentRequestDocument != null) {
                // handle PREQ
                List<PaymentRequestItem> items = paymentRequestDocument.getItems();
                for (PaymentRequestItem paymentRequestItem : items) {
                    // find account line history
                    List<PurApAccountingLine> paymentAccountingLines = paymentRequestItem.getSourceAccountingLines();
                }
            }
            else {
                // handle CM

            }

        }
    }

    protected CreditMemoDocument findCreditMemoDocument(Entry entry) {
        CreditMemoDocument creditMemoDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put("documentNumber", entry.getDocumentNumber());
        Collection<CreditMemoDocument> matchingCms = businessObjectService.findMatching(CreditMemoDocument.class, keys);
        if (matchingCms != null && matchingCms.size() == 1) {
            creditMemoDocument = matchingCms.iterator().next();
        }
        return creditMemoDocument;
    }

    protected PaymentRequestDocument findPaymentRequestDocument(Entry entry) {
        PaymentRequestDocument paymentRequestDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put("documentNumber", entry.getDocumentNumber());
        Collection<PaymentRequestDocument> matchingPreqs = businessObjectService.findMatching(PaymentRequestDocument.class, keys);
        if (matchingPreqs != null && matchingPreqs.size() == 1) {
            paymentRequestDocument = matchingPreqs.iterator().next();
        }
        return paymentRequestDocument;
    }
}
