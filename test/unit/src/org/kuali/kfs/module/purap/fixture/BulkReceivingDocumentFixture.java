/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.BulkReceiving;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum BulkReceivingDocumentFixture {
    
    SIMPLE_DOCUMENT(
        null,//purchaseOrderIdentifier
        BulkReceiving.SHIPMENT_RECEIVIED_DATE,//shipmentReceivedDate
        "123",//shipmentPackingSlipNumber
        "BEK",//carrierCode
        "456",//shipmentBillOfLadingNumber
        "789",//shipmentReferenceNumber
        "10 lb",//shipmentWeight
        new Integer("10"),//noOfCartons
        "10",//trackingNumber
        new Integer("1000"),//vendorHeaderGeneratedIdentifier
        new Integer("0"),//vendorDetailAssignedIdentifier
        "ABC CLEANING SERVICES",//vendorName
        "123456 BROAD ST",//vendorLine1Address
        null,//vendorLine2Address
        "TRUMANSBURG",//vendorCityName
        "NY",//vendorStateCode
        "14886",//vendorPostalCode
        "US",//vendorCountryCode
        null,//vendorAddressInternationalProvinceName
        null,//vendorNoteText
        null,//alternateVendorHeaderGeneratedIdentifier
        null,//alternateVendorDetailAssignedIdentifier
        null,//alternateVendorName
        "ADMN",//deliveryBuildingCode
        "Administration",//deliveryBuildingName
        "100",//deliveryBuildingRoomNumber
        "211 S Indiana Ave",//deliveryBuildingLine1Address
        null,//deliveryBuildingLine2Address
        "Bloomington",//deliveryCityName
        "IN",//deliveryStateCode
        "95207",//deliveryPostalCode
        "US",//deliveryCountryCode
        "BA",//deliveryCampusCode
        "Deliver at the front office",//deliveryInstructionText
        null,//deliveryAdditionalInstructionText
        "ARROWOOD,DEIRDRE K",//deliveryToName
        "abc@efg.com",//deliveryToEmailAddress
        "000-111-2222",//deliveryToPhoneNumber
        null,//institutionContactName
        null,//institutionContactPhoneNumber
        null//institutionContactEmailAddress
        
    ),
    
    SIMPLE_DOCUMENT_FOR_PO(
        null,//purchaseOrderIdentifier
        BulkReceiving.SHIPMENT_RECEIVIED_DATE,//shipmentReceivedDate
        "123",//shipmentPackingSlipNumber
        "BEK",//carrierCode
        "456",//shipmentBillOfLadingNumber
        "789",//shipmentReferenceNumber
        "10 lb",//shipmentWeight
        new Integer("10"),//noOfCartons
        "10",//trackingNumber
        null,//vendorHeaderGeneratedIdentifier
        null,//vendorDetailAssignedIdentifier
        null,//vendorName
        null,//vendorLine1Address
        null,//vendorLine2Address
        null,//vendorCityName
        null,//vendorStateCode
        null,//vendorPostalCode
        null,//vendorCountryCode
        null,//vendorAddressInternationalProvinceName
        null,//vendorNoteText
        null,//alternateVendorHeaderGeneratedIdentifier
        null,//alternateVendorDetailAssignedIdentifier
        null,//alternateVendorName
        null,//deliveryBuildingCode
        null,//deliveryBuildingName
        null,//deliveryBuildingRoomNumber
        null,//deliveryBuildingLine1Address
        null,//deliveryBuildingLine2Address
        null,//deliveryCityName
        null,//deliveryStateCode
        null,//deliveryPostalCode
        null,//deliveryCountryCode
        null,//deliveryCampusCode
        null,//deliveryInstructionText
        null,//deliveryAdditionalInstructionText
        null,//deliveryToName
        null,//deliveryToEmailAddress
        null,//deliveryToPhoneNumber
        null,//institutionContactName
        null,//institutionContactPhoneNumber
        null//institutionContactEmailAddress
    ),
    ;
    
    private Integer purchaseOrderIdentifier;
    private Date shipmentReceivedDate;
    private String shipmentPackingSlipNumber;
    private String carrierCode;
    private String shipmentBillOfLadingNumber;
    
    private String shipmentReferenceNumber;
    private String shipmentWeight;
    private Integer noOfCartons;
    private String trackingNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
    private String vendorAddressInternationalProvinceName;
    private String vendorNoteText;
    
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    
    private String deliveryBuildingCode;
    private String deliveryBuildingName;
    private String deliveryBuildingRoomNumber;
    private String deliveryBuildingLine1Address;
    private String deliveryBuildingLine2Address;
    private String deliveryCityName;
    private String deliveryStateCode;
    private String deliveryPostalCode;
    private String deliveryCountryCode;
    private String deliveryCampusCode;
    private String deliveryInstructionText;
    private String deliveryAdditionalInstructionText;
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    
    private String institutionContactName;
    private String institutionContactPhoneNumber;
    private String institutionContactEmailAddress;
    
    
    private BulkReceivingDocumentFixture(Integer purchaseOrderIdentifier,
                                         Date shipmentReceivedDate,
                                         String shipmentPackingSlipNumber,
                                         String carrierCode,
                                         String shipmentBillOfLadingNumber,
                                        
                                         String shipmentReferenceNumber,
                                         String shipmentWeight,
                                         Integer noOfCartons,
                                         String trackingNumber,
                                        
                                         Integer vendorHeaderGeneratedIdentifier,
                                         Integer vendorDetailAssignedIdentifier,
                                         String vendorName,
                                         String vendorLine1Address,
                                         String vendorLine2Address,
                                         String vendorCityName,
                                         String vendorStateCode,
                                         String vendorPostalCode,
                                         String vendorCountryCode,
                                         String vendorAddressInternationalProvinceName,
                                         String vendorNoteText,
                                         
                                         Integer alternateVendorHeaderGeneratedIdentifier,
                                         Integer alternateVendorDetailAssignedIdentifier,
                                         String  alternateVendorName,
                                         
                                         String deliveryBuildingCode,
                                         String deliveryBuildingName,
                                         String deliveryBuildingRoomNumber,
                                         String deliveryBuildingLine1Address,
                                         String deliveryBuildingLine2Address,
                                         String deliveryCityName,
                                         String deliveryStateCode,
                                         String deliveryPostalCode,
                                         String deliveryCountryCode,
                                         String deliveryCampusCode,
                                         String deliveryInstructionText,
                                         String deliveryAdditionalInstructionText,
                                         String deliveryToName,
                                         String deliveryToEmailAddress,
                                         String deliveryToPhoneNumber,
                                        
                                         String institutionContactName,
                                         String institutionContactPhoneNumber,
                                         String institutionContactEmailAddress){
     
        
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
        this.shipmentReceivedDate = shipmentReceivedDate;
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
        this.carrierCode = carrierCode;
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
        
        this.shipmentReferenceNumber = shipmentReferenceNumber;
        this.shipmentWeight = shipmentWeight;
        this.noOfCartons = noOfCartons;
        this.trackingNumber = trackingNumber;
        
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorLine2Address = vendorLine2Address; 
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode; 
        this.vendorPostalCode = vendorPostalCode;
        this.vendorCountryCode = vendorCountryCode; 
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
        this.vendorNoteText = vendorNoteText;
        
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
        this.alternateVendorName = alternateVendorName;
        
        this.deliveryBuildingCode = deliveryBuildingCode;
        this.deliveryBuildingName = deliveryBuildingName;
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
        this.deliveryCityName = deliveryCityName;
        this.deliveryStateCode = deliveryStateCode;
        this.deliveryPostalCode = deliveryPostalCode;
        this.deliveryCountryCode = deliveryCountryCode;
        this.deliveryCampusCode = deliveryCampusCode;
        this.deliveryInstructionText = deliveryInstructionText;
        this.deliveryAdditionalInstructionText = deliveryAdditionalInstructionText;
        this.deliveryToName = deliveryToName;
        this.deliveryToEmailAddress = deliveryToEmailAddress;
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
        
        this.institutionContactName = institutionContactName;
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }
    
    public BulkReceivingDocument createBulkReceivingDocument(){
        BulkReceivingDocument doc;
        try {
             doc = (BulkReceivingDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), BulkReceivingDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        doc.setPurchaseOrderIdentifier(purchaseOrderIdentifier);
        doc.setShipmentReceivedDate(shipmentReceivedDate);
        doc.setShipmentReferenceNumber(shipmentReferenceNumber);
        doc.setShipmentPackingSlipNumber(shipmentPackingSlipNumber);
        doc.setCarrierCode(carrierCode);
        doc.setShipmentBillOfLadingNumber(shipmentBillOfLadingNumber);
        doc.setShipmentWeight(shipmentWeight);
        doc.setNoOfCartons(noOfCartons);
        doc.setTrackingNumber(trackingNumber);
        doc.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedIdentifier);
        doc.setVendorDetailAssignedIdentifier(vendorDetailAssignedIdentifier);
        doc.setVendorName(vendorName);
        doc.setVendorLine1Address(vendorLine1Address);
        doc.setVendorLine2Address(vendorLine2Address);
        doc.setVendorCityName(vendorCityName);
        doc.setVendorStateCode(vendorStateCode);
        doc.setVendorPostalCode(vendorPostalCode);
        doc.setVendorCountryCode(vendorCountryCode);
        doc.setVendorAddressInternationalProvinceName(vendorAddressInternationalProvinceName);
        doc.setVendorNoteText(vendorNoteText);
        doc.setAlternateVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedIdentifier);
        doc.setAlternateVendorDetailAssignedIdentifier(alternateVendorDetailAssignedIdentifier);
        doc.setAlternateVendorName(alternateVendorName);
        doc.setDeliveryBuildingCode(deliveryBuildingCode);
        doc.setDeliveryBuildingName(deliveryBuildingName);
        doc.setDeliveryBuildingRoomNumber(deliveryBuildingRoomNumber);
        doc.setDeliveryBuildingLine1Address(deliveryBuildingLine1Address);
        doc.setDeliveryBuildingLine2Address(deliveryBuildingLine2Address);
        doc.setDeliveryCityName(deliveryCityName);
        doc.setDeliveryStateCode(deliveryStateCode);
        doc.setDeliveryPostalCode(deliveryPostalCode);
        doc.setDeliveryCountryCode(deliveryCountryCode);
        doc.setDeliveryCampusCode(deliveryCampusCode);
        doc.setDeliveryInstructionText(deliveryInstructionText);
        doc.setDeliveryAdditionalInstructionText(deliveryAdditionalInstructionText);
        doc.setDeliveryToName(deliveryToName);
        doc.setDeliveryToEmailAddress(deliveryToEmailAddress);
        doc.setDeliveryToPhoneNumber(deliveryToPhoneNumber);
        
        return doc;
    }
    
    public BulkReceivingDocument createBulkReceivingDocument(PurchaseOrderDocument poDoc){
        BulkReceivingDocument doc;
        try {
             doc = (BulkReceivingDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), BulkReceivingDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        doc.setShipmentReceivedDate(shipmentReceivedDate);
        doc.setShipmentReferenceNumber(shipmentReferenceNumber);
        doc.setShipmentPackingSlipNumber(shipmentPackingSlipNumber);
        doc.setCarrierCode(carrierCode);
        doc.setShipmentBillOfLadingNumber(shipmentBillOfLadingNumber);
        doc.setShipmentWeight(shipmentWeight);
        doc.setNoOfCartons(noOfCartons);
        doc.setTrackingNumber(trackingNumber);
        doc.setDeliveryAdditionalInstructionText(deliveryAdditionalInstructionText);
        
        doc.populateBulkReceivingFromPurchaseOrder(poDoc);
        
        return doc;
    }
    
}
