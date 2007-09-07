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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.Note;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.PurQuestionCallback;
import org.kuali.module.purap.web.struts.form.AccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the AP.
 */
public class AccountsPayableActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableActionBase.class);

    /**
     * Performs refresh of objects after a lookup.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase baseForm = (AccountsPayableFormBase) form;
        AccountsPayableDocumentBase document = (AccountsPayableDocumentBase) baseForm.getDocument();

        //TODO (KULPURAP-1573) couldn't this be moved up to purap action base becuase pur documents do the same thing!!!! (then we do not need this method)
        if (StringUtils.equals(baseForm.getRefreshCaller(), VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotBlank(request.getParameter(RicePropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                Integer vendorAddressGeneratedId = document.getVendorAddressGeneratedIdentifier();
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(vendorAddressGeneratedId);
                refreshVendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * This method checks the continuation account indicator and generates warnings if
     * continuation accounts were used to replace original accounts on the document.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        AccountsPayableDocument document = (AccountsPayableDocument) kualiDocumentFormBase.getDocument();
        
        SpringContext.getBean(AccountsPayableService.class).generateExpiredOrClosedAccountWarning(document);
    }

    /**
     * Perform calculation on item line.
     */
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) apForm.getDocument();

        customCalculate(apDoc);

        // doesn't really matter what happens above we still reset the calculate flag
        apForm.setCalculated(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is an overridable area to do calculate specific tasks
     * 
     * @param apDoc
     */
    protected void customCalculate(AccountsPayableDocument apDoc) {
        // do nothing by default
    }
    
    /**
     * 
     * This method checks if calculation is required.  Currently it is required when it has not already been calculated and full document entry
     * status has not already passed.
     * 
     * @param apForm
     * @param purapDocument
     * @return true if calculation is required, false otherwise
     */
    protected boolean requiresCaculate(AccountsPayableFormBase apForm) {
        boolean requiresCalculate = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)apForm.getDocument();
        requiresCalculate = !apForm.isCalculated() && !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(purapDocument);
        return requiresCalculate;
    }

    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        
        //if form is not yet calculated, return and prompt user to calculate
        if (requiresCaculate(apForm)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);            
        }
                
        //recalculate
        customCalculate( (AccountsPayableDocument)apForm.getDocument() );

        //route
        return super.route(mapping, form, request, response);               
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        if (!requiresCaculate(apForm)) {
            return super.save(mapping, form, request, response);
        }
        GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_SAVE_REQUIRES_CALCULATE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * This method prompts for a reason to hold a payment request or credit memo.
     */
    protected ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String notePrefix, String operation, String messageKey, PurQuestionCallback callback) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountsPayableDocumentBase apDocument = (AccountsPayableDocumentBase) kualiDocumentFormBase.getDocument();

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // Start in logic for confirming the close.
        if (question == null) {
            String key = kualiConfiguration.getPropertyString(messageKey);
            String message = StringUtils.replace(key, "{0}", operation);

            // Ask question if not already asked.
            return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                // If 'No' is the button clicked, just reload the doc
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            // Have to check length on value entered.
            String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

            // Build out full message.
            noteText = introNoteMessage + reason;
            int noteTextLength = noteText.length();

            // Get note text max length from DD.
            int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();
            if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                // Figure out exact number of characters that the user can enter.
                int reasonLimit = noteTextMaxLength - noteTextLength;
                if (reason == null) {
                    // Prevent a NPE by setting the reason to a blank string.
                    reason = "";
                }
                
                return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(messageKey), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PAYMENT_REQUEST_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }
        
        // make callback
        callback.doPostQuestion(apDocument, noteText);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}