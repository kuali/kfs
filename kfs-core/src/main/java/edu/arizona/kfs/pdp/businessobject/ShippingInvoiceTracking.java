package edu.arizona.kfs.pdp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ShippingInvoiceTracking extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -3917402012628430508L;
    protected String invoiceNumber;
    protected String trackingNumber;
    protected Integer sequenceRowNumber;   
    protected String referenceNumber;
    protected KualiDecimal netCharge;
    protected String currencyCode;
    protected KualiDecimal volumeDiscountAmount;
    protected KualiDecimal fuelSurchargeAmount;
    protected KualiDecimal declaredValueAmount;
    protected KualiDecimal miscChargeAmount;
    protected String declaredValue;
    protected KualiInteger numberOfPieces;
    protected String shipperName;
    protected String shipperCompany;
    protected String shipperDept;
    protected String shipperAddressLine1;
    protected String shipperAddressLine2;
    protected String shipperCity;
    protected String shipperState;
    protected String shipperPostalCode;
    protected String usRegionOriginZip;
    protected String shipperCountryCode;
    protected String regionCode;
    protected String recipientName;
    protected String recipientCompany;
    protected String recipientAddressLine1;
    protected String recipientAddressLine2;
    protected String recipientCity;
    protected String recipientState;
    protected String recipientPostalCode;
    protected String recipientCountryCode;
    protected String deliveryDate;
    protected String deliveryTime;
    protected String recipientSignature;
    protected Date shipDate;
    protected String rebillIndicator;
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
   
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
   
    public Integer getSequenceRowNumber() {
        return sequenceRowNumber;
    }
   
    public void setSequenceRowNumber(Integer sequenceRowNumber) {
        this.sequenceRowNumber = sequenceRowNumber;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
   
    public KualiDecimal getNetCharge() {
        return netCharge;
    }
   
    public void setNetCharge(KualiDecimal netCharge) {
        this.netCharge = netCharge;
    }
        
    public void setNetCharge(String netCharge) {
        if (StringUtils.isNotBlank(netCharge)) {
            this.netCharge = new KualiDecimal(netCharge);
        }
        else {
            this.netCharge = KualiDecimal.ZERO;
        }    
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public KualiDecimal getVolumeDiscountAmount() {
        return volumeDiscountAmount;
    }
    
    public void setVolumeDiscountAmount(KualiDecimal volumeDiscountAmount) {
        this.volumeDiscountAmount = volumeDiscountAmount;
    }
    
    public void setVolumeDiscountAmount(String volumeDiscountAmount) {
        if (StringUtils.isNotBlank(volumeDiscountAmount)) {
            this.volumeDiscountAmount = new KualiDecimal(volumeDiscountAmount);
        }
        else {
            this.volumeDiscountAmount = KualiDecimal.ZERO;
        }    
    }
    
    public KualiDecimal getFuelSurchargeAmount() {
        return fuelSurchargeAmount;
    }
    
    public void setFuelSurchargeAmount(KualiDecimal fuelSurchargeAmount) {
        this.fuelSurchargeAmount = fuelSurchargeAmount;
    }
    
    public void setFuelSurchargeAmount(String fuelSurchargeAmount) {
        if (StringUtils.isNotBlank(fuelSurchargeAmount)) {
            this.fuelSurchargeAmount = new KualiDecimal(fuelSurchargeAmount);
        }
        else {
            this.fuelSurchargeAmount = KualiDecimal.ZERO;
        }    
    }
    
    public KualiDecimal getDeclaredValueAmount() {
        return declaredValueAmount;
    }
    
    public void setDeclaredValueAmount(KualiDecimal declaredValueAmount) {
        this.declaredValueAmount = declaredValueAmount;
    }
   
    public void setDeclaredValueAmount(String declaredValueAmount) {
        if (StringUtils.isNotBlank(declaredValueAmount)) {
            this.declaredValueAmount = new KualiDecimal(declaredValueAmount);
        }
        else {
            this.declaredValueAmount = KualiDecimal.ZERO;
        }    
    }
    
    public KualiDecimal getMiscChargeAmount() {
        return miscChargeAmount;
    }
    
    public void setMiscChargeAmount(KualiDecimal miscChargeAmount) {
        this.miscChargeAmount = miscChargeAmount;
    }
    
    public void setMiscChargeAmount(String miscChargeAmount) {
        if (StringUtils.isNotBlank(miscChargeAmount)) {
            this.miscChargeAmount = new KualiDecimal(miscChargeAmount);
        }
        else {
            this.miscChargeAmount = KualiDecimal.ZERO;
        }    
    }
    
    public String getDeclaredValue() {
        return declaredValue;
    }
    
    public void setDeclaredValue(String declaredValue) {
        this.declaredValue = declaredValue;
    }
    
    public KualiInteger getNumberOfPieces() {
        return numberOfPieces;
    }
    
    public void setNumberOfPieces(KualiInteger numberOfPieces) {
        this.numberOfPieces = numberOfPieces;
    }
    
    public void setNumberOfPieces(String numberOfPieces) {
        if (StringUtils.isNotBlank(numberOfPieces)) {
            this.numberOfPieces = new KualiInteger(numberOfPieces);
        }
        else {
            this.numberOfPieces = KualiInteger.ZERO;
        }    
    }
    
    public String getShipperName() {
        return shipperName;
    }
    
    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }
    
    public String getShipperCompany() {
        return shipperCompany;
    }
    
    public void setShipperCompany(String shipperCompany) {
        this.shipperCompany = shipperCompany;
    }
    
    public String getShipperDept() {
        return shipperDept;
    }
    
    public void setShipperDept(String shipperDept) {
        this.shipperDept = shipperDept;
    }
    
    public String getShipperAddressLine1() {
        return shipperAddressLine1;
    }
    
    public void setShipperAddressLine1(String shipperAddressLine1) {
        this.shipperAddressLine1 = shipperAddressLine1;
    }
    
    public String getShipperAddressLine2() {
        return shipperAddressLine2;
    }
    
    public void setShipperAddressLine2(String shipperAddressLine2) {
        this.shipperAddressLine2 = shipperAddressLine2;
    }
    
    public String getShipperCity() {
        return shipperCity;
    }
    
    public void setShipperCity(String shipperCity) {
        this.shipperCity = shipperCity;
    }
    
    public String getShipperState() {
        return shipperState;
    }
    
    public void setShipperState(String shipperState) {
        this.shipperState = shipperState;
    }
    
    public String getShipperPostalCode() {
        return shipperPostalCode;
    }
    
    public void setShipperPostalCode(String shipperPostalCode) {
        this.shipperPostalCode = shipperPostalCode;
    }
    
    public String getUsRegionOriginZip() {
        return usRegionOriginZip;
    }
    
    public void setUsRegionOriginZip(String usRegionOriginZip) {
        this.usRegionOriginZip = usRegionOriginZip;
    }
    
    public String getShipperCountryCode() {
        return shipperCountryCode;
    }
    
    public void setShipperCountryCode(String shipperCountryCode) {
        this.shipperCountryCode = shipperCountryCode;
    }
    
    public String getRegionCode() {
        return regionCode;
    }
    
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getRecipientCompany() {
        return recipientCompany;
    }
    
    public void setRecipientCompany(String recipientCompany) {
        this.recipientCompany = recipientCompany;
    }
    
    public String getRecipientAddressLine1() {
        return recipientAddressLine1;
    }
    
    public void setRecipientAddressLine1(String recipientAddressLine1) {
        this.recipientAddressLine1 = recipientAddressLine1;
    }
    
    public String getRecipientAddressLine2() {
        return recipientAddressLine2;
    }
    
    public void setRecipientAddressLine2(String recipientAddressLine2) {
        this.recipientAddressLine2 = recipientAddressLine2;
    }
    
    public String getRecipientCity() {
        return recipientCity;
    }
    
    public void setRecipientCity(String recipientCity) {
        this.recipientCity = recipientCity;
    }
    
    public String getRecipientState() {
        return recipientState;
    }
    
    public void setRecipientState(String recipientState) {
        this.recipientState = recipientState;
    }
    
    public String getRecipientPostalCode() {
        return recipientPostalCode;
    }
    
    public void setRecipientPostalCode(String recipientPostalCode) {
        this.recipientPostalCode = recipientPostalCode;
    }
    
    public String getRecipientCountryCode() {
        return recipientCountryCode;
    }
    
    public void setRecipientCountryCode(String recipientCountryCode) {
        this.recipientCountryCode = recipientCountryCode;
    }
        
    public String getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getDeliveryTime() {
        return deliveryTime;
    }
    
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    
    public String getRecipientSignature() {
        return recipientSignature;
    }
   
    public void setRecipientSignature(String recipientSignature) {
        this.recipientSignature = recipientSignature;
    }
    
    public Date getShipDate() {
        return shipDate;
    }
    
    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }
    
    public void setShipDate(String shipDate) {
        if (StringUtils.isNotBlank(shipDate)) {
            this.shipDate = (Date) (new SqlDateConverter()).convert(Date.class, shipDate);
        } 
    } 
    
    public String getRebillIndicator() {
        return rebillIndicator;
    }
    
    public void setRebillIndicator(String rebillIndicator) {
        this.rebillIndicator = rebillIndicator;
    }
       
    protected LinkedHashMap<String, String> toStringMapper() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
    	map.put("invoiceNumber", getInvoiceNumber());
    	map.put("trackingNumber", getTrackingNumber());
    	map.put("sequenceRowNumber", getSequenceRowNumber().toString());
    	map.put("referenceNumber", getReferenceNumber());    	
    	return map;
    }
        
    protected void copyFields( ShippingInvoiceTracking trackingToCopy ) {
        this.invoiceNumber = trackingToCopy.invoiceNumber;
        this.trackingNumber = trackingToCopy.trackingNumber;
        this.sequenceRowNumber = trackingToCopy.sequenceRowNumber;
        this.referenceNumber = trackingToCopy.referenceNumber;
        this.shipDate = trackingToCopy.shipDate;
        this.rebillIndicator = trackingToCopy.rebillIndicator;
        this.netCharge = trackingToCopy.netCharge;
        this.currencyCode = trackingToCopy.currencyCode;
        this.volumeDiscountAmount = trackingToCopy.volumeDiscountAmount;
        this.fuelSurchargeAmount = trackingToCopy.fuelSurchargeAmount;
        this.declaredValueAmount = trackingToCopy.declaredValueAmount;
        this.miscChargeAmount = trackingToCopy.miscChargeAmount;
        this.declaredValue = trackingToCopy.declaredValue;
        this.numberOfPieces = trackingToCopy.numberOfPieces;
        this.shipperName = trackingToCopy.shipperName;
        this.shipperCompany = trackingToCopy.shipperCompany;
        this.shipperDept = trackingToCopy.shipperDept;
        this.shipperAddressLine1 = trackingToCopy.shipperAddressLine1;
        this.shipperAddressLine2 = trackingToCopy.shipperAddressLine2;
        this.shipperCity = trackingToCopy.shipperCity;
        this.shipperState = trackingToCopy.shipperState;
        this.shipperPostalCode = trackingToCopy.shipperPostalCode;
        this.usRegionOriginZip = trackingToCopy.usRegionOriginZip;
        this.shipperCountryCode = trackingToCopy.shipperCountryCode;
        this.regionCode = trackingToCopy.regionCode;
        this.recipientName = trackingToCopy.recipientName;
        this.recipientCompany = trackingToCopy.recipientCompany;
        this.recipientAddressLine1 = trackingToCopy.recipientAddressLine1;
        this.recipientAddressLine2 = trackingToCopy.recipientAddressLine2;
        this.recipientCity = trackingToCopy.recipientCity;
        this.recipientState = trackingToCopy.recipientState;
        this.recipientPostalCode = trackingToCopy.recipientPostalCode;
        this.recipientCountryCode = trackingToCopy.recipientCountryCode;
        this.deliveryDate = trackingToCopy.deliveryDate;
        this.deliveryTime = trackingToCopy.deliveryTime;
        this.recipientSignature = trackingToCopy.recipientSignature;        
    }
    
}
