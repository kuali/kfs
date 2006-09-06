/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ShipmentReceiving extends BusinessObjectBase {

	private Integer shipmentReceivingIdentifier;
	private Integer purchaseOrderIdentifier;
	private String purchaseOrderGeneralDescription;
	private String carrierCode;
	private String shipmentTrackingNumber;
	private Date shipmentReceivedDate;
	private String shipmentReceivingNoteText;
	private String vendorName;
	private String vendorLine1Address;
	private String vendorLine2Address;
	private String vendorCityName;
	private String vendorStateCode;
	private String vendorPostalCode;
	private String vendorCountryCode;
	private String vendorShippingPaymentTermsCode;
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

    private Carrier carrier;
	private Campus deliveryCampus;

	/**
	 * Default constructor.
	 */
	public ShipmentReceiving() {

	}

	/**
	 * Gets the shipmentReceivingIdentifier attribute.
	 * 
	 * @return - Returns the shipmentReceivingIdentifier
	 * 
	 */
	public Integer getShipmentReceivingIdentifier() { 
		return shipmentReceivingIdentifier;
	}

	/**
	 * Sets the shipmentReceivingIdentifier attribute.
	 * 
	 * @param - shipmentReceivingIdentifier The shipmentReceivingIdentifier to set.
	 * 
	 */
	public void setShipmentReceivingIdentifier(Integer shipmentReceivingIdentifier) {
		this.shipmentReceivingIdentifier = shipmentReceivingIdentifier;
	}


	/**
	 * Gets the purchaseOrderIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderIdentifier
	 * 
	 */
	public Integer getPurchaseOrderIdentifier() { 
		return purchaseOrderIdentifier;
	}

	/**
	 * Sets the purchaseOrderIdentifier attribute.
	 * 
	 * @param - purchaseOrderIdentifier The purchaseOrderIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
		this.purchaseOrderIdentifier = purchaseOrderIdentifier;
	}


	/**
	 * Gets the purchaseOrderGeneralDescription attribute.
	 * 
	 * @return - Returns the purchaseOrderGeneralDescription
	 * 
	 */
	public String getPurchaseOrderGeneralDescription() { 
		return purchaseOrderGeneralDescription;
	}

	/**
	 * Sets the purchaseOrderGeneralDescription attribute.
	 * 
	 * @param - purchaseOrderGeneralDescription The purchaseOrderGeneralDescription to set.
	 * 
	 */
	public void setPurchaseOrderGeneralDescription(String purchaseOrderGeneralDescription) {
		this.purchaseOrderGeneralDescription = purchaseOrderGeneralDescription;
	}


	/**
	 * Gets the carrierCode attribute.
	 * 
	 * @return - Returns the carrierCode
	 * 
	 */
	public String getCarrierCode() { 
		return carrierCode;
	}

	/**
	 * Sets the carrierCode attribute.
	 * 
	 * @param - carrierCode The carrierCode to set.
	 * 
	 */
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}


	/**
	 * Gets the shipmentTrackingNumber attribute.
	 * 
	 * @return - Returns the shipmentTrackingNumber
	 * 
	 */
	public String getShipmentTrackingNumber() { 
		return shipmentTrackingNumber;
	}

	/**
	 * Sets the shipmentTrackingNumber attribute.
	 * 
	 * @param - shipmentTrackingNumber The shipmentTrackingNumber to set.
	 * 
	 */
	public void setShipmentTrackingNumber(String shipmentTrackingNumber) {
		this.shipmentTrackingNumber = shipmentTrackingNumber;
	}


	/**
	 * Gets the shipmentReceivedDate attribute.
	 * 
	 * @return - Returns the shipmentReceivedDate
	 * 
	 */
	public Date getShipmentReceivedDate() { 
		return shipmentReceivedDate;
	}

	/**
	 * Sets the shipmentReceivedDate attribute.
	 * 
	 * @param - shipmentReceivedDate The shipmentReceivedDate to set.
	 * 
	 */
	public void setShipmentReceivedDate(Date shipmentReceivedDate) {
		this.shipmentReceivedDate = shipmentReceivedDate;
	}


	/**
	 * Gets the shipmentReceivingNoteText attribute.
	 * 
	 * @return - Returns the shipmentReceivingNoteText
	 * 
	 */
	public String getShipmentReceivingNoteText() { 
		return shipmentReceivingNoteText;
	}

	/**
	 * Sets the shipmentReceivingNoteText attribute.
	 * 
	 * @param - shipmentReceivingNoteText The shipmentReceivingNoteText to set.
	 * 
	 */
	public void setShipmentReceivingNoteText(String shipmentReceivingNoteText) {
		this.shipmentReceivingNoteText = shipmentReceivingNoteText;
	}


	/**
	 * Gets the vendorName attribute.
	 * 
	 * @return - Returns the vendorName
	 * 
	 */
	public String getVendorName() { 
		return vendorName;
	}

	/**
	 * Sets the vendorName attribute.
	 * 
	 * @param - vendorName The vendorName to set.
	 * 
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	/**
	 * Gets the vendorLine1Address attribute.
	 * 
	 * @return - Returns the vendorLine1Address
	 * 
	 */
	public String getVendorLine1Address() { 
		return vendorLine1Address;
	}

	/**
	 * Sets the vendorLine1Address attribute.
	 * 
	 * @param - vendorLine1Address The vendorLine1Address to set.
	 * 
	 */
	public void setVendorLine1Address(String vendorLine1Address) {
		this.vendorLine1Address = vendorLine1Address;
	}


	/**
	 * Gets the vendorLine2Address attribute.
	 * 
	 * @return - Returns the vendorLine2Address
	 * 
	 */
	public String getVendorLine2Address() { 
		return vendorLine2Address;
	}

	/**
	 * Sets the vendorLine2Address attribute.
	 * 
	 * @param - vendorLine2Address The vendorLine2Address to set.
	 * 
	 */
	public void setVendorLine2Address(String vendorLine2Address) {
		this.vendorLine2Address = vendorLine2Address;
	}

	/**
	 * Gets the vendorCityName attribute.
	 * 
	 * @return - Returns the vendorCityName
	 * 
	 */
	public String getVendorCityName() { 
		return vendorCityName;
	}

	/**
	 * Sets the vendorCityName attribute.
	 * 
	 * @param - vendorCityName The vendorCityName to set.
	 * 
	 */
	public void setVendorCityName(String vendorCityName) {
		this.vendorCityName = vendorCityName;
	}


	/**
	 * Gets the vendorStateCode attribute.
	 * 
	 * @return - Returns the vendorStateCode
	 * 
	 */
	public String getVendorStateCode() { 
		return vendorStateCode;
	}

	/**
	 * Sets the vendorStateCode attribute.
	 * 
	 * @param - vendorStateCode The vendorStateCode to set.
	 * 
	 */
	public void setVendorStateCode(String vendorStateCode) {
		this.vendorStateCode = vendorStateCode;
	}


	/**
	 * Gets the vendorPostalCode attribute.
	 * 
	 * @return - Returns the vendorPostalCode
	 * 
	 */
	public String getVendorPostalCode() { 
		return vendorPostalCode;
	}

	/**
	 * Sets the vendorPostalCode attribute.
	 * 
	 * @param - vendorPostalCode The vendorPostalCode to set.
	 * 
	 */
	public void setVendorPostalCode(String vendorPostalCode) {
		this.vendorPostalCode = vendorPostalCode;
	}


	/**
	 * Gets the vendorCountryCode attribute.
	 * 
	 * @return - Returns the vendorCountryCode
	 * 
	 */
	public String getVendorCountryCode() { 
		return vendorCountryCode;
	}

	/**
	 * Sets the vendorCountryCode attribute.
	 * 
	 * @param - vendorCountryCode The vendorCountryCode to set.
	 * 
	 */
	public void setVendorCountryCode(String vendorCountryCode) {
		this.vendorCountryCode = vendorCountryCode;
	}


	/**
	 * Gets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @return - Returns the vendorShippingPaymentTermsCode
	 * 
	 */
	public String getVendorShippingPaymentTermsCode() { 
		return vendorShippingPaymentTermsCode;
	}

	/**
	 * Sets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @param - vendorShippingPaymentTermsCode The vendorShippingPaymentTermsCode to set.
	 * 
	 */
	public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
		this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
	}


	/**
	 * Gets the deliveryCampusCode attribute.
	 * 
	 * @return - Returns the deliveryCampusCode
	 * 
	 */
	public String getDeliveryCampusCode() { 
		return deliveryCampusCode;
	}

	/**
	 * Sets the deliveryCampusCode attribute.
	 * 
	 * @param - deliveryCampusCode The deliveryCampusCode to set.
	 * 
	 */
	public void setDeliveryCampusCode(String deliveryCampusCode) {
		this.deliveryCampusCode = deliveryCampusCode;
	}


	/**
	 * Gets the deliveryBuildingCode attribute.
	 * 
	 * @return - Returns the deliveryBuildingCode
	 * 
	 */
	public String getDeliveryBuildingCode() { 
		return deliveryBuildingCode;
	}

	/**
	 * Sets the deliveryBuildingCode attribute.
	 * 
	 * @param - deliveryBuildingCode The deliveryBuildingCode to set.
	 * 
	 */
	public void setDeliveryBuildingCode(String deliveryBuildingCode) {
		this.deliveryBuildingCode = deliveryBuildingCode;
	}


	/**
	 * Gets the deliveryBuildingName attribute.
	 * 
	 * @return - Returns the deliveryBuildingName
	 * 
	 */
	public String getDeliveryBuildingName() { 
		return deliveryBuildingName;
	}

	/**
	 * Sets the deliveryBuildingName attribute.
	 * 
	 * @param - deliveryBuildingName The deliveryBuildingName to set.
	 * 
	 */
	public void setDeliveryBuildingName(String deliveryBuildingName) {
		this.deliveryBuildingName = deliveryBuildingName;
	}


	/**
	 * Gets the deliveryBuildingRoomNumber attribute.
	 * 
	 * @return - Returns the deliveryBuildingRoomNumber
	 * 
	 */
	public String getDeliveryBuildingRoomNumber() { 
		return deliveryBuildingRoomNumber;
	}

	/**
	 * Sets the deliveryBuildingRoomNumber attribute.
	 * 
	 * @param - deliveryBuildingRoomNumber The deliveryBuildingRoomNumber to set.
	 * 
	 */
	public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
		this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
	}


	/**
	 * Gets the deliveryBuildingLine1Address attribute.
	 * 
	 * @return - Returns the deliveryBuildingLine1Address
	 * 
	 */
	public String getDeliveryBuildingLine1Address() { 
		return deliveryBuildingLine1Address;
	}

	/**
	 * Sets the deliveryBuildingLine1Address attribute.
	 * 
	 * @param - deliveryBuildingLine1Address The deliveryBuildingLine1Address to set.
	 * 
	 */
	public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
		this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
	}


	/**
	 * Gets the deliveryBuildingLine2Address attribute.
	 * 
	 * @return - Returns the deliveryBuildingLine2Address
	 * 
	 */
	public String getDeliveryBuildingLine2Address() { 
		return deliveryBuildingLine2Address;
	}

	/**
	 * Sets the deliveryBuildingLine2Address attribute.
	 * 
	 * @param - deliveryBuildingLine2Address The deliveryBuildingLine2Address to set.
	 * 
	 */
	public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
		this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
	}


	/**
	 * Gets the deliveryCityName attribute.
	 * 
	 * @return - Returns the deliveryCityName
	 * 
	 */
	public String getDeliveryCityName() { 
		return deliveryCityName;
	}

	/**
	 * Sets the deliveryCityName attribute.
	 * 
	 * @param - deliveryCityName The deliveryCityName to set.
	 * 
	 */
	public void setDeliveryCityName(String deliveryCityName) {
		this.deliveryCityName = deliveryCityName;
	}


	/**
	 * Gets the deliveryStateCode attribute.
	 * 
	 * @return - Returns the deliveryStateCode
	 * 
	 */
	public String getDeliveryStateCode() { 
		return deliveryStateCode;
	}

	/**
	 * Sets the deliveryStateCode attribute.
	 * 
	 * @param - deliveryStateCode The deliveryStateCode to set.
	 * 
	 */
	public void setDeliveryStateCode(String deliveryStateCode) {
		this.deliveryStateCode = deliveryStateCode;
	}


	/**
	 * Gets the deliveryPostalCode attribute.
	 * 
	 * @return - Returns the deliveryPostalCode
	 * 
	 */
	public String getDeliveryPostalCode() { 
		return deliveryPostalCode;
	}

	/**
	 * Sets the deliveryPostalCode attribute.
	 * 
	 * @param - deliveryPostalCode The deliveryPostalCode to set.
	 * 
	 */
	public void setDeliveryPostalCode(String deliveryPostalCode) {
		this.deliveryPostalCode = deliveryPostalCode;
	}


	/**
	 * Gets the deliveryCountryCode attribute.
	 * 
	 * @return - Returns the deliveryCountryCode
	 * 
	 */
	public String getDeliveryCountryCode() { 
		return deliveryCountryCode;
	}

	/**
	 * Sets the deliveryCountryCode attribute.
	 * 
	 * @param - deliveryCountryCode The deliveryCountryCode to set.
	 * 
	 */
	public void setDeliveryCountryCode(String deliveryCountryCode) {
		this.deliveryCountryCode = deliveryCountryCode;
	}


	/**
	 * Gets the deliveryToName attribute.
	 * 
	 * @return - Returns the deliveryToName
	 * 
	 */
	public String getDeliveryToName() { 
		return deliveryToName;
	}

	/**
	 * Sets the deliveryToName attribute.
	 * 
	 * @param - deliveryToName The deliveryToName to set.
	 * 
	 */
	public void setDeliveryToName(String deliveryToName) {
		this.deliveryToName = deliveryToName;
	}


	/**
	 * Gets the deliveryToEmailAddress attribute.
	 * 
	 * @return - Returns the deliveryToEmailAddress
	 * 
	 */
	public String getDeliveryToEmailAddress() { 
		return deliveryToEmailAddress;
	}

	/**
	 * Sets the deliveryToEmailAddress attribute.
	 * 
	 * @param - deliveryToEmailAddress The deliveryToEmailAddress to set.
	 * 
	 */
	public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
		this.deliveryToEmailAddress = deliveryToEmailAddress;
	}


	/**
	 * Gets the deliveryToPhoneNumber attribute.
	 * 
	 * @return - Returns the deliveryToPhoneNumber
	 * 
	 */
	public String getDeliveryToPhoneNumber() { 
		return deliveryToPhoneNumber;
	}

	/**
	 * Sets the deliveryToPhoneNumber attribute.
	 * 
	 * @param - deliveryToPhoneNumber The deliveryToPhoneNumber to set.
	 * 
	 */
	public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
		this.deliveryToPhoneNumber = deliveryToPhoneNumber;
	}


	/**
	 * Gets the deliveryRequiredDate attribute.
	 * 
	 * @return - Returns the deliveryRequiredDate
	 * 
	 */
	public Date getDeliveryRequiredDate() { 
		return deliveryRequiredDate;
	}

	/**
	 * Sets the deliveryRequiredDate attribute.
	 * 
	 * @param - deliveryRequiredDate The deliveryRequiredDate to set.
	 * 
	 */
	public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
		this.deliveryRequiredDate = deliveryRequiredDate;
	}


	/**
	 * Gets the deliveryInstructionText attribute.
	 * 
	 * @return - Returns the deliveryInstructionText
	 * 
	 */
	public String getDeliveryInstructionText() { 
		return deliveryInstructionText;
	}

	/**
	 * Sets the deliveryInstructionText attribute.
	 * 
	 * @param - deliveryInstructionText The deliveryInstructionText to set.
	 * 
	 */
	public void setDeliveryInstructionText(String deliveryInstructionText) {
		this.deliveryInstructionText = deliveryInstructionText;
	}


	/**
	 * Gets the carrier attribute.
	 * 
	 * @return - Returns the carrier
	 * 
	 */
	public Carrier getCarrier() { 
		return carrier;
	}

	/**
	 * Sets the carrier attribute.
	 * 
	 * @param - carrier The carrier to set.
	 * @deprecated
	 */
	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	/**
	 * Gets the deliveryCampus attribute.
	 * 
	 * @return - Returns the deliveryCampus
	 * 
	 */
	public Campus getDeliveryCampus() { 
		return deliveryCampus;
	}

	/**
	 * Sets the deliveryCampus attribute.
	 * 
	 * @param - deliveryCampus The deliveryCampus to set.
	 * @deprecated
	 */
	public void setDeliveryCampus(Campus deliveryCampus) {
		this.deliveryCampus = deliveryCampus;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.shipmentReceivingIdentifier != null) {
            m.put("shipmentReceivingIdentifier", this.shipmentReceivingIdentifier.toString());
        }
	    return m;
    }
}
