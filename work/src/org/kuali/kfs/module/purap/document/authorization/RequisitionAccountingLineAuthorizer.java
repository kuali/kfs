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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.List;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class RequisitionAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String INITIATOR_NODE = "Initiator";
    private static final String CONTENT_REVIEW_NODE = "Organization";

    /**
     * Allow new lines to be rendered at Initiator node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        if (accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(RequisitionAccountingLineAuthorizer.INITIATOR_NODE) || accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(RequisitionAccountingLineAuthorizer.CONTENT_REVIEW_NODE)) return true;
        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }
    
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, 
                                   List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, 
                                   Person currentUser) {
        
        boolean isEditable = super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
        
        if (isEditable){
            isEditable = allowAccountingLineEditable(accountingLineRenderingContexts.get(0).getAccountingLine());
        }
        
        return isEditable;
    }
    
//    @Override
//    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName) {
//        boolean isEditable = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty);
//        if (isEditable){
//            isEditable = allowAccountingLineEditable(accountingLine);
//        }
//        return isEditable;
//    }
//    
//    @Override
//    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty) {
//        boolean isEditable = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty);
//        if (isEditable){
//            isEditable = allowAccountingLineEditable(accountingLine);
//        }
//        return isEditable;
//    }
    
    private boolean allowAccountingLineEditable(AccountingLine accountingLine){
       
        RequisitionAccount reqAccount = (RequisitionAccount)accountingLine;
        List<String> restrictedItemTypesList = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT);
        
        String itemTypeCode = new String();
        //because account distribution accounts are not associated with an item
        if(reqAccount.getRequisitionItem()!=null){
            itemTypeCode = reqAccount.getRequisitionItem().getItemTypeCode();
        }
        return !restrictedItemTypesList.contains(itemTypeCode);
    }
    
}
