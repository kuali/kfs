/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DocumentAuthorizer class for KRA Budget Documents.
 */
public class BudgetDocumentAuthorizer extends ResearchDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(BudgetDocumentAuthorizer.class);

    // TODO fix for kim
//    /**
//     * Overrides most of the inherited flags so that the buttons behave exactly like they used to in the obsoleted
//     * budgetDocumentControls.tag
//     * 
//     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        LOG.debug("calling BudgetDocumentAuthorizer.getDocumentActionFlags");
//
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//
//        flags.setCanAcknowledge(false);
//        flags.setCanApprove(false);
//        flags.setCanBlanketApprove(false);
//        flags.setCanCancel(false);
//        flags.setCanDisapprove(false);
//        flags.setCanFYI(false);
//        flags.setCanClose(false);
//        flags.setCanSave(true);
//        flags.setCanAnnotate(true);
//
//        BudgetDocument budgetDocument = (BudgetDocument) document;
//
//        // use inherited canRoute, canAnnotate, and canReload values
//
//        return flags;
//    }
}

