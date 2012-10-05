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
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.module.purap.util.PurapSearchUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.framework.country.CountryEbo;

public abstract class ReceivingDocumentBase extends FinancialSystemTransactionalDocumentBase implements ReceivingDocument {

    protected String carrierCode;
    protected String shipmentPackingSlipNumber;
    protected String shipmentReferenceNumber;
    protected String shipmentBillOfLadingNumber;
    protected Date shipmentReceivedDate;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorName;
    protected String vendorLine1Address;
    protected String vendorLine2Address;
    protected String vendorCityName;
    protected String vendorStateCode;
    protected String vendorPostalCode;
    protected String vendorCountryCode;
    protected String deliveryCampusCode;
    protected boolean deliveryBuildingOtherIndicator;
    protected String deliveryBuildingCode;
    protected String deliveryBuildingName;
    protected String deliveryBuildingRoomNumber;
    protected String deliveryBuildingLine1Address;
    protected String deliveryBuildingLine2Address;
    protected String deliveryCityName;
    protected String deliveryStateCode;
    protected String deliveryPostalCode;
    protected String deliveryCountryCode;
    protected String deliveryToName;
    protected String deliveryToEmailAddress;
    protected String deliveryToPhoneNumber;
    protected Date deliveryRequiredDate;
    protected String deliveryInstructionText;
    protected String deliveryRequiredDateReasonCode;

    protected Integer alternateVendorHeaderGeneratedIdentifier;
    protected Integer alternateVendorDetailAssignedIdentifier;
    protected String alternateVendorName;

    //not persisted in db
    protected String vendorNumber;
    protected Integer vendorAddressGeneratedIdentifier;
    protected String alternateVendorNumber;
    protected boolean sensitive;

    protected CampusParameter deliveryCampus;
    protected CountryEbo vendorCountry;
    protected Carrier carrier;
    protected VendorDetail vendorDetail;
    protected DeliveryRequiredDateReason deliveryRequiredDateReason;
    protected Integer purchaseOrderIdentifier;
    protected Integer accountsPayablePurchasingDocumentLinkIdentifier;
    protected transient PurchaseOrderDocument purchaseOrderDocument;

    protected transient PurApRelatedViews relatedViews;

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

    @Override
    public String getCarrierCode() {
        return carrierCode;
    }
    @Override
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    @Override
    public String getShipmentPackingSlipNumber() {
        return shipmentPackingSlipNumber;
    }

    @Override
    public void setShipmentPackingSlipNumber(String shipmentPackingSlipNumber) {
        this.shipmentPackingSlipNumber = shipmentPackingSlipNumber;
    }

    @Override
    public String getShipmentReferenceNumber() {
        return shipmentReferenceNumber;
    }

    @Override
    public void setShipmentReferenceNumber(String shipmentReferenceNumber) {
        this.shipmentReferenceNumber = shipmentReferenceNumber;
    }

    @Override
    public String getShipmentBillOfLadingNumber() {
        return shipmentBillOfLadingNumber;
    }

    @Override
    public void setShipmentBillOfLadingNumber(String shipmentBillOfLadingNumber) {
        this.shipmentBillOfLadingNumber = shipmentBillOfLadingNumber;
    }

    @Override
    public Date getShipmentReceivedDate() {
        return shipmentReceivedDate;
    }

    @Override
    public void setShipmentReceivedDate(Date shipmentReceivedDate) {
        this.shipmentReceivedDate = shipmentReceivedDate;
    }

    @Override
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    @Override
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    @Override
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    @Override
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    @Override
    public String getVendorName() {
        return vendorName;
    }

    @Override
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Override
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    @Override
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    @Override
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    @Override
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    @Override
    public String getVendorCityName() {
        return vendorCityName;
    }

    @Override
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    @Override
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    @Override
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    @Override
    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    @Override
    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    @Override
    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    @Override
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    @Override
    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    @Override
    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    @Override
    public String getDeliveryBuildingCode() {
        return deliveryBuildingCode;
    }

    @Override
    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    @Override
    public String getDeliveryBuildingName() {
        return deliveryBuildingName;
    }

    @Override
    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    @Override
    public String getDeliveryBuildingRoomNumber() {
        return deliveryBuildingRoomNumber;
    }

    @Override
    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    @Override
    public String getDeliveryBuildingLine1Address() {
        return deliveryBuildingLine1Address;
    }

    @Override
    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    @Override
    public String getDeliveryBuildingLine2Address() {
        return deliveryBuildingLine2Address;
    }

    @Override
    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    @Override
    public String getDeliveryCityName() {
        return deliveryCityName;
    }

    @Override
    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    @Override
    public String getDeliveryStateCode() {
        return deliveryStateCode;
    }

    @Override
    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    @Override
    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    @Override
    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    @Override
    public String getDeliveryCountryCode() {
        return deliveryCountryCode;
    }

    @Override
    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    @Override
    public String getDeliveryCountryName() {
        if ( StringUtils.isNotBlank(getDeliveryCountryCode()) ) {
            Country country = SpringContext.getBean(CountryService.class).getCountry(getDeliveryCountryCode());
            if (country != null) {
                return country.getName();
            }
        }
        return null;
    }

    @Override
    public String getDeliveryToName() {
        return deliveryToName;
    }

    @Override
    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    @Override
    public String getDeliveryToEmailAddress() {
        return deliveryToEmailAddress;
    }

    @Override
    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    @Override
    public String getDeliveryToPhoneNumber() {
        return deliveryToPhoneNumber;
    }

