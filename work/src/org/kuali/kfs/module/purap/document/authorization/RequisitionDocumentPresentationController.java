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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;


public class RequisitionDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {

    @Override
    public boolean canEdit(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument)document;
        if (!RequisitionStatuses.APPDOC_IN_PROCESS.equals(reqDocument.getApplicationDocumentStatus()) &&
                !RequisitionStatuses.APPDOC_AWAIT_CONTENT_REVIEW.equals(reqDocument.getApplicationDocumentStatus()) &&
                !RequisitionStatuses.APPDOC_AWAIT_HAS_ACCOUNTING_LINES.equals(reqDocument.getApplicationDocumentStatus()) &&
                !RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocument.getApplicationDocumentStatus())) {
            //unless the Requisition is in process, awaiting content, awaiting accounting lines or awaiting fiscal, editing is not allowed
            return false;
        }
        return super.canEdit(document);
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        RequisitionDocument reqDocument = (RequisitionDocument)document;

        //if the ENABLE_COMMODITY_CODE_IND system parameter is Y then add this edit mode so that the commodity code fields would display on the document.
        boolean enableCommodityCode = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
        if (enableCommodityCode) {
            editModes.add(RequisitionEditMode.ENABLE_COMMODITY_CODE);
        }

        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(reqDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(RequisitionEditMode.LOCK_VENDOR_ENTRY);
        }

        // if B2B requisition, certain fields are not allowed to be edited
        if (RequisitionSources.B2B.equals(reqDocument.getRequisitionSourceCode())) {
            editModes.add(RequisitionEditMode.LOCK_B2B_ENTRY);
        }

        // if not B2B requisition, the posting year cannot be edited if within a given amount of time set in a parameter
        if (!RequisitionSources.B2B.equals(reqDocument.getRequisitionSourceCode()) &&
                SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear()) {
            editModes.add(RequisitionEditMode.ALLOW_POSTING_YEAR_ENTRY);
        }

        // check if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
            editModes.add(RequisitionEditMode.CLEAR_ALL_TAXES); // always available now if taxes

            if (reqDocument.isUseTaxIndicator()) {
                // don't allow tax editing if doc using use tax
                editModes.add(RequisitionEditMode.LOCK_TAX_AMOUNT_ENTRY);
            }
        }

        // set display mode for Receiving Address section according to parameter value
        boolean displayReceivingAddress = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND);
        if (displayReceivingAddress) {
            editModes.add(RequisitionEditMode.DISPLAY_RECEIVING_ADDRESS);
        }

        // set display mode for Address to Vendor section according to parameter value
        boolean lockAddressToVendor = !SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(PurapConstants.PURAP_NAMESPACE, "Requisition", PurapParameterConstants.ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND);
        if (lockAddressToVendor) {
            editModes.add(RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR);
        }

        // CONTENT ROUTE LEVEL - Approvers can edit full detail on Requisition except they cannot change the CHART/ORG.
        //to be removed
        //for app doc status
        if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_CONTENT_REVIEW) ||
            reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_HAS_ACCOUNTING_LINES))
        {
            editModes.add(RequisitionEditMode.LOCK_CONTENT_ENTRY);
        }

         // FISCAL OFFICER ROUTE LEVEL - Approvers can edit only the accounting lines that they own and no other detail on REQ.
        // to be removed
        //for app doc status
        else if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ACCOUNT)) {

            // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
            editModes.add(RequisitionEditMode.RESTRICT_FISCAL_ENTRY);

            List lineList = new ArrayList();
            for (Iterator iter = reqDocument.getItems().iterator(); iter.hasNext();) {
                RequisitionItem item = (RequisitionItem) iter.next();
                lineList.addAll(item.getSourceAccountingLines());
                // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one

                if (item.getSourceAccountingLines().size() == 0) {
                    editModes.add(RequisitionEditMode.RESTRICT_FISCAL_ENTRY);
                    //FIXME hjs-this is a problem because we can't put the id in the map anymore
//                    editModeMap.put(RequisitionEditMode.ALLOW_ITEM_ENTRY, item.getItemIdentifier());
                }
            }

        }

        // allow capital asset tab to be editable based on requisition status
        String reqDocumentStatus = reqDocument.getApplicationDocumentStatus();
        if (RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocumentStatus)
                || RequisitionStatuses.APPDOC_IN_PROCESS.equals(reqDocumentStatus))
        {
            editModes.add(RequisitionEditMode.ALLOW_CAPITAL_ASSET_EDITS);
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isEnroute()) {
             Set<String> nodeNames = workflowDocument.getNodeNames();
             for (Iterator<String> iterator = nodeNames.iterator(); iterator.hasNext();) {
                    String nodeNamesNode = iterator.next();
                    if (RequisitionStatuses.NODE_ACCOUNT.equals(nodeNamesNode)) {
                        // disable the button for setup distribution
                        editModes.add(RequisitionEditMode.DISABLE_SETUP_ACCT_DISTRIBUTION);
                        // disable the button for remove accounts from all items
                        editModes.add(RequisitionEditMode.DISABLE_REMOVE_ACCTS);
                        // disable the button for remove commodity codes from all items
                        if (editModes.contains(RequisitionEditMode.ENABLE_COMMODITY_CODE)) {
                            editModes.remove(RequisitionEditMode.ENABLE_COMMODITY_CODE);
                        }
                    }
             }
        }

        return editModes;
    }

    @Override
    public boolean canCopy(Document document) {
        //  disallow copying until the doc is saved
     //   WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument();

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isInitiated() && !workflowDocument.isSaved()) {
            return false;
        }
        return super.canCopy(document);
    }

    @Override
    public boolean canReload(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument) document;

        //  this is a global rule, if the doc is in your queue for ACK, then you lose the reload button
        WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isAcknowledgeRequested()) {
            return false;
        }
        if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_SUBACCOUNT)) {
            return false;
        }
        //  but the non-approvers do

        else if (isDocInRouteNodeNotForCurrentUser(reqDocument, RequisitionStatuses.NODE_SUBACCOUNT)) {
            return true;
        }

        //  while in AccountingHierarchyOrgReview ... org reviewers do NOT get reload button
        else if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ORG_REVIEW)) {
            return false;
        }
        // but the non-approvers do
        else if (isDocInRouteNodeNotForCurrentUser(reqDocument, RequisitionStatuses.NODE_ORG_REVIEW)) {
            return true;
        }

        //  while in SeparationOfDuties ... approvers do NOT get reload button
        else if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_SEPARATION_OF_DUTIES)) {
            return false;
        }
        // but the non-approvers do
        else if (isDocInRouteNodeNotForCurrentUser(reqDocument, RequisitionStatuses.NODE_SEPARATION_OF_DUTIES)) {
            return true;
        }

        return super.canReload(document);
    }

    @Override
    public boolean canSave(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument) document;

        if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ORG_REVIEW)) {
            return false;
        }
        return super.canSave(document);
    }

    /**
     *
     * @param document
     * @param nodeDetails
     * @return
     */
    protected boolean isDocInRouteNodeNotForCurrentUser(Document document, String nodeName) {
        List<String> currentRouteLevels = new ArrayList<String>();

        WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument();
        Set<String> names = workflowDocument.getCurrentNodeNames();

        if (CollectionUtils.isNotEmpty(names)) {
            currentRouteLevels = new ArrayList<String>(names);

            if (currentRouteLevels.contains(nodeName) && !workflowDocument.isApprovalRequested()) {
                return true;
            }
        }
        return false;
    }
}
