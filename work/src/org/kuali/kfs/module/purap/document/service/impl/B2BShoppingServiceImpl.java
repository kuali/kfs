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
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.B2BInformation;
import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.DefaultPrincipalAddress;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BShoppingService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.exception.B2BShoppingException;
import org.kuali.kfs.module.purap.util.cxml.B2BParserHelper;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupCxml;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupResponse;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BShoppingServiceImpl implements B2BShoppingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BShoppingServiceImpl.class);

    private B2BDao b2bDao;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private PersistenceService persistenceService;
    private PhoneNumberService phoneNumberService;
    private PurchasingService purchasingService;
    private PurapService purapService;
    private VendorService vendorService;
    
    // injected values
    private String b2bEnvironment;
    private String b2bPunchoutURL;
    private String b2bPunchbackURL;
    private String b2bUserAgent;
    private String b2bShoppingPassword;
    private String b2bPurchaseOrderURL;
    private String b2bPurchaseOrderPassword;

    private B2BInformation getB2bShoppingConfigurationInformation() {
        B2BInformation b2b = new B2BInformation();

        //FIXME hjs (Sciquest)
        b2b.setPunchoutURL(b2bPunchoutURL);
        b2b.setPunchbackURL(b2bPunchbackURL);
        b2b.setEnvironment(b2bEnvironment);
        b2b.setUserAgent(b2bUserAgent);
        b2b.setPassword(b2bShoppingPassword);
        return b2b;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#getPunchOutUrl(org.kuali.rice.kim.bo.Person)
     */
    public String getPunchOutUrl(Person user) {
        // retrieve info for punchout (url, password, etc)
        B2BInformation b2b = getB2bShoppingConfigurationInformation();

        // create purnchout cxml
        PunchOutSetupCxml cxml = new PunchOutSetupCxml(user, b2b);

        // send punchout request
        String response = b2bDao.sendPunchOutRequest(cxml.getPunchOutSetupRequestMessage(), b2b.getPunchoutURL());

        // parse response
        PunchOutSetupResponse posr = B2BParserHelper.getInstance().parsePunchOutSetupResponse(response);

        // return url to use for punchout
        return posr.getPunchOutUrl();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#createRequisitionsFromCxml(org.kuali.kfs.module.purap.util.cxml.B2BParserHelper,
     *      org.kuali.rice.kim.bo.Person)
     */
    public List createRequisitionsFromCxml(B2BShoppingCart message, Person user) throws WorkflowException {
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
                req.setVendorContractGeneratedIdentifier(contract.getVendorContractGeneratedIdentifier());
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
                LOG.error("createRequisitionsFromCxml() Contract is missing for vendor " + vendor.getVendorName() + " (" + vendor.getVendorNumber() + ")");
                throw new B2BShoppingException(PurapConstants.B2B_VENDOR_CONTRACT_NOT_FOUND_ERROR_MESSAGE);
            }

            // get items for this vendor
            List itemsForVendor = getAllVendorItems(items, vendor.getVendorNumber(), user.getPrincipalName());

            // default data from user
            req.setDeliveryCampusCode(user.getCampusCode());
            req.setDeliveryToName(user.getName());
            req.setDeliveryToEmailAddress(user.getEmailAddressUnmasked());
            req.setDeliveryToPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(user.getPhoneNumber()));
            
            DefaultPrincipalAddress defaultPrincipalAddress = new DefaultPrincipalAddress(user.getPrincipalId());
            Map addressKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(defaultPrincipalAddress);
            defaultPrincipalAddress = (DefaultPrincipalAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DefaultPrincipalAddress.class, addressKeys);
            if (ObjectUtils.isNotNull(defaultPrincipalAddress) && ObjectUtils.isNotNull(defaultPrincipalAddress.getBuilding())) {
                if (defaultPrincipalAddress.getBuilding().isActive()) {
                    req.setDeliveryCampusCode(defaultPrincipalAddress.getCampusCode());
                    req.templateBuildingToDeliveryAddress(defaultPrincipalAddress.getBuilding());
                    req.setDeliveryBuildingRoomNumber(defaultPrincipalAddress.getBuildingRoomNumber());
                }
                else {
                    //since building is now inactive, delete default building record
                    SpringContext.getBean(BusinessObjectService.class).delete(defaultPrincipalAddress);
                }
            }
            
            ChartOrgHolder purapChartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(user, PurapConstants.PURAP_NAMESPACE);
            if (ObjectUtils.isNotNull(purapChartOrg)) {
                req.setChartOfAccountsCode(purapChartOrg.getChartOfAccountsCode());
                req.setOrganizationCode(purapChartOrg.getOrganizationCode());
            }
            
            req.setRequestorPersonName(user.getName());
            req.setRequestorPersonEmailAddress(user.getEmailAddress());
            req.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(user.getPhoneNumber()));
            req.setUseTaxIndicator(purchasingService.getDefaultUseTaxIndicatorValue(req));

            // set defaults that need to be set
            req.setVendorHeaderGeneratedIdentifier(vendor.getVendorHeaderGeneratedIdentifier());
            req.setVendorDetailAssignedIdentifier(vendor.getVendorDetailAssignedIdentifier());
            req.setVendorName(vendor.getVendorName());
            req.setVendorRestrictedIndicator(vendor.getVendorRestrictedIndicator());
            req.setItems(itemsForVendor);
            req.setDocumentFundingSourceCode(parameterService.getParameterValue(RequisitionDocument.class, PurapParameterConstants.DEFAULT_FUNDING_SOURCE));
            req.setRequisitionSourceCode(PurapConstants.RequisitionSources.B2B);
            req.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);
            req.setPurchaseOrderTransmissionMethodCode(PurapConstants.POTransmissionMethods.ELECTRONIC);
            req.setOrganizationAutomaticPurchaseOrderLimit(purapService.getApoLimit(req.getVendorContractGeneratedIdentifier(), req.getChartOfAccountsCode(), req.getOrganizationCode()));

            // retrieve default PO address and set address
            VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.PURCHASE_ORDER, user.getCampusCode());
            if (ObjectUtils.isNotNull(vendorAddress)) {
                req.templateVendorAddress(vendorAddress);
            }

            // retrieve billing address based on delivery campus and populate REQ with retrieved billing address
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setBillingCampusCode(req.getDeliveryCampusCode());
            Map keys = persistenceService.getPrimaryKeyFieldValues(billingAddress);
            billingAddress = (BillingAddress) businessObjectService.findByPrimaryKey(BillingAddress.class, keys);
            req.templateBillingAddress(billingAddress);

            // populate receiving address with the default one for the chart/org
            req.loadReceivingAddress();

            req.fixItemReferences();
            
            // save requisition to database
            purapService.saveDocumentNoValidation(req);

            // add requisition to List
            requisitions.add(req);
        }
        return requisitions;
    }

    /**
     * Get all the vendors in a single shopping cart by the vendor number. 
     * 
     * @param items Items in the shopping cart
     * @return List of VendorDetails for each vendor in the shopping cart
     */
    private List getAllVendors(List items) {
        LOG.debug("getAllVendors() started");

        Set vendorNumbers = new HashSet();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            String externalSupplierId = item.getExtrinsic("ExternalSupplierId");
            if (StringUtils.isBlank(externalSupplierId)){
                throw new B2BShoppingException(PurapConstants.B2B_VENDOR_CONTRACT_NOT_FOUND_ERROR_MESSAGE);
            }
            vendorNumbers.add(item.getExtrinsic("ExternalSupplierId"));
        }

        ArrayList vendors = new ArrayList();
        for (Iterator iter = vendorNumbers.iterator(); iter.hasNext();) {
            String vendorNumber = (String) iter.next();
            VendorDetail vd = vendorService.getVendorDetail(vendorNumber);
            
            if (ObjectUtils.isNotNull(vd)) {
                vendors.add(vd);
            }
            else {
                LOG.error("getAllVendors() Invalid vendor number from shopping cart: " + vendorNumber);
                throw new B2BShoppingException("Invalid vendor number from shopping cart: " + vendorNumber);
            }
        }

        return vendors;
    }

    /**
     * Get all the items for a specific vendor
     * 
     * @param items List of all items
     * @param vendorId  String containing "vendorHeaderId-vendorDetailId"
     * @param updateUserId last update user id
     * @return list of RequisitionItems for a specific vendor id
     */
    private List getAllVendorItems(List items, String vendorId, String updateUserId) {
        LOG.debug("getAllVendorItems() started");

        // First get all the ShoppingCartItems for this vendor in a list
        List scItems = new ArrayList();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            if (vendorId.equals(item.getExtrinsic("ExternalSupplierId"))) {
                scItems.add(item);
            }
        }

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
        reqItem.setExternalOrganizationB2bProductTypeName(item.getExtrinsic("Product Source"));
        reqItem.setExternalOrganizationB2bProductReferenceNumber(item.getExtrinsic("SystemProductID"));
        reqItem.setItemRestrictedIndicator(false);

        return reqItem;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setB2bDao(B2BDao b2bDao) {
        this.b2bDao = b2bDao;
    }

    public void setPhoneNumberService(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurchasingService(PurchasingService purchasingService) {
        this.purchasingService = purchasingService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setB2bEnvironment(String environment) {
        b2bEnvironment = environment;
    }

    public void setB2bPunchoutURL(String punchoutURL) {
        b2bPunchoutURL = punchoutURL;
    }

    public void setB2bPunchbackURL(String punchbackURL) {
        b2bPunchbackURL = punchbackURL;
    }

    public void setB2bUserAgent(String userAgent) {
        b2bUserAgent = userAgent;
    }

    public void setB2bShoppingPassword(String password) {
        b2bShoppingPassword = password;
    }

    public void setB2bPurchaseOrderURL(String purchaseOrderURL) {
        b2bPurchaseOrderURL = purchaseOrderURL;
    }

    public void setB2bPurchaseOrderPassword(String purchaseOrderPassword) {
        b2bPurchaseOrderPassword = purchaseOrderPassword;
    }

}

