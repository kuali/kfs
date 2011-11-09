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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;

public class PendingBudgetConstructionGeneralLedger extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private KualiInteger accountLineAnnualBalanceAmount;
    private KualiInteger financialBeginningBalanceLineAmount;

    private BudgetConstructionHeader budgetConstructionHeader;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private BalanceType balanceType;
    private ObjectType objectType;

    private List<BudgetConstructionMonthly> budgetConstructionMonthly;

    // These are not defined under ojb since not all expenditure line objects have these
    private LaborLedgerObject laborObject;
    private List<LaborLedgerPositionObjectBenefit> positionObjectBenefit;

    private KualiDecimal adjustmentAmount;
    private KualiDecimal percentChange;
    private KualiInteger persistedAccountLineAnnualBalanceAmount;
    private boolean pendingBudgetConstructionAppointmentFundingExists;

    /**
     * Default constructor.
     */
    public PendingBudgetConstructionGeneralLedger() {
        super();

        budgetConstructionMonthly = new ArrayList<BudgetConstructionMonthly>();
    }

    /**
     * Gets the adjustmentAmount attribute. 
     * @return Returns the adjustmentAmount.
     */
    public KualiDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    /**
     * Sets the adjustmentAmount attribute value.
     * @param adjustmentAmount The adjustmentAmount to set.
     */
    public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    /**
     * Gets(sets) the percentChange based on the current values of base and request amounts
     * 
     * @return Returns percentChange
     */
    public KualiDecimal getPercentChange() {
        KualiInteger baseAmount = this.getFinancialBeginningBalanceLineAmount();
        KualiInteger requestedAmount = this.getAccountLineAnnualBalanceAmount();

        return SalarySettingCalculator.getPercentChange(baseAmount, requestedAmount);
    }

    /**
     * Sets the percentChange attribute value.
     * 
     * @param percentChange The percentChange to set.
     */
    public void setPercentChange(KualiDecimal percentChange) {
        this.percentChange = percentChange;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
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
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
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
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }


    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }


    /**
     * Gets the accountLineAnnualBalanceAmount attribute.
     * 
     * @return Returns the accountLineAnnualBalanceAmount.
     */
    public KualiInteger getAccountLineAnnualBalanceAmount() {
        if (accountLineAnnualBalanceAmount == null) {
            accountLineAnnualBalanceAmount = KualiInteger.ZERO;
        }
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
     * Gets the persistedAccountLineAnnualBalanceAmount attribute.
     * 
     * @return Returns the persistedAccountLineAnnualBalanceAmount.
     */
    public KualiInteger getPersistedAccountLineAnnualBalanceAmount() {
        return persistedAccountLineAnnualBalanceAmount;
    }

    /**
     * Sets the persistedAccountLineAnnualBalanceAmount attribute value.
     * 
     * @param persistedAccountLineAnnualBalanceAmount The persistedAccountLineAnnualBalanceAmount to set.
     */
    public void setPersistedAccountLineAnnualBalanceAmount(KualiInteger persistedAccountLineAnnualBalanceAmount) {
        this.persistedAccountLineAnnualBalanceAmount = persistedAccountLineAnnualBalanceAmount;
    }

    /**
     * Gets the financialBeginningBalanceLineAmount attribute.
     * 
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    public KualiInteger getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * Sets the financialBeginningBalanceLineAmount attribute value.
     * 
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    public void setFinancialBeginningBalanceLineAmount(KualiInteger financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    /**
     * Gets the budgetConstructionMonthly attribute.
     * 
     * @return Returns the budgetConstructionMonthly
     */
    public List<BudgetConstructionMonthly> getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructionMonthly attribute.
     * 
     * @param budgetConstructionMonthly The budgetConstructionMonthly to set.
     * @deprecated
     */
    public void setBudgetConstructionMonthly(List<BudgetConstructionMonthly> budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
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
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public BalanceType getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     * @deprecated
     */
    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     * @deprecated
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the budgetConstructionHeader attribute.
     * 
     * @return Returns the budgetConstructionHeader.
     */
    public BudgetConstructionHeader getBudgetConstructionHeader() {
        return budgetConstructionHeader;
    }

    /**
     * Sets the budgetConstructionHeader attribute value.
     * 
     * @param budgetConstructionHeader The budgetConstructionHeader to set.
     * @deprecated
     */
    public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
        this.budgetConstructionHeader = budgetConstructionHeader;
    }

    /**
     * Gets the laborObject attribute.
     * 
     * @return Returns the laborObject.
     */
    public LaborLedgerObject getLaborObject() {
        laborObject = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborLedgerObject.class).retrieveExternalizableBusinessObjectIfNecessary(this, laborObject, "laborObject");
        return laborObject;
    }

    /**
     * Sets the laborObject attribute value.
     * 
     * @param laborObject The laborObject to set.
     */
    public void setLaborObject(LaborLedgerObject laborObject) {
        this.laborObject = laborObject;
    }

    /**
     * Gets the positionObjectBenefit attribute.
     * 
     * @return Returns the positionObjectBenefit.
     */
    public List<LaborLedgerPositionObjectBenefit> getPositionObjectBenefit() {
        positionObjectBenefit = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).retrieveExternalizableBusinessObjectsList(this, "positionObjectBenefit", LaborLedgerPositionObjectBenefit.class);
        return positionObjectBenefit;
    }

    /**
     * Sets the positionObjectBenefit attribute value.
     * 
     * @param positionObjectBenefit The positionObjectBenefit to set.
     */
    public void setPositionObjectBenefit(List<LaborLedgerPositionObjectBenefit> positionObjectBenefit) {
        this.positionObjectBenefit = positionObjectBenefit;
    }

    /**
     * Gets the pendingBudgetConstructionAppointmentFundingExists attribute.
     * 
     * @return Returns the pendingBudgetConstructionAppointmentFundingExists.
     */
    public boolean isPendingBudgetConstructionAppointmentFundingExists() {
        pendingBudgetConstructionAppointmentFundingExists = false;

        if (this.getLaborObject() != null && this.getLaborObject().isDetailPositionRequiredIndicator()) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();

            fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityFiscalYear());
            fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, getFinancialObjectCode());
            fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, getFinancialSubObjectCode());

            int recCount = SpringContext.getBean(BusinessObjectService.class).countMatching(PendingBudgetConstructionAppointmentFunding.class, fieldValues);

            pendingBudgetConstructionAppointmentFundingExists = (recCount > 0) ? true : false;
        }

        return pendingBudgetConstructionAppointmentFundingExists;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        if (this.universityFiscalYear != null) {
            m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear.toString());
        }

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        m.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.getSubAccountNumber());
        m.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        m.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, this.getFinancialSubObjectCode());
        m.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getFinancialBalanceTypeCode());
        m.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, this.getFinancialObjectTypeCode());

        return m;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = this.buildPrimaryKeyMap();

        return simpleValues;
    }

    /**
     * build the primary key map with the field names as the map keys and the field values as the map values
     * 
     * @return the primary key map with the field names as the map keys and the field values as the map values
     */
    public Map<String, Object> buildPrimaryKeyMap() {
        return ObjectUtil.buildPropertyMap(this, getPrimaryKeyFields());
    }
    
    /**
     * get the list of primary keys
     * @return the list of primary keys
     */
    public static List<String> getPrimaryKeyFields(){
        List<String> primaryKeyFields = new ArrayList<String>();
        primaryKeyFields.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        primaryKeyFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        primaryKeyFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        primaryKeyFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        primaryKeyFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        primaryKeyFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        primaryKeyFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        primaryKeyFields.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        primaryKeyFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        
        return primaryKeyFields;
    }
}
