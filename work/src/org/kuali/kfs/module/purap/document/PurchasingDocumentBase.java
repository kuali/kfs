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
package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Campus;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.FundingSource;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod;
import org.kuali.module.purap.bo.ReceivingAddress;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionSource;
import org.kuali.module.purap.util.ItemParser;
import org.kuali.module.purap.util.ItemParserBase;
import org.kuali.module.vendor.bo.PurchaseOrderCostSource;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;

/**
 * Base class for Purchasing Documents.
 */
public abstract class PurchasingDocumentBase extends PurchasingAccountsPayableDocumentBase implements PurchasingDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION AND PURCHASE ORDER
    private String fundingSourceCode;
    private String requisitionSourceCode;
    private String purchaseOrderTransmissionMethodCode;
    private String purchaseOrderCostSourceCode;
    private String deliveryRequiredDateReasonCode;
    private String recurringPaymentTypeCode;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String deliveryCampusCode;
    private KualiDecimal purchaseOrderTotalLimit;
    private Boolean vendorRestrictedIndicator;
    private String vendorPhoneNumber;
    private String vendorFaxNumber;
    private Integer vendorContractGeneratedIdentifier;
    private String vendorNoteText;
    private String requestorPersonName;
    private String requestorPersonEmailAddress;
    private String requestorPersonPhoneNumber;
    private String nonInstitutionFundOrgChartOfAccountsCode;
    private String nonInstitutionFundOrganizationCode;
    private String nonInstitutionFundChartOfAccountsCode;
    private String nonInstitutionFundAccountNumber;
    private String deliveryBuildingCode;
    private String deliveryBuildingName;
    private String deliveryBuildingRoomNumber;
    private String deliveryBuildingLine1Address;
    private String deliveryBuildingLine2Address;
    private String deliveryCityName;
    private String deliveryStateCode;
    private String deliveryPostalCode;
    private String deliveryCountryCode;
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    private Date deliveryRequiredDate;
    private String deliveryInstructionText;
    private Date purchaseOrderBeginDate;
    private Date purchaseOrderEndDate;
    private String institutionContactName;
    private String institutionContactPhoneNumber;
    private String institutionContactEmailAddress;
    private String billingName;
    private String billingLine1Address;
    private String billingLine2Address;
    private String billingCityName;
    private String billingStateCode;
    private String billingPostalCode;
    private String billingCountryCode;
    private String billingPhoneNumber;
    private String receivingName;
    private String receivingLine1Address;
    private String receivingLine2Address;
    private String receivingCityName;
    private String receivingStateCode;
    private String receivingPostalCode;
    private String receivingCountryCode;
    private boolean addressToVendorIndicator; // if true, use receiving address
    private String externalOrganizationB2bSupplierIdentifier;
    private boolean purchaseOrderAutomaticIndicator;
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;

    // NOT PERSISTED IN DB
    private String vendorContractName;
    private String supplierDiversityLabel;
    private String vendorContactsLabel;
    private boolean deliveryBuildingOther;

    // REFERENCE OBJECTS
    private FundingSource fundingSource;
    private RequisitionSource requisitionSource;
    private PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod;
    private PurchaseOrderCostSource purchaseOrderCostSource;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
    private RecurringPaymentType recurringPaymentType;
    private Org organization;
    private Chart chartOfAccounts;
    private Campus deliveryCampus;
    private Chart nonInstitutionFundOrgChartOfAccounts;
    private Org nonInstitutionFundOrganization;
    private Account nonInstitutionFundAccount;
    private Chart nonInstitutionFundChartOfAccounts;
    private VendorContract vendorContract;
    
    private boolean receivingDocumentRequiredIndicator;
    private boolean paymentRequestPositiveApprovalIndicator;

    /**
     * Default Constructor.
     */
    public PurchasingDocumentBase() {
        super();
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocument#templateVendorDetail(org.kuali.module.vendor.bo.VendorDetail)
     */
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }

        this.setVendorDetail(vendorDetail);
        this.setVendorName(vendorDetail.getVendorName());
        this.setVendorShippingTitleCode(vendorDetail.getVendorShippingTitleCode());
        this.setVendorPaymentTermsCode(vendorDetail.getVendorPaymentTermsCode());
        this.setVendorShippingPaymentTermsCode(vendorDetail.getVendorShippingPaymentTermsCode());
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocument#templateVendorContract(org.kuali.module.vendor.bo.VendorContract)
     */
    public void templateVendorContract(VendorContract vendorContract) {
        if (vendorContract == null) {
            return;
        }
        this.setVendorContract(vendorContract);
        this.setVendorContractName(vendorContract.getVendorContractName());
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#templateVendorAddress(org.kuali.module.vendor.bo.VendorAddress)
     */
    public void templateVendorAddress(VendorAddress vendorAddress) {
        super.templateVendorAddress(vendorAddress);
        this.setVendorFaxNumber(vendorAddress.getVendorFaxNumber());
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocumentBase#templateBillingAddress(org.kuali.module.purap.bo.BillingAddress).
     */
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
        }
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingDocumentBase#templateReceivingAddress(org.kuali.module.purap.bo.ReceivingAddress).
     */
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
    public void loadReceivingAddress() {
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
        //TODO what if more than one is found? throw an exception
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#addItem(org.kuali.module.purap.bo.PurApItem)
     */
    @Override
    public void addItem(PurApItem item) {
        item.refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
        super.addItem(item);
    }
    
    
    // GETTERS AND SETTERS

    /**
     * @see org.kuali.module.purap.document.PurchasingDocument.getItemParser().
     */
    public ItemParser getItemParser() {
        return new ItemParserBase();
    }
    
    public String getBillingCityName() {
        return billingCityName;
    }

    public void setBillingCityName(String billingCityName) {
        this.billingCityName = billingCityName;
    }

    public String getBillingCountryCode() {
        return billingCountryCode;
    }

    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    public String getBillingLine1Address() {
        return billingLine1Address;
    }

    public void setBillingLine1Address(String billingLine1Address) {
        this.billingLine1Address = billingLine1Address;
    }

    public String getBillingLine2Address() {
        return billingLine2Address;
    }

    public void setBillingLine2Address(String billingLine2Address) {
        this.billingLine2Address = billingLine2Address;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingPhoneNumber() {
        return billingPhoneNumber;
    }

    public void setBillingPhoneNumber(String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    public String getBillingStateCode() {
        return billingStateCode;
    }

    public void setBillingStateCode(String billingStateCode) {
        this.billingStateCode = billingStateCode;
    }

    public String getReceivingCityName() {
        return receivingCityName;
    }

    public void setReceivingCityName(String receivingCityName) {
        this.receivingCityName = receivingCityName;
    }

    public String getReceivingCountryCode() {
        return receivingCountryCode;
    }

    public void setReceivingCountryCode(String receivingCountryCode) {
        this.receivingCountryCode = receivingCountryCode;
    }

    public String getReceivingLine1Address() {
        return receivingLine1Address;
    }

    public void setReceivingLine1Address(String receivingLine1Address) {
        this.receivingLine1Address = receivingLine1Address;
    }

    public String getReceivingLine2Address() {
        return receivingLine2Address;
    }

    public void setReceivingLine2Address(String receivingLine2Address) {
        this.receivingLine2Address = receivingLine2Address;
    }

    public String getReceivingName() {
        return receivingName;
    }

    public void setReceivingName(String receivingName) {
        this.receivingName = receivingName;
    }

    public String getReceivingPostalCode() {
        return receivingPostalCode;
    }

    public void setReceivingPostalCode(String receivingPostalCode) {
        this.receivingPostalCode = receivingPostalCode;
    }

    public String getReceivingStateCode() {
        return receivingStateCode;
    }

    public void setReceivingStateCode(String receivingStateCode) {
        this.receivingStateCode = receivingStateCode;
    }

    public boolean getAddressToVendorIndicator() {
        return addressToVendorIndicator;
    }

    public void setAddressToVendorIndicator(boolean addressToVendor) {
        this.addressToVendorIndicator = addressToVendor;
    }
    
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getDeliveryBuildingCode() {
        return deliveryBuildingCode;
    }

    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    public String getDeliveryBuildingLine1Address() {
        return deliveryBuildingLine1Address;
    }

    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    public String getDeliveryBuildingLine2Address() {
        return deliveryBuildingLine2Address;
    }

    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    public String getDeliveryBuildingName() {
        return deliveryBuildingName;
    }

    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    public boolean isDeliveryBuildingOther() {
        return deliveryBuildingOther;
    }

    public void setDeliveryBuildingOther(boolean deliveryBuildingOther) {
        this.deliveryBuildingOther = deliveryBuildingOther;
    }

    public String getDeliveryBuildingRoomNumber() {
        return deliveryBuildingRoomNumber;
    }

    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getDeliveryCityName() {
        return deliveryCityName;
    }

    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    public String getDeliveryCountryCode() {
        return deliveryCountryCode;
    }

    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    public String getDeliveryInstructionText() {
        return deliveryInstructionText;
    }

    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public Date getDeliveryRequiredDate() {
        return deliveryRequiredDate;
    }

    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    public String getDeliveryStateCode() {
        return deliveryStateCode;
    }

    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    public String getDeliveryToEmailAddress() {
        return deliveryToEmailAddress;
    }

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    public String getDeliveryToName() {
        return deliveryToName;
    }

    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    public String getDeliveryToPhoneNumber() {
        return deliveryToPhoneNumber;
    }

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    public String getExternalOrganizationB2bSupplierIdentifier() {
        return externalOrganizationB2bSupplierIdentifier;
    }

    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier) {
        this.externalOrganizationB2bSupplierIdentifier = externalOrganizationB2bSupplierIdentifier;
    }

    public String getFundingSourceCode() {
        return fundingSourceCode;
    }

    public void setFundingSourceCode(String fundingSourceCode) {
        this.fundingSourceCode = fundingSourceCode;
    }

    public String getInstitutionContactEmailAddress() {
        return institutionContactEmailAddress;
    }

    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress) {
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }

    public String getInstitutionContactName() {
        return institutionContactName;
    }

    public void setInstitutionContactName(String institutionContactName) {
        this.institutionContactName = institutionContactName;
    }

    public String getInstitutionContactPhoneNumber() {
        return institutionContactPhoneNumber;
    }

    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber) {
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
    }

    public String getNonInstitutionFundAccountNumber() {
        return nonInstitutionFundAccountNumber;
    }

    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber) {
        this.nonInstitutionFundAccountNumber = nonInstitutionFundAccountNumber;
    }

    public String getNonInstitutionFundChartOfAccountsCode() {
        return nonInstitutionFundChartOfAccountsCode;
    }

    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode) {
        this.nonInstitutionFundChartOfAccountsCode = nonInstitutionFundChartOfAccountsCode;
    }

    public String getNonInstitutionFundOrganizationCode() {
        return nonInstitutionFundOrganizationCode;
    }

    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode) {
        this.nonInstitutionFundOrganizationCode = nonInstitutionFundOrganizationCode;
    }

    public String getNonInstitutionFundOrgChartOfAccountsCode() {
        return nonInstitutionFundOrgChartOfAccountsCode;
    }

    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode) {
        this.nonInstitutionFundOrgChartOfAccountsCode = nonInstitutionFundOrgChartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public boolean getPurchaseOrderAutomaticIndicator() {
        return purchaseOrderAutomaticIndicator;
    }

    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator) {
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
    }

    public Date getPurchaseOrderBeginDate() {
        return purchaseOrderBeginDate;
    }

    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate) {
        this.purchaseOrderBeginDate = purchaseOrderBeginDate;
    }

    public String getPurchaseOrderCostSourceCode() {
        return purchaseOrderCostSourceCode;
    }

    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    public KualiDecimal getPurchaseOrderTotalLimit() {
        return purchaseOrderTotalLimit;
    }

    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit) {
        this.purchaseOrderTotalLimit = purchaseOrderTotalLimit;
    }

    public String getPurchaseOrderTransmissionMethodCode() {
        return purchaseOrderTransmissionMethodCode;
    }

    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode) {
        this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    public String getRequestorPersonEmailAddress() {
        return requestorPersonEmailAddress;
    }

    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
    }

    public String getRequestorPersonName() {
        return requestorPersonName;
    }

    public void setRequestorPersonName(String requestorPersonName) {
        this.requestorPersonName = requestorPersonName;
    }

    public String getRequestorPersonPhoneNumber() {
        return requestorPersonPhoneNumber;
    }

    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
    }

    public String getRequisitionSourceCode() {
        return requisitionSourceCode;
    }

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
        return vendorContract;
    }

    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    public Integer getVendorContractGeneratedIdentifier() {
        return vendorContractGeneratedIdentifier;
    }

    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
    }

    public String getVendorContractName() {
        return vendorContractName;
    }

    public void setVendorContractName(String vendorContractName) {
        this.vendorContractName = vendorContractName;
    }

    public String getVendorFaxNumber() {
        return vendorFaxNumber;
    }

    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    public String getVendorNoteText() {
        return vendorNoteText;
    }

    public void setVendorNoteText(String vendorNoteText) {
        this.vendorNoteText = vendorNoteText;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorPhoneNumber() {
        return vendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    public Boolean getVendorRestrictedIndicator() {
        return vendorRestrictedIndicator;
    }

    public void setVendorRestrictedIndicator(Boolean vendorRestrictedIndicator) {
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
    }

    public String getVendorShippingPaymentTermsCode() {
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public Campus getDeliveryCampus() {
        return deliveryCampus;
    }

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    public Account getNonInstitutionFundAccount() {
        return nonInstitutionFundAccount;
    }

    public Chart getNonInstitutionFundChartOfAccounts() {
        return nonInstitutionFundChartOfAccounts;
    }

    public Org getNonInstitutionFundOrganization() {
        return nonInstitutionFundOrganization;
    }

    public Chart getNonInstitutionFundOrgChartOfAccounts() {
        return nonInstitutionFundOrgChartOfAccounts;
    }

    public Org getOrganization() {
        return organization;
    }

    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod() {
        return purchaseOrderTransmissionMethod;
    }

    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    public RequisitionSource getRequisitionSource() {
        return requisitionSource;
    }

    public String getSupplierDiversityLabel() {
        return supplierDiversityLabel;
    }

    public PurchaseOrderCostSource getPurchaseOrderCostSource() {
        if (ObjectUtils.isNull(purchaseOrderCostSource))
            refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_COST_SOURCE);
        return purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    /**
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * @deprecated
     */
    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount) {
        this.nonInstitutionFundAccount = nonInstitutionFundAccount;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts) {
        this.nonInstitutionFundChartOfAccounts = nonInstitutionFundChartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Org nonInstitutionFundOrganization) {
        this.nonInstitutionFundOrganization = nonInstitutionFundOrganization;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts) {
        this.nonInstitutionFundOrgChartOfAccounts = nonInstitutionFundOrgChartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource) {
        this.purchaseOrderCostSource = purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod) {
        this.purchaseOrderTransmissionMethod = purchaseOrderTransmissionMethod;
    }

    /**
     * @deprecated
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    /**
     * @deprecated
     */
    public void setRequisitionSource(RequisitionSource requisitionSource) {
        this.requisitionSource = requisitionSource;
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute. 
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }
    
    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     * @param receivingDocumentRequiredIndicator The receivingDocumentRequiredIndicator to set.
     */
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
    }

    public boolean isPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator) {
        this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
    }
}
