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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Credit Memo document.
 */
public class CreditMemoForm extends AccountsPayableFormBase {

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public CreditMemoForm() {
        super();
        setDocument(new CreditMemoDocument());
    }

    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(((CreditMemoDocument) getDocument()).getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.CreditMemoDocument.attributes.purapDocumentIdentifier", ((CreditMemoDocument) getDocument()).getPurapDocumentIdentifier().toString()));
        }
        getDocInfo().add(new HeaderField("DataDictionary.CreditMemoDocument.attributes.purapDocumentIdentifier", "Not Available"));
        if (ObjectUtils.isNotNull(((CreditMemoDocument) getDocument()).getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.CreditMemoDocument.attributes.statusCode", ((CreditMemoDocument) getDocument()).getStatus().getStatusDescription()));
        }
        getDocInfo().add(new HeaderField("DataDictionary.CreditMemoDocument.attributes.statusCode", "Not Available"));
    }

    /**
     * Determines if the credit memo document has reached the INITIATE status.
     * 
     * @return - true if credit memo has been initiated, false otherwise
     */
    public boolean isCreditMemoInitiated() {
        return StringUtils.equals(((CreditMemoDocument) getDocument()).getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE);
    }

    /**
     * This method determines if a user is able to reopen a purchase order. This is used by the checkbox "reopen PO" on the credit
     * memo form.
     * 
     * @return - true if able to reopen a purchase order, false otherwise
     */
    public boolean isAbleToReopenPurchaseOrder() {
        boolean valid = false;

        CreditMemoDocument creditMemo = (CreditMemoDocument) this.getDocument();

        if (!creditMemo.isSourceVendor() &&
                SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(creditMemo) == false && isApUser() && PurapConstants.PurchaseOrderStatuses.CLOSED.equals(creditMemo.getPurchaseOrderDocument().getStatusCode())) {

            valid = true;
        }

        return valid;
    }

    /**
     * Helper method to indicate if the current document has reached full document entry.
     * 
     * @return - true if document has been fully entered, false otherwise
     */
    public boolean isFullDocumentEntryCompleted() {
        CreditMemoDocument creditMemo = (CreditMemoDocument) this.getDocument();
        return SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(creditMemo);
    }

    /**
     * Build additional credit memo specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear out the extra buttons array
        extraButtons.clear();

        CreditMemoDocument cmDocument = (CreditMemoDocument) getDocument();

        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        if (getEditingMode().containsKey(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB)) {
            if (getEditingMode().get(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
                addExtraButton("methodToCall.continueCreditMemo", externalImageURL + "buttonsmall_continue.gif", "Continue");
                addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
            }
            else {
                if (SpringContext.getBean(CreditMemoService.class).canHoldCreditMemo(cmDocument, GlobalVariables.getUserSession().getFinancialSystemUser())) {
                    addExtraButton("methodToCall.addHoldOnCreditMemo", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
                }
                else if (SpringContext.getBean(CreditMemoService.class).canRemoveHoldCreditMemo(cmDocument, GlobalVariables.getUserSession().getFinancialSystemUser())) {
                    addExtraButton("methodToCall.removeHoldFromCreditMemo", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
                }

                // add the calculate button
                String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
                boolean isApUser = GlobalVariables.getUserSession().getFinancialSystemUser().isMember(apGroup);
                if ( SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(cmDocument) == false && isApUser ) {
                    addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
                }
            }
        }

        return extraButtons;
    }

    /**
     * Overrides the method in KualiDocumentFormBase to provide only FYI and ACK.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getAdHocActionRequestCodes()
     */
    @Override
    public Map getAdHocActionRequestCodes() {
        Map adHocActionRequestCodes = new HashMap();
        if (getWorkflowDocument() != null) {
            if (getWorkflowDocument().isFYIRequested()) {
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isAcknowledgeRequested()) {
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isApprovalRequested() || getWorkflowDocument().isCompletionRequested() || getWorkflowDocument().stateIsInitiated() || getWorkflowDocument().stateIsSaved()) {
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
        }
        return adHocActionRequestCodes;
    }
}
