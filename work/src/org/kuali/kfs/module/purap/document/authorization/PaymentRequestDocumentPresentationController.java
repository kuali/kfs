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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PaymentRequestDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) document;
        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preqDocument);

        if (preqDocAuth.isFullEntryCompleted()) {
            return false;
        }

        // if the hold or cancel indicator is true, don't allow editing
        if (preqDocument.isHoldIndicator() || preqDocument.isPaymentRequestedCancelIndicator()) {
            return false;
        }

        if (preqDocAuth.isAdHocRequested()) {
            return false;
        }

        return super.canEdit(document);
    }

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PaymentRequestDocument preqDocument = (PaymentRequestDocument)document;
        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preqDocument);
        Set<String> editModes = new HashSet<String>();
        
        // always show amount after full entry
        if (preqDocAuth.isFullEntryCompleted()) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);
        }

        // make sure ap user can edit certain fields
        if (preqDocAuth.canEditPreExtractFields() && !preqDocAuth.isAdHocRequested() && !preqDocument.isPaymentRequestedCancelIndicator()) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT);
        }

        if (preqDocAuth.isInitiateStatus()) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB);
        }
        else {
            //FIXME can we still set the editmode to false?
//            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "FALSE");
        }
        
        if (ObjectUtils.isNotNull(preqDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_VENDOR_ENTRY);
        }
        
        //TODO check this logic (used to check FULL_ENTRY)
        if(canEdit(preqDocument)){
            //Use tax indicator editing is enabled
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.USE_TAX_INDICATOR_CHANGEABLE);
            
            if (!preqDocument.isUseTaxIndicator()) {
                //if full entry, and not use tax, allow editing
                editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_AMOUNT_CHANGEABLE);
            }
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
        }


        //FIXME is this the right status?  should it check to see if there is data too?
        // after tax is approved, the tax tab is viewable to everyone
        if (PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(preqDocument.getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_INFO_VIEWABLE);
        }

        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
        if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
            // expense_entry was already added in super
            // add amount edit mode
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);

            // only do line item check if the hold/cancel indicator is false, otherwise document editing should be turned off.
            if (!preqDocument.isHoldIndicator() && !preqDocument.isPaymentRequestedCancelIndicator()) {
                List lineList = new ArrayList();
                for (Iterator iter = preqDocument.getItems().iterator(); iter.hasNext();) {
                    PaymentRequestItem item = (PaymentRequestItem) iter.next();
                    lineList.addAll(item.getSourceAccountingLines());
                    // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
                    if (item.getItemType().isLineItemIndicator() && item.getSourceAccountingLines().size() == 0) {
                        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY);
                    }
                }
            }
        }

        return editModes;
    }

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

}
