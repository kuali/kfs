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

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.DocumentService;

public class PaymentRequestHoldCancelInitiatorDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    private DocumentService documentService;

    {
        requiredAttributes.add( KimAttributes.DOCUMENT_NUMBER );
        checkRequiredAttributes = true;
    }
    
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
        try {
            PaymentRequestDocument document = (PaymentRequestDocument) getDocumentService().getByDocumentHeaderId(qualification.get(KfsKimAttributes.DOCUMENT_NUMBER));
            if ((document != null) && (document.getLastActionPerformedByUser() != null)) {
                members.add( new RoleMembershipInfo(null,null,document.getLastActionPerformedByUser().getPrincipalId(),Role.PRINCIPAL_MEMBER_TYPE,null) );
            }
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Unable to load document in getPrincipalIdsFromApplicationRole: " + qualification.get(KfsKimAttributes.DOCUMENT_NUMBER), e);
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