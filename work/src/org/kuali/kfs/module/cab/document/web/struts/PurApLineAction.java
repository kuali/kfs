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
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class PurApLineAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    PurApLineService purApLineService = SpringContext.getBean(PurApLineService.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;
        if (StringUtils.isEmpty(purApLineForm.getPurchaseOrderIdentifier())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_EMPTY);
        }
        else {
            purApLineService.setPurchaseOrderInfo(purApLineForm);
            buildPurApDocList(purApLineForm);

            if (!purApLineForm.getPurApDocs().isEmpty()) {
                purApLineService.buildPurApItemAssetsList(purApLineForm);
                // GlobalVariables.getUserSession().addObject("purApLineSesion", new PurApLineSession());
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Build PurchasingAccountsPayableDocument list from given PO_ID.
     * 
     * @param purApLineForm
     */
    protected void buildPurApDocList(PurApLineForm purApLineForm) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, purApLineForm.getPurchaseOrderIdentifier());
        Collection<PurchasingAccountsPayableDocument> purApDocs = SpringContext.getBean(BusinessObjectService.class).findMatching(PurchasingAccountsPayableDocument.class, cols);

        if (purApDocs == null || purApDocs.isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.ERROR_PO_ID_INVALID, purApLineForm.getPurchaseOrderIdentifier());
        }
        else {
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                // Select active purApDoc
                if (ObjectUtils.isNotNull(purApDoc) && purApDoc.isActive()) {
                    purApLineForm.getPurApDocs().add(purApDoc);
                }
            }
            if (purApLineForm.getPurApDocs().isEmpty()) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.MESSAGE_NO_ACTIVE_PURAP_DOC);
            }
        }
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        GlobalVariables.getMessageList().add(CabKeyConstants.MESSAGE_CAB_CHANGES_SAVED_SUCCESS);
        // PurApLineSession purApLineSession = (PurApLineSession)
        // GlobalVariables.getUserSession().retrieveObject("purApLineSesion");
        purApLineService.processSaveBusinessObjects(purApLineForm);
        // GlobalVariables.getUserSession().removeObject("purApLineSesion");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handling for screen close. Default action is return to caller.
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm) form;

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

        // logic for close question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KNSConstants.CONFIRMATION_QUESTION, KNSConstants.MAPPING_CLOSE, "");
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // TODO: if yes button clicked - save the doc
                PurApLineSession purApLineSession = (PurApLineSession) GlobalVariables.getUserSession().retrieveObject("purApLineSesion");
                purApLineService.processSaveBusinessObjects(purApLineForm);
            }
            // GlobalVariables.getUserSession().removeObject("purApLineSesion");
            // else go to close logic below
        }

        return returnToSender(mapping, purApLineForm);
    }


    /**
     * This method...
     * 
     * @param mapping
     * @param purApLineForm
     * @return
     */
    protected ActionForward returnToSender(ActionMapping mapping, PurApLineForm purApLineForm) {
        return mapping.findForward(KNSConstants.MAPPING_PORTAL);
    }

    /**
     * This method handles split action
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward split(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableDocument purApDoc = getSelectedPurApDoc((PurApLineForm) form);
        PurchasingAccountsPayableItemAsset itemAsset = getSelectedLineItem((PurApLineForm) form);
        PurApLineForm purApLineForm = (PurApLineForm) form;

        String errorPath = CabPropertyConstants.PurApLineForm.PURAP_DOCS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionPurApDocIndex() + KFSConstants.SQUARE_BRACKET_RIGHT + "." + CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS + KFSConstants.SQUARE_BRACKET_LEFT + purApLineForm.getActionItemAssetIndex() + KFSConstants.SQUARE_BRACKET_RIGHT;
        GlobalVariables.getErrorMap().addToErrorPath(errorPath);
        checkSplitQty(itemAsset, errorPath);
        // checkAdditionalChargeEmpty(purApLineForm, CabConstants.Actions.SPLIT);
        if (GlobalVariables.getErrorMap().isEmpty() && itemAsset != null) {
            PurchasingAccountsPayableItemAsset newItemAsset = purApLineService.processSplit(itemAsset);
            if (newItemAsset != null) {
                purApDoc.getPurchasingAccountsPayableItemAssets().add(newItemAsset);
                Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
            }
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Check if all addition charge lines(not include trade-in) have been processed.
     * 
     * @param purApLineForm
     * @param action
     */
    // protected void checkAdditionalChargeEmpty(PurApLineForm purApLineForm, String action) {
    // if (purApLineForm.isAdditionalChargeIndicator()) {
    // GlobalVariables.getErrorMap().putError(CabPropertyConstants.DOCUMENT_NUMBER,
    // CabKeyConstants.ERROR_ADDITIONAL_CHARGE_NOT_ALLOCATED, action);
    // }
    // return;
    //
    // }
    /**
     * Check user input splitQty. It must be required and can't be greater than current quantity.
     * 
     * @param itemAsset
     * @param errorPath
     */
    protected void checkSplitQty(PurchasingAccountsPayableItemAsset itemAsset, String errorPath) {
        KualiDecimal splitQty = itemAsset.getSplitQty();
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        // splitQty is required and must be greater than the initial value
        if (splitQty == null) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_REQUIRED);
        }
        else if (splitQty.isGreaterEqual(oldQty)) {
            GlobalVariables.getErrorMap().putError(CabPropertyConstants.PurchasingAccountsPayableItemAsset.SPLIT_QTY, CabKeyConstants.ERROR_SPLIT_QTY_INVALID, oldQty.toString());
        }
        return;
    }


    // TODO:
    public ActionForward merge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // check all merge line documents have no additional charge unallocated.
        // ???check all merge lines have no trade-in indicator set. If set, further check if no trade-in allowance line exists.
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward percentPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableItemAsset itemAsset = getSelectedLineItem((PurApLineForm) form);

        if (itemAsset != null) {
            purApLineService.processPercentPayment(itemAsset);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    // TODO:
    public ActionForward allocate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApForm = (PurApLineForm) form;
        PurchasingAccountsPayableItemAsset selectedLineItem = getSelectedLineItem(purApForm);

        if (selectedLineItem != null) {
            List<PurchasingAccountsPayableItemAsset> allocateTargetLines = purApLineService.getAllocateTargetLines(selectedLineItem, purApForm);

            if (purApLineService.processAllocate(selectedLineItem, allocateTargetLines)) {
                List<PurchasingAccountsPayableItemAsset> sameDocItems = getSelectedPurApDoc(purApForm).getPurchasingAccountsPayableItemAssets();
                sameDocItems.remove(selectedLineItem);
                purApLineService.resetSelectedValue(purApForm);
                // PurApLineSession purApLineSession = (PurApLineSession)
                // GlobalVariables.getUserSession().retrieveObject("purApLineSesion");
                // if (purApLineSession != null) {
                // purApLineSession.getDeletedItemAssets().add(selectedLineItem);
                // }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected PurchasingAccountsPayableItemAsset getSelectedLineItem(PurApLineForm purApLineForm) {
        PurchasingAccountsPayableDocument purApDoc = purApLineForm.getPurApDocs().get(purApLineForm.getActionPurApDocIndex());
        return purApDoc.getPurchasingAccountsPayableItemAssets().get(purApLineForm.getActionItemAssetIndex());
    }

    protected PurchasingAccountsPayableDocument getSelectedPurApDoc(PurApLineForm purApLineForm) {
        return purApLineForm.getPurApDocs().get(purApLineForm.getActionPurApDocIndex());
    }
}
