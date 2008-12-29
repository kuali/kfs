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

import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;

/**
 * Document Authorizer for the Requisition document.
 */
public class RequisitionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    

//    /**
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
//        RequisitionDocument requisitionDocument = (RequisitionDocument) document;
//        if (requisitionDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
//            flags.setCanSave(true);
//        }
//
//        return flags;
//    }

}

