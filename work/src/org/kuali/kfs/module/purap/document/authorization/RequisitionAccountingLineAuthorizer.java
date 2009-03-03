/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.Map;

import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class RequisitionAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String INITIATOR_NODE = "Initiator";
    private static final String CONTENT_REVIEW_NODE = "Organization";

    /**
     * Allow new lines to be rendered at Initiator node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty, Map documentActions) {
        if (accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(RequisitionAccountingLineAuthorizer.INITIATOR_NODE) || accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(RequisitionAccountingLineAuthorizer.CONTENT_REVIEW_NODE)) return true;
        return super.renderNewLine(accountingDocument, accountingGroupProperty, documentActions);
    }
    
    
}