    @Override
    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    @Override
    public Date getDeliveryRequiredDate() {
        return deliveryRequiredDate;
    }

    @Override
    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    @Override
    public String getDeliveryInstructionText() {
        return deliveryInstructionText;
    }

    @Override
    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    @Override
    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    @Override
    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    @Override
    public CampusParameter getDeliveryCampus() {
        return deliveryCampus;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setDeliveryCampus(CampusParameter deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    @Override
    public Carrier getCarrier() {
        return carrier;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @Override
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * Gets the vendorCountryCode attribute.
     *
     * @return Returns the vendorCountryCode.
     */
    @Override
    public CountryEbo getVendorCountry() {
        if ( StringUtils.isBlank(vendorCountryCode) ) {
            vendorCountry = null;
        } else {
            if ( vendorCountry == null || !StringUtils.equals( vendorCountry.getCode(), vendorCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, vendorCountryCode);
                    vendorCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }

        return vendorCountry;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setVendorCountry(CountryEbo vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    @Override
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    @Override
    public String getVendorNumber() {
        if (StringUtils.isNotEmpty(vendorNumber)) {
            return vendorNumber;
        }
        else if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }
        else {
            return "";
        }
    }

    @Override
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    @Override
    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    @Override
    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    @Override
    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    @Override
    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    @Override
    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    @Override
    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    @Override
    public String getAlternateVendorName() {
        return alternateVendorName;
    }

    @Override
    public void setAlternateVendorName(String alternateVendorName) {
        this.alternateVendorName = alternateVendorName;
    }

    @Override
    public String getAlternateVendorNumber() {
        return alternateVendorNumber;
    }

    @Override
    public void setAlternateVendorNumber(String alternateVendorNumber) {
        this.alternateVendorNumber = alternateVendorNumber;
    }

    @Override
    public boolean isDeliveryBuildingOtherIndicator() {
        return deliveryBuildingOtherIndicator;
    }

    @Override
    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator) {
        this.deliveryBuildingOtherIndicator = deliveryBuildingOtherIndicator;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if(!(this instanceof BulkReceivingDocument)){
            if(this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                //delete unentered items and update po totals and save po
                SpringContext.getBean(ReceivingService.class).completeReceivingDocument(this);
            }
        }
    }

    @Override
    public abstract Class getItemClass();

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    @Override
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    @Override
    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    @Override
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ((ObjectUtils.isNull(this.purchaseOrderDocument) || ObjectUtils.isNull(this.purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return this.purchaseOrderDocument;
    }

    @Override
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
    @Override
    public void appSpecificRouteDocumentToUser(WorkflowDocument workflowDocument, String routePrincipalId, String annotation, String responsibility) throws WorkflowException {
        if (ObjectUtils.isNotNull(workflowDocument)) {
            String annotationNote = (ObjectUtils.isNull(annotation)) ? "" : annotation;
            String responsibilityNote = (ObjectUtils.isNull(responsibility)) ? "" : responsibility;
            String currentNodeName = getCurrentRouteNodeName(workflowDocument);
            workflowDocument.adHocToPrincipal( ActionRequestType.FYI, currentNodeName, annotationNote, routePrincipalId, responsibilityNote, true);
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
    protected String getCurrentRouteNodeName(WorkflowDocument wd) throws WorkflowException {
        Set<String> nodeNames = wd.getCurrentNodeNames();
        if ((nodeNames == null) || (nodeNames.size() == 0)) {
            return null;
        }
        else {
            return (String)nodeNames.toArray()[0];
        }
    }

    public boolean isBoNotesSupport() {
        return true;
    }

    @Override
    public String getAppDocStatus() {
        return this.getApplicationDocumentStatus();
    }

    @Override
    public void setAppDocStatus(String appDocStatus) {
        this.setApplicationDocumentStatus(appDocStatus);
    }

    /**
     * Always returns true.
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    @Override
    public boolean getIsATypeOfPurAPRecDoc() {
        return true;
    }

    /**
     * Always returns false.
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    @Override
    public boolean getIsATypeOfPurDoc() {
        return false;
    }

    /**
     * Always returns false.
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    @Override
    public boolean getIsATypeOfPODoc() {
        return false;
    }

    /**
     * Always returns false.
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    @Override
    public boolean getIsPODoc() {
        return false;
    }

    /**
     * Always returns false.
     * This method is needed here because it's called by some tag files shared with PurAp documents.
     */
    @Override
    public boolean getIsReqsDoc() {
        return false;
    }

    public String getWorkflowStatusForResult(){
        return PurapSearchUtils.getWorkFlowStatusString(getDocumentHeader());
    }

    public java.util.Date getCreateDateForResult() {
        return new java.util.Date(this.getFinancialSystemDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
    }

    public String getDocumentTitleForResult() throws WorkflowException{
       return KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(this.getFinancialSystemDocumentHeader().getWorkflowDocument().getDocumentTypeName()).getLabel();
    }

    /**
     * Checks whether the related purchase order views need a warning to be displayed,
     * i.e. if at least one of the purchase orders has never been opened.
     * @return true if at least one related purchase order needs a warning; false otherwise
     */
    public boolean getNeedWarningRelatedPOs() {
        List<PurchaseOrderView> poViews = getRelatedViews().getRelatedPurchaseOrderViews();
        for (PurchaseOrderView poView : poViews) {
            if (poView.getNeedWarning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NoteType getNoteType() {
        return NoteType.BUSINESS_OBJECT;
    }

}
