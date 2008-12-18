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
package org.kuali.kfs.module.purap.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class RelatedDocumentDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static DocumentService documentService;
    
    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KimAttributes.DOCUMENT_NUMBER);
    }

    /**
     * This service will accept the following attributes:
     *  Document Number
     *  
     *  Context:
     *  An fyi to the initiator - in the case of Automatic Purchase Orders (apo), the fyi is supposed to go to the requisition router. 
     *  Otherwise, it should go to the PO router.
     *
     *  Requirements:
     *  - KFS-PURAP Source Document Router - 
     *  for Automated Purchase Order, Requisition router according to KR-WKFLW Router role / 
     *  for normal Purchase Order, Purchase Order router according to KR-WKFLW Router
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

        String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        List<String> principalIds = new ArrayList<String>();
        PurchasingAccountsPayableDocument document = getPurchasingAccountsPayableDocument(documentNumber);            //Assuming that if the document is an APO, sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId() 
        //will return the user network id of the requisition router, else it will return the id of the PO router.
        if(document!=null){
            PurchasingAccountsPayableDocument sourceDocument = document.getPurApSourceDocumentIfPossible();
            if(sourceDocument!=null)
                principalIds.add(sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId());
        }
        return principalIds;
    }
    
    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        boolean hasApplicationRole = false;
        PurchasingAccountsPayableDocument document = getPurchasingAccountsPayableDocument(documentNumber);
        if(document!=null){
            PurchasingAccountsPayableDocument sourceDocument = document.getPurApSourceDocumentIfPossible();
            if(sourceDocument!=null)
                hasApplicationRole = 
                    principalId.equals(sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId());
        }
        return hasApplicationRole;
    }

    protected PurchasingAccountsPayableDocument getPurchasingAccountsPayableDocument(String documentNumber){
        try{
            return (PurchasingAccountsPayableDocument)getDocumentService().getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            String errorMessage = "Workflow problem while trying to get document using doc id '" + documentNumber + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * @return the documentService
     */
     protected static DocumentService getDocumentService(){
        if (documentService == null ) {
            documentService = KNSServiceLocator.getDocumentService();
        }
        return documentService;
    }

}