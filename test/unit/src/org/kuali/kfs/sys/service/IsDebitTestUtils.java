/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.service.AccountService;
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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.service.DocumentService;

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

    private static Map<String, String> sourceLines = new HashMap<String, String>();
    private static Map<String, String> targetLines = new HashMap<String, String>();

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
        sourceLines.put("AD", ImportLines.WITH_DESCRIPTION);
        sourceLines.put("CR", ImportLines.WITH_DESCRIPTION);
        sourceLines.put("CCR", ImportLines.WITH_DESCRIPTION);
        sourceLines.put("DV", ImportLines.WITH_DESCRIPTION);
        sourceLines.put("DI", ImportLines.DEFAULT);
        sourceLines.put("GEC", ImportLines.WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION);
        sourceLines.put("ICA", ImportLines.WITHOUT_OBJECT_CODE);
        sourceLines.put("IB", ImportLines.DEFAULT);
        sourceLines.put("ND", ImportLines.WITH_REF_NUM_AND_DESCRIPTION);
        sourceLines.put("PE", ImportLines.DEFAULT);
        sourceLines.put("SB", ImportLines.WITH_DESCRIPTION);
        sourceLines.put("TF", ImportLines.DEFAULT);
    }

    static {
        targetLines.put("DI", ImportLines.DEFAULT);
        targetLines.put("GEC", ImportLines.WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION);
        targetLines.put("ICA", ImportLines.WITHOUT_OBJECT_CODE);
        targetLines.put("IB", ImportLines.DEFAULT);
        targetLines.put("PE", ImportLines.WITH_REF_NUM);
        targetLines.put("SB", ImportLines.WITH_DESCRIPTION);
        targetLines.put("TF", ImportLines.DEFAULT);
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
        financialDocument.getFinancialSystemDocumentHeader().setFinancialDocumentInErrorNumber("fakeErrorCorrection");

        return financialDocument;
    }

    private static AccountingLine getAccountingLine(AccountingDocument financialDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount, String objectCode) {
        String unparsedLine = null;
        AccountingLine line = null;
        if (SourceAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = sourceLines.get(getDocumentTypeCode(financialDocument));
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in sourceMap for: " + financialDocument.getClass() + ";" + lineClass);
            }
            unparsedLine = removeChartIfNotNeeded(unparsedLine);
            line = financialDocument.getAccountingLineParser().parseSourceAccountingLine(financialDocument, unparsedLine);
        }
        else if (TargetAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = targetLines.get(getDocumentTypeCode(financialDocument));
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in targetMap for: " + financialDocument.getClass() + ";" + lineClass);
            }
            unparsedLine = removeChartIfNotNeeded(unparsedLine);
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
     * Returns the document type name for the given document
     * @param financialDocument the document to find a doc type name for
     * @return the doc type name
     */
    private static String getDocumentTypeCode(AccountingDocument financialDocument) {
        final DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final String docTypeName = dataDictionaryService.getDocumentTypeNameByClass(financialDocument.getClass());
        return docTypeName;
    }
    
    /**
     * Checks if accounts can cross charts; if not, removes chart from accounting line
     * @param accountingLine the accounting line to potentially correct
     * @return the accounting line, with perhaps the chart removed
     */
    private static String removeChartIfNotNeeded(String accountingLine) {
        final AccountService accountService = SpringContext.getBean(AccountService.class);
        final String updatedAccountingLine = (!accountService.accountsCanCrossCharts()) ?
                accountingLine.substring(3) :
                accountingLine;
        return updatedAccountingLine;
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
