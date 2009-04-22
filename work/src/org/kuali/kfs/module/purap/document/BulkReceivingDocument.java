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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class BulkReceivingDocument extends ReceivingDocumentBase{

    private static final Logger LOG = Logger.getLogger(BulkReceivingDocument.class);
    
    private String shipmentWeight;
    private Integer noOfCartons;
    private String trackingNumber;
    private String vendorAddressInternationalProvinceName;
    private String vendorNoteText;

    /**
     * Goods delivered vendor
     */
    private Integer goodsDeliveredVendorHeaderGeneratedIdentifier;
    private Integer goodsDeliveredVendorDetailAssignedIdentifier;
    private String goodsDeliveredVendorNumber;
    private String deliveryAdditionalInstructionText;

    private String requestorPersonName;
    private String requestorPersonPhoneNumber;
    private String requestorPersonEmailAddress;
    
    private String preparerPersonName;
    private String preparerPersonPhoneNumber;
    
    private String deliveryCampusName;
    private String institutionContactName;
    private String institutionContactPhoneNumber;
    private String institutionContactEmailAddress;

    private VendorDetail alternateVendorDetail;
    
    /**
     * Not persisted in DB
     */
    private String goodsDeliveredVendorName;
    private String vendorContact;
    
    public BulkReceivingDocument() {
        super();
    }

    public void initiateDocument(){
        setShipmentReceivedDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }
    
    public void populateBulkReceivingFromPurchaseOrder(PurchaseOrderDocument po){
        
        setPurchaseOrderIdentifier( po.getPurapDocumentIdentifier() );
        getDocumentHeader().setOrganizationDocumentNumber( po.getDocumentHeader().getOrganizationDocumentNumber() );
        setAccountsPayablePurchasingDocumentLinkIdentifier( po.getAccountsPayablePurchasingDocumentLinkIdentifier() );
        
        //copy vendor
        setVendorHeaderGeneratedIdentifier( po.getVendorHeaderGeneratedIdentifier() );
        setVendorDetailAssignedIdentifier( po.getVendorDetailAssignedIdentifier() );        
        setVendorName( po.getVendorName() );
        setVendorNumber( po.getVendorNumber() );
        setVendorLine1Address( po.getVendorLine1Address() );
        setVendorLine2Address( po.getVendorLine2Address() );
        setVendorCityName( po.getVendorCityName() );
        setVendorStateCode( po.getVendorStateCode() );
        setVendorPostalCode( po.getVendorPostalCode() );
        setVendorCountryCode( po.getVendorCountryCode() );
        setVendorDetail(po.getVendorDetail());
        setVendorNumber(po.getVendorNumber());
        setVendorAddressInternationalProvinceName(po.getVendorAddressInternationalProvinceName());
        setVendorNoteText(po.getVendorNoteText());
        setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
        
        //copy alternate vendor
        setAlternateVendorName( po.getAlternateVendorName() );
        setAlternateVendorNumber( StringUtils.isEmpty(po.getAlternateVendorNumber()) ? null : po.getAlternateVendorNumber());
        setAlternateVendorDetailAssignedIdentifier( po.getAlternateVendorDetailAssignedIdentifier() );
        setAlternateVendorHeaderGeneratedIdentifier( po.getAlternateVendorHeaderGeneratedIdentifier() );
        
        //copy delivery
        setDeliveryBuildingCode( po.getDeliveryBuildingCode() );
        setDeliveryBuildingLine1Address( po.getDeliveryBuildingLine1Address() );
        setDeliveryBuildingLine2Address( po.getDeliveryBuildingLine2Address() );
        setDeliveryBuildingName( po.getDeliveryBuildingName() );        
        setDeliveryBuildingRoomNumber( po.getDeliveryBuildingRoomNumber() );
        setDeliveryCampusCode( po.getDeliveryCampusCode() );
        setDeliveryCityName( po.getDeliveryCityName() );
        setDeliveryCountryCode( po.getDeliveryCountryCode() );
        setDeliveryInstructionText( po.getDeliveryInstructionText() );
        setDeliveryPostalCode( po.getDeliveryPostalCode() );
        setDeliveryStateCode( po.getDeliveryStateCode() );
        setDeliveryToEmailAddress( po.getDeliveryToEmailAddress() );
        setDeliveryToName( po.getDeliveryToName() );
        setDeliveryToPhoneNumber( po.getDeliveryToPhoneNumber() );
        setInstitutionContactName(po.getInstitutionContactName());
        setInstitutionContactPhoneNumber(po.getInstitutionContactPhoneNumber());
        setInstitutionContactEmailAddress(po.getInstitutionContactEmailAddress());

        //Requestor and Requisition preparer
        setRequestorPersonName(po.getRequestorPersonName());
        setRequestorPersonPhoneNumber(po.getRequestorPersonPhoneNumber());
        setRequestorPersonEmailAddress(po.getRequestorPersonEmailAddress());
        
        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(po.getRequisitionIdentifier());
        if (reqDoc != null){ // reqDoc is null when called from unit test
            String requisitionPreparer = reqDoc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            /**
             * This is to get the user name for display
             */
            Person initiatorUser = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(requisitionPreparer);
            setPreparerPersonName(initiatorUser.getName());
        }
        
        if (getVendorNumber() != null){
            setGoodsDeliveredVendorHeaderGeneratedIdentifier(getVendorHeaderGeneratedIdentifier());
            setGoodsDeliveredVendorDetailAssignedIdentifier(getVendorDetailAssignedIdentifier());
            setGoodsDeliveredVendorNumber(getVendorNumber());
            setGoodsDeliveredVendorName(getVendorName());
        }
        
        populateVendorDetails();
        populateDocumentDescription(po);
        
    }
     
    private void populateVendorDetails(){
        
        if (getVendorHeaderGeneratedIdentifier() != null &&
            getVendorDetailAssignedIdentifier() != null){
            VendorDetail tempVendor = new VendorDetail();
            tempVendor.setVendorHeaderGeneratedIdentifier(getVendorHeaderGeneratedIdentifier());
            tempVendor.setVendorDetailAssignedIdentifier(getVendorDetailAssignedIdentifier());
            setVendorNumber(tempVendor.getVendorNumber());
        }
        
        if (getAlternateVendorHeaderGeneratedIdentifier() != null &&
            getAlternateVendorDetailAssignedIdentifier() != null){
            
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(getAlternateVendorHeaderGeneratedIdentifier(), 
                                                                                                   getAlternateVendorDetailAssignedIdentifier());
            //copied from creditmemocreateserviceimpl.populatedocumentfromvendor
            String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
            VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(getAlternateVendorHeaderGeneratedIdentifier(), 
                                                                                                             getAlternateVendorDetailAssignedIdentifier(), 
                                                                                                             VendorConstants.AddressTypes.REMIT, 
                                                                                                             userCampus);
            if (vendorAddress == null) {
            // pick up the default vendor po address type
                vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(getAlternateVendorHeaderGeneratedIdentifier(), 
                                                                                                   getAlternateVendorDetailAssignedIdentifier(), 
                                                                                                   VendorConstants.AddressTypes.PURCHASE_ORDER, 
                                                                                                   userCampus);
            }

            if (vendorAddress != null){
                setAlternateVendorName(vendorDetail.getVendorName());
                setAlternateVendorNumber(vendorDetail.getVendorNumber());
                vendorDetail.setDefaultAddressLine1(vendorAddress.getVendorLine1Address());
                vendorDetail.setDefaultAddressLine2(vendorAddress.getVendorLine2Address());
                vendorDetail.setDefaultAddressCity(vendorAddress.getVendorCityName());
                vendorDetail.setDefaultAddressCountryCode(vendorAddress.getVendorCountryCode());
                vendorDetail.setDefaultAddressPostalCode(vendorAddress.getVendorZipCode());
                vendorDetail.setDefaultAddressStateCode(vendorAddress.getVendorStateCode());
                vendorDetail.setDefaultAddressInternationalProvince(vendorAddress.getVendorAddressInternationalProvinceName());
            }

            setAlternateVendorDetail(vendorDetail);
        }
        
        if (getGoodsDeliveredVendorHeaderGeneratedIdentifier() != null &&
            getGoodsDeliveredVendorDetailAssignedIdentifier() != null){
            VendorDetail tempVendor = new VendorDetail();
            tempVendor.setVendorHeaderGeneratedIdentifier(getGoodsDeliveredVendorHeaderGeneratedIdentifier());
            tempVendor.setVendorDetailAssignedIdentifier(getGoodsDeliveredVendorDetailAssignedIdentifier());
            setGoodsDeliveredVendorNumber(tempVendor.getVendorNumber());
            if (StringUtils.equals(getVendorNumber(),getGoodsDeliveredVendorNumber())){
                setGoodsDeliveredVendorName(getVendorName());
            }else {
                setGoodsDeliveredVendorName(getAlternateVendorName());
            }
        }
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
        
        setShipmentReceivedDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        setShipmentPackingSlipNumber(null);
        setShipmentBillOfLadingNumber(null);
        setCarrierCode(null);        
    }

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        refreshNonUpdateableReferences();
        populateVendorDetails();
    }
    
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        
        if (event instanceof AttributedContinuePurapEvent) {
            SpringContext.getBean(BulkReceivingService.class).populateBulkReceivingFromPurchaseOrder(this);
            if (getPurchaseOrderIdentifier() == null){
                getDocumentHeader().setDocumentDescription(PurapConstants.BulkReceivingDocumentStrings.MESSAGE_BULK_RECEIVING_DEFAULT_DOC_DESCRIPTION);;
            }
        }else{
            if (getGoodsDeliveredVendorNumber() != null){
                VendorDetail tempVendor = new VendorDetail();
                tempVendor.setVendorNumber(getGoodsDeliveredVendorNumber());
                setGoodsDeliveredVendorHeaderGeneratedIdentifier(tempVendor.getVendorHeaderGeneratedIdentifier());
                setGoodsDeliveredVendorDetailAssignedIdentifier(tempVendor.getVendorDetailAssignedIdentifier());
            }
        }
        
        super.prepareForSave(event);
    }
    
    private void populateDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName();
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        getDocumentHeader().setDocumentDescription(description);
    }

    public String getDeliveryCountryName() {
        Country country = SpringContext.getBean(CountryService.class).getByPrimaryId(getDeliveryCountryCode());
        if (country != null)
            return country.getPostalCountryName();
        return null;
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

    public String getVendorContact() {
        return vendorContact;
    }

    public void setVendorContact(String vendorContact) {
        this.vendorContact = vendorContact;
    }
 
    public VendorDetail getAlternateVendorDetail() {
        return alternateVendorDetail;
    }

    public void setAlternateVendorDetail(VendorDetail alternateVendorDetail) {
        this.alternateVendorDetail = alternateVendorDetail;
    }

    public String getShipmentWeight() {
        return shipmentWeight;
    }

    public void setShipmentWeight(String shipmentWeight) {
        this.shipmentWeight = shipmentWeight;
    }

    public String getGoodsDeliveredVendorName() {
        return goodsDeliveredVendorName;
    }
    
    public void setGoodsDeliveredVendorName(String goodsDeliveredVendorName) {
        this.goodsDeliveredVendorName = goodsDeliveredVendorName;
    }
    
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public String getDeliveryCampusName() {
        return deliveryCampusName;
    }

    public void setDeliveryCampusName(String deliveryCampusName) {
        this.deliveryCampusName = deliveryCampusName;
    }

    public String getRequestorPersonName() {
        return requestorPersonName;
    }

    public void setRequestorPersonName(String requestorPersonName) {
        this.requestorPersonName = requestorPersonName;
    }

    public String getRequestorPersonPhoneNumber() {
        return requestorPersonPhoneNumber;
    }

    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
    }

    public String getPreparerPersonName() {
        return preparerPersonName;
    }

    public void setPreparerPersonName(String preparerPersonName) {
        this.preparerPersonName = preparerPersonName;
    }

    public String getPreparerPersonPhoneNumber() {
        return preparerPersonPhoneNumber;
    }

    public void setPreparerPersonPhoneNumber(String preparerPersonPhoneNumber) {
        this.preparerPersonPhoneNumber = preparerPersonPhoneNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getVendorNoteText() {
        return vendorNoteText;
    }

    public void setVendorNoteText(String vendorNoteText) {
        this.vendorNoteText = vendorNoteText;
    }

    public String getRequestorPersonEmailAddress() {
        return requestorPersonEmailAddress;
    }

    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
    }

    public String getInstitutionContactEmailAddress() {
        return institutionContactEmailAddress;
    }

    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress) {
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }

    public String getInstitutionContactName() {
        return institutionContactName;
    }

    public void setInstitutionContactName(String institutionContactName) {
        this.institutionContactName = institutionContactName;
    }

    public String getInstitutionContactPhoneNumber() {
        return institutionContactPhoneNumber;
    }

    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber) {
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
    }

    public String getDeliveryAdditionalInstructionText() {
        return deliveryAdditionalInstructionText;
    }

    public void setDeliveryAdditionalInstructionText(String deliveryAdditionalInstructionText) {
        this.deliveryAdditionalInstructionText = deliveryAdditionalInstructionText;
    }

    public Integer getGoodsDeliveredVendorDetailAssignedIdentifier() {
        return goodsDeliveredVendorDetailAssignedIdentifier;
    }

    public void setGoodsDeliveredVendorDetailAssignedIdentifier(Integer goodsDeliveredVendorDetailAssignedIdentifier) {
        this.goodsDeliveredVendorDetailAssignedIdentifier = goodsDeliveredVendorDetailAssignedIdentifier;
    }

    public Integer getGoodsDeliveredVendorHeaderGeneratedIdentifier() {
        return goodsDeliveredVendorHeaderGeneratedIdentifier;
    }

    public void setGoodsDeliveredVendorHeaderGeneratedIdentifier(Integer goodsDeliveredVendorHeaderGeneratedIdentifier) {
        this.goodsDeliveredVendorHeaderGeneratedIdentifier = goodsDeliveredVendorHeaderGeneratedIdentifier;
    }

    public void appSpecificRouteDocumentToUser(KualiWorkflowDocument workflowDocument, String userNetworkId, String annotation, String responsibility) throws WorkflowException {
        // TODO Auto-generated method stub
        
    }

    public Date getDeliveryRequiredDate() {
        // TODO Auto-generated method stub
        return null;
    }

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDeliveryRequiredDateReasonCode() {
        // TODO Auto-generated method stub
        return null;
    }

    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        // TODO Auto-generated method stub
        return null;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        // TODO Auto-generated method stub
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        // TODO Auto-generated method stub
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument po) {
        // TODO Auto-generated method stub
    }

    public <T> T getItem(int pos) {
        // TODO Auto-generated method stub
        return null;
    }

    public Class getItemClass() {
        // TODO Auto-generated method stub
        return null;
    }

    public List getItems() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setItems(List items) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isBoNotesSupport() {
        return true;
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
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        m.put("PO", getPurchaseOrderIdentifier());
        return m;
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
}
