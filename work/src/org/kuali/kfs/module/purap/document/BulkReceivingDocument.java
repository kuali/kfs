/*
 * Copyright 2006-2008 The Kuali Foundation
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

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.util.PurapSearchUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

public class BulkReceivingDocument extends ReceivingDocumentBase{

    protected static final Logger LOG = Logger.getLogger(BulkReceivingDocument.class);

    protected String shipmentWeight;
    protected Integer noOfCartons;
    protected String trackingNumber;
    protected String vendorAddressInternationalProvinceName;
    protected String vendorNoteText;

    /**
     * Goods delivered vendor
     */
    protected Integer goodsDeliveredVendorHeaderGeneratedIdentifier;
    protected Integer goodsDeliveredVendorDetailAssignedIdentifier;
    protected String goodsDeliveredVendorNumber;
    protected String deliveryAdditionalInstructionText;

    protected String requestorPersonName;
    protected String requestorPersonPhoneNumber;
    protected String requestorPersonEmailAddress;

    protected String preparerPersonName;
    protected String preparerPersonPhoneNumber;

    protected String deliveryCampusName;
    protected String institutionContactName;
    protected String institutionContactPhoneNumber;
    protected String institutionContactEmailAddress;

    protected VendorDetail alternateVendorDetail;

    /**
     * Not persisted in DB
     */
    protected String goodsDeliveredVendorName;
    protected String vendorContact;

    public BulkReceivingDocument() {
        super();
    }

    @Override
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
            String requisitionPreparer = reqDoc.getFinancialSystemDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            /**
             * This is to get the user name for display
             */
            Person initiatorUser = KimApiServiceLocator.getPersonService().getPerson(requisitionPreparer);
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

    protected void populateVendorDetails(){

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
        getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(null);
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

    protected void populateDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName();
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KRADPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        getDocumentHeader().setDocumentDescription(description);
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

    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("PO", getPurchaseOrderIdentifier());
        return m;
    }

    @Override
    public String getWorkflowStatusForResult(){
        return PurapSearchUtils.getWorkFlowStatusString(getDocumentHeader());
    }

    /**
     * It's not needed to implement this method in this class since bulk receiving doesn't support items.
     * Calling this method will throw an UnsupportedOperationException
     * @throws UnsupportedOperationException
     */
    @Override
    public Class getItemClass() {
        throw new UnsupportedOperationException("Items are not being handled in Bulk Receiving document");
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this class since bulk receiving doesn't support items.
     * Calling this method will throw an UnsupportedOperationException
     * @throws UnsupportedOperationException
     */
    @Override
    public <T> T getItem(int pos) {
        throw new UnsupportedOperationException("Items are not being handled in Bulk Receiving document");
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this class since bulk receiving doesn't support items.
     * Calling this method will throw an UnsupportedOperationException
     * @throws UnsupportedOperationException
     */
    @Override
    public List getItems() {
        throw new UnsupportedOperationException("Items are not being handled in Bulk Receiving document");
    }

    /**
     * It's not needed to implement PurapItemOperations methods in this class since bulk receiving doesn't support items.
     * Calling this method will throw an UnsupportedOperationException
     * @throws UnsupportedOperationException
     */
    @Override
    public void setItems(List items) {
        throw new UnsupportedOperationException("Items are not being handled in Bulk Receiving document");
    }

}
