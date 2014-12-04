/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    private boolean fullApply;
    private boolean fullApplyOldValue;
    private boolean invoiceQuickApplied;
    private String payAppDocNumber;
    
    public PaymentApplicationInvoiceDetailApply(String payAppDocNumber, CustomerInvoiceDetail invoiceDetail) {
        this.invoiceDetail = invoiceDetail;
        this.amountApplied = KualiDecimal.ZERO;
        this.amountAppliedOldValue = KualiDecimal.ZERO;
        this.fullApply = false;
        this.fullApplyOldValue = false;
        this.paidApplied = new InvoicePaidApplied();
        this.payAppDocNumber = payAppDocNumber;
        this.invoiceQuickApplied = false;
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
    
    public boolean isFullApply() {
        return fullApply;
    }

    public void setFullApply(boolean fullApply) {
        this.fullApplyOldValue = this.fullApply;
        this.fullApply = fullApply;
    }

    public boolean isFullApplyChanged() {
        return fullApply != fullApplyOldValue;
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

    public boolean isInvoiceQuickApplied() {
        return invoiceQuickApplied;
    }

    public void setInvoiceQuickApplied(boolean invoiceQuickApplied) {
        this.invoiceQuickApplied = invoiceQuickApplied;
    }
    
}
