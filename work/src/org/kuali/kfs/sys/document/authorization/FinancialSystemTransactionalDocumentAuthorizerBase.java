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
package org.kuali.kfs.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.AmountTotaling;
import org.kuali.kfs.document.FinancialSystemTransactionalDocument;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentAuthorizerBase.class);

    /**
     * Adds settings for KFS transactional-document-specific flags.
     * 
     * @see org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(Document, UniversalUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling FinancialSystemTransactionalDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");
        FinancialSystemTransactionalDocumentActionFlags flags =  new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        FinancialSystemTransactionalDocument transactionalDocument = (FinancialSystemTransactionalDocument) document;
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (canCopy(workflowDocument.getDocumentType(), user)) {
            flags.setCanErrorCorrect(transactionalDocument.getAllowsErrorCorrection() && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal()));
        }

        // if document implements AmountTotaling interface, then we should display the total
        if (document instanceof AmountTotaling) {
            flags.setHasAmountTotal(true);
        }
        else {
            flags.setHasAmountTotal(false);
        }

        return flags;
    }

}
