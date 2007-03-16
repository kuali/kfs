/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.service;

import java.util.List;

import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;


public interface PurchaseOrderService {

    public void save(PurchaseOrderDocument purchaseOrderDocument);
    
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument);   
    public PurchaseOrderPostProcessorService convertDocTypeToService(String documentTypeId);  
    
    public void updateFlagsAndRoute(PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients);
    
    public void completePurchaseOrder(PurchaseOrderDocument po);
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);
    public void setCurrentAndPendingIndicatorsInPostProcessor(PurchaseOrderDocument newPO, String workflowState);

}
