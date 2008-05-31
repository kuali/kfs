/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.KFSResourceLoader;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.Carrier;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;
import org.kuali.module.purap.util.PurApRelatedViews;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.rice.resourceloader.ServiceLocator;
import org.springframework.ui.velocity.SpringResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public abstract class ReceivingDocumentBase extends TransactionalDocumentBase implements ReceivingDocument {

    private String carrierCode;
    private String shipmentPackingSlipNumber;
    private String shipmentReferenceNumber;
    private String shipmentBillOfLadingNumber;
    private Date shipmentReceivedDate;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
    private String deliveryCampusCode;
    private String deliveryBuildingCode;
    private String deliveryBuildingName;
    private String deliveryBuildingRoomNumber;
    private String deliveryBuildingLine1Address;
    private String deliveryBuildingLine2Address;
    private String deliveryCityName;
    private String deliveryStateCode;
    private String deliveryPostalCode;
    private String deliveryCountryCode;
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    private Date deliveryRequiredDate;
    private String deliveryInstructionText;
    private String deliveryRequiredDateReasonCode;

    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    
    //not persisted in db
    private boolean deliveryBuildingOther;
    private String vendorNumber;
    private Integer vendorAddressGeneratedIdentifier;
    private String alternateVendorNumber;

    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
    private Integer purchaseOrderIdentifier;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private transient PurchaseOrderDocument purchaseOrderDocument;

    private transient PurApRelatedViews relatedViews;

    public ReceivingDocumentBase(){
        super();           
    }
        
    /**
     * Gets the carrierCode attribute.
     * 
     * @return Returns the carrierCode
     * 
     */
    public String getCarrierCode() { 
        return carrierCode;
    }

    /**
     * Sets the carrierCode attribute.
     * 
     * @param carrierCode The carrierCode to set.
     * 
     */
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }


    /**
     * Gets the shipmentPackingSlipNumber attribute.
     * 
     * @return Returns the shipmentPackingSlipNumber
     * 
     */
    public String getShipmentPackingSlipNumber() { 
        return shipmentPackingSlipNumber;
    }

    /**
     * Sets the shipmentPackingSlipNumber attribute.
     * 
     * @param shipmentPackingSlipNumber The shipmentPackingSlipNumber to set.
     * 
     */
    public void setShipmentPackingSlipNumber(String shipmentPackingSlipNumber) {
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
    }


    /**
     * Gets the shipmentReferenceNumber attribute.
     * 
     * @return Returns the shipmentReferenceNumber
     * 
     */
    public String getShipmentReferenceNumber() { 
        return shipmentReferenceNumber;
    }

    /**
     * Sets the shipmentReferenceNumber attribute.
     * 
     * @param shipmentReferenceNumber The shipmentReferenceNumber to set.
     * 
     */
    public void setShipmentReferenceNumber(String shipmentReferenceNumber) {
        this.shipmentReferenceNumber = shipmentReferenceNumber;
    }


    /**
     * Gets the shipmentBillOfLadingNumber attribute.
     * 
     * @return Returns the shipmentBillOfLadingNumber
     * 
     */
    public String getShipmentBillOfLadingNumber() { 
        return shipmentBillOfLadingNumber;
    }

    /**
     * Sets the shipmentBillOfLadingNumber attribute.
     * 
     * @param shipmentBillOfLadingNumber The shipmentBillOfLadingNumber to set.
     * 
     */
    public void setShipmentBillOfLadingNumber(String shipmentBillOfLadingNumber) {
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
    }


    /**
     * Gets the shipmentReceivedDate attribute.
     * 
     * @return Returns the shipmentReceivedDate
     * 
     */
    public Date getShipmentReceivedDate() { 
        return shipmentReceivedDate;
    }

    /**
     * Sets the shipmentReceivedDate attribute.
     * 
     * @param shipmentReceivedDate The shipmentReceivedDate to set.
     * 
     */
    public void setShipmentReceivedDate(Date shipmentReceivedDate) {
        this.shipmentReceivedDate = shipmentReceivedDate;
    }


    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getVendorHeaderGeneratedIdentifier() { 
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     * 
     */
    public Integer getVendorDetailAssignedIdentifier() { 
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     * 
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     * 
     */
    public String getVendorName() { 
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     * 
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address() { 
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }


    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address() { 
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }


    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     * 
     */
    public String getVendorCityName() { 
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }


    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode() { 
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }


    /**
     * Gets the vendorPostalCode attribute.
     * 
     * @return Returns the vendorPostalCode
     * 
     */
    public String getVendorPostalCode() { 
        return vendorPostalCode;
    }

    /**
     * Sets the vendorPostalCode attribute.
     * 
     * @param vendorPostalCode The vendorPostalCode to set.
     * 
     */
    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }


    /**
     * Gets the vendorCountryCode attribute.
     * 
     * @return Returns the vendorCountryCode
     * 
     */
    public String getVendorCountryCode() { 
        return vendorCountryCode;
    }

    /**
     * Sets the vendorCountryCode attribute.
     * 
     * @param vendorCountryCode The vendorCountryCode to set.
     * 
     */
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }


    /**
     * Gets the deliveryCampusCode attribute.
     * 
     * @return Returns the deliveryCampusCode
     * 
     */
    public String getDeliveryCampusCode() { 
        return deliveryCampusCode;
    }

    /**
     * Sets the deliveryCampusCode attribute.
     * 
     * @param deliveryCampusCode The deliveryCampusCode to set.
     * 
     */
    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }


    /**
     * Gets the deliveryBuildingCode attribute.
     * 
     * @return Returns the deliveryBuildingCode
     * 
     */
    public String getDeliveryBuildingCode() { 
        return deliveryBuildingCode;
    }

    /**
     * Sets the deliveryBuildingCode attribute.
     * 
     * @param deliveryBuildingCode The deliveryBuildingCode to set.
     * 
     */
    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }


    /**
     * Gets the deliveryBuildingName attribute.
     * 
     * @return Returns the deliveryBuildingName
     * 
     */
    public String getDeliveryBuildingName() { 
        return deliveryBuildingName;
    }

    /**
     * Sets the deliveryBuildingName attribute.
     * 
     * @param deliveryBuildingName The deliveryBuildingName to set.
     * 
     */
    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }


    /**
     * Gets the deliveryBuildingRoomNumber attribute.
     * 
     * @return Returns the deliveryBuildingRoomNumber
     * 
     */
    public String getDeliveryBuildingRoomNumber() { 
        return deliveryBuildingRoomNumber;
    }

    /**
     * Sets the deliveryBuildingRoomNumber attribute.
     * 
     * @param deliveryBuildingRoomNumber The deliveryBuildingRoomNumber to set.
     * 
     */
    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }


    /**
     * Gets the deliveryBuildingLine1Address attribute.
     * 
     * @return Returns the deliveryBuildingLine1Address
     * 
     */
    public String getDeliveryBuildingLine1Address() { 
        return deliveryBuildingLine1Address;
    }

    /**
     * Sets the deliveryBuildingLine1Address attribute.
     * 
     * @param deliveryBuildingLine1Address The deliveryBuildingLine1Address to set.
     * 
     */
    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }


    /**
     * Gets the deliveryBuildingLine2Address attribute.
     * 
     * @return Returns the deliveryBuildingLine2Address
     * 
     */
    public String getDeliveryBuildingLine2Address() { 
        return deliveryBuildingLine2Address;
    }

    /**
     * Sets the deliveryBuildingLine2Address attribute.
     * 
     * @param deliveryBuildingLine2Address The deliveryBuildingLine2Address to set.
     * 
     */
    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }


    /**
     * Gets the deliveryCityName attribute.
     * 
     * @return Returns the deliveryCityName
     * 
     */
    public String getDeliveryCityName() { 
        return deliveryCityName;
    }

    /**
     * Sets the deliveryCityName attribute.
     * 
     * @param deliveryCityName The deliveryCityName to set.
     * 
     */
    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }


    /**
     * Gets the deliveryStateCode attribute.
     * 
     * @return Returns the deliveryStateCode
     * 
     */
    public String getDeliveryStateCode() { 
        return deliveryStateCode;
    }

    /**
     * Sets the deliveryStateCode attribute.
     * 
     * @param deliveryStateCode The deliveryStateCode to set.
     * 
     */
    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }


    /**
     * Gets the deliveryPostalCode attribute.
     * 
     * @return Returns the deliveryPostalCode
     * 
     */
    public String getDeliveryPostalCode() { 
        return deliveryPostalCode;
    }

    /**
     * Sets the deliveryPostalCode attribute.
     * 
     * @param deliveryPostalCode The deliveryPostalCode to set.
     * 
     */
    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }


    /**
     * Gets the deliveryCountryCode attribute.
     * 
     * @return Returns the deliveryCountryCode
     * 
     */
    public String getDeliveryCountryCode() { 
        return deliveryCountryCode;
    }

    /**
     * Sets the deliveryCountryCode attribute.
     * 
     * @param deliveryCountryCode The deliveryCountryCode to set.
     * 
     */
    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }


    /**
     * Gets the deliveryToName attribute.
     * 
     * @return Returns the deliveryToName
     * 
     */
    public String getDeliveryToName() { 
        return deliveryToName;
    }

    /**
     * Sets the deliveryToName attribute.
     * 
     * @param deliveryToName The deliveryToName to set.
     * 
     */
    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }


    /**
     * Gets the deliveryToEmailAddress attribute.
     * 
     * @return Returns the deliveryToEmailAddress
     * 
     */
    public String getDeliveryToEmailAddress() { 
        return deliveryToEmailAddress;
    }

    /**
     * Sets the deliveryToEmailAddress attribute.
     * 
     * @param deliveryToEmailAddress The deliveryToEmailAddress to set.
     * 
     */
    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }


    /**
     * Gets the deliveryToPhoneNumber attribute.
     * 
     * @return Returns the deliveryToPhoneNumber
     * 
     */
    public String getDeliveryToPhoneNumber() { 
        return deliveryToPhoneNumber;
    }

    /**
     * Sets the deliveryToPhoneNumber attribute.
     * 
     * @param deliveryToPhoneNumber The deliveryToPhoneNumber to set.
     * 
     */
    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }


    /**
     * Gets the deliveryRequiredDate attribute.
     * 
     * @return Returns the deliveryRequiredDate
     * 
     */
    public Date getDeliveryRequiredDate() { 
        return deliveryRequiredDate;
    }

    /**
     * Sets the deliveryRequiredDate attribute.
     * 
     * @param deliveryRequiredDate The deliveryRequiredDate to set.
     * 
     */
    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }


    /**
     * Gets the deliveryInstructionText attribute.
     * 
     * @return Returns the deliveryInstructionText
     * 
     */
    public String getDeliveryInstructionText() { 
        return deliveryInstructionText;
    }

    /**
     * Sets the deliveryInstructionText attribute.
     * 
     * @param deliveryInstructionText The deliveryInstructionText to set.
     * 
     */
    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }


    /**
     * Gets the deliveryRequiredDateReasonCode attribute.
     * 
     * @return Returns the deliveryRequiredDateReasonCode
     * 
     */
    public String getDeliveryRequiredDateReasonCode() { 
        return deliveryRequiredDateReasonCode;
    }

    /**
     * Sets the deliveryRequiredDateReasonCode attribute.
     * 
     * @param deliveryRequiredDateReasonCode The deliveryRequiredDateReasonCode to set.
     * 
     */
    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    /**
     * Gets the deliveryCampus attribute.
     * 
     * @return Returns the deliveryCampus
     * 
     */
    public Campus getDeliveryCampus() { 
        return deliveryCampus;
    }

    /**
     * Sets the deliveryCampus attribute.
     * 
     * @param deliveryCampus The deliveryCampus to set.
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    /**
     * Gets the carrier attribute. 
     * @return Returns the carrier.
     */
    public Carrier getCarrier() {
        return carrier;
    }

    /**
     * Sets the carrier attribute value.
     * @param carrier The carrier to set.
     * @deprecated
     */
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    /**
     * Gets the deliveryRequiredDateReason attribute. 
     * @return Returns the deliveryRequiredDateReason.
     */
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    /**
     * Sets the deliveryRequiredDateReason attribute value.
     * @param deliveryRequiredDateReason The deliveryRequiredDateReason to set.
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * Gets the vendorCountry attribute. 
     * @return Returns the vendorCountry.
     */
    public Country getVendorCountry() {
        return vendorCountry;
    }

    /**
     * Sets the vendorCountry attribute value.
     * @param vendorCountry The vendorCountry to set.
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    /**
     * Gets the vendorDetail attribute. 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * @param vendorDetail The vendorDetail to set.
     * @deprecated
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public String getVendorNumber() {
        if (StringUtils.isNotEmpty(vendorNumber)) {
            return vendorNumber;
        }
        else if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }
        else
            return "";
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
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

    public String getAlternateVendorNumber() {
        return alternateVendorNumber;
    }

    public void setAlternateVendorNumber(String alternateVendorNumber) {
        this.alternateVendorNumber = alternateVendorNumber;
    }

    public boolean isDeliveryBuildingOther() {
        return deliveryBuildingOther;
    }

    public void setDeliveryBuildingOther(boolean deliveryBuildingOther) {
        this.deliveryBuildingOther = deliveryBuildingOther;
    }

    public abstract AccountsPayableDocumentSpecificService getDocumentSpecificService();
    
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if(this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            //delete unentered items and update po totals and save po
            SpringContext.getBean(ReceivingService.class).completeReceivingDocument(this);
        }
    }
    
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.getItems());
        return managedLists;
    }

    public abstract Class getItemClass();

    public Integer getPurchaseOrderIdentifier() { 
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ((ObjectUtils.isNull(this.purchaseOrderDocument) || ObjectUtils.isNull(this.purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return this.purchaseOrderDocument;
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            this.purchaseOrderDocument = null;
        }
        else {
            if (ObjectUtils.isNotNull(purchaseOrderDocument.getPurapDocumentIdentifier())) {
                setPurchaseOrderIdentifier(purchaseOrderDocument.getPurapDocumentIdentifier());
            }
            this.purchaseOrderDocument = purchaseOrderDocument;
        }
    }

    public PurApRelatedViews getRelatedViews() {
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
        return relatedViews;
    }

    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }

    public void initiateDocument(){
        //initiate code
    }
    /**
     * FIXME: this is same as PurchaseOrderDoc please move somewhere appropriate
     * Sends FYI workflow request to the given user on this document.
     * 
     * @param workflowDocument the associated workflow document.
     * @param userNetworkId the network ID of the user to be sent to.
     * @param annotation the annotation notes contained in this document.
     * @param responsibility the responsibility specified in the request.
     * @throws WorkflowException
     */
    public void appSpecificRouteDocumentToUser(KualiWorkflowDocument workflowDocument, String userNetworkId, String annotation, String responsibility) throws WorkflowException {
        if (ObjectUtils.isNotNull(workflowDocument)) {
            String annotationNote = (ObjectUtils.isNull(annotation)) ? "" : annotation;
            String responsibilityNote = (ObjectUtils.isNull(responsibility)) ? "" : responsibility;
            String currentNodeName = getCurrentRouteNodeName(workflowDocument);
            workflowDocument.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, currentNodeName, 0, annotationNote, new NetworkIdVO(userNetworkId), responsibilityNote, true);
        }
    }
    /**
     * FIXME: this is same as PurchaseOrderDoc please move somewhere appropriate
     * Returns the name of the current route node.
     * 
     * @param wd the current workflow document.
     * @return the name of the current route node.
     * @throws WorkflowException
     */
    private String getCurrentRouteNodeName(KualiWorkflowDocument wd) throws WorkflowException {
        String[] nodeNames = wd.getNodeNames();
        if ((nodeNames == null) || (nodeNames.length == 0)) {
            return null;
        }
        else {
            return nodeNames[0];
        }
    }
}
