/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemType;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetSystemBase;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddPurchasingAccountsPayableItemEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddPurchasingCapitalAssetLocationEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddPurchasingItemCapitalAssetEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCommodityCodesForDistributionEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedImportPurchasingAccountsPayableItemEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedUpdateCamsViewPurapEvent;
import org.kuali.kfs.module.purap.exception.ItemParserException;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.ItemParser;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Struts Action for Purchasing documents.
 */

public class PurchasingActionBase extends PurchasingAccountsPayableActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingActionBase.class);

    /**
     * save the document without any validations.....
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) kualiDocumentFormBase.getDocument();
        
        //clear any error messages but there should not be any currently.
        GlobalVariables.getMessageMap().clearErrorMessages();
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(purapDocument);        
        
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SAVED);
        kualiDocumentFormBase.setAnnotation("");

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase baseForm = (PurchasingAccountsPayableFormBase) form;

        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();
        String refreshCaller = baseForm.getRefreshCaller();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        PhoneNumberService phoneNumberService = SpringContext.getBean(PhoneNumberService.class);

        // Format phone numbers
        document.setInstitutionContactPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getInstitutionContactPhoneNumber()));
        document.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getRequestorPersonPhoneNumber()));
        document.setDeliveryToPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getDeliveryToPhoneNumber()));

        //names in KIM are longer than what we store these names at; truncate them to match our data dictionary maxlengths
        if (StringUtils.equals(refreshCaller, "kimPersonLookupable")) {
            Integer deliveryToNameMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getClass(), PurapPropertyConstants.DELIVERY_TO_NAME);
            if (StringUtils.isNotEmpty(document.getDeliveryToName()) && ObjectUtils.isNotNull(deliveryToNameMaxLength) && document.getDeliveryToName().length() > deliveryToNameMaxLength.intValue()) {
                document.setDeliveryToName(document.getDeliveryToName().substring(0, deliveryToNameMaxLength));
                GlobalVariables.getMessageMap().clearErrorPath();
                GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.DELIVERY_TAB_ERRORS);
                GlobalVariables.getMessageMap().putWarning(PurapPropertyConstants.DELIVERY_TO_NAME, PurapKeyConstants.WARNING_DELIVERY_TO_NAME_TRUNCATED);
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapConstants.DELIVERY_TAB_ERRORS);
            }

            Integer requestorNameMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getClass(), PurapPropertyConstants.REQUESTOR_PERSON_NAME);
            if (StringUtils.isNotEmpty(document.getRequestorPersonName()) && ObjectUtils.isNotNull(requestorNameMaxLength) && document.getRequestorPersonName().length() > requestorNameMaxLength.intValue()) {
                document.setRequestorPersonName(document.getRequestorPersonName().substring(0, requestorNameMaxLength));
                GlobalVariables.getMessageMap().clearErrorPath();
                GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.ADDITIONAL_TAB_ERRORS);
                GlobalVariables.getMessageMap().putWarning(PurapPropertyConstants.REQUESTOR_PERSON_NAME, PurapKeyConstants.WARNING_REQUESTOR_NAME_TRUNCATED);
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapConstants.ADDITIONAL_TAB_ERRORS);
            }
        }
        
        // Refreshing the fields after returning from a vendor lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && document.getVendorDetailAssignedIdentifier() != null && document.getVendorHeaderGeneratedIdentifier() != null) {
            document.setVendorContractGeneratedIdentifier(null);
            document.refreshReferenceObject("vendorContract");

            // retrieve vendor based on selection from vendor lookup
            document.refreshReferenceObject("vendorDetail");
            document.templateVendorDetail(document.getVendorDetail());

            // populate default address based on selected vendor
            VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(document.getVendorDetail().getVendorAddresses(), document.getVendorDetail().getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), document.getDeliveryCampusCode());
            document.templateVendorAddress(defaultAddress);

        }

        // Refreshing the fields after returning from a contract lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_CONTRACT_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_CONTRACT_ID))) {
                // retrieve Contract based on selection from contract lookup
                VendorContract refreshVendorContract = new VendorContract();
                refreshVendorContract.setVendorContractGeneratedIdentifier(document.getVendorContractGeneratedIdentifier());
                refreshVendorContract = (VendorContract) businessObjectService.retrieve(refreshVendorContract);

                // retrieve Vendor based on selected contract
                document.setVendorHeaderGeneratedIdentifier(refreshVendorContract.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(refreshVendorContract.getVendorDetailAssignedIdentifier());
                document.refreshReferenceObject("vendorDetail");
                document.templateVendorDetail(document.getVendorDetail());

                // always template contract after vendor to keep contract defaults last
                document.templateVendorContract(refreshVendorContract);

                // populate default address from selected vendor
                VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(document.getVendorDetail().getVendorAddresses(), document.getVendorDetail().getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
                document.templateVendorAddress(defaultAddress);

                // update internal dollar limit for PO since the contract might affect this value
                if (document instanceof PurchaseOrderDocument) {
                    PurchaseOrderDocument poDoc = (PurchaseOrderDocument) document;
                    KualiDecimal limit = SpringContext.getBean(PurchaseOrderService.class).getInternalPurchasingDollarLimit(poDoc);
                    poDoc.setInternalPurchasingLimit(limit);
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
                // returning from a building or campus lookup on the delivery tab (update billing address)
                BillingAddress billingAddress = new BillingAddress();
                billingAddress.setBillingCampusCode(document.getDeliveryCampusCode());
                Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
                billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);
                document.templateBillingAddress(billingAddress);

                if (request.getParameter("document.deliveryBuildingName") == null) {
                    // came from campus lookup not building, so clear building
                    document.setDeliveryBuildingCode("");
                    document.setDeliveryBuildingName("");
                    document.setDeliveryBuildingLine1Address("");
                    document.setDeliveryBuildingLine2Address("");
                    document.setDeliveryBuildingRoomNumber("");
                    document.setDeliveryCityName("");
                    document.setDeliveryStateCode("");
                    document.setDeliveryPostalCode("");
                    document.setDeliveryCountryCode("");
                }
                else {
                    // came from building lookup then turn off "OTHER" and clear room and line2address
                    document.setDeliveryBuildingOtherIndicator(false);
                    document.setDeliveryBuildingRoomNumber("");
                    document.setDeliveryBuildingLine2Address("");
                }
            }
            else if (request.getParameter("document.chartOfAccountsCode") != null) {
                // returning from a chart/org lookup on the document detail tab (update receiving address)
                document.loadReceivingAddress();
            }
            else {
                String buildingCodeParam = findBuildingCodeFromCapitalAssetBuildingLookup(request);
                if (!StringUtils.isEmpty(buildingCodeParam)) {
                    // returning from a building lookup in a capital asset tab location (update location address)
                    PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
                    CapitalAssetLocation location = null;

                    // get building code
                    String buildingCode = request.getParameterValues(buildingCodeParam)[0];
                    // get campus code
                    String campusCodeParam = buildingCodeParam.replace("buildingCode", "campusCode");
                    String campusCode = request.getParameterValues(campusCodeParam)[0];
                    // lookup building
                    Building locationBuilding = new Building();
                    locationBuilding.setCampusCode(campusCode);
                    locationBuilding.setBuildingCode(buildingCode);
                    Map<String, String> keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(locationBuilding);
                    locationBuilding = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, keys);

                    Map<String, String> parameters = request.getParameterMap();
                    Set<String> parameterKeys = parameters.keySet();
                    String locationCapitalAssetLocationNumber = "";
                    String locationCapitalAssetItemNumber = "";
                    for (String parameterKey : parameterKeys) {
                        if (StringUtils.containsIgnoreCase(parameterKey, "newPurchasingCapitalAssetLocationLine")) {
                            // its the new line
                            if (document.getCapitalAssetSystemType().getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                                // get the item number
                                locationCapitalAssetItemNumber = getCaptialAssetItemNumberFromParameter(parameterKey);
                                PurchasingCapitalAssetItem capitalAssetItem = document.getPurchasingCapitalAssetItems().get(Integer.parseInt(locationCapitalAssetItemNumber));
                                location = capitalAssetItem.getPurchasingCapitalAssetSystem().getNewPurchasingCapitalAssetLocationLine();
                            }
                            else {
                                // no item number
                                location = purchasingForm.getNewPurchasingCapitalAssetLocationLine();
                            }
                            break;
                        }
                        else if (StringUtils.containsIgnoreCase(parameterKey, "purchasingCapitalAssetLocationLine")) {
                            // its one of the numbered lines, lets
                            locationCapitalAssetLocationNumber = getCaptialAssetLocationNumberFromParameter(parameterKey);

                            if (document.getCapitalAssetSystemType().getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                                // get the item number
                                locationCapitalAssetItemNumber = getCaptialAssetItemNumberFromParameter(parameterKey);
                                PurchasingCapitalAssetItem capitalAssetItem = document.getPurchasingCapitalAssetItems().get(Integer.parseInt(locationCapitalAssetItemNumber));
                                location = capitalAssetItem.getPurchasingCapitalAssetSystem().getCapitalAssetLocations().get(Integer.parseInt(locationCapitalAssetLocationNumber));
                            }
                            break;
                        }
                        else if (StringUtils.containsIgnoreCase(parameterKey, "purchasingCapitalAssetSystems")) {
                            // its one of the numbered lines, lets
                            locationCapitalAssetLocationNumber = getCaptialAssetLocationNumberFromParameter(parameterKey);

                            if (!document.getCapitalAssetSystemType().getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                                CapitalAssetSystem capitalAssetSystem = document.getPurchasingCapitalAssetSystems().get(0);
                                location = capitalAssetSystem.getCapitalAssetLocations().get(Integer.parseInt(locationCapitalAssetLocationNumber));
                            }
                            break;
                        }
                    }

                    if ((location != null) && (locationBuilding != null)) {
                        location.templateBuilding(locationBuilding);
                    }
                }
            }
        }
        return super.refresh(mapping, form, request, response);
    }

    protected String getCaptialAssetLocationNumberFromParameter(String parameterKey) {
        int beginIndex = parameterKey.lastIndexOf("[") + 1;
        int endIndex = parameterKey.lastIndexOf("]");
        return parameterKey.substring(beginIndex, endIndex);
    }

    protected String getCaptialAssetItemNumberFromParameter(String parameterKey) {
        int beginIndex = parameterKey.indexOf("[") + 1;
        int endIndex = parameterKey.indexOf("]");
        return parameterKey.substring(beginIndex, endIndex);
    }

    /**
     * Setup document to use "OTHER" building
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request A HttpServletRequest
     * @param response A HttpServletResponse
     * @return An ActionForward
     * @throws Exception
     */
    public ActionForward useOtherDeliveryBuilding(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();

        document.setDeliveryBuildingOtherIndicator(true);
        document.setDeliveryBuildingName("");
        document.setDeliveryBuildingCode("");
        document.setDeliveryBuildingLine1Address("");
        document.setDeliveryBuildingLine2Address("");
        document.setDeliveryBuildingRoomNumber("");
        document.setDeliveryCityName("");
        document.setDeliveryStateCode("");
        document.setDeliveryCountryCode("");
        document.setDeliveryPostalCode("");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward useOffCampusAssetLocationBuildingByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();

        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String systemIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        String assetLocationIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);

        CapitalAssetSystem system = document.getPurchasingCapitalAssetSystems().get(Integer.parseInt(systemIndex));

        if ("new".equals(assetLocationIndex)) {
            useOffCampusAssetLocationBuilding(baseForm.getNewPurchasingCapitalAssetLocationLine());
        }
        else {
            useOffCampusAssetLocationBuilding(system.getCapitalAssetLocations().get(Integer.parseInt(assetLocationIndex)));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward useOffCampusAssetLocationBuildingByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocument document = (PurchasingDocument) baseForm.getDocument();

        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String assetItemIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        String assetLocationIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);

        PurchasingCapitalAssetItem assetItem = document.getPurchasingCapitalAssetItems().get(Integer.parseInt(assetItemIndex));
        CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();

        if ("new".equals(assetLocationIndex)) {
            useOffCampusAssetLocationBuilding(system.getNewPurchasingCapitalAssetLocationLine());
        }
        else {
            useOffCampusAssetLocationBuilding(system.getCapitalAssetLocations().get(Integer.parseInt(assetLocationIndex)));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void useOffCampusAssetLocationBuilding(CapitalAssetLocation location) {
        if (location != null) {
            location.setOffCampusIndicator(true);
            location.setBuildingCode("");
            location.setCapitalAssetLine1Address("");
            location.setCapitalAssetCityName("");
            location.setCapitalAssetStateCode("");
            location.setCapitalAssetPostalCode("");
            location.setCapitalAssetCountryCode("");
            location.setBuildingRoomNumber("");
        }
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
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingAccountsPayableItemEvent("", purDocument, item));

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
        int itemLinePosition = purDocument.getItemLinePosition(); // starting position of the imported items, equals the # of
        // existing above-the-line items.

        try {
            importedItems = itemParser.importItems(itemFile, itemClass, documentNumber);
            // validate imported items
            boolean allPassed = true;
            int itemLineNumber = 0;
            for (PurApItem item : importedItems) {
                // Before the validation, set the item line number to the same as the line number in the import file (starting from
                // 1)
                // so that the error message will use the correct line number if there're errors for the current item line.
                item.setItemLineNumber(++itemLineNumber);
                allPassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedImportPurchasingAccountsPayableItemEvent("", purDocument, item));
                // After the validation, set the item line number to the correct value as if it's added to the item list.
                item.setItemLineNumber(itemLineNumber + itemLinePosition);
            }
            if (allPassed) {
                updateBOReferenceforNewItems(importedItems, (PurchasingDocumentBase) purDocument);
                purDocument.getItems().addAll(itemLinePosition, importedItems);

            }
        }
        catch (ItemParserException e) {
            GlobalVariables.getMessageMap().putError(errorPath, e.getErrorKey(), e.getErrorParameters());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Whenever add a new item, we need to keep track of the reference from Item to Doc and from Account to Item
     * 
     * @param importedItems
     */
    protected void updateBOReferenceforNewItems(List<PurApItem> importedItems, PurchasingDocumentBase purDocument) {
        // update reference from Item to Document and from Account to Item.
        for (PurApItem item : (List<PurApItem>) importedItems) {
            item.setPurapDocument(purDocument);
            // set the PurapDocumentIdentifier so in the future, item acquire the object again by calling refreshReferenceObject for
            // purApDocument.
            if (purDocument.getPurapDocumentIdentifier() != null) {
                item.setPurapDocumentIdentifier(purDocument.getPurapDocumentIdentifier());
            }
            for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                account.setPurapItem(item);
                if (item.getItemIdentifier() != null) {
                    account.setItemIdentifier(item.getItemIdentifier());
                }
            }
        }
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
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;

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
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
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
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
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
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapConstants.QUESTION_REMOVE_ACCOUNTS);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.REMOVE_ACCOUNTS_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            for (PurApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                item.getSourceAccountingLines().clear();
            }

            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_REMOVED);
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
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapConstants.QUESTION_CLEAR_ALL_COMMODITY_CODES);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.CLEAR_COMMODITY_CODES_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            for (PurApItem item : ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItems()) {
                PurchasingItemBase purItem = ((PurchasingItemBase) item);
                purItem.setPurchasingCommodityCode(null);
                purItem.setCommodityCode(null);
            }

            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PUR_COMMODITY_CODES_CLEARED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Validates that the accounting lines while a distribute accounts action is being taken.
     * 
     * @param document
     * @param distributionsourceAccountingLines
     * @return
     */
    protected boolean validateDistributeAccounts(Document document, List<PurApAccountingLine> distributionsourceAccountingLines) {
        boolean rulePassed = true;
        String errorPrefix = null;
        int i = 0;

        for (PurApAccountingLine accountingLine : distributionsourceAccountingLines) {
            errorPrefix = "accountDistributionsourceAccountingLine" + "[" + Integer.toString(i) + "]";
            rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(errorPrefix, document, accountingLine));
            i++;
        }

        return rulePassed;
    }

    /**
     * Distribute accounting line(s) to the item(s). Does not distribute the accounting line(s) to an item if there are already
     * accounting lines associated with that item, if the item is a below-the-line item and has no unit cost, or if the item is
     * inactive. Distribute commodity code to the item(s). Does not distribute the commodity code to an item if the item is not
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
        boolean needToDistributeCommodityCode = false;

        if (StringUtils.isNotBlank(purchasingForm.getDistributePurchasingCommodityCode())) {
            // Do the logic for distributing purchasing commodity code to all the items.
            needToDistributeCommodityCode = true;
        }

        boolean needToDistributeAccount = false;
        List<PurApAccountingLine> distributionsourceAccountingLines = purchasingForm.getAccountDistributionsourceAccountingLines();
        if (distributionsourceAccountingLines.size() > 0) {
            needToDistributeAccount = true;
        }
        if (needToDistributeAccount || needToDistributeCommodityCode) {
            PurchasingAccountsPayableDocumentBase purApDocument = (PurchasingAccountsPayableDocumentBase)purchasingForm.getDocument();
            
            boolean institutionNeedsDistributeAccountValidation = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.VALIDATE_ACCOUNT_DISTRIBUTION_IND);
            boolean foundAccountDistributionError = false;
            boolean foundCommodityCodeDistributionError = false;
            boolean performedAccountDistribution = false;
            boolean performedCommodityCodeDistribution = false;

            //do check for account percents only if distribution method not equal to "P"
            if (!PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE.equalsIgnoreCase(purApDocument.getAccountDistributionMethod())) {
                // If the institution's validate account distribution indicator is true and
                // the total percentage in the distribute account list does not equal 100 % then we should display error
                if (institutionNeedsDistributeAccountValidation && needToDistributeAccount && purchasingForm.getTotalPercentageOfAccountDistributionsourceAccountingLines().compareTo(new BigDecimal(100)) != 0) {
                    GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.ERROR_DISTRIBUTE_ACCOUNTS_NOT_100_PERCENT);
                    foundAccountDistributionError = true;
                }
            }
            
            // if the institution's validate account distribution indicator is true and
            // there is a validation error in the accounts to distribute then we should display an error
            if (institutionNeedsDistributeAccountValidation && needToDistributeAccount && (validateDistributeAccounts(purchasingForm.getDocument(), distributionsourceAccountingLines) == false)) {
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
                    if (item.getItemType().isLineItemIndicator() && StringUtils.isBlank(((PurchasingItemBase) item).getPurchasingCommodityCode()) && itemIsActive) {
                        // Ideally we should invoke rules to check whether the commodity code is valid (active, not restricted,
                        // not missing, etc), probably somewhere here or invoke the rule class from here.

                        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedCommodityCodesForDistributionEvent("", purchasingForm.getDocument(), purchasingForm.getDistributePurchasingCommodityCode()));
                        if (rulePassed) {
                            ((PurchasingItemBase) item).setPurchasingCommodityCode(purchasingForm.getDistributePurchasingCommodityCode());
                            performedCommodityCodeDistribution = true;
                        }
                        else {
                            foundCommodityCodeDistributionError = true;
                        }
                    }else if(item.getItemType().isLineItemIndicator() && !StringUtils.isBlank(((PurchasingItemBase) item).getPurchasingCommodityCode()) && itemIsActive){
                        //could not apply to line, as it wasn't blank
                        foundCommodityCodeDistributionError = true;
                    }
                }
                if (needToDistributeAccount && !foundAccountDistributionError) {
                    BigDecimal zero = new BigDecimal(0);
                    // We should be distributing accounting lines to above the line items all the time;
                    // but only to the below the line items when there is a unit cost.
                    boolean unitCostNotZeroForBelowLineItems = item.getItemType().isLineItemIndicator() ? true : item.getItemUnitPrice() != null && zero.compareTo(item.getItemUnitPrice()) < 0;
                    Document document = ((PurchasingFormBase) form).getDocument();
                    Class clazz = document instanceof PurchaseOrderAmendmentDocument ? PurchaseOrderDocument.class : document.getClass();
                    List<String> typesNotAllowingEdit = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(clazz, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT) );
                    boolean itemOnExcludeList = (typesNotAllowingEdit == null) ? false : typesNotAllowingEdit.contains(item.getItemTypeCode());
                    if (item.getSourceAccountingLines().size() == 0 && unitCostNotZeroForBelowLineItems && !itemOnExcludeList && itemIsActive) {
                        for (PurApAccountingLine purApAccountingLine : distributionsourceAccountingLines) {
                            item.getSourceAccountingLines().add((PurApAccountingLine) ObjectUtils.deepCopy(purApAccountingLine));
                        }

                        performedAccountDistribution = true;
                    }
                }
            }

            if ((needToDistributeCommodityCode && performedCommodityCodeDistribution && !foundCommodityCodeDistributionError) || (needToDistributeAccount && performedAccountDistribution && !foundAccountDistributionError)) {
                if (needToDistributeCommodityCode && !foundCommodityCodeDistributionError && performedCommodityCodeDistribution) {
                    KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PUR_COMMODITY_CODE_DISTRIBUTED);
                    purchasingForm.setDistributePurchasingCommodityCode(null);
                }
                if (needToDistributeAccount && !foundAccountDistributionError && performedAccountDistribution) {
                    KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PURAP_GENERAL_ACCOUNTS_DISTRIBUTED);
                    distributionsourceAccountingLines.clear();
                }
                purchasingForm.setHideDistributeAccounts(true);
            }

            if ((needToDistributeAccount && !performedAccountDistribution && foundAccountDistributionError)) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO, "account numbers");
            }
            if (needToDistributeCommodityCode && !performedCommodityCodeDistribution && foundCommodityCodeDistributionError) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO, "commodity codes");
            }
        }
        else {
            GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PURAP_GENERAL_NO_ACCOUNTS_TO_DISTRIBUTE);
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
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingAccountsPayableActionBase#processCustomInsertAccountingLine(org.kuali.kfs.module.purap.document.web.struts.PurchasingAccountsPayableFormBase)
     */
    @Override
    public boolean processCustomInsertAccountingLine(PurchasingAccountsPayableFormBase purapForm, HttpServletRequest request) {
        boolean success = false;
        PurchasingFormBase purchasingForm = (PurchasingFormBase) purapForm;

        // index of item selected
        int itemIndex = getSelectedLine(request);
        PurApItem item = null;

        boolean institutionNeedsDistributeAccountValidation = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.VALIDATE_ACCOUNT_DISTRIBUTION_IND);

        if (itemIndex == -2 && !institutionNeedsDistributeAccountValidation) {
            PurApAccountingLine line = purchasingForm.getAccountDistributionnewSourceLine();
            purchasingForm.addAccountDistributionsourceAccountingLine(line);
            success = true;
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
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
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectSystemType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocumentBase document = (PurchasingDocumentBase) purchasingForm.getDocument();

        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        String systemTypeCode = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        systemTypeCode = StringUtils.substringBetween(systemTypeCode, "selectSystemType.", ".");

        if (question == null) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapConstants.CapitalAssetTabStrings.QUESTION_SYSTEM_SWITCHING);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.CapitalAssetTabStrings.SYSTEM_SWITCHING_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {

            // document.setCapitalAssetSystemTypeCode(systemTypeCode);
            document.refreshReferenceObject(PurapPropertyConstants.CAPITAL_ASSET_SYSTEM_TYPE);

            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PUR_CAPITAL_ASSET_SYSTEM_TYPE_SWITCHED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addItemCapitalAssetByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        ItemCapitalAsset asset = purDocument.getPurchasingCapitalAssetItems().get(0).getNewPurchasingItemCapitalAssetLine();

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingItemCapitalAssetEvent("", purDocument, asset));

        if (rulePassed) {
            // get specific asset item and grab system as well and attach asset number
            CapitalAssetSystem system = purDocument.getPurchasingCapitalAssetSystems().get(getSelectedLine(request));
            asset = purDocument.getPurchasingCapitalAssetItems().get(0).getAndResetNewPurchasingItemCapitalAssetLine();
            asset.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            system.getItemCapitalAssets().add(asset);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addItemCapitalAssetByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        // get specific asset item
        PurchasingCapitalAssetItem assetItem = purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request));

        ItemCapitalAsset asset = assetItem.getNewPurchasingItemCapitalAssetLine();

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingItemCapitalAssetEvent("", purDocument, asset));

        if (rulePassed) {
            // grab system as well and attach asset number
            CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
            asset = assetItem.getAndResetNewPurchasingItemCapitalAssetLine();
            asset.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            system.getItemCapitalAssets().add(asset);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteItemCapitalAssetByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        // get specific asset item
        PurchasingCapitalAssetItem assetItem = purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request));
        ItemCapitalAsset asset = assetItem.getNewPurchasingItemCapitalAssetLine();

        boolean rulePassed = true; // SpringContext.getBean(KualiRuleService.class).applyRules(new
        // AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
            String systemIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
            String assetIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);

            PurchasingCapitalAssetSystemBase system = (PurchasingCapitalAssetSystemBase) purDocument.getPurchasingCapitalAssetSystems().get(Integer.parseInt(systemIndex));
            system.getItemCapitalAssets().remove(Integer.parseInt(assetIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteItemCapitalAssetByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        // get specific asset item
        PurchasingCapitalAssetItem assetItem = purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request));

        ItemCapitalAsset asset = assetItem.getNewPurchasingItemCapitalAssetLine();

        boolean rulePassed = true; // SpringContext.getBean(KualiRuleService.class).applyRules(new
        // AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
            String assetIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
            PurchasingCapitalAssetSystemBase system = (PurchasingCapitalAssetSystemBase) assetItem.getPurchasingCapitalAssetSystem();
            system.getItemCapitalAssets().remove(Integer.parseInt(assetIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addCapitalAssetLocationByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        CapitalAssetLocation location = purchasingForm.getAndResetNewPurchasingCapitalAssetLocationLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingCapitalAssetLocationEvent("", purDocument, location));
        rulePassed = rulePassed && SpringContext.getBean(PurchasingService.class).checkCapitalAssetLocation(location);

        if (rulePassed) {
            // get specific asset item and grab system as well and attach asset number
            CapitalAssetSystem system = purDocument.getPurchasingCapitalAssetSystems().get(getSelectedLine(request));
            location.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            system.getCapitalAssetLocations().add(location);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addCapitalAssetLocationByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        CapitalAssetLocation location = purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request)).getPurchasingCapitalAssetSystem().getNewPurchasingCapitalAssetLocationLine();
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingCapitalAssetLocationEvent("", purDocument, location));

        if (rulePassed) {
            // get specific asset item and grab system as well and attach asset location
            PurchasingCapitalAssetItem assetItem = purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request));
            CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
            location.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            system.getCapitalAssetLocations().add(location);
            // now reset the location as all the rules are passed successfully
            purDocument.getPurchasingCapitalAssetItems().get(getSelectedLine(request)).getPurchasingCapitalAssetSystem().resetNewPurchasingCapitalAssetLocationLine();
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteCapitalAssetLocationByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        CapitalAssetLocation location = purchasingForm.getNewPurchasingCapitalAssetLocationLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();

        boolean rulePassed = true; // SpringContext.getBean(KualiRuleService.class).applyRules(new
        // AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
            String systemIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
            String locationIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);

            // get specific asset item and grab system as well and attach asset number
            CapitalAssetSystem system = purDocument.getPurchasingCapitalAssetSystems().get(Integer.parseInt(systemIndex));
            system.getCapitalAssetLocations().remove(Integer.parseInt(locationIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteCapitalAssetLocationByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        CapitalAssetLocation location = purchasingForm.getNewPurchasingCapitalAssetLocationLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();

        boolean rulePassed = true; // SpringContext.getBean(KualiRuleService.class).applyRules(new
        // AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
            String assetItemIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
            String locationIndex = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);

            // get specific asset item and grab system as well and attach asset number
            PurchasingCapitalAssetItem assetItem = purDocument.getPurchasingCapitalAssetItems().get(Integer.parseInt(assetItemIndex));
            CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
            system.getCapitalAssetLocations().remove(Integer.parseInt(locationIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward setupCAMSSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(document);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward selectSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();
        String errorPath = PurapConstants.CAPITAL_ASSET_TAB_ERRORS;
        // validate entry is selected for each field
        if (StringUtils.isEmpty(document.getCapitalAssetSystemTypeCode())) {
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_MISSING, "Capital Assets System Type Code");
        }
        else if (StringUtils.isEmpty(document.getCapitalAssetSystemStateCode())) {
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_MISSING, "Capital Assets System State Code");
        }
        else {
            SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(document);
            SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(document);
            if (!document.getPurchasingCapitalAssetItems().isEmpty()) {
                saveDocumentNoValidationUsingClearErrorMap(document);
            }
            else {
                // TODO: extract this and above strings to app resources
                GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_CUSTOM, "No items were found that met the requirements for Capital Asset data collection");
            }
            saveDocumentNoValidationUsingClearErrorMap(document);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    /**
     * Sets the error map to a new, empty error map before calling saveDocumentNoValidation to save the document.
     * 
     * @param document The purchase order document to be saved.
     */
    protected void saveDocumentNoValidationUsingClearErrorMap(PurchasingDocument document) {
        MessageMap errorHolder = GlobalVariables.getMessageMap();
        GlobalVariables.setMessageMap(new MessageMap());
        try {
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(document);
        }
        finally {
            GlobalVariables.setMessageMap(errorHolder);
        }
    }


    public ActionForward changeSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();
        Object question = request.getParameter(PurapConstants.QUESTION_INDEX);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if (question == null) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASING_QUESTION_CONFIRM_CHANGE_SYSTEM);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.CapitalAssetTabStrings.SYSTEM_SWITCHING_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "0");
        }
        else if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            // Add a note if system change occurs when the document is a PO that is being amended.
            if ((document instanceof PurchaseOrderDocument) && (PurapConstants.PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(document.getApplicationDocumentStatus()))) {
                Integer poId = document.getPurapDocumentIdentifier();
                PurchaseOrderDocument currentPO = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(poId);
                String oldSystemTypeCode = "";
                if (currentPO != null) {
                    oldSystemTypeCode = currentPO.getCapitalAssetSystemTypeCode();
                }
                CapitalAssetSystemType oldSystemType = new CapitalAssetSystemType();
                oldSystemType.setCapitalAssetSystemTypeCode(oldSystemTypeCode);
                Map<String, String> keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(oldSystemType);
                oldSystemType = (CapitalAssetSystemType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CapitalAssetSystemType.class, keys);
                String description = ((oldSystemType == null) ? "(NONE)" : oldSystemType.getCapitalAssetSystemTypeDescription());

                if (document instanceof PurchaseOrderAmendmentDocument) {
                    String noteText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_AMEND_MESSAGE_CHANGE_SYSTEM_TYPE);
                    noteText = StringUtils.replace(noteText, "{0}", description);

                    try {
                        Note systemTypeChangeNote = getDocumentService().createNoteFromDocument(document, noteText);
                        purchasingForm.setNewNote(systemTypeChangeNote);
                        insertBONote(mapping, purchasingForm, request, response);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            document.clearCapitalAssetFields();
            //saveDocumentNoValidationUsingClearErrorMap(document);

            
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(document);
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PURCHASING_MESSAGE_SYSTEM_CHANGED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

 
    public ActionForward updateCamsView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedUpdateCamsViewPurapEvent(document));

        if (rulePassed) {
            SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(document);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    public ActionForward setManufacturerFromVendorByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        String vendorName = document.getVendorName();
        if (StringUtils.isEmpty(vendorName)) {
            GlobalVariables.getMessageMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, PurapKeyConstants.ERROR_CAPITAL_ASSET_NO_VENDOR, (String[])null);
        }
        else {
            CapitalAssetSystem system = document.getPurchasingCapitalAssetSystems().get(getSelectedLine(request));
            if (system != null) {
                system.setCapitalAssetManufacturerName(vendorName);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward setManufacturerFromVendorByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        String vendorName = document.getVendorName();
        if (StringUtils.isEmpty(vendorName)) {
            GlobalVariables.getMessageMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, PurapKeyConstants.ERROR_CAPITAL_ASSET_NO_VENDOR, (String[])null);
        }
        else {
            PurchasingCapitalAssetItem assetItem = document.getPurchasingCapitalAssetItems().get(getSelectedLine(request));
            CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
            if (system != null) {
                system.setCapitalAssetManufacturerName(vendorName);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward selectNotCurrentYearByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        CapitalAssetSystem system = document.getPurchasingCapitalAssetSystems().get(getSelectedLine(request));
        if (system != null) {
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(true);
            system.setCapitalAssetTypeCode(SpringContext.getBean(PurchasingService.class).getDefaultAssetTypeCodeNotThisFiscalYear());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward selectNotCurrentYearByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        PurchasingCapitalAssetItem assetItem = document.getPurchasingCapitalAssetItems().get(getSelectedLine(request));
        CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
        if (system != null) {
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(true);
            system.setCapitalAssetTypeCode(SpringContext.getBean(PurchasingService.class).getDefaultAssetTypeCodeNotThisFiscalYear());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clearNotCurrentYearByDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        CapitalAssetSystem system = document.getPurchasingCapitalAssetSystems().get(getSelectedLine(request));
        if (system != null) {
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            system.setCapitalAssetTypeCode("");
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clearNotCurrentYearByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument document = (PurchasingDocument) purchasingForm.getDocument();

        PurchasingCapitalAssetItem assetItem = document.getPurchasingCapitalAssetItems().get(getSelectedLine(request));
        CapitalAssetSystem system = assetItem.getPurchasingCapitalAssetSystem();
        if (system != null) {
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            system.setCapitalAssetTypeCode("");
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument purDoc = (PurchasingDocument) purchasingForm.getDocument();

        boolean defaultUseTaxIndicatorValue = SpringContext.getBean(PurchasingService.class).getDefaultUseTaxIndicatorValue(purDoc);
        SpringContext.getBean(PurapService.class).updateUseTaxIndicator(purDoc, defaultUseTaxIndicatorValue);
        SpringContext.getBean(PurapService.class).calculateTax(purDoc);
        
        // call prorateDiscountTradeIn
        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(purDoc);

        //recalculate the amounts and percents on the accounting line.
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(purDoc);
        
        customCalculate(purDoc);

        PurchasingFormBase formBase = (PurchasingFormBase) form;
        formBase.setInitialZipCode(purDoc.getDeliveryPostalCode());
        formBase.setCalculated(true);
        
        KNSGlobalVariables.getMessageList().clear();

        return super.calculate(mapping, form, request, response);
    }

    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchasingFormBase formBase = (PurchasingFormBase) kualiDocumentFormBase;
        if (StringUtils.isEmpty(formBase.getInitialZipCode())) {
            formBase.setInitialZipCode(((PurchasingDocument) formBase.getDocument()).getDeliveryPostalCode());
        }
    }

    @Override
    public ActionForward clearAllTaxes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingDocument purDoc = (PurchasingDocument) purchasingForm.getDocument();

        SpringContext.getBean(PurapService.class).clearAllTaxes(purDoc);

        return super.clearAllTaxes(mapping, form, request, response);
    }

    /**
     * Determine from request parameters if user is returning from capital asset building lookup. Parameter will start with either
     * document.purchasingCapitalAssetItems or document.purchasingCapitalAssetSystems
     * 
     * @param request
     * @return
     */
    protected String findBuildingCodeFromCapitalAssetBuildingLookup(HttpServletRequest request) {
        Enumeration anEnum = request.getParameterNames();
        while (anEnum.hasMoreElements()) {
            String paramName = (String) anEnum.nextElement();
            if (paramName.contains("urchasingCapitalAsset") && paramName.contains("buildingCode")) {
                return paramName;
            }
        }
        return "";
    }

    /**
     * Overrides the superclass method so that it will also do proration for trade in and full order discount when the user clicks
     * on the submit button.
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDoc = (PurchasingDocument) purchasingForm.getDocument();
     
        // if form is not yet calculated, return and prompt user to calculate
        if (requiresCalculate(purchasingForm)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_PURCHASING_REQUIRES_CALCULATE);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        // call prorateDiscountTradeIn
        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(purDoc);

        return super.route(mapping, form, request, response);
    }

    /**
     * Overrides the superclass method so that it will also do proration for trade in and full order discount when the user clicks
     * on the approve button.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDoc = (PurchasingDocument) purchasingForm.getDocument();

        // call prorateDiscountTradeIn
        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(purDoc);
        
        return super.approve(mapping, form, request, response);
    }

    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurchasingDocument purDoc = (PurchasingDocument) purchasingForm.getDocument();
        // call prorateDiscountTradeIn
        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(purDoc);
        return super.blanketApprove(mapping, form, request, response);
    }

    /**
     * Checks if calculation is required. Currently it is required when it has not already been calculated and if the user can perform calculate
     * 
     * @return true if calculation is required, false otherwise
     */
    protected boolean requiresCalculate(PurchasingFormBase purForm) {
        boolean requiresCalculate = true;
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        
        requiresCalculate = salesTaxInd && (!purForm.isCalculated() && purForm.canUserCalculate());

        return requiresCalculate;
    }
}
