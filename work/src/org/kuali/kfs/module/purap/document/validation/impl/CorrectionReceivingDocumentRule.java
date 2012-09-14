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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurapEnterableItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CorrectionReceivingDocumentRule extends DocumentRuleBase {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        CorrectionReceivingDocument correctionReceivingDocument = (CorrectionReceivingDocument)document;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateCorrectionReceivingDocument(correctionReceivingDocument);
        valid &= isAtLeastOneItemEntered(correctionReceivingDocument);
        
        return valid;
    }


    protected boolean isAtLeastOneItemEntered(ReceivingDocument receivingDocument){
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            if (((PurapEnterableItem)item).isConsideredEntered()) {
                //if any item is entered return true
                return true;
            }
        }
        //if no items are entered return false
        GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINEITEM_REQUIRED);
        return false;
        
    }    
    /**
     * Determines if it is valid to create a receiving correction document.  Only one
     * receiving correction document can be active at any time per receiving line document.
     * 
     * @param receivingCorrectionDocument
     * @return
     */
    protected boolean canCreateCorrectionReceivingDocument(CorrectionReceivingDocument correctionReceivingDocument){
        
        boolean valid = true;
        
        if( SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(correctionReceivingDocument.getLineItemReceivingDocument(), correctionReceivingDocument.getDocumentNumber()) == false){
            valid &= false;
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.LINE_ITEM_RECEIVING_DOCUMENT_NUMBER, PurapKeyConstants.ERROR_RECEIVING_CORRECTION_DOCUMENT_ACTIVE_FOR_RCV_LINE, correctionReceivingDocument.getDocumentNumber(), correctionReceivingDocument.getLineItemReceivingDocumentNumber());
        }
         
        return valid;
    }

}
