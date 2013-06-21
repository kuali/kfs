/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.Serializable;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PaymentApplicationInvoiceDetailApply implements Serializable {

    private static final int DEFAULT_PAID_APPLIED_ITEM_NUMBER = 0;

    private CustomerInvoiceDetail invoiceDetail;
    private InvoicePaidApplied paidApplied;

    private KualiDecimal amountApplied;
    private KualiDecimal amountAppliedOldValue;
    private boolean fullApplyInd;
    private boolean fullApplyOldValueInd;
    private boolean invoiceQuickAppliedInd;
    private String payAppDocNumber;

    public PaymentApplicationInvoiceDetailApply(String payAppDocNumber, CustomerInvoiceDetail invoiceDetail) {
        this.invoiceDetail = invoiceDetail;
        this.amountApplied = KualiDecimal.ZERO;
        this.amountAppliedOldValue = KualiDecimal.ZERO;
        this.fullApplyInd = false;
        this.fullApplyOldValueInd = false;
        this.paidApplied = new InvoicePaidApplied();
        this.payAppDocNumber = payAppDocNumber;
        this.invoiceQuickAppliedInd = false;
    }

    public InvoicePaidApplied generatePaidApplied() {
        InvoicePaidApplied paidApplied = new InvoicePaidApplied(payAppDocNumber, invoiceDetail.getDocumentNumber(),
                invoiceDetail.getSequenceNumber(), amountApplied, DEFAULT_PAID_APPLIED_ITEM_NUMBER);
        return paidApplied;
    }

    public KualiDecimal getAmountOpen() {
        return invoiceDetail.getAmountOpen();
    }

    public KualiDecimal getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(KualiDecimal amountApplied) {
        this.amountAppliedOldValue = this.amountApplied;
        if (amountApplied == null) {
            this.amountApplied = KualiDecimal.ZERO;
        }
        else {
            this.amountApplied = amountApplied;
        }
    }

    public boolean isAmountAppliedChanged() {
        return !amountApplied.equals(amountAppliedOldValue);
    }

    public boolean isFullApplyInd() {
        return fullApplyInd;
    }

    public void setFullApplyInd(boolean fullApplyInd) {
        this.fullApplyOldValueInd = this.fullApplyInd;
        this.fullApplyInd = fullApplyInd;
    }

    public boolean isFullApplyIndChanged() {
        return fullApplyInd != fullApplyOldValueInd;
    }

    public CustomerInvoiceDetail getInvoiceDetail() {
        return invoiceDetail;
    }

    public String getInvoiceDocumentNumber() {
        return invoiceDetail.getDocumentNumber();
    }

    public Integer getSequenceNumber() {
        return invoiceDetail.getSequenceNumber();
    }

    public String getChartOfAccountsCode() {
        return invoiceDetail.getChartOfAccountsCode();
    }

    public String getAccountNumber() {
        return invoiceDetail.getAccountNumber();
    }

    public String getInvoiceItemDescription() {
        return invoiceDetail.getInvoiceItemDescription();
    }

    public KualiDecimal getAmount() {
        return invoiceDetail.getAmount();
    }

    public boolean isInvoiceQuickAppliedInd() {
        return invoiceQuickAppliedInd;
    }

    public void setInvoiceQuickAppliedInd(boolean invoiceQuickAppliedInd) {
        this.invoiceQuickAppliedInd = invoiceQuickAppliedInd;
    }

}
