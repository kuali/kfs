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
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingStatus;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

public abstract class ReceivingDocumentBase extends FinancialSystemTransactionalDocumentBase implements ReceivingDocument {

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
    private boolean deliveryBuildingOtherIndicator;
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
    private String lineItemReceivingStatusCode;
    private String lineItemReceivingStatusDescription;

    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    
    //not persisted in db
    private String vendorNumber;
    private Integer vendorAddressGeneratedIdentifier;
    private String alternateVendorNumber;
    private boolean sensitive;

    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
    private LineItemReceivingStatus lineItemReceivingStatus;
    private Integer purchaseOrderIdentifier;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private transient PurchaseOrderDocument purchaseOrderDocument;

    private transient PurApRelatedViews relatedViews;

    public ReceivingDocumentBase(){
        super();           
    }
        
    public boolean isSensitive() {
        List<SensitiveData> sensitiveData = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByRelatedDocId(getAccountsPayablePurchasingDocumentLinkIdentifier());
        if (ObjectUtils.isNotNull(sensitiveData) && !sensitiveData.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getCarrierCode() { 
        return carrierCode;
    }
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getShipmentPackingSlipNumber() { 
        return shipmentPackingSlipNumber;
    }

    public void setShipmentPackingSlipNumber(String shipmentPackingSlipNumber) {
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
    }

    public String getShipmentReferenceNumber() { 
        return shipmentReferenceNumber;
    }

    public void setShipmentReferenceNumber(String shipmentReferenceNumber) {
        this.shipmentReferenceNumber = shipmentReferenceNumber;
    }

    public String getShipmentBillOfLadingNumber() { 
        return shipmentBillOfLadingNumber;
    }

    public void setShipmentBillOfLadingNumber(String shipmentBillOfLadingNumber) {
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
    }

    public Date getShipmentReceivedDate() { 
        return shipmentReceivedDate;
    }

    public void setShipmentReceivedDate(Date shipmentReceivedDate) {
        this.shipmentReceivedDate = shipmentReceivedDate;
    }

    public Integer getVendorHeaderGeneratedIdentifier() { 
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() { 
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorName() { 
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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

    public String getVendorCityName() { 
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorStateCode() { 
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorPostalCode() { 
        return vendorPostalCode;
    }

    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    public String getVendorCountryCode() { 
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getDeliveryCampusCode() { 
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getDeliveryBuildingCode() { 
        return deliveryBuildingCode;
    }

    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
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

    public String getDeliveryCityName() { 
        return deliveryCityName;
    }

    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    public String getDeliveryStateCode() { 
        return deliveryStateCode;
    }

    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    public String getDeliveryPostalCode() { 
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getDeliveryCountryCode() { 
        return deliveryCountryCode;
    }

    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    public String getDeliveryCountryName() {
        Country country = SpringContext.getBean(CountryService.class).getByPrimaryId(getDeliveryCountryCode());
        if (country != null)
            return country.getPostalCountryName();
        return null;
    }
       
    public String getDeliveryToName() { 
        return deliveryToName;
    }

    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    public String getDeliveryToEmailAddress() { 
        return deliveryToEmailAddress;
    }

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    public String getDeliveryToPhoneNumber() { 
        return deliveryToPhoneNumber;
    }

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    public Date getDeliveryRequiredDate() { 
        return deliveryRequiredDate;
    }

    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    public String getDeliveryInstructionText() { 
        return deliveryInstructionText;
    }

    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    public String getDeliveryRequiredDateReasonCode() { 
        return deliveryRequiredDateReasonCode;
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    public Campus getDeliveryCampus() {
        return deliveryCampus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, deliveryCampus, "deliveryCampus");
    }

    /**
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    /**
     * @deprecated
     */
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    /**
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    public Country getVendorCountry() {
        vendorCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, vendorCountryCode, vendorCountry);
        return vendorCountry;
    }

    /**
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
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

    public boolean isDeliveryBuildingOtherIndicator() {
        return deliveryBuildingOtherIndicator;
    }

    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator) {
        this.deliveryBuildingOtherIndicator = deliveryBuildingOtherIndicator;
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if(!(this instanceof BulkReceivingDocument)){
            if(this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                //delete unentered items and update po totals and save po
                SpringContext.getBean(ReceivingService.class).completeReceivingDocument(this);
            }
        }
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
            KimPrincipal principal = SpringContext.getBean(IdentityManagementService.class).getPrincipalByPrincipalName(userNetworkId);
            workflowDocument.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, currentNodeName, annotationNote, principal.getPrincipalId(), responsibilityNote, true);
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
    
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    public LineItemReceivingStatus getLineItemReceivingStatus() {
        return lineItemReceivingStatus;
    }

    public void setLineItemReceivingStatus(LineItemReceivingStatus receivingLineStatus) {
        this.lineItemReceivingStatus = receivingLineStatus;
    }

    public String getLineItemReceivingStatusCode() {
        return lineItemReceivingStatusCode;
    }

    public void setLineItemReceivingStatusCode(String lineItemReceivingStatusCode) {
        this.lineItemReceivingStatusCode = lineItemReceivingStatusCode;
    }

    public String getLineItemReceivingStatusDescription() {
        return lineItemReceivingStatusDescription;
    }

    public void setLineItemReceivingStatusDescription(String lineItemReceivingStatusDescription) {
        this.lineItemReceivingStatusDescription = lineItemReceivingStatusDescription;
    }
    
    /**
     * Always returns false. 
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    public boolean getIsATypeOfPurDoc() {
        return false;
    }
    
    /**
     * Always returns false. 
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    public boolean getIsATypeOfPODoc() {
        return false;
    }
    
    /**
     * Always returns false. 
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    public boolean getIsPODoc() {
        return false;
    }
    
    /**
     * Always returns false. 
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    public boolean getIsReqsDoc() {
        return false;
    }       
    
    public String getWorkflowStatusForResult(){
        if (StringUtils.equals(KFSConstants.DocumentStatusCodes.INITIATED,getDocumentHeader().getFinancialDocumentStatusCode())){
            return "INITIATED";
        }else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.ENROUTE,getDocumentHeader().getFinancialDocumentStatusCode())){
            return "ENROUTE";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.DISAPPROVED,getDocumentHeader().getFinancialDocumentStatusCode())){
            return "DISAPPROVED";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.CANCELLED,getDocumentHeader().getFinancialDocumentStatusCode())){
            return "CANCELLED";
        } else if (StringUtils.equals(KFSConstants.DocumentStatusCodes.APPROVED,getDocumentHeader().getFinancialDocumentStatusCode())){
            return "APPROVED";
        }else{
            return StringUtils.EMPTY;
        }
    }
    
    public java.util.Date getCreateDateForResult() {
        return new java.util.Date(getDocumentHeader().getWorkflowDocument().getCreateDate().getTime());
    }
    
    public String getDocumentTitleForResult() throws WorkflowException{
        return SpringContext.getBean(KualiWorkflowInfo.class).getDocType(this.getDocumentHeader().getWorkflowDocument().getDocumentType()).getDocTypeLabel();
    }
    
}
