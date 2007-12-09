/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.sql.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.fixtures.PurapTestConstants.PREQInvoice;
import org.kuali.module.purap.fixtures.PurapTestConstants.CMInit;

public enum CreditMemoInitTabFixture {
    
    WITH_INVOICE_WITH_DATE_WITH_AMOUNT(PREQInvoice.INVOICE_NUMBER,PREQInvoice.INVOICE_DATE,PREQInvoice.AMOUNT),
    NO_INVOICE_WITH_DATE_WITH_AMOUNT(null,PREQInvoice.INVOICE_DATE,PREQInvoice.AMOUNT),
    WITH_INVOICE_NO_DATE_WITH_AMOUNT(PREQInvoice.INVOICE_NUMBER,null,PREQInvoice.AMOUNT),
    WITH_INVOICE_WITH_DATE_NO_AMOUNT(PREQInvoice.INVOICE_NUMBER,PREQInvoice.INVOICE_DATE,null),
    WITH_VENDOR_NUMBER(CMInit.INITIAL_VENDOR_NUMBER),
    ;
    
    private String invoice_num;
    private Date invoice_date;    
    private KualiDecimal amount;
    private String vendor_num;
    
    private CreditMemoInitTabFixture(String invoice_num, Date invoice_date, KualiDecimal amount) {
        this.invoice_num = invoice_num;
        this.invoice_date = invoice_date;
        this.amount = amount;        
    }
    
    private CreditMemoInitTabFixture(String vendor_num) {
        this.vendor_num = vendor_num;
    }
        
    public CreditMemoDocument populateForRequiredness(CreditMemoDocument cmDocument) {
        cmDocument.setCreditMemoNumber(invoice_num);
        cmDocument.setCreditMemoDate(invoice_date);
        cmDocument.setCreditMemoAmount(amount);
        return cmDocument;
    }
    
    public CreditMemoDocument populateForReferenceNumbers(CreditMemoDocument cmDocument) {
        cmDocument.setVendorNumber(vendor_num);
        return cmDocument;
    }
    


}
