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
import java.sql.Timestamp;

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum CreditMemoDocumentFixture {
    
    CM_ONLY_REQUIRED_PO_FIELDS(null, // paymentRequestIdentifier
            null,                   // creditMemoNumber
            null,                   // creditMemoDate
            null,                   // creditMemoAmount
            new KualiDecimal(1),    // grandTotal
            new KualiDecimal(1),    // totalDollarAmount
            null,                   // creditMemoPaidTimestamp
            null,                   // itemMiscellaneousCreditDescription
            null,                   // purchaseOrderEndDate
            "appleton",             // accountsPayableProcessorIdentifier
            PurchasingAccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            AccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,             // apDocumentFixture
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_QTY_UNRESTRICTED_ITEM_1} // requisitionItemMultiFixtures
            ),
    CM_ONLY_REQUIRED_FIELDS(null,   // paymentRequestIdentifier
            "12345",                   // creditMemoNumber
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(),     // creditMemoDate
            new KualiDecimal(1),    // creditMemoAmount
            new KualiDecimal(1),    // grandTotal
            new KualiDecimal(1),    // totalDollarAmount
            null,                   // creditMemoPaidTimestamp
            null,                   // itemMiscellaneousCreditDescription
            null,                   // purchaseOrderEndDate
            "appleton",             // accountsPayableProcessorIdentifier
            PurchasingAccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            AccountsPayableDocumentFixture.CM_ONLY_REQUIRED_FIELDS,             // apDocumentFixture
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_QTY_UNRESTRICTED_ITEM_1} // creditMemoItemMultiFixtures
            );            
            
  
    public final Integer paymentRequestIdentifier;
    public final String creditMemoNumber;
    public final Date creditMemoDate;
    public final KualiDecimal creditMemoAmount;
    public final KualiDecimal grandTotal;
    public final KualiDecimal totalDollarAmount;
    public final Timestamp creditMemoPaidTimestamp;
    public final String itemMiscellaneousCreditDescription;
    public final Date purchaseOrderEndDate;
    public final String accountsPayableProcessorIdentifier;
    
    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private AccountsPayableDocumentFixture apDocumentFixture;
    private CreditMemoItemFixture[] creditMemoItemFixtures;
    
    
    private CreditMemoDocumentFixture(
            Integer paymentRequestIdentifier,
            String creditMemoNumber,
            Date creditMemoDate,
            KualiDecimal creditMemoAmount,
            KualiDecimal grandTotal,
            KualiDecimal totalDollarAmount,
            Timestamp creditMemoPaidTimestamp,
            String itemMiscellaneousCreditDescription,
            Date purchaseOrderEndDate,
            String accountsPayableProcessorIdentifier,
            PurchasingAccountsPayableDocumentFixture purapDocumentFixture,
            AccountsPayableDocumentFixture apDocumentFixture,
            CreditMemoItemFixture[] creditMemoItemFixtures) {
        
        this.paymentRequestIdentifier = paymentRequestIdentifier;
        this.creditMemoNumber = creditMemoNumber;
        this.creditMemoDate = creditMemoDate;
        this.creditMemoAmount = creditMemoAmount;
        this.grandTotal = grandTotal;
        this.totalDollarAmount = totalDollarAmount;
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
        this.purchaseOrderEndDate = purchaseOrderEndDate;
        this.purapDocumentFixture = purapDocumentFixture;
        this.apDocumentFixture = apDocumentFixture;
        this.creditMemoItemFixtures = creditMemoItemFixtures;
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
    }
    
    public VendorCreditMemoDocument createCreditMemoDocument() {
        VendorCreditMemoDocument doc = apDocumentFixture.createCreditMemoDocument(purapDocumentFixture);
        doc.setPaymentRequestIdentifier(this.paymentRequestIdentifier);
        doc.setCreditMemoNumber(this.creditMemoNumber);
        doc.setCreditMemoDate(this.creditMemoDate);
        doc.setCreditMemoAmount(this.creditMemoAmount);
        doc.setGrandTotal(this.grandTotal);
        doc.setTotalDollarAmount(this.totalDollarAmount);
        doc.setCreditMemoPaidTimestamp(this.creditMemoPaidTimestamp);
        doc.setItemMiscellaneousCreditDescription(this.itemMiscellaneousCreditDescription);
        doc.setPurchaseOrderEndDate(this.purchaseOrderEndDate);
        doc.setAccountsPayableProcessorIdentifier(this.accountsPayableProcessorIdentifier);
        //manually set bank for now
        doc.setBankCode("TEST");   
        
        for (CreditMemoItemFixture creditMemoItemFixture : creditMemoItemFixtures) { 
            creditMemoItemFixture.addTo(doc);
        }
        
        return doc;
    }
    
}
