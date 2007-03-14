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
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.FundingSource;
import org.kuali.module.purap.bo.PurchaseOrderCostSource;
import org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionSource;



/**
 * Purchasing Document Interface
 * 
 */
public interface PurchasingDocument extends PurchasingAccountsPayableDocument {
    
    /**
     * Gets the fundingSourceCode attribute.
     * 
     * @return Returns the fundingSourceCode
     * 
     */
    public String getFundingSourceCode();

    /**
     * Sets the fundingSourceCode attribute.
     * 
     * @param fundingSourceCode The fundingSourceCode to set.
     * 
     */
    public void setFundingSourceCode(String fundingSourceCode);

    /**
     * Gets the requisitionSourceCode attribute.
     * 
     * @return Returns the requisitionSourceCode
     * 
     */
    public String getRequisitionSourceCode();

    /**
     * Sets the requisitionSourceCode attribute.
     * 
     * @param requisitionSourceCode The requisitionSourceCode to set.
     * 
     */
    public void setRequisitionSourceCode(String requisitionSourceCode);

    /**
     * Gets the purchaseOrderTransmissionMethodCode attribute.
     * 
     * @return Returns the purchaseOrderTransmissionMethodCode
     * 
     */
    public String getPurchaseOrderTransmissionMethodCode();

