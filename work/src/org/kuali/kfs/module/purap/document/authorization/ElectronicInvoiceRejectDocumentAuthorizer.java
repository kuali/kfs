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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the PREQ document.
 */
public class ElectronicInvoiceRejectDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, Person user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName("KFS", authorizedWorkgroup);
        if (group == null) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found");
        }
        return org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), group.getGroupId());
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Map getEditMode(Document document, Person user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        ElectronicInvoiceRejectDocument eirDoc = (ElectronicInvoiceRejectDocument) document;

//        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preq);

        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        boolean fullDocumentEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) document);

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            // only allow full entry if status allows it

            if (!fullDocumentEntryCompleted) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }

        editModeMap.put(editMode, "TRUE");

        // always show amount after full entry
        if (fullDocumentEntryCompleted) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY, "TRUE");
        }

        return editModeMap;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPrincipalName())) {
            flags.setCanBlanketApprove(true);
        }

        ElectronicInvoiceRejectDocument eirDoc = (ElectronicInvoiceRejectDocument) document;
//        if (StringUtils.equals(eirDoc.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
//            flags.setCanSave(false);
//            flags.setCanClose(true);
//            flags.setCanCancel(false);
//            flags.setCanDisapprove(false);
//        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}

