/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Document Authorizer for the Assign Contract Manager document.
 */
public class AssignContractManagerDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // do not allow this document to be saved; once initiated, it must be routed or canceled
            flags.setCanSave(false);
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

    /**
     * Override this method to add extra validation, so that when there's no requistion to
     * assign contract manager, an error mesage will be displayed, instead of creating an
     * AssignContractManagerDocument.
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#gcanInitiate(String documentTypeName, UniversalUser user)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        super.canInitiate(documentTypeName, user);
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.STATUS_CODE, PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN);
        List<RequisitionDocument> unassignedRequisitions = new ArrayList(SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(RequisitionDocument.class, fieldValues, PurapPropertyConstants.PURAP_DOC_ID, true));
        if (unassignedRequisitions.size()==0) 
            //throw new DocumentInitiationAuthorizationException(new String[] {"somegroup",documentTypeName});
            throw new DocumentInitiationAuthorizationException(PurapKeyConstants.ERROR_AUTHORIZATION_ACM_INITIATION, new String[]{documentTypeName});
            //throw new DocumentInitiationAuthorizationException("error.authorization.workgroupInitiation", new String[]{documentTypeName});
    }
    
}