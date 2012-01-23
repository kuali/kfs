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
package org.kuali.kfs.module.cab.document.web.struts;


import java.util.Collection;
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
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.service.PurApLineDocumentService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurApLineAction extends CabActionBase {
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
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_EMPTY);
        }
        else {
            // set Contact Email Address and Phone Number from PurAp Purchase Order document
            purApInfoService.setPurchaseOrderFromPurAp(purApLineForm);

            // save PurAp document list into form
            buildPurApDocList(purApLineForm);

            if (!purApLineForm.getPurApDocs().isEmpty()) {
                // set item pre-populated fields
                purApLineService.buildPurApItemAssetList(purApLineForm.getPurApDocs());
                // create session object for current processing
                createPurApLineSession(purApLineForm.getPurchaseOrderIdentifier());
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void createPurApLineSession(Integer purchaseOrderIdentifier) {
        GlobalVariables.getUserSession().addObject(CabConstants.CAB_PURAP_SESSION.concat(purchaseOrderIdentifier.toString()), new PurApLineSession());
    }

    protected void clearPurApLineSession(Integer purchaseOrderIdentifier) {
        if (purchaseOrderIdentifier != null) {
            GlobalVariables.getUserSession().removeObject(CabConstants.CAB_PURAP_SESSION.concat(purchaseOrderIdentifier.toString()));
        }
    }

    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;
        if (purApLineForm.getPurchaseOrderIdentifier() == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_EMPTY);
        }
        purApLineForm.getPurApDocs().clear();
        // clear the session and reload the page
        clearPurApLineSession(purApLineForm.getPurchaseOrderIdentifier());
        return start(mapping, form, request, response);
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
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_INVALID, purApLineForm.getPurchaseOrderIdentifier().toString());
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
            purApLineForm.getPurApDocs().clear();
            purApLineForm.getPurApDocs().addAll(purApDocs);
            setupObjectRelationship(purApLineForm.getPurApDocs());
            // If no active item exists or no exist document, display a message.
            if (!existActiveDoc) {
                KNSGlobalVariables.getMessageList().add(CabKeyConstants.MESSAGE_NO_ACTIVE_PURAP_DOC);
            }
        }
    }

    /**
     * Setup relationship from account to item and item to doc. In this way, we keep all working objects in the same view as form.
     * 
     * @param purApDocs
     */
    protected void setupObjectRelationship(List<PurchasingAccountsPayableDocument> purApDocs) {
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                item.setPurchasingAccountsPayableDocument(purApDoc);
                for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                    account.setPurchasingAccountsPayableItemAsset(item);
                }
            }
        }
    }

    /**
     * Cancels the action and returns to portal main page
     * 
     * @param mapping {@link ActionMapping}
     * @param form {@link ActionForm}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link ActionForward}
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KRADConstants.MAPPING_PORTAL);
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;
        // get the current processing object from session
        PurApLineSession purApLineSession = retrievePurApLineSession(purApLineForm);
        // persistent changes to CAB tables
        purApLineService.processSaveBusinessObjects(purApLineForm.getPurApDocs(), purApLineSession);
        KNSGlobalVariables.getMessageList().add(CabKeyConstants.MESSAGE_CAB_CHANGES_SAVED_SUCCESS);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handling for screen close. Default action is return to caller.
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        // Create question page for save before close.
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        // logic for close question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyValueAsString(RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_CLOSE, "");
        }
        else {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            PurApLineSession purApLineSession = retrievePurApLineSession(purApLineForm);
            if ((KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                purApLineService.processSaveBusinessObjects(purApLineForm.getPurApDocs(), purApLineSession);
            }
            // remove current processing object from session
            removePurApLineSession(purApLineForm.getPurchaseOrderIdentifier());
        }

        return mapping.findForward(KRADConstants.MAPPING_PORTAL);
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

        if (selectedLineItem == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        String errorPath = CabPropertyConstants.PurApLineForm.PURAP_DOCS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionPurApDocIndex() + KFSConstants.SQUARE_BRACKET_RIGHT + "." + CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionItemAssetIndex() + KFSConstants.SQUARE_BRACKET_RIGHT;
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
        // check user input split quantity.
        checkSplitQty(selectedLineItem, errorPath);
        GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);

        // apply split when error free
        if (GlobalVariables.getMessageMap().hasNoErrors() && selectedLineItem != null) {
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
        if (itemAsset.getSplitQty() == null)
            itemAsset.setSplitQty(KualiDecimal.ZERO);

        if (itemAsset.getAccountsPayableItemQuantity() == null)
            itemAsset.setAccountsPayableItemQuantity(KualiDecimal.ZERO);

        KualiDecimal splitQty = itemAsset.getSplitQty();
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        KualiDecimal maxAllowQty = oldQty.subtract(new KualiDecimal(0.1));

        if (splitQty == null) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_REQUIRED);
        }
        else if (splitQty.isLessEqual(KualiDecimal.ZERO) || splitQty.isGreaterEqual(oldQty)) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_INVALID, maxAllowQty.toString());
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
        boolean isMergeAll = purApLineService.isMergeAllAction(purApForm.getPurApDocs());
        List<PurchasingAccountsPayableItemAsset> mergeLines = purApLineService.getSelectedMergeLines(isMergeAll, purApForm.getPurApDocs());

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // logic for trade-in allowance question
        if (question != null) {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if (CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                if (purApLineService.mergeLinesHasDifferentObjectSubTypes(mergeLines)) {
                    // check if objectSubTypes are different and bring the obj sub types warning message
                    String warningMessage = generateObjectSubTypeQuestion();
                    return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage, KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.MERGE, "");
                }
                else {
                    performMerge(purApForm, mergeLines, isMergeAll);
                }
            }
            else if (CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                performMerge(purApForm, mergeLines, isMergeAll);
            }

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        boolean tradeInAllowanceInAllLines = purApLineService.isTradeInAllowanceExist(purApForm.getPurApDocs());
        boolean tradeInIndicatorInSelectedLines = purApLineService.isTradeInIndExistInSelectedLines(mergeLines);
        // validating...
        validateMergeAction(purApForm, mergeLines, isMergeAll, tradeInAllowanceInAllLines, tradeInIndicatorInSelectedLines);

        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            // Display a warning message without blocking the action if TI indicator exists but TI allowance not exist.
            if (tradeInIndicatorInSelectedLines && !tradeInAllowanceInAllLines) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.MERGE, "");
            }
            else if (purApLineService.mergeLinesHasDifferentObjectSubTypes(mergeLines)) {
                // check if objectSubTypes are different and bring the obj sub types warning message
                String warningMessage = generateObjectSubTypeQuestion();
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage, KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.MERGE, "");
            }
            else {
                performMerge(purApForm, mergeLines, isMergeAll);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Generate the question string for different object sub type codes.
     * 
     * @return
     */
    protected String generateObjectSubTypeQuestion() {
        String parameterDetail = "(module:" + KRADUtils.getNamespaceAndComponentSimpleName(AssetGlobal.class) + ")";
        ConfigurationService kualiConfiguration = this.getConfigurationService();

        String continueQuestion = kualiConfiguration.getPropertyValueAsString(CamsKeyConstants.CONTINUE_QUESTION);
        String warningMessage = kualiConfiguration.getPropertyValueAsString(CabKeyConstants.QUESTION_DIFFERENT_OBJECT_SUB_TYPES) + " " + CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS + " " + parameterDetail + ". " + continueQuestion;
        return warningMessage;
    }
 
    /**
     * Merge with service help.
     * 
     * @param purApForm
     * @param mergeLines
     */
    protected void performMerge(PurApLineForm purApForm, List<PurchasingAccountsPayableItemAsset> mergeLines, boolean isMergeAll) {
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        // handle merging lines including merge all situation.
        retrieveUserInputForMerge(mergeLines.get(0), purApForm);
        purApLineService.processMerge(mergeLines, purApLineSession.getActionsTakenHistory(), isMergeAll);
        // add all other mergeLines except the first one into processedItem list.
        mergeLines.remove(0);
        purApLineSession.getProcessedItems().addAll(mergeLines);
        clearForMerge(purApForm);
    }

    /**
     * Retrieve user input merge quantity and merge description.
     * 
     * @param firstItem
     * @param purApForm
     */
    protected void retrieveUserInputForMerge(PurchasingAccountsPayableItemAsset firstItem, PurApLineForm purApForm) {
        if (ObjectUtils.isNotNull(firstItem)) {
            // Set new value for quantity and description.
            firstItem.setAccountsPayableItemQuantity(purApForm.getMergeQty());
            firstItem.setAccountsPayableLineItemDescription(purApForm.getMergeDesc());
        }
    }

    /**
     * Clear user input after merge.
     * 
     * @param purApForm
     */
    protected void clearForMerge(PurApLineForm purApForm) {
        // reset user input values.
        purApLineService.resetSelectedValue(purApForm.getPurApDocs());
        purApForm.setMergeQty(null);
        purApForm.setMergeDesc(null);
        purApForm.setSelectAll(false);
    }

    /**
     * Check if the merge action is valid or not.
     * 
     * @param purApForm
     * @param mergeLines
     */
    protected void validateMergeAction(PurApLineForm purApForm, List<PurchasingAccountsPayableItemAsset> mergeLines, boolean isMergeAll, boolean tradeInAllowanceInAllLines, boolean tradeInIndicatorInSelectedLines) {
        // check if the user entered merge quantity and merge description
        checkMergeRequiredFields(purApForm);

        // Check if the selected merge lines violate the business constraints.
        if (isMergeAll) {
            checkMergeAllValid(tradeInAllowanceInAllLines, tradeInIndicatorInSelectedLines);
        }
        else {
            checkMergeLinesValid(mergeLines, tradeInAllowanceInAllLines, tradeInIndicatorInSelectedLines);
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
    protected void checkPreTagValidForMerge(List<PurchasingAccountsPayableItemAsset> mergeLines, Integer purchaseOrderIdentifier) {
        Map validNumberMap = getItemLineNumberMap(mergeLines);

        if (!validNumberMap.isEmpty() && validNumberMap.size() > 1 && purApLineService.isMultipleTagExisting(purchaseOrderIdentifier, validNumberMap.keySet())) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_MERGE_WITH_PRETAGGING);
        }
    }

    /**
     * Build a Hashmap for itemLineNumber since itemLines could exist duplicate itemLineNumber
     * 
     * @param itemLines
     * @return
     */
    protected Map getItemLineNumberMap(List<PurchasingAccountsPayableItemAsset> itemLines) {
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
    protected void checkMergeAllValid(boolean tradeInAllowanceInAllLines, boolean tradeInIndicatorInSelectedLines) {
        if (tradeInAllowanceInAllLines && tradeInIndicatorInSelectedLines) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
        }
    }

    /**
     * Check if merge lines selected are allowed to continue this action. Constraints include: merge lines must more than 1; no
     * additional charges pending for allocate.
     * 
     * @param mergeLines
     * @param purApForm
     */
    protected void checkMergeLinesValid(List<PurchasingAccountsPayableItemAsset> mergeLines, boolean tradeInAllowanceInAllLines, boolean tradeInIndicatorInSelectedLines) {
        if (mergeLines.size() <= 1) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_MERGE_LINE_SELECTED);
        }
        else {
            // if merge for different document lines and that document has additional charge allocation pending, additional charges
            // should be allocated first.
            if (purApLineService.isAdditionalChargePending(mergeLines)) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
            }

            // if merge lines has indicator exists and trade-in allowance is pending for allocation, we will block this action.
            if (tradeInIndicatorInSelectedLines && tradeInAllowanceInAllLines) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
            }
        }
    }


    /**
     * Check the required fields entered for merge.
     */
    protected void checkMergeRequiredFields(PurApLineForm purApForm) {
        if (purApForm.getMergeQty() == null) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.MERGE_QTY, CabKeyConstants.ERROR_MERGE_QTY_EMPTY);
        }

        if (StringUtils.isBlank(purApForm.getMergeDesc())) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.MERGE_DESC, CabKeyConstants.ERROR_MERGE_DESCRIPTION_EMPTY);
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
        if (allocateSourceLine == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        List<PurchasingAccountsPayableItemAsset> allocateTargetLines = purApLineService.getAllocateTargetLines(allocateSourceLine, purApForm.getPurApDocs());

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // logic for trade-in allowance question
        if (question != null) {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                if (purApLineService.allocateLinesHasDifferentObjectSubTypes(allocateTargetLines, allocateSourceLine)) {
                    // check if objectSubTypes are different and bring the obj sub types warning message
                    String warningMessage = generateObjectSubTypeQuestion();
                    return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage, KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.ALLOCATE, "");
                }
                else {
                    performAllocate(purApForm, allocateSourceLine, allocateTargetLines);
                }
            }
            else if (CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                performAllocate(purApForm, allocateSourceLine, allocateTargetLines);
            }

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        boolean targetLineHasTradeIn = purApLineService.isTradeInIndExistInSelectedLines(allocateTargetLines);
        boolean hasTradeInAllowance = purApLineService.isTradeInAllowanceExist(purApForm.getPurApDocs());
        // Check if this allocate is valid.
        validateAllocateAction(allocateSourceLine, allocateTargetLines, targetLineHasTradeIn, hasTradeInAllowance, purApForm.getPurApDocs());
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            // TI indicator exists in either source or target lines, but TI allowance not found, bring up a warning message
            // to confirm this action.
            if (!allocateSourceLine.isAdditionalChargeNonTradeInIndicator() && !allocateSourceLine.isTradeInAllowance() && (allocateSourceLine.isItemAssignedToTradeInIndicator() || targetLineHasTradeIn) && hasTradeInAllowance) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.ALLOCATE, "");
            }
            else if (purApLineService.allocateLinesHasDifferentObjectSubTypes(allocateTargetLines, allocateSourceLine)) {
                // check if objectSubTypes are different and bring the obj sub types warning message
                String warningMessage = generateObjectSubTypeQuestion();
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage, KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.ALLOCATE, "");
            }
            else {
                performAllocate(purApForm, allocateSourceLine, allocateTargetLines);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Allocate with service help.
     * 
     * @param purApForm
     * @param allocateSourceLine
     * @param allocateTargetLines
     */
    protected void performAllocate(PurApLineForm purApForm, PurchasingAccountsPayableItemAsset allocateSourceLine, List<PurchasingAccountsPayableItemAsset> allocateTargetLines) {
        PurApLineSession purApLineSession = retrievePurApLineSession(purApForm);
        if (!purApLineService.processAllocate(allocateSourceLine, allocateTargetLines, purApLineSession.getActionsTakenHistory(), purApForm.getPurApDocs(), false)) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ALLOCATE_NO_TARGET_ACCOUNT);
        }
        else {
            purApLineSession.getProcessedItems().add(allocateSourceLine);
            // clear select check box
            purApLineService.resetSelectedValue(purApForm.getPurApDocs());
            purApForm.setSelectAll(false);
        }
    }

    /**
     * Check if the line items are allowed to allocate.
     * 
     * @param selectedLine
     * @param allocateTargetLines
     * @param purApForm
     */
    protected void validateAllocateAction(PurchasingAccountsPayableItemAsset allocateSourceLine, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, boolean targetLineHasTradeIn, boolean hasTradeInAllowance, List<PurchasingAccountsPayableDocument> purApDocs) {
        // if no target selected...
        if (allocateTargetLines.isEmpty()) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ALLOCATE_NO_LINE_SELECTED);
        }

        // For allocate trade-in allowance, additional charges(non trade-in) must be allocated before allocate trade-in.
        if (allocateSourceLine.isTradeInAllowance() && purApLineService.isAdditionalChargeExistInAllLines(purApDocs)) {
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
        }
        // For line item, we need to check...
        if (!allocateSourceLine.isAdditionalChargeNonTradeInIndicator() && !allocateSourceLine.isTradeInAllowance()) {
            allocateTargetLines.add(allocateSourceLine);
            // Pending additional charges(non trade-in) can't associate with either source line or target lines.
            if (purApLineService.isAdditionalChargePending(allocateTargetLines)) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_ADDL_CHARGE_PENDING);
            }

            // For line item, check if trade-in allowance allocation pending.
            if (targetLineHasTradeIn && hasTradeInAllowance) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_TRADE_IN_PENDING);
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
        if (!selectedItem.isActive()) {
            selectedItem = null;
        }
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

        if (selectedLine == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question != null) {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
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
            return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.APPLY_PAYMENT, "");
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
        if ((documentNumber = purApLineDocumentService.processApplyPayment(selectedLine, purApForm.getPurApDocs(), purApLineSession, purApForm.getRequisitionIdentifier())) != null) {
            purApForm.setDocumentNumber(documentNumber);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

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

        if (selectedLine == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question != null) {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if ((CabConstants.TRADE_IN_INDICATOR_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // If PurAp user set capitalAssetNumbers for apply Asset Payment document, bring up a warning message for
                // confirmation.
                if (isSettingAssetsInPurAp(selectedLine)) {
                    return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
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

        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            // TI indicator exists, bring up a warning message to confirm this action.
            if (selectedLine.isItemAssignedToTradeInIndicator()) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.TRADE_IN_INDICATOR_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_TRADE_IN_INDICATOR_EXISTING), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
            }
            // If PurAp user set capitalAssetNumbers for apply Asset Payment document, bring up a warning message to confirm using
            // Asset Global document.
            else if (isSettingAssetsInPurAp(selectedLine)) {
                return this.performQuestionWithoutInput(mapping, form, request, response, CabConstants.SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CabKeyConstants.QUESTION_SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL), KRADConstants.CONFIRMATION_QUESTION, CabConstants.Actions.CREATE_ASSET, "");
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
        if ((documentNumber = purApLineDocumentService.processCreateAsset(selectedLine, purApForm.getPurApDocs(), purApLineSession, purApForm.getRequisitionIdentifier())) != null) {
            // forward link to asset global
            purApForm.setDocumentNumber(documentNumber);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
            GlobalVariables.getMessageMap().putError(CabPropertyConstants.PurApLineForm.PURAP_DOCS, CabKeyConstants.ERROR_FRACTIONAL_QUANTITY);
        }
        // if quantity is between (0,1) , set it to 1.
        else if (quantity.isGreaterThan(KualiDecimal.ZERO) && quantity.isLessThan(integerOne)) {
            selectedLine.setAccountsPayableItemQuantity(integerOne);
        }

    }

    protected ParameterService getParameterService() {
        return (ParameterService) SpringContext.getBean(ParameterService.class);
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }
}
