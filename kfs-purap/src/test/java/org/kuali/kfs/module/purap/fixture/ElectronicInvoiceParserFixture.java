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

import org.apache.commons.lang.StringUtils;

public class ElectronicInvoiceParserFixture {

    /**
     * CXML element attributes
     */
    public static final String payloadID = "200807260401062080.964@eai002";
    public static final String timestamp = "2008-07-26T04:01:06-08:00";
    public static final String version = "1.2.014";
    public static final String locale = "en";
    
    /**
     * Request attribute
     */
    public static final String deploymentMode = "production";
    
    /**
     * Header information
     */
    public static final String fromDomain = "DUNS";
    public static final String fromIdentity = "121212";
    
    public static final String toDomain = "NetworkId";
    public static final String toIdentity = "IUHIGHERM";
    
    public static final String senderDomain = "DUNS";
    public static final String senderIdentity = "121212";
    public static final String senderSharedSecret = "fisherscipass";
    public static final String senderUserAgent = "IUPUI";
    
    /**
     * Invoice details header information
     */
    public static final String invoiceDate = "2008-07-25T00:00:00-08:00";
    public static final String invoiceID = "133";
    public static final String operation = "new";
    public static final String purpose = "standard";
    
    /**
     * BillTo Invoice Partner
     */
    public static final class BillToContact {
        public static final String addressId = StringUtils.EMPTY;
        public static final String role = "billTo";
        public static final String name= "INDIANA UNIV@INDPLS";
        public static final String street1 = "ACCOUNTING DEPT";
        public static final String street2 = "620 UNION DR"; 
        public static final String street3 = "RM 443";
        public static final String city = "INDIANAPOLIS";
        public static final String state = "IN";
        public static final String postalCode = "462025130";
        public static final String country = "United States";
        public static final String countryCode = "US";
    }
    
    /**
     * remitTo Invoice Partner
     */
    public static final class RemitToContact {
        public static final String addressId = "004321519";
        public static final String role = "remitTo";
        public static final String name= "FISHER SCIENTIFIC COMPANY LLC";
        public static final String street1 = "13551 COLLECTIONS CTR DR";
        public static final String street2 = null; 
        public static final String street3 = null;
        public static final String city = "CHICAGO";
        public static final String state = "IL";
        public static final String postalCode = "60693";
        public static final String country = "United States";
        public static final String countryCode = "US";
    }
    
    public static final String shippingDate = "2008-07-25T00:00:00-08:00";
    
    /**
     * Invoice details shipping
     */
    public static final class ShipToContact {
        public static final String addressId = "387520002";
        public static final String role = "shipTo";
        public static final String name= "INDIANA UNIVERSITY";
        public static final String street1 = "950 W WALNUT ST";
        public static final String street2 = "ROOM #451"; 
        public static final String street3 = null;
        public static final String city = "INDIANAPOLIS";
        public static final String state = "IN";
        public static final String postalCode = "462025188";
        public static final String country = "United States";
        public static final String countryCode = "US";
        
        public static final String emailName1 = "test1";
        public static final String emailValue1 = "abc@efg.com";
        public static final String emailName2 = "test2";
        public static final String emailValue2 = "efg@hij.com";
        
        public static final String phoneName = "testPhone";
        public static final String phoneNumber = "12099545333";
        
        public static final String faxName = "testFax";
        public static final String faxNumber = "12099545331";
        
        public static final String url = "www.abc.com";
    }
    
    /**
     * Payment term 
     */
    public static final int payInNumberOfDays = 30;
    public static final String percentageRate = "0";
    
    /**
     * Invoice Detail Order 
     */
    public static final String orderDate = "2008-07-25T00:00:00-08:00";
    public static final String orderID = "1085";
    public static final String documentRefPayloadID = StringUtils.EMPTY;
    
    public static final class InvoiceItem {
        public static final String invoiceLineNumber = "1";
        public static final String quantity = "10";
        public static final String uom = "BG";
        public static final String unitPrice = "11";
        public static final String itemReferenceLineNumber = "1";
        public static final String SupplierPartID = "1212";
        public static final String itemDescription = "LABCOAT UNISEX LONG XL WHT";
        public static final double lineSubTotalAmt = 110.00;
        public static final double lineTaxAmt = 2.00;
        public static final String lineTaxDesc = "Sales Tax";
        public static final double lineShippingAmt = 10.00;
        public static final double lineSpecialHandlingAmt = 5.00;
    }
    
    public static final class SummaryDetail {
        public static final String subTotalAmt = "1.00";
        public static final String taxAmount = "2.00";
        public static final String taxDescription = "Total Tax";
        public static final String splHandlingAmt = "3.00";
        public static final String shippingAmt = "4.00";
        public static final String grossAmt = "5.00";
        public static final String discountAmt = "6.00";
        public static final String netAmt = "7.00";
        public static final String depositAmt = "8.00";
        public static final String dueAmt = "9.00";
    }
}
