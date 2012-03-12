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
package org.kuali.kfs.module.purap.fixture;

import java.sql.Date;

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.CMInit;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.PREQInvoice;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum CreditMemoInitTabFixture {
    
    WITH_INVOICE_WITH_DATE_WITH_AMOUNT(PREQInvoice.INVOICE_NUMBER,PREQInvoice.INVOICE_DATE,PREQInvoice.AMOUNT),
    NO_INVOICE_WITH_DATE_WITH_AMOUNT(null,PREQInvoice.INVOICE_DATE,PREQInvoice.AMOUNT),
    WITH_INVOICE_NO_DATE_WITH_AMOUNT(PREQInvoice.INVOICE_NUMBER,null,PREQInvoice.AMOUNT),
    WITH_INVOICE_WITH_DATE_NO_AMOUNT(PREQInvoice.INVOICE_NUMBER,PREQInvoice.INVOICE_DATE,null),
    WITH_VENDOR_NUMBER(CMInit.INITIAL_VENDOR_NUMBER),
    HI_AMOUNT_LO_TOTAL(CMInit.HIGH_AMOUNT,CMInit.LOW_AMOUNT),
    LO_AMOUNT_HI_TOTAL(CMInit.LOW_AMOUNT,CMInit.HIGH_AMOUNT),
    HI_AMOUNT_HI_TOTAL(CMInit.HIGH_AMOUNT,CMInit.HIGH_AMOUNT),
    HI_AMOUNT_ZERO_TOTAL(CMInit.HIGH_AMOUNT,CMInit.ZERO_AMOUNT),
    ;
    
    private String invoice_num;
    private Date invoice_date;    
    private KualiDecimal amount;
    private String vendor_num;
    private KualiDecimal total;
    
    private CreditMemoInitTabFixture(String invoice_num, Date invoice_date, KualiDecimal amount) {
        this.invoice_num = invoice_num;
        this.invoice_date = invoice_date;
        this.amount = amount;        
    }
    
    private CreditMemoInitTabFixture(String vendor_num) {
        this.vendor_num = vendor_num;
    }
    
    private CreditMemoInitTabFixture(KualiDecimal amount, KualiDecimal total) {
        this.amount = amount;
        this.total = total;
    }
        
    public VendorCreditMemoDocument populateForRequiredness(VendorCreditMemoDocument cmDocument) {
        cmDocument.setCreditMemoNumber(invoice_num);
        cmDocument.setCreditMemoDate(invoice_date);
        cmDocument.setCreditMemoAmount(amount);
        return cmDocument;
    }
    
    public VendorCreditMemoDocument populateForReferenceNumbers(VendorCreditMemoDocument cmDocument) {
        cmDocument.setVendorNumber(vendor_num);
        return cmDocument;
    }
    
    public VendorCreditMemoDocument populateForAmounts(VendorCreditMemoDocument cmDocument) {
        cmDocument.setCreditMemoAmount(amount);
        cmDocument.setGrandTotal(total);
        return cmDocument;
    }

}
