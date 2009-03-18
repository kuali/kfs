/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Business Prerules applicable to purchase order document.
 */
public class PurchaseOrderAmendmentDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * Overrides the method in PromptBeforeValidationBase.
     * 
     * @param document The purchase order amendment document upon which we're performing the prerules logic.
     * @return boolean true if it passes the pre rules conditions.
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {

        boolean preRulesOK = true;

        preRulesOK &= validateReceivingRequiredIndicator((PurchaseOrderAmendmentDocument)document) ;
        
        return preRulesOK;
    }

    /**
     * The receiving required indicator can only be set to Yes if there are no
     * outstanding payment requests.
     * 
     * @param purchaseOrderDocument
     * @return
     */
    private boolean validateReceivingRequiredIndicator(PurchaseOrderAmendmentDocument poAmendmentDocument){
        
        boolean valid = true;

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(KFSConstants.DOCUMENT_ERRORS);

        if( poAmendmentDocument.isReceivingDocumentRequiredIndicator() && 
            SpringContext.getBean(PaymentRequestService.class).hasActivePaymentRequestsForPurchaseOrder(poAmendmentDocument.getPurapDocumentIdentifier()) ){
            
            //set receiving required indicator back to no and add errors
            poAmendmentDocument.setReceivingDocumentRequiredIndicator(false);
            event.setActionForwardName(KFSConstants.MAPPING_BASIC);            
            errorMap.putError(PurapPropertyConstants.RECEIVING_DOCUMENT_REQUIRED_ID, PurapKeyConstants.ERROR_PURCHASE_ORDER_RECEIVING_DOC_REQUIRED_ID_PENDING_PREQ);
            valid = false;
        }
        
        return valid;
    }
    
}
