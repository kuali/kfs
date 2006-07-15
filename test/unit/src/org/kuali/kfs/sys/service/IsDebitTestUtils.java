/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class IsDebitTestUtils {
    /**
     * arbitrary amount constants
     */
    public static class Amount {
        public static final KualiDecimal POSITIVE = new KualiDecimal("10");
        public static final KualiDecimal NEGATIVE = new KualiDecimal("-5");
    }

    private static Map<Class<? extends TransactionalDocument>, String> sourceExpenseLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> targetExpenseLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> sourceAssetLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> targetAssetLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> sourceIncomeLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> targetIncomeLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> sourceLiabilityLines = new HashMap<Class<? extends TransactionalDocument>, String>();
    private static Map<Class<? extends TransactionalDocument>, String> targetLiabilityLines = new HashMap<Class<? extends TransactionalDocument>, String>();

    private static class Source {
        public static final String EXPENSE = "BA,6044900,x,5000,x,x,x,x,x,0";
        public static final String ASSET = "BA,6044900,x,8010,x,x,x,x,x,0";
        public static final String INCOME = "BA,6044900,x,1301,x,x,x,x,x,0";
        public static final String LIABILITY = "BA,6044900,x,9120,x,x,x,x,x,0";
    }

    private static class Target {
        public static final String EXPENSE = "BA,6044900,x,5000,x,x,x,x,x,0";
        public static final String ASSET = "BA,6044900,x,8010,x,x,x,x,x,0";
        public static final String INCOME = "BA,6044900,x,1301,x,x,x,x,x,0";
        public static final String LIABILITY = "BA,6044900,x,9120,x,x,x,x,x,0";

        private static class PreEncumrance {
            public static final String EXPENSE = "BA,6044900,x,5000,x,x,x,x,x,x,x,0";
            public static final String ASSET = "BA,6044900,x,8010,x,x,x,x,x,x,x,0";
            public static final String INCOME = "BA,6044900,x,1301,x,x,x,x,x,x,x,0";
            public static final String LIABILITY = "BA,6044900,x,9120,x,x,x,x,x,x,x,0";
        }
    }


    static {
        /**
         * EXPENSE:source
         */
        sourceExpenseLines.put(AdvanceDepositDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(CashReceiptDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(CreditCardReceiptDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(DisbursementVoucherDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(DistributionOfIncomeAndExpenseDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(GeneralErrorCorrectionDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(IndirectCostAdjustmentDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(InternalBillingDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(NonCheckDisbursementDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(PreEncumbranceDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(ServiceBillingDocument.class, Source.EXPENSE);
        sourceExpenseLines.put(TransferOfFundsDocument.class, Source.EXPENSE);
    }

    static {
        /**
         * EXPENSE:target
         */
        targetExpenseLines.put(DistributionOfIncomeAndExpenseDocument.class, Target.EXPENSE);
        targetExpenseLines.put(GeneralErrorCorrectionDocument.class, Target.EXPENSE);
        targetExpenseLines.put(IndirectCostAdjustmentDocument.class, Target.EXPENSE);
        targetExpenseLines.put(InternalBillingDocument.class, Target.EXPENSE);
        targetExpenseLines.put(PreEncumbranceDocument.class, Target.PreEncumrance.EXPENSE);
        targetExpenseLines.put(ServiceBillingDocument.class, Target.EXPENSE);
        targetExpenseLines.put(TransferOfFundsDocument.class, Target.EXPENSE);
    }

    static {
        /**
         * ASSET:source
         */
        sourceAssetLines.put(AdvanceDepositDocument.class, Source.ASSET);
        sourceAssetLines.put(CashReceiptDocument.class, Source.ASSET);
        sourceAssetLines.put(CreditCardReceiptDocument.class, Source.ASSET);
        sourceAssetLines.put(DisbursementVoucherDocument.class, Source.ASSET);
        sourceAssetLines.put(DistributionOfIncomeAndExpenseDocument.class, Source.ASSET);
        sourceAssetLines.put(GeneralErrorCorrectionDocument.class, Source.ASSET);
        sourceAssetLines.put(IndirectCostAdjustmentDocument.class, Source.ASSET);
        sourceAssetLines.put(InternalBillingDocument.class, Source.ASSET);
        sourceAssetLines.put(NonCheckDisbursementDocument.class, Source.ASSET);
        sourceAssetLines.put(PreEncumbranceDocument.class, Source.ASSET);
        sourceAssetLines.put(ServiceBillingDocument.class, Source.ASSET);
        sourceAssetLines.put(TransferOfFundsDocument.class, Source.ASSET);
    }

    static {
        /**
         * ASSET:target
         */
        targetAssetLines.put(DistributionOfIncomeAndExpenseDocument.class, Target.ASSET);
        targetAssetLines.put(GeneralErrorCorrectionDocument.class, Target.ASSET);
        targetAssetLines.put(IndirectCostAdjustmentDocument.class, Target.ASSET);
        targetAssetLines.put(InternalBillingDocument.class, Target.ASSET);
        targetAssetLines.put(PreEncumbranceDocument.class, Target.PreEncumrance.ASSET);
        targetAssetLines.put(ServiceBillingDocument.class, Target.ASSET);
        targetAssetLines.put(TransferOfFundsDocument.class, Target.ASSET);
    }

    static {
        /**
         * INCOME:source
         */
        sourceIncomeLines.put(AdvanceDepositDocument.class, Source.INCOME);
        sourceIncomeLines.put(CashReceiptDocument.class, Source.INCOME);
        sourceIncomeLines.put(CreditCardReceiptDocument.class, Source.INCOME);
        sourceIncomeLines.put(DisbursementVoucherDocument.class, Source.INCOME);
        sourceIncomeLines.put(DistributionOfIncomeAndExpenseDocument.class, Source.INCOME);
        sourceIncomeLines.put(GeneralErrorCorrectionDocument.class, Source.INCOME);
        sourceIncomeLines.put(IndirectCostAdjustmentDocument.class, Source.INCOME);
        sourceIncomeLines.put(InternalBillingDocument.class, Source.INCOME);
        sourceIncomeLines.put(NonCheckDisbursementDocument.class, Source.INCOME);
        sourceIncomeLines.put(PreEncumbranceDocument.class, Source.INCOME);
        sourceIncomeLines.put(ServiceBillingDocument.class, Source.INCOME);
        sourceIncomeLines.put(TransferOfFundsDocument.class, Source.INCOME);
    }

    static {
        /**
         * INCOME:target
         */
        targetIncomeLines.put(DistributionOfIncomeAndExpenseDocument.class, Target.INCOME);
        targetIncomeLines.put(GeneralErrorCorrectionDocument.class, Target.INCOME);
        targetIncomeLines.put(IndirectCostAdjustmentDocument.class, Target.INCOME);
        targetIncomeLines.put(InternalBillingDocument.class, Target.INCOME);
        targetIncomeLines.put(PreEncumbranceDocument.class, Target.PreEncumrance.INCOME);
        targetIncomeLines.put(ServiceBillingDocument.class, Target.INCOME);
        targetIncomeLines.put(TransferOfFundsDocument.class, Target.INCOME);
    }

    static {
        /**
         * LIABILITY:source
         */
        sourceLiabilityLines.put(AdvanceDepositDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(CashReceiptDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(CreditCardReceiptDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(DisbursementVoucherDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(DistributionOfIncomeAndExpenseDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(GeneralErrorCorrectionDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(IndirectCostAdjustmentDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(InternalBillingDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(NonCheckDisbursementDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(PreEncumbranceDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(ServiceBillingDocument.class, Source.LIABILITY);
        sourceLiabilityLines.put(TransferOfFundsDocument.class, Source.LIABILITY);

    }

    static {
        /**
         * LIABILITY:target
         */
        targetLiabilityLines.put(DistributionOfIncomeAndExpenseDocument.class, Target.LIABILITY);
        targetLiabilityLines.put(GeneralErrorCorrectionDocument.class, Target.LIABILITY);
        targetLiabilityLines.put(IndirectCostAdjustmentDocument.class, Target.LIABILITY);
        targetLiabilityLines.put(InternalBillingDocument.class, Target.LIABILITY);
        targetLiabilityLines.put(PreEncumbranceDocument.class, Target.PreEncumrance.LIABILITY);
        targetLiabilityLines.put(ServiceBillingDocument.class, Target.LIABILITY);
        targetLiabilityLines.put(TransferOfFundsDocument.class, Target.LIABILITY);
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

    private static AccountingLine getAccountingLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, Map<Class<? extends TransactionalDocument>, String> sourceLineMap, Map<Class<? extends TransactionalDocument>, String> targetLineMap, KualiDecimal amount) {
        String unparsedLine = null;
        AccountingLine line = null;
        if (SourceAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = sourceLineMap.get(transactionalDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in sourceMap for: " + transactionalDocument.getClass() + ";" + lineClass);
            }
            line = transactionalDocument.getAccountingLineParser().parseSourceAccountingLine(transactionalDocument,unparsedLine);
        }
        else if (TargetAccountingLine.class.isAssignableFrom(lineClass)) {
            unparsedLine = targetLineMap.get(transactionalDocument.getClass());
            if (unparsedLine == null) {
                throw new IllegalArgumentException("no value found in targetMap for: " + transactionalDocument.getClass() + ";" + lineClass);
            }
            line = transactionalDocument.getAccountingLineParser().parseTargetAccountingLine(transactionalDocument,unparsedLine);
        }
        else {
            throw new IllegalArgumentException("invalid accounting line type (" + lineClass + ")");
        }

        line.setAmount(amount);
        return line;
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getExpenseLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount)  {
        return getAccountingLine(transactionalDocument, lineClass, sourceExpenseLines, targetExpenseLines, amount);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getAssetLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount)  {
        return getAccountingLine(transactionalDocument, lineClass, sourceAssetLines, targetAssetLines, amount);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getIncomeLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, sourceIncomeLines, targetIncomeLines, amount);
    }

    /**
     * @param transactionalDocument
     * @param lineClass
     * @param amount
     * @return AccountingLine
     */
    public static AccountingLine getLiabilityLine(TransactionalDocument transactionalDocument, Class<? extends AccountingLine> lineClass, KualiDecimal amount) {
        return getAccountingLine(transactionalDocument, lineClass, sourceLiabilityLines, targetLiabilityLines, amount);
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
