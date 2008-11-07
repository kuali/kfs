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
package org.kuali.kfs.module.cab.document.web.struts;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.service.PurApLineDocumentService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class PurApLineAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    PurApLineService purApLineService = SpringContext.getBean(PurApLineService.class);
    PurApInfoService purApInfoService = SpringContext.getBean(PurApInfoService.class);
    PurApLineDocumentService purApLineDocumentService = SpringContext.getBean(PurApLineDocumentService.class);

    /**
     * Handle start action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;
        if (purApLineForm.getPurchaseOrderIdentifier() == null) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_EMPTY);
        }
        else {
            // set non-persistent order from PurAp Purchase Order document
            purApInfoService.setPurchaseOrderFromPurAp(purApLineForm);

            // save PurAp document list into form
            buildPurApDocList(purApLineForm);

            if (!purApLineForm.getPurApDocs().isEmpty()) {
                // set item pre-populated fields
                purApLineService.buildPurApItemAssetList(purApLineForm);
                // create session object for current processing
                createPurApLineSession(purApLineForm.getPurchaseOrderIdentifier());
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private void createPurApLineSession(Integer purchaseOrderIdentifier) {
        GlobalVariables.getUserSession().addObject(CabConstants.CAB_PURAP_SESSION.concat(purchaseOrderIdentifier.toString()), new PurApLineSession());
    }

    /**
     * Build PurchasingAccountsPayableDocument list in which all documents have the same PO_ID.
     * 
     * @param purApLineForm
     */
    protected void buildPurApDocList(PurApLineForm purApLineForm) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, purApLineForm.getPurchaseOrderIdentifier());
        Collection<PurchasingAccountsPayableDocument> purApDocs = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(PurchasingAccountsPayableDocument.class, cols, CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, true);

        if (purApDocs == null || purApDocs.isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_INVALID, purApLineForm.getPurchaseOrderIdentifier().toString());
        }
        else {
            boolean existActiveDoc = false;
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                if (ObjectUtils.isNotNull(purApDoc) && purApDoc.isActive()) {
                    // If there exists active document, set the existActiveDoc indicator.
                    existActiveDoc = true;
                    break;
                }
            }
            purApLineForm.getPurApDocs().addAll(purApDocs);
            // If no active item exists or no exist document, display a message.
            if (!existActiveDoc) {
                GlobalVariables.getMessageList().add(CabKeyConstants.MESSAGE_NO_ACTIVE_PURAP_DOC);
            }
        }
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        GlobalVariables.getMessageList().add(CabKeyConstants.MESSAGE_CAB_CHANGES_SAVED_SUCCESS);
        // get the current processing object from session
        PurApLineSession purApLineSession = retrievePurApLineSession(purApLineForm);
        // persistent changes to CAB tables
        purApLineService.processSaveBusinessObjects(purApLineForm, purApLineSession);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handling for screen close. Default action is return to caller.
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        // Create question page for save before close.
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

        // logic for close question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KNSConstants.CONFIRMATION_QUESTION, KNSConstants.MAPPING_CLOSE, "");
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            PurApLineSession purApLineSession = retrievePurApLineSession(purApLineForm);
            if ((KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                purApLineService.processSaveBusinessObjects(purApLineForm, purApLineSession);
            }
            // remove current processing object from session
            removePurApLineSession(purApLineForm.getPurchaseOrderIdentifier());
        }

        return mapping.findForward(KNSConstants.MAPPING_PORTAL);
    }

    /**
     * Remove PurApLineSession object from user session.
     * 
     * @param purApLineForm
     */
    private void removePurApLineSession(Integer purchaseOrderIdentifier) {
        GlobalVariables.getUserSession().removeObject(CabConstants.CAB_PURAP_SESSION.concat(purchaseOrderIdentifier.toString()));
    }


    /**
     * This method handles split action. Create one item with split quantity
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward split(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        // Get the line item for applying split action.
        PurchasingAccountsPayableItemAsset selectedLineItem = getSelectedLineItem((PurApLineForm) form);

        String errorPath = CabPropertyConstants.PurApLineForm.PURAP_DOCS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionPurApDocIndex() + KFSConstants.SQUARE_BRACKET_RIGHT + "." + CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionItemAssetIndex() + KFSConstants.SQUARE_BRACKET_RIGHT;
        GlobalVariables.getErrorMap().addToErrorPath(errorPath);
        // check user input split quantity.
        checkSplitQty(selectedLineItem, errorPath);
        GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);

        // apply split when error free
        if (GlobalVariables.getErrorMap().isEmpty() && selectedLineItem != null) {
            PurApLineSession purApLineSession = retrievePurApLineSession(purApLineForm);
            // create a new item with split quantity from selected item
            purApLineService.processSplit(selectedLineItem, purApLineSession.getActionsTakenHistory());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Get PurApLineSession object from user session.
     * 
     * @param purApLineForm
     * @return
     */
    private PurApLineSession retrievePurApLineSession(PurApLineForm purApForm) {
        PurApLineSession purApLineSession = (PurApLineSession) GlobalVariables.getUserSession().retrieveObject(CabConstants.CAB_PURAP_SESSION.concat(purApForm.getPurchaseOrderIdentifier().toString()));
        if (purApLineSession == null) {
            purApLineSession = new PurApLineSession();
            GlobalVariables.getUserSession().addObject(CabConstants.CAB_PURAP_SESSION.concat(purApForm.getPurchaseOrderIdentifier().toString()), purApLineSession);
        }
        return purApLineSession;
    }

    /**
     * Check user input splitQty. It must be required and can't be zero or greater than current quantity.
     * 
     * @param itemAsset
     * @param errorPath
     */
    protected void checkSplitQty(PurchasingAccountsPayableItemAsset itemAsset, String errorPath) {
        KualiDecimal splitQty = itemAsset.getSplitQty();
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        if (splitQty == null) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_REQUIRED);
        }
        else if (splitQty.isLessEqual(KualiDecimal.ZERO) || splitQty.isGreaterEqual(oldQty)) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_INVALID, oldQty.toString());
        }
        return;
    }

    /**
     * Merge Action includes merge all functionality.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward merge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApForm = (PurApLineForm) form;
        List<PurchasingAccountsPayableItemAsset> mergeLines = purApLineService.getSelectedMergeLines(purApForm);

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        // logic for trade-in allowance question
        if (question != null) {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                purApLineService.processMerge(mergeLines, purApForm, purApLineSession);
            }
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // validating...
        validateMergeAction(purApForm, mergeLines);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            // Display a warning message without blocking the action if TI indicator exists but TI allowance not exist.
            if (purApLineService.isTradeInIndicatorExist(mergeLines) & !purApLineService.isTradeInAllowanceExist(purApForm)) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.MERGE, "");
            }

            // handle merging lines including merge all situation.
            purApLineService.processMerge(mergeLines, purApForm, purApLineSession);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Check if the merge action is valid or not.
     * 
     * @param purApForm
     * @param mergeLines
     */
    protected void validateMergeAction(PurApLineForm purApForm, List<PurchasingAccountsPayableItemAsset> mergeLines) {
        // check if the user entered merge quantity and merge description
        checkMergeRequiredFields(purApForm);

        // Check if the selected merge lines violate the business constraints.
        if (purApLineService.isMergeAllAction(purApForm)) {
            checkMergeAllValid(mergeLines, purApForm);
        }
        else {
            checkMergeLinesValid(mergeLines, purApForm);
        }

        // Check the associated pre-tagging data entries.
        checkPreTagValidForMerge(mergeLines, purApForm.getPurchaseOrderIdentifier());
    }

    /**
     * If to be merged items have: (1) No Pretag data: No problem; (2) 1 Pretag data entry: Associate this one with the new item
     * created after merge;(3) 1+ Pretag data entries: Display error, user has to manually fix data
     * 
     * @param mergeLines
     */
    private void checkPreTagValidForMerge(List<PurchasingAccountsPayableItemAsset> mergeLines, Integer purchaseOrderIdentifier) {
        Map validNumberMap = getItemLineNumberHashMap(mergeLines);

        if (!validNumberMap.isEmpty() && validNumberMap.size() > 1 && purApLineService.isMultipleTagExisting(purchaseOrderIdentifier, validNumberMap.keySet())) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_MERGE_WITH_PRETAGGING);
        }
    }

    /**
     * Build a Hashmap for itemLineNumber
     * 
     * @param itemLines
     * @return
     */
    private Map getItemLineNumberHashMap(List<PurchasingAccountsPayableItemAsset> itemLines) {
        Map validNumberMap = new HashMap<Integer, Integer>();
        for (PurchasingAccountsPayableItemAsset item : itemLines) {
            if (item.getItemLineNumber() != null) {
                validNumberMap.put(item.getItemLineNumber(), item.getItemLineNumber());
            }
        }
        return validNumberMap;
    }

    /**
     * For merge all, check if exists Trade-in allowance pending for allocate.
     * 
     * @param mergeLines
     * @param purApForm
     */
    private void checkMergeAllValid(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm) {
        if (purApLineService.isTradeInAllowanceExist(purApForm) && purApLineService.isTradeInIndicatorExist(mergeLines)) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
        }
    }

    /**
     * Check if merge lines selected are allowed to continue this action. Constraints include: merge lines must more than 1; no
     * additional charges pending for allocate.
     * 
     * @param mergeLines
     * @param purApForm
     */
    private void checkMergeLinesValid(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm) {
        if (mergeLines.size() <= 1) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_MERGE_LINE_SELECTED);
        }
        else {
            // if merge for different document lines and that document has additional charge allocation pending, additional charges
            // should be allocated first.
            if (purApLineService.isAdditionalChargePending(mergeLines)) {
                GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
            }

            // if merge lines has indicator exists and trade-in allowance is pending for allocation, we will block this action.
            if (purApLineService.isTradeInIndicatorExist(mergeLines) && purApLineService.isTradeInAllowanceExist(purApForm)) {
                GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
            }
        }
    }


    /**
     * Check the required fields entered for merge.
     */
    private void checkMergeRequiredFields(PurApLineForm purApForm) {
        if (purApForm.getMergeQty() == null) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.MERGE_QTY, CabKeyConstants.ERROR_MERGE_QTY_EMPTY);
        }

        if (StringUtils.isBlank(purApForm.getMergeDesc())) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.MERGE_DESC, CabKeyConstants.ERROR_MERGE_DESCRIPTION_EMPTY);
        }
    }


    /**
     * Update the item quantity value from a decimal(less than 1) to 1.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward percentPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApform = (PurApLineForm) form;
        PurchasingAccountsPayableItemAsset itemAsset = getSelectedLineItem(purApform);

        if (itemAsset != null) {
            PurApLineSession purApLineSession = retrievePurApLineSession(purApform);
            purApLineService.processPercentPayment(itemAsset, purApLineSession.getActionsTakenHistory());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Allocate line items including allocate additional charges functionality.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward allocate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApForm = (PurApLineForm) form;
        PurchasingAccountsPayableItemAsset allocateSourceLine = getSelectedLineItem(purApForm);
        List<PurchasingAccountsPayableItemAsset> allocateTargetLines = purApLineService.getAllocateTargetLines(allocateSourceLine, purApForm);

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        // logic for trade-in allowance question
        if (question != null) {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                if (!purApLineService.processAllocate(allocateSourceLine, allocateTargetLines, purApForm, purApLineSession)) {
                    GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ALLOCATE_NO_TARGET_ACCOUNT);
                }
            }
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Check if this allocate is valid.
        validateAllocateAction(allocateSourceLine, allocateTargetLines, purApForm);
        if (GlobalVariables.getErrorMap().isEmpty()) {
            if (!allocateSourceLine.isAdditionalChargeNonTradeInIndicator() & !allocateSourceLine.isTradeInAllowance() & (allocateSourceLine.isItemAssignedToTradeInIndicator() || purApLineService.isTradeInIndicatorExist(allocateTargetLines)) & purApLineService.isTradeInAllowanceExist(purApForm)) {
                // TI indicator exists in either source or target lines, but TI allowance not found, bring up a warning message
                // to confirm this action.
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.ALLOCATE, "");
            }
            if (!purApLineService.processAllocate(allocateSourceLine, allocateTargetLines, purApForm, purApLineSession)) {
                GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ALLOCATE_NO_TARGET_ACCOUNT);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Check if the line items are allowed to allocate.
     * 
     * @param selectedLine
     * @param allocateTargetLines
     * @param purApForm
     */
    protected void validateAllocateAction(PurchasingAccountsPayableItemAsset allocateSourceLine, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, PurApLineForm purApForm) {
        // if no target selected...
        if (allocateTargetLines.isEmpty()) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ALLOCATE_NO_LINE_SELECTED);
        }

        // For allocate trade-in allowance, additional charges(non trade-in) must be allocated before allocate trade-in.
        if (allocateSourceLine.isTradeInAllowance() && purApLineService.isAdditionalChargeExist(purApForm)) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
        }
        // For line item, we need to check...
        if (!allocateSourceLine.isAdditionalChargeNonTradeInIndicator() & !allocateSourceLine.isTradeInAllowance()) {
            allocateTargetLines.add(allocateSourceLine);
            // Pending additional charges(non trade-in) can't associate with either source line or target lines.
            if (purApLineService.isAdditionalChargePending(allocateTargetLines)) {
                GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
            }

            if (purApLineService.isTradeInIndicatorExist(allocateTargetLines) && purApLineService.isTradeInAllowanceExist(purApForm)) {
                // For line item, check if trade-in allowance allocation pending.
                GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
            }
            allocateTargetLines.remove(allocateSourceLine);
        }

    }

    /**
     * Get the user selected line item and set the link reference to document
     * 
     * @param purApLineForm
     * @return
     */
    private PurchasingAccountsPayableItemAsset getSelectedLineItem(PurApLineForm purApLineForm) {
        PurchasingAccountsPayableDocument purApDoc = purApLineForm.getPurApDocs().get(purApLineForm.getActionPurApDocIndex());
        PurchasingAccountsPayableItemAsset selectedItem = purApDoc.getPurchasingAccountsPayableItemAssets().get(purApLineForm.getActionItemAssetIndex());
        selectedItem.setPurchasingAccountsPayableDocument(purApDoc);
        return selectedItem;
    }


    /**
     * Get the user selected document.
     * 
     * @param purApLineForm
     * @return
     */
    private PurchasingAccountsPayableDocument getSelectedPurApDoc(PurApLineForm purApLineForm) {
        return purApLineForm.getPurApDocs().get(purApLineForm.getActionPurApDocIndex());
    }

    /**
     * Handle apply payment action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward applyPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApForm = (PurApLineForm) form;
        PurchasingAccountsPayableItemAsset selectedLine = getSelectedLineItem(purApForm);

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question != null) {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // create CAMS asset payment global document.
                return createApplyPaymentDocument(mapping, purApForm, selectedLine);
            }
            else {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        if (selectedLine.isItemAssignedToTradeInIndicator()) {
            // TI indicator exists, bring up a warning message to confirm this action.
            return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.APPLY_PAYMENT, "");
        }

        // create CAMS asset payment global document.
        return createApplyPaymentDocument(mapping, purApForm, selectedLine);
    }

    /**
     * Create CAMS asset payment document.
     * 
     * @param mapping
     * @param purApForm
     * @param selectedLine
     * @param purApLineSession
     * @return
     * @throws WorkflowException
     */
    private ActionForward createApplyPaymentDocument(ActionMapping mapping, PurApLineForm purApForm, PurchasingAccountsPayableItemAsset selectedLine) throws WorkflowException {
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        String documentNumber;
        // create CAMS asset payment global document.
        if ((documentNumber = purApLineDocumentService.processApplyPayment(selectedLine, purApForm, purApLineSession)) != null) {
            String forwardUrl = getDocHandlerForwardLink(CabConstants.ASSET_PAYMENT_DOCUMENT, documentNumber);
            return new ActionForward(forwardUrl, true);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Handle create asset action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApForm = (PurApLineForm) form;
        PurchasingAccountsPayableItemAsset selectedLine = getSelectedLineItem(purApForm);

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question != null) {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // If PurAp user set capitalAssetNumbers for apply Asset Payment document, bring up a warning message for
                // confirmation.
                if (isSettingAssetsInPurAp(selectedLine)) {
                    return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
                }
                else {
                    return createAssetGlobalDocument(mapping, purApForm, selectedLine);
                }
            }
            else if (CabConstants.SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                return createAssetGlobalDocument(mapping, purApForm, selectedLine);
            }
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // validate selected line item
        validateCreateAssetAction(selectedLine);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            // TI indicator exists, bring up a warning message to confirm this action.
            if (selectedLine.isItemAssignedToTradeInIndicator()) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
            }
            // If PurAp user set capitalAssetNumbers for apply Asset Payment document, bring up a warning message to confirm using
            // Asset Global document.
            else if (isSettingAssetsInPurAp(selectedLine)) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION, KNSServiceLocator.getKualiConfigurationService().getPropertyString(CabKeyConstants.QUESTION_SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL), KNSConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
            }
            else {
                return createAssetGlobalDocument(mapping, purApForm, selectedLine);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * check if PurAp set CAMS Assets information
     * 
     * @param selectedLine
     * @return
     */
    private boolean isSettingAssetsInPurAp(PurchasingAccountsPayableItemAsset selectedLine) {
        return selectedLine.getPurApItemAssets() != null && !selectedLine.getPurApItemAssets().isEmpty();
    }

    /**
     * Create asset global document
     * 
     * @param mapping
     * @param purApForm
     * @param selectedLine
     * @param purApLineSession
     * @return
     * @throws WorkflowException
     */
    private ActionForward createAssetGlobalDocument(ActionMapping mapping, PurApLineForm purApForm, PurchasingAccountsPayableItemAsset selectedLine) throws WorkflowException {
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        String documentNumber = null;
        // create CAMS asset global document.
        if ((documentNumber = purApLineDocumentService.processCreateAsset(selectedLine, purApForm, purApLineSession)) != null) {
            // forward link to asset global
            String forwardUrl = getDocHandlerForwardLink(CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT, documentNumber);
            return new ActionForward(forwardUrl, true);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Return Asset Global forwarding URL.
     * 
     * @param request
     * @param documentNumber
     * @return
     */
    private String getDocHandlerForwardLink(String docTypeName, String documentNumber) {
        DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        DocumentType documentType = documentTypeService.findByName(docTypeName);
        String docHandlerUrl = documentType.getDocHandlerUrl();

        if (docHandlerUrl.indexOf("?") == -1) {
            docHandlerUrl += "?";
        }
        else {
            docHandlerUrl += "&";
        }

        docHandlerUrl += KNSConstants.PARAMETER_DOC_ID + "=" + documentNumber + "&" + KNSConstants.PARAMETER_COMMAND + "=" + KEWConstants.DOCSEARCH_COMMAND;
        return docHandlerUrl;
    }

    /**
     * Validate selected line item for asset global creation.
     * 
     * @param selectedLine
     */
    protected void validateCreateAssetAction(PurchasingAccountsPayableItemAsset selectedLine) {
        KualiDecimal integerOne = new KualiDecimal(1);
        KualiDecimal quantity = selectedLine.getAccountsPayableItemQuantity();
        // check if item quantity is a fractional value greater than 1.
        if (quantity.isGreaterThan(integerOne) && quantity.mod(integerOne).isNonZero()) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
        }
        // if quantity is between (0,1) , set it to 1. 
        else if (quantity.isGreaterThan(KualiDecimal.ZERO) && quantity.isLessThan(integerOne)) {
            selectedLine.setAccountsPayableItemQuantity(integerOne);
        }

    }
}
