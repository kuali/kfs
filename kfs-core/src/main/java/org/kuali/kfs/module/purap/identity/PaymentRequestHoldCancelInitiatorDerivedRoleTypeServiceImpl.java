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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleMembership.Builder;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.DocumentService;

public class PaymentRequestHoldCancelInitiatorDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    protected DocumentService documentService;

   List<String> requiredAttributes = new ArrayList<String>(1);
    {
        requiredAttributes.add( KimConstants.AttributeConstants.DOCUMENT_NUMBER );

    }

   @Override
   public boolean isCheckRequiredAttributes() {
       return true;
   }
    
   @Override
   public List<String> getRequiredAttributes() {
       return Collections.unmodifiableList(requiredAttributes);
   }
    
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if(qualification!=null && !qualification.isEmpty()){
            try {
                AccountsPayableDocumentBase document = (AccountsPayableDocumentBase) getDocumentService().getByDocumentHeaderId(qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER));
                if ((document != null) && (document.getLastActionPerformedByUser() != null)) {
                    Builder roleMember = RoleMembership.Builder.create(null,null,document.getLastActionPerformedByUser().getPrincipalId(),KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE,null);

                    members.add( roleMember.build());
                    
                }
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Unable to load document in getPrincipalIdsFromApplicationRole: " + qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER), e);
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
}
