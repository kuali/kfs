/*
 * Copyright 2008-2009 The Kuali Foundation
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
