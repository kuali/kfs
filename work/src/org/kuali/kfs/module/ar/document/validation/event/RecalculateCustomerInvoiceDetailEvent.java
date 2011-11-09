/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

public class RecalculateCustomerInvoiceDetailEvent extends AttributedDocumentEventBase {

    private final CustomerInvoiceDetail customerInvoiceDetail;
    
    public RecalculateCustomerInvoiceDetailEvent(String errorPathPrefix, Document document, CustomerInvoiceDetail customerInvoiceDetail) {
        super("Recalculating customer invoice detail for document " + getDocumentId(document), errorPathPrefix, document);
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }
    
    /**
     * Convenience getter, for standard names (which will make this event a bit more reusable)
     * @return returns the encapsulated CustomerInvoiceDetail
     */
    public AccountingLine getAccountingLine() {
        return customerInvoiceDetail;
    }

}
