/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.AccountingLineRule;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.CreditCardReceiptDocument;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.module.financial.document.IndirectCostAdjustmentDocument;
import org.kuali.module.financial.document.InternalBillingDocument;
import org.kuali.module.financial.document.NonCheckDisbursementDocument;
import org.kuali.module.financial.document.PreEncumbranceDocument;
import org.kuali.module.financial.document.ServiceBillingDocument;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBase.IsDebitUtils;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * IsDebitTestUtils
 * 
 * 
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
        public static final String DEFAULT = "BA,6044900,x,objc,x,x,x,x,0";
        public static final String WITH_DESCRIPTION = "BA,6044900,x,objc,x,x,x,description,x,0";
        public static final String WITH_REF_NUM_AND_DESCRIPTION = "BA,6044900,x,objc,x,x,x,refnum,description,x,0";
        public static final String WITH_REF_NUM = "BA,6044900,x,objc,x,x,x,refnum,x,0";
        public static final String WITH_ORIGIN_CODE_AND_REF_NUM_AND_DESCRIPTION = "BA,6044900,x,objc,x,x,x,01,refnum,description,x,0";
        public static final String WITHOUT_OBJECT_CODE = "BA,6044900,x,x,x,x,x,0";
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
    public static TransactionalDocument getDocument(DocumentService documentService, Class<? extends TransactionalDocument> documentClass) throws WorkflowException {
        return (TransactionalDocument) documentService.getNewDocument(documentClass);
    }

    /**
     * @param documentService
     * @param documentClass
     * @return TransactionalDocument
     * @throws WorkflowException
     */
    public static TransactionalDocument getErrorCorrectionDocument(DocumentService documentService, Class<? extends TransactionalDocument> documentClass) throws WorkflowException {
        TransactionalDocument transactionalDocument = getDocument(documentService, documentClass);
        transactionalDocument.getDocumentHeader().setFinancialDocumentInErrorNumber("fakeErrorCorrection");

        return transactionalDocument;
    }

    private static AccountingLine getAccountingLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount, String objectCode) {
        String unparsedLine = null;
        AccountingLine line = null;
        if (SourceAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = sourceLines.get(transactionalDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in sourceMap for: " + transactionalDocument.getClass() + ";" + lineClass);
            }
            line = transactionalDocument.getAccountingLineParser().parseSourceAccountingLine(transactionalDocument, unparsedLine);
        }
        else if (TargetAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = targetLines.get(transactionalDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in targetMap for: " + transactionalDocument.getClass() + ";" + lineClass);
            }
            line = transactionalDocument.getAccountingLineParser().parseTargetAccountingLine(transactionalDocument, unparsedLine);
        }
        else {
            throw new IllegalArgumentException("invalid accounting line type (" + lineClass + ")");
        }

        line.setAmount(amount);
        line.setFinancialObjectCode(objectCode);
        return line;
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getExpenseLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, amount, BaChartObjectCodes.EXPENSE);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getAssetLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, amount, BaChartObjectCodes.ASSET);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getIncomeLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, amount, BaChartObjectCodes.INCOME);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getLiabilityLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, amount, BaChartObjectCodes.LIABILITY);
    }

    /**
     * 
     * @param documentTypeService
     * @param dataDicitionaryService
     * @param transactionalDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isDebit(DocumentTypeService documentTypeService, DataDictionaryService dataDicitionaryService, TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        String documentTypeName = documentTypeService.getDocumentTypeNameByClass(transactionalDocument.getClass());
        AccountingLineRule rule = (AccountingLineRule) dataDicitionaryService.getDataDictionary().getBusinessRulesClass(documentTypeName).newInstance();

        return rule.isDebit(transactionalDocument, accountingLine);
    }


    /**
     * @param documentTypeService
     * @param dataDicitionaryService
     * @param transactionalDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isDebitIllegalStateException(DocumentTypeService documentTypeService, DataDictionaryService dataDicitionaryService, TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        boolean failedAsExpected = false;
        try {
            IsDebitTestUtils.isDebit(documentTypeService, dataDicitionaryService, transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage.equals(e.getMessage());
        }

        return failedAsExpected;
    }

    /**
     * @param documentTypeService
     * @param dataDicitionaryService
     * @param transactionalDocument
     * @param accountingLine
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static boolean isErrorCorrectionIllegalStateException(DocumentTypeService documentTypeService, DataDictionaryService dataDicitionaryService, TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws InstantiationException, IllegalAccessException {
        boolean failedAsExpected = false;
        try {
            IsDebitTestUtils.isDebit(documentTypeService, dataDicitionaryService, transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isErrorCorrectionIllegalStateExceptionMessage.equals(e.getMessage());
        }

        return failedAsExpected;
    }
}
