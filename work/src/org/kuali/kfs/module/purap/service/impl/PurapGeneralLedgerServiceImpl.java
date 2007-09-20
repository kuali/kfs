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
package org.kuali.module.purap.service.impl;

import static org.kuali.core.util.KualiDecimal.ZERO;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE;
import static org.kuali.kfs.KFSConstants.ENCUMB_UPDT_DOCUMENT_CD;
import static org.kuali.kfs.KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD;
import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.MONTH1;
import static org.kuali.module.purap.PurapConstants.HUNDRED;
import static org.kuali.module.purap.PurapConstants.PURAP_ORIGIN_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.GenerateGeneralLedgerPendingEntriesEvent;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PaymentRequestSummaryAccount;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurapGeneralLedgerServiceImpl implements PurapGeneralLedgerService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapGeneralLedgerServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;
    private PaymentRequestService paymentRequestService;
    private PurapAccountingService purapAccountingService;
    private UniversityDateService universityDateService;

    private void saveGLEntries(List<GeneralLedgerPendingEntry> glEntries) {
        businessObjectService.save(glEntries);
    }
    
    private void savePaymentRequestSummaryAccounts(List<SourceAccountingLine> sourceLines, Integer purapDocumentIdentifier) {
        LOG.debug("savePaymentRequestSummaryAccounts() enter method");
        paymentRequestService.deleteSummaryAccounts(purapDocumentIdentifier);
        List<PaymentRequestSummaryAccount> summaryAccounts = new ArrayList();
        for (SourceAccountingLine account : sourceLines) {
            summaryAccounts.add(new PaymentRequestSummaryAccount(account, purapDocumentIdentifier));
        }
        businessObjectService.save(summaryAccounts);
    }
    
    private List getPaymentRequestSummaryAccounts(Integer purapDocumentIdentifier) {
        LOG.debug("getPaymentRequestSummaryAccounts() enter method");
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.PURAP_DOC_ID, purapDocumentIdentifier);
        return new ArrayList(businessObjectService.findMatching(PaymentRequestSummaryAccount.class, fieldValues));
    }
    
    public void customizeGeneralLedgerPendingEntry(PurchasingAccountsPayableDocument purapDocument, AccountingLine accountingLine, 
            GeneralLedgerPendingEntry explicitEntry, Integer referenceDocumentNumber, String debitCreditCode,
            String docType, boolean isEncumbrance) {

        UniversityDate uDate = universityDateService.getCurrentUniversityDate();

        explicitEntry.setFinancialSystemOriginationCode(PURAP_ORIGIN_CODE);
        explicitEntry.setReferenceFinancialSystemOriginationCode(PURAP_ORIGIN_CODE);
        explicitEntry.setReferenceFinancialDocumentTypeCode(PurapDocTypeCodes.PO_DOCUMENT);
        if (ObjectUtils.isNotNull(referenceDocumentNumber)) {
            explicitEntry.setReferenceFinancialDocumentNumber(referenceDocumentNumber.toString());
        }

        // TODO should we be doing it like this or storing the FY in the acct table in which case we wouldn't need this at all we'd inherit it from the accountingdocument
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(explicitEntry.getUniversityFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialObjectCode());
        if (ObjectUtils.isNotNull(objectCode)) {
            explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        }

        if (isEncumbrance) {
            explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_EXTERNAL_ENCUMBRANCE);

            // D - means the encumbrance is based on the document number
            // R - means the encumbrance is based on the referring document number
            // Encumbrances are created on the PO. They are updated by PREQ's and CM's.
            // So PO encumbrances are D, PREQ & CM's are R.
            if (PurapDocTypeCodes.PO_DOCUMENT.equals(docType)) {
                explicitEntry.setTransactionEncumbranceUpdateCode(ENCUMB_UPDT_DOCUMENT_CD);
            }
            else {
                explicitEntry.setTransactionEncumbranceUpdateCode(ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
            }
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


        if (PurapDocTypeCodes.PO_DOCUMENT.equals(docType)) {
            explicitEntry.setTransactionLedgerEntryDescription(entryDescription(purapDocument.getVendorName()));

            if (purapDocument.getPostingYear().compareTo(uDate.getUniversityFiscalYear()) > 0) {
                // USE NEXT AS SET ON PO; POs can be forward dated to not encumber until next fiscal year
                explicitEntry.setUniversityFiscalYear(purapDocument.getPostingYear());
                explicitEntry.setUniversityFiscalPeriodCode(MONTH1);
            }
            else {
                // USE CURRENT; don't use FY on PO in case it's a prior year
                explicitEntry.setUniversityFiscalYear(uDate.getUniversityFiscalYear());
                explicitEntry.setUniversityFiscalPeriodCode(uDate.getUniversityFiscalAccountingPeriod());
                // TODO do we need to update the doc posting year?
            }

        }
        else if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            PaymentRequestDocument preq = (PaymentRequestDocument) purapDocument;
            explicitEntry.setDocumentNumber(preq.getDocumentNumber());

            // PREQs created in the previous fiscal year get backdated if we're at the beginning of the new fiscal year (i.e. prior to first closing)
            Integer allowBackpost = new Integer(kualiConfigurationService.getApplicationParameterValue(PurapRuleConstants.PURAP_ADMIN_GROUP, PurapRuleConstants.ALLOW_BACKPOST_DAYS));
            if (allowBackpost == null) {
                throw new IllegalArgumentException("ALLOW_BACKPOST_DAYS needs to be defined in system parameters");
            }

            Calendar today = dateTimeService.getCurrentCalendar();
            Integer currentFY = uDate.getUniversityFiscalYear();
            Date priorClosingDateTemp = universityDateService.getLastDateOfFiscalYear(currentFY - 1);
            Calendar priorClosingDate = Calendar.getInstance();
            priorClosingDate.setTime(priorClosingDateTemp);

            Calendar allowBackpostDate = Calendar.getInstance();
            allowBackpostDate.setTime(priorClosingDate.getTime());
            allowBackpostDate.add(Calendar.DATE, allowBackpost.intValue());

            Calendar preqInvoiceDate = Calendar.getInstance();
            preqInvoiceDate.setTime(preq.getInvoiceDate());

            if (today.after(priorClosingDate) && today.before(allowBackpostDate) && (preqInvoiceDate.before(priorClosingDate) || preqInvoiceDate.equals(priorClosingDate))) {
                LOG.debug("createGlPendingTransaction() within range to allow backpost; posting entry to period 12 of previous FY");
                explicitEntry.setUniversityFiscalYear(currentFY - 1);
                explicitEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH12);
            }
            else {
                LOG.debug("createGlPendingTransaction() posting entry to current year and period");
                explicitEntry.setUniversityFiscalYear(currentFY);
                explicitEntry.setUniversityFiscalPeriodCode(uDate.getUniversityFiscalAccountingPeriod());
            }

            // if alternate payee is paid for escrow payment, send alternate vendor name in GL desc
            if (preq.getAlternateVendorHeaderGeneratedIdentifier() != null && 
                    preq.getAlternateVendorDetailAssignedIdentifier() != null && 
                    preq.getVendorHeaderGeneratedIdentifier().compareTo(preq.getAlternateVendorHeaderGeneratedIdentifier()) == 0 && 
                    preq.getVendorDetailAssignedIdentifier().compareTo(preq.getAlternateVendorDetailAssignedIdentifier()) == 0) {
                // TODO PHASE 3 - once alternate payee functionality is added, name might be stored in preq insted of having to go to PO
                explicitEntry.setTransactionLedgerEntryDescription(entryDescription(preq.getPurchaseOrderDocument().getAlternateVendorName()));
            }
            else {
                explicitEntry.setTransactionLedgerEntryDescription(entryDescription(preq.getPurchaseOrderDocument().getVendorName()));
            }

        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            CreditMemoDocument cm = (CreditMemoDocument) purapDocument;

            explicitEntry.setDocumentNumber(cm.getDocumentNumber());
            explicitEntry.setUniversityFiscalYear(uDate.getUniversityFiscalYear());
            explicitEntry.setUniversityFiscalPeriodCode(uDate.getUniversityFiscalAccountingPeriod());

            // TODO do we need to blank this out? (hjs)
            // gpt.setOrgDocNbr("");

            // Always make the referring document the PO for CM's unless the CM is a vendor type.
            // This is required for encumbrance entries. It's not required for actual/liability
            // entries, but it makes things easier to deal with. If vendor, leave referring stuff blank.
            if (cm.isSourceDocumentPaymentRequest()) {

                // if CM is off of PREQ, use vendor name associated with PREQ (primary or alternate)
                PaymentRequestDocument cmPR = cm.getPaymentRequestDocument();
                PurchaseOrderDocument cmPO = cm.getPurchaseOrderDocument();
                // if alternate payee is paid for escrow payment, send alternate vendor name in GL desc
                if (cmPR.getAlternateVendorHeaderGeneratedIdentifier() != null && 
                        cmPR.getAlternateVendorDetailAssignedIdentifier() != null && 
                        cmPR.getVendorHeaderGeneratedIdentifier().compareTo(cmPR.getAlternateVendorHeaderGeneratedIdentifier()) == 0 && 
                        cmPR.getVendorDetailAssignedIdentifier().compareTo(cmPR.getAlternateVendorDetailAssignedIdentifier()) == 0) {
                    explicitEntry.setTransactionLedgerEntryDescription(entryDescription(cmPO.getAlternateVendorName()));
                }
                else {
                    explicitEntry.setTransactionLedgerEntryDescription(entryDescription(cmPO.getVendorName()));
                }

            }
            else if (cm.isSourceDocumentPurchaseOrder()) {
                explicitEntry.setTransactionLedgerEntryDescription(entryDescription(cm.getVendorDetail().getVendorName()));
            }
            else {
                // Vendor type
                explicitEntry.setReferenceFinancialDocumentNumber(null);
                explicitEntry.setReferenceFinancialDocumentTypeCode(null);
                explicitEntry.setReferenceFinancialSystemOriginationCode(null);

                explicitEntry.setTransactionLedgerEntryDescription(entryDescription(cm.getVendorDetail().getVendorName()));
            }
        }
        else {
            throw new IllegalArgumentException("purapDocument (doc #" + purapDocument.getDocumentNumber() + ") is invalid");
        }


    }// end purapCustomizeGeneralLedgerPendingEntry()

    public void generateEntriesCancelAccountsPayableDocument(AccountsPayableDocument apDocument) {
        if (apDocument instanceof PaymentRequestDocument) {
            generateEntriesCancelPaymentRequest((PaymentRequestDocument)apDocument);
        }
        else if (apDocument instanceof CreditMemoDocument) {
            generateEntriesCancelCreditMemo((CreditMemoDocument)apDocument);
        }
        else {
            //doc not found
        }
    }
        
    public void generateEntriesCreatePaymentRequest(PaymentRequestDocument preq) {
        generateEntriesPaymentRequest(preq, CREATE_PAYMENT_REQUEST);
    }
        
    /**
     * This should re-encumber amounts that the PREQ disencumbered as well as reverse the expense and liability entries
     * More specifically, it should re-encumber amounts not to exceed amounts on the PO minus the total of other 
     * non-cancelled PREQs, which could possibly be different than the amounts disencumbered due to other things which 
     * may have happened in the meantime.
     * 
     * @param preq PREQ to cancel
     */
    private void generateEntriesCancelPaymentRequest(PaymentRequestDocument preq) {
        generateEntriesPaymentRequest(preq, CANCEL_PAYMENT_REQUEST);
    }

    private boolean generateEntriesPaymentRequest(PaymentRequestDocument preq, boolean isCancel) {

        boolean success = false;
        if (preq.getPurapDocumentIdentifier() != null) {
            
            /* Can't let generalLedgerPendingEntryService just create all the entries because we need the sequenceHelper to carry over
             * from the encumbrances to the actuals and also because we need to tell the PaymentRequestDocumentRule customize entry
             * method how to customize differently based on if creating an encumbrance or actual.
             */
            
            generalLedgerPendingEntryService.delete(preq.getDocumentNumber());
            
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();

            List encumbrances = null;
            if (!isCancel) {
                //on create, relieve encumbrances on PO
                encumbrances = relieveEncumbrance(preq);

                //on create, use CREDIT code
                preq.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
            }
            else {
                //on cancel, reencumber encumbrances on PO
                encumbrances = reencumberEncumbrance(preq);

                //on cancel, use DEBIT code
                preq.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
            }
                
            if (encumbrances != null) {
                preq.setGenerateEncumbranceEntries(true);
                for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                    AccountingLine accountingLine = (AccountingLine) iter.next();
                    GenerateGeneralLedgerPendingEntriesEvent glEvent = new GenerateGeneralLedgerPendingEntriesEvent(preq, accountingLine, sequenceHelper);
                    success &= kualiRuleService.applyRules(glEvent);
                    sequenceHelper.increment(); // increment for the next line
                }
            }

            //now book the actuals from the PREQ
            List accountingLines = purapAccountingService.generateSummaryWithNoZeroTotals(preq.getItems());
            if (accountingLines != null) {
                preq.setGenerateEncumbranceEntries(false);

                if (!isCancel) {
                    //on create, use DEBIT code
                    preq.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
                }
                else {
                    //on cancel, use CREDIT code
                    preq.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
                }

                for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
                    AccountingLine accountingLine = (AccountingLine) iter.next();
                    GenerateGeneralLedgerPendingEntriesEvent glEvent = new GenerateGeneralLedgerPendingEntriesEvent(preq, accountingLine, sequenceHelper);
                    success &= kualiRuleService.applyRules(glEvent);
                    sequenceHelper.increment(); // increment for the next line
                }

                //Manually save summary accounts
                savePaymentRequestSummaryAccounts(accountingLines, preq.getPurapDocumentIdentifier());
            }

            if (success) {
                //Manually save GL entries for Payment Request and encumbrances
                saveGLEntries(preq.getGeneralLedgerPendingEntries());
            }
        }
        return success;
    }

    /**
     * This is called in the Fiscal Officer route level of PREQ. It will adjust the accounts on the PREQ if the Fiscal Officer
     * changes them. It shouldn't generate any G/L entries if they don't change anything. It should only generate entries to move
     * the money from the old account(s) to the new account(s). 
     * 
     * !!IMPORTANT!! Note that this must be called before the preq is stored to the database, since this needs to know the old and new values
     * 
     * @param preq Preq check for G/L entries
     */
    public void generateEntriesModifyPaymentRequest(PaymentRequestDocument preq) {
        LOG.debug("generateEntriesModifyPreq(preq) started");
        generateEntriesModifyPaymentRequest(preq, false);
    }

    private void generateEntriesModifyPaymentRequest(PaymentRequestDocument preq, boolean oldPreqExcludeTaxItems) {
        LOG.debug("generateEntriesModifyPreq() started");

        Map actualsPositive = new HashMap();
        List<SourceAccountingLine> newAccountingLines = purapAccountingService.generateSummaryWithNoZeroTotals(preq.getItems());
        for (SourceAccountingLine newAccount : newAccountingLines) {
            actualsPositive.put(newAccount, newAccount.getAmount());
        }

        Map actualsNegative = new HashMap();
        List<PaymentRequestSummaryAccount> oldAccountingLines = getPaymentRequestSummaryAccounts(preq.getPurapDocumentIdentifier());

        for (PaymentRequestSummaryAccount oldAccount : oldAccountingLines) {
            actualsNegative.put(oldAccount.generateSourceAccountingLine(), oldAccount.getAmount());
        }

        // Add the positive entries and subtract the negative entries
        Map glEntries = new HashMap();

        // Combine the two maps (copy all the positive entries)
        LOG.debug("generateEntriesModifyPreq() Combine positive/negative entries");
        glEntries.putAll(actualsPositive);

        for (Iterator iter = actualsNegative.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine key = (SourceAccountingLine) iter.next();

            KualiDecimal amt;
            if (glEntries.containsKey(key)) {
                amt = (KualiDecimal) glEntries.get(key);
                amt = amt.subtract((KualiDecimal) actualsNegative.get(key));
            }
            else {
                amt = ZERO;
                amt = amt.subtract((KualiDecimal) actualsNegative.get(key));
            }
            glEntries.put(key, amt);
        }

        List<SourceAccountingLine> accounts = new ArrayList();
        for (Iterator iter = glEntries.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) glEntries.get(account);
            if (ZERO.compareTo(amount) != 0) {
                account.setAmount(amount);
                accounts.add(account);
            }
        }

        //save summary accounts
        savePaymentRequestSummaryAccounts(newAccountingLines, preq.getPurapDocumentIdentifier());
        
        LOG.debug("generateEntriesModifyPreq() Generate GL entries");
        preq.setSourceAccountingLines(accounts);
        preq.setGenerateEncumbranceEntries(false);
        preq.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
        generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(preq);
        saveGLEntries(preq.getGeneralLedgerPendingEntries());
    }

    public void generateEntriesCreateCreditMemo(CreditMemoDocument cm) {
        generateEntriesCreditMemo(cm, CREATE_CREDIT_MEMO);
    }
    
    private void generateEntriesCancelCreditMemo(CreditMemoDocument cm) {
        generateEntriesCreditMemo(cm, CANCEL_CREDIT_MEMO);
    }
    
    private boolean generateEntriesCreditMemo(CreditMemoDocument cm, boolean isCancel) {
        LOG.debug("generateEntriesCreditMemo() started");

        generalLedgerPendingEntryService.delete(cm.getDocumentNumber());

        boolean success = true;
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();

        PurchaseOrderDocument po = null;
        if (cm.isSourceDocumentPurchaseOrder()) {
            LOG.debug("generateEntriesCreditMemo() PO type");
            po = cm.getPurchaseOrderDocument();
        }
        else if (cm.isSourceDocumentPaymentRequest()) {
            LOG.debug("generateEntriesCreditMemo() PREQ type");
            po = cm.getPaymentRequestDocument().getPurchaseOrderDocument();
        }

        List encumbrances = getCreditMemoEncumbrance(cm, po, isCancel);
        if (encumbrances != null) {
            cm.setGenerateEncumbranceEntries(true);
          
            //even if generating encumbrance entries on cancel, call is the same because the method gets negative amounts from the map so Debits on negatives = a credit
            cm.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);

            for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) iter.next();
                if (accountingLine.getAmount().compareTo(ZERO) != 0) {
                    GenerateGeneralLedgerPendingEntriesEvent glEvent = new GenerateGeneralLedgerPendingEntriesEvent(cm, accountingLine, sequenceHelper);
                    success &= kualiRuleService.applyRules(glEvent);
                    sequenceHelper.increment(); // increment for the next line
                }
            }
        }

        // now book the actuals from the CM
        List<SourceAccountingLine> accountingLines = purapAccountingService.generateSummaryWithNoZeroTotals(cm.getItems());
        if (accountingLines != null) {
            cm.setGenerateEncumbranceEntries(false);

            if (!isCancel) {
                //on create, use CREDIT code
                cm.setDebitCreditCodeForGLEntries(GL_CREDIT_CODE);
            }
            else {
                //on cancel, use DEBIT code
                cm.setDebitCreditCodeForGLEntries(GL_DEBIT_CODE);
            }
            
            for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) iter.next();
                GenerateGeneralLedgerPendingEntriesEvent glEvent = new GenerateGeneralLedgerPendingEntriesEvent(cm, accountingLine, sequenceHelper);
                success &= kualiRuleService.applyRules(glEvent);
                sequenceHelper.increment(); // increment for the next line
            }
        }

        if (success) {
            saveGLEntries(cm.getGeneralLedgerPendingEntries());
        }

        LOG.debug("generateEntriesCreditMemo() ended");
        return success;
    }

    public void generateEntriesApproveAmendPurchaseOrder(PurchaseOrderDocument po) {
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
                    item.setItemOutstandingEncumberedAmount(item.getItemOutstandingEncumberedQuantity().multiply(new KualiDecimal(item.getItemUnitPrice())));
                }
                else {
                    if (item.getItemUnitPrice() != null) {
                        item.setItemOutstandingEncumberedAmount(new KualiDecimal(item.getItemUnitPrice().subtract(item.getItemInvoicedTotalAmount().bigDecimalValue())));
                    }
                }

                // TODO Deal with rounding
                for (Iterator iter = item.getSourceAccountingLines().iterator(); iter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
                    BigDecimal percent = new BigDecimal(account.getAccountLinePercent().toString());
                    percent = percent.divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP);
                    account.setItemAccountOutstandingEncumbranceAmount(item.getItemOutstandingEncumberedAmount().multiply(new KualiDecimal(percent)));
                    account.setAlternateAmountForGLEntryCreation(account.getItemAccountOutstandingEncumbranceAmount());
                }
            }
        }

        PurchaseOrderDocument oldPO = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(po.getPurapDocumentIdentifier());

        if (oldPO == null) {
            throw new IllegalArgumentException("Current Purchase Order not found - poId = " + oldPO.getPurapDocumentIdentifier());
        }

        List newAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly());
        List oldAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummaryWithNoZeroTotalsUsingAlternateAmount(oldPO.getItemsActiveOnlySetupAlternateAmount());

        Map combination = new HashMap();

        // Add amounts from the new PO
        for (Iterator iter = newAccounts.iterator(); iter.hasNext();) {
            SourceAccountingLine newAccount = (SourceAccountingLine) iter.next();
            combination.put(newAccount, newAccount.getAmount());
        }

        LOG.info("generateEntriesApproveAmendPo() combination after the add");
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = (SourceAccountingLine) iter.next();
            LOG.info("generateEntriesApproveAmendPo() " + element + " = " + ((KualiDecimal) combination.get(element)).floatValue());
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

        LOG.debug("generateEntriesApproveAmendPo() combination after the subtract");
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = (SourceAccountingLine) iter.next();
            LOG.info("generateEntriesApproveAmendPo() " + element + " = " + ((KualiDecimal) combination.get(element)).floatValue());
        }

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) combination.get(account);
            if (amount.doubleValue() != 0) {
                account.setAmount(amount);
                encumbranceAccounts.add(account);
            }
        }

        po.setSourceAccountingLines(encumbranceAccounts);
        generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
        LOG.debug("generateEntriesApproveAmendPo() gl entries created; exit method");

    }
    
    
    private List<SourceAccountingLine> relieveEncumbrance(PaymentRequestDocument preq) {
        LOG.debug("relieveEncumbrance() started");

        Map encumbranceAccountMap = new HashMap();
        PurchaseOrderDocument po = preq.getPurchaseOrderDocument();

        // Get each item one by one
        for (Iterator items = preq.getItems().iterator(); items.hasNext();) {
            PaymentRequestItem preqItem = (PaymentRequestItem) items.next();
            PurchaseOrderItem poItem = getPoItem(po, preqItem.getItemLineNumber());

            boolean takeAll = false; // Set this true if we relieve the entire encumbrance
            KualiDecimal itemDisEncumber = null; // Amount to disencumber for this item

            String logItmNbr = "Item # " + preqItem.getItemLineNumber();
            LOG.debug("relieveEncumbrance() " + logItmNbr);

            // If there isn't a PO item or the extended price is 0, we don't need encumbrances
            if (poItem == null) {
                LOG.debug("relieveEncumbrance() " + logItmNbr + " No encumbrances required because po item is null");
            }
            else if (ZERO.compareTo(preqItem.getExtendedPrice()) == 0) {
                /*
                 * This is a specialized case where PREQ item being processed must adjust the PO item's outstanding encumbered
                 * quantity. This kind of scenario is mostly seen on warranty type items. The following must be true to do this:
                 * 
                 * PREQ item Extended Price must be ZERO 
                 * PREQ item invoice quantity must be not empty and not ZERO 
                 * PO item is quantity based 
                 * PO item unit cost is ZERO
                 */
                LOG.debug("relieveEncumbrance() " + logItmNbr + " No GL encumbrances required because extended price is ZERO");
                if ((poItem.getItemQuantity() != null) && ((BigDecimal.ZERO.compareTo(poItem.getItemUnitPrice())) == 0)) {
                    // po has order quantity and unit price is ZERO... reduce outstanding encumbered quantity
                    LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate po oustanding encumbrance");

                    // Do encumbrance calculations based on quantity
                    if ((preqItem.getItemQuantity() != null) && ((ZERO.compareTo(preqItem.getItemQuantity())) != 0)) {
                        KualiDecimal invoiceQuantity = preqItem.getItemQuantity();
                        KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();

                        KualiDecimal encumbranceQuantity;
                        if (invoiceQuantity.compareTo(outstandingEncumberedQuantity) > 0) {
                            // We bought more than the quantity on the PO
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " we bought more than the qty on the PO");
                            encumbranceQuantity = outstandingEncumberedQuantity;
                            poItem.setItemOutstandingEncumberedQuantity(ZERO);
                        }
                        else {
                            encumbranceQuantity = invoiceQuantity;
                            poItem.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity.subtract(encumbranceQuantity));
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " adjusting oustanding encunbrance qty - encumbranceQty " + encumbranceQuantity + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity());
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
                LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");

                // Do we calculate the encumbrance amount based on quantity or amount?
                if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");

                    // Do encumbrance calculations based on quantity
                    KualiDecimal invoiceQuantity = preqItem.getItemQuantity() == null ? ZERO : preqItem.getItemQuantity();
                    KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();

                    KualiDecimal encumbranceQuantity;
                    if (invoiceQuantity.compareTo(outstandingEncumberedQuantity) > 0) {
                        // We bought more than the quantity on the PO
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " we bought more than the qty on the PO");
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
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " encumbranceQty " + encumbranceQuantity + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity());
                    }

                    if (poItem.getItemInvoicedTotalQuantity() == null) {
                        poItem.setItemInvoicedTotalQuantity(invoiceQuantity);
                    }
                    else {
                        poItem.setItemInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity().add(invoiceQuantity));
                    }

                    itemDisEncumber = encumbranceQuantity.multiply(new KualiDecimal(poItem.getItemUnitPrice()));
                }
                else {
                    LOG.debug("relieveEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");

                    // Do encumbrance calculations based on amount only
                    if ((poItem.getItemOutstandingEncumberedAmount().bigDecimalValue().signum() == -1) && (preqItem.getExtendedPrice().bigDecimalValue().signum() == -1)) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is negative: " + poItem.getItemOutstandingEncumberedAmount());
                        if (preqItem.getExtendedPrice().compareTo(poItem.getItemOutstandingEncumberedAmount()) >= 0) {
                            // extended price is equal to or greater than outstanding encumbered
                            itemDisEncumber = preqItem.getExtendedPrice();
                        }
                        else {
                            // extended price is less than outstanding encumbered
                            takeAll = true;
                            itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                        }
                    }
                    else {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is positive or ZERO: " + poItem.getItemOutstandingEncumberedAmount());
                        if (poItem.getItemOutstandingEncumberedAmount().compareTo(preqItem.getExtendedPrice()) >= 0) {
                            // outstanding amount is equal to or greater than extended price
                            itemDisEncumber = preqItem.getExtendedPrice();
                        }
                        else {
                            // outstanding amount is less than extended price
                            takeAll = true;
                            itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                        }
                    }
                }

                LOG.debug("relieveEncumbrance() " + logItmNbr + " Amount to disencumber: " + itemDisEncumber);

                KualiDecimal newOutstandingEncumberedAmount = poItem.getItemOutstandingEncumberedAmount().subtract(itemDisEncumber);
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New Outstanding Encumbered amount is : " + newOutstandingEncumberedAmount);
                poItem.setItemOutstandingEncumberedAmount(newOutstandingEncumberedAmount);

                KualiDecimal newInvoicedTotalAmount = poItem.getItemInvoicedTotalAmount().add(preqItem.getExtendedPrice());
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New Invoiced Total Amount is: " + newInvoicedTotalAmount);
                poItem.setItemInvoicedTotalAmount(newInvoicedTotalAmount);

                // Sort accounts
                Collections.sort((List) poItem.getSourceAccountingLines());

                // make the list of accounts for the disencumbrance entry
                PurchaseOrderAccount lastAccount = null;
                KualiDecimal accountTotal = ZERO;
