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

import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceBill
 */
public enum InvoiceBillFixture {

    INV_BILL_1("5030", new Long(111), new Long(111), "Bill 1", new Long(1), new KualiDecimal(1), "No");
    private String documentNumber;
    private Long proposalNumber;
    private Long billNumber;
    private String billDescription;
    private Long billIdentifier;

    private KualiDecimal estimatedAmount;
    private String isItBilled;


    private InvoiceBillFixture(String documentNumber, Long proposalNumber, Long billNumber, String billDescription, Long billIdentifier, KualiDecimal estimatedAmount, String isItBilled) {
        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.billNumber = billNumber;
        this.billDescription = billDescription;
        this.billIdentifier = billIdentifier;
        this.estimatedAmount = estimatedAmount;
        this.isItBilled = isItBilled;

    }

    public InvoiceBill createInvoiceBill() {
        InvoiceBill bill = new InvoiceBill();
        bill.setDocumentNumber(this.documentNumber);
        bill.setProposalNumber(this.proposalNumber);
        bill.setBillNumber(this.billNumber);
        bill.setBillIdentifier(this.billIdentifier);
        bill.setBillDescription(this.billDescription);
        bill.setEstimatedAmount(this.estimatedAmount);
        bill.setIsItBilled(this.isItBilled);
        return bill;
    }
}
