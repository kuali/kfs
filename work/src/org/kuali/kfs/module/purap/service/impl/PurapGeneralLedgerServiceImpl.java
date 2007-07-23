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

import java.util.ArrayList;
import java.util.Calendar;
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
import org.kuali.kfs.rule.event.GenerateGeneralLedgerPendingEntriesEvent;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurapGeneralLedgerPendingEntry;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;

public class PurapGeneralLedgerServiceImpl implements PurapGeneralLedgerService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapGeneralLedgerServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;
    private PurapAccountingService purapAccountingService;
    private UniversityDateService universityDateService;

    public void saveGLEntries(List<GeneralLedgerPendingEntry> glEntries) {
        businessObjectService.save(glEntries);
        
        List purapGLEntries = new ArrayList();
        for (GeneralLedgerPendingEntry entry : glEntries) {
            purapGLEntries.add(new PurapGeneralLedgerPendingEntry(entry));
        }

        businessObjectService.save(purapGLEntries);
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
        ObjectCode objectCode = SpringServiceLocator.getObjectCodeService().getByPrimaryId(explicitEntry.getUniversityFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialObjectCode());
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
                // TODO PHASE 2b - once alternate payee functionality is added, name might be stored in preq insted of having to go to PO
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

    public void generateEntriesCreatePreq(PaymentRequestDocument preq) {

        //FIXME move this code
        if (preq.getPurapDocumentIdentifier() != null) {
            
        //FIXME need to code PaymentChange
//        PaymentChange paymentChange = new PaymentChange();
//        paymentChange.setPaymentRequest(preq);
//        paymentChange.setCreditMemo(null);
//        paymentChange.setLastUpdateTimestamp(now);
//        paymentChange.setLastUpdateUserId(preq.getLastUpdateUserId());
//set PC in doc to be used in GL creation
        
            generalLedgerPendingEntryService.delete(preq.getDocumentNumber());
            
            boolean success = true;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            
            //relieve encumbrances on PO
            List encumbrances = relieveEncumbrance(preq);
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
                for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
                    AccountingLine accountingLine = (AccountingLine) iter.next();
                    GenerateGeneralLedgerPendingEntriesEvent glEvent = new GenerateGeneralLedgerPendingEntriesEvent(preq, accountingLine, sequenceHelper);
                    success &= kualiRuleService.applyRules(glEvent);
                    sequenceHelper.increment(); // increment for the next line
                }
            }

            if (!success) {
                //TODO blowup
            }

            saveGLEntries(preq.getGeneralLedgerPendingEntries());
            
            //save PaymentChange now that it has its accounts

        }
    }

    public void generateEntriesModifyPreq(PaymentRequestDocument preq) {

    }

    private List<SourceAccountingLine> relieveEncumbrance(PaymentRequestDocument preq) {
        LOG.debug("relieveEncumbrance() started");

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList();
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
                 * PREQ item Extended Price must be ZERO PREQ item invoice quantity must be not empty and not ZERO PO item is
                 * quantity based PO item unit cost is ZERO
                 */
                LOG.debug("relieveEncumbrance() " + logItmNbr + " No GL encumbrances required because extended price is ZERO");
                if ((poItem.getItemQuantity() != null) && ((ZERO.compareTo(poItem.getItemUnitPrice())) == 0)) {
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
                    if ((poItem.getItemOutstandingEncumbranceAmount().bigDecimalValue().signum() == -1) && (preqItem.getExtendedPrice().bigDecimalValue().signum() == -1)) {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is negative: " + poItem.getItemOutstandingEncumbranceAmount());
                        if (preqItem.getExtendedPrice().compareTo(poItem.getItemOutstandingEncumbranceAmount()) >= 0) {
                            // extended price is equal to or greater than outstanding encumbered
                            itemDisEncumber = preqItem.getExtendedPrice();
                        }
                        else {
                            // extended price is less than outstanding encumbered
                            takeAll = true;
                            itemDisEncumber = poItem.getItemOutstandingEncumbranceAmount();
                        }
                    }
                    else {
                        LOG.debug("relieveEncumbrance() " + logItmNbr + " Outstanding Encumbered amount is positive or ZERO: " + poItem.getItemOutstandingEncumbranceAmount());
                        if (poItem.getItemOutstandingEncumbranceAmount().compareTo(preqItem.getExtendedPrice()) >= 0) {
                            // outstanding amount is equal to or greater than extended price
                            itemDisEncumber = preqItem.getExtendedPrice();
                        }
                        else {
                            // outstanding amount is less than extended price
                            takeAll = true;
                            itemDisEncumber = poItem.getItemOutstandingEncumbranceAmount();
                        }
                    }
                }

                LOG.debug("relieveEncumbrance() " + logItmNbr + " Amount to disencumber: " + itemDisEncumber);

                KualiDecimal newOutstandingEncumberedAmount = poItem.getItemOutstandingEncumbranceAmount().subtract(itemDisEncumber);
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New Outstanding Encumbered amount is : " + newOutstandingEncumberedAmount);
                poItem.setItemOutstandingEncumbranceAmount(newOutstandingEncumberedAmount);

                KualiDecimal newInvoicedTotalAmount = poItem.getItemInvoicedTotalAmount().add(preqItem.getExtendedPrice());
                LOG.debug("relieveEncumbrance() " + logItmNbr + " New Invoiced Total Amount is: " + newInvoicedTotalAmount);
                poItem.setItemInvoicedTotalAmount(newInvoicedTotalAmount);

                // make the list of accounts for the disencumbrance entry
                // Sort accounts
                //FIXME none of the sorting of accounts work
//                Collections.sort((List) poItem.getSourceAccountingLines());

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

    
    // (hjs) this could probably be done in a more generic way with a better method name, but this works for now
    private String entryDescription(String description) {
        if (description != null && description.length() > 40) {
            return description.toString().substring(0, 39);
        }
        else {
            return description;
        }
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
}
