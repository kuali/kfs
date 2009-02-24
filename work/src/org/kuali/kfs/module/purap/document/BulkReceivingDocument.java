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
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class BulkReceivingDocument extends FinancialSystemTransactionalDocumentBase implements ReceivingDocument{

    private static final Logger LOG = Logger.getLogger(BulkReceivingDocument.class);
    
    private Integer purchaseOrderIdentifier;
    private Date shipmentReceivedDate;
    private String shipmentPackingSlipNumber;
    private String carrierCode;
    private String shipmentBillOfLadingNumber;
    
    private String shipmentReferenceNumber;
    private String shipmentWeight;
    private Integer noOfCartons;
    private String trackingNumber;
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
    private String vendorNoteText;
    
    /**
     * Alternate Vendor
     */
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;
    
    /**
     * Goods delivered vendor
     */
    private Integer goodsDeliveredVendorHeaderGeneratedIdentifier;
    private Integer goodsDeliveredVendorDetailAssignedIdentifier;
    private String goodsDeliveredVendorNumber;
    
    /**
     * Delivery Information
     */
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
    private String deliveryCampusCode;
    private String deliveryInstructionText;
    private String deliveryAdditionalInstructionText;
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    
    private String requestorPersonName;
    private String requestorPersonPhoneNumber;
    private String requestorPersonEmailAddress;
    
    private String preparerPersonName;
    private String preparerPersonPhoneNumber;
    
    private String deliveryCampusName;
    private String institutionContactName;
    private String institutionContactPhoneNumber;
    private String institutionContactEmailAddress;
    
    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private VendorDetail alternateVendorDetail;
    
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    private transient PurApRelatedViews relatedViews;
    
    /**
     * Not persisted in DB
     */
    private String vendorNumber;
    private String alternateVendorNumber;
    private String goodsDeliveredVendorName;
    private String vendorContact;
    private Integer vendorAddressGeneratedIdentifier;
    
    
    public BulkReceivingDocument() {
        super();
    }

    public boolean isPotentiallySensitive() {
        List<SensitiveData> sensitiveData = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByRelatedDocId(getAccountsPayablePurchasingDocumentLinkIdentifier());
        if (ObjectUtils.isNotNull(sensitiveData) && !sensitiveData.isEmpty()) {
            return true;
        }
        return false;
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
        setDeliveryCampus(po.getDeliveryCampus()); 
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

    public String getDeliveryCountryName() {
        Country country = SpringContext.getBean(CountryService.class).getByPrimaryId(getDeliveryCountryCode());
        if (country != null)
            return country.getPostalCountryName();
        return null;
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
        vendorCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, vendorCountryCode, vendorCountry);
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

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }
    
    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }
    
    public PurApRelatedViews getRelatedViews() {
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
        return relatedViews;
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

    public boolean isDeliveryBuildingOtherIndicator() {
        return deliveryBuildingOtherIndicator;
    }

    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator) {
        this.deliveryBuildingOtherIndicator = deliveryBuildingOtherIndicator;
    }

    /**
     * TODO: Have to discuss with Chris/Dan to move all these methods to somewhere else in the Receiving class hierarchy
     * @see org.kuali.kfs.module.purap.document.ReceivingDocument#setDeliveryRequiredDate(java.sql.Date)
     */
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
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        m.put("PO", getPurchaseOrderIdentifier());
        return m;
    }
}
