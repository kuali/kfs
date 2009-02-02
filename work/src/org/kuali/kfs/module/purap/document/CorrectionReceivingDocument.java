package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingItem;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionReceivingDocument extends ReceivingDocumentBase {

    private String lineItemReceivingDocumentNumber;
    //Collections
    private List<CorrectionReceivingItem> items;
    
    private LineItemReceivingDocument lineItemReceivingDocument;
    
    /**
     * Default constructor.
     */
    public CorrectionReceivingDocument() {
        super();
        items = new TypedArrayList(getItemClass());
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
    public void handleRouteStatusChange() {
        if(this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(ReceivingService.class).completeCorrectionReceivingDocument(this);
        }
        super.handleRouteStatusChange();
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
        if(lineItemReceivingDocument == null){
            this.refreshReferenceObject("lineItemReceivingDocument");
        }
        
        return lineItemReceivingDocument;
    }

    /**
     * Sets the lineItemReceivingDocument attribute value.
     * @param lineItemReceivingDocument The lineItemReceivingDocument to set.
     * @deprecated
     */
    public void setLineItemReceivingDocument(LineItemReceivingDocument lineItemReceivingDocument) {
        this.lineItemReceivingDocument = lineItemReceivingDocument;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public Class getItemClass() {
        return CorrectionReceivingItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

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
    public Campus getDeliveryCampus() {
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
    public Country getVendorCountry() {
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

}
