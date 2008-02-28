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

    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc, String poDocId) {
        
        if(rlDoc == null){
            rlDoc = new ReceivingLineDocument();
        }
                             
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = null;
        poDoc = purchaseOrderService.getPurchaseOrderByDocumentNumber(poDocId);

        if(poDoc != null){
            rlDoc.populateReceivingLineFromPurchaseOrder(poDoc);
        }                
        
    }

}
