/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class KfsKimAttributes extends org.kuali.rice.kim.bo.impl.KimAttributes {

    public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    public static final String ACCOUNT_NUMBER = "accountNumber";
    public static final String FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE = "financialSystemDocumentTypeCode";
    public static final String ORGANIZATION_CODE = "organizationCode";
    public static final String DESCEND_HIERARCHY = "descendHierarchy";
    public static final String FROM_AMOUNT = "fromAmount";
    public static final String TO_AMOUNT = "toAmount";
    public static final String FINANCIAL_DOCUMENT_TOTAL_AMOUNT = "financialDocumentTotalAmount";
    public static final String ACCOUNTING_LINE_OVERRIDE_CODE = "accountingLineOverrideCode";
    public static final String SUB_FUND_GROUP_CODE = "subFundGroupCode";
    public static final String PURCHASING_COMMODITY_CODE = "purchasingCommodityCode";
    public static final String CONTRACT_MANAGER_CODE = "contractManagerCode";
    public static final String CUSTOMER_PROFILE_ID = "customerProfileId";
    public static final String ACH_TRANSACTION_TYPE_CODE = "achTransactionTypeCode";
    public static final String VENDOR_TYPE_CODE = "vendorTypeCode";
    public static final String CONTRACTS_AND_GRANTS_ACCOUNT_RESPONSIBILITY_ID = "contractsAndGrantsAccountResponsibilityId";
    public static final String DISBURSEMENT_VOUCHER_PAYMENT_METHOD_CODE = "disbursementVoucherPaymentMethodCode";
    public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    public static final String FILE_PATH = "filePath";
    public static final String ROUTE_NODE_NAME = "routeNodeName";

    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String organizationCode;
    protected String descendHierarchy;
    protected String fromAmount;
    protected String toAmount;
    protected String accountingLineOverrideCode;
    protected String subFundGroupCode;
    protected String purchasingCommodityCode;
    protected Integer contractManagerCode;
    protected KualiInteger customerProfileId;
    protected String achTransactionTypeCode;
    protected String vendorTypeCode;
    protected String contractsAndGrantsAccountResponsibilityId;
    protected String disbursementVoucherPaymentMethodCode;
    protected String subAccountNumber;
    protected String filePath;

    protected Chart chart;
    protected Organization organization;
    protected Account account;
    protected SubFundGroup subFundGroup;
    protected ContractManager contractManager;
    protected CommodityCode commodityCode;
    protected CustomerProfile customerProfile;
    protected SubAccount subAccount;
    protected VendorType vendorType;


    @SuppressWarnings("unchecked")

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the descendHierarchy attribute.
     *
     * @return Returns the descendHierarchy.
     */
    public String isDescendHierarchy() {
        return descendHierarchy;
    }

    /**
     * Sets the descendHierarchy attribute value.
     *
     * @param descendHierarchy The descendHierarchy to set.
     */
    public void setDescendHierarchy(String descendHierarchy) {
        this.descendHierarchy = descendHierarchy;
    }

    /**
     * Gets the fromAmount attribute.
     *
     * @return Returns the fromAmount.
     */
    public String getFromAmount() {
        return fromAmount;
    }

    /**
     * Sets the fromAmount attribute value.
     *
     * @param fromAmount The fromAmount to set.
     */
    public void setFromAmount(String fromAmount) {
        this.fromAmount = fromAmount;
    }

    /**
     * Gets the toAmount attribute.
     *
     * @return Returns the toAmount.
     */
    public String getToAmount() {
        return toAmount;
    }

    /**
     * Sets the toAmount attribute value.
     *
     * @param toAmount The toAmount to set.
     */
    public void setToAmount(String toAmount) {
        this.toAmount = toAmount;
    }

    /**
     * Gets the accountingLineOverrideCode attribute.
     *
     * @return Returns the accountingLineOverrideCode.
     */
    public String getAccountingLineOverrideCode() {
        return accountingLineOverrideCode;
    }

    /**
     * Sets the accountingLineOverrideCode attribute value.
     *
     * @param accountingLineOverrideCode The accountingLineOverrideCode to set.
     */
    public void setAccountingLineOverrideCode(String accountingLineOverrideCode) {
        this.accountingLineOverrideCode = accountingLineOverrideCode;
    }

    /**
     * Gets the subFundGroupCode attribute.
     *
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode attribute value.
     *
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    public String getPurchasingCommodityCode() {
        return purchasingCommodityCode;
    }

    public void setPurchasingCommodityCode(String purchasingCommodityCode) {
        this.purchasingCommodityCode = purchasingCommodityCode;
    }

    public Integer getContractManagerCode() {
        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    public KualiInteger getCustomerProfileId() {
        return customerProfileId;
    }

    public void setCustomerProfileId(KualiInteger customerProfileId) {
        this.customerProfileId = customerProfileId;
    }

    public String getAchTransactionTypeCode() {
        return achTransactionTypeCode;
    }

    public void setAchTransactionTypeCode(String achTransactionTypeCode) {
        this.achTransactionTypeCode = achTransactionTypeCode;
    }

    public String getVendorTypeCode() {
        return vendorTypeCode;
    }

    public void setVendorTypeCode(String vendorTypeCode) {
        this.vendorTypeCode = vendorTypeCode;
    }

    public String getContractsAndGrantsAccountResponsibilityId() {
        return contractsAndGrantsAccountResponsibilityId;
    }

    public void setContractsAndGrantsAccountResponsibilityId(String contractsAndGrantsAccountResponsibilityId) {
        this.contractsAndGrantsAccountResponsibilityId = contractsAndGrantsAccountResponsibilityId;
    }

    public String getDisbursementVoucherPaymentMethodCode() {
        return disbursementVoucherPaymentMethodCode;
    }

    public void setDisbursementVoucherPaymentMethodCode(String disbursementVoucherPaymentMethodCode) {
        this.disbursementVoucherPaymentMethodCode = disbursementVoucherPaymentMethodCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    public ContractManager getContractManager() {
        return contractManager;
    }

    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public CommodityCode getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public VendorType getVendorType() {
        return vendorType;
    }

    public void setVendorType(VendorType vendorType) {
        this.vendorType = vendorType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
