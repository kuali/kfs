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
package org.kuali.kfs.module.purap.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.role.service.impl.RouteLogDerivedRoleTypeServiceImpl;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleMembership.Builder;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;

public class RelatedDocumentDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    protected static final String SOURCE_DOCUMENT_ROUTER_ROLE_NAME = "Source Document Router";
    protected static final String SENSITIVE_RELATED_DOCUMENT_INITATOR_OR_REVIEWER_ROLE_NAME = "Sensitive Related Document Initiator Or Reviewer";

    private DocumentService documentService;
    private PurapService purapService;
    private RoleService roleManagementService;

    /**
     * This service will accept the following attributes: Document Number Context: An fyi to the initiator - in the case of
     * Automatic Purchase Orders (apo), the fyi is supposed to go to the requisition router. Otherwise, it should go to the PO
     * router. Requirements: - KFS-PURAP Source Document Router - for Automated Purchase Order, Requisition router according to
     * KR-WKFLW Router role / for normal Purchase Order, Purchase Order router according to KR-WKFLW Router
     *
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if(qualification!=null && !qualification.isEmpty()){
            if (SOURCE_DOCUMENT_ROUTER_ROLE_NAME.equals(roleName)) {
                try {
                    PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) getDocumentService().getByDocumentHeaderId(qualification.get(KFSPropertyConstants.DOCUMENT_NUMBER));
                    if (document != null) {
                        PurchasingAccountsPayableDocument sourceDocument = document.getPurApSourceDocumentIfPossible();
                        if (sourceDocument != null && StringUtils.isNotBlank(sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByPrincipalId()) ) {
                            Map<String,String> roleQualifier = new HashMap<String,String>(1);
                            roleQualifier.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, sourceDocument.getDocumentNumber() );
                            Builder roleMember = RoleMembership.Builder.create(null,null,sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByPrincipalId(),KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE,roleQualifier);
                            members.add( roleMember.build());
                        }
                    }
                }
                catch (WorkflowException e) {
                    throw new RuntimeException("Unable to load document in getPrincipalIdsFromApplicationRole", e);
                }
            }
            else if (SENSITIVE_RELATED_DOCUMENT_INITATOR_OR_REVIEWER_ROLE_NAME.equals(roleName)) {
                for (String documentId : getPurapService().getRelatedDocumentIds(new Integer(qualification.get(PurapKimAttributes.ACCOUNTS_PAYABLE_PURCHASING_DOCUMENT_LINK_IDENTIFIER)))) {
                    Map<String,String> tempQualification = new HashMap<String,String>(1);
                    tempQualification.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentId);
                    for ( String principalId : getRoleService().getRoleMemberPrincipalIds(KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE, RouteLogDerivedRoleTypeServiceImpl.INITIATOR_OR_REVIEWER_ROLE_NAME, tempQualification) ) {
                        Builder roleMember = RoleMembership.Builder.create(null,null,principalId,KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE,tempQualification);
                        members.add( roleMember.build());

                    }
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

    protected RoleService getRoleService() {
        if (roleManagementService == null) {
            roleManagementService = KimApiServiceLocator.getRoleService();
        }
        return roleManagementService;
    }
}
