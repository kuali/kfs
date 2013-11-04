/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.module.purap.util.cxml.B2BParserHelper;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupResponse;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BShoppingServiceImpl implements B2BShoppingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BShoppingServiceImpl.class);

    private B2BDao b2bDao;
    private BusinessObjectService businessObjectService;
    private ConfigurationService kualiConfigurationService;
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
    private String b2bShoppingIdentity;
    private String b2bShoppingPassword;

    protected B2BInformation getB2bShoppingConfigurationInformation() {
        B2BInformation b2b = new B2BInformation();
        b2b.setPunchoutURL(b2bPunchoutURL);
        b2b.setPunchbackURL(b2bPunchbackURL);
        b2b.setEnvironment(b2bEnvironment);
        b2b.setUserAgent(b2bUserAgent);
        b2b.setIdentity(b2bShoppingIdentity);
        b2b.setPassword(b2bShoppingPassword);
        return b2b;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#getPunchOutUrl(org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public String getPunchOutUrl(Person user) {
        // retrieve info for punchout (url, password, etc)
        B2BInformation b2b = getB2bShoppingConfigurationInformation();

        // send punchout request
        String response = b2bDao.sendPunchOutRequest(getPunchOutSetupRequestMessage(user, b2b), b2b.getPunchoutURL());

        // parse response
        PunchOutSetupResponse posr = B2BParserHelper.getInstance().parsePunchOutSetupResponse(response);

        // return url to use for punchout
        return posr.getPunchOutUrl();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#getPunchOutSetupRequestMessage(org.kuali.rice.kim.api.identity.Person,org.kuali.kfs.module.purap.businessobject.B2BInformation)
     */
    @Override
    public String getPunchOutSetupRequestMessage(Person user, B2BInformation b2bInformation) {
      StringBuffer cxml = new StringBuffer();
      Date d = SpringContext.getBean(DateTimeService.class).getCurrentDate();
      SimpleDateFormat date = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_DATE_FORMAT);
      SimpleDateFormat time = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_TIME_FORMAT);

      // doing as two parts b/c they want a T instead of space
      // between them, and SimpleDateFormat doesn't allow putting the
      // constant "T" in the string

      cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      cxml.append("<!DOCTYPE cXML SYSTEM \"cXML.dtd\">\n");
      cxml.append("<cXML payloadID=\"irrelevant\" xml:lang=\"en-US\" timestamp=\"").append(date.format(d)).append("T")
          .append(time.format(d)).append("-05:00").append("\">\n");

      // note that timezone is hard coded b/c this is the format
      // they wanted, but SimpleDateFormat returns -0500, so rather than
      // parse it just hard-coded

      cxml.append("  <Header>\n");
      cxml.append("    <From>\n");
      cxml.append("      <Credential domain=\"NetworkId\">\n");
      cxml.append("        <Identity>").append(b2bInformation.getIdentity()).append("</Identity>\n");
      cxml.append("      </Credential>\n");
      cxml.append("    </From>\n");
      cxml.append("    <To>\n");
      cxml.append("      <Credential domain=\"DUNS\">\n");
      cxml.append("        <Identity>").append(b2bInformation.getIdentity()).append("</Identity>\n");
      cxml.append("      </Credential>\n");
      cxml.append("      <Credential domain=\"internalsupplierid\">\n");
      cxml.append("        <Identity>1016</Identity>\n");
      cxml.append("      </Credential>\n");
      cxml.append("    </To>\n");
      cxml.append("    <Sender>\n");
      cxml.append("      <Credential domain=\"TOPSNetworkUserId\">\n");
      cxml.append("        <Identity>").append(user.getPrincipalName().toUpperCase()).append("</Identity>\n");
      cxml.append("        <SharedSecret>").append(b2bInformation.getPassword()).append("</SharedSecret>\n");
      cxml.append("      </Credential>\n");
      cxml.append("      <UserAgent>").append(b2bInformation.getUserAgent()).append("</UserAgent>\n");
      cxml.append("    </Sender>\n");
      cxml.append("  </Header>\n");
      cxml.append("  <Request deploymentMode=\"").append(b2bInformation.getEnvironment()).append("\">\n");
      cxml.append("    <PunchOutSetupRequest operation=\"create\">\n");
      cxml.append("      <BuyerCookie>").append(user.getPrincipalName().toUpperCase()).append("</BuyerCookie>\n");
      //cxml.append(" <Extrinsic
      // name=\"UserEmail\">jdoe@TOPS.com</Extrinsic>\n"); // we can't reliably
      // get the e-mail address, so we're leaving it out
      cxml.append("      <Extrinsic name=\"UniqueName\">").append(user.getPrincipalName().toUpperCase()).append("</Extrinsic>\n");
      cxml.append("      <Extrinsic name=\"Department\">IU").append(user.getCampusCode()).append(user.getPrimaryDepartmentCode()).append("</Extrinsic>\n");
      cxml.append("      <Extrinsic name=\"Campus\">").append(user.getCampusCode()).append("</Extrinsic>\n");
      cxml.append("      <BrowserFormPost>\n");
      cxml.append("        <URL>").append(b2bInformation.getPunchbackURL()).append("</URL>\n");
      cxml.append("      </BrowserFormPost>\n");
      cxml.append("      <Contact role=\"endUser\">\n");
      cxml.append("        <Name xml:lang=\"en\">").append(user.getName()).append("</Name>\n");
      //cxml.append(" <Email>jdoe@TOPS.com</Email>\n"); // again, we can't
      // reliably get this, so we're leaving it out
      cxml.append("      </Contact>\n");
      cxml.append("      <SupplierSetup>\n");
      cxml.append("        <URL>").append(b2bInformation.getPunchoutURL()).append("</URL>\n");
      cxml.append("      </SupplierSetup>\n");
      cxml.append("    </PunchOutSetupRequest>\n");
      cxml.append("  </Request>\n");
      cxml.append("</cXML>\n");

      return cxml.toString();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BService#createRequisitionsFromCxml(org.kuali.kfs.module.purap.util.cxml.B2BParserHelper,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
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

            req.setupAccountDistributionMethod();
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
            List itemsForVendor = getAllVendorItems(items, vendor);

            // default data from user
            req.setDeliveryCampusCode(user.getCampusCode());
            req.setDeliveryToName(user.getName());
            req.setDeliveryToEmailAddress(user.getEmailAddressUnmasked());
            req.setDeliveryToPhoneNumber(SpringContext.getBean(PhoneNumberService.class).formatNumberIfPossible(user.getPhoneNumber()));

            DefaultPrincipalAddress defaultPrincipalAddress = new DefaultPrincipalAddress(user.getPrincipalId());
            Map addressKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(defaultPrincipalAddress);
            defaultPrincipalAddress = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DefaultPrincipalAddress.class, addressKeys);
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
            req.setDocumentFundingSourceCode(parameterService.getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.DEFAULT_FUNDING_SOURCE));
            req.setRequisitionSourceCode(PurapConstants.RequisitionSources.B2B);

            req.updateAndSaveAppDocStatus(PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS);

            req.setPurchaseOrderTransmissionMethodCode(PurapConstants.POTransmissionMethods.ELECTRONIC);
            req.setOrganizationAutomaticPurchaseOrderLimit(purapService.getApoLimit(req.getVendorContractGeneratedIdentifier(), req.getChartOfAccountsCode(), req.getOrganizationCode()));

            //retrieve from an item (sent in cxml at item level, but stored in db at REQ level)
            req.setExternalOrganizationB2bSupplierIdentifier(getSupplierIdFromFirstItem(itemsForVendor));

            // retrieve default PO address and set address
            VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.PURCHASE_ORDER, user.getCampusCode());
            if (ObjectUtils.isNotNull(vendorAddress)) {
                req.templateVendorAddress(vendorAddress);
            }

            // retrieve billing address based on delivery campus and populate REQ with retrieved billing address
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setBillingCampusCode(req.getDeliveryCampusCode());
            Map keys = persistenceService.getPrimaryKeyFieldValues(billingAddress);
            billingAddress = businessObjectService.findByPrimaryKey(BillingAddress.class, keys);
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
     * Returns true if the system has been configured to use DUNS vendor numbers rather than
     * traditional internal vendor numbers.
     */
    private boolean isDunsNumberEnabled() {
        return parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_B2B_BY_VENDOR_DUNS_NUMBER_IND);
    }

    /**
     * Get all the vendors in a single shopping cart by the vendor number.
     *
     * @param items Items in the shopping cart
     * @return List of VendorDetails for each vendor in the shopping cart
     */
    protected List getAllVendors(List items) {
        LOG.debug("getAllVendors() started");

        Set vendorIdentifiers = new HashSet();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            vendorIdentifiers.add( getVendorNumber(item) );
        }

        ArrayList vendors = new ArrayList();
        for (Iterator iter = vendorIdentifiers.iterator(); iter.hasNext();) {
            String vendorIdentifier = (String) iter.next();
            VendorDetail vd = null;
            if (isDunsNumberEnabled()) {
                //retrieve vendor by duns number
                vd = vendorService.getVendorByDunsNumber(vendorIdentifier);
            }
            else {
                //retrieve vendor by vendor id
                vd = vendorService.getVendorDetail(vendorIdentifier);
            }

            if (ObjectUtils.isNotNull(vd)) {
                vendors.add(vd);
            }
            else {
                LOG.error("getAllVendors() Invalid vendor number or DUNS from shopping cart: " + vendorIdentifier);
                throw new B2BShoppingException("Invalid vendor number or DUNS from shopping cart: " + vendorIdentifier);
            }
        }

        return vendors;
    }

    /**
     * Get all the items for a specific vendor
     *
     * @param items List of all items
     * @param vendorId  String containing "vendorHeaderId-vendorDetailId"
     * @return list of RequisitionItems for a specific vendor id
     */
    protected List getAllVendorItems(List items, VendorDetail vendorDetail) {
        LOG.debug("getAllVendorItems() started");

        // determine if the system is configured to use DUNS numbers, rather than VendorNumbers, if so, use that to
        // filter vendor-specific items off the cart
        String vendorNumberOrDUNS = (isDunsNumberEnabled() ? vendorDetail.getVendorDunsNumber() : vendorDetail.getVendorNumber());

        // First get all the ShoppingCartItems for this vendor in a list
        List scItems = new ArrayList();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            if ( StringUtils.equals(vendorNumberOrDUNS, getVendorNumber(item)) ) {
                scItems.add(item);
            }
        }

        List<VendorCommodityCode> vcc = vendorDetail.getVendorCommodities();
        String defaultCommodityCode = null;
        Iterator<VendorCommodityCode> it = vcc.iterator();
        while (it.hasNext()) {
            VendorCommodityCode commodity = it.next();
            if (commodity.isCommodityDefaultIndicator()) {
                defaultCommodityCode = commodity.getPurchasingCommodityCode();
            }
        }

        // Now convert them to Requisition items
        int itemLine = 1;
        List vendorItems = new ArrayList();
        for (Iterator iter = scItems.iterator(); iter.hasNext();) {
            B2BShoppingCartItem item = (B2BShoppingCartItem) iter.next();
            RequisitionItem reqItem = createRequisitionItem(item, new Integer(itemLine), defaultCommodityCode);
            itemLine = itemLine + 1;
            vendorItems.add(reqItem);
        }

        return vendorItems;
    }


    // These are helper classes for extracting information from the cxml message
    protected RequisitionItem createRequisitionItem(B2BShoppingCartItem item, Integer itemLine, String defaultCommodityCode) {
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

        boolean commCodeParam = parameterService.getParameterValueAsBoolean(RequisitionDocument.class, PurapParameterConstants.ENABLE_DEFAULT_VENDOR_COMMODITY_CODE_IND);

        if (commCodeParam) {
            String classification = parameterService.getParameterValueAsString(RequisitionDocument.class, PurapParameterConstants.B2B_CLASSIFICATION_FOR_COMMODITY_CODE);
            if (StringUtils.isBlank(classification)) {
                classification = "UNSPSC";
            }

            String commodityCode = item.getClassification(classification);
            if (verifyCommodityCode(commodityCode)) {
                reqItem.setPurchasingCommodityCode(commodityCode);
            } else {
                reqItem.setPurchasingCommodityCode(defaultCommodityCode);
            }
        }
        //returned in cxml at item level, but stored in db at REQ level
        reqItem.setHoldSupplierId(item.getSupplier("SystemSupplierID"));

        return reqItem;
    }

    private boolean verifyCommodityCode(String commodityCode) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("purchasingCommodityCode", commodityCode);
        CommodityCode commodity = businessObjectService.findByPrimaryKey(CommodityCode.class, fieldValues);
        if (ObjectUtils.isNotNull(commodity)) {
            return true;
        } else {
            LOG.warn("Could not retrieve CommodityCode: "+commodityCode+"! Instead using default commodity code for vendor");
            return false;
        }
    }

    /**
     * The supplier id is received on the cxml at the item level, but we store it at the Requisition
     * at the document level.  Supplier id should be the same for each item received for a vendor so
     * just return the id held on the first item.
     *
     * @param reqItems
     * @return
     */
    protected String getSupplierIdFromFirstItem(List reqItems) {
        if (ObjectUtils.isNotNull(reqItems) && !reqItems.isEmpty()) {
            return ((RequisitionItem)reqItems.get(0)).getHoldSupplierId();
        }
        return "";
    }

    /**
     * Gets the vendor number from the specified B2BShoppingCartItem, depending on whether DUNS is enabled for B2B:
     * If yes, vendor DUNS number is retrieved from the SupplierId-DUNS tag in the B2B cxml file;
     * otherwise vendor ID is retrieved from the Extrinsic-ExternalSupplierId tag.
     *
     * @param item the specified B2BShoppingCartItem.
     * @return the Vendor number retrieved from the B2BShoppingCartItem.
     */
    protected String getVendorNumber(B2BShoppingCartItem item){
        String vendorNumber = null;

        if (isDunsNumberEnabled()) {
            vendorNumber = item.getSupplier("DUNS");
        }
        else {
            vendorNumber = item.getExtrinsic("ExternalSupplierId");
        }

        if (StringUtils.isBlank(vendorNumber)){
            throw new B2BShoppingException(PurapConstants.B2B_VENDOR_CONTRACT_NOT_FOUND_ERROR_MESSAGE);
        }

        return vendorNumber;
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

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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

    public void setB2bShoppingIdentity(String b2bShoppingIdentity) {
        this.b2bShoppingIdentity = b2bShoppingIdentity;
    }

    public void setB2bShoppingPassword(String password) {
        b2bShoppingPassword = password;
    }

}
