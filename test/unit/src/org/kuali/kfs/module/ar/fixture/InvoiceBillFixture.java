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

    INV_BILL_1("5030", new Long(111), new Long(111), "Bill 1", new Long(1), null, false, new KualiDecimal(1)),
    INV_BILL_2("5030", new Long(111), new Long(111), "Bill 1", new Long(1), new Date(System.currentTimeMillis()), false, new KualiDecimal(1));

    private String documentNumber;
    private Long proposalNumber;
    private Long billNumber;
    private String billDescription;
    private Long billIdentifier;
    private Date billDate;
    private boolean billed;
    private KualiDecimal estimatedAmount;

    private InvoiceBillFixture(String documentNumber, Long proposalNumber, Long billNumber, String billDescription, Long billIdentifier, Date billDate, boolean billed, KualiDecimal estimatedAmount) {
        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.billNumber = billNumber;
        this.billDescription = billDescription;
        this.billIdentifier = billIdentifier;
        this.billDate = billDate;
        this.billed = billed;
        this.estimatedAmount = estimatedAmount;
    }

    public InvoiceBill createInvoiceBill() {
        InvoiceBill bill = new InvoiceBill();
        bill.setDocumentNumber(this.documentNumber);
        bill.setProposalNumber(this.proposalNumber);
        bill.setBillNumber(this.billNumber);
        bill.setBillIdentifier(this.billIdentifier);
        bill.setBillDescription(this.billDescription);
        bill.setBillDate(this.billDate);
        bill.setBilled(this.billed);
        bill.setEstimatedAmount(this.estimatedAmount);
        return bill;
    }
}
