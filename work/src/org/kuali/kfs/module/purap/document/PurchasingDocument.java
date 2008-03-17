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
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.DeliveryRequiredDateReason;
import org.kuali.module.purap.bo.FundingSource;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod;
import org.kuali.module.purap.bo.ReceivingAddress;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionSource;
import org.kuali.module.purap.util.ItemParser;
import org.kuali.module.vendor.bo.PurchaseOrderCostSource;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;


/**
 * Interface for Purchasing Documents.
 */
public interface PurchasingDocument extends PurchasingAccountsPayableDocument {

    /**
     * Gets the appropriate <code>{@link ItemParser}</code> for the <code>PurchasingDocument</code>
     * 
     * @return an ItemParser instance
     */
    public ItemParser getItemParser();

    public String getFundingSourceCode();

    public void setFundingSourceCode(String fundingSourceCode);

    public String getRequisitionSourceCode();

    public void setRequisitionSourceCode(String requisitionSourceCode);

    public String getPurchaseOrderTransmissionMethodCode();

    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode);

    public String getPurchaseOrderCostSourceCode();

    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode);

    public String getDeliveryRequiredDateReasonCode();

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode);

    public String getRecurringPaymentTypeCode();

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode);

    public String getChartOfAccountsCode();

    public void setChartOfAccountsCode(String chartOfAccountsCode);

    public String getOrganizationCode();

    public void setOrganizationCode(String organizationCode);

    public String getDeliveryCampusCode();

    public void setDeliveryCampusCode(String deliveryCampusCode);

    public KualiDecimal getPurchaseOrderTotalLimit();

    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit);

    public String getVendorName();

    public void setVendorName(String vendorName);

    public String getVendorLine1Address();

    public void setVendorLine1Address(String vendorLine1Address);

    public String getVendorLine2Address();

    public void setVendorLine2Address(String vendorLine2Address);

    public String getVendorCityName();

    public void setVendorCityName(String vendorCityName);

    public String getVendorStateCode();

    public void setVendorStateCode(String vendorStateCode);

    public String getVendorPostalCode();

    public void setVendorPostalCode(String vendorPostalCode);

    public String getVendorCountryCode();

    public void setVendorCountryCode(String vendorCountryCode);

    public Boolean getVendorRestrictedIndicator();

    public void setVendorRestrictedIndicator(Boolean vendorRestrictedIndicator);

    public String getVendorPhoneNumber();

    public void setVendorPhoneNumber(String vendorPhoneNumber);

    public String getVendorFaxNumber();

    public void setVendorFaxNumber(String vendorFaxNumber);

    public Integer getVendorContractGeneratedIdentifier();

    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier);

    public String getVendorNoteText();

    public void setVendorNoteText(String vendorNoteText);

    public String getRequestorPersonName();

    public void setRequestorPersonName(String requestorPersonName);

    public String getRequestorPersonEmailAddress();

    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress);

    public String getRequestorPersonPhoneNumber();

    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber);

    public String getNonInstitutionFundOrgChartOfAccountsCode();

    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode);

    public String getNonInstitutionFundOrganizationCode();

    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode);

    public String getNonInstitutionFundChartOfAccountsCode();

    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode);

    public String getNonInstitutionFundAccountNumber();

    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber);

    public String getDeliveryBuildingCode();

    public void setDeliveryBuildingCode(String deliveryBuildingCode);

    public String getDeliveryBuildingName();

    public void setDeliveryBuildingName(String deliveryBuildingName);

    public String getDeliveryBuildingRoomNumber();

    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber);

    public String getDeliveryBuildingLine1Address();

    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address);

    public String getDeliveryBuildingLine2Address();

    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address);

    public String getDeliveryCityName();

    public void setDeliveryCityName(String deliveryCityName);

    public String getDeliveryStateCode();

    public void setDeliveryStateCode(String deliveryStateCode);

    public String getDeliveryPostalCode();

    public void setDeliveryPostalCode(String deliveryPostalCode);

    public String getDeliveryCountryCode();

    public void setDeliveryCountryCode(String deliveryCountryCode);

    public String getDeliveryToName();

    public void setDeliveryToName(String deliveryToName);

    public String getDeliveryToEmailAddress();

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress);

    public String getDeliveryToPhoneNumber();

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber);

    public Date getDeliveryRequiredDate();

    public void setDeliveryRequiredDate(Date deliveryRequiredDate);

    public String getDeliveryInstructionText();

    public void setDeliveryInstructionText(String deliveryInstructionText);

    public Date getPurchaseOrderBeginDate();

    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate);

    public Date getPurchaseOrderEndDate();

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate);

    public String getInstitutionContactName();

    public void setInstitutionContactName(String institutionContactName);

    public String getInstitutionContactPhoneNumber();

    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber);

    public String getInstitutionContactEmailAddress();

    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress);

    public String getBillingName();

    public void setBillingName(String billingName);

    public String getBillingLine1Address();

    public void setBillingLine1Address(String billingLine1Address);

    public String getBillingLine2Address();

    public void setBillingLine2Address(String billingLine2Address);

    public String getBillingCityName();

    public void setBillingCityName(String billingCityName);

    public String getBillingStateCode();

    public void setBillingStateCode(String billingStateCode);

    public String getBillingPostalCode();

    public void setBillingPostalCode(String billingPostalCode);

    public String getBillingCountryCode();

    public void setBillingCountryCode(String billingCountryCode);

    public String getBillingPhoneNumber();

    public void setBillingPhoneNumber(String receivingPhoneNumber);

    public String getReceivingName();

    public void setReceivingName(String receivingName);

    public String getReceivingLine1Address();

    public void setReceivingLine1Address(String receivingLine1Address);

    public String getReceivingLine2Address();

    public void setReceivingLine2Address(String receivingLine2Address);

    public String getReceivingCityName();

    public void setReceivingCityName(String receivingCityName);

    public String getReceivingStateCode();

    public void setReceivingStateCode(String receivingStateCode);

    public String getReceivingPostalCode();

    public void setReceivingPostalCode(String receivingPostalCode);

    public String getReceivingCountryCode();

    public void setReceivingCountryCode(String receivingCountryCode);

    public boolean getAddressToVendorIndicator();

    public void setAddressToVendorIndicator(boolean addressToVendorIndicator);

    public String getExternalOrganizationB2bSupplierIdentifier();

    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier);

    public boolean getPurchaseOrderAutomaticIndicator();

    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator);

    public FundingSource getFundingSource();

    public void setFundingSource(FundingSource fundingSource);

    public RequisitionSource getRequisitionSource();

    /**
     * @deprecated
     */
    public void setRequisitionSource(RequisitionSource requisitionSource);

    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod();

    /**
     * @deprecated
     */
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod);

    public PurchaseOrderCostSource getPurchaseOrderCostSource();

    /**
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource);

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason();

    /**
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason);

    public RecurringPaymentType getRecurringPaymentType();

    /**
     * @deprecated
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType);

    public Org getOrganization();

    /**
     * @deprecated
     */
    public void setOrganization(Org organization);

    public Chart getChartOfAccounts();

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts);

    public Campus getDeliveryCampus();

    /**
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus);

    public Chart getNonInstitutionFundOrgChartOfAccounts();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts);

    public Org getNonInstitutionFundOrganization();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Org nonInstitutionFundOrganization);

    public Account getNonInstitutionFundAccount();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount);

    public Chart getNonInstitutionFundChartOfAccounts();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts);

    public String getVendorPaymentTermsCode();

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode);

    public String getVendorShippingPaymentTermsCode();

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode);

    public String getVendorShippingTitleCode();

    public void setVendorShippingTitleCode(String vendorShippingTitleCode);

    public void setVendorContractName(String vendorContractName);

    public boolean isDeliveryBuildingOther();

    public List<PurApItem> getItems();

    public void setItems(List<PurApItem> items);

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getItem(int)
     */
    public PurApItem getItem(int pos);

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addItem(PurApItem item)
     */
    public void addItem(PurApItem item);

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#deleteItem(int lineNum)
     */
    public void deleteItem(int lineNum);

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     */
    public KualiDecimal getTotalDollarAmount();

    /**
     * Sets vendor detail fields based on the specified Vendor Detail.
     * 
     * @param vendorDetail the specified Vendor Detail.
     */
    public void templateVendorDetail(VendorDetail vendorDetail);

    /**
     * Sets vendor contract fields based on the specified Vendor Contract.
     * 
     * @param vendorContract the specified Vendor Contract.
     */
    public void templateVendorContract(VendorContract vendorContract);

    /**
     * Sets billing address fields based on the specified Billing Address.
     * 
     * @param billingAddress the specified Billing Address.
     */
    public void templateBillingAddress(BillingAddress billingAddress);
    
    /**
     * Sets receiving address fields based on the specified Receiving Address.
     * 
     * @param receivingAddress the specified Receiving Address.
     */
    public void templateReceivingAddress(ReceivingAddress receivingAddress);
    
    /**
     * Loads the default receiving address from database corresponding to the chart/org of this document.
     */
    public void loadReceivingAddress();
    
    public boolean isReceivingDocumentRequiredIndicator() ;

    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator);
    
    public boolean isPaymentRequestPositiveApprovalIndicator();
    
    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator);
    
}