//                Collections.sort((List) poItem.getSourceAccountingLines());
                for (Iterator accountIter = poItem.getSourceAccountingLines().iterator(); accountIter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) accountIter.next();
                    if (!account.isEmpty()) {
                        KualiDecimal encumbranceAmount = null;
                        SourceAccountingLine acctString = account.generateSourceAccountingLine();
                        if (takeAll) {
                            // fully paid; remove remaining encumbrance
                            encumbranceAmount = account.getItemAccountOutstandingEncumbranceAmount();
                            account.setItemAccountOutstandingEncumbranceAmount(ZERO);
                            LOG.debug("relieveEncumbrance() " + logItmNbr + " take all");
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
                        
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " " + acctString + " = " + encumbranceAmount);
                        if (ObjectUtils.isNull(encumbranceAccountMap.get(acctString))) {
                            encumbranceAccountMap.put(acctString, encumbranceAmount);
                        }
                        else {
                            KualiDecimal amt = (KualiDecimal)encumbranceAccountMap.get(acctString);
                            encumbranceAccountMap.put(acctString, amt.add(encumbranceAmount));
                        }
                        
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemDisEncumber.subtract(accountTotal);
                    LOG.debug("relieveEncumbrance() difference: " + logItmNbr + " " + difference);
                    
                    SourceAccountingLine acctString = lastAccount.generateSourceAccountingLine();
                    KualiDecimal amount = (KualiDecimal)encumbranceAccountMap.get(acctString);
                    if (ObjectUtils.isNull(amount)) {
                        encumbranceAccountMap.put(acctString, difference);
                    }
                    else {
                        encumbranceAccountMap.put(acctString, amount.add(difference));
                    }

                    lastAccount.setItemAccountOutstandingEncumbranceAmount(lastAccount.getItemAccountOutstandingEncumbranceAmount().subtract(difference));
                }
            }
        }//endfor
        
        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = encumbranceAccountMap.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine acctString = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
            if (amount.doubleValue() != 0) {
                acctString.setAmount(amount);
                encumbranceAccounts.add(acctString);
            }
        }

        //FIXME should this use the save in PurhcaseOrderService? (hjs)
        businessObjectService.save(po);
        
        return encumbranceAccounts;
    }

    /**
     * Re-encumber the Encumbrance on a PO based on values in a PREQ.
     * 
     * Note:  This modifies the encumbrance values on the PO and saves the PO
     * 
     * This is used when a PREQ is cancelled.
     * 
     * @param po Purchase Order
     * @param preq PREQ for invoice
     * @return Map of GlAccountingString/BigDecimal for amounts to re-encumber the encumbrance
     */
    private List reencumberEncumbrance(PaymentRequestDocument preq) {
        LOG.debug("reencumberEncumbrance() started");

        PurchaseOrderDocument po = preq.getPurchaseOrderDocument();
        Map encumbranceAccountMap = new HashMap();

        // Get each item one by one
        for (Iterator items = preq.getItems().iterator(); items.hasNext();) {
            PaymentRequestItem payRequestItem = (PaymentRequestItem) items.next();
            PurchaseOrderItem poItem = getPoItem(po, payRequestItem.getItemLineNumber());

            KualiDecimal itemReEncumber = null; // Amount to reencumber for this item

            String logItmNbr = "Item # " + payRequestItem.getItemLineNumber();
            LOG.debug("reencumberEncumbrance() " + logItmNbr);

            // If there isn't a PO item or the extended price is 0, we don't need encumbrances
            if ((poItem == null) || (payRequestItem.getExtendedPrice().doubleValue() == 0)) {
                LOG.debug("reencumberEncumbrance() " + logItmNbr + " No encumbrances required");
            }
            else {
                LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");

                // Do we calculate the encumbrance amount based on quantity or amount?
                if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");

                    // Do disencumbrance calculations based on quantity
                    KualiDecimal preqQuantity = payRequestItem.getItemQuantity() == null ? ZERO : payRequestItem.getItemQuantity();
                    KualiDecimal outstandingEncumberedQuantity = poItem.getItemOutstandingEncumberedQuantity() == null ? ZERO : poItem.getItemOutstandingEncumberedQuantity();
                    KualiDecimal invoicedTotal = poItem.getItemInvoicedTotalQuantity() == null ? ZERO : poItem.getItemInvoicedTotalQuantity();

                    poItem.setItemInvoicedTotalQuantity(invoicedTotal.subtract(preqQuantity));
                    poItem.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity.add(preqQuantity));

                    itemReEncumber = preqQuantity.multiply(new KualiDecimal(poItem.getItemUnitPrice()));
                }
                else {
                    LOG.debug("reencumberEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");

                    itemReEncumber = payRequestItem.getExtendedPrice();
                    // if re-encumber amount is more than original PO ordered amount... do not exceed ordered amount
                    // this prevents negative encumbrance
                    if ((poItem.getExtendedPrice() != null) && (poItem.getExtendedPrice().bigDecimalValue().signum() < 0)) {
                        // po item extended cost is negative
                        if ((poItem.getExtendedPrice().compareTo(itemReEncumber)) > 0) {
                            itemReEncumber = poItem.getExtendedPrice();
                        }
                    }
                    else if ((poItem.getExtendedPrice() != null) && (poItem.getExtendedPrice().bigDecimalValue().signum() >= 0)) {
                        // po item extended cost is positive
                        if ((poItem.getExtendedPrice().compareTo(itemReEncumber)) < 0) {
                            itemReEncumber = poItem.getExtendedPrice();
                        }
                    }
                }

                LOG.debug("reencumberEncumbrance() " + logItmNbr + " Amount to reencumber: " + itemReEncumber);

                KualiDecimal outstandingEncumberedAmount = poItem.getItemOutstandingEncumberedAmount() == null ? ZERO : poItem.getItemOutstandingEncumberedAmount();
                LOG.debug("relieveEncumbrance() " + logItmNbr + " PO Item Outstanding Encumbrance Amount set to: " + outstandingEncumberedAmount);
                KualiDecimal newOutstandingEncumberedAmount = outstandingEncumberedAmount.add(itemReEncumber);
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New PO Item Outstanding Encumbrance Amount to set: " + newOutstandingEncumberedAmount);
                poItem.setItemOutstandingEncumberedAmount(newOutstandingEncumberedAmount);

                KualiDecimal invoicedTotalAmount = poItem.getItemInvoicedTotalAmount() == null ? ZERO : poItem.getItemInvoicedTotalAmount();
                LOG.debug("relieveEncumbrance() " + logItmNbr + " PO Item Invoiced Total Amount set to: " + invoicedTotalAmount);
                KualiDecimal newInvoicedTotalAmount = invoicedTotalAmount.subtract(payRequestItem.getExtendedPrice());
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New PO Item Invoiced Total Amount to set: " + newInvoicedTotalAmount);
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

                        LOG.debug("reencumberEncumbrance() " + logItmNbr + " " + acctString + " = " + reencumbranceAmount);
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
                    LOG.debug("reencumberEncumbrance() difference: " + logItmNbr + " " + difference);

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

        //FIXME should this use the save in PurhcaseOrderService? (hjs)
        businessObjectService.save(po);

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
        for (Iterator iter = encumbranceAccountMap.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine acctString = (SourceAccountingLine) iter.next();
            KualiDecimal amount = (KualiDecimal) encumbranceAccountMap.get(acctString);
            if (amount.doubleValue() != 0) {
                acctString.setAmount(amount);
                encumbranceAccounts.add(acctString);
            }
        }

        return encumbranceAccounts;
    }

    
    private List<SourceAccountingLine> getCreditMemoEncumbrance(CreditMemoDocument cm, PurchaseOrderDocument po, boolean cancel) {
        LOG.debug("getCreditMemoEncumbrance() started");
        
        if(ObjectUtils.isNull(po)) {
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
            PurchaseOrderItem poItem = getPoItem(po, cmItem.getItemLineNumber());

            KualiDecimal itemDisEncumber = null; // Amount to disencumber for this item

            String logItmNbr = "Item # " + cmItem.getItemLineNumber();
            LOG.debug("getCreditMemoEncumbrance() " + logItmNbr);

            // If there isn't a PO item or the extended price is 0, we don't need encumbrances
            if ((poItem == null) || (cmItem.getExtendedPrice() == null) || (cmItem.getExtendedPrice().doubleValue() == 0)) {
                LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " No encumbrances required");
            }
            else {
                LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance GL entries");

                // Do we calculate the encumbrance amount based on quantity or amount?
                if (poItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance based on quantity");

                    // Do encumbrance calculations based on quantity
                    KualiDecimal cmQuantity = cmItem.getItemQuantity() == null ? ZERO : cmItem.getItemQuantity();

                    KualiDecimal encumbranceQuantityChange = calculateQuantityChange(cancel, poItem, cmQuantity);

                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " encumbranceQtyChange " + encumbranceQuantityChange + " outstandingEncumberedQty " + poItem.getItemOutstandingEncumberedQuantity() + " invoicedTotalQuantity " + poItem.getItemInvoicedTotalQuantity());

                    itemDisEncumber = encumbranceQuantityChange.multiply(new KualiDecimal(poItem.getItemUnitPrice()));
                }
                else {
                    LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Calculate encumbrance based on amount");

                    // Do encumbrance calculations based on amount only
                    if (cancel) {
                        // Decrease encumbrance
                        itemDisEncumber = cmItem.getExtendedPrice().multiply(new KualiDecimal("-1"));

                        if (poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber).doubleValue() < 0) {
                            LOG.debug("getCreditMemoEncumbrance() Cancel overflow");

                            itemDisEncumber = poItem.getItemOutstandingEncumberedAmount();
                        }
                    }
                    else {
                        // Increase encumbrance
                        itemDisEncumber = cmItem.getExtendedPrice();

                        if (poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber).doubleValue() > poItem.getExtendedPrice().doubleValue()) {
                            LOG.debug("getCreditMemoEncumbrance() Create overflow");

                            itemDisEncumber = poItem.getExtendedPrice().subtract(poItem.getItemOutstandingEncumberedAmount());
                        }
                    }
                }

                poItem.setItemOutstandingEncumberedAmount(poItem.getItemOutstandingEncumberedAmount().add(itemDisEncumber));
                poItem.setItemInvoicedTotalAmount(poItem.getItemInvoicedTotalAmount().subtract(itemDisEncumber));

                LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " Amount to disencumber: " + itemDisEncumber);

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

                        LOG.debug("getCreditMemoEncumbrance() " + logItmNbr + " " + acctString + " = " + encumbranceAmount);

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
                    LOG.debug("getCreditMemoEncumbrance() difference: " + logItmNbr + " " + difference);

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

        //TODO should this use the save in PurhcaseOrderService? (hjs)
        businessObjectService.save(po);
        
        return encumbranceAccounts;
    }

    private PurchaseOrderItem getPoItem(PurchaseOrderDocument po, Integer nbr) {
        for (Iterator iter = po.getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem element = (PurchaseOrderItem) iter.next();
            //FIXME don't think these #s should ever be null; is that right? (hjs)
            if (ObjectUtils.isNotNull(nbr) && ObjectUtils.isNotNull(element.getItemLineNumber()) && 
                    (nbr.compareTo(element.getItemLineNumber()) == 0)) {
                return element;
            }
        }
        return null;
    }

    
    //TODO (hjs) this could probably be done in a more generic way with a better method name, but this works for now
    private String entryDescription(String description) {
        if (description != null && description.length() > 40) {
            return description.toString().substring(0, 39);
        }
        else {
            return description;
        }
    }

    private KualiDecimal calculateQuantityChange(boolean cancel, PurchaseOrderItem poItem, KualiDecimal cmQuantity) {
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
                LOG.debug("creditMemoEncumbrance() Cancel overflow");
                KualiDecimal difference = poItem.getItemOutstandingEncumberedQuantity().abs();
                poItem.setItemOutstandingEncumberedQuantity(ZERO);
                poItem.setItemInvoicedTotalQuantity(poItem.getItemQuantity());
                encumbranceQuantityChange = encumbranceQuantityChange.add(difference);
            }
        }
        else {
            if (poItem.getItemInvoicedTotalQuantity().doubleValue() < 0) {
                LOG.debug("creditMemoEncumbrance() Create overflow");
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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;    
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }
}
