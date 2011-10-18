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

package org.kuali.kfs.coa.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * 
 */
public class A21SubAccount extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 2983753447370117974L;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountTypeCode;
    private String indirectCostRecoveryTypeCode;
    private String financialIcrSeriesIdentifier;
    private boolean offCampusCode;
    private String costShareChartOfAccountCode;
    private String costShareSourceAccountNumber;
    private String costShareSourceSubAccountNumber;

    private Chart costShareChartOfAccount;
    private Account costShareAccount;
    private SubAccount costShareSourceSubAccount;
    private IndirectCostRecoveryType indirectCostRecoveryType;
    private Chart chartOfAccounts;
    private Account account;
    
    private List<A21IndirectCostRecoveryAccount> a21IndirectCostRecoveryAccounts;

    /**
     * 
     */
    public A21SubAccount() {
        super();
        a21IndirectCostRecoveryAccounts = new TypedArrayList(A21IndirectCostRecoveryAccount.class);
    }

    /**
     * Gets the serialVersionUID attribute.
     * 
     * @return Returns the serialVersionUID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
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
     * Gets the indirectCostRecoveryType attribute.
     * 
     * @return Returns the indirectCostRecoveryType.
     */
    public IndirectCostRecoveryType getIndirectCostRecoveryType() {
        return indirectCostRecoveryType;
    }

    /**
     * Sets the indirectCostRecoveryType attribute value.
     * 
     * @param indirectCostRecoveryType The indirectCostRecoveryType to set.
     */
    public void setIndirectCostRecoveryType(IndirectCostRecoveryType icrTypeCode) {
        this.indirectCostRecoveryType = icrTypeCode;
    }

    /**
     * Gets the indirectCostRecoveryTypeCode attribute.
     * 
     * @return Returns the indirectCostRecoveryTypeCode.
     */
    public String getIndirectCostRecoveryTypeCode() {
        return indirectCostRecoveryTypeCode;
    }

    /**
     * Sets the indirectCostRecoveryTypeCode attribute value.
     * 
     * @param indirectCostRecoveryTypeCode The indirectCostRecoveryTypeCode to set.
     */
    public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
        this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return Returns the financialIcrSeriesIdentifier.
     */
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute value.
     * 
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the subAccountTypeCode attribute.
     * 
     * @return Returns the subAccountTypeCode.
     */
    public String getSubAccountTypeCode() {
        return subAccountTypeCode;
    }

    /**
     * Sets the subAccountTypeCode attribute value.
     * 
     * @param subAccountTypeCode The subAccountTypeCode to set.
     */
    public void setSubAccountTypeCode(String subAccountTypeCode) {
        this.subAccountTypeCode = subAccountTypeCode;
    }

    /**
     * Gets the costShareAccount attribute.
     * 
     * @return Returns the costShareAccount.
     */
    public Account getCostShareAccount() {
        return costShareAccount;
    }

    /**
     * Sets the costShareAccount attribute value.
     * 
     * @param costShareAccount The costShareAccount to set.
     */
    public void setCostShareAccount(Account costShareAccount) {
        this.costShareAccount = costShareAccount;
    }

    /**
     * Gets the costShareChartOfAccount attribute.
     * 
     * @return Returns the costShareChartOfAccount.
     */
    public Chart getCostShareChartOfAccount() {
        return costShareChartOfAccount;
    }

    /**
     * Sets the costShareChartOfAccount attribute value.
     * 
     * @param costShareChartOfAccount The costShareChartOfAccount to set.
     */
    public void setCostShareChartOfAccount(Chart costShareChartOfAccount) {
        this.costShareChartOfAccount = costShareChartOfAccount;
    }

    /**
     * Gets the costShareChartOfAccountCode attribute.
     * 
     * @return Returns the costShareChartOfAccountCode.
     */
    public String getCostShareChartOfAccountCode() {
        return costShareChartOfAccountCode;
    }

    /**
     * Sets the costShareChartOfAccountCode attribute value.
     * 
     * @param costShareChartOfAccountCode The costShareChartOfAccountCode to set.
     */
    public void setCostShareChartOfAccountCode(String costShareChartOfAccountCode) {
        this.costShareChartOfAccountCode = costShareChartOfAccountCode;
    }

    /**
     * Gets the costShareSourceAccountNumber attribute.
     * 
     * @return Returns the costShareSourceAccountNumber.
     */
    public String getCostShareSourceAccountNumber() {
        return costShareSourceAccountNumber;
    }

    /**
     * Sets the costShareSourceAccountNumber attribute value.
     * 
     * @param costShareSourceAccountNumber The costShareSourceAccountNumber to set.
     */
    public void setCostShareSourceAccountNumber(String costShareSourceAccountNumber) {
        this.costShareSourceAccountNumber = costShareSourceAccountNumber;
    }

    /**
     * Gets the costShareSourceSubAccount attribute.
     * 
     * @return Returns the costShareSourceSubAccount.
     */
    public SubAccount getCostShareSourceSubAccount() {
        return costShareSourceSubAccount;
    }

    /**
     * Sets the costShareSourceSubAccount attribute value.
     * 
     * @param costShareSourceSubAccount The costShareSourceSubAccount to set.
     */
    public void setCostShareSourceSubAccount(SubAccount costShareSourceSubAccount) {
        this.costShareSourceSubAccount = costShareSourceSubAccount;
    }

    /**
     * Gets the costShareSourceSubAccountNumber attribute.
     * 
     * @return Returns the costShareSourceSubAccountNumber.
     */
    public String getCostShareSourceSubAccountNumber() {
        return costShareSourceSubAccountNumber;
    }

    /**
     * Sets the costShareSourceSubAccountNumber attribute value.
     * 
     * @param costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
     */
    public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
        this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
    }

    /**
     * Gets the offCampusCode attribute.
     * 
     * @return Returns the offCampusCode.
     */
    public boolean getOffCampusCode() {
        return offCampusCode;
    }

    /**
     * Sets the offCampusCode attribute value.
     * 
     * @param offCampusCode The offCampusCode to set.
     */
    public void setOffCampusCode(boolean offCampusCode) {
        this.offCampusCode = offCampusCode;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public List<A21IndirectCostRecoveryAccount> getA21IndirectCostRecoveryAccounts() {
        return a21IndirectCostRecoveryAccounts;
    }

    public List<A21IndirectCostRecoveryAccount> getA21ActiveIndirectCostRecoveryAccounts() {
        List<A21IndirectCostRecoveryAccount> activeList = new ArrayList<A21IndirectCostRecoveryAccount>();
        for (A21IndirectCostRecoveryAccount icr : getA21IndirectCostRecoveryAccounts()){
            if (icr.isActive()){
                activeList.add(A21IndirectCostRecoveryAccount.copyICRAccount(icr));
            }
        }
        return activeList;
    }
    
    public void setA21IndirectCostRecoveryAccounts(List<A21IndirectCostRecoveryAccount> a21IndirectCostRecoveryAccounts) {
        this.a21IndirectCostRecoveryAccounts = a21IndirectCostRecoveryAccounts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("accountNumber", getAccountNumber());
        map.put("subAccountNumber", getSubAccountNumber());
        return map;
    }
    
    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getA21IndirectCostRecoveryAccounts());
        return managedLists;
    }

}