    /**
     * Sets the purchaseOrderTransmissionMethodCode attribute.
     * 
     * @param purchaseOrderTransmissionMethodCode The purchaseOrderTransmissionMethodCode to set.
     * 
     */
    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode);

    /**
     * Gets the purchaseOrderCostSourceCode attribute.
     * 
     * @return Returns the purchaseOrderCostSourceCode
     * 
     */
    public String getPurchaseOrderCostSourceCode();

    /**
     * Sets the purchaseOrderCostSourceCode attribute.
     * 
     * @param purchaseOrderCostSourceCode The purchaseOrderCostSourceCode to set.
     * 
     */
    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode);

    /**
     * Gets the deliveryRequiredDateReasonCode attribute.
     * 
     * @return Returns the deliveryRequiredDateReasonCode
     * 
     */
    public String getDeliveryRequiredDateReasonCode();

    /**
     * Sets the deliveryRequiredDateReasonCode attribute.
     * 
     * @param deliveryRequiredDateReasonCode The deliveryRequiredDateReasonCode to set.
     * 
     */
    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode);

    /**
     * Gets the recurringPaymentTypeCode attribute.
     * 
     * @return Returns the recurringPaymentTypeCode
     * 
     */
    public String getRecurringPaymentTypeCode();

    /**
     * Sets the recurringPaymentTypeCode attribute.
     * 
     * @param recurringPaymentTypeCode The recurringPaymentTypeCode to set.
     * 
     */
    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode);

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode();

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode();

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode);

    /**
     * Gets the deliveryCampusCode attribute.
     * 
     * @return Returns the deliveryCampusCode
     * 
     */
    public String getDeliveryCampusCode();

    /**
     * Sets the deliveryCampusCode attribute.
     * 
     * @param deliveryCampusCode The deliveryCampusCode to set.
     * 
     */
    public void setDeliveryCampusCode(String deliveryCampusCode);

    /**
     * Gets the purchaseOrderTotalLimit attribute.
     * 
     * @return Returns the purchaseOrderTotalLimit
     * 
     */
    public KualiDecimal getPurchaseOrderTotalLimit();

    /**
     * Sets the purchaseOrderTotalLimit attribute.
     * 
     * @param purchaseOrderTotalLimit The purchaseOrderTotalLimit to set.
     * 
     */
    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit);

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     * 
     */
    public String getVendorName();

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     * 
     */
    public void setVendorName(String vendorName);

    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address();

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address);

    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address();

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address);

    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     * 
     */
    public String getVendorCityName();

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName);

    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode();

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode);

    /**
     * Gets the vendorPostalCode attribute.
     * 
     * @return Returns the vendorPostalCode
     * 
     */
    public String getVendorPostalCode();

    /**
     * Sets the vendorPostalCode attribute.
     * 
     * @param vendorPostalCode The vendorPostalCode to set.
     * 
     */
    public void setVendorPostalCode(String vendorPostalCode);

    /**
     * Gets the vendorCountryCode attribute.
     * 
     * @return Returns the vendorCountryCode
     * 
     */
    public String getVendorCountryCode();

    /**
     * Sets the vendorCountryCode attribute.
     * 
     * @param vendorCountryCode The vendorCountryCode to set.
     * 
     */
    public void setVendorCountryCode(String vendorCountryCode);

    /**
     * Gets the vendorRestrictedIndicator attribute.
     * 
     * @return Returns the vendorRestrictedIndicator
     * 
     */
    public boolean getVendorRestrictedIndicator();

    /**
     * Sets the vendorRestrictedIndicator attribute.
     * 
     * @param vendorRestrictedIndicator The vendorRestrictedIndicator to set.
     * 
     */
    public void setVendorRestrictedIndicator(boolean vendorRestrictedIndicator);

    /**
     * Gets the vendorPhoneNumber attribute.
     * 
     * @return Returns the vendorPhoneNumber
     * 
     */
    public String getVendorPhoneNumber();

    /**
     * Sets the vendorPhoneNumber attribute.
     * 
     * @param vendorPhoneNumber The vendorPhoneNumber to set.
     * 
     */
    public void setVendorPhoneNumber(String vendorPhoneNumber);

    /**
     * Gets the vendorFaxNumber attribute.
     * 
     * @return Returns the vendorFaxNumber
     * 
     */
    public String getVendorFaxNumber();

    /**
     * Sets the vendorFaxNumber attribute.
     * 
     * @param vendorFaxNumber The vendorFaxNumber to set.
     * 
     */
    public void setVendorFaxNumber(String vendorFaxNumber);

    /**
     * Gets the vendorContractGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorContractGeneratedIdentifier
     * 
     */
    public Integer getVendorContractGeneratedIdentifier();

    /**
     * Sets the vendorContractGeneratedIdentifier attribute.
     * 
     * @param vendorContractGeneratedIdentifier The vendorContractGeneratedIdentifier to set.
     * 
     */
    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier);

    /**
     * Gets the vendorNoteText attribute.
     * 
     * @return Returns the vendorNoteText
     * 
     */
    public String getVendorNoteText();

    /**
     * Sets the vendorNoteText attribute.
     * 
     * @param vendorNoteText The vendorNoteText to set.
     * 
     */
    public void setVendorNoteText(String vendorNoteText);

    /**
     * Gets the requestorPersonName attribute.
     * 
     * @return Returns the requestorPersonName
     * 
     */
    public String getRequestorPersonName();

    /**
     * Sets the requestorPersonName attribute.
     * 
     * @param requestorPersonName The requestorPersonName to set.
     * 
     */
    public void setRequestorPersonName(String requestorPersonName);

    /**
     * Gets the requestorPersonEmailAddress attribute.
     * 
     * @return Returns the requestorPersonEmailAddress
     * 
     */
    public String getRequestorPersonEmailAddress();

    /**
     * Sets the requestorPersonEmailAddress attribute.
     * 
     * @param requestorPersonEmailAddress The requestorPersonEmailAddress to set.
     * 
     */
    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress);

    /**
     * Gets the requestorPersonPhoneNumber attribute.
     * 
     * @return Returns the requestorPersonPhoneNumber
     * 
     */
    public String getRequestorPersonPhoneNumber();

    /**
     * Sets the requestorPersonPhoneNumber attribute.
     * 
     * @param requestorPersonPhoneNumber The requestorPersonPhoneNumber to set.
     * 
     */
    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber);

    /**
     * Gets the nonInstitutionFundOrgChartOfAccountsCode attribute.
     * 
     * @return Returns the nonInstitutionFundOrgChartOfAccountsCode
     * 
     */
    public String getNonInstitutionFundOrgChartOfAccountsCode();

    /**
     * Sets the nonInstitutionFundOrgChartOfAccountsCode attribute.
     * 
     * @param nonInstitutionFundOrgChartOfAccountsCode The nonInstitutionFundOrgChartOfAccountsCode to set.
     * 
     */
    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode);

    /**
     * Gets the nonInstitutionFundOrganizationCode attribute.
     * 
     * @return Returns the nonInstitutionFundOrganizationCode
     * 
     */
    public String getNonInstitutionFundOrganizationCode();

    /**
     * Sets the nonInstitutionFundOrganizationCode attribute.
     * 
     * @param nonInstitutionFundOrganizationCode The nonInstitutionFundOrganizationCode to set.
     * 
     */
    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode);

    /**
     * Gets the nonInstitutionFundChartOfAccountsCode attribute.
     * 
     * @return Returns the nonInstitutionFundChartOfAccountsCode
     * 
     */
    public String getNonInstitutionFundChartOfAccountsCode();

    /**
     * Sets the nonInstitutionFundChartOfAccountsCode attribute.
     * 
     * @param nonInstitutionFundChartOfAccountsCode The nonInstitutionFundChartOfAccountsCode to set.
     * 
     */
    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode);

    /**
     * Gets the nonInstitutionFundAccountNumber attribute.
     * 
     * @return Returns the nonInstitutionFundAccountNumber
     * 
     */
    public String getNonInstitutionFundAccountNumber();

    /**
     * Sets the nonInstitutionFundAccountNumber attribute.
     * 
     * @param nonInstitutionFundAccountNumber The nonInstitutionFundAccountNumber to set.
     * 
     */
    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber);

    /**
     * Gets the deliveryBuildingCode attribute.
     * 
     * @return Returns the deliveryBuildingCode
     * 
     */
    public String getDeliveryBuildingCode();

    /**
     * Sets the deliveryBuildingCode attribute.
     * 
     * @param deliveryBuildingCode The deliveryBuildingCode to set.
     * 
     */
    public void setDeliveryBuildingCode(String deliveryBuildingCode);

    /**
     * Gets the deliveryBuildingName attribute.
     * 
     * @return Returns the deliveryBuildingName
     * 
     */
    public String getDeliveryBuildingName();

    /**
     * Sets the deliveryBuildingName attribute.
     * 
     * @param deliveryBuildingName The deliveryBuildingName to set.
     * 
     */
    public void setDeliveryBuildingName(String deliveryBuildingName);

    /**
     * Gets the deliveryBuildingRoomNumber attribute.
     * 
     * @return Returns the deliveryBuildingRoomNumber
     * 
     */
    public String getDeliveryBuildingRoomNumber();

    /**
     * Sets the deliveryBuildingRoomNumber attribute.
     * 
     * @param deliveryBuildingRoomNumber The deliveryBuildingRoomNumber to set.
     * 
     */
    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber);

    /**
     * Gets the deliveryBuildingLine1Address attribute.
     * 
     * @return Returns the deliveryBuildingLine1Address
     * 
     */
    public String getDeliveryBuildingLine1Address();

    /**
     * Sets the deliveryBuildingLine1Address attribute.
     * 
     * @param deliveryBuildingLine1Address The deliveryBuildingLine1Address to set.
     * 
     */
    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address);

    /**
     * Gets the deliveryBuildingLine2Address attribute.
     * 
     * @return Returns the deliveryBuildingLine2Address
     * 
     */
    public String getDeliveryBuildingLine2Address();

    /**
     * Sets the deliveryBuildingLine2Address attribute.
     * 
     * @param deliveryBuildingLine2Address The deliveryBuildingLine2Address to set.
     * 
     */
    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address);

    /**
     * Gets the deliveryCityName attribute.
     * 
     * @return Returns the deliveryCityName
     * 
     */
    public String getDeliveryCityName();

    /**
     * Sets the deliveryCityName attribute.
     * 
     * @param deliveryCityName The deliveryCityName to set.
     * 
     */
    public void setDeliveryCityName(String deliveryCityName);

    /**
     * Gets the deliveryStateCode attribute.
     * 
     * @return Returns the deliveryStateCode
     * 
     */
    public String getDeliveryStateCode();

    /**
     * Sets the deliveryStateCode attribute.
     * 
     * @param deliveryStateCode The deliveryStateCode to set.
     * 
     */
    public void setDeliveryStateCode(String deliveryStateCode);

    /**
     * Gets the deliveryPostalCode attribute.
     * 
     * @return Returns the deliveryPostalCode
     * 
     */
    public String getDeliveryPostalCode();

    /**
     * Sets the deliveryPostalCode attribute.
     * 
     * @param deliveryPostalCode The deliveryPostalCode to set.
     * 
     */
    public void setDeliveryPostalCode(String deliveryPostalCode);

    /**
     * Gets the deliveryCountryCode attribute.
     * 
     * @return Returns the deliveryCountryCode
     * 
     */
    public String getDeliveryCountryCode();

    /**
     * Sets the deliveryCountryCode attribute.
     * 
     * @param deliveryCountryCode The deliveryCountryCode to set.
     * 
     */
    public void setDeliveryCountryCode(String deliveryCountryCode);

    /**
     * Gets the deliveryToName attribute.
     * 
     * @return Returns the deliveryToName
     * 
     */
    public String getDeliveryToName();

    /**
     * Sets the deliveryToName attribute.
     * 
     * @param deliveryToName The deliveryToName to set.
     * 
     */
    public void setDeliveryToName(String deliveryToName);

    /**
     * Gets the deliveryToEmailAddress attribute.
     * 
     * @return Returns the deliveryToEmailAddress
     * 
     */
    public String getDeliveryToEmailAddress();

    /**
     * Sets the deliveryToEmailAddress attribute.
     * 
     * @param deliveryToEmailAddress The deliveryToEmailAddress to set.
     * 
     */
    public void setDeliveryToEmailAddress(String deliveryToEmailAddress);

    /**
     * Gets the deliveryToPhoneNumber attribute.
     * 
     * @return Returns the deliveryToPhoneNumber
     * 
     */
    public String getDeliveryToPhoneNumber();

    /**
     * Sets the deliveryToPhoneNumber attribute.
     * 
     * @param deliveryToPhoneNumber The deliveryToPhoneNumber to set.
     * 
     */
    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber);

    /**
     * Gets the deliveryRequiredDate attribute.
     * 
     * @return Returns the deliveryRequiredDate
     * 
     */
    public Date getDeliveryRequiredDate();

    /**
     * Sets the deliveryRequiredDate attribute.
     * 
     * @param deliveryRequiredDate The deliveryRequiredDate to set.
     * 
     */
    public void setDeliveryRequiredDate(Date deliveryRequiredDate);

    /**
     * Gets the deliveryInstructionText attribute.
     * 
     * @return Returns the deliveryInstructionText
     * 
     */
    public String getDeliveryInstructionText();

    /**
     * Sets the deliveryInstructionText attribute.
     * 
     * @param deliveryInstructionText The deliveryInstructionText to set.
     * 
     */
    public void setDeliveryInstructionText(String deliveryInstructionText);

    /**
     * Gets the purchaseOrderBeginDate attribute.
     * 
     * @return Returns the purchaseOrderBeginDate
     * 
     */
    public Date getPurchaseOrderBeginDate();

    /**
     * Sets the purchaseOrderBeginDate attribute.
     * 
     * @param purchaseOrderBeginDate The purchaseOrderBeginDate to set.
     * 
     */
    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate);

    /**
     * Gets the purchaseOrderEndDate attribute.
     * 
     * @return Returns the purchaseOrderEndDate
     * 
     */
    public Date getPurchaseOrderEndDate();

    /**
     * Sets the purchaseOrderEndDate attribute.
     * 
     * @param purchaseOrderEndDate The purchaseOrderEndDate to set.
     * 
     */
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate);

    /**
     * Gets the institutionContactName attribute.
     * 
     * @return Returns the institutionContactName
     * 
     */
    public String getInstitutionContactName();

    /**
     * Sets the institutionContactName attribute.
     * 
     * @param institutionContactName The institutionContactName to set.
     * 
     */
    public void setInstitutionContactName(String institutionContactName);

    /**
     * Gets the institutionContactPhoneNumber attribute.
     * 
     * @return Returns the institutionContactPhoneNumber
     * 
     */
    public String getInstitutionContactPhoneNumber();

    /**
     * Sets the institutionContactPhoneNumber attribute.
     * 
     * @param institutionContactPhoneNumber The institutionContactPhoneNumber to set.
     * 
     */
    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber);

    /**
     * Gets the institutionContactEmailAddress attribute.
     * 
     * @return Returns the institutionContactEmailAddress
     * 
     */
    public String getInstitutionContactEmailAddress();

    /**
     * Sets the institutionContactEmailAddress attribute.
     * 
     * @param institutionContactEmailAddress The institutionContactEmailAddress to set.
     * 
     */
    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress);

    /**
     * Gets the billingName attribute.
     * 
     * @return Returns the billingName
     * 
     */
    public String getBillingName();

    /**
     * Sets the billingName attribute.
     * 
     * @param billingName The billingName to set.
     * 
     */
    public void setBillingName(String billingName);

    /**
     * Gets the billingLine1Address attribute.
     * 
     * @return Returns the billingLine1Address
     * 
     */
    public String getBillingLine1Address();

    /**
     * Sets the billingLine1Address attribute.
     * 
     * @param billingLine1Address The billingLine1Address to set.
     * 
     */
    public void setBillingLine1Address(String billingLine1Address);

    /**
     * Gets the billingLine2Address attribute.
     * 
     * @return Returns the billingLine2Address
     * 
     */
    public String getBillingLine2Address();

    /**
     * Sets the billingLine2Address attribute.
     * 
     * @param billingLine2Address The billingLine2Address to set.
     * 
     */
    public void setBillingLine2Address(String billingLine2Address);

    /**
     * Gets the billingCityName attribute.
     * 
     * @return Returns the billingCityName
     * 
     */
    public String getBillingCityName();

    /**
     * Sets the billingCityName attribute.
     * 
     * @param billingCityName The billingCityName to set.
     * 
     */
    public void setBillingCityName(String billingCityName);

    /**
     * Gets the billingStateCode attribute.
     * 
     * @return Returns the billingStateCode
     * 
     */
    public String getBillingStateCode();

    /**
     * Sets the billingStateCode attribute.
     * 
     * @param billingStateCode The billingStateCode to set.
     * 
     */
    public void setBillingStateCode(String billingStateCode);

    /**
     * Gets the billingPostalCode attribute.
     * 
     * @return Returns the billingPostalCode
     * 
     */
    public String getBillingPostalCode();

    /**
     * Sets the billingPostalCode attribute.
     * 
     * @param billingPostalCode The billingPostalCode to set.
     * 
     */
    public void setBillingPostalCode(String billingPostalCode);

    /**
     * Gets the billingCountryCode attribute.
     * 
     * @return Returns the billingCountryCode
     * 
     */
    public String getBillingCountryCode();

    /**
     * Sets the billingCountryCode attribute.
     * 
     * @param billingCountryCode The billingCountryCode to set.
     * 
     */
    public void setBillingCountryCode(String billingCountryCode);

    /**
     * Gets the billingPhoneNumber attribute.
     * 
     * @return Returns the billingPhoneNumber
     * 
     */
    public String getBillingPhoneNumber();

    /**
     * Sets the billingPhoneNumber attribute.
     * 
     * @param billingPhoneNumber The billingPhoneNumber to set.
     * 
     */
    public void setBillingPhoneNumber(String billingPhoneNumber);

    /**
     * Gets the externalOrganizationB2bSupplierIdentifier attribute.
     * 
     * @return Returns the externalOrganizationB2bSupplierIdentifier
     * 
     */
    public String getExternalOrganizationB2bSupplierIdentifier();

    /**
     * Sets the externalOrganizationB2bSupplierIdentifier attribute.
     * 
     * @param externalOrganizationB2bSupplierIdentifier The externalOrganizationB2bSupplierIdentifier to set.
     * 
     */
    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier);

    /**
     * Gets the contractManagerCode attribute.
     * 
     * @return Returns the contractManagerCode
     * 
     */
    public Integer getContractManagerCode();

    /**
     * Sets the contractManagerCode attribute.
     * 
     * @param contractManagerCode The contractManagerCode to set.
     * 
     */
    public void setContractManagerCode(Integer contractManagerCode);

    /**
     * Gets the purchaseOrderAutomaticIndicator attribute.
     * 
     * @return Returns the purchaseOrderAutomaticIndicator
     * 
     */
    public boolean getPurchaseOrderAutomaticIndicator();

    /**
     * Sets the purchaseOrderAutomaticIndicator attribute.
     * 
     * @param purchaseOrderAutomaticIndicator The purchaseOrderAutomaticIndicator to set.
     * 
     */
    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator);

    /**
     * Gets the fundingSource attribute.
     * 
     * @return Returns the fundingSource
     * 
     */
    public FundingSource getFundingSource();

    /**
     * Sets the fundingSource attribute.
     * 
     * @param fundingSource The fundingSource to set.
     * @deprecated
     */
    public void setFundingSource(FundingSource fundingSource);

    /**
     * Gets the requisitionSource attribute.
     * 
     * @return Returns the requisitionSource
     * 
     */
    public RequisitionSource getRequisitionSource();

    /**
     * Sets the requisitionSource attribute.
     * 
     * @param requisitionSource The requisitionSource to set.
     * @deprecated
     */
    public void setRequisitionSource(RequisitionSource requisitionSource);

    /**
     * Gets the purchaseOrderTransmissionMethod attribute.
     * 
     * @return Returns the purchaseOrderTransmissionMethod
     * 
     */
    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod();

    /**
     * Sets the purchaseOrderTransmissionMethod attribute.
     * 
     * @param purchaseOrderTransmissionMethod The purchaseOrderTransmissionMethod to set.
     * @deprecated
     */
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod);

    /**
     * Gets the purchaseOrderCostSource attribute.
     * 
     * @return Returns the purchaseOrderCostSource
     * 
     */
    public PurchaseOrderCostSource getPurchaseOrderCostSource();

    /**
     * Sets the purchaseOrderCostSource attribute.
     * 
     * @param purchaseOrderCostSource The purchaseOrderCostSource to set.
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource);

    /**
     * Gets the deliveryRequiredDateReason attribute.
     * 
     * @return Returns the deliveryRequiredDateReason
     * 
     */
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason();

    /**
     * Sets the deliveryRequiredDateReason attribute.
     * 
     * @param deliveryRequiredDateReason The deliveryRequiredDateReason to set.
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason);

    /**
     * Gets the recurringPaymentType attribute.
     * 
     * @return Returns the recurringPaymentType
     * 
     */
    public RecurringPaymentType getRecurringPaymentType();

    /**
     * Sets the recurringPaymentType attribute.
     * 
     * @param recurringPaymentType The recurringPaymentType to set.
     * @deprecated
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType);

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     * 
     */
    public Org getOrganization();

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization);

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the deliveryCampus attribute.
     * 
     * @return Returns the deliveryCampus
     * 
     */
    public Campus getDeliveryCampus();

    /**
     * Sets the deliveryCampus attribute.
     * 
     * @param deliveryCampus The deliveryCampus to set.
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus);

    /**
     * Gets the nonInstitutionFundOrgChartOfAccounts attribute.
     * 
     * @return Returns the nonInstitutionFundOrgChartOfAccounts
     * 
     */
    public Chart getNonInstitutionFundOrgChartOfAccounts();

    /**
     * Sets the nonInstitutionFundOrgChartOfAccounts attribute.
     * 
     * @param nonInstitutionFundOrgChartOfAccounts The nonInstitutionFundOrgChartOfAccounts to set.
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts);

    /**
     * Gets the nonInstitutionFundOrganization attribute.
     * 
     * @return Returns the nonInstitutionFundOrganization
     * 
     */
    public Org getNonInstitutionFundOrganization();

    /**
     * Sets the nonInstitutionFundOrganization attribute.
     * 
     * @param nonInstitutionFundOrganization The nonInstitutionFundOrganization to set.
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Org nonInstitutionFundOrganization);

    /**
     * Gets the nonInstitutionFundAccount attribute.
     * 
     * @return Returns the nonInstitutionFundAccount
     * 
     */
    public Account getNonInstitutionFundAccount();

    /**
     * Sets the nonInstitutionFundAccount attribute.
     * 
     * @param nonInstitutionFundAccount The nonInstitutionFundAccount to set.
     * @deprecated
     */
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount);

    /**
     * Gets the nonInstitutionFundChartOfAccounts attribute.
     * 
     * @return Returns the nonInstitutionFundChartOfAccounts
     * 
     */
    public Chart getNonInstitutionFundChartOfAccounts();

    /**
     * Sets the nonInstitutionFundChartOfAccounts attribute.
     * 
     * @param nonInstitutionFundChartOfAccounts The nonInstitutionFundChartOfAccounts to set.
     * @deprecated
     */
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts);
    
    public List<PurchasingApItem> getItems();
    public void setItems(List<PurchasingApItem> items);
    public void addItem(PurchasingApItem item);
    public void deleteItem(int lineNum);
    public KualiDecimal getTotal(); 
    public PurchasingApItem getItem(int pos);
}
