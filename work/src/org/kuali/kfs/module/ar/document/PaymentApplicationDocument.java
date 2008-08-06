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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase {

    private List<InvoicePaidApplied> appliedPayments;
    private Collection<NonInvoiced> nonInvoicedPayments;
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

    public Collection<NonInvoiced> getNonInvoicedPayments() {
        return nonInvoicedPayments;
    }

    public void setNonInvoicedPayments(Collection<NonInvoiced> nonInvoicedPayments) {
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
}
