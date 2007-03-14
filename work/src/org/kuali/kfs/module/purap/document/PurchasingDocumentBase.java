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
import java.util.List;

import org.kuali.core.bo.Campus;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.FundingSource;
import org.kuali.module.purap.bo.PurApItemBase;
import org.kuali.module.purap.bo.PurchaseOrderCostSource;
import org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.bo.RequisitionSource;
import org.kuali.module.purap.bo.VendorAddress;
import org.kuali.module.purap.bo.VendorContract;
import org.kuali.module.purap.bo.VendorDetail;

/**
 * Purchasing Document Base
 * 
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
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
    private boolean vendorRestrictedIndicator;
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
    private String externalOrganizationB2bSupplierIdentifier;
    private Integer contractManagerCode;
    private boolean purchaseOrderAutomaticIndicator;

    // NOT PERSISTED IN DB
    private String vendorNumber; 
    private Integer vendorAddressGeneratedIdentifier;
    private String vendorContractName;
    private String supplierDiversityLabel;
    private String vendorContactsLabel;
    private String contractManagerName;
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
    
    
    

    public PurchasingDocumentBase() {
        super();
    }

    
    /**
     * Retrieve all references common to purchasing
     */
    public void refreshAllReferences() {
        super.refreshAllReferences();
        this.refreshReferenceObject("fundingSource");
        this.refreshReferenceObject("requisitionSource");
        this.refreshReferenceObject("purchaseOrderCostSource");
        this.refreshReferenceObject("purchaseOrderTransmissionMethod");
        this.refreshReferenceObject("chartOfAccounts");
        this.refreshReferenceObject("organization");
        this.refreshReferenceObject("deliveryCampus");
        this.refreshReferenceObject("vendorContract");
    }

    /**
     * Convenience method to set vendor detail fields based on a given VendorDetail.
     * 
     * @param vendorDetail
     */
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }
    
        this.setVendorDetail(vendorDetail);
        this.setVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + PurapConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setVendorName(vendorDetail.getVendorName());
    }
    
    /**
     * Convenience method to set vendor contract fields based on a given VendorContract.
     * 
     * @param vendorContract
     */
    public void templateVendorContract(VendorContract vendorContract) {
        if (vendorContract == null) {
            return;
        }

        this.setVendorContract(vendorContract);
        this.setVendorContractName(vendorContract.getVendorContractName());
    }
    
    /**
     * Convenience method to set vendor address fields based on a given VendorAddress.
     * 
     * @param vendorAddress
     */
    public void templateVendorAddress(VendorAddress vendorAddress) {
        if (vendorAddress == null) {
            return;
        }
        this.setVendorLine1Address(vendorAddress.getVendorLine1Address());
        this.setVendorLine2Address(vendorAddress.getVendorLine2Address());
        this.setVendorCityName(vendorAddress.getVendorCityName());
        this.setVendorStateCode(vendorAddress.getVendorStateCode());
        this.setVendorPostalCode(vendorAddress.getVendorZipCode());
        this.setVendorCountryCode(vendorAddress.getVendorCountryCode());
        this.setVendorFaxNumber(vendorAddress.getVendorFaxNumber());
    }    
    
    /**
     * Convenience method to set billing address fields based on a given BillingAddress.
     * 
     * @param billingAddress
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
    
    // GETTERS AND SETTERS    
    /**
     * Gets the fundingSourceCode attribute.
     * 
     * @return Returns the fundingSourceCode
     * 
     */
    public String getFundingSourceCode() { 
        return fundingSourceCode;
    }

    /**
     * Sets the fundingSourceCode attribute.
     * 
     * @param fundingSourceCode The fundingSourceCode to set.
     * 
     */
    public void setFundingSourceCode(String fundingSourceCode) {
        this.fundingSourceCode = fundingSourceCode;
    }

    /**
     * Gets the requisitionSourceCode attribute.
     * 
     * @return Returns the requisitionSourceCode
     * 
     */
    public String getRequisitionSourceCode() { 
        return requisitionSourceCode;
    }

    /**
     * Sets the requisitionSourceCode attribute.
     * 
     * @param requisitionSourceCode The requisitionSourceCode to set.
     * 
     */
    public void setRequisitionSourceCode(String requisitionSourceCode) {
        this.requisitionSourceCode = requisitionSourceCode;
    }

    /**
     * Gets the purchaseOrderTransmissionMethodCode attribute.
     * 
     * @return Returns the purchaseOrderTransmissionMethodCode
     * 
     */
    public String getPurchaseOrderTransmissionMethodCode() { 
        return purchaseOrderTransmissionMethodCode;
    }

    /**
     * Sets the purchaseOrderTransmissionMethodCode attribute.
     * 
     * @param purchaseOrderTransmissionMethodCode The purchaseOrderTransmissionMethodCode to set.
     * 
     */
    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode) {
        this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
    }

    /**
     * Gets the purchaseOrderCostSourceCode attribute.
     * 
     * @return Returns the purchaseOrderCostSourceCode
     * 
     */
    public String getPurchaseOrderCostSourceCode() { 
        return purchaseOrderCostSourceCode;
    }

    /**
     * Sets the purchaseOrderCostSourceCode attribute.
     * 
     * @param purchaseOrderCostSourceCode The purchaseOrderCostSourceCode to set.
     * 
     */
    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    /**
     * Gets the deliveryRequiredDateReasonCode attribute.
     * 
     * @return Returns the deliveryRequiredDateReasonCode
     * 
     */
    public String getDeliveryRequiredDateReasonCode() { 
        return deliveryRequiredDateReasonCode;
    }

    /**
     * Sets the deliveryRequiredDateReasonCode attribute.
     * 
     * @param deliveryRequiredDateReasonCode The deliveryRequiredDateReasonCode to set.
     * 
     */
    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    /**
     * Gets the recurringPaymentTypeCode attribute.
     * 
     * @return Returns the recurringPaymentTypeCode
     * 
     */
    public String getRecurringPaymentTypeCode() { 
        return recurringPaymentTypeCode;
    }

    /**
     * Sets the recurringPaymentTypeCode attribute.
     * 
     * @param recurringPaymentTypeCode The recurringPaymentTypeCode to set.
     * 
     */
    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() { 
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() { 
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the deliveryCampusCode attribute.
     * 
     * @return Returns the deliveryCampusCode
     * 
     */
    public String getDeliveryCampusCode() { 
        return deliveryCampusCode;
    }

    /**
     * Sets the deliveryCampusCode attribute.
     * 
     * @param deliveryCampusCode The deliveryCampusCode to set.
     * 
     */
    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    /**
     * Gets the purchaseOrderTotalLimit attribute.
     * 
     * @return Returns the purchaseOrderTotalLimit
     * 
     */
    public KualiDecimal getPurchaseOrderTotalLimit() { 
        return purchaseOrderTotalLimit;
    }

    /**
     * Sets the purchaseOrderTotalLimit attribute.
     * 
     * @param purchaseOrderTotalLimit The purchaseOrderTotalLimit to set.
     * 
     */
    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit) {
        this.purchaseOrderTotalLimit = purchaseOrderTotalLimit;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     * 
     */
    public String getVendorName() { 
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     * 
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address() { 
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address() { 
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     * 
     */
    public String getVendorCityName() { 
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode() { 
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    /**
     * Gets the vendorPostalCode attribute.
     * 
     * @return Returns the vendorPostalCode
     * 
     */
    public String getVendorPostalCode() { 
        return vendorPostalCode;
    }

    /**
     * Sets the vendorPostalCode attribute.
     * 
     * @param vendorPostalCode The vendorPostalCode to set.
     * 
     */
    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    /**
     * Gets the vendorCountryCode attribute.
     * 
     * @return Returns the vendorCountryCode
     * 
     */
    public String getVendorCountryCode() { 
        return vendorCountryCode;
    }

    /**
     * Sets the vendorCountryCode attribute.
     * 
     * @param vendorCountryCode The vendorCountryCode to set.
     * 
     */
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    /**
     * Gets the vendorRestrictedIndicator attribute.
     * 
     * @return Returns the vendorRestrictedIndicator
     * 
     */
    public boolean getVendorRestrictedIndicator() { 
        return vendorRestrictedIndicator;
    }

    /**
     * Sets the vendorRestrictedIndicator attribute.
     * 
     * @param vendorRestrictedIndicator The vendorRestrictedIndicator to set.
     * 
     */
    public void setVendorRestrictedIndicator(boolean vendorRestrictedIndicator) {
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
    }

    /**
     * Gets the vendorPhoneNumber attribute.
     * 
     * @return Returns the vendorPhoneNumber
     * 
     */
    public String getVendorPhoneNumber() { 
        return vendorPhoneNumber;
    }

    /**
     * Sets the vendorPhoneNumber attribute.
     * 
     * @param vendorPhoneNumber The vendorPhoneNumber to set.
     * 
     */
    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    /**
     * Gets the vendorFaxNumber attribute.
     * 
     * @return Returns the vendorFaxNumber
     * 
     */
    public String getVendorFaxNumber() { 
        return vendorFaxNumber;
    }

    /**
     * Sets the vendorFaxNumber attribute.
     * 
     * @param vendorFaxNumber The vendorFaxNumber to set.
     * 
     */
    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    /**
     * Gets the vendorContractGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorContractGeneratedIdentifier
     * 
     */
    public Integer getVendorContractGeneratedIdentifier() { 
        return vendorContractGeneratedIdentifier;
    }

    /**
     * Sets the vendorContractGeneratedIdentifier attribute.
     * 
     * @param vendorContractGeneratedIdentifier The vendorContractGeneratedIdentifier to set.
     * 
     */
    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
    }

    /**
     * Gets the vendorNoteText attribute.
     * 
     * @return Returns the vendorNoteText
     * 
     */
    public String getVendorNoteText() { 
        return vendorNoteText;
    }

    /**
     * Sets the vendorNoteText attribute.
     * 
     * @param vendorNoteText The vendorNoteText to set.
     * 
     */
    public void setVendorNoteText(String vendorNoteText) {
        this.vendorNoteText = vendorNoteText;
    }

    /**
     * Gets the requestorPersonName attribute.
     * 
     * @return Returns the requestorPersonName
     * 
     */
    public String getRequestorPersonName() { 
        return requestorPersonName;
    }

    /**
     * Sets the requestorPersonName attribute.
     * 
     * @param requestorPersonName The requestorPersonName to set.
     * 
     */
    public void setRequestorPersonName(String requestorPersonName) {
        this.requestorPersonName = requestorPersonName;
    }

    /**
     * Gets the requestorPersonEmailAddress attribute.
     * 
     * @return Returns the requestorPersonEmailAddress
     * 
     */
    public String getRequestorPersonEmailAddress() { 
        return requestorPersonEmailAddress;
    }

    /**
     * Sets the requestorPersonEmailAddress attribute.
     * 
     * @param requestorPersonEmailAddress The requestorPersonEmailAddress to set.
     * 
     */
    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
    }

    /**
     * Gets the requestorPersonPhoneNumber attribute.
     * 
     * @return Returns the requestorPersonPhoneNumber
     * 
     */
    public String getRequestorPersonPhoneNumber() { 
        return requestorPersonPhoneNumber;
    }

    /**
     * Sets the requestorPersonPhoneNumber attribute.
     * 
     * @param requestorPersonPhoneNumber The requestorPersonPhoneNumber to set.
     * 
     */
    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
    }

    /**
     * Gets the nonInstitutionFundOrgChartOfAccountsCode attribute.
     * 
     * @return Returns the nonInstitutionFundOrgChartOfAccountsCode
     * 
     */
    public String getNonInstitutionFundOrgChartOfAccountsCode() { 
        return nonInstitutionFundOrgChartOfAccountsCode;
    }

    /**
     * Sets the nonInstitutionFundOrgChartOfAccountsCode attribute.
     * 
     * @param nonInstitutionFundOrgChartOfAccountsCode The nonInstitutionFundOrgChartOfAccountsCode to set.
     * 
     */
    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode) {
        this.nonInstitutionFundOrgChartOfAccountsCode = nonInstitutionFundOrgChartOfAccountsCode;
    }

    /**
     * Gets the nonInstitutionFundOrganizationCode attribute.
     * 
     * @return Returns the nonInstitutionFundOrganizationCode
     * 
     */
    public String getNonInstitutionFundOrganizationCode() { 
        return nonInstitutionFundOrganizationCode;
    }

    /**
     * Sets the nonInstitutionFundOrganizationCode attribute.
     * 
     * @param nonInstitutionFundOrganizationCode The nonInstitutionFundOrganizationCode to set.
     * 
     */
    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode) {
        this.nonInstitutionFundOrganizationCode = nonInstitutionFundOrganizationCode;
    }

    /**
     * Gets the nonInstitutionFundChartOfAccountsCode attribute.
     * 
     * @return Returns the nonInstitutionFundChartOfAccountsCode
     * 
     */
    public String getNonInstitutionFundChartOfAccountsCode() { 
        return nonInstitutionFundChartOfAccountsCode;
    }

    /**
     * Sets the nonInstitutionFundChartOfAccountsCode attribute.
     * 
     * @param nonInstitutionFundChartOfAccountsCode The nonInstitutionFundChartOfAccountsCode to set.
     * 
     */
    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode) {
        this.nonInstitutionFundChartOfAccountsCode = nonInstitutionFundChartOfAccountsCode;
    }

    /**
     * Gets the nonInstitutionFundAccountNumber attribute.
     * 
     * @return Returns the nonInstitutionFundAccountNumber
     * 
     */
    public String getNonInstitutionFundAccountNumber() { 
        return nonInstitutionFundAccountNumber;
    }

    /**
     * Sets the nonInstitutionFundAccountNumber attribute.
     * 
     * @param nonInstitutionFundAccountNumber The nonInstitutionFundAccountNumber to set.
     * 
     */
    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber) {
        this.nonInstitutionFundAccountNumber = nonInstitutionFundAccountNumber;
    }

    /**
     * Gets the deliveryBuildingCode attribute.
     * 
     * @return Returns the deliveryBuildingCode
     * 
     */
    public String getDeliveryBuildingCode() { 
        return deliveryBuildingCode;
    }

    /**
     * Sets the deliveryBuildingCode attribute.
     * 
     * @param deliveryBuildingCode The deliveryBuildingCode to set.
     * 
     */
    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    /**
     * Gets the deliveryBuildingName attribute.
     * 
     * @return Returns the deliveryBuildingName
     * 
     */
    public String getDeliveryBuildingName() { 
        return deliveryBuildingName;
    }

    /**
     * Sets the deliveryBuildingName attribute.
     * 
     * @param deliveryBuildingName The deliveryBuildingName to set.
     * 
     */
    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    /**
     * Gets the deliveryBuildingRoomNumber attribute.
     * 
     * @return Returns the deliveryBuildingRoomNumber
     * 
     */
    public String getDeliveryBuildingRoomNumber() { 
        return deliveryBuildingRoomNumber;
    }

    /**
     * Sets the deliveryBuildingRoomNumber attribute.
     * 
     * @param deliveryBuildingRoomNumber The deliveryBuildingRoomNumber to set.
     * 
     */
    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    /**
     * Gets the deliveryBuildingLine1Address attribute.
     * 
     * @return Returns the deliveryBuildingLine1Address
     * 
     */
    public String getDeliveryBuildingLine1Address() { 
        return deliveryBuildingLine1Address;
    }

    /**
     * Sets the deliveryBuildingLine1Address attribute.
     * 
     * @param deliveryBuildingLine1Address The deliveryBuildingLine1Address to set.
     * 
     */
    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    /**
     * Gets the deliveryBuildingLine2Address attribute.
     * 
     * @return Returns the deliveryBuildingLine2Address
     * 
     */
    public String getDeliveryBuildingLine2Address() { 
        return deliveryBuildingLine2Address;
    }

    /**
     * Sets the deliveryBuildingLine2Address attribute.
     * 
     * @param deliveryBuildingLine2Address The deliveryBuildingLine2Address to set.
     * 
     */
    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    /**
     * Gets the deliveryCityName attribute.
     * 
     * @return Returns the deliveryCityName
     * 
     */
    public String getDeliveryCityName() { 
        return deliveryCityName;
    }

    /**
     * Sets the deliveryCityName attribute.
     * 
     * @param deliveryCityName The deliveryCityName to set.
     * 
     */
    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    /**
     * Gets the deliveryStateCode attribute.
     * 
     * @return Returns the deliveryStateCode
     * 
     */
    public String getDeliveryStateCode() { 
        return deliveryStateCode;
    }

    /**
     * Sets the deliveryStateCode attribute.
     * 
     * @param deliveryStateCode The deliveryStateCode to set.
     * 
     */
    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    /**
     * Gets the deliveryPostalCode attribute.
     * 
     * @return Returns the deliveryPostalCode
     * 
     */
    public String getDeliveryPostalCode() { 
        return deliveryPostalCode;
    }

    /**
     * Sets the deliveryPostalCode attribute.
     * 
     * @param deliveryPostalCode The deliveryPostalCode to set.
     * 
     */
    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    /**
     * Gets the deliveryCountryCode attribute.
     * 
     * @return Returns the deliveryCountryCode
     * 
     */
    public String getDeliveryCountryCode() { 
        return deliveryCountryCode;
    }

    /**
     * Sets the deliveryCountryCode attribute.
     * 
     * @param deliveryCountryCode The deliveryCountryCode to set.
     * 
     */
    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    /**
     * Gets the deliveryToName attribute.
     * 
     * @return Returns the deliveryToName
     * 
     */
    public String getDeliveryToName() { 
        return deliveryToName;
    }

    /**
     * Sets the deliveryToName attribute.
     * 
     * @param deliveryToName The deliveryToName to set.
     * 
     */
    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    /**
     * Gets the deliveryToEmailAddress attribute.
     * 
     * @return Returns the deliveryToEmailAddress
     * 
     */
    public String getDeliveryToEmailAddress() { 
        return deliveryToEmailAddress;
    }

    /**
     * Sets the deliveryToEmailAddress attribute.
     * 
     * @param deliveryToEmailAddress The deliveryToEmailAddress to set.
     * 
     */
    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    /**
     * Gets the deliveryToPhoneNumber attribute.
     * 
     * @return Returns the deliveryToPhoneNumber
     * 
     */
    public String getDeliveryToPhoneNumber() { 
        return deliveryToPhoneNumber;
    }

    /**
     * Sets the deliveryToPhoneNumber attribute.
     * 
     * @param deliveryToPhoneNumber The deliveryToPhoneNumber to set.
     * 
     */
    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    /**
     * Gets the deliveryRequiredDate attribute.
     * 
     * @return Returns the deliveryRequiredDate
     * 
     */
    public Date getDeliveryRequiredDate() { 
        return deliveryRequiredDate;
    }

    /**
     * Sets the deliveryRequiredDate attribute.
     * 
     * @param deliveryRequiredDate The deliveryRequiredDate to set.
     * 
     */
    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    /**
     * Gets the deliveryInstructionText attribute.
     * 
     * @return Returns the deliveryInstructionText
     * 
     */
    public String getDeliveryInstructionText() { 
        return deliveryInstructionText;
    }

    /**
     * Sets the deliveryInstructionText attribute.
     * 
     * @param deliveryInstructionText The deliveryInstructionText to set.
     * 
     */
    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    /**
     * Gets the purchaseOrderBeginDate attribute.
     * 
     * @return Returns the purchaseOrderBeginDate
     * 
     */
    public Date getPurchaseOrderBeginDate() { 
        return purchaseOrderBeginDate;
    }

    /**
     * Sets the purchaseOrderBeginDate attribute.
     * 
     * @param purchaseOrderBeginDate The purchaseOrderBeginDate to set.
     * 
     */
    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate) {
        this.purchaseOrderBeginDate = purchaseOrderBeginDate;
    }

    /**
     * Gets the purchaseOrderEndDate attribute.
     * 
     * @return Returns the purchaseOrderEndDate
     * 
     */
    public Date getPurchaseOrderEndDate() { 
        return purchaseOrderEndDate;
    }

    /**
     * Sets the purchaseOrderEndDate attribute.
     * 
     * @param purchaseOrderEndDate The purchaseOrderEndDate to set.
     * 
     */
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * Gets the institutionContactName attribute.
     * 
     * @return Returns the institutionContactName
     * 
     */
    public String getInstitutionContactName() { 
        return institutionContactName;
    }

    /**
     * Sets the institutionContactName attribute.
     * 
     * @param institutionContactName The institutionContactName to set.
     * 
     */
    public void setInstitutionContactName(String institutionContactName) {
        this.institutionContactName = institutionContactName;
    }

    /**
     * Gets the institutionContactPhoneNumber attribute.
     * 
     * @return Returns the institutionContactPhoneNumber
     * 
     */
    public String getInstitutionContactPhoneNumber() { 
        return institutionContactPhoneNumber;
    }

    /**
     * Sets the institutionContactPhoneNumber attribute.
     * 
     * @param institutionContactPhoneNumber The institutionContactPhoneNumber to set.
     * 
     */
    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber) {
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
    }

    /**
     * Gets the institutionContactEmailAddress attribute.
     * 
     * @return Returns the institutionContactEmailAddress
     * 
     */
    public String getInstitutionContactEmailAddress() { 
        return institutionContactEmailAddress;
    }

    /**
     * Sets the institutionContactEmailAddress attribute.
     * 
     * @param institutionContactEmailAddress The institutionContactEmailAddress to set.
     * 
     */
    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress) {
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }

    /**
     * Gets the billingName attribute.
     * 
     * @return Returns the billingName
     * 
     */
    public String getBillingName() { 
        return billingName;
    }

    /**
     * Sets the billingName attribute.
     * 
     * @param billingName The billingName to set.
     * 
     */
    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    /**
     * Gets the billingLine1Address attribute.
     * 
     * @return Returns the billingLine1Address
     * 
     */
    public String getBillingLine1Address() { 
        return billingLine1Address;
    }

    /**
     * Sets the billingLine1Address attribute.
     * 
     * @param billingLine1Address The billingLine1Address to set.
     * 
     */
    public void setBillingLine1Address(String billingLine1Address) {
        this.billingLine1Address = billingLine1Address;
    }

    /**
     * Gets the billingLine2Address attribute.
     * 
     * @return Returns the billingLine2Address
     * 
     */
    public String getBillingLine2Address() { 
        return billingLine2Address;
    }

    /**
     * Sets the billingLine2Address attribute.
     * 
     * @param billingLine2Address The billingLine2Address to set.
     * 
     */
    public void setBillingLine2Address(String billingLine2Address) {
        this.billingLine2Address = billingLine2Address;
    }

    /**
     * Gets the billingCityName attribute.
     * 
     * @return Returns the billingCityName
     * 
     */
    public String getBillingCityName() { 
        return billingCityName;
    }

    /**
     * Sets the billingCityName attribute.
     * 
     * @param billingCityName The billingCityName to set.
     * 
     */
    public void setBillingCityName(String billingCityName) {
        this.billingCityName = billingCityName;
    }

    /**
     * Gets the billingStateCode attribute.
     * 
     * @return Returns the billingStateCode
     * 
     */
    public String getBillingStateCode() { 
        return billingStateCode;
    }

    /**
     * Sets the billingStateCode attribute.
     * 
     * @param billingStateCode The billingStateCode to set.
     * 
     */
    public void setBillingStateCode(String billingStateCode) {
        this.billingStateCode = billingStateCode;
    }

    /**
     * Gets the billingPostalCode attribute.
     * 
     * @return Returns the billingPostalCode
     * 
     */
    public String getBillingPostalCode() { 
        return billingPostalCode;
    }

    /**
     * Sets the billingPostalCode attribute.
     * 
     * @param billingPostalCode The billingPostalCode to set.
     * 
     */
    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    /**
     * Gets the billingCountryCode attribute.
     * 
     * @return Returns the billingCountryCode
     * 
     */
    public String getBillingCountryCode() { 
        return billingCountryCode;
    }

    /**
     * Sets the billingCountryCode attribute.
     * 
     * @param billingCountryCode The billingCountryCode to set.
     * 
     */
    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    /**
     * Gets the billingPhoneNumber attribute.
     * 
     * @return Returns the billingPhoneNumber
     * 
     */
    public String getBillingPhoneNumber() { 
        return billingPhoneNumber;
    }

    /**
     * Sets the billingPhoneNumber attribute.
     * 
     * @param billingPhoneNumber The billingPhoneNumber to set.
     * 
     */
    public void setBillingPhoneNumber(String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }

    /**
     * Gets the externalOrganizationB2bSupplierIdentifier attribute.
     * 
     * @return Returns the externalOrganizationB2bSupplierIdentifier
     * 
     */
    public String getExternalOrganizationB2bSupplierIdentifier() { 
        return externalOrganizationB2bSupplierIdentifier;
    }

    /**
     * Sets the externalOrganizationB2bSupplierIdentifier attribute.
     * 
     * @param externalOrganizationB2bSupplierIdentifier The externalOrganizationB2bSupplierIdentifier to set.
     * 
     */
    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier) {
        this.externalOrganizationB2bSupplierIdentifier = externalOrganizationB2bSupplierIdentifier;
    }

    /**
     * Gets the contractManagerCode attribute.
     * 
     * @return Returns the contractManagerCode
     * 
     */
    public Integer getContractManagerCode() { 
        return contractManagerCode;
    }

    /**
     * Sets the contractManagerCode attribute.
     * 
     * @param contractManagerCode The contractManagerCode to set.
     * 
     */
    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    /**
     * Gets the purchaseOrderAutomaticIndicator attribute.
     * 
     * @return Returns the purchaseOrderAutomaticIndicator
     * 
     */
    public boolean getPurchaseOrderAutomaticIndicator() { 
        return purchaseOrderAutomaticIndicator;
    }

    /**
     * Sets the purchaseOrderAutomaticIndicator attribute.
     * 
     * @param purchaseOrderAutomaticIndicator The purchaseOrderAutomaticIndicator to set.
     * 
     */
    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator) {
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
    }

    /**
     * Gets the fundingSource attribute.
     * 
     * @return Returns the fundingSource
     * 
     */
    public FundingSource getFundingSource() { 
        return fundingSource;
    }

    /**
     * Sets the fundingSource attribute.
     * 
     * @param fundingSource The fundingSource to set.
     * @deprecated
     */
    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
    }

    /**
     * Gets the requisitionSource attribute.
     * 
     * @return Returns the requisitionSource
     * 
     */
    public RequisitionSource getRequisitionSource() { 
        return requisitionSource;
    }

    /**
     * Sets the requisitionSource attribute.
     * 
     * @param requisitionSource The requisitionSource to set.
     * @deprecated
     */
    public void setRequisitionSource(RequisitionSource requisitionSource) {
        this.requisitionSource = requisitionSource;
    }

    /**
     * Gets the purchaseOrderTransmissionMethod attribute.
     * 
     * @return Returns the purchaseOrderTransmissionMethod
     * 
     */
    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod() { 
        return purchaseOrderTransmissionMethod;
    }

    /**
     * Sets the purchaseOrderTransmissionMethod attribute.
     * 
     * @param purchaseOrderTransmissionMethod The purchaseOrderTransmissionMethod to set.
     * @deprecated
     */
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod) {
        this.purchaseOrderTransmissionMethod = purchaseOrderTransmissionMethod;
    }

    /**
     * Gets the purchaseOrderCostSource attribute.
     * 
     * @return Returns the purchaseOrderCostSource
     * 
     */
    public PurchaseOrderCostSource getPurchaseOrderCostSource() { 
        return purchaseOrderCostSource;
    }

    /**
     * Sets the purchaseOrderCostSource attribute.
     * 
     * @param purchaseOrderCostSource The purchaseOrderCostSource to set.
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource) {
        this.purchaseOrderCostSource = purchaseOrderCostSource;
    }

    /**
     * Gets the deliveryRequiredDateReason attribute.
     * 
     * @return Returns the deliveryRequiredDateReason
     * 
     */
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() { 
        return deliveryRequiredDateReason;
    }

    /**
     * Sets the deliveryRequiredDateReason attribute.
     * 
     * @param deliveryRequiredDateReason The deliveryRequiredDateReason to set.
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * Gets the recurringPaymentType attribute.
     * 
     * @return Returns the recurringPaymentType
     * 
     */
    public RecurringPaymentType getRecurringPaymentType() { 
        return recurringPaymentType;
    }

    /**
     * Sets the recurringPaymentType attribute.
     * 
     * @param recurringPaymentType The recurringPaymentType to set.
     * @deprecated
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     * 
     */
    public Org getOrganization() { 
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() { 
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the deliveryCampus attribute.
     * 
     * @return Returns the deliveryCampus
     * 
     */
    public Campus getDeliveryCampus() { 
        return deliveryCampus;
    }

    /**
     * Sets the deliveryCampus attribute.
     * 
     * @param deliveryCampus The deliveryCampus to set.
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    /**
     * Gets the nonInstitutionFundOrgChartOfAccounts attribute.
     * 
     * @return Returns the nonInstitutionFundOrgChartOfAccounts
     * 
     */
    public Chart getNonInstitutionFundOrgChartOfAccounts() { 
        return nonInstitutionFundOrgChartOfAccounts;
    }

    /**
     * Sets the nonInstitutionFundOrgChartOfAccounts attribute.
     * 
     * @param nonInstitutionFundOrgChartOfAccounts The nonInstitutionFundOrgChartOfAccounts to set.
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts) {
        this.nonInstitutionFundOrgChartOfAccounts = nonInstitutionFundOrgChartOfAccounts;
    }

    /**
     * Gets the nonInstitutionFundOrganization attribute.
     * 
     * @return Returns the nonInstitutionFundOrganization
     * 
     */
    public Org getNonInstitutionFundOrganization() { 
        return nonInstitutionFundOrganization;
    }

    /**
     * Sets the nonInstitutionFundOrganization attribute.
     * 
     * @param nonInstitutionFundOrganization The nonInstitutionFundOrganization to set.
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Org nonInstitutionFundOrganization) {
        this.nonInstitutionFundOrganization = nonInstitutionFundOrganization;
    }

    /**
     * Gets the nonInstitutionFundAccount attribute.
     * 
     * @return Returns the nonInstitutionFundAccount
     * 
     */
    public Account getNonInstitutionFundAccount() { 
        return nonInstitutionFundAccount;
    }

    /**
     * Sets the nonInstitutionFundAccount attribute.
     * 
     * @param nonInstitutionFundAccount The nonInstitutionFundAccount to set.
     * @deprecated
     */
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount) {
        this.nonInstitutionFundAccount = nonInstitutionFundAccount;
    }

    /**
     * Gets the nonInstitutionFundChartOfAccounts attribute.
     * 
     * @return Returns the nonInstitutionFundChartOfAccounts
     * 
     */
    public Chart getNonInstitutionFundChartOfAccounts() { 
        return nonInstitutionFundChartOfAccounts;
    }

    /**
     * Sets the nonInstitutionFundChartOfAccounts attribute.
     * 
     * @param nonInstitutionFundChartOfAccounts The nonInstitutionFundChartOfAccounts to set.
     * @deprecated
     */
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts) {
        this.nonInstitutionFundChartOfAccounts = nonInstitutionFundChartOfAccounts;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    public String getVendorContractName() {
        return vendorContractName;
    }

    public void setVendorContractName(String vendorContractName) {
        this.vendorContractName = vendorContractName;
    }

    public String getSupplierDiversityLabel() {
        return supplierDiversityLabel;
    }

    public String getVendorContactsLabel() {
        return vendorContactsLabel;
    }

    public VendorContract getVendorContract() {
        return vendorContract;
    }

    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    public String getContractManagerName() {
        return contractManagerName;
    }

    public void setContractManagerName(String contractManagerName) {
        this.contractManagerName = contractManagerName;
    }


    /**
     * Gets the deliveryBuildingOther attribute. 
     * @return Returns the deliveryBuildingOther.
     */
    public boolean isDeliveryBuildingOther() {
        return deliveryBuildingOther;
    }

    /**
     * Sets the deliveryBuildingOther attribute value.
     * @param deliveryBuildingOther The deliveryBuildingOther to set.
     */
    public void setDeliveryBuildingOther(boolean deliveryBuildingOther) {
        this.deliveryBuildingOther = deliveryBuildingOther;
    }
    
    public void setVendorContactsLabel(String vendorContactsLabel) {
        this.vendorContactsLabel = vendorContactsLabel;
    }


    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getItems());
        return managedLists;
    }

    
}
