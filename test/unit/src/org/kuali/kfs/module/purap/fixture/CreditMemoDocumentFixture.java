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
import java.sql.Timestamp;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.CreditMemoDocument;

public enum CreditMemoDocumentFixture {

    CM_ONLY_REQUIRED_PO_FIELDS(null,   // paymentRequestIdentifier
            null,                   // creditMemoNumber
            null,                   // creditMemoDate
            null,                   // creditMemoAmount
            null,                   // creditMemoPaidTimestamp
            null,                   // itemMiscellaneousCreditDescription
            null,                   // purchaseOrderEndDate
            PurchasingAccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            AccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,             // apDocumentFixture
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_QTY_UNRESTRICTED_ITEM_1} // requisitionItemMultiFixtures
            ),
    CM_ONLY_REQUIRED_FIELDS(null,   // paymentRequestIdentifier
            "12345",                   // creditMemoNumber
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(),                   // creditMemoDate
            new KualiDecimal(1),                   // creditMemoAmount
            null,                   // creditMemoPaidTimestamp
            null,                   // itemMiscellaneousCreditDescription
            null,                   // purchaseOrderEndDate
            PurchasingAccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            AccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,             // apDocumentFixture
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_QTY_UNRESTRICTED_ITEM_1} // creditMemoItemMultiFixtures
            );            
            
  
    public final Integer paymentRequestIdentifier;
    public final String creditMemoNumber;
    public final Date creditMemoDate;
    public final KualiDecimal creditMemoAmount;
    public final Timestamp creditMemoPaidTimestamp;
    public final String itemMiscellaneousCreditDescription;
    public final Date purchaseOrderEndDate;
    
    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private AccountsPayableDocumentFixture apDocumentFixture;
    private CreditMemoItemFixture[] creditMemoItemFixtures;
    
    private CreditMemoDocumentFixture(
            Integer paymentRequestIdentifier,
            String creditMemoNumber,
            Date creditMemoDate,
            KualiDecimal creditMemoAmount,
            Timestamp creditMemoPaidTimestamp,
            String itemMiscellaneousCreditDescription,
            Date purchaseOrderEndDate,
            PurchasingAccountsPayableDocumentFixture purapDocumentFixture,
            AccountsPayableDocumentFixture apDocumentFixture,
            CreditMemoItemFixture[] creditMemoItemFixtures) {
        
        this.paymentRequestIdentifier = paymentRequestIdentifier;
        this.creditMemoNumber = creditMemoNumber;
        this.creditMemoDate = creditMemoDate;
        this.creditMemoAmount = creditMemoAmount;
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
        this.purchaseOrderEndDate = purchaseOrderEndDate;
        this.purapDocumentFixture = purapDocumentFixture;
        this.apDocumentFixture = apDocumentFixture;
        this.creditMemoItemFixtures = creditMemoItemFixtures;
    }
    
    public CreditMemoDocument createCreditMemoDocument() {
        CreditMemoDocument doc = apDocumentFixture.createCreditMemoDocument(purapDocumentFixture);
        doc.setPaymentRequestIdentifier(this.paymentRequestIdentifier);
        doc.setCreditMemoNumber(this.creditMemoNumber);
        doc.setCreditMemoDate(this.creditMemoDate);
        doc.setCreditMemoAmount(this.creditMemoAmount);
        doc.setCreditMemoPaidTimestamp(this.creditMemoPaidTimestamp);
        doc.setItemMiscellaneousCreditDescription(this.itemMiscellaneousCreditDescription);
        doc.setPurchaseOrderEndDate(this.purchaseOrderEndDate);
        
        for (CreditMemoItemFixture creditMemoItemFixture : creditMemoItemFixtures) { 
            creditMemoItemFixture.addTo(doc);
        }
        
        return doc;
    }
    
}
