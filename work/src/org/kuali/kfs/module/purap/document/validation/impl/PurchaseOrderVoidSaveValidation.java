/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurchaseOrderVoidSaveValidation extends GenericValidation {

    private PurchaseOrderService purchaseOrderService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchaseOrderDocument document = (PurchaseOrderDocument)event.getDocument();
        
        // Check that the PO is not null
        if (ObjectUtils.isNull(document)) {
            throw new ValidationException("Purchase Order Void document was null on validation.");
        }
        else {
            PurchaseOrderDocument currentPO = purchaseOrderService.getCurrentPurchaseOrder(document.getPurapDocumentIdentifier());
            // The PO must be in OPEN status.
            if (!StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.OPEN) && !StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.PENDING_VOID) && !StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.PENDING_PRINT)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.ERROR_PURCHASE_ORDER_STATUS_NOT_REQUIRED_STATUS, PurchaseOrderStatuses.OPEN);
            }

            // Check that the user is in purchasing workgroup.
            valid &= purchaseOrderService.isPurchasingUser(document, "void");
        }
        return valid;
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

}
