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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;

/**
 * Document Authorizer for the Assign Contract Manager document.
 */
public class ContractManagerAssignmentDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * Overriding this method to prevent users from saving Contract Manager Assignment documents.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canSave(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    // TODO this needs to be done in the presentation controller
//    protected boolean canSave(Document document, Person user) {
//        return false;
//    }

    // TODO this needs to be done in the presentation controller, since it is not user-related
//    /**
//     * Override this method to add extra validation, so that when there's no requistion to
//     * assign contract manager, an error mesage will be displayed, instead of creating an
//     * ContractManagerAssignmentDocument.
//     * 
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#gcanInitiate(String documentTypeName, Person user)
//     */
//    @Override
//    public void canInitiate(String documentTypeName, Person user) {
//        super.canInitiate(documentTypeName, user);
//
//        int numberOfRequisitions = SpringContext.getBean(RequisitionService.class).getCountOfRequisitionsAwaitingContractManagerAssignment();
//        if (numberOfRequisitions == 0) {
//            throw new DocumentInitiationAuthorizationException(PurapKeyConstants.ERROR_AUTHORIZATION_ACM_INITIATION, new String[] { documentTypeName });
//        }
//    }
    
}

