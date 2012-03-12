/*
 * Copyright 2007-2008 The Kuali Foundation
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
import java.sql.Timestamp;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.SequenceAccessorService;

public enum PurchaseOrderVendorQuoteFixture {
    BASIC_VENDOR_QUOTE_1(
                         1000, //vendorHeaderGeneratedIdentifier
                         0,    //vendorDetailAssignedIdentifier
                         "A Name For Vendor", //vendorName
                         "123 Hagadorn Rd", //vendorLine1Address
                         "East Lansing", //vendorCityName
                         "MI", //vendorStateCode
                         "48823", //vendorPostalCode
                         "5173533121", //vendorPhoneNumber
                         "", //vendorFaxNumber
                         "msu@msu.edu", //vendorEmailAddress
                         "Attention Name", //vendorAttentionName
                         "PRIN", //purchaseOrderQuoteTransmitTypeCode
                         SpringContext.getBean(DateTimeService.class).getCurrentTimestamp(), //purchaseOrderQuoteTransmitTimestamp
                         null, //purchaseOrderQuotePriceExpirationDate
                         PurapConstants.QuoteStatusCode.FUIP, //purchaseOrderQuoteStatusCode
                         null, //purchaseOrderQuoteAwardTimestamp
                         "1", //purchaseOrderQuoteRankNumber
                         "US", //vendorCountryCode
                         null //vendorAddressInternationalProvinceName
                         ),  ;

    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorPhoneNumber;
    private String vendorFaxNumber;
    private String vendorEmailAddress;
    private String vendorAttentionName;
    private String purchaseOrderQuoteTransmitTypeCode;
    private Timestamp purchaseOrderQuoteTransmitTimestamp;
    private Date purchaseOrderQuotePriceExpirationDate;
    private String purchaseOrderQuoteStatusCode;
    private Timestamp purchaseOrderQuoteAwardTimestamp;
    private String purchaseOrderQuoteRankNumber;
    private String vendorCountryCode;
    private String vendorAddressInternationalProvinceName;


    private PurchaseOrderVendorQuoteFixture(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String vendorName, String vendorLine1Address, String vendorCityName, String vendorStateCode, String vendorPostalCode, String vendorPhoneNumber, String vendorFaxNumber, String vendorEmailAddress, String vendorAttentionName, String purchaseOrderQuoteTransmitTypeCode, Timestamp purchaseOrderQuoteTransmitTimestamp, Date purchaseOrderQuotePriceExpirationDate, String purchaseOrderQuoteStatusCode, Timestamp purchaseOrderQuoteAwardTimestamp, String purchaseOrderQuoteRankNumber, String vendorCountryCode, String vendorAddressInternationalProvinceName) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode;
        this.vendorPostalCode = vendorPostalCode;
        this.vendorPhoneNumber = vendorPhoneNumber;
        this.vendorFaxNumber = vendorFaxNumber;
        this.vendorEmailAddress = vendorEmailAddress;
        this.vendorAttentionName = vendorAttentionName;
        this.purchaseOrderQuoteTransmitTypeCode = purchaseOrderQuoteTransmitTypeCode;
        this.purchaseOrderQuoteTransmitTimestamp = purchaseOrderQuoteTransmitTimestamp;
        this.purchaseOrderQuotePriceExpirationDate = purchaseOrderQuotePriceExpirationDate;
        this.purchaseOrderQuoteStatusCode = purchaseOrderQuoteStatusCode;
        this.purchaseOrderQuoteAwardTimestamp = purchaseOrderQuoteAwardTimestamp;
        this.purchaseOrderQuoteRankNumber = purchaseOrderQuoteRankNumber;
        this.vendorCountryCode = vendorCountryCode;
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public PurchaseOrderVendorQuote createPurchaseOrderVendorQuote() {
        PurchaseOrderVendorQuote vendorQuote = new PurchaseOrderVendorQuote();
        vendorQuote.setVendorHeaderGeneratedIdentifier (vendorHeaderGeneratedIdentifier);
        vendorQuote.setVendorDetailAssignedIdentifier (vendorDetailAssignedIdentifier);
        vendorQuote.setVendorName (vendorName);
        vendorQuote.setVendorLine1Address (vendorLine1Address);
        vendorQuote.setVendorCityName (vendorCityName);
        vendorQuote.setVendorStateCode (vendorStateCode);
        vendorQuote.setVendorPostalCode (vendorPostalCode);
        vendorQuote.setVendorPhoneNumber (vendorPhoneNumber);
        vendorQuote.setVendorFaxNumber (vendorFaxNumber);
        vendorQuote.setVendorEmailAddress (vendorEmailAddress);
        vendorQuote.setVendorAttentionName (vendorAttentionName);
        vendorQuote.setPurchaseOrderQuoteTransmitTypeCode (purchaseOrderQuoteTransmitTypeCode);
        vendorQuote.setPurchaseOrderQuoteTransmitTimestamp (purchaseOrderQuoteTransmitTimestamp);
        vendorQuote.setPurchaseOrderQuotePriceExpirationDate (purchaseOrderQuotePriceExpirationDate);
        vendorQuote.setPurchaseOrderQuoteStatusCode(purchaseOrderQuoteStatusCode);
        vendorQuote.setPurchaseOrderQuoteAwardTimestamp (purchaseOrderQuoteAwardTimestamp);
        vendorQuote.setPurchaseOrderQuoteRankNumber (purchaseOrderQuoteRankNumber);
        vendorQuote.setVendorCountryCode (vendorCountryCode);
        vendorQuote.setVendorAddressInternationalProvinceName (vendorAddressInternationalProvinceName);

        SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        Integer purchaseOrderVendorQuoteIdentifier = new Integer(sequenceAccessorService.getNextAvailableSequenceNumber("PO_VNDR_QT_ID").toString());
        vendorQuote.setPurchaseOrderVendorQuoteIdentifier(purchaseOrderVendorQuoteIdentifier);
        vendorQuote.refreshNonUpdateableReferences();
        
        return vendorQuote;
    }
}
