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

import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.location.framework.country.CountryEbo;


public interface ReceivingDocument extends TransactionalDocument, PurapItemOperations {

    public String getCarrierCode();

    public void setCarrierCode(String carrierCode);

    public String getShipmentPackingSlipNumber();

    public void setShipmentPackingSlipNumber(String shipmentPackingSlipNumber);

    public String getShipmentReferenceNumber();

    public void setShipmentReferenceNumber(String shipmentReferenceNumber);

    public String getShipmentBillOfLadingNumber();

    public void setShipmentBillOfLadingNumber(String shipmentBillOfLadingNumber);

    public Date getShipmentReceivedDate();

    public void setShipmentReceivedDate(Date shipmentReceivedDate);

    public Integer getVendorHeaderGeneratedIdentifier();

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier);

    public Integer getVendorDetailAssignedIdentifier();

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier);

    public String getVendorName();

    public void setVendorName(String vendorName);

    public String getVendorLine1Address();

    public void setVendorLine1Address(String vendorLine1Address);

    public String getVendorLine2Address();

    public void setVendorLine2Address(String vendorLine2Address);

    public String getVendorCityName();

    public void setVendorCityName(String vendorCityName);

    public String getVendorStateCode();

    public void setVendorStateCode(String vendorStateCode);

    public String getVendorPostalCode();

    public void setVendorPostalCode(String vendorPostalCode);

    public String getVendorCountryCode();

    public void setVendorCountryCode(String vendorCountryCode);

    public String getDeliveryCampusCode();

    public void setDeliveryCampusCode(String deliveryCampusCode);

    public String getDeliveryBuildingCode();

    public void setDeliveryBuildingCode(String deliveryBuildingCode);

    public String getDeliveryBuildingName();

    public void setDeliveryBuildingName(String deliveryBuildingName);

    public String getDeliveryBuildingRoomNumber();

    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber);

    public String getDeliveryBuildingLine1Address();

    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address);

    public String getDeliveryBuildingLine2Address();

    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address);

    public String getDeliveryCityName();

    public void setDeliveryCityName(String deliveryCityName);

    public String getDeliveryStateCode();

    public void setDeliveryStateCode(String deliveryStateCode);

    public String getDeliveryPostalCode();

    public void setDeliveryPostalCode(String deliveryPostalCode);

    public String getDeliveryCountryCode();

    public void setDeliveryCountryCode(String deliveryCountryCode);

    public String getDeliveryCountryName();

    public String getDeliveryToName();

    public void setDeliveryToName(String deliveryToName);

    public String getDeliveryToEmailAddress();

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress);

    public String getDeliveryToPhoneNumber();

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber);

    public Date getDeliveryRequiredDate();

    public void setDeliveryRequiredDate(Date deliveryRequiredDate);

    public String getDeliveryInstructionText();

    public void setDeliveryInstructionText(String deliveryInstructionText);

    public String getDeliveryRequiredDateReasonCode();

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode);

    public CampusParameter getDeliveryCampus();

    public Carrier getCarrier();

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason();

    public CountryEbo getVendorCountry();

    public VendorDetail getVendorDetail();

    public String getVendorNumber();

    public void setVendorNumber(String vendorNumber);

    public Integer getVendorAddressGeneratedIdentifier();

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier);

    public Integer getAlternateVendorDetailAssignedIdentifier();

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier);

    public Integer getAlternateVendorHeaderGeneratedIdentifier();

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier);

    public String getAlternateVendorName();

    public void setAlternateVendorName(String alternateVendorName);

    public String getAlternateVendorNumber();

    public void setAlternateVendorNumber(String alternateVendorNumber);

    public boolean isDeliveryBuildingOtherIndicator();

    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator);

    public PurchaseOrderDocument getPurchaseOrderDocument();

    public void setPurchaseOrderDocument(PurchaseOrderDocument po);

    public void appSpecificRouteDocumentToUser(WorkflowDocument workflowDocument, String userNetworkId, String annotation, String responsibility) throws WorkflowException;

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier();

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier);

    public boolean getIsATypeOfPurAPRecDoc();

    public boolean getIsATypeOfPurDoc();

    public boolean getIsATypeOfPODoc();

    public boolean getIsPODoc();

    public boolean getIsReqsDoc(); 

    public String getAppDocStatus();
    
    public void setAppDocStatus(String appDocStatus);
}
