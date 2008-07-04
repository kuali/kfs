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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.ContinuePurapEvent;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;

public class BulkReceivingDocument extends FinancialSystemTransactionalDocumentBase {

    private Integer purchaseOrderIdentifier;
    private Date shipmentReceivedDate;
    private String shipmentPackingSlipNumber;
    private String carrierCode;
    private String shipmentBillOfLadingNumber;
    
    private String goodsDeliveredVendorNumber;
    private String shipmentReferenceNumber;
    private String shipmentWeight;
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
    private String vendorAddressInternationalProvinceName;
    
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
    
    private String requestorPersonName;
    private String requestorPersonPhoneNumber;
    private String requestorPersonWorkflowId;
    
    private String preparerPersonName;
    private String preparerPersonPhoneNumber;
    private String preparerPersonWorkflowId;
    
    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private VendorDetail alternateVendorDetail;
    private Org organization;
    
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    private transient PurApRelatedViews relatedViews;
    
    /**
     * Added recently
     */
    private String organizationCode;
    
    /**
     * Not persisted in DB
     */
    private String vendorNumber;
    private String alternateVendorNumber;
    private String goodsDeliveredVendorName;
    private String vendorContact;
    private String deliveryCampusName;
    private String organizationName;
    
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
        setDeliveryCampus(po.getDeliveryCampus()); // Object assign - Is it reqd? It's not there in PO doc
//        setDeliveryCampusName(po.getDeliveryCampus().getCampusName());
        setOrganizationCode(po.getOrganizationCode());
        setOrganization(po.getOrganization()); // Object assign - Is it reqd? It's not there in PO doc. Here we need to display the org name. so i think it's reqd
        

        //Requestor and Requisition preparer
        setRequestorPersonName(po.getRequestorPersonName());
        setRequestorPersonPhoneNumber(po.getRequestorPersonPhoneNumber());
        
        /**
         * TODO : Get UserVO for the REQUESTOR
         */
//        try {
//            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(getRequisitionRequestorName());
//        }
//            catch (UserNotFoundException e) {
//                e.printStackTrace();
//            }
//            try{
//            UniversalUser user2 = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getRequisitionRequestorName());}
//            catch (UserNotFoundException e) {
//                e.printStackTrace();
//            }
            //workflowdocumentservice
//            UserIdVO user1 = new NetworkIdVO(user.getPersonUserIdentifier());
//            UserIdVO user4 = new NetworkIdVO(user2.getPersonUserIdentifier());
//            try {
//                WorkflowUser wfUser = SpringContext.getBean(UserService.class).getWorkflowUser(user1);
//                setRequisitionRequestorWorkflowId(wfUser.getWorkflowId());
//            }
//            catch (EdenUserNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        catch (UserNotFoundException e) {
//            e.printStackTrace();
//        }
        
        
        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(po.getRequisitionIdentifier());
        String requisitionPreparer = reqDoc.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId();
        
        /**
         * This is to get the user name for display
         */
        try {
            UserIdVO userVO = new NetworkIdVO(requisitionPreparer);
            WorkflowUser wfUser = SpringContext.getBean(UserService.class).getWorkflowUser(userVO);
            setPreparerPersonName(wfUser.getDisplayName());
            setPreparerPersonWorkflowId(wfUser.getWorkflowId());
        }
        catch (EdenUserNotFoundException e) {
            e.printStackTrace();
        }
        
        
        if (StringUtils.isNotEmpty(getAlternateVendorNumber())){
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(getAlternateVendorHeaderGeneratedIdentifier(), 
                                                                                                   getAlternateVendorDetailAssignedIdentifier());
            //copied from creditmemocreateserviceimpl.populatedocumentfromvendor
            String userCampus = GlobalVariables.getUserSession().getFinancialSystemUser().getCampusCode();
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
        
        if (getVendorNumber() != null){
            setGoodsDeliveredVendorNumber(getVendorNumber());
            setGoodsDeliveredVendorName(getVendorName());
        }
        
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
//        setPOAvailable(false);
        
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
    
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if(getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            /**
             * FIXME: this is not needed.
             */
//            SpringContext.getBean(BulkReceivingService.class).completeBulkReceivingDocument(this);
        }
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

//    @Override
//    public String getDocumentTitle() {
//        
//        String docTitle = ""; 
//        
//        if (getPurchaseOrderIdentifier() != null && 
//            getVendorName() != null){
//            
//            docTitle = "PO: " + getPurchaseOrderIdentifier() + " Vendor: " + getVendorName();
//
//            int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
//            if (noteTextMaxLength < docTitle.length()) {
//                docTitle = docTitle.substring(0, noteTextMaxLength);
//            }
//            
//            return docTitle;
//        }
//        
//        return super.getDocumentTitle();
//        
//    }
    
    private void populateDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName();
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
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
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
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

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
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

    public String getRequestorPersonWorkflowId() {
        return requestorPersonWorkflowId;
    }

    public void setRequestorPersonWorkflowId(String requestorPersonWorkflowId) {
        this.requestorPersonWorkflowId = requestorPersonWorkflowId;
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

    public String getPreparerPersonWorkflowId() {
        return preparerPersonWorkflowId;
    }

    public void setPreparerPersonWorkflowId(String preparerPersonWorkflowId) {
        this.preparerPersonWorkflowId = preparerPersonWorkflowId;
    }

    public Org getOrganization() {
        return organization;
    }

    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }



}
