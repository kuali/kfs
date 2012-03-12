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

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.PREQInvoice;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PaymentRequestInvoiceTabFixture {

    WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT(PREQInvoice.PO_ID, PREQInvoice.INVOICE_DATE, PREQInvoice.INVOICE_NUMBER, PREQInvoice.AMOUNT), NO_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT(null, PREQInvoice.INVOICE_DATE, PREQInvoice.INVOICE_NUMBER, PREQInvoice.AMOUNT), WITH_POID_NO_DATE_WITH_NUMBER_WITH_AMOUNT(PREQInvoice.PO_ID, null, PREQInvoice.INVOICE_NUMBER, PREQInvoice.AMOUNT), WITH_POID_WITH_DATE_NO_NUMBER_WITH_AMOUNT(PREQInvoice.PO_ID, PREQInvoice.INVOICE_DATE, null, PREQInvoice.AMOUNT), WITH_POID_WITH_DATE_WITH_NUMBER_NO_AMOUNT(PREQInvoice.PO_ID, PREQInvoice.INVOICE_DATE, PREQInvoice.INVOICE_NUMBER, null)

    ;

    private Integer po_id;
    private Date invoice_date;
    private String invoice_num;
    private KualiDecimal amount;

    private PaymentRequestInvoiceTabFixture(Integer po_id, Date invoice_date, String invoice_num, KualiDecimal amount) {
        this.po_id = po_id;
        this.invoice_date = invoice_date;
        this.invoice_num = invoice_num;
        this.amount = amount;
    }

    public PaymentRequestDocument populate(PaymentRequestDocument preqDocument) {
        preqDocument.setPurchaseOrderIdentifier(po_id);
        preqDocument.setInvoiceDate(invoice_date);
        preqDocument.setInvoiceNumber(invoice_num);
        preqDocument.setVendorInvoiceAmount(amount);
        return preqDocument;
    }
}
