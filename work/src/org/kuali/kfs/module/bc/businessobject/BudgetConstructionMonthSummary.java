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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionMonthSummary extends PersistableBusinessObjectBase {

    private String principalId;
    private String organizationChartOfAccountsCode;
    private String organizationCode;
    private String subFundGroupCode;
    private String chartOfAccountsCode;
    private String incomeExpenseCode;
    private String financialConsolidationSortCode;
    private String financialLevelSortCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private KualiInteger accountLineAnnualBalanceAmount;
    private KualiInteger financialDocumentMonth1LineAmount;
    private KualiInteger financialDocumentMonth2LineAmount;
    private KualiInteger financialDocumentMonth3LineAmount;
    private KualiInteger financialDocumentMonth4LineAmount;
    private KualiInteger financialDocumentMonth5LineAmount;
    private KualiInteger financialDocumentMonth6LineAmount;
    private KualiInteger financialDocumentMonth7LineAmount;
    private KualiInteger financialDocumentMonth8LineAmount;
    private KualiInteger financialDocumentMonth9LineAmount;
    private KualiInteger financialDocumentMonth10LineAmount;
    private KualiInteger financialDocumentMonth11LineAmount;
    private KualiInteger financialDocumentMonth12LineAmount;
    private String financialConsolidationObjectCode;
    private String financialObjectLevelCode;

    private Chart organizationChartOfAccounts;
    private Organization organization;
    private Chart chartOfAccounts;
    private SubFundGroup subFundGroup;
    private ObjectLevel financialObjectLevel;
    private ObjectConsolidation financialConsolidationObject;

    /**
     * Default constructor.
     */
    public BudgetConstructionMonthSummary() {

    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the organizationChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationChartOfAccountsCode
     */
    public String getOrganizationChartOfAccountsCode() {
        return organizationChartOfAccountsCode;
    }

    /**
     * Sets the organizationChartOfAccountsCode attribute.
     * 
     * @param organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
     */
    public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
        this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the subFundGroupCode attribute.
     * 
     * @return Returns the subFundGroupCode
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode attribute.
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the incomeExpenseCode attribute.
     * 
     * @return Returns the incomeExpenseCode
     */
    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    /**
     * Sets the incomeExpenseCode attribute.
     * 
     * @param incomeExpenseCode The incomeExpenseCode to set.
     */
    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
    }


    /**
     * Gets the financialConsolidationSortCode attribute.
     * 
     * @return Returns the financialConsolidationSortCode
     */
    public String getFinancialConsolidationSortCode() {
        return financialConsolidationSortCode;
    }

    /**
     * Sets the financialConsolidationSortCode attribute.
     * 
     * @param financialConsolidationSortCode The financialConsolidationSortCode to set.
     */
    public void setFinancialConsolidationSortCode(String financialConsolidationSortCode) {
        this.financialConsolidationSortCode = financialConsolidationSortCode;
    }


    /**
     * Gets the financialLevelSortCode attribute.
     * 
     * @return Returns the financialLevelSortCode
     */
    public String getFinancialLevelSortCode() {
        return financialLevelSortCode;
    }

    /**
     * Sets the financialLevelSortCode attribute.
     * 
     * @param financialLevelSortCode The financialLevelSortCode to set.
     */
    public void setFinancialLevelSortCode(String financialLevelSortCode) {
        this.financialLevelSortCode = financialLevelSortCode;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the accountLineAnnualBalanceAmount attribute.
     * 
     * @return Returns the accountLineAnnualBalanceAmount.
     */
    public KualiInteger getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    /**
     * Sets the accountLineAnnualBalanceAmount attribute value.
     * 
     * @param accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
     */
    public void setAccountLineAnnualBalanceAmount(KualiInteger accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth10LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    /**
     * Sets the financialDocumentMonth10LineAmount attribute value.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     */
    public void setFinancialDocumentMonth10LineAmount(KualiInteger financialDocumentMonth10LineAmount) {
        this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
    }

    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth11LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    /**
     * Sets the financialDocumentMonth11LineAmount attribute value.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     */
    public void setFinancialDocumentMonth11LineAmount(KualiInteger financialDocumentMonth11LineAmount) {
        this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
    }

    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth12LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    /**
     * Sets the financialDocumentMonth12LineAmount attribute value.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
     */
    public void setFinancialDocumentMonth12LineAmount(KualiInteger financialDocumentMonth12LineAmount) {
        this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
    }

    /**
     * Gets the financialDocumentMonth1LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth1LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    /**
     * Sets the financialDocumentMonth1LineAmount attribute value.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     */
    public void setFinancialDocumentMonth1LineAmount(KualiInteger financialDocumentMonth1LineAmount) {
        this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
    }

    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth2LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    /**
     * Sets the financialDocumentMonth2LineAmount attribute value.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     */
    public void setFinancialDocumentMonth2LineAmount(KualiInteger financialDocumentMonth2LineAmount) {
        this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
    }

    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth3LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    /**
     * Sets the financialDocumentMonth3LineAmount attribute value.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     */
    public void setFinancialDocumentMonth3LineAmount(KualiInteger financialDocumentMonth3LineAmount) {
        this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
    }

    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth4LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    /**
     * Sets the financialDocumentMonth4LineAmount attribute value.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     */
    public void setFinancialDocumentMonth4LineAmount(KualiInteger financialDocumentMonth4LineAmount) {
        this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
    }

    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth5LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    /**
     * Sets the financialDocumentMonth5LineAmount attribute value.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     */
    public void setFinancialDocumentMonth5LineAmount(KualiInteger financialDocumentMonth5LineAmount) {
        this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
    }

    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth6LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    /**
     * Sets the financialDocumentMonth6LineAmount attribute value.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     */
    public void setFinancialDocumentMonth6LineAmount(KualiInteger financialDocumentMonth6LineAmount) {
        this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
    }

    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth7LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    /**
     * Sets the financialDocumentMonth7LineAmount attribute value.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     */
    public void setFinancialDocumentMonth7LineAmount(KualiInteger financialDocumentMonth7LineAmount) {
        this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
    }

    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth8LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    /**
     * Sets the financialDocumentMonth8LineAmount attribute value.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     */
    public void setFinancialDocumentMonth8LineAmount(KualiInteger financialDocumentMonth8LineAmount) {
        this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
    }

    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth9LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    /**
     * Sets the financialDocumentMonth9LineAmount attribute value.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     */
    public void setFinancialDocumentMonth9LineAmount(KualiInteger financialDocumentMonth9LineAmount) {
        this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
    }

    /**
     * Gets the financialConsolidationObjectCode attribute.
     * 
     * @return Returns the financialConsolidationObjectCode
     */
    public String getFinancialConsolidationObjectCode() {
        return financialConsolidationObjectCode;
    }

    /**
     * Sets the financialConsolidationObjectCode attribute.
     * 
     * @param financialConsolidationObjectCode The financialConsolidationObjectCode to set.
     */
    public void setFinancialConsolidationObjectCode(String financialConsolidationObjectCode) {
        this.financialConsolidationObjectCode = financialConsolidationObjectCode;
    }


    /**
     * Gets the financialObjectLevelCode attribute.
     * 
     * @return Returns the financialObjectLevelCode
     */
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    /**
     * Sets the financialObjectLevelCode attribute.
     * 
     * @param financialObjectLevelCode The financialObjectLevelCode to set.
     */
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }


    /**
     * Gets the organizationChartOfAccounts attribute.
     * 
     * @return Returns the organizationChartOfAccounts
     */
    public Chart getOrganizationChartOfAccounts() {
        return organizationChartOfAccounts;
    }

    /**
     * Sets the organizationChartOfAccounts attribute.
     * 
     * @param organizationChartOfAccounts The organizationChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
        this.organizationChartOfAccounts = organizationChartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
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
     * Gets the financialObjectLevel attribute.
     * 
     * @return Returns the financialObjectLevel.
     */
    public ObjectLevel getFinancialObjectLevel() {
        return financialObjectLevel;
    }

    /**
     * Sets the financialObjectLevel attribute value.
     * 
     * @param financialObjectLevel The financialObjectLevel to set.
     * @deprecated
     */
    public void setFinancialObjectLevel(ObjectLevel financialObjectLevel) {
        this.financialObjectLevel = financialObjectLevel;
    }

    /**
     * Gets the subFundGroup attribute.
     * 
     * @return Returns the subFundGroup.
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute value.
     * 
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the financialConsolidationObject attribute.
     * 
     * @return Returns the financialConsolidationObject.
     */
    public ObjectConsolidation getFinancialConsolidationObject() {
        return financialConsolidationObject;
    }

    /**
     * Sets the financialConsolidationObject attribute value.
     * 
     * @param financialConsolidationObject The financialConsolidationObject to set.
     * @deprecated
     */
    public void setFinancialConsolidationObject(ObjectConsolidation financialConsolidationObject) {
        this.financialConsolidationObject = financialConsolidationObject;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("organizationChartOfAccountsCode", this.organizationChartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("subFundGroupCode", this.subFundGroupCode);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("incomeExpenseCode", this.incomeExpenseCode);
        m.put("financialConsolidationSortCode", this.financialConsolidationSortCode);
        m.put("financialLevelSortCode", this.financialLevelSortCode);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        return m;
    }

}

