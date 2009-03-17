/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.Serializable;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationInvoiceDetailApply implements Serializable {

    private CustomerInvoiceDetail invoiceDetail;
    
    private KualiDecimal amountApplied;
    private boolean fullApply;
    
    public PaymentApplicationInvoiceDetailApply(CustomerInvoiceDetail invoiceDetail) {
        this.invoiceDetail = invoiceDetail;
        this.amountApplied = KualiDecimal.ZERO;
        this.fullApply = false;
    }

    public InvoicePaidApplied generatePaidApplied() {
        //TODO finish this
        return null;
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
    
    public KualiDecimal getAmountOpen() {
        return invoiceDetail.getAmountOpen().subtract(amountApplied);
    }
    
    public KualiDecimal getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(KualiDecimal amountApplied) {
        this.amountApplied = amountApplied;
    }

    public boolean isFullApply() {
        return fullApply;
    }

    public void setFullApply(boolean applyFullAmount) {
        this.fullApply = applyFullAmount;
    }

}
