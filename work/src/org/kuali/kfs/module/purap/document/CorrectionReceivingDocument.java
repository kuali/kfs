/*
 * Copyright 2008-2009 The Kuali Foundation
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
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingItem;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.framework.country.CountryEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionReceivingDocument extends ReceivingDocumentBase {

    protected String lineItemReceivingDocumentNumber;
    //Collections
    protected List<CorrectionReceivingItem> items;

    protected LineItemReceivingDocument lineItemReceivingDocument;

    /**
     * Default constructor.
     */
    public CorrectionReceivingDocument() {
        super();
        items = new ArrayList<CorrectionReceivingItem>();
    }

    public void populateCorrectionReceivingFromReceivingLine(LineItemReceivingDocument rlDoc){

        //populate receiving line document from purchase order
        this.setPurchaseOrderIdentifier( rlDoc.getPurchaseOrderIdentifier() );
        this.getDocumentHeader().setDocumentDescription( rlDoc.getDocumentHeader().getDocumentDescription());
        this.getDocumentHeader().setOrganizationDocumentNumber( rlDoc.getDocumentHeader().getOrganizationDocumentNumber() );
        this.setAccountsPayablePurchasingDocumentLinkIdentifier( rlDoc.getAccountsPayablePurchasingDocumentLinkIdentifier() );
        this.setLineItemReceivingDocumentNumber(rlDoc.getDocumentNumber());

        //copy receiving line items
        for (LineItemReceivingItem rli : (List<LineItemReceivingItem>) rlDoc.getItems()) {
            this.getItems().add(new CorrectionReceivingItem(rli, this));
        }

    }


    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

        if(this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
            SpringContext.getBean(ReceivingService.class).completeCorrectionReceivingDocument(this);
        }
        super.doRouteStatusChange(statusChangeEvent);
    }

    /**
     * Gets the lineItemReceivingDocumentNumber attribute.
     *
     * @return Returns the lineItemReceivingDocumentNumber
     *
     */
    public String getLineItemReceivingDocumentNumber() {
        return lineItemReceivingDocumentNumber;
    }

    /**
     * Sets the lineItemReceivingDocumentNumber attribute.
     *
     * @param lineItemReceivingDocumentNumber The lineItemReceivingDocumentNumber to set.
     *
     */
    public void setLineItemReceivingDocumentNumber(String lineItemReceivingDocumentNumber) {
        this.lineItemReceivingDocumentNumber = lineItemReceivingDocumentNumber;
    }

    /**
     * Gets the lineItemReceivingDocument attribute.
     * @return Returns the lineItemReceivingDocument.
     */
    public LineItemReceivingDocument getLineItemReceivingDocument() {
        refreshLineReceivingDocument();
        return lineItemReceivingDocument;
    }

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        refreshLineReceivingDocument();
    }

    protected void refreshLineReceivingDocument(){
        if(ObjectUtils.isNull(lineItemReceivingDocument) || lineItemReceivingDocument.getDocumentNumber() == null){
            this.refreshReferenceObject("lineItemReceivingDocument");
            if (ObjectUtils.isNull(lineItemReceivingDocument.getDocumentHeader().getDocumentNumber())) {
                lineItemReceivingDocument.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            }
        }else{
            if (ObjectUtils.isNull(lineItemReceivingDocument.getDocumentHeader().getDocumentNumber())) {
                lineItemReceivingDocument.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            }
        }
    }

    @Override
    public Integer getPurchaseOrderIdentifier() {
        if (ObjectUtils.isNull(super.getPurchaseOrderIdentifier())){
            refreshLineReceivingDocument();
            if (ObjectUtils.isNotNull(lineItemReceivingDocument)){
                setPurchaseOrderIdentifier(lineItemReceivingDocument.getPurchaseOrderIdentifier());
            }
        }
        return super.getPurchaseOrderIdentifier();
    }

    /**
     * Sets the lineItemReceivingDocument attribute value.
     * @param lineItemReceivingDocument The lineItemReceivingDocument to set.
     * @deprecated
     */
    public void setLineItemReceivingDocument(LineItemReceivingDocument lineItemReceivingDocument) {
        this.lineItemReceivingDocument = lineItemReceivingDocument;
    }

    @Override
    public Class getItemClass() {
        return CorrectionReceivingItem.class;
    }

    @Override
    public List getItems() {
        return items;
    }

    @Override
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public ReceivingItem getItem(int pos) {
        return (ReceivingItem) items.get(pos);
    }

    public void addItem(ReceivingItem item) {
        getItems().add(item);
    }

    public void deleteItem(int lineNum) {
        if (getItems().remove(lineNum) == null) {
            // throw error here
        }
    }

    @Override
    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return getLineItemReceivingDocument().getAlternateVendorDetailAssignedIdentifier();
    }

    @Override
    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return getLineItemReceivingDocument().getAlternateVendorHeaderGeneratedIdentifier();
    }

    @Override
    public String getAlternateVendorName() {
        return getLineItemReceivingDocument().getAlternateVendorName();
    }

    @Override
    public String getAlternateVendorNumber() {
        return getLineItemReceivingDocument().getAlternateVendorNumber();
    }

    @Override
    public Carrier getCarrier() {
        return getLineItemReceivingDocument().getCarrier();
    }

    @Override
    public String getCarrierCode() {
        return getLineItemReceivingDocument().getCarrierCode();
    }

    @Override
    public String getDeliveryBuildingCode() {
        return getLineItemReceivingDocument().getDeliveryBuildingCode();
    }

    @Override
    public String getDeliveryBuildingLine1Address() {
        return getLineItemReceivingDocument().getDeliveryBuildingLine1Address();
    }

    @Override
    public String getDeliveryBuildingLine2Address() {
        return getLineItemReceivingDocument().getDeliveryBuildingLine2Address();
    }

    @Override
    public String getDeliveryBuildingName() {
        return getLineItemReceivingDocument().getDeliveryBuildingName();
    }

    @Override
    public String getDeliveryBuildingRoomNumber() {
        return getLineItemReceivingDocument().getDeliveryBuildingRoomNumber();
    }

    @Override
    public CampusParameter getDeliveryCampus() {
        return getLineItemReceivingDocument().getDeliveryCampus();
    }

    @Override
    public String getDeliveryCampusCode() {
        return getLineItemReceivingDocument().getDeliveryCampusCode();
    }

    @Override
    public String getDeliveryCityName() {
        return getLineItemReceivingDocument().getDeliveryCityName();
    }

    @Override
    public String getDeliveryCountryCode() {
        return getLineItemReceivingDocument().getDeliveryCountryCode();
    }

    @Override
    public String getDeliveryInstructionText() {
        return getLineItemReceivingDocument().getDeliveryInstructionText();
    }

    @Override
    public String getDeliveryPostalCode() {
        return getLineItemReceivingDocument().getDeliveryPostalCode();
    }

    @Override
    public Date getDeliveryRequiredDate() {
        return getLineItemReceivingDocument().getDeliveryRequiredDate();
    }

    @Override
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return getLineItemReceivingDocument().getDeliveryRequiredDateReason();
    }

    @Override
    public String getDeliveryRequiredDateReasonCode() {
        return getLineItemReceivingDocument().getDeliveryRequiredDateReasonCode();
    }

    @Override
    public String getDeliveryStateCode() {
        return getLineItemReceivingDocument().getDeliveryStateCode();
    }

    @Override
    public String getDeliveryToEmailAddress() {
        return getLineItemReceivingDocument().getDeliveryToEmailAddress();
    }

    @Override
    public String getDeliveryToName() {
        return getLineItemReceivingDocument().getDeliveryToName();
    }

    @Override
    public String getDeliveryToPhoneNumber() {
        return getLineItemReceivingDocument().getDeliveryToPhoneNumber();
    }

    @Override
    public String getShipmentBillOfLadingNumber() {
        return getLineItemReceivingDocument().getShipmentBillOfLadingNumber();
    }

    @Override
    public String getShipmentPackingSlipNumber() {
        return getLineItemReceivingDocument().getShipmentPackingSlipNumber();
    }

    @Override
    public Date getShipmentReceivedDate() {
        return getLineItemReceivingDocument().getShipmentReceivedDate();
    }

    @Override
    public String getShipmentReferenceNumber() {
        return getLineItemReceivingDocument().getShipmentReferenceNumber();
    }

    @Override
    public Integer getVendorAddressGeneratedIdentifier() {
        return getLineItemReceivingDocument().getVendorAddressGeneratedIdentifier();
    }

    @Override
    public String getVendorCityName() {
        return getLineItemReceivingDocument().getVendorCityName();
    }

    @Override
    public CountryEbo getVendorCountry() {
        return getLineItemReceivingDocument().getVendorCountry();
    }

    @Override
    public String getVendorCountryCode() {
        return getLineItemReceivingDocument().getVendorCountryCode();
    }

    @Override
    public VendorDetail getVendorDetail() {
        return getLineItemReceivingDocument().getVendorDetail();
    }

    @Override
    public Integer getVendorDetailAssignedIdentifier() {
        return getLineItemReceivingDocument().getVendorDetailAssignedIdentifier();
    }

    @Override
    public Integer getVendorHeaderGeneratedIdentifier() {
        return getLineItemReceivingDocument().getVendorHeaderGeneratedIdentifier();
    }

    @Override
    public String getVendorLine1Address() {
        return getLineItemReceivingDocument().getVendorLine1Address();
    }

    @Override
    public String getVendorLine2Address() {
        return getLineItemReceivingDocument().getVendorLine2Address();
    }

    @Override
    public String getVendorName() {
        return getLineItemReceivingDocument().getVendorName();
    }

    @Override
    public String getVendorNumber() {
        return getLineItemReceivingDocument().getVendorNumber();
    }

    @Override
    public String getVendorPostalCode() {
        return getLineItemReceivingDocument().getVendorPostalCode();
    }

    @Override
    public String getVendorStateCode() {
        return getLineItemReceivingDocument().getVendorStateCode();
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.getItems());
        return managedLists;
    }

}
