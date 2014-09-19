/*
 * Copyright 2011 The Kuali Foundation.
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
