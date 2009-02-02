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
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.role.service.impl.RouteLogDerivedRoleTypeServiceImpl;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;

public class RelatedDocumentDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    private static final String SOURCE_DOCUMENT_ROUTER_ROLE_NAME = "Source Document Router";
    private static final String SENSITIVE_RELATED_DOCUMENT_INITATOR_OR_REVIEWER_ROLE_NAME = "Sensitive Related Document Initiator Or Reviewer";

    private DocumentService documentService;
    private PurapService purapService;
    private RoleManagementService roleManagementService;

    /**
     * This service will accept the following attributes: Document Number Context: An fyi to the initiator - in the case of
     * Automatic Purchase Orders (apo), the fyi is supposed to go to the requisition router. Otherwise, it should go to the PO
     * router. Requirements: - KFS-PURAP Source Document Router - for Automated Purchase Order, Requisition router according to
     * KR-WKFLW Router role / for normal Purchase Order, Purchase Order router according to KR-WKFLW Router
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
        if (SOURCE_DOCUMENT_ROUTER_ROLE_NAME.equals(roleName)) {
            try {
                PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) getDocumentService().getByDocumentHeaderId(qualification.get(PurapKimAttributes.DOCUMENT_NUMBER));
                if (document != null) {
                    PurchasingAccountsPayableDocument sourceDocument = document.getPurApSourceDocumentIfPossible();
                    if (sourceDocument != null) {
                        AttributeSet roleQualifier = new AttributeSet(1);
                        roleQualifier.put(KfsKimAttributes.DOCUMENT_NUMBER, sourceDocument.getDocumentNumber() );
                        members.add( new RoleMembershipInfo(null,null,sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByPrincipalId(),KimRole.PRINCIPAL_MEMBER_TYPE,roleQualifier) );
                    }
                }
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Unable to load document in getPrincipalIdsFromApplicationRole", e);
            }
        }
        else if (SENSITIVE_RELATED_DOCUMENT_INITATOR_OR_REVIEWER_ROLE_NAME.equals(roleName)) {
            for (String documentId : getPurapService().getRelatedDocumentIds(new Integer(qualification.get(PurapKimAttributes.ACCOUNTS_PAYABLE_PURCHASING_DOCUMENT_LINK_IDENTIFIER)))) {
                AttributeSet tempQualification = new AttributeSet();
                tempQualification.put(PurapKimAttributes.DOCUMENT_NUMBER, documentId);
                for ( String principalId : getRoleManagementService().getRoleMemberPrincipalIds(KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, RouteLogDerivedRoleTypeServiceImpl.INITIATOR_OR_REVIEWER_ROLE_NAME, tempQualification) ) {
                    members.add( new RoleMembershipInfo(null,null,principalId,KimRole.PRINCIPAL_MEMBER_TYPE,tempQualification) );
                }
            }
        }
        return members;
    }

    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    protected PurapService getPurapService() {
        if (purapService == null) {
            purapService = SpringContext.getBean(PurapService.class);
        }
        return purapService;
    }

    protected RoleManagementService getRoleManagementService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleManagementService.class);
        }
        return roleManagementService;
    }
}