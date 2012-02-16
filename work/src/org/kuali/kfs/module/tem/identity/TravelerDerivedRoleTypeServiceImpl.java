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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelerDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private DocumentService documentService;
    
    {
        requiredAttributes.add(KimAttributes.DOCUMENT_NUMBER);
    }

    /**
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>(1);
        if (qualification!=null && !qualification.isEmpty()) {
            final String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
            if ( StringUtils.isNotBlank( documentNumber ) ) {
                TravelArrangerDocument doc = arrangerDocument(documentNumber);
                if(doc != null) {
                    members.add(new RoleMembershipInfo(null,null,doc.getProfile().getPrincipalId(),Role.PRINCIPAL_MEMBER_TYPE,null));
                } else {
                    final TravelDocument document = getTravelDocument(documentNumber);
                    if (document != null){
                        members.add( new RoleMembershipInfo(null,null,document.getTraveler().getPrincipalId(),Role.PRINCIPAL_MEMBER_TYPE,null) );
                    }
                }
                
            }
        }
        return members;
    }

    private TravelArrangerDocument arrangerDocument(String documentNumber) {
        try{
            Document doc = getDocumentService().getByDocumentHeaderId(documentNumber);
            if(TemConstants.TravelDocTypes.TRAVEL_ARRANGER_DOCUMENT.equals(doc.getDocumentHeader().getWorkflowDocument().getDocumentType())) {
                return (TravelArrangerDocument)doc;
            } else {
                return null;
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * @param documentNumber
     * @return
     */
    protected TravelDocument getTravelDocument(String documentNumber){
        try{
            return (TravelDocument)getDocumentService().getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

}
