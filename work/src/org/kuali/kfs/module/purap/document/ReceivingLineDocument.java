package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Country;
import org.kuali.module.purap.bo.Carrier;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.vendor.bo.VendorDetail;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingLineDocument extends PurchasingAccountsPayableDocumentBase {

    private String documentNumber;
    private Integer purchaseOrderIdentifier;
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

    //Not Persisted in DB
    private boolean deliveryBuildingOther;
    
    //Collections
    private List<ReceivingLineItem> items;
    
    private DocumentHeader documentHeader;
    private Campus deliveryCampus;
    private Country vendorCountry;
    private Carrier carrier;
    private VendorDetail vendorDetail;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
 
    
    /**
     * Default constructor.
     */
    public ReceivingLineDocument() {
        items = new TypedArrayList(getItemClass());
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() { 
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the purchaseOrderIdentifier attribute.
     * 
     * @return Returns the purchaseOrderIdentifier
     * 
     */
    public Integer getPurchaseOrderIdentifier() { 
        return purchaseOrderIdentifier;
    }

    /**
     * Sets the purchaseOrderIdentifier attribute.
     * 
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     * 
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
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
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
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

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public boolean isDeliveryBuildingOther() {
        return deliveryBuildingOther;
    }

    public void setDeliveryBuildingOther(boolean deliveryBuildingOther) {
        this.deliveryBuildingOther = deliveryBuildingOther;
    }

    public Class getItemClass() {
        return ReceivingLineItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    /*public void addItem(ReceivingLineItem item) {
        int itemLinePosition = getItemLinePosition();
        if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <= itemLinePosition)) {
            itemLinePosition = item.getItemLineNumber().intValue() - 1;
        }
        items.add(itemLinePosition, item);
        renumberItems(itemLinePosition);
    }

    public void deleteItem(int lineNum) {
        if (items.remove(lineNum) == null) {
            // throw error here
        }
        renumberItems(lineNum);
    }

    public void renumberItems(int start) {
        for (int i = start; i < items.size(); i++) {
            ReceivingLineItem item = (ReceivingLineItem) items.get(i);
            item.setItemLineNumber(new Integer(i + 1));
        }
    }

    public void itemSwap(int positionFrom, int positionTo) {
        // if out of range do nothing
        if ((positionTo < 0) || (positionTo >= getItemLinePosition())) {
            return;
        }
        ReceivingLineItem item1 = this.getItem(positionFrom);
        ReceivingLineItem item2 = this.getItem(positionTo);
        Integer oldFirstPos = item1.getItemLineNumber();
        // swap line numbers
        item1.setItemLineNumber(item2.getItemLineNumber());
        item2.setItemLineNumber(oldFirstPos);
        // fix ordering in list
        items.remove(positionFrom);
        items.add(positionTo, item1);
    }

    public int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (ReceivingLineItem item : items) {
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }

    public ReceivingLineItem getItem(int pos) {
        return (ReceivingLineItem) items.get(pos);
    }*/

    /**
     * Iterates through the items of the document and returns the item with the line number equal to the number given, or null if a
     * match is not found.
     * 
     * @param lineNumber line number to match on.
     * @return the PurchasingAp Item if a match is found, else null.
     */
    /*public ReceivingLineItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            ReceivingLineItem item = (ReceivingLineItem) iter.next();
            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }*/

}
