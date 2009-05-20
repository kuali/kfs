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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;


public class RequisitionDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {

    @Override
    protected boolean canEdit(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument)document;
        if (!RequisitionStatuses.IN_PROCESS.equals(reqDocument.getStatusCode()) &&
                !RequisitionStatuses.AWAIT_CONTENT_REVIEW.equals(reqDocument.getStatusCode()) &&
                !RequisitionStatuses.AWAIT_HAS_ACCOUNTING_LINES.equals(reqDocument.getStatusCode()) &&
                !RequisitionStatuses.AWAIT_FISCAL_REVIEW.equals(reqDocument.getStatusCode())) {
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
        boolean enableCommodityCode = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
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
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);

            if (reqDocument.isUseTaxIndicator()) {
                // don't allow tax editing if doc using use tax
                editModes.add(RequisitionEditMode.LOCK_TAX_AMOUNT_ENTRY);
            }
            else {
                // display the "clear all taxes" button if doc is not using use tax
                editModes.add(RequisitionEditMode.CLEAR_ALL_TAXES);
            }
        }

        // set display mode for Receiving Address section according to parameter value
        boolean displayReceivingAddress = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND);                
        if (displayReceivingAddress) {
            editModes.add(RequisitionEditMode.DISPLAY_RECEIVING_ADDRESS);
        }
            
        // set display mode for Address to Vendor section according to parameter value 
        boolean lockAddressToVendor = !SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Requisition", PurapParameterConstants.ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND);                
        if (lockAddressToVendor) {
            editModes.add(RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR);
        }

        // CONTENT ROUTE LEVEL - Approvers can edit full detail on Requisition except they cannot change the CHART/ORG.
        if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW) ||
                reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.HAS_ACCOUNTING_LINES)) {
            editModes.add(RequisitionEditMode.LOCK_CONTENT_ENTRY);
        }

         // FISCAL OFFICER ROUTE LEVEL - Approvers can edit only the accounting lines that they own and no other detail on REQ.
        else if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW)) {

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

        return editModes;
    }

}
