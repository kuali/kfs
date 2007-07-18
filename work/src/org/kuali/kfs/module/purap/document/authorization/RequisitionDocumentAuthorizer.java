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
package org.kuali.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants.WorkflowConstants.RequisitionDocument.NodeDetails;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

/**
 * Document Authorizer for the Requisition document.
 * 
 */
public class RequisitionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        //anyone with access to the system can complete a REQ document
        return true;
    }

    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        RequisitionDocument reqDocument = (RequisitionDocument)document;
        
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute()) {
            if (ObjectUtils.isNotNull(reqDocument.getVendorHeaderGeneratedIdentifier())) {
                editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_VENDOR_ENTRY, "TRUE");
            }
        }
        
        if (workflowDocument.stateIsEnroute()) {
            ChartUser chartUser = (ChartUser) user.getModuleUser(ChartUser.MODULE_ID);
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            String editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

            /**
             * CONTENT ROUTE LEVEL - Approvers can edit full detail on Requisition except they cannot change the CHART/ORG.
             */
            if (currentRouteLevels.contains(NodeDetails.CONTENT_REVIEW) && workflowDocument.isApprovalRequested()) {
                // FULL_ENTRY will be set by super which is fine; also set content lock
                editMode = PurapAuthorizationConstants.RequisitionEditMode.LOCK_CONTENT_ENTRY;
            }

            /**
             * FISCAL OFFICER ROUTE LEVEL - Approvers can edit only the accounting lines that they own and no other detail on REQ.
             */
            else if (currentRouteLevels.contains(NodeDetails.ACCOUNT_REVIEW)) {
                List lineList = new ArrayList();
                for (Iterator iter = reqDocument.getItems().iterator(); iter.hasNext();) {
                    RequisitionItem item = (RequisitionItem) iter.next();
                    lineList.addAll(item.getSourceAccountingLines());
                }

                if (workflowDocument.isApprovalRequested() && userOwnsAnyAccountingLine(chartUser, lineList)) {
                    // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
                    editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                    editMode = AuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
                }
            }
            
            /**
             * SEP of DUTIES ROUTE LEVEL - Approvers can only approve or diapprove.
             */
            else if (currentRouteLevels.contains(NodeDetails.SEPARAION_OF_DUTIES_REVIEW)) {
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
            }

            /**
             * SUB-ACCOUNT ROUTE LEVEL - Approvers cannot edit any detail on REQ.
             * BASE ORG REVIEW ROUTE LEVEL - Approvers cannot edit any detail on REQ.
             * SEPARATION OF DUTIES ROUTE LEVEL - Approvers cannot edit any detail on REQ.
             */
            else {
                //VIEW_ENTRY that is already being set is sufficient. 
            }

            editModeMap.put(editMode, "TRUE");
        }

        return editModeMap;
    }

}