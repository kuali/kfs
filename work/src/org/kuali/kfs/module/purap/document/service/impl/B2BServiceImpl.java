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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.B2BInformation;
import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.B2BRemoteError;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.MissingContractIdError;
import org.kuali.kfs.module.purap.exception.ServiceError;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupCxml;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupResponse;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderCxml;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderResponse;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BServiceImpl implements B2BService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BServiceImpl.class);

    private B2BDao b2bDao;
    private DocumentService documentService;
    private RequisitionService requisitionService;
    private ParameterService parameterService;
    private VendorService vendorService;

    // FIXME do we really need this?
    private UniversalUserService universalUserService;

    private B2BInformation getB2bShoppingConfigurationInformation() {
        B2BInformation b2b = new B2BInformation();
        b2b.setPunchoutURL(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PUNCHOUT_URL));
        b2b.setPunchbackURL(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PUNCHBACK_URL));
        b2b.setEnvironment(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.ENVIRONMENT));
        b2b.setUserAgent(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.USER_AGENT));
        b2b.setPassword(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PASSWORD));
        return b2b;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#getPunchOutUrl(org.kuali.kfs.sys.businessobject.FinancialSystemUser)
     */
    public String getPunchOutUrl(FinancialSystemUser user) {
        // retrieve info for punchout (url, password, etc)
        B2BInformation b2b = getB2bShoppingConfigurationInformation();

        // create purnchout cxml
        PunchOutSetupCxml cxml = new PunchOutSetupCxml(user, b2b);

        // send punchout request
        String response = b2bDao.sendPunchOutRequest(cxml.getPunchOutSetupRequestMessage(), b2b.getPunchoutURL());

        // parse response
        PunchOutSetupResponse posr = new PunchOutSetupResponse(response);

        // return url to use for punchout
        return posr.getPunchOutUrl();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#createRequisitionsFromCxml(org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser,
     *      org.kuali.kfs.sys.businessobject.FinancialSystemUser)
     */
    public List createRequisitionsFromCxml(B2BShoppingCartParser message, FinancialSystemUser user) throws WorkflowException {
        LOG.debug("createRequisitionsFromCxml() started");
        // for returning requisitions
        ArrayList requisitions = new ArrayList();

        // get items from the cart
        List items = message.getItems();

        // get vendor(s) for the items
        List vendors = getAllVendors(items);

        // create requisition(s) (one per vendor)
        for (Iterator iter = vendors.iterator(); iter.hasNext();) {
            VendorDetail vendor = (VendorDetail) iter.next();

            // create requisition
            RequisitionDocument req = (RequisitionDocument) documentService.getNewDocument(PurapConstants.REQUISITION_DOCUMENT_TYPE);

            // set b2b contract for vendor
            VendorContract contract = vendorService.getVendorB2BContract(vendor, user.getCampusCode());
            if (ObjectUtils.isNotNull(contract)) {
                req.setVendorContract(contract);
                if (ObjectUtils.isNotNull(contract.getPurchaseOrderCostSourceCode())) {
                    // if cost source is set on contract, use it
                    req.setPurchaseOrderCostSourceCode(contract.getPurchaseOrderCostSourceCode());
                }
                else {
                    // if cost source is null on the contract, we set it by default to "Estimate"
                    req.setPurchaseOrderCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
                }
            }
            else {
                throw new MissingContractIdError("Contract ID is missing for vendor " + vendor.getVendorName());
            }

            String dunsNumber = vendor.getVendorDunsNumber();

            // get items for this vendor
            List itemsForVendor = getAllVendorItems(items, dunsNumber, user.getPersonUserIdentifier());
            String supplierId = getExternalSupplierId(items, dunsNumber);

            // default data from user
            req.setDeliveryCampusCode(user.getCampusCode());
            req.setChartOfAccountsCode(user.getChartOfAccountsCode());
            req.setOrganizationCode(user.getOrganizationCode());

            // set defaults that need to be set
            req.setVendorHeaderGeneratedIdentifier(vendor.getVendorHeaderGeneratedIdentifier());
            req.setVendorDetailAssignedIdentifier(vendor.getVendorDetailAssignedIdentifier());
            req.setVendorName(vendor.getVendorName());
            req.setVendorRestrictedIndicator(vendor.getVendorRestrictedIndicator());
            req.setItems(itemsForVendor);
            req.setRequisitionSourceCode(PurapConstants.RequisitionSources.B2B);
            req.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);
            req.setPurchaseOrderTransmissionMethodCode(PurapConstants.POTransmissionMethods.ELECTRONIC);
            req.setExternalOrganizationB2bSupplierIdentifier(supplierId);

            // set address
            VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.PURCHASE_ORDER, user.getCampusCode());
            if (ObjectUtils.isNotNull(vendorAddress)) {
                req.templateVendorAddress(vendorAddress);
                req.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            }
            else {
                // FIXME (hjs-b2b) not sure this data is populated; may need to call updateDefault method (should
                // updateDefaultvendoraddress be moved from PO service into vendor?)
                req.setVendorLine1Address(vendor.getDefaultAddressLine1());
                req.setVendorLine2Address(vendor.getDefaultAddressLine2());
                req.setVendorCityName(vendor.getDefaultAddressCity());
                req.setVendorStateCode(vendor.getDefaultAddressStateCode());
                req.setVendorPostalCode(vendor.getDefaultAddressPostalCode());
                req.setVendorCountryCode(vendor.getDefaultAddressCountryCode());
                req.setVendorFaxNumber(vendor.getDefaultFaxNumber());
                req.setVendorAddressGeneratedIdentifier(vendor.getVendorHeaderGeneratedIdentifier());
            }

            // retrieve billing address based on delivery campus
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setBillingCampusCode(req.getDeliveryCampusCode());
            Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
            billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);

            // populate REQ with retrieved billing address
            req.templateBillingAddress(billingAddress);

            // save requisition to database
            requisitionService.saveDocumentWithoutValidation(req);

            // add requisition to List
            requisitions.add(req);
        }
        return requisitions;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#sendPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.kfs.sys.businessobject.FinancialSystemUser)
     */
    public Collection sendPurchaseOrder(PurchaseOrderDocument purchaseOrder, FinancialSystemUser user) {
        LOG.debug("sendPurchaseOrder(PurchaseOrder purchaseOrder, UniversalUser user) started.");

        // Get what we need to create the cXML.
        PurchaseOrderCxml poCxml = new PurchaseOrderCxml(purchaseOrder);

        /*
         * IMPORTANT DESIGN NOTE: We need the contract manager's name, phone number, and e-mail address. B2B orders that don’t
         * qualify to become APO's will have contract managers on the PO, and the ones that DO become APO's will not. We decided to
         * always get the contract manager from the B2B contract associated with the order, and for B2B orders to ignore the
         * contract manager field on the PO. We pull the name and phone number from the contract manager table and get the e-mail
         * address from the user data.
         */

        ContractManager contractManager = purchaseOrder.getVendorContract().getContractManager();
        String contractManagerEmail = this.getContractManagerEmail(contractManager);

        String vendorDuns = purchaseOrder.getVendorDetail().getVendorDunsNumber();

        RequisitionDocument r = requisitionService.getRequisitionById(purchaseOrder.getRequisitionIdentifier());
        KualiWorkflowDocument reqWorkflowDoc = r.getDocumentHeader().getWorkflowDocument();
        UniversalUser initiator = null;
        try {
            universalUserService.getUniversalUser(reqWorkflowDoc.getInitiatorNetworkId());
        }
        catch (UserNotFoundException e) {
            LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
            return null;
        }

        String password = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_PASSWORD);
        String punchoutUrl = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_URL);
        LOG.debug("sendPurchaseOrder(): punchoutUrl is " + punchoutUrl);

        Collection errors;

        errors = poCxml.verifyCxmlPOData(initiator, password, contractManager, contractManagerEmail, vendorDuns);
        if (!errors.isEmpty()) {
            return errors;
        }
        else {
            errors = new ArrayList();
        }

        try {

            // check for vendor specific code
            // TODO: See about setting this zero items variable another way

            boolean includeZeroItems = true;
            /*
             * String beanId = "purVendorSpecific" + vendorDuns; LOG.debug("sendPurchaseOrder() beanId = " + beanId); if (
             * beanFactory.containsBean(beanId) ) { VendorSpecificService vendorService =
             * (VendorSpecificService)beanFactory.getBean(beanId); LOG.debug("getAllVendorItems() Using specific service for " +
             * vendorService.getVendorName()); includeZeroItems = vendorService.includeZeroItems(); } else {
             * LOG.debug("getAllVendorItems() No vendor specific service"); }
             */

            LOG.debug("sendPurchaseOrder() Generating cxml");
            String cxml = poCxml.getCxml(initiator, password, contractManager, contractManagerEmail, vendorDuns, includeZeroItems);

            LOG.debug("sendPurchaseOrder() Sending cxml");
            String responseCxml = b2bDao.sendPunchOutRequest(cxml, punchoutUrl);

            LOG.info("sendPurchaseOrder(): Response cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":" + responseCxml);

            PurchaseOrderResponse poResponse = new PurchaseOrderResponse(responseCxml);
            String statusText = poResponse.getStatusText();
            LOG.debug("sendPurchaseOrder(): statusText is " + statusText);
            if ((ObjectUtils.isNotNull(statusText)) || (!"success".equals(statusText.trim().toLowerCase()))) {
                LOG.error("sendPurchaseOrder(): PO cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + " failed sending to SciQuest: " + statusText);
                ServiceError se = new ServiceError("cxml.response.error", statusText);
                errors.add(se);
                // Find error messages other than the status.
                List errorMessages = poResponse.getPOResponseErrorMessages();
                if (ObjectUtils.isNotNull(errorMessages) || errorMessages.isEmpty()) {
                    // Not all of the cXML responses have error messages other than the status text error.
                    LOG.debug("sendPurchaseOrder() Unable to find errors in response other than status, but not all responses have other errors.");
                }
                else {
                    for (Iterator iter = errorMessages.iterator(); iter.hasNext();) {
                        String errorMessage = (String) iter.next();

                        if (ObjectUtils.isNotNull(errorMessage)) {
                            LOG.error("sendPurchaseOrder(): errorMessage not found.");
                            return null;
                        }
                        LOG.error("sendPurchaseOrder(): SciQuest error message for po number " + purchaseOrder.getPurapDocumentIdentifier() + ": " + errorMessage);
                        // FIXME (hjs-b2b) is there a way to avoid using ServiceError?
                        se = new ServiceError("cxml.response.error", errorMessage);
                        errors.add(se);
                    }
                }
            }
        }
        catch (B2BRemoteError sqre) {
            LOG.error("sendPurchaseOrder() Error sendng", sqre);
            errors.add(new ServiceError("cxml.response.error", "Unable to talk to SciQuest"));
        }
        catch (CxmlParseError e) {
            LOG.error("sendPurchaseOrder() Error Parsing", e);
            errors.add(new ServiceError("cxml.response.error", "Unable to read response"));
        }
        catch (Throwable e) {
            LOG.error("sendPurchaseOrder() Unknown Error", e);
            errors.add(new ServiceError("cxml.response.error", "Unknown exception: " + e.getMessage()));
        }

        return errors;
    }

    /**
     * Get all the vendors in a single shopping cart
     * 
     * @param items Items in the shopping cart
     * @return List of VendorDetails for each vendor in the shopping cart
     */
    private List getAllVendors(List items) {
        LOG.debug("getAllVendors() started");

        Set vendorDuns = new HashSet();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            vendorDuns.add(item.getSupplier("DUNS"));
        }

        ArrayList vendors = new ArrayList();
        for (Iterator iter = vendorDuns.iterator(); iter.hasNext();) {
            String duns = (String) iter.next();
            // TODO: get vendor by duns number
            VendorDetail vd = vendorService.getVendorByDunsNumber(duns);
            if (vd == null) {
                LOG.error("getAllVendors() Invalid DUNS number from shopping cart: " + duns);
                throw new IllegalArgumentException("Invalid DUNS number from shopping cart: " + duns);
            }
            vendors.add(vd);
        }

        return vendors;
    }

    /**
     * Get all the items for a specific vendor
     * 
     * @param items List of all items
     * @param vendorDuns Vendor DUNS
     * @param updateUserId last update user id
     * @return list of RequisitionItems for a specific DUNS
     */
    private List getAllVendorItems(List items, String vendorDuns, String updateUserId) {
        LOG.debug("getAllVendorItems() started");

        // First get all the ShoppingCartItems for this vendor in a list
        List scItems = new ArrayList();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            if (vendorDuns.equals(item.getSupplier("DUNS"))) {
                scItems.add(item);
            }
        }

        // Handle vendor specific code
        // TODO: seems like this just sorts the list, may need to incorporate this.
        /*
         * String beanId = "purVendorSpecific" + vendorDuns; if ( beanFactory.containsBean(beanId) ) { VendorSpecificService
         * vendorService = (VendorSpecificService)beanFactory.getBean(beanId); LOG.debug("getAllVendorItems() Using specific service
         * for " + vendorService.getVendorName()); scItems = vendorService.sortList(scItems); } else {
         * LOG.debug("getAllVendorItems() No vendor specific service"); }
         */

        // Now convert them to Requisition items
        int itemLine = 1;
        List vendorItems = new ArrayList();
        for (Iterator iter = scItems.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            RequisitionItem reqItem = createRequisitionItem(item, new Integer(itemLine));
            itemLine = itemLine + 1;
            vendorItems.add(reqItem);
        }

        return vendorItems;
    }


    // These are helper classes for extracting information from the cxml message
    private RequisitionItem createRequisitionItem(B2BShoppingCartItem item, Integer itemLine) {
        RequisitionItem reqItem = new RequisitionItem();
        reqItem.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        reqItem.setItemLineNumber(itemLine);
        reqItem.setItemUnitPrice(new BigDecimal(item.getUnitPrice()));
        reqItem.setItemQuantity(new KualiDecimal(item.getQuantity()));
        reqItem.setItemCatalogNumber(item.getSupplierPartId());
        reqItem.setItemAuxiliaryPartIdentifier(item.getSupplierPartAuxiliaryId());
        reqItem.setItemDescription(item.getDescription());
        reqItem.setItemUnitOfMeasureCode(item.getUnitOfMeasure());
        // FIXME looks like we need this field back in the item table; probably should be renamed to start with "external
        // organization..."
        // reqItem.setRequisitionLineId(item.getExtrinsic("RequisitionLineID"));
        reqItem.setExternalOrganizationB2bProductTypeName(item.getClassification("Product Source"));
        reqItem.setExternalOrganizationB2bProductReferenceNumber(item.getExtrinsic("SystemProductID"));
        reqItem.setItemRestrictedIndicator(false);

        return reqItem;
    }

    /**
     * This method looks up the Contract Manager's email
     */
    public String getContractManagerEmail(ContractManager cm) {
        try {
            UniversalUser contractManager = universalUserService.getUniversalUser(cm.getContractManagerUserIdentifier());
            return contractManager.getPersonEmailAddress();
        }
        catch (UserNotFoundException e) {
            LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
            return null;
        }
    }

    private String getExternalSupplierId(List items, String vendorDuns) {
        LOG.debug("getExternalSupplierId() ");

        String id = null;
        List scItems = new ArrayList();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();

            if (vendorDuns.equals(item.getSupplier("DUNS"))) {
                id = item.getSupplier("SystemSupplierID");
            }

        }

        return id;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setRequisitionService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setB2bDao(B2BDao b2bDao) {
        this.b2bDao = b2bDao;
    }

}
