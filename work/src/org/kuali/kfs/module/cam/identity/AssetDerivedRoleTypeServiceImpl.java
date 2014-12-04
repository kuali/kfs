/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.DocumentService;

public class AssetDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    protected DocumentService documentService;
    
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
     * @see org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase#getRoleMembersFromDerivedRole(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        if(qualification!=null && !qualification.isEmpty()){
            String documentNumber = qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER);
            if ( StringUtils.isNotBlank( documentNumber ) ) {
                EquipmentLoanOrReturnDocument document = getEquipmentLoanOrReturnDocument(documentNumber);
                if(document!=null){
                    RoleMembership.Builder builder = RoleMembership.Builder.create(null, null, document.getBorrowerUniversalIdentifier(),KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE,null);
                    members.add(builder.build());
                }
            }
        }
        return members;
    }

    /**
     * @param documentNumber
     * @return
     */
    protected EquipmentLoanOrReturnDocument getEquipmentLoanOrReturnDocument(String documentNumber){
        try{
            return (EquipmentLoanOrReturnDocument)getDocumentService().getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

    protected DocumentService getDocumentService() {
        if ( documentService == null ) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }
    
}
