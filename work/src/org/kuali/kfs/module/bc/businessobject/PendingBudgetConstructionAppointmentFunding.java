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

package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * 
 */
public class PendingBudgetConstructionAppointmentFunding extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String positionNumber;
    private String emplid;
    private String appointmentFundingDurationCode;
    private KualiInteger appointmentRequestedCsfAmount;
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private BigDecimal appointmentRequestedCsfTimePercent;
    private KualiInteger appointmentTotalIntendedAmount;
    private BigDecimal appointmentTotalIntendedFteQuantity;
    private KualiInteger appointmentRequestedAmount;
    private BigDecimal appointmentRequestedTimePercent;
    private BigDecimal appointmentRequestedFteQuantity;
    private BigDecimal appointmentRequestedPayRate;
    private boolean appointmentFundingDeleteIndicator;
    private Integer appointmentFundingMonth;
    private boolean positionObjectChangeIndicator;
    private boolean positionSalaryChangeIndicator;

    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;
    private BudgetConstructionPosition budgetConstructionPosition;
    private BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
    private BudgetConstructionAccountReports budgetConstructionAccountReports;
    private BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
    private BudgetConstructionDuration budgetConstructionDuration;

    private List<BudgetConstructionCalculatedSalaryFoundationTracker> bcnCalculatedSalaryFoundationTracker;
    private List<BudgetConstructionSalaryFunding> budgetConstructionSalaryFunding;
    private List<BudgetConstructionAppointmentFundingReason> budgetConstructionAppointmentFundingReason;

    private KualiDecimal percentChange;

    private String adjustmentMeasurement;
    private KualiDecimal adjustmentAmount;

    /**
     * Default constructor.
     */
    public PendingBudgetConstructionAppointmentFunding() {
        budgetConstructionSalaryFunding = new TypedArrayList(BudgetConstructionSalaryFunding.class);
        bcnCalculatedSalaryFoundationTracker = new TypedArrayList(BudgetConstructionCalculatedSalaryFoundationTracker.class);
        budgetConstructionAppointmentFundingReason = new TypedArrayList(BudgetConstructionAppointmentFundingReason.class);
    }

    /**
     * Gets(sets) the percentChange based on the current values of csf and request amounts Checks to see if a CSF object exists
     * 
     * @return Returns percentChange
     */
    public KualiDecimal getPercentChange() {

        if (bcnCalculatedSalaryFoundationTracker == null || bcnCalculatedSalaryFoundationTracker.isEmpty()) {
            percentChange = null;
        }
        else {
            BudgetConstructionCalculatedSalaryFoundationTracker csfLine = (BudgetConstructionCalculatedSalaryFoundationTracker) bcnCalculatedSalaryFoundationTracker.get(0);

            if (csfLine.getCsfAmount() == null || csfLine.getCsfAmount().isZero()) {
                percentChange = null;
            }
            else {
                BigDecimal diffRslt = (getAppointmentRequestedAmount().bigDecimalValue().setScale(4)).subtract(csfLine.getCsfAmount().bigDecimalValue().setScale(4));
                BigDecimal divRslt = diffRslt.divide((csfLine.getCsfAmount().bigDecimalValue().setScale(4)), KualiDecimal.ROUND_BEHAVIOR);
                percentChange = new KualiDecimal(divRslt.multiply(BigDecimal.valueOf(100)).setScale(2));
            }
        }
        return percentChange;
    }

    /**
     * Sets the percentChange attribute value.
     * 
     * @param percentChange The percentChange to set.
     * @deprecated
     */
    public void setPercentChange(KualiDecimal percentChange) {
        this.percentChange = percentChange;
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
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the appointmentFundingDurationCode attribute.
     * 
     * @return Returns the appointmentFundingDurationCode
     */
    public String getAppointmentFundingDurationCode() {
        return appointmentFundingDurationCode;
    }

    /**
     * Sets the appointmentFundingDurationCode attribute.
     * 
     * @param appointmentFundingDurationCode The appointmentFundingDurationCode to set.
     */
    public void setAppointmentFundingDurationCode(String appointmentFundingDurationCode) {
        this.appointmentFundingDurationCode = appointmentFundingDurationCode;
    }

    /**
     * Gets the appointmentRequestedCsfAmount attribute.
     * 
     * @return Returns the appointmentRequestedCsfAmount.
     */
    public KualiInteger getAppointmentRequestedCsfAmount() {
        return appointmentRequestedCsfAmount;
    }

    /**
     * Sets the appointmentRequestedCsfAmount attribute value.
     * 
     * @param appointmentRequestedCsfAmount The appointmentRequestedCsfAmount to set.
     */
    public void setAppointmentRequestedCsfAmount(KualiInteger appointmentRequestedCsfAmount) {
        this.appointmentRequestedCsfAmount = appointmentRequestedCsfAmount;
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantity attribute.
     * 
     * @return Returns the appointmentRequestedCsfFteQuantity
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }

    /**
     * Sets the appointmentRequestedCsfFteQuantity attribute.
     * 
     * @param appointmentRequestedCsfFteQuantity The appointmentRequestedCsfFteQuantity to set.
     */
    public void setAppointmentRequestedCsfFteQuantity(BigDecimal appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }

    /**
     * Gets the appointmentRequestedCsfTimePercent attribute.
     * 
     * @return Returns the appointmentRequestedCsfTimePercent
     */
    public BigDecimal getAppointmentRequestedCsfTimePercent() {
        return appointmentRequestedCsfTimePercent;
    }

    /**
     * Sets the appointmentRequestedCsfTimePercent attribute.
     * 
     * @param appointmentRequestedCsfTimePercent The appointmentRequestedCsfTimePercent to set.
     */
    public void setAppointmentRequestedCsfTimePercent(BigDecimal appointmentRequestedCsfTimePercent) {
        this.appointmentRequestedCsfTimePercent = appointmentRequestedCsfTimePercent;
    }

    /**
     * Gets the appointmentTotalIntendedAmount attribute.
     * 
     * @return Returns the appointmentTotalIntendedAmount.
     */
    public KualiInteger getAppointmentTotalIntendedAmount() {
        return appointmentTotalIntendedAmount;
    }

    /**
     * Sets the appointmentTotalIntendedAmount attribute value.
     * 
     * @param appointmentTotalIntendedAmount The appointmentTotalIntendedAmount to set.
     */
    public void setAppointmentTotalIntendedAmount(KualiInteger appointmentTotalIntendedAmount) {
        this.appointmentTotalIntendedAmount = appointmentTotalIntendedAmount;
    }

    /**
     * Gets the appointmentTotalIntendedFteQuantity attribute.
     * 
     * @return Returns the appointmentTotalIntendedFteQuantity
     */
    public BigDecimal getAppointmentTotalIntendedFteQuantity() {
        return appointmentTotalIntendedFteQuantity;
    }

    /**
     * Sets the appointmentTotalIntendedFteQuantity attribute.
     * 
     * @param appointmentTotalIntendedFteQuantity The appointmentTotalIntendedFteQuantity to set.
     */
    public void setAppointmentTotalIntendedFteQuantity(BigDecimal appointmentTotalIntendedFteQuantity) {
        this.appointmentTotalIntendedFteQuantity = appointmentTotalIntendedFteQuantity;
    }

    /**
     * Gets the appointmentRequestedAmount attribute.
     * 
     * @return Returns the appointmentRequestedAmount
     */
    public KualiInteger getAppointmentRequestedAmount() {
        if (appointmentRequestedAmount == null) {
            appointmentRequestedAmount = KualiInteger.ZERO;
        }
        return appointmentRequestedAmount;
    }

    /**
     * Sets the appointmentRequestedAmount attribute.
     * 
     * @param appointmentRequestedAmount The appointmentRequestedAmount to set.
     */
    public void setAppointmentRequestedAmount(KualiInteger appointmentRequestedAmount) {
        this.appointmentRequestedAmount = appointmentRequestedAmount;
    }

    /**
     * Gets the appointmentRequestedTimePercent attribute.
     * 
     * @return Returns the appointmentRequestedTimePercent
     */
    public BigDecimal getAppointmentRequestedTimePercent() {
        return appointmentRequestedTimePercent;
    }

    /**
     * Sets the appointmentRequestedTimePercent attribute.
     * 
     * @param appointmentRequestedTimePercent The appointmentRequestedTimePercent to set.
     */
    public void setAppointmentRequestedTimePercent(BigDecimal appointmentRequestedTimePercent) {
        this.appointmentRequestedTimePercent = appointmentRequestedTimePercent;
    }

    /**
     * Gets the appointmentRequestedFteQuantity attribute.
     * 
     * @return Returns the appointmentRequestedFteQuantity
     */
    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    /**
     * Sets the appointmentRequestedFteQuantity attribute.
     * 
     * @param appointmentRequestedFteQuantity The appointmentRequestedFteQuantity to set.
     */
    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }

    /**
     * Gets the appointmentRequestedPayRate attribute.
     * 
     * @return Returns the appointmentRequestedPayRate
     */
    public BigDecimal getAppointmentRequestedPayRate() {
        return appointmentRequestedPayRate;
    }

    /**
     * Sets the appointmentRequestedPayRate attribute.
     * 
     * @param appointmentRequestedPayRate The appointmentRequestedPayRate to set.
     */
    public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
        this.appointmentRequestedPayRate = appointmentRequestedPayRate;
    }

    /**
     * Gets the appointmentFundingDeleteIndicator attribute.
     * 
     * @return Returns the appointmentFundingDeleteIndicator
     */
    public boolean isAppointmentFundingDeleteIndicator() {
        return appointmentFundingDeleteIndicator;
    }

    /**
     * Sets the appointmentFundingDeleteIndicator attribute.
     * 
     * @param appointmentFundingDeleteIndicator The appointmentFundingDeleteIndicator to set.
     */
    public void setAppointmentFundingDeleteIndicator(boolean appointmentFundingDeleteIndicator) {
        this.appointmentFundingDeleteIndicator = appointmentFundingDeleteIndicator;
    }

    /**
     * Gets the appointmentFundingMonth attribute.
     * 
     * @return Returns the appointmentFundingMonth
     */
    public Integer getAppointmentFundingMonth() {
        return appointmentFundingMonth;
    }

    /**
     * Sets the appointmentFundingMonth attribute.
     * 
     * @param appointmentFundingMonth The appointmentFundingMonth to set.
     */
    public void setAppointmentFundingMonth(Integer appointmentFundingMonth) {
        this.appointmentFundingMonth = appointmentFundingMonth;
    }

    /**
     * Gets the positionObjectChangeIndicator attribute.
     * 
     * @return Returns the positionObjectChangeIndicator
     */
    public boolean isPositionObjectChangeIndicator() {
        return positionObjectChangeIndicator;
    }

    /**
     * Sets the positionObjectChangeIndicator attribute.
     * 
     * @param positionObjectChangeIndicator The positionObjectChangeIndicator to set.
     */
    public void setPositionObjectChangeIndicator(boolean positionObjectChangeIndicator) {
        this.positionObjectChangeIndicator = positionObjectChangeIndicator;
    }

    /**
     * Gets the positionSalaryChangeIndicator attribute.
     * 
     * @return Returns the positionSalaryChangeIndicator
     */
    public boolean isPositionSalaryChangeIndicator() {
        return positionSalaryChangeIndicator;
    }

    /**
     * Sets the positionSalaryChangeIndicator attribute.
     * 
     * @param positionSalaryChangeIndicator The positionSalaryChangeIndicator to set.
     */
    public void setPositionSalaryChangeIndicator(boolean positionSalaryChangeIndicator) {
        this.positionSalaryChangeIndicator = positionSalaryChangeIndicator;
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
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
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
     * Gets the budgetConstructionPosition attribute.
     * 
     * @return Returns the budgetConstructionPosition
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() {
        return budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute.
     * 
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     * @deprecated
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }


    /**
     * Gets the budgetConstructionSalaryFunding attribute.
     * 
     * @return Returns the budgetConstructionSalaryFunding.
     */
    public List<BudgetConstructionSalaryFunding> getBudgetConstructionSalaryFunding() {
        return budgetConstructionSalaryFunding;
    }

    /**
     * Sets the budgetConstructionSalaryFunding attribute value.
     * 
     * @param budgetConstructionSalaryFunding The budgetConstructionSalaryFunding to set.
     */
    @Deprecated
    public void setBudgetConstructionSalaryFunding(List<BudgetConstructionSalaryFunding> budgetConstructionSalaryFunding) {
        this.budgetConstructionSalaryFunding = budgetConstructionSalaryFunding;
    }

    /**
     * Gets the budgetConstructionAppointmentFundingReason attribute.
     * 
     * @return Returns the budgetConstructionAppointmentFundingReason.
     */
    public List<BudgetConstructionAppointmentFundingReason> getBudgetConstructionAppointmentFundingReason() {
        return budgetConstructionAppointmentFundingReason;
    }

    /**
     * Sets the budgetConstructionAppointmentFundingReason attribute value.
     * 
     * @param budgetConstructionAppointmentFundingReason The budgetConstructionAppointmentFundingReason to set.
     */
    @Deprecated
    public void setBudgetConstructionAppointmentFundingReason(List<BudgetConstructionAppointmentFundingReason> budgetConstructionAppointmentFundingReason) {
        this.budgetConstructionAppointmentFundingReason = budgetConstructionAppointmentFundingReason;
    }

    /**
     * Gets the budgetConstructionAdministrativePost attribute.
     * 
     * @return Returns the budgetConstructionAdministrativePost.
     */
    public BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost() {
        return budgetConstructionAdministrativePost;
    }

    /**
     * Sets the budgetConstructionAdministrativePost attribute value.
     * 
     * @param budgetConstructionAdministrativePost The budgetConstructionAdministrativePost to set.
     * @deprecated
     */
    public void setBudgetConstructionAdministrativePost(BudgetConstructionAdministrativePost budgetConstructionAdministrativePost) {
        this.budgetConstructionAdministrativePost = budgetConstructionAdministrativePost;
    }

    /**
     * Gets the budgetConstructionAccountReports attribute.
     * 
     * @return Returns the budgetConstructionAccountReports.
     */
    public BudgetConstructionAccountReports getBudgetConstructionAccountReports() {
        return budgetConstructionAccountReports;
    }

    /**
     * Sets the budgetConstructionAccountReports attribute value.
     * 
     * @param budgetConstructionAccountReports The budgetConstructionAccountReports to set.
     * @deprecated
     */
    public void setBudgetConstructionAccountReports(BudgetConstructionAccountReports budgetConstructionAccountReports) {
        this.budgetConstructionAccountReports = budgetConstructionAccountReports;
    }

    /**
     * Gets the budgetConstructionDuration attribute.
     * 
     * @return Returns the budgetConstructionDuration.
     */
    public BudgetConstructionDuration getBudgetConstructionDuration() {
        return budgetConstructionDuration;
    }

    /**
     * Sets the budgetConstructionDuration attribute value.
     * 
     * @param budgetConstructionDuration The budgetConstructionDuration to set.
     */
    public void setBudgetConstructionDuration(BudgetConstructionDuration budgetConstructionDuration) {
        this.budgetConstructionDuration = budgetConstructionDuration;
    }

    /**
     * Gets the budgetConstructionIntendedIncumbent attribute.
     * 
     * @return Returns the budgetConstructionIntendedIncumbent.
     */
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent() {
        return budgetConstructionIntendedIncumbent;
    }

    /**
     * Sets the budgetConstructionIntendedIncumbent attribute value.
     * 
     * @param budgetConstructionIntendedIncumbent The budgetConstructionIntendedIncumbent to set.
     * @deprecated
     */
    public void setBudgetConstructionIntendedIncumbent(BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent) {
        this.budgetConstructionIntendedIncumbent = budgetConstructionIntendedIncumbent;
    }

    /**
     * Gets the bcnCalculatedSalaryFoundationTracker attribute.
     * 
     * @return Returns the bcnCalculatedSalaryFoundationTracker.
     */
    public List<BudgetConstructionCalculatedSalaryFoundationTracker> getBcnCalculatedSalaryFoundationTracker() {
        return bcnCalculatedSalaryFoundationTracker;
    }

    /**
     * Sets the bcnCalculatedSalaryFoundationTracker attribute value.
     * 
     * @param bcnCalculatedSalaryFoundationTracker The bcnCalculatedSalaryFoundationTracker to set.
     * @deprecated
     */
    public void setBcnCalculatedSalaryFoundationTracker(List<BudgetConstructionCalculatedSalaryFoundationTracker> bcnCalculatedSalaryFoundationTracker) {
        this.bcnCalculatedSalaryFoundationTracker = bcnCalculatedSalaryFoundationTracker;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map getValuesMap() {
        Map simpleValues = new HashMap();

        simpleValues.put("universityFiscalYear", getUniversityFiscalYear());
        simpleValues.put("chartOfAccountsCode", getChartOfAccountsCode());
        simpleValues.put("accountNumber", getAccountNumber());
        simpleValues.put("subAccountNumber", getSubAccountNumber());
        simpleValues.put("financialObjectCode", getFinancialObjectCode());
        simpleValues.put("financialSubObjectCode", getFinancialSubObjectCode());
        simpleValues.put("positionNumber", getPositionNumber());
        simpleValues.put("emplid", getEmplid());

        return simpleValues;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
        return m;
    }

    /**
     * Gets the adjustmentAmount attribute.
     * 
     * @return Returns the adjustmentAmount.
     */
    public KualiDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    /**
     * Sets the adjustmentAmount attribute value.
     * 
     * @param adjustmentAmount The adjustmentAmount to set.
     */
    public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    /**
     * Gets the adjustmentMeasurement attribute.
     * 
     * @return Returns the adjustmentMeasurement.
     */
    public String getAdjustmentMeasurement() {
        return adjustmentMeasurement;
    }

    /**
     * Sets the adjustmentMeasurement attribute value.
     * 
     * @param adjustmentMeasurement The adjustmentMeasurement to set.
     */
    public void setAdjustmentMeasurement(String adjustmentMeasurement) {
        this.adjustmentMeasurement = adjustmentMeasurement;
    }

    /**
     * get the effective calculated salary fundation for current appionment funding if any
     * 
     * @return the the effective calculated salary fundation for current appionment funding if any; otherwise, null
     */
    public BudgetConstructionCalculatedSalaryFoundationTracker getEffectiveCSFTracker() {
        if (bcnCalculatedSalaryFoundationTracker == null || bcnCalculatedSalaryFoundationTracker.size() <= 0) {
            return null;
        }

        return bcnCalculatedSalaryFoundationTracker.get(0);
    }
}
