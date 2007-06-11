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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.rule.event.AddPurchasingAccountsPayableItemEvent;
import org.kuali.module.purap.web.struts.form.PurchasingAccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.PhoneNumberService;

/**
 * This class handles specific Actions requests for the Purchasing Ap.
 */
public class PurchasingActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingActionBase.class);

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();
        String refreshCaller = baseForm.getRefreshCaller();
        BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();
        PhoneNumberService phoneNumberService = SpringServiceLocator.getPhoneNumberService();

        // Format phone numbers
        document.setInstitutionContactPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getInstitutionContactPhoneNumber()));
        document.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getRequestorPersonPhoneNumber()));
        document.setDeliveryToPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getDeliveryToPhoneNumber()));

        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && document.getVendorDetailAssignedIdentifier() != null && document.getVendorHeaderGeneratedIdentifier() != null) {

            document.setVendorContractGeneratedIdentifier(null);
            document.setVendorContractName(null);
            Integer vendorDetailAssignedId = document.getVendorDetailAssignedIdentifier();
            Integer vendorHeaderGeneratedId = document.getVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(vendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
            document.templateVendorDetail(refreshVendorDetail);
        }

        if (StringUtils.equals(refreshCaller, KFSConstants.KUALI_LOOKUPABLE_IMPL)) {

            if (StringUtils.isNotEmpty(request.getParameter(PurapPropertyConstants.VENDOR_CONTRACT_ID))) {
                Integer vendorContractGeneratedId = document.getVendorContractGeneratedIdentifier();
                VendorContract refreshVendorContract = new VendorContract();
                refreshVendorContract.setVendorContractGeneratedIdentifier(vendorContractGeneratedId);
                refreshVendorContract = (VendorContract) businessObjectService.retrieve(refreshVendorContract);
                document.templateVendorContract(refreshVendorContract);
                VendorDetail refreshVendorDetail = new VendorDetail();
                refreshVendorDetail.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());
                refreshVendorDetail.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
                document.templateVendorDetail(refreshVendorDetail);

                // populate default address
                populateDefaultAddress(refreshVendorDetail, document);

            }
            if (StringUtils.isNotEmpty(request.getParameter(PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                Integer vendorAddressGeneratedId = document.getVendorAddressGeneratedIdentifier();
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(vendorAddressGeneratedId);
                refreshVendorAddress = (VendorAddress) businessObjectService.retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }
        return super.refresh(mapping, form, request, response);
    }

    private void populateDefaultAddress(VendorDetail refreshVendorDetail, PurchasingDocument document) {
        VendorAddress defaultAddress = SpringServiceLocator.getVendorService().getVendorDefaultAddress(refreshVendorDetail.getVendorAddresses(), refreshVendorDetail.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        if (defaultAddress != null && defaultAddress.getVendorState() != null) {
            refreshVendorDetail.setVendorStateForLookup(defaultAddress.getVendorState().getPostalStateName());
            refreshVendorDetail.setDefaultAddressLine1(defaultAddress.getVendorLine1Address());
            refreshVendorDetail.setDefaultAddressLine2(defaultAddress.getVendorLine2Address());
            refreshVendorDetail.setDefaultAddressCity(defaultAddress.getVendorCityName());
            refreshVendorDetail.setDefaultAddressPostalCode(defaultAddress.getVendorZipCode());
            refreshVendorDetail.setDefaultAddressStateCode(defaultAddress.getVendorStateCode());
            refreshVendorDetail.setDefaultAddressCountryCode(defaultAddress.getVendorCountryCode());
        }
        document.setVendorAddressGeneratedIdentifier(defaultAddress.getVendorAddressGeneratedIdentifier());
        document.setVendorLine1Address(defaultAddress.getVendorLine1Address());
        document.setVendorLine2Address(defaultAddress.getVendorLine2Address());
        document.setVendorCityName(defaultAddress.getVendorCityName());
        document.setVendorPostalCode(defaultAddress.getVendorZipCode());
        document.setVendorCountryCode(defaultAddress.getVendorCountryCode());
        document.setVendorStateCode(defaultAddress.getVendorStateCode());
    }

    /**
     * This method is intended to be used by the refresh button that appears in the delivery tab to refresh the delivery building,
     * as specified in KULPURAP-260. Since this is a refresh method, it is calling the general refresh method when it's done.
     * Typical Struts signature.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request A HttpServletRequest
     * @param response A HttpServletResponse
     * @return An ActionForward
     * @throws Exception
     */
    public ActionForward refreshDeliveryBuilding(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();
        if (ObjectUtils.isNotNull(document.isDeliveryBuildingOther())) {
            if (document.isDeliveryBuildingOther()) {
                document.setDeliveryBuildingName(PurapConstants.DELIVERY_BUILDING_OTHER);
                document.setDeliveryBuildingCode(PurapConstants.DELIVERY_BUILDING_OTHER_CODE);
                baseForm.setNotOtherDeliveryBuilding(false);
            }
            else {
                document.setDeliveryBuildingName(null);
                document.setDeliveryBuildingCode(null);
                baseForm.setNotOtherDeliveryBuilding(true);
            }
        }
        return refresh(mapping, form, request, response);
    }

    /**
     * Add a new item to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call add line event/rules here
        PurchasingApItem item = purchasingForm.getAndResetNewPurchasingItemLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddPurchasingAccountsPayableItemEvent("item", purDocument, item));
        // AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINES_PROPERTY_NAME + "[" + Integer.toString(itemIndex) + "]",
        // purchasingForm.getDocument(), (AccountingLine) line)
        if (rulePassed) {

            purDocument.addItem(item);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete an item from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call delete line event/rules here

        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.deleteItem(getSelectedLine(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward setupAccountDistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        purchasingForm.setHideDistributeAccounts(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward removeAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(PurapConstants.QUESTION_REMOVE_ACCOUNTS);
            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.REMOVE_ACCOUNTS_QUESTION, questionText, Constants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            for (PurchasingApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                item.getSourceAccountingLines().clear();
            }

            GlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_REMOVED);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward doAccountDistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        if (((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems().size() > 0) {
            if (purchasingForm.getAccountDistributionsourceAccountingLines().size() > 0) {
                for (PurchasingApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                    if (item.getSourceAccountingLines().size() == 0) {
                        item.getSourceAccountingLines().addAll(purchasingForm.getAccountDistributionsourceAccountingLines());
                    }
                }
                purchasingForm.getAccountDistributionsourceAccountingLines().clear();
                GlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_DISTRIBUTED);
                purchasingForm.setHideDistributeAccounts(true);
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ACCOUNTS_TO_DISTRIBUTE);
            }
        }
        else {
            GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancelAccountDistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        purchasingForm.setHideDistributeAccounts(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.module.purap.web.struts.action.PurchasingAccountsPayableActionBase#processCustomInsertAccountingLine(org.kuali.module.purap.web.struts.form.PurchasingAccountsPayableFormBase)
     */
    @Override
    public boolean processCustomInsertAccountingLine(PurchasingAccountsPayableFormBase purapForm, HttpServletRequest request) {

        boolean success = false;
        PurchasingFormBase purchasingForm = (PurchasingFormBase) purapForm;

        // index of item selected
        int itemIndex = getSelectedLine(request);
        PurchasingApItem item = null;

        if (itemIndex == -2) {
            PurApAccountingLine line = purchasingForm.getAccountDistributionnewSourceLine();
            purchasingForm.addAccountDistributionsourceAccountingLine(line);
            success = true;
        }

        return success;
    }
}