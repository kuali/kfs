/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.document.validation.event.ContinuePurapEvent;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

public class BulkReceivingDocument extends FinancialSystemTransactionalDocumentBase {

    private Integer purchaseOrderIdentifier;
    private Date shipmentReceivedDate;
    private String shipmentPackingSlipNumber;
    private String carrierCode;
    private String shipmentBillOfLadingNumber;
    
    private String goodsDeliveredVendorNumber;
    private String shipmentReferenceNumber;
    private Integer noOfCartons;
    /**
     * Primary Vendor
     */
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
    
    /**
     * Alternate Vendor
     */
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    
    /**
     * Delivery Information
     */
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
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    
    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
    
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    private transient PurApRelatedViews relatedViews;
    
    /**
     * Not persisted in DB
     */
    private String vendorNumber;
    private String alternateVendorNumber;
    private String vendorContactsLabel;
    private boolean isPOAvailable;
    
    private String vendorDetailsForDisplay;
    private String alternateVendorDetailsForDisplay;
    
    public BulkReceivingDocument() {
        super();
    }

    public void initiateDocument(){
        this.setShipmentReceivedDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }
    
    public void populateBulkReceivingFromPurchaseOrder(PurchaseOrderDocument po){
        
        this.setPurchaseOrderIdentifier( po.getPurapDocumentIdentifier() );
        this.getDocumentHeader().setOrganizationDocumentNumber( po.getDocumentHeader().getOrganizationDocumentNumber() );
        this.setAccountsPayablePurchasingDocumentLinkIdentifier( po.getAccountsPayablePurchasingDocumentLinkIdentifier() );
        
        //copy vendor
        this.setVendorHeaderGeneratedIdentifier( po.getVendorHeaderGeneratedIdentifier() );
        this.setVendorDetailAssignedIdentifier( po.getVendorDetailAssignedIdentifier() );        
        this.setVendorName( po.getVendorName() );
        this.setVendorNumber( po.getVendorNumber() );
        this.setVendorLine1Address( po.getVendorLine1Address() );
        this.setVendorLine2Address( po.getVendorLine2Address() );
        this.setVendorCityName( po.getVendorCityName() );
        this.setVendorStateCode( po.getVendorStateCode() );
        this.setVendorPostalCode( po.getVendorPostalCode() );
        this.setVendorCountryCode( po.getVendorCountryCode() );
        
        //copy alternate vendor
        this.setAlternateVendorName( po.getAlternateVendorName() );
        this.setAlternateVendorNumber( po.getAlternateVendorNumber() );
        this.setAlternateVendorDetailAssignedIdentifier( po.getAlternateVendorDetailAssignedIdentifier() );
        this.setAlternateVendorHeaderGeneratedIdentifier( po.getAlternateVendorHeaderGeneratedIdentifier() );
        
        //copy delivery
        this.setDeliveryBuildingCode( po.getDeliveryBuildingCode() );
        this.setDeliveryBuildingLine1Address( po.getDeliveryBuildingLine1Address() );
        this.setDeliveryBuildingLine2Address( po.getDeliveryBuildingLine2Address() );
        this.setDeliveryBuildingName( po.getDeliveryBuildingName() );        
        this.setDeliveryBuildingRoomNumber( po.getDeliveryBuildingRoomNumber() );
        this.setDeliveryCampusCode( po.getDeliveryCampusCode() );
        this.setDeliveryCityName( po.getDeliveryCityName() );
        this.setDeliveryCountryCode( po.getDeliveryCountryCode() );
        this.setDeliveryInstructionText( po.getDeliveryInstructionText() );
        this.setDeliveryPostalCode( po.getDeliveryPostalCode() );
        this.setDeliveryStateCode( po.getDeliveryStateCode() );
        this.setDeliveryToEmailAddress( po.getDeliveryToEmailAddress() );
        this.setDeliveryToName( po.getDeliveryToName() );
        this.setDeliveryToPhoneNumber( po.getDeliveryToPhoneNumber() );
                
        /**
         * FIXME: For testing. Needs refactor
         */
        StringBuffer vendorDetails = new StringBuffer();
        vendorDetails.append(getVendorName() + "\n");
        vendorDetails.append(StringUtils.defaultString(getVendorLine1Address()) + " " + StringUtils.defaultString(getVendorLine2Address()) + "\n");
        vendorDetails.append(StringUtils.defaultString(getVendorCityName()) + ", " + StringUtils.defaultString(getVendorStateCode()) + " " + StringUtils.defaultString(getVendorPostalCode()) + " " + StringUtils.defaultString(getVendorCountryCode()));
        setVendorDetailsForDisplay(vendorDetails.toString());
        
        StringBuffer alternateVendorDetails = new StringBuffer();
        alternateVendorDetails.append(getAlternateVendorName() + "\n");
        alternateVendorDetails.append(getAlternateVendorNumber());
        setAlternateVendorDetailsForDisplay(alternateVendorDetails.toString());
        
        populateDocumentDescription(po);
    }
        
