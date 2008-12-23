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
package org.kuali.kfs.module.cam.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.DocumentService;

public class AssetDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KimAttributes.DOCUMENT_NUMBER);
    }

    /**
     * Context:
     *  the EquipmentLoanOrReturnDocument document can be accessed by clicking "loan" on the Asset lookup results. 
     *  With this document, a user may decide to lend an asset to someone. 
     *  Therefore, the borrower will want to approve the document confirming that he indeed wants to borrow the item.
     *  
     * Requirement:
     *  The service will take in a document number as a role qualifier. 
     *  It will retrieve the document given the doc ID, extract out the user ID of the borrower 
     *  (the borrowerUniversalIdentifier property of the document object), 
     *  and return this principal ID as the only role member for a given document.
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        
        String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        List<String> principalIds = new ArrayList<String>();
        EquipmentLoanOrReturnDocument document = getEquipmentLoanOrReturnDocument(documentNumber);
        if(document!=null){
            principalIds.add(document.getBorrowerUniversalIdentifier());
        }
        return principalIds;
    }

    /**
     * @param documentNumber
     * @return
     */
    protected EquipmentLoanOrReturnDocument getEquipmentLoanOrReturnDocument(String documentNumber){
        try{
            return (EquipmentLoanOrReturnDocument) 
                    SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            String errorMessage = "Workflow problem while trying to get document using doc id '" + documentNumber + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }
    
}