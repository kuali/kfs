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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {

    private List<InvoicePaidApplied> appliedPayments;
    private List<NonInvoiced> nonInvoicedPayments;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private transient PaymentApplicationDocumentService paymentApplicationDocumentService;

    public PaymentApplicationDocument() {
        super();
        this.appliedPayments = new ArrayList<InvoicePaidApplied>();
        this.nonInvoicedPayments = new ArrayList<NonInvoiced>();
        this.nonInvoicedDistributions = new ArrayList<NonInvoicedDistribution>();
        this.nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
        this.paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
    }

    public KualiDecimal getTotalUnappliedFunds() {
        return paymentApplicationDocumentService.getTotalUnappliedFundsForPaymentApplicationDocument(this);
    }

    public KualiDecimal getTotalUnappliedFundsToBeApplied() {
        return paymentApplicationDocumentService.getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(this);
    }

    public KualiDecimal getTotalToBeApplied() {
        return getDocumentHeader().getFinancialDocumentTotalAmount().subtract(getTotalAppliedAmount());
    }

    public KualiDecimal getTotalAppliedAmount() {
        return paymentApplicationDocumentService.getTotalAppliedAmountForPaymentApplicationDocument(this);
    }

    public List<InvoicePaidApplied> getAppliedPayments() {
        return appliedPayments;
    }

    public void setAppliedPayments(List<InvoicePaidApplied> appliedPayments) {
        this.appliedPayments = appliedPayments;
    }

    // /**
    // * This method returns non invoiced payments sorted by financial document line number.
    // *
    // * @return
    // */
    // public List<NonInvoiced> getNonInvoicedPayments() {
    // Collections.sort((List<NonInvoiced>)nonInvoicedPayments,new Comparator() {
    // public int compare(Object o1, Object o2) {
    // NonInvoiced ni1 = (NonInvoiced)o1;
    // NonInvoiced ni2 = (NonInvoiced)o2;
    //                
    // return ni1.getFinancialDocumentLineNumber().compareTo(ni2.getFinancialDocumentLineNumber());
    // }
    // });
    // return nonInvoicedPayments;
    // }

    public void setNonInvoicedPayments(List<NonInvoiced> nonInvoicedPayments) {
        this.nonInvoicedPayments = nonInvoicedPayments;
    }

    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    public void setNonAppliedDistributions(Collection<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    public NonAppliedHolding getNonAppliedHolding() {
        return nonAppliedHolding;
    }

    public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
        this.nonAppliedHolding = nonAppliedHolding;
    }

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    /**
     * This method retrieves a specific applied payment from the list, by array index
     * 
     * @param index the index of the applied payment to retrieve
     * @return an InvoicePaidApplied
     */
    public InvoicePaidApplied getAppliedPayment(int index) {
        if (index >= appliedPayments.size()) {
            for (int i = appliedPayments.size(); i <= index; i++) {
                appliedPayments.add(new InvoicePaidApplied());
            }
        }
        return (InvoicePaidApplied) appliedPayments.get(index);
    }

    /**
     * This method retrieves a specific non invoiced payment from the list, by array index
     * 
     * @param index the index of the non invoiced payment to retrieve
     * @return an NonInvoiced
     */
    public NonInvoiced getNonInvoicedPayment(int index) {
        if (index >= nonInvoicedPayments.size()) {
            for (int i = nonInvoicedPayments.size(); i <= index; i++) {
                nonInvoicedPayments.add(new NonInvoiced());
            }
        }
        return (NonInvoiced) nonInvoicedPayments.get(index);
    }

    public List<NonInvoiced> getNonInvoicedPayments() {
        return nonInvoicedPayments;
    }

    private boolean isForLockbox() {
        // TODO
        return false;
    }
    
    private boolean isForCreditCard() {
        // TODO
        return false;
    }
    
    private boolean isForWireTransfer() {
        // TODO
        return false;
    }
    
    /**
     * @param sequenceHelper
     * @return
     */
    private List<GeneralLedgerPendingEntry> createPendingEntriesForLockboxDocument(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        String processingOrganizationClearingAccountNumber = null;
        String billingOrganizationAccountNumber = null;
        String cashObjectCode = null;
        String accountsReceivableObjectCode = null;
        String unappliedCashObjectCode = null;
        String accountsReceivableRefundsDueObjectCode = null;
        
        // TODO Fill in code to set all of the object codes and accounts numbers needed.
        // TODO Fill in business logic.
        
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        
        return entries;
    }

    /**
     * @param sequenceHelper
     * @return
     */
    private List<GeneralLedgerPendingEntry> createPendingEntriesForCreditCardDocument(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        String processingOrganizationClearingAccountNumber = null;
        String billingOrganizationAccountNumber = null;
        String cashObjectCode = null;
        String accountsReceivableObjectCode = null;
        String unappliedCashObjectCode = null;
        String creditCardChargesObjectCode = null;
        
        // TODO Fill in code to set all of the object codes and accounts numbers needed.
        // TODO Fill in business logic.
        
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        return entries;
    }
    
    /**
     * @param sequenceHelper
     * @return
     */
    private List<GeneralLedgerPendingEntry> createPendingEntriesForWireTransferDocument(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        
        String eftClearingAccountNumber = null;
        String processingOrganizationClearingAccountNumber = null;
        String billingOrganizationAccountNumber = null;
        String cashObjectCode = null;
        String accountsReceivableObjectCode = null;
        String unappliedCashObjectCode = null;
        String incomeObjectCode = null;
        
        // TODO Fill in code to set all of the object codes and accounts numbers needed.
        // TODO Fill in business logic.
        
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        return entries;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        
        String billingOrganizationAccountNumber = null;
        String accountReceivableObjectCode = null;
        String incomeObjectCode = null;
        String cashObjectCode = null;
        
        try {
            List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
            if(isForLockbox()) {
                entries = createPendingEntriesForLockboxDocument(sequenceHelper);
            } else if(isForCreditCard()) {
                entries = createPendingEntriesForCreditCardDocument(sequenceHelper);
            } else if (isForWireTransfer()) {
                entries = createPendingEntriesForWireTransferDocument(sequenceHelper);
            }
        } catch(Throwable t) {
            return false;
        }
        
        return true;
    }

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        // TODO Auto-generated method stub
        return false;
    }

    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }

    
}
