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
import org.kuali.kfs.bo.Country;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.FundingSource;
import org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionSource;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.ContractManager;
import org.kuali.module.vendor.bo.PurchaseOrderCostSource;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;


/**
 * Accounts Payable Document Base
 * 
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);
    
    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
   /*
    private String fundingSourceCode;
    private String requisitionSourceCode;
    private String purchaseOrderTransmissionMethodCode;
    private String purchaseOrderCostSourceCode;
    private String deliveryRequiredDateReasonCode;
    
    private String chartOfAccountsCode;
    private String organizationCode;
    private String deliveryCampusCode;
    private KualiDecimal purchaseOrderTotalLimit;
    */
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
   // private boolean vendorRestrictedIndicator;
    private String vendorPhoneNumber;
    private String vendorFaxNumber;
    private Integer vendorContractGeneratedIdentifier;
    private String vendorNoteText;
    private String recurringPaymentTypeCode;
    /*
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
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;
*/
    // NOT PERSISTED IN DB
    private String vendorNumber; 
 
    private Integer vendorAddressGeneratedIdentifier;
   /*
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
    
    private ContractManager contractManager;
    */
    private Country vendorCountry;
    private RecurringPaymentType recurringPaymentType;

    public AccountsPayableDocumentBase() {
        super();
    }

    
    /**
     * Retrieve all references common to AccountsPayable
     */
  /*
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
        this.refreshReferenceObject("vendorCountry");
    }
*/
    /**
     * @see org.kuali.module.purap.document.PurchasingDocument#templateVendorDetail(org.kuali.module.vendor.bo.VendorDetail)
     */
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }
    
        this.setVendorDetail(vendorDetail);
        this.setVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + VendorConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setVendorName(vendorDetail.getVendorName());
    }
    
   
    
    /**
     * @see org.kuali.module.purap.document.PurchasingDocument#templateVendorAddress(org.kuali.module.vendor.bo.VendorAddress)
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
    
   
    
    // GETTERS AND SETTERS    


    /**
     * Gets the vendorNumber attribute. 
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        return vendorNumber;
    }


    /**
     * Sets the vendorNumber attribute value.
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    

    /**
     * Gets the vendorAddressGeneratedIdentifier attribute. 
     * @return Returns the vendorAddressGeneratedIdentifier.
     */
    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }


    /**
     * Sets the vendorAddressGeneratedIdentifier attribute value.
     * @param vendorAddressGeneratedIdentifier The vendorAddressGeneratedIdentifier to set.
     */
    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
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

 
      public Country getVendorCountry() {
        return vendorCountry;
    }

    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }


    /**
     * Gets the recurringPaymentType attribute. 
     * @return Returns the recurringPaymentType.
     */
    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }


    /**
     * Sets the recurringPaymentType attribute value.
     * @param recurringPaymentType The recurringPaymentType to set.
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }


    /**
     * Gets the recurringPaymentTypeCode attribute. 
     * @return Returns the recurringPaymentTypeCode.
     */
    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }


    /**
     * Sets the recurringPaymentTypeCode attribute value.
     * @param recurringPaymentTypeCode The recurringPaymentTypeCode to set.
     */
    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute. 
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }


    /**
     * Sets the vendorDetailAssignedIdentifier attribute value.
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }


    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute. 
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }


    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute value.
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }



}
