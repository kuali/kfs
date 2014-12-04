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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceBill
 */
public enum InvoiceBillFixture {

    INV_BILL_1("5030", new Long(1), "Bill 1", new Long(1), null, new KualiDecimal(1)),
    INV_BILL_2("5030", new Long(2), "Bill 2", new Long(2), new Date(System.currentTimeMillis()), new KualiDecimal(1));

    private String documentNumber;
    private Long billNumber;
    private String billDescription;
    private Long billIdentifier;
    private Date billDate;
    private KualiDecimal estimatedAmount;

    private InvoiceBillFixture(String documentNumber, Long billNumber, String billDescription, Long billIdentifier, Date billDate, KualiDecimal estimatedAmount) {
        this.documentNumber = documentNumber;
        this.billNumber = billNumber;
        this.billDescription = billDescription;
        this.billIdentifier = billIdentifier;
        this.billDate = billDate;
        this.estimatedAmount = estimatedAmount;
    }

    public InvoiceBill createInvoiceBill() {
        InvoiceBill bill = new InvoiceBill();
        bill.setDocumentNumber(this.documentNumber);
        bill.setBillNumber(this.billNumber);
        bill.setBillIdentifier(this.billIdentifier);
        bill.setBillDescription(this.billDescription);
        bill.setBillDate(this.billDate);
        bill.setEstimatedAmount(this.estimatedAmount);
        return bill;
    }
}
