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
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PaymentApplicationInvoiceApply implements Serializable {

    private CustomerInvoiceDocument invoice;
    private List<PaymentApplicationInvoiceDetailApply> detailApplications;
    
    private String payAppDocNumber;
    private boolean quickApply;
    private boolean quickApplyOldValue;
    
    public PaymentApplicationInvoiceApply(String payAppDocNumber, CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
        this.detailApplications = new ArrayList<PaymentApplicationInvoiceDetailApply>();
        for (CustomerInvoiceDetail invoiceDetail : invoice.getCustomerInvoiceDetailsWithoutDiscounts()) {
            this.detailApplications.add(new PaymentApplicationInvoiceDetailApply(payAppDocNumber, invoiceDetail));
        }
        this.quickApply = false;
        this.quickApplyOldValue = false;
        this.payAppDocNumber = payAppDocNumber;
    }

    public KualiDecimal getAmountToApply() {
        KualiDecimal applyAmount = KualiDecimal.ZERO;
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            applyAmount = applyAmount.add(detailApplication.getAmountApplied());
        }
        return applyAmount;
    }
    
    // yes this method name is awkward.  Blame JSP that expects an is* or get*
    public boolean isAnyAppliedAmounts() {
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            if (detailApplication.getAmountApplied().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }
    
    public List<PaymentApplicationInvoiceDetailApply> getDetailApplications() {
        return detailApplications;
    }
    
    public String getDocumentNumber() {
        return invoice.getDocumentNumber();
    }
    
    public KualiDecimal getOpenAmount() {
        return invoice.getOpenAmount();
    }
    
    public boolean isQuickApply() {
        return quickApply;
    }

    public void setQuickApply(boolean quickApply) {
        this.quickApplyOldValue = this.quickApply;
        this.quickApply = quickApply;
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            detailApplication.setInvoiceQuickApplied(quickApply);
        }
    }

    public boolean isQuickApplyChanged() {
        return quickApply != quickApplyOldValue;
    }
    
    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }
    
}
