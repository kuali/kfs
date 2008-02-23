/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.service.impl;

import java.util.List;

import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;

public class ReceivingServiceImpl implements ReceivingService {

    private PurchaseOrderService purchaseOrderService;
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void populateReceivingLine(ReceivingLineDocument rlDoc, String poDocId) {
        
        if(rlDoc == null){
            rlDoc = new ReceivingLineDocument();
        }
                        
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = new PurchaseOrderDocument();
        poDoc = purchaseOrderService.getPurchaseOrderByDocumentNumber(poDocId);
        
        //populate receiving line document from purchase order
        //copy vendor
        rlDoc.setVendorName( poDoc.getVendorName() );
        //rlDoc.setVendorNumber( poDoc.getVendorNumber() );
        rlDoc.setVendorLine1Address( poDoc.getVendorLine1Address() );
        rlDoc.setVendorLine2Address( poDoc.getVendorLine2Address() );
        rlDoc.setVendorCityName( poDoc.getVendorCityName() );
        rlDoc.setVendorStateCode( poDoc.getVendorStateCode() );
        rlDoc.setVendorPostalCode( poDoc.getVendorPostalCode() );
        rlDoc.setVendorCountryCode( poDoc.getVendorCountryCode() );
        //rlDoc.setVendorAddressGeneratedIdentifier( poDoc.getVendorAddressGeneratedIdentifier() );
        //rlDoc.setVendorAddressInternationalProvinceName( poDoc.getVendorAddressInternationalProvinceName() );
        //rlDoc.setVendorCustomerNumber( poDoc.getVendorCustomerNumber() );
        rlDoc.setVendorDetailAssignedIdentifier( poDoc.getVendorDetailAssignedIdentifier() );
        rlDoc.setVendorHeaderGeneratedIdentifier( poDoc.getVendorHeaderGeneratedIdentifier() );
        
        //copy delivery
        rlDoc.setDeliveryBuildingCode( poDoc.getDeliveryBuildingCode() );
        rlDoc.setDeliveryBuildingLine1Address( poDoc.getDeliveryBuildingLine1Address() );
        rlDoc.setDeliveryBuildingLine2Address( poDoc.getDeliveryBuildingLine2Address() );
        rlDoc.setDeliveryBuildingName( poDoc.getDeliveryBuildingName() );        
        rlDoc.setDeliveryBuildingRoomNumber( poDoc.getDeliveryBuildingRoomNumber() );
        rlDoc.setDeliveryCampusCode( poDoc.getDeliveryCampusCode() );
        rlDoc.setDeliveryCityName( poDoc.getDeliveryCityName() );
        rlDoc.setDeliveryCountryCode( poDoc.getDeliveryCountryCode() );
        rlDoc.setDeliveryInstructionText( poDoc.getDeliveryInstructionText() );
        rlDoc.setDeliveryPostalCode( poDoc.getDeliveryPostalCode() );
        rlDoc.setDeliveryRequiredDate( poDoc.getDeliveryRequiredDate() );
        rlDoc.setDeliveryRequiredDateReasonCode( poDoc.getDeliveryRequiredDateReasonCode() );
        rlDoc.setDeliveryStateCode( poDoc.getDeliveryStateCode() );
        rlDoc.setDeliveryToEmailAddress( poDoc.getDeliveryToEmailAddress() );
        rlDoc.setDeliveryToName( poDoc.getDeliveryToName() );
        rlDoc.setDeliveryToPhoneNumber( poDoc.getDeliveryToPhoneNumber() );
                
        //copy purchase order items
        /*for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) poDoc.getItems()) {
            rlDoc.getItems().add(new ReceivingLineItem(poi, this));
        }*/
        
    }

}
