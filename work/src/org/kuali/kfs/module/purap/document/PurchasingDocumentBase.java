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
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemState;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemType;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.FundingSource;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.businessobject.RequisitionSource;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.service.ReceivingAddressService;
import org.kuali.kfs.module.purap.util.ItemParser;
import org.kuali.kfs.module.purap.util.ItemParserBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

/**
 * Base class for Purchasing Documents.
 */
public abstract class PurchasingDocumentBase extends PurchasingAccountsPayableDocumentBase implements PurchasingDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION AND PURCHASE ORDER
    protected String documentFundingSourceCode;
    protected String requisitionSourceCode;
    protected String purchaseOrderTransmissionMethodCode;
    protected String purchaseOrderCostSourceCode;
    protected String deliveryRequiredDateReasonCode;
    protected String recurringPaymentTypeCode;
    protected String chartOfAccountsCode;
    protected String organizationCode;
    protected String deliveryCampusCode;
    protected KualiDecimal purchaseOrderTotalLimit;
    protected Boolean vendorRestrictedIndicator;
    protected String vendorPhoneNumber;
    protected String vendorFaxNumber;
    protected Integer vendorContractGeneratedIdentifier;
    protected String vendorNoteText;
    protected String requestorPersonName;
    protected String requestorPersonEmailAddress;
    protected String requestorPersonPhoneNumber;
    protected String nonInstitutionFundOrgChartOfAccountsCode;
    protected String nonInstitutionFundOrganizationCode;
    protected String nonInstitutionFundChartOfAccountsCode;
    protected String nonInstitutionFundAccountNumber;
    protected boolean deliveryBuildingOtherIndicator;
    protected String deliveryBuildingCode;
    protected String deliveryBuildingName;
    protected String deliveryBuildingRoomNumber;
    protected String deliveryBuildingLine1Address;
    protected String deliveryBuildingLine2Address;
    protected String deliveryCityName;
    protected String deliveryStateCode;
    protected String deliveryPostalCode;
    protected String deliveryCountryCode;
    protected String deliveryToName;
    protected String deliveryToEmailAddress;
    protected String deliveryToPhoneNumber;
    protected Date deliveryRequiredDate;
    protected String deliveryInstructionText;
    protected Date purchaseOrderBeginDate;
    protected Date purchaseOrderEndDate;
    protected String institutionContactName;
    protected String institutionContactPhoneNumber;
    protected String institutionContactEmailAddress;
    protected String billingName;
    protected String billingLine1Address;
    protected String billingLine2Address;
    protected String billingCityName;
    protected String billingStateCode;
    protected String billingPostalCode;
    protected String billingCountryCode;
    protected String billingPhoneNumber;
    protected String billingEmailAddress;
    protected String receivingName;
    protected String receivingLine1Address;
    protected String receivingLine2Address;
    protected String receivingCityName;
    protected String receivingStateCode;
    protected String receivingPostalCode;
    protected String receivingCountryCode;
    protected boolean addressToVendorIndicator; // if true, use receiving address
    protected String externalOrganizationB2bSupplierIdentifier;
    protected boolean purchaseOrderAutomaticIndicator;
    protected String vendorPaymentTermsCode;
    protected String vendorShippingTitleCode;
    protected String vendorShippingPaymentTermsCode;
    protected String capitalAssetSystemTypeCode;
    protected String capitalAssetSystemStateCode;
    protected String justification;

    // NOT PERSISTED IN DB
    protected String supplierDiversityLabel;
    protected String vendorContactsLabel;

    // REFERENCE OBJECTS
    protected FundingSource fundingSource;
    protected RequisitionSource requisitionSource;
    protected PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod;
    protected PurchaseOrderCostSource purchaseOrderCostSource;
    protected DeliveryRequiredDateReason deliveryRequiredDateReason;
    protected RecurringPaymentType recurringPaymentType;
    protected Organization organization;
    protected Chart chartOfAccounts;
    protected CampusParameter deliveryCampus;
    protected Chart nonInstitutionFundOrgChartOfAccounts;
    protected Organization nonInstitutionFundOrganization;
    protected Account nonInstitutionFundAccount;
    protected Chart nonInstitutionFundChartOfAccounts;
    protected VendorContract vendorContract;
    protected CapitalAssetSystemType capitalAssetSystemType;
    protected CapitalAssetSystemState capitalAssetSystemState;
    protected List<CapitalAssetSystem> purchasingCapitalAssetSystems;
    protected List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems;

    protected boolean receivingDocumentRequiredIndicator;
    protected boolean paymentRequestPositiveApprovalIndicator;

    protected List<CommodityCode> commodityCodesForRouting;

    /**
     * Default Constructor.
     */
    public PurchasingDocumentBase() {
        super();

        purchasingCapitalAssetItems = new ArrayList();
        purchasingCapitalAssetSystems = new ArrayList();
    }

    @Override
    public abstract PurchasingDocumentSpecificService getDocumentSpecificService();

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#templateVendorDetail(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    @Override
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (ObjectUtils.isNotNull(vendorDetail)) {
            this.setVendorDetail(vendorDetail);
            this.setVendorName(vendorDetail.getVendorName());
            this.setVendorShippingTitleCode(vendorDetail.getVendorShippingTitleCode());
            this.setVendorPaymentTermsCode(vendorDetail.getVendorPaymentTermsCode());
            this.setVendorShippingPaymentTermsCode(vendorDetail.getVendorShippingPaymentTermsCode());
            this.setVendorCustomerNumber("");
        }
    }
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#templateVendorContract(org.kuali.kfs.vnd.businessobject.VendorContract)
     */
    @Override
    public void templateVendorContract(VendorContract vendorContract) {
        if (ObjectUtils.isNotNull(vendorContract)) {
            this.setVendorContract(vendorContract);
            this.setVendorContractGeneratedIdentifier(vendorContract.getVendorContractGeneratedIdentifier());
            this.setVendorShippingTitleCode(vendorContract.getVendorShippingTitleCode());
            this.setVendorPaymentTermsCode(vendorContract.getVendorPaymentTermsCode());
            this.setVendorShippingPaymentTermsCode(vendorContract.getVendorShippingPaymentTermsCode());
            this.setPurchaseOrderCostSourceCode(vendorContract.getPurchaseOrderCostSourceCode());
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#templateVendorAddress(org.kuali.kfs.vnd.businessobject.VendorAddress)
     */
    @Override
    public void templateVendorAddress(VendorAddress vendorAddress) {
        super.templateVendorAddress(vendorAddress);
        if (vendorAddress != null) {
            this.setVendorFaxNumber(vendorAddress.getVendorFaxNumber());
            this.setVendorAttentionName(vendorAddress.getVendorAttentionName());
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#templateBillingAddress(org.kuali.kfs.module.purap.businessobject.BillingAddress).
     */
    @Override
    public void templateBillingAddress(BillingAddress billingAddress) {
        if (ObjectUtils.isNotNull(billingAddress)) {
            this.setBillingName(billingAddress.getBillingName());
            this.setBillingLine1Address(billingAddress.getBillingLine1Address());
            this.setBillingLine2Address(billingAddress.getBillingLine2Address());
            this.setBillingCityName(billingAddress.getBillingCityName());
            this.setBillingStateCode(billingAddress.getBillingStateCode());
            this.setBillingPostalCode(billingAddress.getBillingPostalCode());
            this.setBillingCountryCode(billingAddress.getBillingCountryCode());
            this.setBillingPhoneNumber(billingAddress.getBillingPhoneNumber());
            this.setBillingEmailAddress(billingAddress.getBillingEmailAddress());
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#templateReceivingAddress(org.kuali.kfs.module.purap.businessobject.ReceivingAddress).
     */
    @Override
    public void templateReceivingAddress(ReceivingAddress receivingAddress) {
        if (receivingAddress != null) {
            this.setReceivingName(receivingAddress.getReceivingName());
            this.setReceivingLine1Address(receivingAddress.getReceivingLine1Address());
            this.setReceivingLine2Address(receivingAddress.getReceivingLine2Address());
            this.setReceivingCityName(receivingAddress.getReceivingCityName());
            this.setReceivingStateCode(receivingAddress.getReceivingStateCode());
            this.setReceivingPostalCode(receivingAddress.getReceivingPostalCode());
            this.setReceivingCountryCode(receivingAddress.getReceivingCountryCode());
            this.setAddressToVendorIndicator(receivingAddress.isUseReceivingIndicator());
        }
        else {
            this.setReceivingName(null);
            this.setReceivingLine1Address(null);
            this.setReceivingLine2Address(null);
            this.setReceivingCityName(null);
            this.setReceivingStateCode(null);
            this.setReceivingPostalCode(null);
            this.setReceivingCountryCode(null);
            this.setAddressToVendorIndicator(false);
        }
    }

    /**
     * Loads the default receiving address from database corresponding to the chart/org of this document.
     */
    @Override
    public void loadReceivingAddress() {
        /*
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, this.getOrganizationCode());
        criteria.put(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);
        criteria.put(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);
        List<ReceivingAddress> addresses = (List)SpringContext.getBean(BusinessObjectService.class).findMatching(ReceivingAddress.class, criteria);
        if (addresses != null && addresses.size()>0 )
            this.templateReceivingAddress(addresses.get(0));
        else // if no address is found, fill with null
            this.templateReceivingAddress(null);
        */
        String chartCode = getChartOfAccountsCode();
        String orgCode = getOrganizationCode();
        ReceivingAddress address = SpringContext.getBean(ReceivingAddressService.class).findUniqueDefaultByChartOrg(chartCode, orgCode);
        // if default address for chart/org not found, look for chart default
        if (address == null && orgCode != null) {
            address = SpringContext.getBean(ReceivingAddressService.class).findUniqueDefaultByChartOrg(chartCode, null);
        }
        this.templateReceivingAddress(address);
    }

    /**
     * Iterates through the purchasingCapitalAssetItems of the document and returns the purchasingCapitalAssetItem with the item id equal to the number given, or null if a
     * match is not found.
     *
     * @param itemIdentifier item id to match on.
     * @return the PurchasingCapitalAssetItem if a match is found, else null.
     */
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItemByItemIdentifier(int itemIdentifier) {
        for (Iterator iter = purchasingCapitalAssetItems.iterator(); iter.hasNext();) {
            PurchasingCapitalAssetItem camsItem = (PurchasingCapitalAssetItem) iter.next();
            if (camsItem.getItemIdentifier().intValue() == itemIdentifier) {
                return camsItem;
            }
        }
        return null;
    }


    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#addItem(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public void addItem(PurApItem item) {
        item.refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
        super.addItem(item);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        commodityCodesForRouting = new ArrayList<CommodityCode>();
        for (PurchasingItemBase item : (List<PurchasingItemBase>)this.getItems()) {
            if (item.getCommodityCode() != null && !commodityCodesForRouting.contains(item.getCommodityCode())) {
                commodityCodesForRouting.add(item.getCommodityCode());
            }
        }
        super.populateDocumentForRouting();
    }

    // GETTERS AND SETTERS

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument.getItemParser().
     */
    @Override
    public ItemParser getItemParser() {
        return new ItemParserBase();
    }

    /**
     * Decides whether receivingDocumentRequiredIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnableReceivingDocumentRequiredIndicator() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.RECEIVING_DOCUMENT_REQUIRED_IND);
    }

    /**
     * Decides whether paymentRequestPositiveApprovalIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnablePaymentRequestPositiveApprovalIndicator() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.PAYMENT_REQUEST_POSITIVE_APPROVAL_IND);
    }

    @Override
    public String getBillingCityName() {
        return billingCityName;
    }

    @Override
    public void setBillingCityName(String billingCityName) {
        this.billingCityName = billingCityName;
    }

    @Override
    public String getBillingCountryCode() {
        return billingCountryCode;
    }

    @Override
    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    @Override
    public String getBillingCountryName() {
        if ( StringUtils.isNotBlank(getBillingCountryCode()) ) {
            Country country = SpringContext.getBean(CountryService.class).getCountry(getBillingCountryCode());
            if (country != null) {
                return country.getName();
            }
        }
        return null;
    }

    @Override
    public String getBillingLine1Address() {
        return billingLine1Address;
    }

    @Override
    public void setBillingLine1Address(String billingLine1Address) {
        this.billingLine1Address = billingLine1Address;
    }

    @Override
    public String getBillingLine2Address() {
        return billingLine2Address;
    }

    @Override
    public void setBillingLine2Address(String billingLine2Address) {
        this.billingLine2Address = billingLine2Address;
    }

    @Override
    public String getBillingName() {
        return billingName;
    }

    @Override
    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    @Override
    public String getBillingPhoneNumber() {
        return billingPhoneNumber;
    }

    @Override
    public void setBillingPhoneNumber(String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }

    public String getBillingEmailAddress() {
        return billingEmailAddress;
    }

    public void setBillingEmailAddress(String billingEmailAddress) {
        this.billingEmailAddress = billingEmailAddress;
    }

    @Override
    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    @Override
    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    @Override
    public String getBillingStateCode() {
        return billingStateCode;
    }

    @Override
    public void setBillingStateCode(String billingStateCode) {
        this.billingStateCode = billingStateCode;
    }

    @Override
    public String getReceivingCityName() {
        return receivingCityName;
    }

    @Override
    public void setReceivingCityName(String receivingCityName) {
        this.receivingCityName = receivingCityName;
    }

    @Override
    public String getReceivingCountryCode() {
        return receivingCountryCode;
    }

    @Override
    public void setReceivingCountryCode(String receivingCountryCode) {
        this.receivingCountryCode = receivingCountryCode;
    }

    @Override
    public String getReceivingCountryName() {
        if ( StringUtils.isNotBlank(getReceivingCountryCode()) ) {
            Country country = SpringContext.getBean(CountryService.class).getCountry(getReceivingCountryCode());
            if (country != null) {
                return country.getName();
            }
        }
        return null;
    }

    @Override
    public String getReceivingLine1Address() {
        return receivingLine1Address;
    }

    @Override
    public void setReceivingLine1Address(String receivingLine1Address) {
        this.receivingLine1Address = receivingLine1Address;
    }

    @Override
    public String getReceivingLine2Address() {
        return receivingLine2Address;
    }

    @Override
    public void setReceivingLine2Address(String receivingLine2Address) {
        this.receivingLine2Address = receivingLine2Address;
    }

    @Override
    public String getReceivingName() {
        return receivingName;
    }

    @Override
    public void setReceivingName(String receivingName) {
        this.receivingName = receivingName;
    }

    @Override
    public String getReceivingPostalCode() {
        return receivingPostalCode;
    }

    @Override
    public void setReceivingPostalCode(String receivingPostalCode) {
        this.receivingPostalCode = receivingPostalCode;
    }

    @Override
    public String getReceivingStateCode() {
        return receivingStateCode;
    }

    @Override
    public void setReceivingStateCode(String receivingStateCode) {
        this.receivingStateCode = receivingStateCode;
    }

    @Override
    public boolean getAddressToVendorIndicator() {
        return addressToVendorIndicator;
    }

    @Override
    public void setAddressToVendorIndicator(boolean addressToVendor) {
        this.addressToVendorIndicator = addressToVendor;
    }

    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    @Override
    public String getDeliveryBuildingCode() {
        return deliveryBuildingCode;
    }

    @Override
    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    @Override
    public String getDeliveryBuildingLine1Address() {
        return deliveryBuildingLine1Address;
    }

    @Override
    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    @Override
    public String getDeliveryBuildingLine2Address() {
        return deliveryBuildingLine2Address;
    }

    @Override
    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    @Override
    public String getDeliveryBuildingName() {
        return deliveryBuildingName;
    }

    @Override
    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    @Override
    public boolean isDeliveryBuildingOtherIndicator() {
        return deliveryBuildingOtherIndicator;
    }

    @Override
    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator) {
        this.deliveryBuildingOtherIndicator = deliveryBuildingOtherIndicator;
    }

    @Override
    public String getDeliveryBuildingRoomNumber() {
        return deliveryBuildingRoomNumber;
    }

    @Override
    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    @Override
    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    @Override
    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    @Override
    public String getDeliveryCityName() {
        return deliveryCityName;
    }

    @Override
    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    @Override
    public String getDeliveryCountryCode() {
        return deliveryCountryCode;
    }

    @Override
    public String getDeliveryCountryName() {
        if ( StringUtils.isNotBlank(getDeliveryCountryCode()) ) {
            Country country = SpringContext.getBean(CountryService.class).getCountry(getDeliveryCountryCode());
            if (country != null) {
                return country.getName();
            }
        }
        return null;
    }

    @Override
    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }


    @Override
    public String getDeliveryInstructionText() {
        return deliveryInstructionText;
    }

    @Override
    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    @Override
    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    @Override
    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    @Override
    public Date getDeliveryRequiredDate() {
        return deliveryRequiredDate;
    }

    @Override
    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    @Override
    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    @Override
    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    @Override
    public String getDeliveryStateCode() {
        return deliveryStateCode;
    }

    @Override
    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    @Override
    public String getDeliveryToEmailAddress() {
        return deliveryToEmailAddress;
    }

    @Override
    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    @Override
    public String getDeliveryToName() {
        return deliveryToName;
    }

    @Override
    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    @Override
    public String getDeliveryToPhoneNumber() {
        return deliveryToPhoneNumber;
    }

    @Override
    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    @Override
    public String getExternalOrganizationB2bSupplierIdentifier() {
        return externalOrganizationB2bSupplierIdentifier;
    }

    @Override
    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier) {
        this.externalOrganizationB2bSupplierIdentifier = externalOrganizationB2bSupplierIdentifier;
    }

    @Override
    public String getDocumentFundingSourceCode() {
        return documentFundingSourceCode;
    }

    @Override
    public void setDocumentFundingSourceCode(String documentFundingSourceCode) {
        this.documentFundingSourceCode = documentFundingSourceCode;
    }

    @Override
    public String getInstitutionContactEmailAddress() {
        return institutionContactEmailAddress;
    }

    @Override
    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress) {
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }

    @Override
    public String getInstitutionContactName() {
        return institutionContactName;
    }

    @Override
    public void setInstitutionContactName(String institutionContactName) {
        this.institutionContactName = institutionContactName;
    }

    @Override
    public String getInstitutionContactPhoneNumber() {
        return institutionContactPhoneNumber;
    }

    @Override
    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber) {
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
    }

    @Override
    public String getNonInstitutionFundAccountNumber() {
        return nonInstitutionFundAccountNumber;
    }

    @Override
    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber) {
        this.nonInstitutionFundAccountNumber = nonInstitutionFundAccountNumber;
    }

    @Override
    public String getNonInstitutionFundChartOfAccountsCode() {
        return nonInstitutionFundChartOfAccountsCode;
    }

    @Override
    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode) {
        this.nonInstitutionFundChartOfAccountsCode = nonInstitutionFundChartOfAccountsCode;
    }

    @Override
    public String getNonInstitutionFundOrganizationCode() {
        return nonInstitutionFundOrganizationCode;
    }

    @Override
    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode) {
        this.nonInstitutionFundOrganizationCode = nonInstitutionFundOrganizationCode;
    }

    @Override
    public String getNonInstitutionFundOrgChartOfAccountsCode() {
        return nonInstitutionFundOrgChartOfAccountsCode;
    }

    @Override
    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode) {
        this.nonInstitutionFundOrgChartOfAccountsCode = nonInstitutionFundOrgChartOfAccountsCode;
    }

    @Override
    public String getOrganizationCode() {
        return organizationCode;
    }

    @Override
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    @Override
    public boolean getPurchaseOrderAutomaticIndicator() {
        return purchaseOrderAutomaticIndicator;
    }

    @Override
    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator) {
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
    }

    @Override
    public Date getPurchaseOrderBeginDate() {
        return purchaseOrderBeginDate;
    }

    @Override
    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate) {
        this.purchaseOrderBeginDate = purchaseOrderBeginDate;
    }

    @Override
    public String getPurchaseOrderCostSourceCode() {
        return purchaseOrderCostSourceCode;
    }

    @Override
    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    @Override
    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    @Override
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    @Override
    public KualiDecimal getPurchaseOrderTotalLimit() {
        return purchaseOrderTotalLimit;
    }

    @Override
    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit) {
        this.purchaseOrderTotalLimit = purchaseOrderTotalLimit;
    }

    @Override
    public String getPurchaseOrderTransmissionMethodCode() {
        return purchaseOrderTransmissionMethodCode;
    }

    @Override
    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode) {
        this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
    }

    @Override
    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    @Override
    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    @Override
    public String getRequestorPersonEmailAddress() {
        return requestorPersonEmailAddress;
    }

    @Override
    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
    }

    @Override
    public String getRequestorPersonName() {
        return requestorPersonName;
    }

    @Override
    public void setRequestorPersonName(String requestorPersonName) {
        this.requestorPersonName = requestorPersonName;
    }

    @Override
    public String getRequestorPersonPhoneNumber() {
        return requestorPersonPhoneNumber;
    }

    @Override
    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
    }

    @Override
    public String getRequisitionSourceCode() {
        return requisitionSourceCode;
    }

    @Override
    public void setRequisitionSourceCode(String requisitionSourceCode) {
        this.requisitionSourceCode = requisitionSourceCode;
    }

    public String getVendorContactsLabel() {
        return vendorContactsLabel;
    }

    public void setVendorContactsLabel(String vendorContactsLabel) {
        this.vendorContactsLabel = vendorContactsLabel;
    }

    public VendorContract getVendorContract() {
        if (ObjectUtils.isNull(vendorContract)) {
            refreshReferenceObject(PurapPropertyConstants.VENDOR_CONTRACT);
        }
        return vendorContract;
    }

    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    @Override
    public Integer getVendorContractGeneratedIdentifier() {
        return vendorContractGeneratedIdentifier;
    }

    @Override
    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
    }

    public String getVendorContractName() {
        getVendorContract();
        if (ObjectUtils.isNull(vendorContract)) {
            return "";
        }
        else {
            return vendorContract.getVendorContractName();
        }
    }

    @Override
    public String getVendorFaxNumber() {
        return vendorFaxNumber;
    }

    @Override
    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    @Override
    public String getVendorNoteText() {
        return vendorNoteText;
    }

    @Override
    public void setVendorNoteText(String vendorNoteText) {
        this.vendorNoteText = vendorNoteText;
    }

    @Override
    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    @Override
    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    @Override
    public String getVendorPhoneNumber() {
        return vendorPhoneNumber;
    }

    @Override
    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    @Override
    public Boolean getVendorRestrictedIndicator() {
        return vendorRestrictedIndicator;
    }

    @Override
    public void setVendorRestrictedIndicator(Boolean vendorRestrictedIndicator) {
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
    }

    @Override
    public String getVendorShippingPaymentTermsCode() {
        return vendorShippingPaymentTermsCode;
    }

    @Override
    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    @Override
    public String getVendorShippingTitleCode() {
        return vendorShippingTitleCode;
    }

    @Override
    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    @Override
    public CampusParameter getDeliveryCampus() {
        return deliveryCampus;
    }

    @Override
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    @Override
    public FundingSource getFundingSource() {
        return fundingSource;
    }

    @Override
    public Account getNonInstitutionFundAccount() {
        return nonInstitutionFundAccount;
    }

    @Override
    public Chart getNonInstitutionFundChartOfAccounts() {
        return nonInstitutionFundChartOfAccounts;
    }

    @Override
    public Organization getNonInstitutionFundOrganization() {
        return nonInstitutionFundOrganization;
    }

    @Override
    public Chart getNonInstitutionFundOrgChartOfAccounts() {
        return nonInstitutionFundOrgChartOfAccounts;
    }

    @Override
    public Organization getOrganization() {
        return organization;
    }

    @Override
    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod() {
        return purchaseOrderTransmissionMethod;
    }

    @Override
    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    @Override
    public RequisitionSource getRequisitionSource() {
        return requisitionSource;
    }

    public String getSupplierDiversityLabel() {
        return supplierDiversityLabel;
    }

    @Override
    public PurchaseOrderCostSource getPurchaseOrderCostSource() {
        if (ObjectUtils.isNull(purchaseOrderCostSource)) {
            refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_COST_SOURCE);
        }
        return purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setDeliveryCampus(CampusParameter deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount) {
        this.nonInstitutionFundAccount = nonInstitutionFundAccount;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts) {
        this.nonInstitutionFundChartOfAccounts = nonInstitutionFundChartOfAccounts;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setNonInstitutionFundOrganization(Organization nonInstitutionFundOrganization) {
        this.nonInstitutionFundOrganization = nonInstitutionFundOrganization;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts) {
        this.nonInstitutionFundOrgChartOfAccounts = nonInstitutionFundOrgChartOfAccounts;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource) {
        this.purchaseOrderCostSource = purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod) {
        this.purchaseOrderTransmissionMethod = purchaseOrderTransmissionMethod;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setRequisitionSource(RequisitionSource requisitionSource) {
        this.requisitionSource = requisitionSource;
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute.
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    @Override
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }

    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     * @param receivingDocumentRequiredIndicator The receivingDocumentRequiredIndicator to set.
     */
    @Override
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        // if receivingDocumentRequiredIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnableReceivingDocumentRequiredIndicator()) {
            receivingDocumentRequiredIndicator = false;
        }
        else {
            this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
        }
    }

    @Override
    public boolean isPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    @Override
    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator) {
        // if paymentRequestPositiveApprovalIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnablePaymentRequestPositiveApprovalIndicator()) {
            paymentRequestPositiveApprovalIndicator = false;
        }
        else {
            this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
        }
    }

    public List<CommodityCode> getCommodityCodesForRouting() {
        return commodityCodesForRouting;
    }

    public void setCommodityCodesForRouting(List<CommodityCode> commodityCodesForRouting) {
        this.commodityCodesForRouting = commodityCodesForRouting;
    }

    @Override
    public String getCapitalAssetSystemTypeCode() {
        return capitalAssetSystemTypeCode;
    }

    @Override
    public void setCapitalAssetSystemTypeCode(String capitalAssetSystemTypeCode) {
        this.capitalAssetSystemTypeCode = capitalAssetSystemTypeCode;
    }

    @Override
    public String getCapitalAssetSystemStateCode() {
        return capitalAssetSystemStateCode;
    }

    @Override
    public void setCapitalAssetSystemStateCode(String capitalAssetSystemStateCode) {
        this.capitalAssetSystemStateCode = capitalAssetSystemStateCode;
    }

    /**
     * Gets the justification attribute.
     * @return Returns the justification.
     */
    public String getJustification() {
        return justification;
    }

    /**
     * Sets the justification attribute value.
     * @param justification The justification to set.
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }

    @Override
    public CapitalAssetSystemType getCapitalAssetSystemType() {
        if(ObjectUtils.isNull(capitalAssetSystemType)){
            this.refreshReferenceObject("capitalAssetSystemType");
        }
        return capitalAssetSystemType;
    }

    @Override
    public void setCapitalAssetSystemType(CapitalAssetSystemType capitalAssetSystemType) {
        this.capitalAssetSystemType = capitalAssetSystemType;
    }

    @Override
    public CapitalAssetSystemState getCapitalAssetSystemState() {
        if(ObjectUtils.isNull(capitalAssetSystemState)){
            this.refreshReferenceObject("capitalAssetSystemState");
        }
        return capitalAssetSystemState;
    }

    @Override
    public void setCapitalAssetSystemState(CapitalAssetSystemState capitalAssetSystemState) {
        this.capitalAssetSystemState = capitalAssetSystemState;
    }

    @Override
    public List<CapitalAssetSystem> getPurchasingCapitalAssetSystems() {
        return purchasingCapitalAssetSystems;
    }

    @Override
    public void setPurchasingCapitalAssetSystems(List<CapitalAssetSystem> purchasingCapitalAssetSystems) {
        this.purchasingCapitalAssetSystems = purchasingCapitalAssetSystems;
    }

    @Override
    public List<PurchasingCapitalAssetItem> getPurchasingCapitalAssetItems() {
        return purchasingCapitalAssetItems;
    }

    @Override
    public void setPurchasingCapitalAssetItems(List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems) {
        this.purchasingCapitalAssetItems = purchasingCapitalAssetItems;
    }

    @Override
    public abstract Class getPurchasingCapitalAssetItemClass();

    @Override
    public abstract Class getPurchasingCapitalAssetSystemClass();

    @Override
    public PurchasingItem getPurchasingItem(Integer itemIdentifier){

        if(ObjectUtils.isNull(itemIdentifier)) {
            return null;
        }

        PurchasingItem item = null;

        for(PurchasingItem pi: (List<PurchasingItem>)this.getItems()){
            if(itemIdentifier.equals(pi.getItemIdentifier())){
                item = pi;
                break;
            }
        }

        return item;
    }

    @Override
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItem(Integer itemIdentifier){

        if(ObjectUtils.isNull(itemIdentifier)) {
            return null;
        }

        PurchasingCapitalAssetItem item = null;

        for(PurchasingCapitalAssetItem pcai: this.getPurchasingCapitalAssetItems()){
            if(itemIdentifier.equals(pcai.getItemIdentifier())){
                item = pcai;
                break;
            }
        }

        return item;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = new ArrayList<List>();
        managedLists.add(getDeletionAwareAccountingLines());
        if (allowDeleteAwareCollection) {
            List<ItemCapitalAsset> assetLists = new ArrayList<ItemCapitalAsset>();
            List<CapitalAssetLocation>capitalAssetLocationLists = new ArrayList<CapitalAssetLocation>();
            if (StringUtils.equals(this.getCapitalAssetSystemTypeCode(),PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (PurchasingCapitalAssetItem capitalAssetItem : this.getPurchasingCapitalAssetItems()) {
                    //We only need to add the itemCapitalAssets to assetLists if the system is not null, otherwise
                    //just let the assetLists be empty ArrayList.
                    if (capitalAssetItem.getPurchasingCapitalAssetSystem() != null) {
                        assetLists.addAll(capitalAssetItem.getPurchasingCapitalAssetSystem().getItemCapitalAssets());
                        capitalAssetLocationLists.addAll(capitalAssetItem.getPurchasingCapitalAssetSystem().getCapitalAssetLocations());
                    }
                }
            }
            else {
                for (CapitalAssetSystem system : this.getPurchasingCapitalAssetSystems()) {
                    assetLists.addAll(system.getItemCapitalAssets());
                    capitalAssetLocationLists.addAll(system.getCapitalAssetLocations());
                }
            }
            managedLists.add(assetLists);
            managedLists.add(capitalAssetLocationLists);
            managedLists.add(this.getPurchasingCapitalAssetSystems());
            managedLists.add(this.getPurchasingCapitalAssetItems());
            managedLists.add(this.getItems());
        }
        return managedLists;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to remove the
     * purchasingCapitalAssetSystem when the system type is either ONE or MULT.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        if (StringUtils.isNotBlank(this.getCapitalAssetSystemTypeCode())) {
            if (this.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM) || this.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.MULTIPLE)) {
                //If the system state is ONE or MULT, we have to remove all the systems on the items because it's not applicable.
                for (PurchasingCapitalAssetItem camsItem : this.getPurchasingCapitalAssetItems()) {
                    camsItem.setPurchasingCapitalAssetSystem(null);
                }
            }
        }
        if (event instanceof RouteDocumentEvent || event instanceof ApproveDocumentEvent) {

            boolean defaultUseTaxIndicatorValue = SpringContext.getBean(PurchasingService.class).getDefaultUseTaxIndicatorValue(this);
            SpringContext.getBean(PurapService.class).updateUseTaxIndicator(this, defaultUseTaxIndicatorValue);
        }
    }

    @Override
    public Date getTransactionTaxDate() {
        return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
    }

    @Override
    public void clearCapitalAssetFields() {
        this.getPurchasingCapitalAssetItems().clear();
        this.getPurchasingCapitalAssetSystems().clear();
        this.setCapitalAssetSystemStateCode(null);
        this.setCapitalAssetSystemTypeCode(null);
        this.setCapitalAssetSystemState(null);
        this.setCapitalAssetSystemType(null);

    }

    /**
     * @return the payment request positive approval indicator
     */
    public boolean getPaymentRequestPositiveApprovalIndicatorForSearching() {
        return paymentRequestPositiveApprovalIndicator;
    }

    /**
     * @return the receiving document required indicator
     */
    public boolean getReceivingDocumentRequiredIndicatorForSearching() {
        return receivingDocumentRequiredIndicator;
    }

    public String getDocumentChartOfAccountsCodeForSearching(){
        return chartOfAccountsCode;
    }

    public String getDocumentOrganizationCodeForSearching(){
        return organizationCode;
    }

    @Override
    public boolean shouldGiveErrorForEmptyAccountsProration() {
        return true;
    }

    public String getChartAndOrgCodeForResult(){
        return getChartOfAccountsCode() + "-" + getOrganizationCode();
    }

    public String getDeliveryCampusCodeForSearch(){
        return getDeliveryCampusCode();
    }

    public boolean getHasB2BVendor() {
        if (getVendorHeaderGeneratedIdentifier() != null) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_DETAIL);
            String campusCode = GlobalVariables.getUserSession().getPerson().getCampusCode();
            VendorDetail vendorDetail = getVendorDetail();
            if (vendorDetail == null || StringUtils.isEmpty(campusCode))
             {
                return false; // this should never happen
            }
            return SpringContext.getBean(VendorService.class).getVendorB2BContract(vendorDetail, campusCode) != null;
        }
        return false;
    }
}
