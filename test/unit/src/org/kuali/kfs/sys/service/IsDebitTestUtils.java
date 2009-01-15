/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.fp.document.IndirectCostAdjustmentDocument;
import org.kuali.kfs.fp.document.InternalBillingDocument;
import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.fp.document.PreEncumbranceDocument;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.fp.document.TransferOfFundsDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.AccountingLineRule;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * IsDebitTestUtils
 */
public class IsDebitTestUtils {
    /**
     * arbitrary amount constants
     */
    public static class Amount {
        public static final KualiDecimal POSITIVE = new KualiDecimal("10");
        public static final KualiDecimal NEGATIVE = new KualiDecimal("-5");
    }

    private static Map<Class<? extends TransactionalDocument>, String> sourceLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> targetLines = new HashMap<Class<? extends TransactionalDocument>, String>();

    private static class BaChartObjectCodes {
        public static final String EXPENSE = "5000";
        public static final String ASSET = "8010";
        public static final String INCOME = "1301";
        public static final String LIABILITY = "9120";
    }

    private static class ImportLines {
        public static final String DEFAULT = "BA,6044900,x,objc,x,x,x,0";
        public static final String WITH_DESCRIPTION = "BA,6044900,x,objc,x,x,x,description,0";
        public static final String WITH_REF_NUM_AND_DESCRIPTION = "BA,6044900,x,objc,x,x,x,refnum,description,0";
        public static final String WITH_REF_NUM = "BA,6044900,x,objc,x,x,x,refnum,0";
        public static final String WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION = "BA,6044900,x,objc,x,x,x,01,refnum,description,0";
        public static final String WITHOUT_OBJECT_CODE = "BA,6044900,x,x,x,x,0";
    }


    static {
        sourceLines.put(AdvanceDepositDocument.class, ImportLines.WITH_DESCRIPTION);
        sourceLines.put(CashReceiptDocument.class, ImportLines.WITH_DESCRIPTION);
        sourceLines.put(CreditCardReceiptDocument.class, ImportLines.WITH_DESCRIPTION);
        sourceLines.put(DisbursementVoucherDocument.class, ImportLines.WITH_DESCRIPTION);
        sourceLines.put(DistributionOfIncomeAndExpenseDocument.class, ImportLines.DEFAULT);
        sourceLines.put(GeneralErrorCorrectionDocument.class, ImportLines.WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION);
        sourceLines.put(IndirectCostAdjustmentDocument.class, ImportLines.WITHOUT_OBJECT_CODE);
        sourceLines.put(InternalBillingDocument.class, ImportLines.DEFAULT);
        sourceLines.put(NonCheckDisbursementDocument.class, ImportLines.WITH_REF_NUM_AND_DESCRIPTION);
        sourceLines.put(PreEncumbranceDocument.class, ImportLines.DEFAULT);
        sourceLines.put(ServiceBillingDocument.class, ImportLines.WITH_DESCRIPTION);
        sourceLines.put(TransferOfFundsDocument.class, ImportLines.DEFAULT);
    }

    static {
        targetLines.put(DistributionOfIncomeAndExpenseDocument.class, ImportLines.DEFAULT);
        targetLines.put(GeneralErrorCorrectionDocument.class, ImportLines.WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION);
        targetLines.put(IndirectCostAdjustmentDocument.class, ImportLines.WITHOUT_OBJECT_CODE);
        targetLines.put(InternalBillingDocument.class, ImportLines.DEFAULT);
        targetLines.put(PreEncumbranceDocument.class, ImportLines.WITH_REF_NUM);
        targetLines.put(ServiceBillingDocument.class, ImportLines.WITH_DESCRIPTION);
        targetLines.put(TransferOfFundsDocument.class, ImportLines.DEFAULT);
    }

    /**
     * @param documentService
     * @param documentClass
     * @return TransactionalDocument
     * @throws WorkflowException
     */
    public static AccountingDocument getDocument(DocumentService documentService, Class<? extends AccountingDocument> documentClass) throws WorkflowException {
        return (AccountingDocument) documentService.getNewDocument(documentClass);
    }

    /**
     * @param documentService
     * @param documentClass
     * @return TransactionalDocument
     * @throws WorkflowException
     */
    public static AccountingDocument getErrorCorrectionDocument(DocumentService documentService, Class<? extends AccountingDocument> documentClass) throws WorkflowException {
        AccountingDocument financialDocument = getDocument(documentService, documentClass);
        financialDocument.getDocumentHeader().setFinancialDocumentInErrorNumber("fakeErrorCorrection");

        return financialDocument;
    }

    private static AccountingLine getAccountingLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount, String objectCode) {
        String unparsedLine = null;
        AccountingLine line = null;
        if (SourceAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = sourceLines.get(financialDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in sourceMap for: " + financialDocument.getClass() + ";" + lineClass);
            }
            line = financialDocument.getAccountingLineParser().parseSourceAccountingLine(financialDocument, unparsedLine);
        }
        else if (TargetAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = targetLines.get(financialDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in targetMap for: " + financialDocument.getClass() + ";" + lineClass);
            }
            line = financialDocument.getAccountingLineParser().parseTargetAccountingLine(financialDocument, unparsedLine);
        }
        else {
            throw new IllegalArgumentException("invalid accounting line type (" + lineClass + ")");
        }

        line.setAmount(amount);
        line.setFinancialObjectCode(objectCode);
        return line;
    }

    /**
     * @param financialDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getExpenseLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(financialDocument, lineClass, amount, BaChartObjectCodes.EXPENSE);
    }

    /**
     * @param financialDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getAssetLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(financialDocument, lineClass, amount, BaChartObjectCodes.ASSET);
    }

    /**
     * @param financialDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getIncomeLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(financialDocument, lineClass, amount, BaChartObjectCodes.INCOME);
    }

    /**
     * @param financialDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getLiabilityLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(financialDocument, lineClass, amount, BaChartObjectCodes.LIABILITY);
    }

    /**
     * @param dataDictionaryService
     * @param dataDicitionaryService
     * @param financialDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isDebit(DataDictionaryService dataDictionaryService, DataDictionaryService dataDicitionaryService, AccountingDocument financialDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        String documentTypeName = dataDictionaryService.getValidDocumentTypeNameByClass(financialDocument.getClass());
        AccountingLineRule rule = (AccountingLineRule) dataDicitionaryService.getDataDictionary().getDocumentEntry(documentTypeName).getBusinessRulesClass().newInstance();

        return financialDocument.isDebit(accountingLine);
    }


    /**
     * @param dataDictionaryService
     * @param dataDicitionaryService
     * @param financialDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isDebitIllegalStateException(DataDictionaryService dataDictionaryService, DataDictionaryService dataDicitionaryService, AccountingDocument financialDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        boolean failedAsExpected = false;
        try {
            IsDebitTestUtils.isDebit(dataDictionaryService, dataDicitionaryService, financialDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            failedAsExpected = isDebitUtils.getDebitCalculationIllegalStateExceptionMessage().equals(e.getMessage());
        }

        return failedAsExpected;
    }

    /**
     * @param dataDictionaryService
     * @param dataDicitionaryService
     * @param financialDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isErrorCorrectionIllegalStateException(DataDictionaryService dataDictionaryService, DataDictionaryService dataDicitionaryService, AccountingDocument financialDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        boolean failedAsExpected = false;
        try {
            IsDebitTestUtils.isDebit(dataDictionaryService, dataDicitionaryService, financialDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            failedAsExpected = isDebitUtils.getErrorCorrectionIllegalStateExceptionMessage().equals(e.getMessage());
        }

        return failedAsExpected;
    }
}
