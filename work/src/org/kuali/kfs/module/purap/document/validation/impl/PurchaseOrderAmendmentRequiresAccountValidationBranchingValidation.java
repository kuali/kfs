/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchaseOrderAmendmentRequiresAccountValidationBranchingValidation extends PurchaseOrderRequiresAccountValidationBranchingValidation {
    
    protected PurchaseOrderService purchaseOrderService;
    protected PurapService purapService;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        PurchaseOrderDocument document = (PurchaseOrderDocument)event.getDocument();
        // If a new unordered item has been added to the purchase order, this is due to Receiving Line document,
        // and items should not have account validation performed initially, until new unordered items review.
        if( SpringContext.getBean(PurchaseOrderService.class).hasNewUnorderedItem(document) &&
            !SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode(document, "New Unordered Items") ){
            return KFSConstants.EMPTY_STRING;
        }else{
            return super.determineBranch(event);
        }
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    
}
