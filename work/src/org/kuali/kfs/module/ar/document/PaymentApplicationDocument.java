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
package org.kuali.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.InvoicePaidApplied;
import org.kuali.module.ar.bo.NonAppliedDistribution;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.bo.NonInvoiced;
import org.kuali.module.ar.bo.NonInvoicedDistribution;

public class PaymentApplicationDocument extends AccountingDocumentBase {

    private Collection<InvoicePaidApplied> appliedPayments;
    private Collection<NonInvoiced> nonInvoicedPayments;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private Collection<NonAppliedHolding> nonAppliedHoldings;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    
    public PaymentApplicationDocument() {
        super();
        this.appliedPayments = new ArrayList<InvoicePaidApplied>();
        this.nonInvoicedPayments = new ArrayList<NonInvoiced>();
        this.nonInvoicedDistributions = new ArrayList<NonInvoicedDistribution>();
        this.nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
        this.nonAppliedHoldings = new ArrayList<NonAppliedHolding>();
    }

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPendingEntrySourceDetail) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection<InvoicePaidApplied> getAppliedPayments() {
        return appliedPayments;
    }

    public void setAppliedPayments(Collection<InvoicePaidApplied> appliedPayments) {
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

    public Collection<NonAppliedHolding> getNonAppliedHoldings() {
        return nonAppliedHoldings;
    }

    public void setNonAppliedHoldings(Collection<NonAppliedHolding> nonAppliedHoldings) {
        this.nonAppliedHoldings = nonAppliedHoldings;
    }

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }
    
}
