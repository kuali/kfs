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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.exceptions.ItemParserException;
import org.kuali.module.purap.rule.event.AddPurchasingAccountsPayableItemEvent;
import org.kuali.module.purap.rule.event.ImportPurchasingAccountsPayableItemEvent;
import org.kuali.module.purap.rules.PurchasingDocumentRuleBase;
import org.kuali.module.purap.util.ItemParser;
import org.kuali.module.purap.web.struts.form.PurchasingAccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.service.PhoneNumberService;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Struts Action for Purchasing documents.
 */
public class PurchasingActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingActionBase.class);

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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

        // Refreshing the fields after returning from a vendor lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && document.getVendorDetailAssignedIdentifier() != null && document.getVendorHeaderGeneratedIdentifier() != null) {
            document.setVendorContractGeneratedIdentifier(null);
            document.setVendorContractName(null);

            // retrieve vendor based on selection from vendor lookup
            document.refreshReferenceObject("vendorDetail");
            document.templateVendorDetail(document.getVendorDetail());

            // populate default address based on selected vendor
            VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(document.getVendorDetail().getVendorAddresses(), document.getVendorDetail().getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
            document.templateVendorAddress(defaultAddress);
        }

        // Refreshing the fields after returning from a contract lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_CONTRACT_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_CONTRACT_ID))) {
                // retrieve Contract based on selection from contract lookup
                VendorContract refreshVendorContract = new VendorContract();
                refreshVendorContract.setVendorContractGeneratedIdentifier(document.getVendorContractGeneratedIdentifier());
                refreshVendorContract = (VendorContract) businessObjectService.retrieve(refreshVendorContract);

                // Need to reset the vendor header and detail id of the document from the refreshVendorContract as well
                // so that we can continue to do the other lookups (address, customer number) using the correct vendor ids.
                document.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());

                // Need to clear out the Customer Number (see comments on KULPURAP-832).
                document.setVendorCustomerNumber(null);

                // retrieve Vendor based on selected contract
                document.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());
                document.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                document.refreshReferenceObject("vendorDetail");
                document.templateVendorDetail(document.getVendorDetail());

                document.templateVendorContract(refreshVendorContract, document.getVendorDetail());

                // populate default address from selected vendor
                VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(document.getVendorDetail().getVendorAddresses(), document.getVendorDetail().getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
                document.templateVendorAddress(defaultAddress);

                // populate cost source from the selected contract
                if (refreshVendorContract != null) {
                    String costSourceCode = refreshVendorContract.getPurchaseOrderCostSourceCode();
                    if (StringUtils.isNotBlank(costSourceCode)) {
                        document.setPurchaseOrderCostSourceCode(costSourceCode);
                        document.refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_COST_SOURCE);
                    }
                }
            }
        }

        // Refreshing the fields after returning from an address lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                // retrieve address based on selection from address lookup
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(document.getVendorAddressGeneratedIdentifier());
                refreshVendorAddress = (VendorAddress) businessObjectService.retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }

        // Refreshing corresponding fields after returning from various kuali lookups
        if (StringUtils.equals(refreshCaller, KFSConstants.KUALI_LOOKUPABLE_IMPL)) {
            if (request.getParameter("document.deliveryCampusCode") != null) {
                // returning from a building lookup on the delivery tab (update billing address)
                BillingAddress billingAddress = new BillingAddress();
                billingAddress.setBillingCampusCode(document.getDeliveryCampusCode());
                Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
                billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);
                document.templateBillingAddress(billingAddress);
            }
            else if (request.getParameter("document.chartOfAccountsCode") != null) {
                // returning from a chart/org lookup on the document detail tab (update receiving address)
                document.loadReceivingAddress();
            }
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Intended to be used by the refresh button that appears in the delivery tab to refresh the delivery building. Since this is a
     * refresh method, it is calling the general refresh method when it's done.
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
                document.setDeliveryBuildingLine1Address(null);
                document.setDeliveryBuildingLine2Address(null);
                document.setDeliveryBuildingRoomNumber(null);
                document.setDeliveryCityName(null);
                document.setDeliveryStateCode(null);
                document.setDeliveryCountryCode(null);
                document.setDeliveryPostalCode(null);
                baseForm.setNotOtherDeliveryBuilding(false);
            }
            else {
                document.setDeliveryBuildingName(null);
                document.setDeliveryBuildingCode(null);
                document.setDeliveryBuildingLine1Address(null);
                document.setDeliveryBuildingLine2Address(null);
                document.setDeliveryBuildingRoomNumber(null);
                document.setDeliveryCityName(null);
                document.setDeliveryStateCode(null);
                document.setDeliveryCountryCode(null);
                document.setDeliveryPostalCode(null);
                baseForm.setNotOtherDeliveryBuilding(true);
            }
        }

        return refresh(mapping, form, request, response);
    }

    /**
     * Add a new item to the document.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurApItem item = purchasingForm.getNewPurchasingItemLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            item = purchasingForm.getAndResetNewPurchasingItemLine();
            purDocument.addItem(item);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Import items to the document from a spreadsheet.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward importItems(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("Importing item lines");

        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        String documentNumber = purDocument.getDocumentNumber();
        FormFile itemFile = purchasingForm.getItemImportFile();
        Class itemClass = purDocument.getItemClass();
        List<PurApItem> importedItems = null;
        String errorPath = PurapConstants.ITEM_TAB_ERRORS;
        ItemParser itemParser = purDocument.getItemParser();
        int itemLinePosition = purDocument.getItemLinePosition(); // starting position of the imported items, equals the # of existing above-the-line items.

        try {
            importedItems = itemParser.importItems(itemFile, itemClass, documentNumber);
            // validate imported items
            boolean allPassed = true;
            int itemLineNumber = 0;
            for (PurApItem item : importedItems) {
                // Before the validation, set the item line number to the same as the line number in the import file (starting from 1)
                // so that the error message will use the correct line number if there're errors for the current item line.
                item.setItemLineNumber(++itemLineNumber);
                allPassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new ImportPurchasingAccountsPayableItemEvent("", purDocument, item));
                // After the validation, set the item line number to the correct value as if it's added to the item list.
                item.setItemLineNumber(itemLineNumber + itemLinePosition);
            }
            if (allPassed) {
                purDocument.getItems().addAll(itemLinePosition, importedItems);
            }
        }
        catch (ItemParserException e) {
            GlobalVariables.getErrorMap().putError(errorPath, e.getErrorKey(), e.getErrorParameters());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete an item from the document.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.deleteItem(getSelectedLine(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Moves the selected item up one position.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward upItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        int line = getSelectedLine(request);
        purDocument.itemSwap(line, line - 1);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Moves the selected item down one position (These two methods up/down could easily be consolidated. For now, it seems more
     * straightforward to keep them separate.)
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward downItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        int line = getSelectedLine(request);
        purDocument.itemSwap(line, line + 1);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Reveals the account distribution section.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward setupAccountDistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        purchasingForm.setHideDistributeAccounts(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clear out the accounting lines from all the items.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward removeAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapConstants.QUESTION_REMOVE_ACCOUNTS);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.REMOVE_ACCOUNTS_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            for (PurApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                item.getSourceAccountingLines().clear();
            }

            GlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_REMOVED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clear out the commodity codes from all the items.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearItemsCommodityCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapConstants.QUESTION_CLEAR_ALL_COMMODITY_CODES);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.CLEAR_COMMODITY_CODES_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            for (PurApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                PurchasingItemBase purItem = ((PurchasingItemBase) item);
                purItem.setPurchasingCommodityCode(null);
                purItem.setCommodityCode(null);
            }

            GlobalVariables.getMessageList().add(PurapKeyConstants.PUR_COMMODITY_CODES_CLEARED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Distribute accounting line(s) to the item(s). Does not distribute the accounting line(s) to an item if there are 
	 * already accounting lines associated with that item, if the item is a below-the-line item and has no unit cost, or if the item 
	 * is inactive. Distribute commodity code to the item(s). Does not distribute the commodity code to an item if the item is not
     * above the line item, is inactive or if the commodity code fails the validation (i.e. inactive commodity code or non existence
     * commodity code).
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward doDistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        PurchasingDocumentRuleBase purchasingDocumentRule = (PurchasingDocumentRuleBase) (SpringContext.getBean(KualiRuleService.class).getBusinessRulesInstance(purchasingForm.getDocument(), PurchasingDocumentRuleBase.class));

        boolean needToDistributeCommodityCode = false;

        if (StringUtils.isNotBlank(purchasingForm.getDistributePurchasingCommodityCode())) {
            // Do the logic for distributing purchasing commodity code to all the items.
            needToDistributeCommodityCode = true;
        }

        boolean needToDistributeAccount = false;
        if (purchasingForm.getAccountDistributionsourceAccountingLines().size() > 0) {
            needToDistributeAccount = true;
        }
        if (needToDistributeAccount || needToDistributeCommodityCode) {

            boolean institutionNeedsDistributeAccountValidation = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter("KFS-PA", "Document", PurapParameterConstants.VALIDATE_ACCOUNT_DISTRIBUTION_IND);
            boolean foundAccountDistributionError = false;
            boolean foundCommodityCodeDistributionError = false;
            boolean performedAccountDistribution = false;
            boolean performedCommodityCodeDistribution = false;

            // If the institution's validate account distribution indicator is true and
            // the total percentage in the distribute account list does not equal 100 % then we should display error
            if (institutionNeedsDistributeAccountValidation && needToDistributeAccount && purchasingForm.getTotalPercentageOfAccountDistributionsourceAccountingLines().compareTo(new BigDecimal(100)) != 0) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.ERROR_DISTRIBUTE_ACCOUNTS_NOT_100_PERCENT);
                foundAccountDistributionError = true;

            }
            for (PurApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                boolean itemIsActive = true;
                if (item instanceof PurchaseOrderItem) {
                    // if item is PO item... only validate active items
                    itemIsActive = ((PurchaseOrderItem) item).isItemActiveIndicator();
                }
                if (needToDistributeCommodityCode) {
                    // only the above the line items need the commodity code.
                    if (item.getItemType().isItemTypeAboveTheLineIndicator() && StringUtils.isBlank(((PurchasingItemBase) item).getPurchasingCommodityCode()) && itemIsActive) {
                        // Ideally we should invoke rules to check whether the commodity code is valid (active, not restricted,
                        // not missing, etc), probably somewhere here or invoke the rule class from here.

                        boolean rulePassed = purchasingDocumentRule.validateCommodityCodesForDistribution(purchasingForm.getDistributePurchasingCommodityCode());
                        if (rulePassed) {
                            ((PurchasingItemBase) item).setPurchasingCommodityCode(purchasingForm.getDistributePurchasingCommodityCode());
                            performedCommodityCodeDistribution = true;
                        }
                        else {
                            foundCommodityCodeDistributionError = true;
                        }
                    }
                }
                if (needToDistributeAccount && !foundAccountDistributionError) {
                    BigDecimal zero = new BigDecimal(0);
                    // We should be distributing accounting lines to above the line items all the time;
                    // but only to the below the line items when there is a unit cost.
                    boolean unitCostNotZeroForBelowLineItems = item.getItemType().isItemTypeAboveTheLineIndicator() ? true : item.getItemUnitPrice() != null && zero.compareTo(item.getItemUnitPrice()) < 0;
                    if (item.getSourceAccountingLines().size() == 0 && unitCostNotZeroForBelowLineItems && itemIsActive) {
                        item.getSourceAccountingLines().addAll(purchasingForm.getAccountDistributionsourceAccountingLines());
                        performedAccountDistribution = true;
                    }
                }
            }

            if ((needToDistributeCommodityCode && performedCommodityCodeDistribution && !foundCommodityCodeDistributionError) || (needToDistributeAccount && performedAccountDistribution && !foundAccountDistributionError)) {
                if (needToDistributeCommodityCode && !foundCommodityCodeDistributionError && performedCommodityCodeDistribution) {
                    GlobalVariables.getMessageList().add(PurapKeyConstants.PUR_COMMODITY_CODE_DISTRIBUTED);
                    purchasingForm.setDistributePurchasingCommodityCode(null);
                }
                if (needToDistributeAccount && !foundAccountDistributionError && performedAccountDistribution) {
                    GlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_DISTRIBUTED);
                    purchasingForm.getAccountDistributionsourceAccountingLines().clear();
                }
                purchasingForm.setHideDistributeAccounts(true);
            }

            if ((needToDistributeAccount && !performedAccountDistribution && !foundAccountDistributionError)) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO, "Account");
            }
            if (needToDistributeCommodityCode && !performedCommodityCodeDistribution && !foundCommodityCodeDistributionError) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO, "Commodity Code");
            }
        }
        else {
            GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ACCOUNTS_TO_DISTRIBUTE);
        }


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Simply hides the account distribution section.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
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
        PurApItem item = null;

        boolean institutionNeedsDistributeAccountValidation = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter("KFS-PA", "Document", PurapParameterConstants.VALIDATE_ACCOUNT_DISTRIBUTION_IND);

        if (itemIndex == -2 && !institutionNeedsDistributeAccountValidation) {
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
            PurApItem item = (PurApItem) ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItem((itemIndex));
            item.getSourceAccountingLines().remove(accountIndex);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the line for account distribution.
     * 
     * @param accountIndex The index of the account into the request parameter
     * @param purchasingAccountsPayableForm A form which inherits from PurchasingAccountsPayableFormBase
     * @return A SourceAccountingLine
     */
    protected SourceAccountingLine customAccountRetrieval(int accountIndex, PurchasingAccountsPayableFormBase purchasingAccountsPayableForm) {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) purchasingAccountsPayableForm;
        SourceAccountingLine line;
        line = (SourceAccountingLine) ObjectUtils.deepCopy(purchasingForm.getAccountDistributionsourceAccountingLines().get(accountIndex));
        return line;
    }
   
    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectSystemType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocumentBase document = (PurchasingDocumentBase) purchasingForm.getDocument();

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        
        String systemTypeCode = (String)request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        systemTypeCode = StringUtils.substringBetween(systemTypeCode, "selectSystemType.", ".");

        if (question == null) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapConstants.CapitalAssetTabStrings.QUESTION_SYSTEM_SWITCHING);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.CapitalAssetTabStrings.SYSTEM_SWITCHING_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            //TODO: Uncomment the following line when the variable is moved into the superclass for KULPURAP-2431.
            //document.setCapitalAssetSystemTypeCode(systemTypeCode);
            document.refreshReferenceObject(PurapPropertyConstants.CAPITAL_ASSET_SYSTEM_TYPE);

            GlobalVariables.getMessageList().add(PurapKeyConstants.PUR_CAPITAL_ASSET_SYSTEM_TYPE_SWITCHED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}