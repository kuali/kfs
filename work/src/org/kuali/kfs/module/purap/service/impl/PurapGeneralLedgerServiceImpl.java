/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service.impl;

import static org.kuali.kfs.module.purap.PurapConstants.HUNDRED;
import static org.kuali.kfs.module.purap.PurapConstants.PURAP_ORIGIN_CODE;
import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE;
import static org.kuali.kfs.sys.KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.MONTH1;
import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurapGeneralLedgerServiceImpl implements PurapGeneralLedgerService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapGeneralLedgerServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private KualiRuleService kualiRuleService;
    private PaymentRequestService paymentRequestService;
    private ParameterService parameterService;
    private PurapAccountingService purapAccountingService;
    private PurchaseOrderService purchaseOrderService;
    private UniversityDateService universityDateService;
    private ObjectCodeService objectCodeService;
    private SubObjectCodeService subObjectCodeService;

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#customizeGeneralLedgerPendingEntry(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry,
     *      java.lang.Integer, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void customizeGeneralLedgerPendingEntry(PurchasingAccountsPayableDocument purapDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, Integer referenceDocumentNumber, String debitCreditCode, String docType, boolean isEncumbrance) {
        LOG.debug("customizeGeneralLedgerPendingEntry() started");

        explicitEntry.setDocumentNumber(purapDocument.getDocumentNumber());
        explicitEntry.setTransactionLedgerEntryDescription(entryDescription(purapDocument.getVendorName()));
        explicitEntry.setFinancialSystemOriginationCode(PURAP_ORIGIN_CODE);

        // Always make the referring document the PO for all PURAP docs except for CM against a vendor.
        // This is required for encumbrance entries. It's not required for actual/liability
        // entries, but it makes things easier to deal with. If vendor, leave referring stuff blank.
        if (ObjectUtils.isNotNull(referenceDocumentNumber)) {
            explicitEntry.setReferenceFinancialDocumentNumber(referenceDocumentNumber.toString());
            explicitEntry.setReferenceFinancialDocumentTypeCode(PurapDocTypeCodes.PO_DOCUMENT);
            explicitEntry.setReferenceFinancialSystemOriginationCode(PURAP_ORIGIN_CODE);
        }

        // DEFAULT TO USE CURRENT; don't use FY on doc in case it's a prior year
        UniversityDate uDate = universityDateService.getCurrentUniversityDate();
        explicitEntry.setUniversityFiscalYear(uDate.getUniversityFiscalYear());
        explicitEntry.setUniversityFiscalPeriodCode(uDate.getUniversityFiscalAccountingPeriod());

        if (PurapDocTypeCodes.PO_DOCUMENT.equals(docType)) {
            if (purapDocument.getPostingYear().compareTo(uDate.getUniversityFiscalYear()) > 0) {
                // USE NEXT AS SET ON PO; POs can be forward dated to not encumber until next fiscal year
                explicitEntry.setUniversityFiscalYear(purapDocument.getPostingYear());
                explicitEntry.setUniversityFiscalPeriodCode(MONTH1);
            }
        }
        else if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            PaymentRequestDocument preq = (PaymentRequestDocument) purapDocument;
            if (paymentRequestService.allowBackpost(preq)) {
                LOG.debug("createGlPendingTransaction() within range to allow backpost; posting entry to period 12 of previous FY");
                explicitEntry.setUniversityFiscalYear(uDate.getUniversityFiscalYear() - 1);
                explicitEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH12);
            }

            // if alternate payee is paid for non-primary vendor payment, send alternate vendor name in GL desc
            if (preq.getAlternateVendorHeaderGeneratedIdentifier() != null && preq.getAlternateVendorDetailAssignedIdentifier() != null && preq.getVendorHeaderGeneratedIdentifier().compareTo(preq.getAlternateVendorHeaderGeneratedIdentifier()) == 0 && preq.getVendorDetailAssignedIdentifier().compareTo(preq.getAlternateVendorDetailAssignedIdentifier()) == 0) {
                explicitEntry.setTransactionLedgerEntryDescription(entryDescription(preq.getPurchaseOrderDocument().getAlternateVendorName()));
            }

        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            VendorCreditMemoDocument cm = (VendorCreditMemoDocument) purapDocument;
            if (cm.isSourceDocumentPaymentRequest()) {
                // if CM is off of PREQ, use vendor name associated with PREQ (primary or alternate)
                PaymentRequestDocument cmPR = cm.getPaymentRequestDocument();
                PurchaseOrderDocument cmPO = cm.getPurchaseOrderDocument();
                // if alternate payee is paid for non-primary vendor payment, send alternate vendor name in GL desc
                if (cmPR.getAlternateVendorHeaderGeneratedIdentifier() != null && cmPR.getAlternateVendorDetailAssignedIdentifier() != null && cmPR.getVendorHeaderGeneratedIdentifier().compareTo(cmPR.getAlternateVendorHeaderGeneratedIdentifier()) == 0 && cmPR.getVendorDetailAssignedIdentifier().compareTo(cmPR.getAlternateVendorDetailAssignedIdentifier()) == 0) {
                    explicitEntry.setTransactionLedgerEntryDescription(entryDescription(cmPO.getAlternateVendorName()));
                }
            }
        }
        else {
            throw new IllegalArgumentException("purapDocument (doc #" + purapDocument.getDocumentNumber() + ") is invalid");
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(explicitEntry.getUniversityFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialObjectCode());
        if (ObjectUtils.isNotNull(objectCode)) {
            explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        }

        SubObjectCode subObjectCode = subObjectCodeService.getByPrimaryId(explicitEntry.getUniversityFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getAccountNumber(), explicitEntry.getFinancialObjectCode(), explicitEntry.getFinancialSubObjectCode());
        if (ObjectUtils.isNotNull(subObjectCode)) {
            explicitEntry.setFinancialSubObjectCode(subObjectCode.getFinancialSubObjectCode());
        }

        if (isEncumbrance) {
            explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_EXTERNAL_ENCUMBRANCE);

            // D - means the encumbrance is based on the document number
            // R - means the encumbrance is based on the referring document number
            // All encumbrances should set the update code to 'R' regardless of if they were created by the PO, PREQ, or CM
            explicitEntry.setTransactionEncumbranceUpdateCode(ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        }

        // if the amount is negative, flip the D/C indicator
        if (accountingLine.getAmount().doubleValue() < 0) {
            if (GL_CREDIT_CODE.equals(debitCreditCode)) {
                explicitEntry.setTransactionDebitCreditCode(GL_DEBIT_CODE);
            }
            else {
                explicitEntry.setTransactionDebitCreditCode(GL_CREDIT_CODE);
            }
        }
        else {
            explicitEntry.setTransactionDebitCreditCode(debitCreditCode);
        }

    }// end purapCustomizeGeneralLedgerPendingEntry()

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesCancelAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public void generateEntriesCancelAccountsPayableDocument(AccountsPayableDocument apDocument) {
        LOG.debug("generateEntriesCancelAccountsPayableDocument() started");
        if (apDocument instanceof PaymentRequestDocument) {
            LOG.info("generateEntriesCancelAccountsPayableDocument() cancel PaymentRequestDocument");
            generateEntriesCancelPaymentRequest((PaymentRequestDocument) apDocument);
        }
        else if (apDocument instanceof VendorCreditMemoDocument) {
            LOG.info("generateEntriesCancelAccountsPayableDocument() cancel CreditMemoDocument");
            generateEntriesCancelCreditMemo((VendorCreditMemoDocument) apDocument);
        }
        else {
            // doc not found
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesCreatePaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void generateEntriesCreatePaymentRequest(PaymentRequestDocument preq) {
        LOG.debug("generateEntriesCreatePaymentRequest() started");
        List<SourceAccountingLine> encumbrances = relieveEncumbrance(preq);
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(preq);
        generateEntriesPaymentRequest(preq, encumbrances, summaryAccounts, CREATE_PAYMENT_REQUEST);
    }

    /**
     * Called from generateEntriesCancelAccountsPayableDocument() for Payment Request Document
     *
     * @param preq Payment Request document to cancel
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesCancelAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    protected void generateEntriesCancelPaymentRequest(PaymentRequestDocument preq) {
        LOG.debug("generateEntriesCreatePaymentRequest() started");
        List<SourceAccountingLine> encumbrances = reencumberEncumbrance(preq);
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(preq);
        generateEntriesPaymentRequest(preq, encumbrances, summaryAccounts, CANCEL_PAYMENT_REQUEST);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesModifyPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void generateEntriesModifyPaymentRequest(PaymentRequestDocument preq) {
        LOG.debug("generateEntriesModifyPaymentRequest() started");

        Map<SourceAccountingLine, KualiDecimal> actualsPositive = new HashMap<SourceAccountingLine, KualiDecimal>();
        List<SourceAccountingLine> newAccountingLines = purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(preq.getItems());
        for (SourceAccountingLine newAccount : newAccountingLines) {
            actualsPositive.put(newAccount, newAccount.getAmount());
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesModifyPaymentRequest() actualsPositive: " + newAccount.getAccountNumber() + " = " + newAccount.getAmount());
            }
        }

        Map<SourceAccountingLine, KualiDecimal> actualsNegative = new HashMap<SourceAccountingLine, KualiDecimal>();
        List<AccountsPayableSummaryAccount> oldAccountingLines = purapAccountingService.getAccountsPayableSummaryAccounts(preq.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);

        for (AccountsPayableSummaryAccount oldAccount : oldAccountingLines) {
            actualsNegative.put(oldAccount.generateSourceAccountingLine(), oldAccount.getAmount());
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesModifyPaymentRequest() actualsNegative: " + oldAccount.getAccountNumber() + " = " + oldAccount.getAmount());
            }
        }

        // Add the positive entries and subtract the negative entries
        Map<SourceAccountingLine, KualiDecimal> glEntries = new HashMap<SourceAccountingLine, KualiDecimal>();

        // Combine the two maps (copy all the positive entries)
        LOG.debug("generateEntriesModifyPaymentRequest() Combine positive/negative entries");
        glEntries.putAll(actualsPositive);

        for (Iterator<SourceAccountingLine> iter = actualsNegative.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine key = iter.next();

            KualiDecimal amt;
            if (glEntries.containsKey(key)) {
                amt = glEntries.get(key);
                amt = amt.subtract(actualsNegative.get(key));
            }
            else {
                amt = ZERO;
                amt = amt.subtract(actualsNegative.get(key));
            }
            glEntries.put(key, amt);
        }

        List<SummaryAccount> summaryAccounts = new ArrayList<SummaryAccount>();
        for (Iterator<SourceAccountingLine> iter = glEntries.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = iter.next();
            KualiDecimal amount = glEntries.get(account);
            if (ZERO.compareTo(amount) != 0) {
                account.setAmount(amount);
                SummaryAccount sa = new SummaryAccount(account);
                summaryAccounts.add(sa);
            }
        }

        LOG.debug("generateEntriesModifyPaymentRequest() Generate GL entries");
        generateEntriesPaymentRequest(preq, null, summaryAccounts, MODIFY_PAYMENT_REQUEST);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesCreateCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @Override
    public void generateEntriesCreateCreditMemo(VendorCreditMemoDocument cm) {
        LOG.debug("generateEntriesCreateCreditMemo() started");
        generateEntriesCreditMemo(cm, CREATE_CREDIT_MEMO);
    }

    /**
     * Called from generateEntriesCancelAccountsPayableDocument() for Payment Request Document
     *
     * @param preq Payment Request document to cancel
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesCancelAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    protected void generateEntriesCancelCreditMemo(VendorCreditMemoDocument cm) {
        LOG.debug("generateEntriesCancelCreditMemo() started");
        generateEntriesCreditMemo(cm, CANCEL_CREDIT_MEMO);
    }

    /**
     * Retrieves the next available sequence number from the general ledger pending entry table for this document
     *
     * @param documentNumber Document number to find next sequence number
     * @return Next available sequence number
     */
    protected int getNextAvailableSequence(String documentNumber) {
        LOG.debug("getNextAvailableSequence() started");
        Map fieldValues = new HashMap();
        fieldValues.put("financialSystemOriginationCode", PURAP_ORIGIN_CODE);
        fieldValues.put("documentNumber", documentNumber);
        int count = businessObjectService.countMatching(GeneralLedgerPendingEntry.class, fieldValues);
        return count + 1;
    }

    /**
     * Creates the general ledger entries for Payment Request actions.
     *
     * @param preq Payment Request document to create entries
     * @param encumbrances List of encumbrance accounts if applies
     * @param accountingLines List of preq accounts to create entries
     * @param processType Type of process (create, modify, cancel)
     * @return Boolean returned indicating whether entry creation succeeded
     */
    protected boolean generateEntriesPaymentRequest(PaymentRequestDocument preq, List encumbrances, List summaryAccounts, String processType) {
        LOG.debug("generateEntriesPaymentRequest() started");
        boolean success = true;
        preq.setGeneralLedgerPendingEntries(new ArrayList());

        /*
         * Can't let generalLedgerPendingEntryService just create all the entries because we need the sequenceHelper to carry over
         * from the encumbrances to the actuals and also because we need to tell the PaymentRequestDocumentRule customize entry
         * method how to customize differently based on if creating an encumbrance or actual.
         */
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(getNextAvailableSequence(preq.getDocumentNumber()));

        // when cancelling a PREQ, do not book encumbrances if PO is CLOSED
        if (encumbrances != null && !(CANCEL_PAYMENT_REQUEST.equals(processType) && PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(preq.getPurchaseOrderDocument().getApplicationDocumentStatus()))) {
            LOG.debug("generateEntriesPaymentRequest() generate encumbrance entries");
            if (CREATE_PAYMENT_REQUEST.equals(processType)) {
                // on create, use CREDIT code for encumbrances
                preq.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                // on cancel, use DEBIT code
                preq.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
            }
            else if (MODIFY_PAYMENT_REQUEST.equals(processType)) {
                // no encumbrances for modify
            }

            preq.setGenerateEncumbranceEntries(true);
            for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) iter.next();
                preq.generateGeneralLedgerPendingEntries(accountingLine, sequenceHelper);
                sequenceHelper.increment(); // increment for the next line
            }
        }

        if (ObjectUtils.isNotNull(summaryAccounts) && !summaryAccounts.isEmpty()) {
            LOG.debug("generateEntriesPaymentRequest() now book the actuals");
            preq.setGenerateEncumbranceEntries(false);

            if (CREATE_PAYMENT_REQUEST.equals(processType) || MODIFY_PAYMENT_REQUEST.equals(processType)) {
                // on create and modify, use DEBIT code
                preq.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                // on cancel, use CREDIT code
                preq.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
            }

            for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
                SummaryAccount summaryAccount = (SummaryAccount) iter.next();
                preq.generateGeneralLedgerPendingEntries(summaryAccount.getAccount(), sequenceHelper);
                sequenceHelper.increment(); // increment for the next line
            }

            // generate offset accounts for use tax if it exists (useTaxContainers will be empty if not a use tax document)
            List<UseTaxContainer> useTaxContainers = purapAccountingService.generateUseTaxAccount(preq);
            for (UseTaxContainer useTaxContainer : useTaxContainers) {
                PurApItemUseTax offset = useTaxContainer.getUseTax();
                List<SourceAccountingLine> accounts = useTaxContainer.getAccounts();
                for (SourceAccountingLine sourceAccountingLine : accounts) {
                    preq.generateGeneralLedgerPendingEntries(sourceAccountingLine, sequenceHelper, useTaxContainer.getUseTax());
                    sequenceHelper.increment(); // increment for the next line
                }

            }

            // Manually save preq summary accounts
            if (MODIFY_PAYMENT_REQUEST.equals(processType)) {
                //for modify, regenerate the summary from the doc
                List<SummaryAccount> summaryAccountsForModify = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(preq);
                saveAccountsPayableSummaryAccounts(summaryAccountsForModify, preq.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);
            }
            else {
                //for create and cancel, use the summary accounts
                saveAccountsPayableSummaryAccounts(summaryAccounts, preq.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);
            }

            // manually save cm account change tables (CAMS needs this)
            if (CREATE_PAYMENT_REQUEST.equals(processType) || MODIFY_PAYMENT_REQUEST.equals(processType)) {
                SpringContext.getBean(PurapAccountRevisionService.class).savePaymentRequestAccountRevisions(preq.getItems(), preq.getPostingYearFromPendingGLEntries(), preq.getPostingPeriodCodeFromPendingGLEntries());
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                SpringContext.getBean(PurapAccountRevisionService.class).cancelPaymentRequestAccountRevisions(preq.getItems(), preq.getPostingYearFromPendingGLEntries(), preq.getPostingPeriodCodeFromPendingGLEntries());
            }
        }


        // Manually save GL entries for Payment Request and encumbrances
        saveGLEntries(preq.getGeneralLedgerPendingEntries());

        return success;
    }

    /**
     * Creates the general ledger entries for Credit Memo actions.
     *
     * @param cm Credit Memo document to create entries
     * @param isCancel Indicates if request is a cancel or create
     * @return Boolean returned indicating whether entry creation succeeded
     */
    protected boolean generateEntriesCreditMemo(VendorCreditMemoDocument cm, boolean isCancel) {
        LOG.debug("generateEntriesCreditMemo() started");

        cm.setGeneralLedgerPendingEntries(new ArrayList());

        boolean success = true;
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(getNextAvailableSequence(cm.getDocumentNumber()));

        if (!cm.isSourceVendor()) {
            LOG.debug("generateEntriesCreditMemo() create encumbrance entries for CM against a PO or PREQ (not vendor)");
            PurchaseOrderDocument po = null;
            if (cm.isSourceDocumentPurchaseOrder()) {
                LOG.debug("generateEntriesCreditMemo() PO type");
                po = purchaseOrderService.getCurrentPurchaseOrder(cm.getPurchaseOrderIdentifier());
            }
            else if (cm.isSourceDocumentPaymentRequest()) {
                LOG.debug("generateEntriesCreditMemo() PREQ type");
                po = purchaseOrderService.getCurrentPurchaseOrder(cm.getPaymentRequestDocument().getPurchaseOrderIdentifier());
            }

            // for CM cancel or create, do not book encumbrances if PO is CLOSED, but do update the amounts on the PO
            List encumbrances = getCreditMemoEncumbrance(cm, po, isCancel);
            if (!(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(po.getApplicationDocumentStatus()))) {
                if (encumbrances != null) {
                    cm.setGenerateEncumbranceEntries(true);

                    // even if generating encumbrance entries on cancel, call is the same because the method gets negative amounts
                    // from
                    // the map so Debits on negatives = a credit
                    cm.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);

                    for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                        AccountingLine accountingLine = (AccountingLine) iter.next();
                        if (accountingLine.getAmount().compareTo(ZERO) != 0) {
                            cm.generateGeneralLedgerPendingEntries(accountingLine, sequenceHelper);
                            sequenceHelper.increment(); // increment for the next line
                        }
                    }
                }
            }
        }

        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(cm);
        if (summaryAccounts != null) {
            LOG.debug("generateEntriesCreditMemo() now book the actuals");
            cm.setGenerateEncumbranceEntries(false);

            if (!isCancel) {
                // on create, use CREDIT code
                cm.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
            }
            else {
                // on cancel, use DEBIT code
                cm.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
            }

            for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
                SummaryAccount summaryAccount = (SummaryAccount) iter.next();
                cm.generateGeneralLedgerPendingEntries(summaryAccount.getAccount(), sequenceHelper);
                sequenceHelper.increment(); // increment for the next line
            }
            // generate offset accounts for use tax if it exists (useTaxContainers will be empty if not a use tax document)
            List<UseTaxContainer> useTaxContainers = purapAccountingService.generateUseTaxAccount(cm);
            for (UseTaxContainer useTaxContainer : useTaxContainers) {
                PurApItemUseTax offset = useTaxContainer.getUseTax();
                List<SourceAccountingLine> accounts = useTaxContainer.getAccounts();
                for (SourceAccountingLine sourceAccountingLine : accounts) {
                    cm.generateGeneralLedgerPendingEntries(sourceAccountingLine, sequenceHelper, useTaxContainer.getUseTax());
                    sequenceHelper.increment(); // increment for the next line
                }

            }

            // manually save cm account change tables (CAMS needs this)
            if (!isCancel) {
                SpringContext.getBean(PurapAccountRevisionService.class).saveCreditMemoAccountRevisions(cm.getItems(), cm.getPostingYearFromPendingGLEntries(), cm.getPostingPeriodCodeFromPendingGLEntries());
            }
            else {
                SpringContext.getBean(PurapAccountRevisionService.class).cancelCreditMemoAccountRevisions(cm.getItems(), cm.getPostingYearFromPendingGLEntries(), cm.getPostingPeriodCodeFromPendingGLEntries());
            }
        }

        saveGLEntries(cm.getGeneralLedgerPendingEntries());

        LOG.debug("generateEntriesCreditMemo() ended");
        return success;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesApproveAmendPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void generateEntriesApproveAmendPurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesApproveAmendPurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            // if invoice fields are null (as would be for new items), set fields to zero
            item.setItemInvoicedTotalAmount(item.getItemInvoicedTotalAmount() == null ? ZERO : item.getItemInvoicedTotalAmount());
            item.setItemInvoicedTotalQuantity(item.getItemInvoicedTotalQuantity() == null ? ZERO : item.getItemInvoicedTotalQuantity());

            if (!item.isItemActiveIndicator()) {
                // set outstanding encumbrance amounts to zero for inactive items
                item.setItemOutstandingEncumberedQuantity(ZERO);
                item.setItemOutstandingEncumberedAmount(ZERO);

                for (Iterator iter = item.getSourceAccountingLines().iterator(); iter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
                    account.setItemAccountOutstandingEncumbranceAmount(ZERO);
                    account.setAlternateAmountForGLEntryCreation(ZERO);
                }
            }
            else {
                // Set quantities
                if (item.getItemQuantity() != null) {
                    item.setItemOutstandingEncumberedQuantity(item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity()));
                }
                else {
                    // if order qty is null, outstanding encumbered qty should be null
                    item.setItemOutstandingEncumberedQuantity(null);
                }

                // Set amount
                if (item.getItemOutstandingEncumberedQuantity() != null) {
                    //do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                    KualiDecimal itemEncumber = new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(item.getItemUnitPrice()));

                    //add tax for encumbrance
                    KualiDecimal itemTaxAmount = item.getItemTaxAmount() == null ? ZERO : item.getItemTaxAmount();
                    itemEncumber = itemEncumber.add(itemTaxAmount);

                    item.setItemOutstandingEncumberedAmount(itemEncumber);
                }
                else {
                    if (item.getItemUnitPrice() != null) {
                        item.setItemOutstandingEncumberedAmount(new KualiDecimal(item.getItemUnitPrice().subtract(item.getItemInvoicedTotalAmount().bigDecimalValue())));
                    }
                }

                for (Iterator iter = item.getSourceAccountingLines().iterator(); iter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
                    BigDecimal percent = new BigDecimal(account.getAccountLinePercent().toString());
                    percent = percent.divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP);
                    account.setItemAccountOutstandingEncumbranceAmount(item.getItemOutstandingEncumberedAmount().multiply(new KualiDecimal(percent)));
                    account.setAlternateAmountForGLEntryCreation(account.getItemAccountOutstandingEncumbranceAmount());
                }
            }
        }

        PurchaseOrderDocument oldPO = purchaseOrderService.getCurrentPurchaseOrder(po.getPurapDocumentIdentifier());

        if (oldPO == null) {
            throw new IllegalArgumentException("Current Purchase Order not found - poId = " + oldPO.getPurapDocumentIdentifier());
        }

        List newAccounts = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly());
        List oldAccounts = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(oldPO.getItemsActiveOnlySetupAlternateAmount());

        Map combination = new HashMap();

        // Add amounts from the new PO
        for (Iterator iter = newAccounts.iterator(); iter.hasNext();) {
            SourceAccountingLine newAccount = (SourceAccountingLine) iter.next();
            combination.put(newAccount, newAccount.getAmount());
        }

        LOG.info("generateEntriesApproveAmendPurchaseOrder() combination after the add");
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = (SourceAccountingLine) iter.next();
            LOG.info("generateEntriesApproveAmendPurchaseOrder() " + element + " = " + ((KualiDecimal) combination.get(element)).floatValue());
        }

        // Subtract the amounts from the old PO
        for (Iterator iter = oldAccounts.iterator(); iter.hasNext();) {
            SourceAccountingLine oldAccount = (SourceAccountingLine) iter.next();
            if (combination.containsKey(oldAccount)) {
                KualiDecimal amount = (KualiDecimal) combination.get(oldAccount);
                amount = amount.subtract(oldAccount.getAmount());
                combination.put(oldAccount, amount);
            }
            else {
                combination.put(oldAccount, ZERO.subtract(oldAccount.getAmount()));
            }
        }

        LOG.debug("generateEntriesApproveAmendPurchaseOrder() combination after the subtract");
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = (SourceAccountingLine) iter.next();
            LOG.info("generateEntriesApproveAmendPurchaseOrder() " + element + " = " + ((KualiDecimal) combination.get(element)).floatValue());
        }

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) combination.get(account);
            if (ZERO.compareTo(amount) != 0) {
                account.setAmount(amount);
                encumbranceAccounts.add(account);
            }
        }

        po.setGlOnlySourceAccountingLines(encumbranceAccounts);
        generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
        saveGLEntries(po.getGeneralLedgerPendingEntries());
        LOG.debug("generateEntriesApproveAmendPo() gl entries created; exit method");
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesClosePurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void generateEntriesClosePurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesClosePurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            KualiDecimal itemAmount = null;
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesClosePurchaseOrder() " + logItmNbr + " Calculate based on amounts");
            }
            itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO : item.getItemOutstandingEncumberedAmount();

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if (itemAmount.compareTo(ZERO) != 0) {
                // Sort accounts
                Collections.sort((List) item.getSourceAccountingLines());

                for (Iterator iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                        KualiDecimal acctAmount = itemAmount.multiply(new KualiDecimal(acct.getAccountLinePercent().toString())).divide(PurapConstants.HUNDRED);
                        accountTotal = accountTotal.add(acctAmount);
                        acct.setAlternateAmountForGLEntryCreation(acctAmount);
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generateEntriesClosePurchaseOrder() difference: " + logItmNbr + " " + difference);
                    }

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    }
                    else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }// endfor

        po.setGlOnlySourceAccountingLines(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly()));
        if (shouldGenerateGLPEForPurchaseOrder(po)) {
            generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
            saveGLEntries(po.getGeneralLedgerPendingEntries());
            LOG.debug("generateEntriesClosePurchaseOrder() gl entries created; exit method");
        }

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.setItemOutstandingEncumberedQuantity(KualiDecimal.ZERO);
            }
            item.setItemOutstandingEncumberedAmount(KualiDecimal.ZERO);
            List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
            for (PurApAccountingLine purApAccountingLine : sourceAccountingLines) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) purApAccountingLine;
                account.setItemAccountOutstandingEncumbranceAmount(KualiDecimal.ZERO);
            }
        }

        LOG.debug("generateEntriesClosePurchaseOrder() exit method");
    }

    /**
     * We should not generate general ledger pending entries for Purchase Order Close Document and
     * Purchase Order Reopen Document with $0 amount.
     *
     * @param po
     * @return
     */
    protected boolean shouldGenerateGLPEForPurchaseOrder(PurchaseOrderDocument po) {
        for (SourceAccountingLine acct : (List<SourceAccountingLine>)po.getSourceAccountingLines()) {
            if (acct.getAmount().abs().compareTo(new KualiDecimal(0)) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesReopenPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void generateEntriesReopenPurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesReopenPurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity());
                item.setItemOutstandingEncumberedQuantity(item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity()));
                item.setItemOutstandingEncumberedAmount(new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(item.getItemUnitPrice())));
            }
            else {
                item.setItemOutstandingEncumberedAmount(item.getTotalAmount().subtract(item.getItemInvoicedTotalAmount()));
            }
            List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
            for (PurApAccountingLine purApAccountingLine : sourceAccountingLines) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) purApAccountingLine;
                account.setItemAccountOutstandingEncumbranceAmount(new KualiDecimal(item.getItemOutstandingEncumberedAmount().bigDecimalValue().multiply(account.getAccountLinePercent()).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue())));
            }
        }// endfor

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            KualiDecimal itemAmount = null;
            if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateEntriesReopenPurchaseOrder() " + logItmNbr + " Calculate based on amounts");
                }
                itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO : item.getItemOutstandingEncumberedAmount();
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateEntriesReopenPurchaseOrder() " + logItmNbr + " Calculate based on quantities");
                }
                //do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                itemAmount = new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(item.getItemUnitPrice()));
            }

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if (itemAmount.compareTo(ZERO) != 0) {
                // Sort accounts
                Collections.sort((List) item.getSourceAccountingLines());

                for (Iterator iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                        KualiDecimal acctAmount = itemAmount.multiply(new KualiDecimal(acct.getAccountLinePercent().toString())).divide(PurapConstants.HUNDRED);
                        accountTotal = accountTotal.add(acctAmount);
                        acct.setAlternateAmountForGLEntryCreation(acctAmount);
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generateEntriesReopenPurchaseOrder() difference: " + logItmNbr + " " + difference);
                    }

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    }
                    else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }// endfor

        po.setGlOnlySourceAccountingLines(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly()));
        if (shouldGenerateGLPEForPurchaseOrder(po)) {
            generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
            saveGLEntries(po.getGeneralLedgerPendingEntries());
            LOG.debug("generateEntriesReopenPurchaseOrder() gl entries created; exit method");
        }
        LOG.debug("generateEntriesReopenPurchaseOrder() no gl entries created because the amount is 0; exit method");
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapGeneralLedgerService#generateEntriesVoidPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void generateEntriesVoidPurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesVoidPurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            //just use the outstanding amount as recalculating here, particularly the item tax will cause
            //amounts to be over or under encumbered and the remaining encumbered amount should be unencumbered during a close
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesVoidPurchaseOrder() " + logItmNbr + " Calculate based on amounts");
            }
            KualiDecimal itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO : item.getItemOutstandingEncumberedAmount();

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if (itemAmount.compareTo(ZERO) != 0) {
                // Sort accounts
                Collections.sort((List) item.getSourceAccountingLines());

                for (Iterator iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                        KualiDecimal acctAmount = itemAmount.multiply(new KualiDecimal(acct.getAccountLinePercent().toString())).divide(PurapConstants.HUNDRED);
                        accountTotal = accountTotal.add(acctAmount);
                        acct.setAlternateAmountForGLEntryCreation(acctAmount);
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generateEntriesVoidPurchaseOrder() difference: " + logItmNbr + " " + difference);
                    }

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    }
                    else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }// endfor

        po.setGlOnlySourceAccountingLines(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly()));
        generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
        saveGLEntries(po.getGeneralLedgerPendingEntries());
        LOG.debug("generateEntriesVoidPurchaseOrder() gl entries created; exit method");
    }

    /**
     * Relieve the Encumbrance on a PO based on values in a PREQ. This is to be called when a PREQ is created. Note: This modifies
     * the encumbrance values on the PO and saves the PO
     *
     * @param preq PREQ for invoice
     * @return List of accounting lines to use to create the pending general ledger entries
     */
    protected List<SourceAccountingLine> relieveEncumbrance(PaymentRequestDocument preq) {
        LOG.debug("relieveEncumbrance() started");

        Map encumbranceAccountMap = new HashMap();
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(preq.getPurchaseOrderIdentifier());

        // Get each item one by one
        for (Iterator items = preq.getItems().iterator(); items.hasNext();) {
            PaymentRequestItem preqItem = (PaymentRequestItem) items.next();
            PurchaseOrderItem poItem = getPoItem(po, preqItem.getItemLineNumber(), preqItem.getItemType());

            boolean takeAll = false; // Set this true if we relieve the entire encumbrance
            KualiDecimal itemDisEncumber = null; // Amount to disencumber for this item

            String logItmNbr = "Item # " + preqItem.getItemLineNumber();
            if (LOG.isDebugEnabled()) {
                LOG.debug("relieveEncumbrance() " + logItmNbr);
            }

            // If there isn't a PO item or the extended price is 0, we don't need encumbrances
            if (poItem == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("relieveEncumbrance() " + logItmNbr + " No encumbrances required because po item is null");
                }
            }
            else {
                final KualiDecimal preqItemTotalAmount = (preqItem.getTotalAmount() == null) ? KualiDecimal.ZERO : preqItem.getTotalAmount();
                if (ZERO.compareTo(preqItemTotalAmount) == 0) {
                    /*
                     * This is a specialized case where PREQ item being processed must adjust the PO item's outstanding encumbered
                     * quantity. This kind of scenario is mostly seen on warranty type items. The following must be true to do this:
                     * PREQ item Extended Price must be ZERO, PREQ item invoice quantity must be not empty and not ZERO, and PO item
                     * is quantity based PO item unit cost is ZERO
                     */
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " No GL encumbrances required because extended price is ZERO");
                    }
                    if ((poItem.getItemQuantity() != null) && ((BigDecimal.ZERO.compareTo(poItem.getItemUnitPrice())) == 0)) {
                        // po has order quantity and unit price is ZERO... reduce outstanding encumbered quantity
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate po oustanding encumbrance");
                        }

                        // Do encumbrance calculations based on quantity
                        if ((preqItem.getItemQuantity() != null) && ((ZERO.compareTo(preqItem.getItemQuantity())) != 0)) {
                            KualiDecimal invoiceQuantity = preqItem.getItemQuantity();
                            KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();

                            KualiDecimal encumbranceQuantity;
                            if (invoiceQuantity.compareTo(outstandingEncumberedQuantity) > 0) {
                                // We bought more than the quantity on the PO
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("relieveEncumbrance() " + logItmNbr + " we bought more than the qty on the PO");
                                }
                                encumbranceQuantity = outstandingEncumberedQuantity;
                                poItem.setItemOutstandingEncumberedQuantity(ZERO);
                            }
                            else {
                                encumbranceQuantity = invoiceQuantity;
                                poItem.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity.subtract(encumbranceQuantity));
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("relieveEncumbrance() " + logItmNbr + " adjusting oustanding encunbrance qty - encumbranceQty " + encumbranceQuantity + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity());
                                }
                            }

                            if (poItem.getItemInvoicedTotalQuantity() == null) {
                                poItem.setItemInvoicedTotalQuantity(invoiceQuantity);
                            }
                            else {
                                poItem.setItemInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity().add(invoiceQuantity));
                            }
                        }
                    }


                }
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");
                    }

                    // Do we calculate the encumbrance amount based on quantity or amount?
                    if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");
                        }

                        // Do encumbrance calculations based on quantity
                        KualiDecimal invoiceQuantity = preqItem.getItemQuantity() == null ? ZERO : preqItem.getItemQuantity();
                        KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();

                        KualiDecimal encumbranceQuantity;

                        if (invoiceQuantity.compareTo(outstandingEncumberedQuantity) > 0) {
                            // We bought more than the quantity on the PO
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("relieveEncumbrance() " + logItmNbr + " we bought more than the qty on the PO");
                            }
                            encumbranceQuantity = outstandingEncumberedQuantity;
                            poItem.setItemOutstandingEncumberedQuantity(ZERO);
                            takeAll = true;
                        }
                        else {
                            encumbranceQuantity = invoiceQuantity;
                            poItem.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity.subtract(encumbranceQuantity));
                            if (ZERO.compareTo(poItem.getItemOutstandingEncumberedQuantity()) == 0) {
                                takeAll = true;
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("relieveEncumbrance() " + logItmNbr + " encumbranceQty " + encumbranceQuantity + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity());
                            }
                        }

                        if (poItem.getItemInvoicedTotalQuantity() == null) {
                            poItem.setItemInvoicedTotalQuantity(invoiceQuantity);
                        }
                        else {
                            poItem.setItemInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity().add(invoiceQuantity));
                        }

                        itemDisEncumber = new KualiDecimal(encumbranceQuantity.bigDecimalValue().multiply(poItem.getItemUnitPrice()));

                        //add tax for encumbrance
                        KualiDecimal itemTaxAmount = poItem.getItemTaxAmount() == null ? ZERO : poItem.getItemTaxAmount();
                        KualiDecimal encumbranceTaxAmount = encumbranceQuantity.divide(poItem.getItemQuantity()).multiply(itemTaxAmount);
                        itemDisEncumber = itemDisEncumber.add(encumbranceTaxAmount);
                    }
                    else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");
                        }

                        // Do encumbrance calculations based on amount only
                        if ((poItem.getItemOutstandingEncumberedAmount().bigDecimalValue().signum() == -1) && (preqItemTotalAmount.bigDecimalValue().signum() == -1)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is negative: " + poItem.getItemOutstandingEncumberedAmount());
                            }
                            if (preqItemTotalAmount.compareTo(poItem.getItemOutstandingEncumberedAmount()) >= 0) {
                                // extended price is equal to or greater than outstanding encumbered
                                itemDisEncumber = preqItemTotalAmount;
                            }
                            else {
                                // extended price is less than outstanding encumbered
                                takeAll = true;
                                itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                            }
                        }
                        else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is positive or ZERO: " + poItem.getItemOutstandingEncumberedAmount());
                            }
                            if (poItem.getItemOutstandingEncumberedAmount().compareTo(preqItemTotalAmount) >= 0) {
                                // outstanding amount is equal to or greater than extended price
                                itemDisEncumber = preqItemTotalAmount;
                            }
                            else {
                                // outstanding amount is less than extended price
                                takeAll = true;
                                itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                            }
                        }
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Amount to disencumber: " + itemDisEncumber);
                    }

                    KualiDecimal newOutstandingEncumberedAmount = poItem.getItemOutstandingEncumberedAmount().subtract(itemDisEncumber);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " New Outstanding Encumbered amount is : " + newOutstandingEncumberedAmount);
                    }
                    poItem.setItemOutstandingEncumberedAmount(newOutstandingEncumberedAmount);

                    KualiDecimal newInvoicedTotalAmount = poItem.getItemInvoicedTotalAmount().add(preqItemTotalAmount);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " New Invoiced Total Amount is: " + newInvoicedTotalAmount);
                    }
                    poItem.setItemInvoicedTotalAmount(newInvoicedTotalAmount);

                    // Sort accounts
                    Collections.sort((List) poItem.getSourceAccountingLines());

                    // make the list of accounts for the disencumbrance entry
                    PurchaseOrderAccount lastAccount = null;
                    KualiDecimal accountTotal = ZERO;
                    for (Iterator accountIter = poItem.getSourceAccountingLines().iterator(); accountIter.hasNext();) {
                        PurchaseOrderAccount account = (PurchaseOrderAccount) accountIter.next();
                        if (!account.isEmpty()) {
                            KualiDecimal encumbranceAmount = null;
                            SourceAccountingLine acctString = account.generateSourceAccountingLine();
                            if (takeAll) {
                                // fully paid; remove remaining encumbrance
                                encumbranceAmount = account.getItemAccountOutstandingEncumbranceAmount();
                                account.setItemAccountOutstandingEncumbranceAmount(ZERO);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("relieveEncumbrance() " + logItmNbr + " take all");
                                }
                            }
                            else {
                                // amount = item disencumber * account percent / 100
                                encumbranceAmount = itemDisEncumber.multiply(new KualiDecimal(account.getAccountLinePercent().toString())).divide(HUNDRED);

                                account.setItemAccountOutstandingEncumbranceAmount(account.getItemAccountOutstandingEncumbranceAmount().subtract(encumbranceAmount));

                                // For rounding check at the end
                                accountTotal = accountTotal.add(encumbranceAmount);

                                // If we are zeroing out the encumbrance, we don't need to adjust for rounding
                                if (!takeAll) {
                                    lastAccount = account;
                                }
                            }

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("relieveEncumbrance() " + logItmNbr + " " + acctString + " = " + encumbranceAmount);
                            }
                            if (ObjectUtils.isNull(encumbranceAccountMap.get(acctString))) {
                                encumbranceAccountMap.put(acctString, encumbranceAmount);
                            }
                            else {
                                KualiDecimal amt = (KualiDecimal) encumbranceAccountMap.get(acctString);
                                encumbranceAccountMap.put(acctString, amt.add(encumbranceAmount));
                            }

                        }
                    }

                    // account for rounding by adjusting last account as needed
                    if (lastAccount != null) {
                        KualiDecimal difference = itemDisEncumber.subtract(accountTotal);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("relieveEncumbrance() difference: " + logItmNbr + " " + difference);
                        }

                        SourceAccountingLine acctString = lastAccount.generateSourceAccountingLine();
                        KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
                        if (ObjectUtils.isNull(amount)) {
                            encumbranceAccountMap.put(acctString, difference);
                        }
                        else {
                            encumbranceAccountMap.put(acctString, amount.add(difference));
                        }

                        lastAccount.setItemAccountOutstandingEncumbranceAmount(lastAccount.getItemAccountOutstandingEncumbranceAmount().subtract(difference));
                    }
                }
            }
        }// endfor

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = encumbranceAccountMap.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine acctString = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
            if (amount.doubleValue() != 0) {
                acctString.setAmount(amount);
                encumbranceAccounts.add(acctString);
            }
        }

        SpringContext.getBean(BusinessObjectService.class).save(po);
        return encumbranceAccounts;
    }

    /**
     * Re-encumber the Encumbrance on a PO based on values in a PREQ. This is used when a PREQ is cancelled. Note: This modifies the
     * encumbrance values on the PO and saves the PO
     *
     * @param preq PREQ for invoice
     * @return List of accounting lines to use to create the pending general ledger entries
     */
    protected List<SourceAccountingLine> reencumberEncumbrance(PaymentRequestDocument preq) {
        LOG.debug("reencumberEncumbrance() started");

        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(preq.getPurchaseOrderIdentifier());
        Map encumbranceAccountMap = new HashMap();

        // Get each item one by one
        for (Iterator items = preq.getItems().iterator(); items.hasNext();) {
            PaymentRequestItem payRequestItem = (PaymentRequestItem) items.next();
            PurchaseOrderItem poItem = getPoItem(po, payRequestItem.getItemLineNumber(), payRequestItem.getItemType());

            KualiDecimal itemReEncumber = null; // Amount to reencumber for this item

            String logItmNbr = "Item # " + payRequestItem.getItemLineNumber();
            if (LOG.isDebugEnabled()) {
                LOG.debug("reencumberEncumbrance() " + logItmNbr);
            }

            // If there isn't a PO item or the total amount is 0, we don't need encumbrances
            final KualiDecimal preqItemTotalAmount = (payRequestItem.getTotalAmount() == null) ? KualiDecimal.ZERO : payRequestItem.getTotalAmount();
            if ((poItem == null) || (preqItemTotalAmount.doubleValue() == 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " No encumbrances required");
                }
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");
                }

                // Do we calculate the encumbrance amount based on quantity or amount?
                if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");
                    }

                    // Do disencumbrance calculations based on quantity
                    KualiDecimal preqQuantity = payRequestItem.getItemQuantity() == null ? ZERO : payRequestItem.getItemQuantity();
                    KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();
                    KualiDecimal invoicedTotal = poItem.getItemInvoicedTotalQuantity() == null ? ZERO : poItem.getItemInvoicedTotalQuantity();

                    poItem.setItemInvoicedTotalQuantity(invoicedTotal.subtract(preqQuantity));
                    poItem.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity.add(preqQuantity));

                    //do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                    itemReEncumber = new KualiDecimal(preqQuantity.bigDecimalValue().multiply(poItem.getItemUnitPrice()));

                    //add tax for encumbrance
                    KualiDecimal itemTaxAmount = poItem.getItemTaxAmount() == null ? ZERO : poItem.getItemTaxAmount();
                    KualiDecimal encumbranceTaxAmount = preqQuantity.divide(poItem.getItemQuantity()).multiply(itemTaxAmount);
                    itemReEncumber = itemReEncumber.add(encumbranceTaxAmount);

                }
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");
                    }

                    itemReEncumber = preqItemTotalAmount;
                    // if re-encumber amount is more than original PO ordered amount... do not exceed ordered amount
                    // this prevents negative encumbrance
                    if ((poItem.getTotalAmount() != null) && (poItem.getTotalAmount().bigDecimalValue().signum() < 0)) {
                        // po item extended cost is negative
                        if ((poItem.getTotalAmount().compareTo(itemReEncumber)) > 0) {
                            itemReEncumber = poItem.getTotalAmount();
                        }
                    }
                    else if ((poItem.getTotalAmount() != null) && (poItem.getTotalAmount().bigDecimalValue().signum() >= 0)) {
                        // po item extended cost is positive
                        if ((poItem.getTotalAmount().compareTo(itemReEncumber)) < 0) {
                            itemReEncumber = poItem.getTotalAmount();
                        }
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " Amount to reencumber: " + itemReEncumber);
                }

                KualiDecimal outstandingEncumberedAmount = poItem.getItemOutstandingEncumberedAmount() == null ? ZERO : poItem.getItemOutstandingEncumberedAmount();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " PO Item Outstanding Encumbrance Amount set to: " + outstandingEncumberedAmount);
                }
                KualiDecimal newOutstandingEncumberedAmount = outstandingEncumberedAmount.add(itemReEncumber);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " New PO Item Outstanding Encumbrance Amount to set: " + newOutstandingEncumberedAmount);
                }
                poItem.setItemOutstandingEncumberedAmount(newOutstandingEncumberedAmount);

                KualiDecimal invoicedTotalAmount = poItem.getItemInvoicedTotalAmount() == null ? ZERO : poItem.getItemInvoicedTotalAmount();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " PO Item Invoiced Total Amount set to: " + invoicedTotalAmount);
                }
                KualiDecimal newInvoicedTotalAmount = invoicedTotalAmount.subtract(preqItemTotalAmount);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " New PO Item Invoiced Total Amount to set: " + newInvoicedTotalAmount);
                }
                poItem.setItemInvoicedTotalAmount(newInvoicedTotalAmount);

                // make the list of accounts for the reencumbrance entry
                PurchaseOrderAccount lastAccount = null;
                KualiDecimal accountTotal = ZERO;

                // Sort accounts
                Collections.sort((List) poItem.getSourceAccountingLines());

                for (Iterator accountIter = poItem.getSourceAccountingLines().iterator(); accountIter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) accountIter.next();
                    if (!account.isEmpty()) {
                        SourceAccountingLine acctString = account.generateSourceAccountingLine();

                        // amount = item reencumber * account percent / 100
                        KualiDecimal reencumbranceAmount = itemReEncumber.multiply(new KualiDecimal(account.getAccountLinePercent().toString())).divide(HUNDRED);

                        account.setItemAccountOutstandingEncumbranceAmount(account.getItemAccountOutstandingEncumbranceAmount().add(reencumbranceAmount));

                        // For rounding check at the end
                        accountTotal = accountTotal.add(reencumbranceAmount);

                        lastAccount = account;

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("reencumberEncumbrance() " + logItmNbr + " " + acctString + " = " + reencumbranceAmount);
                        }
                        if (encumbranceAccountMap.containsKey(acctString)) {
                            KualiDecimal currentAmount = (KualiDecimal) encumbranceAccountMap.get(acctString);
                            encumbranceAccountMap.put(acctString, reencumbranceAmount.add(currentAmount));
                        }
                        else {
                            encumbranceAccountMap.put(acctString, reencumbranceAmount);
                        }
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemReEncumber.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("reencumberEncumbrance() difference: " + logItmNbr + " " + difference);
                    }

                    SourceAccountingLine acctString = lastAccount.generateSourceAccountingLine();
                    KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
                    if (amount == null) {
                        encumbranceAccountMap.put(acctString, difference);
                    }
                    else {
                        encumbranceAccountMap.put(acctString, amount.add(difference));
                    }
                    lastAccount.setItemAccountOutstandingEncumbranceAmount(lastAccount.getItemAccountOutstandingEncumbranceAmount().add(difference));
                }
            }
        }

        SpringContext.getBean(BusinessObjectService.class).save(po);

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator<SourceAccountingLine> iter = encumbranceAccountMap.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine acctString = iter.next();
            KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
            if (amount.doubleValue() != 0) {
                acctString.setAmount(amount);
                encumbranceAccounts.add(acctString);
            }
        }

        return encumbranceAccounts;
    }


    /**
     * Re-encumber the Encumbrance on a PO based on values in a PREQ. This is used when a PREQ is cancelled. Note: This modifies the
     * encumbrance values on the PO and saves the PO
     *
     * @param cm Credit Memo document
     * @param po Purchase Order document modify encumbrances
     * @return List of accounting lines to use to create the pending general ledger entries
     */
    protected List<SourceAccountingLine> getCreditMemoEncumbrance(VendorCreditMemoDocument cm, PurchaseOrderDocument po, boolean cancel) {
        LOG.debug("getCreditMemoEncumbrance() started");

        if (ObjectUtils.isNull(po)) {
            return null;
        }

        if (cancel) {
            LOG.debug("getCreditMemoEncumbrance() Receiving items back from vendor (cancelled CM)");
        }
        else {
            LOG.debug("getCreditMemoEncumbrance() Returning items to vendor");
        }

        Map encumbranceAccountMap = new HashMap();

        // Get each item one by one
        for (Iterator items = cm.getItems().iterator(); items.hasNext();) {
            CreditMemoItem cmItem = (CreditMemoItem) items.next();
            PurchaseOrderItem poItem = getPoItem(po, cmItem.getItemLineNumber(), cmItem.getItemType());

            KualiDecimal itemDisEncumber = null; // Amount to disencumber for this item
            KualiDecimal itemAlterInvoiceAmt = null; // Amount to alter the invoicedAmt on the PO item

            String logItmNbr = "Item # " + cmItem.getItemLineNumber();
            if (LOG.isDebugEnabled()) {
                LOG.debug("getCreditMemoEncumbrance() " + logItmNbr);
            }

            final KualiDecimal cmItemTotalAmount = (cmItem.getTotalAmount() == null) ? KualiDecimal.ZERO : cmItem.getTotalAmount();
            ;
            // If there isn't a PO item or the total amount is 0, we don't need encumbrances
            if ((poItem == null) || (cmItemTotalAmount == null) || (cmItemTotalAmount.doubleValue() == 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " No encumbrances required");
                }
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");
                }

                // Do we calculate the encumbrance amount based on quantity or amount?
                if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");
                    }

                    // Do encumbrance calculations based on quantity
                    KualiDecimal cmQuantity = cmItem.getItemQuantity() == null ? ZERO : cmItem.getItemQuantity();

                    KualiDecimal encumbranceQuantityChange = calculateQuantityChange(cancel, poItem, cmQuantity);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " encumbranceQtyChange " + encumbranceQuantityChange + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity() + " invoicedTotalQuantity " + poItem.getItemInvoicedTotalQuantity());
                    }

                    //do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                    itemDisEncumber = new KualiDecimal(encumbranceQuantityChange.bigDecimalValue().multiply(poItem.getItemUnitPrice()));

                    //add tax for encumbrance
                    KualiDecimal itemTaxAmount = poItem.getItemTaxAmount() == null ? ZERO : poItem.getItemTaxAmount();
                    KualiDecimal encumbranceTaxAmount = encumbranceQuantityChange.divide(poItem.getItemQuantity()).multiply(itemTaxAmount);
                    itemDisEncumber = itemDisEncumber.add(encumbranceTaxAmount);

                    itemAlterInvoiceAmt = cmItemTotalAmount;
                    if (cancel) {
                        itemAlterInvoiceAmt = itemAlterInvoiceAmt.multiply(new KualiDecimal("-1"));
                    }
                }
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");
                    }

                    // Do encumbrance calculations based on amount only
                    if (cancel) {
                        // Decrease encumbrance
                        itemDisEncumber = cmItemTotalAmount.multiply(new KualiDecimal("-1"));

                        if (poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber).doubleValue() < 0) {
                            LOG.debug("getCreditMemoEncumbrance() Cancel overflow");

                            itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                        }
                    }
                    else {
                        // Increase encumbrance
                        itemDisEncumber = cmItemTotalAmount;

                        if (poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber).doubleValue() > poItem.getTotalAmount().doubleValue()) {
                            LOG.debug("getCreditMemoEncumbrance() Create overflow");

                            itemDisEncumber = poItem.getTotalAmount().subtract(poItem.getItemOutstandingEncumberedAmount());
                        }
                    }
                    itemAlterInvoiceAmt = itemDisEncumber;
                }

                // alter the encumbrance based on what was originally encumbered
                poItem.setItemOutstandingEncumberedAmount(poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber));

                // alter the invoiced amt based on what was actually credited on the credit memo
                poItem.setItemInvoicedTotalAmount(poItem.getItemInvoicedTotalAmount().subtract(itemAlterInvoiceAmt));
                if (poItem.getItemInvoicedTotalAmount().compareTo(ZERO) < 0) {
                    poItem.setItemInvoicedTotalAmount(ZERO);
                }


                if (LOG.isDebugEnabled()) {
                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Amount to disencumber: " + itemDisEncumber);
                }

                // Sort accounts
                Collections.sort((List) poItem.getSourceAccountingLines());

                // make the list of accounts for the disencumbrance entry
                PurchaseOrderAccount lastAccount = null;
                KualiDecimal accountTotal = ZERO;
                // Collections.sort((List)poItem.getSourceAccountingLines());
                for (Iterator accountIter = poItem.getSourceAccountingLines().iterator(); accountIter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) accountIter.next();
                    if (!account.isEmpty()) {
                        KualiDecimal encumbranceAmount = null;

                        SourceAccountingLine acctString = account.generateSourceAccountingLine();
                        // amount = item disencumber * account percent / 100
                        encumbranceAmount = itemDisEncumber.multiply(new KualiDecimal(account.getAccountLinePercent().toString())).divide(new KualiDecimal(100));

                        account.setItemAccountOutstandingEncumbranceAmount(account.getItemAccountOutstandingEncumbranceAmount().add(encumbranceAmount));

                        // For rounding check at the end
                        accountTotal = accountTotal.add(encumbranceAmount);

                        lastAccount = account;

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " " + acctString + " = " + encumbranceAmount);
                        }

                        if (encumbranceAccountMap.get(acctString) == null) {
                            encumbranceAccountMap.put(acctString, encumbranceAmount);
                        }
                        else {
                            KualiDecimal amt = (KualiDecimal) encumbranceAccountMap.get(acctString);
                            encumbranceAccountMap.put(acctString, amt.add(encumbranceAmount));
                        }
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemDisEncumber.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getCreditMemoEncumbrance() difference: " + logItmNbr + " " + difference);
                    }

                    SourceAccountingLine acctString = lastAccount.generateSourceAccountingLine();
                    KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
                    if (amount == null) {
                        encumbranceAccountMap.put(acctString, difference);
                    }
                    else {
                        encumbranceAccountMap.put(acctString, amount.add(difference));
                    }
                    lastAccount.setItemAccountOutstandingEncumbranceAmount(lastAccount.getItemAccountOutstandingEncumbranceAmount().add(difference));
                }
            }
        }

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = encumbranceAccountMap.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine acctString = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
            if (amount.doubleValue() != 0) {
                acctString.setAmount(amount);
                encumbranceAccounts.add(acctString);
            }
        }

        SpringContext.getBean(BusinessObjectService.class).save(po);

        return encumbranceAccounts;
    }

    /**
     * Save the given general ledger entries
     *
     * @param glEntries List of GeneralLedgerPendingEntries to be saved
     */
    protected void saveGLEntries(List<GeneralLedgerPendingEntry> glEntries) {
        LOG.debug("saveGLEntries() started");
        businessObjectService.save(glEntries);
    }

    /**
     * Save the given accounts for the given document.
     *
     * @param sourceLines Accounts to be saved
     * @param purapDocumentIdentifier Purap document id for accounts
     */
    protected void saveAccountsPayableSummaryAccounts(List<SummaryAccount> summaryAccounts, Integer purapDocumentIdentifier, String docType) {
        LOG.debug("saveAccountsPayableSummaryAccounts() started");
        purapAccountingService.deleteSummaryAccounts(purapDocumentIdentifier, docType);
        List<AccountsPayableSummaryAccount> apSummaryAccounts = new ArrayList();
        for (SummaryAccount summaryAccount : summaryAccounts) {
            apSummaryAccounts.add(new AccountsPayableSummaryAccount(summaryAccount.getAccount(), purapDocumentIdentifier, docType));
        }
        businessObjectService.save(apSummaryAccounts);
    }

    /**
     * Find item in PO based on given parameters. Must send either the line # or item type.
     *
     * @param po Purchase Order containing list of items
     * @param nbr Line # of desired item (could be null)
     * @param itemType Item type of desired item
     * @return PurcahseOrderItem found matching given criteria
     */
    protected PurchaseOrderItem getPoItem(PurchaseOrderDocument po, Integer nbr, ItemType itemType) {
        LOG.debug("getPoItem() started");
        for (Iterator iter = po.getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem element = (PurchaseOrderItem) iter.next();
            if (itemType.isLineItemIndicator()) {
                if (ObjectUtils.isNotNull(nbr) && ObjectUtils.isNotNull(element.getItemLineNumber()) && (nbr.compareTo(element.getItemLineNumber()) == 0)) {
                    return element;
                }
            }
            else {
                if (element.getItemTypeCode().equals(itemType.getItemTypeCode())) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Format description for general ledger entry. Currently making sure length is less than 40 char.
     *
     * @param description String to be formatted
     * @return Formatted String
     */
    protected String entryDescription(String description) {
        if (description != null && description.length() > 40) {
            return description.toString().substring(0, 39);
        }
        else {
            return description;
        }
    }

    /**
     * Calculate quantity change for creating Credit Memo entries
     *
     * @param cancel Boolean indicating whether entries are for creation or cancellation of credit memo
     * @param poItem Purchase Order Item
     * @param cmQuantity Quantity on credit memo item
     * @return Calculated change
     */
    protected KualiDecimal calculateQuantityChange(boolean cancel, PurchaseOrderItem poItem, KualiDecimal cmQuantity) {
        LOG.debug("calculateQuantityChange() started");

        // Calculate quantity change & adjust invoiced quantity & outstanding encumbered quantity
        KualiDecimal encumbranceQuantityChange = null;
        if (cancel) {
            encumbranceQuantityChange = cmQuantity.multiply(new KualiDecimal("-1"));
        }
        else {
            encumbranceQuantityChange = cmQuantity;
        }
        poItem.setItemInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity().subtract(encumbranceQuantityChange));
        poItem.setItemOutstandingEncumberedQuantity(poItem.getItemOutstandingEncumberedQuantity().add(encumbranceQuantityChange));

        // Check for overflows
        if (cancel) {
            if (poItem.getItemOutstandingEncumberedQuantity().doubleValue() < 0) {
                LOG.debug("calculateQuantityChange() Cancel overflow");
                KualiDecimal difference = poItem.getItemOutstandingEncumberedQuantity().abs();
                poItem.setItemOutstandingEncumberedQuantity(ZERO);
                poItem.setItemInvoicedTotalQuantity(poItem.getItemQuantity());
                encumbranceQuantityChange = encumbranceQuantityChange.add(difference);
            }
        }
        else {
            if (poItem.getItemInvoicedTotalQuantity().doubleValue() < 0) {
                LOG.debug("calculateQuantityChange() Create overflow");
                KualiDecimal difference = poItem.getItemInvoicedTotalQuantity().abs();
                poItem.setItemOutstandingEncumberedQuantity(poItem.getItemQuantity());
                poItem.setItemInvoicedTotalQuantity(ZERO);
                encumbranceQuantityChange = encumbranceQuantityChange.add(difference);
            }
        }
        return encumbranceQuantityChange;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setSubObjectCodeService(SubObjectCodeService subObjectCodeService) {
        this.subObjectCodeService = subObjectCodeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

}
