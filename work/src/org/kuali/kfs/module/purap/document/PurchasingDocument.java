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
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemState;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemType;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.FundingSource;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.businessobject.RequisitionSource;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.util.ItemParser;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;


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

    public String getDocumentFundingSourceCode();

    public void setDocumentFundingSourceCode(String fundingSourceCode);

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
    
    public String getDeliveryCountryName();

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

    public String getBillingCountryName();
    
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

    public String getReceivingCountryName();

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

    public Organization getOrganization();

    /**
     * @deprecated
     */
    public void setOrganization(Organization organization);

    public Chart getChartOfAccounts();

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts);

    public CampusParameter getDeliveryCampus();

    /**
     * @deprecated
     */
    public void setDeliveryCampus(CampusParameter deliveryCampus);

    public Chart getNonInstitutionFundOrgChartOfAccounts();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts);

    public Organization getNonInstitutionFundOrganization();

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Organization nonInstitutionFundOrganization);

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

    public boolean isDeliveryBuildingOtherIndicator();
    
    public void setDeliveryBuildingOtherIndicator(boolean deliveryBuildingOtherIndicator);

    public List<PurApItem> getItems();

    public void setItems(List items);

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getItem(int)
     */
    public PurApItem getItem(int pos);

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#addItem(PurApItem item)
     */
    public void addItem(PurApItem item);

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#deleteItem(int lineNum)
     */
    public void deleteItem(int lineNum);

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
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

    public String getCapitalAssetSystemTypeCode();
   
    public void setCapitalAssetSystemTypeCode(String capitalAssetSystemTypeCode);
  
    public String getCapitalAssetSystemStateCode();
   
    public void setCapitalAssetSystemStateCode(String capitalAssetSystemStateCode);

    public CapitalAssetSystemType getCapitalAssetSystemType();

    public void setCapitalAssetSystemType(CapitalAssetSystemType capitalAssetSystemType);

    public CapitalAssetSystemState getCapitalAssetSystemState();

    public void setCapitalAssetSystemState(CapitalAssetSystemState capitalAssetSystemState);

    public List<CapitalAssetSystem> getPurchasingCapitalAssetSystems();
    
    public void setPurchasingCapitalAssetSystems(List<CapitalAssetSystem> purchasingCapitalAssetSystems);

    public List<PurchasingCapitalAssetItem> getPurchasingCapitalAssetItems();

    public void setPurchasingCapitalAssetItems(List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems);

    public abstract Class getPurchasingCapitalAssetItemClass();
    
    public abstract Class getPurchasingCapitalAssetSystemClass();
    
    public PurchasingItem getPurchasingItem(Integer itemIdentifier);
    
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItem(Integer itemIdentifier);
    
    public abstract PurchasingDocumentSpecificService getDocumentSpecificService();

    public void clearCapitalAssetFields();
    
    public boolean shouldGiveErrorForEmptyAccountsProration();
}
