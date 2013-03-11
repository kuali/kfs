/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.PermissionTemplate;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

public class FinancialSystemTransactionalDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {
    private static final Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentAuthorizerBase.class);

    /**
     * Overridden to check if document error correction can be allowed here.
     *
     * @see org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document,
     *      org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        if (documentActionsToReturn.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)
                && !(documentActionsToReturn.contains(KRADConstants.KUALI_ACTION_CAN_COPY)
                && canErrorCorrect(document, user))) {
            documentActionsToReturn.remove(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
        }

        if (documentActionsToReturn.contains(KFSConstants.KFS_ACTION_CAN_EDIT_BANK)
                && !canEditBankCode(document, user)) {
            documentActionsToReturn.remove(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
        }

        // CSU 6702 BEGIN
        // rSmart-jkneal-KFSCSU-199-begin mod for adding accounting period edit action
        if (documentActionsToReturn.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && documentActionsToReturn.contains(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION)) {
            // check KIM permission for view, approvers always have permission to view
            if (!document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && !super.isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_PERMISSION, user.getPrincipalId())) {
                documentActionsToReturn.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
            // check KIM permission for edit
            else if (super.isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_PERMISSION, user.getPrincipalId())) {
                documentActionsToReturn.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_DOCUMENT_ACTION);
            }
        }
        // rSmart-jkneal-KFSCSU-199-end mod
        // CSU 6702 END
        return documentActionsToReturn;
    }

    /**
     * Determines if the KIM permission is available to error correct the given document
     *
     * @param document the document to correct
     * @param user the user to check error correction for
     * @return true if the user can error correct, false otherwise
     */
    public boolean canErrorCorrect(Document document, Person user) {
        return isAuthorizedByTemplate(document, KFSConstants.CoreModuleNamespaces.KFS, PermissionTemplate.ERROR_CORRECT_DOCUMENT.name, user.getPrincipalId());
    }

    /**
     * Determines if the KIM permission is available to error correct the given document
     *
     * @param document the document to correct
     * @param user the user to check error correction for
     * @return true if the user can error correct, false otherwise
     */
    public boolean canEditBankCode(Document document, Person user) {
        return isAuthorizedByTemplate(document, KFSConstants.CoreModuleNamespaces.KFS, PermissionTemplate.EDIT_BANK_CODE.name, user.getPrincipalId());
    }

}
