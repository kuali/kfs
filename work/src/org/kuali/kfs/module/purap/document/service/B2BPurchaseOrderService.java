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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.vnd.businessobject.ContractManager;

/**
 * These items will allow a user to send Purchase Orders electronically to vendors
 */
public interface B2BPurchaseOrderService {

    /**
     * Send the Purchase Order
     * 
     * @param po
     * @return Response
     */
    public String sendPurchaseOrder(PurchaseOrderDocument po);

    /**
     * Returns the cxml of the Purchase Order for electronic transmission to the vendor
     * 
     * @param purchaseOrder         PurchaseOrderDocument - PO data
     * @param requisitionInitiator  Person - user that created the Requisition
     * @param password              String - password for PO transmission
     * @param contractManager       ContractManager - contract manager for the PO
     * @param contractManagerEmail  String - email address for the contract manager
     * @param vendorDuns            String - vendor DUNS number for the PO
     * @return String which is the cxml of the PO to send to the vendor
     */
    public String getCxml(PurchaseOrderDocument purchaseOrder, String requisitionInitiatorId, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns);

    /**
     * Verifies that each piece of data required for the PO cXML is present.
     * 
     * @param purchaseOrder
     * @param requisitionInitiator
     * @param password
     * @param contractManager
     * @param contractManagerEmail
     * @param vendorDuns
     * @return
     */
    public String verifyCxmlPOData(PurchaseOrderDocument purchaseOrder, String requisitionInitiatorId, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns);

}
