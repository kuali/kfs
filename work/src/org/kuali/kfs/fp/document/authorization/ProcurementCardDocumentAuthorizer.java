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
package org.kuali.kfs.fp.document.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Procurement Card document.
 */
public class ProcurementCardDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

 // TODO fix for kim
//    /**
//     * Overrides to call super and then blanketly reset the actions not allowed on the procurment card document.
//     * 
//     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));
//
//        flags.setCanErrorCorrect(false); // PCDO doesn't allow error correction
//
//        flags.setCanCancel(false); // PCDO cannot be cancelled
//
//        flags.setCanDisapprove(false); // PCDO cannot be disapproved
//
//        flags.setCanCopy(false); // PCDO cannot be copied
//
//        return flags;
//    }

    /**
     * Override to set the editMode to fullEntry if the routing is at the first account review node (PCDO has 2), second account
     * review functions as normal.
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List activeNodes = getCurrentRouteLevels(workflowDocument);

        Map editModeMap = new HashMap();
        // FULL_ENTRY only if: a) person has an approval request, b) we are at the correct level, c) it's not a correction
        // document, d) it is not an ADHOC request (important so that ADHOC don't get full entry).
        if (workflowDocument.isApprovalRequested() && activeNodes.contains(RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT) && (((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentInErrorNumber() == null) && !document.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            editModeMap.put(KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY, "TRUE");
        }
        else {
            editModeMap = super.getEditMode(document, user);
        }

        return editModeMap;
    }
}

