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
package org.kuali.module.purap.rules;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.bo.PurapEnterableItem;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.ContinuePurapRule;
import org.kuali.module.purap.service.ReceivingService;

public class ReceivingLineDocumentRule extends DocumentRuleBase implements ContinuePurapRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateReceivingLineDocument(receivingLineDocument);
        valid &= isReceivingDetailsAvailable(receivingLineDocument);
        
        return valid;
    }

    private boolean isReceivingDetailsAvailable(ReceivingLineDocument receivingLineDocument){
        for (ReceivingLineItem item : (List<ReceivingLineItem>) receivingLineDocument.getItems()) {
            if (!((PurapEnterableItem)item).isConsideredEntered()) {
                String[] parameters = new String[] { item.getItemLineNumber().toString() };
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINEITEM_REQUIRED,parameters);
                return false;
            }
        }
        return true;
    }
    
    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        
        boolean valid = true;
        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= hasRequiredFieldsForContinue(receivingLineDocument);
        valid &= canCreateReceivingLineDocument(receivingLineDocument);
        
        return valid;
    }
    
    /**
     * Make sure the required fields on the init screen are filled in.
     * 
     * @param receivingLineDocument
     * @return
     */
    private boolean hasRequiredFieldsForContinue(ReceivingLineDocument receivingLineDocument){
        
        boolean valid = true;
        
        if (ObjectUtils.isNull(receivingLineDocument.getPurchaseOrderIdentifier())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            valid &= false;
        }

        if (ObjectUtils.isNull(receivingLineDocument.getShipmentReceivedDate())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.ReceivingLineDocumentStrings.VENDOR_DATE);
            valid &= false;
        }

        return valid;
    }
    
    /**
     * Determines if it is valid to create a receiving line document.  Only one
     * receiving line document can be active at any time per purchase order document.
     * 
     * @param receivingLineDocument
     * @return
     */
    private boolean canCreateReceivingLineDocument(ReceivingLineDocument receivingLineDocument){
        
        boolean valid = true;
        
        if( SpringContext.getBean(ReceivingService.class).canCreateReceivingLineDocument(receivingLineDocument.getPurchaseOrderIdentifier(), receivingLineDocument.getDocumentNumber()) == false){
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_RECEIVING_LINE_DOCUMENT_ACTIVE_FOR_PO, receivingLineDocument.getDocumentNumber(), receivingLineDocument.getPurchaseOrderIdentifier().toString());
        }
         
        return valid;
    }

}
