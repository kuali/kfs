/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
