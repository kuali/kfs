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
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BShoppingService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.MissingContractIdError;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupCxml;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupResponse;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BShoppingServiceImpl implements B2BShoppingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BShoppingServiceImpl.class);

    private B2BDao b2bDao;
    private DocumentService documentService;
    private RequisitionService requisitionService;
    private ParameterService parameterService;
    private VendorService vendorService;


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
