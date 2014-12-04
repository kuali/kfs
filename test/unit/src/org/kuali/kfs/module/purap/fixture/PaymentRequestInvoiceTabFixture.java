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
