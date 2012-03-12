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

import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum LineItemReceivingDocumentFixture {

    // Receiving Line FIXTURES
    EMPTY_LINE_ITEM_RECEIVING(
            null,//carrierCode
            "",//shipmentPackingSlipNumber,
            "",//shipmentReferenceNumber,
            "",//shipmentBillOfLadingNumber,
            new Date(108, 7, 11),//shipmentReceivedDate,
            new Integer(0),//vendorHeaderGeneratedIdentifier,
            new Integer(0),//vendorDetailAssignedIdentifier,
            "",//vendorName,
            "",//vendorLine1Address,
            "",//vendorLine2Address,
            "",//vendorCityName,
            "",//vendorStateCode,
            "",//vendorPostalCode,
            "",//vendorCountryCode,
            "",//deliveryCampusCode,
            "",//deliveryBuildingCode,
            "",//deliveryBuildingName,
            "",//deliveryBuildingRoomNumber,
            "",//deliveryBuildingLine1Address,
            "",//deliveryBuildingLine2Address,
            "",//deliveryCityName,
            "",//deliveryStateCode,
            "",//deliveryPostalCode,
            "",//deliveryCountryCode,
            "",//deliveryToName,
            "",//deliveryToEmailAddress,
            "",//deliveryToPhoneNumber,
            new Date(108, 7, 11),//deliveryRequiredDate,
            "",//deliveryInstructionText,
            null,//deliveryRequiredDateReasonCode,    
            new Integer(0),//alternateVendorHeaderGeneratedIdentifier,
            new Integer(0),//alternateVendorDetailAssignedIdentifier,
            "",//alternateVendorName,        
            false,//deliveryBuildingOther,
            null,//vendorNumber,
            new Integer(0),//vendorAddressGeneratedIdentifier,
            null,//alternateVendorNumber
            new Integer(0),//purchaseOrderIdentifier
            new LineItemReceivingItemFixture[] {} //lineItemReceivingItemFixtures
        ),    
    REQUIRED_FIELDS(
        null,//carrierCode
        "",//shipmentPackingSlipNumber,
        "",//shipmentReferenceNumber,
        "",//shipmentBillOfLadingNumber,
        new Date(108, 7, 11),//shipmentReceivedDate,
        new Integer(1000),//vendorHeaderGeneratedIdentifier,
        new Integer(0),//vendorDetailAssignedIdentifier,
        "ABC CLEANING SERVICES",//vendorName,
        "123456 BROAD ST",//vendorLine1Address,
        "",//vendorLine2Address,
        "TRUMANSBURG",//vendorCityName,
        "NY",//vendorStateCode,
        "14886",//vendorPostalCode,
        "US",//vendorCountryCode,
        "BL",//deliveryCampusCode,
        "",//deliveryBuildingCode,
        "Law",//deliveryBuildingName,
        "1324",//deliveryBuildingRoomNumber,
        "211 S Indiana Ave",//deliveryBuildingLine1Address,
        "",//deliveryBuildingLine2Address,
        "Bloomington",//deliveryCityName,
        "IN",//deliveryStateCode,
        "47405-7001",//deliveryPostalCode,
        "",//deliveryCountryCode,
        "ABEYTA,JULIANNE Z",//deliveryToName,
        "",//deliveryToEmailAddress,
        "",//deliveryToPhoneNumber,
        new Date(108, 7, 11),//deliveryRequiredDate,
        "",//deliveryInstructionText,
        "",//deliveryRequiredDateReasonCode,    
        new Integer(0),//alternateVendorHeaderGeneratedIdentifier,
        new Integer(0),//alternateVendorDetailAssignedIdentifier,
        "",//alternateVendorName,        
        false,//deliveryBuildingOther,
        "1000-0",//vendorNumber,
        new Integer(1000),//vendorAddressGeneratedIdentifier,
        null,//alternateVendorNumber
        new Integer(0),//purchaseOrderIdentifier
        new LineItemReceivingItemFixture[] { //lineItemReceivingItemFixtures
            LineItemReceivingItemFixture.NORMAL_ITEM_1 }
    );
                        
    public String carrierCode;
    public String shipmentPackingSlipNumber;
    public String shipmentReferenceNumber;
    public String shipmentBillOfLadingNumber;
    public Date shipmentReceivedDate;
    public Integer vendorHeaderGeneratedIdentifier;
    public Integer vendorDetailAssignedIdentifier;
    public String vendorName;
    public String vendorLine1Address;
    public String vendorLine2Address;
    public String vendorCityName;
    public String vendorStateCode;
    public String vendorPostalCode;
    public String vendorCountryCode;
    public String deliveryCampusCode;
    public String deliveryBuildingCode;
    public String deliveryBuildingName;
    public String deliveryBuildingRoomNumber;
    public String deliveryBuildingLine1Address;
    public String deliveryBuildingLine2Address;
    public String deliveryCityName;
    public String deliveryStateCode;
    public String deliveryPostalCode;
    public String deliveryCountryCode;
    public String deliveryToName;
    public String deliveryToEmailAddress;
    public String deliveryToPhoneNumber;
    public Date deliveryRequiredDate;
    public String deliveryInstructionText;
    public String deliveryRequiredDateReasonCode;
    private Integer purchaseOrderIdentifier;
    
    public Integer alternateVendorHeaderGeneratedIdentifier;
    public Integer alternateVendorDetailAssignedIdentifier;
    public String alternateVendorName;
    
    //not persisted in db
    public boolean deliveryBuildingOtherIndicator;
    public String vendorNumber;
    public Integer vendorAddressGeneratedIdentifier;
    public String alternateVendorNumber;

    private LineItemReceivingItemFixture[] lineItemReceivingItemFixtures;

    private LineItemReceivingDocumentFixture(    
            String carrierCode, String shipmentPackingSlipNumber, String shipmentReferenceNumber, String shipmentBillOfLadingNumber, Date shipmentReceivedDate,
            Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String vendorName, String vendorLine1Address,
            String vendorLine2Address, String vendorCityName, String vendorStateCode, String vendorPostalCode, String vendorCountryCode,
            String deliveryCampusCode, String deliveryBuildingCode, String deliveryBuildingName, String deliveryBuildingRoomNumber,
            String deliveryBuildingLine1Address, String deliveryBuildingLine2Address, String deliveryCityName, String deliveryStateCode,
            String deliveryPostalCode, String deliveryCountryCode, String deliveryToName, String deliveryToEmailAddress, String deliveryToPhoneNumber,
            Date deliveryRequiredDate, String deliveryInstructionText, String deliveryRequiredDateReasonCode,       
            Integer alternateVendorHeaderGeneratedIdentifier, Integer alternateVendorDetailAssignedIdentifier,
            String alternateVendorName, boolean deliveryBuildingOther, String vendorNumber, Integer vendorAddressGeneratedIdentifier, String alternateVendorNumber,
            Integer purchaseOrderIdentifier, LineItemReceivingItemFixture[] lineItemReceivingItemFixtures) {

        this.carrierCode = carrierCode;
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
        this.shipmentReferenceNumber = shipmentReferenceNumber;
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
        this.shipmentReceivedDate = shipmentReceivedDate;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorLine2Address = vendorLine2Address;
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode;
        this.vendorPostalCode = vendorPostalCode;
        this.vendorCountryCode = vendorCountryCode;
        this.deliveryCampusCode = deliveryCampusCode;
        this.deliveryBuildingCode = deliveryBuildingCode;
        this.deliveryBuildingName = deliveryBuildingName;
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
        this.deliveryCityName = deliveryCityName;
        this.deliveryStateCode = deliveryStateCode;
        this.deliveryPostalCode = deliveryPostalCode;
        this.deliveryCountryCode = deliveryCountryCode;
        this.deliveryToName = deliveryToName;
        this.deliveryToEmailAddress = deliveryToEmailAddress;
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
        this.deliveryRequiredDate = deliveryRequiredDate;
        this.deliveryInstructionText = deliveryInstructionText;
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
        this.alternateVendorName = alternateVendorName;                
        this.deliveryBuildingOtherIndicator = deliveryBuildingOtherIndicator;
        this.vendorNumber = vendorNumber;
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
        this.alternateVendorNumber = alternateVendorNumber;
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
        
        this.lineItemReceivingItemFixtures = lineItemReceivingItemFixtures;
    }

    public LineItemReceivingDocument createLineItemReceivingDocument() {
        LineItemReceivingDocument doc = null;
       
        try {
            doc = (LineItemReceivingDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), LineItemReceivingDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        doc.setCarrierCode(this.carrierCode);
        doc.setShipmentPackingSlipNumber(this.shipmentPackingSlipNumber);
        doc.setShipmentReferenceNumber(this.shipmentReferenceNumber);
        doc.setShipmentBillOfLadingNumber(this.shipmentBillOfLadingNumber);
        doc.setShipmentReceivedDate(this.shipmentReceivedDate);
        doc.setVendorHeaderGeneratedIdentifier(this.vendorHeaderGeneratedIdentifier);
        doc.setVendorDetailAssignedIdentifier(this.vendorDetailAssignedIdentifier);
        doc.setVendorName(this.vendorName);
        doc.setVendorLine1Address(this.vendorLine1Address);
        doc.setVendorLine2Address(this.vendorLine2Address);
        doc.setVendorCityName(this.vendorCityName);
        doc.setVendorStateCode(this.vendorStateCode);
        doc.setVendorPostalCode(this.vendorPostalCode);
        doc.setVendorCountryCode(this.vendorCountryCode);
        doc.setDeliveryCampusCode(this.deliveryCampusCode);
        doc.setDeliveryBuildingCode(this.deliveryBuildingCode);
        doc.setDeliveryBuildingName(this.deliveryBuildingName);
        doc.setDeliveryBuildingRoomNumber(this.deliveryBuildingRoomNumber);
        doc.setDeliveryBuildingLine1Address(this.deliveryBuildingLine1Address);
        doc.setDeliveryBuildingLine2Address(this.deliveryBuildingLine2Address);
        doc.setDeliveryCityName(this.deliveryCityName);
        doc.setDeliveryStateCode(this.deliveryStateCode);
        doc.setDeliveryPostalCode(this.deliveryPostalCode);
        doc.setDeliveryCountryCode(this.deliveryCountryCode);
        doc.setDeliveryToName(this.deliveryToName);
        doc.setDeliveryToEmailAddress(this.deliveryToEmailAddress);
        doc.setDeliveryToPhoneNumber(this.deliveryToPhoneNumber);
        doc.setDeliveryRequiredDate(this.deliveryRequiredDate);
        doc.setDeliveryInstructionText(this.deliveryInstructionText);
        doc.setDeliveryRequiredDateReasonCode(this.deliveryRequiredDateReasonCode);
        doc.setAlternateVendorHeaderGeneratedIdentifier(this.alternateVendorHeaderGeneratedIdentifier);
        doc.setAlternateVendorDetailAssignedIdentifier(this.alternateVendorDetailAssignedIdentifier);
        doc.setAlternateVendorName(this.alternateVendorName);
        doc.setDeliveryBuildingOtherIndicator(this.deliveryBuildingOtherIndicator);
        doc.setVendorNumber(this.vendorNumber);
        doc.setVendorAddressGeneratedIdentifier(this.vendorAddressGeneratedIdentifier);
        doc.setAlternateVendorNumber(this.alternateVendorNumber);
        doc.setPurchaseOrderIdentifier(this.purchaseOrderIdentifier);
        
        for (LineItemReceivingItemFixture lineItemReceivingItemFixture : lineItemReceivingItemFixtures) {
            lineItemReceivingItemFixture.addTo(doc);
        }

        return doc;
    }
    
}
