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

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RicePropertyConstants;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
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
import org.kuali.module.vendor.service.VendorService;

/**
 * This class handles specific Actions requests for the Purchasing Ap.
 */
public class PurchasingActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingActionBase.class);

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();
        String refreshCaller = baseForm.getRefreshCaller();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        PhoneNumberService phoneNumberService = SpringContext.getBean(PhoneNumberService.class);

        // Format phone numbers
        document.setInstitutionContactPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getInstitutionContactPhoneNumber()));
        document.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getRequestorPersonPhoneNumber()));
        document.setDeliveryToPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getDeliveryToPhoneNumber()));

        //Refreshing the fields after returning from a vendor lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && 
                document.getVendorDetailAssignedIdentifier() != null && 
                document.getVendorHeaderGeneratedIdentifier() != null) {
            document.setVendorContractGeneratedIdentifier(null);
            document.setVendorContractName(null);

            // retrieve vendor based on selection from vendor lookup
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(document.getVendorDetailAssignedIdentifier());
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(document.getVendorHeaderGeneratedIdentifier());
            refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
            document.templateVendorDetail(refreshVendorDetail);

            // populate default address based on selected vendor
            VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(refreshVendorDetail.getVendorAddresses(), refreshVendorDetail.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
            document.templateVendorAddress(defaultAddress);
        }

        //Refreshing the fields after returning from a contract lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_CONTRACT_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(RicePropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_CONTRACT_ID))) {
                // retrieve Contract based on selection from contract lookup
                VendorContract refreshVendorContract = new VendorContract();
                refreshVendorContract.setVendorContractGeneratedIdentifier(document.getVendorContractGeneratedIdentifier());
                refreshVendorContract = (VendorContract) businessObjectService.retrieve(refreshVendorContract);
                document.templateVendorContract(refreshVendorContract);

                // Need to reset the vendor header and detail id of the document from the refreshVendorContract as well
                // so that we can continue to do the other lookups (address, customer number) using the correct vendor ids.
                document.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());

                // Need to clear out the Customer Number (see comments on KULPURAP-832).
                document.setVendorCustomerNumber(null);

                // retrieve Vendor based on selected contract
                VendorDetail refreshVendorDetail = new VendorDetail();
                refreshVendorDetail.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());
                refreshVendorDetail.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
                document.templateVendorDetail(refreshVendorDetail);

                // populate default address from selected vendor
                VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(refreshVendorDetail.getVendorAddresses(), refreshVendorDetail.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
                document.templateVendorAddress(defaultAddress);
                
                // populate cost source from the selected contract
                if (refreshVendorContract != null) {
                    String costSourceCode = refreshVendorContract.getPurchaseOrderCostSourceCode();
                    if (StringUtils.isNotBlank(costSourceCode)) {
                        document.setPurchaseOrderCostSourceCode(costSourceCode);
                        // document.setPurchaseOrderCostSource(refreshVendorContract.getPurchaseOrderCostSource());
                    }
                }
            }
        }
        
        //Refreshing the fields after returning from an address lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(RicePropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                // retrieve address based on selection from address lookup
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(document.getVendorAddressGeneratedIdentifier());
                refreshVendorAddress = (VendorAddress) businessObjectService.retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }
        return super.refresh(mapping, form, request, response);
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
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddPurchasingAccountsPayableItemEvent("item", purDocument, item));
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

    /**
     * move item up one position
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward upItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        int line = getSelectedLine(request);
        purDocument.itemSwap(line,line-1);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * move item down one position (these two methods up/down could easily be consolidated - for now it seems more straightforward to keep separate)
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward downItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        int line = getSelectedLine(request);
        purDocument.itemSwap(line,line+1);
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
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapConstants.QUESTION_REMOVE_ACCOUNTS);
            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.REMOVE_ACCOUNTS_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
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
                    BigDecimal zero = new BigDecimal(0);
                    if (item.getSourceAccountingLines().size() == 0 && item.getItemUnitPrice() != null && zero.compareTo(item.getItemUnitPrice()) < 0) {
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
    
    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        String[] indexes = getSelectedLineForAccounts(request);
        int itemIndex = Integer.parseInt(indexes[0]);
        int accountIndex = Integer.parseInt(indexes[1]);
        if (itemIndex == -2) {
            purchasingForm.getAccountDistributionsourceAccountingLines().remove(accountIndex);
        }
        else {
            PurchasingApItem item = (PurchasingApItem) ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItem((itemIndex));
            item.getSourceAccountingLines().remove(accountIndex);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}