    /**
     * Perform logic needed to clear the initial fields on a Receiving Line Document
     */
    public void clearInitFields() {
        // Clearing document overview fields
        getDocumentHeader().setDocumentDescription(null);
        getDocumentHeader().setExplanation(null);
        getDocumentHeader().setFinancialDocumentTotalAmount(null);
        getDocumentHeader().setOrganizationDocumentNumber(null);

        setPurchaseOrderIdentifier(null);
        setPOAvailable(false);
        
        setShipmentReceivedDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        setShipmentPackingSlipNumber(null);
        setShipmentBillOfLadingNumber(null);
        setCarrierCode(null);        
    }

    
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // first populate, then call super
        if (event instanceof ContinuePurapEvent) {
            SpringContext.getBean(BulkReceivingService.class).populateBulkReceivingFromPurchaseOrder(this);
        }
        
        super.prepareForSave(event);
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    private void populateDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName();
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        getDocumentHeader().setDocumentDescription(description);
    }

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public String getAlternateVendorName() {
        return alternateVendorName;
    }

    public void setAlternateVendorName(String alternateVendorName) {
        this.alternateVendorName = alternateVendorName;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getDeliveryBuildingCode() {
        return deliveryBuildingCode;
    }

    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    public String getDeliveryBuildingLine1Address() {
        return deliveryBuildingLine1Address;
    }

    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    public String getDeliveryBuildingLine2Address() {
        return deliveryBuildingLine2Address;
    }

    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    public String getDeliveryBuildingName() {
        return deliveryBuildingName;
    }

    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    public String getDeliveryBuildingRoomNumber() {
        return deliveryBuildingRoomNumber;
    }

    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    public Campus getDeliveryCampus() {
        return deliveryCampus;
    }

    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getDeliveryCityName() {
        return deliveryCityName;
    }

    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    public String getDeliveryCountryCode() {
        return deliveryCountryCode;
    }

    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    public String getDeliveryInstructionText() {
        return deliveryInstructionText;
    }

    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    public String getDeliveryStateCode() {
        return deliveryStateCode;
    }

    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    public String getDeliveryToEmailAddress() {
        return deliveryToEmailAddress;
    }

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    public String getDeliveryToName() {
        return deliveryToName;
    }

    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    public String getDeliveryToPhoneNumber() {
        return deliveryToPhoneNumber;
    }

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    public String getGoodsDeliveredVendorNumber() {
        return goodsDeliveredVendorNumber;
    }

    public void setGoodsDeliveredVendorNumber(String goodsDeliveredVendorNumber) {
        this.goodsDeliveredVendorNumber = goodsDeliveredVendorNumber;
    }

    public Integer getNoOfCartons() {
        return noOfCartons;
    }

    public void setNoOfCartons(Integer noOfCartons) {
        this.noOfCartons = noOfCartons;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public PurApRelatedViews getRelatedViews() {
        return relatedViews;
    }

    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }

    public String getShipmentBillOfLadingNumber() {
        return shipmentBillOfLadingNumber;
    }

    public void setShipmentBillOfLadingNumber(String shipmentBillOfLadingNumber) {
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
    }

    public String getShipmentPackingSlipNumber() {
        return shipmentPackingSlipNumber;
    }

    public void setShipmentPackingSlipNumber(String shipmentPackingSlipNumber) {
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
    }

    public Date getShipmentReceivedDate() {
        return shipmentReceivedDate;
    }

    public void setShipmentReceivedDate(Date shipmentReceivedDate) {
        this.shipmentReceivedDate = shipmentReceivedDate;
    }

    public String getShipmentReferenceNumber() {
        return shipmentReferenceNumber;
    }

    public void setShipmentReferenceNumber(String shipmentReferenceNumber) {
        this.shipmentReferenceNumber = shipmentReferenceNumber;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public Country getVendorCountry() {
        return vendorCountry;
    }

    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getAlternateVendorNumber() {
        return alternateVendorNumber;
    }

    public void setAlternateVendorNumber(String alternateVendorNumber) {
        this.alternateVendorNumber = alternateVendorNumber;
    }

    public String getVendorContactsLabel() {
        return vendorContactsLabel;
    }

    public void setVendorContactsLabel(String vendorContactsLabel) {
        this.vendorContactsLabel = vendorContactsLabel;
    }
 
    public boolean isPOAvailable() {
        return isPOAvailable;
    }

    public void setPOAvailable(boolean isPOAvailable) {
        this.isPOAvailable = isPOAvailable;
    }

    public String getVendorDetailsForDisplay() {
        return vendorDetailsForDisplay;
    }

    public void setVendorDetailsForDisplay(String vendorDetailsForDisplay) {
        this.vendorDetailsForDisplay = vendorDetailsForDisplay;
    }
    
    public String getAlternateVendorDetailsForDisplay() {
        return vendorDetailsForDisplay;
    }

    public void setAlternateVendorDetailsForDisplay(String alternateVendorDetailsForDisplay) {
        this.alternateVendorDetailsForDisplay = alternateVendorDetailsForDisplay;
    }
}
