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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * the base Struts form for salary setting
 */
public abstract class SalarySettingBaseForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingBaseForm.class);

    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private SalarySettingFieldsHolder salarySettingFieldsHolder;

    private boolean hideAdjustmentMeasurement = true;
    private String adjustmentMeasurement;
    private KualiDecimal adjustmentAmount;

    private boolean hideDetails = false;

    private boolean budgetByAccountMode;
    private boolean singleAccountMode;
    private boolean salarySettingClosed;

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

    private Person person = GlobalVariables.getUserSession().getPerson();

    protected String dashSubAccountNumber;
    protected String dashFinancialSubObjectCode;

    public SalarySettingBaseForm() {
        super();

        this.setDashFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        this.setDashSubAccountNumber(KFSConstants.getDashSubAccountNumber());
    }

    /**
     * get the refresh caller name of the current form
     *
     * @return the refresh caller name of the current form
     */
    public abstract String getRefreshCallerName();

    /**
     * get the key map for the salary setting item: salary expension, position, or incumbent
     *
     * @return the key map for the salary setting item
     */
    public abstract Map<String, Object> getKeyMapOfSalarySettingItem();

    /**
     * refresh the the appointment funding lines and make them have connections with associated objects
     */
    public void populateBCAFLines() {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            this.refreshBCAFLine(appointmentFunding);
        }
    }

    /**
     * do some operations on the appointment funding lines. The operations may be included updating and sorting. If everything goes
     * well, return true; otherwise, false
     */
    public boolean postProcessBCAFLines() {
        this.populateBCAFLines();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
            String chartCode = appointmentFunding.getChartOfAccountsCode();
            String objectCode = appointmentFunding.getFinancialObjectCode();

            boolean vacatable = salarySettingService.canBeVacant(appointmentFundings, appointmentFunding);
            appointmentFunding.setVacatable(vacatable);

            boolean budgetable = budgetDocumentService.isAssociatedWithBudgetableDocument(appointmentFunding);
            appointmentFunding.setBudgetable(budgetable);

            boolean hourlyPaid = salarySettingService.isHourlyPaidObject(fiscalYear, chartCode, objectCode);
            appointmentFunding.setHourlyPaid(hourlyPaid);
        }

        DynamicCollectionComparator.sort(appointmentFundings, KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.EMPLID);
        return true;
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    public void refreshBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();
        ObjectUtils.materializeObjects(appointmentFunding.getBudgetConstructionAppointmentFundingReason());
        appointmentFunding.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        appointmentFunding.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);
        this.applyDefaultReasonAmountIfEmpty(appointmentFunding);
        this.applyDefaultTotalIntendedAmountIfEmpty(appointmentFunding);
    }

    /**
     * apply default reason amount of zero if the reason code is set and the amount is null
     * adds a blank row place holder if no reason rows, to be optionally filled in by the user
     *
     * @param appointmentFunding
     */
    public void applyDefaultReasonAmountIfEmpty (PendingBudgetConstructionAppointmentFunding appointmentFunding){
        if (!appointmentFunding.getBudgetConstructionAppointmentFundingReason().isEmpty()){
            BudgetConstructionAppointmentFundingReason afReason = appointmentFunding.getBudgetConstructionAppointmentFundingReason().get(0);
            if (ObjectUtils.isNotNull(afReason)){
                if (afReason.getAppointmentFundingReasonAmount() == null){
                    afReason.setAppointmentFundingReasonAmount(KualiInteger.ZERO);
                }
                if (afReason.getAppointmentFundingReasonCode() != null){
                    afReason.refreshReferenceObject(BCPropertyConstants.APPOINTMENT_FUNDING_REASON);
                }
            }
        }
        else {
            appointmentFunding.getBudgetConstructionAppointmentFundingReason().add(new BudgetConstructionAppointmentFundingReason());
        }
    }

    /**
     * apply total intended amount and fte of zero when field is blank
     *
     * @param appointmentFunding
     */
    public void applyDefaultTotalIntendedAmountIfEmpty (PendingBudgetConstructionAppointmentFunding appointmentFunding){
        if (appointmentFunding.getAppointmentTotalIntendedAmount() == null){
            appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
        }
        if (appointmentFunding.getAppointmentTotalIntendedFteQuantity() == null){
            appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);
        }
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     *
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     *
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     *
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     *
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     *
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the hideAdjustmentMeasurement attribute.
     *
     * @return Returns the hideAdjustmentMeasurement.
     */
    public boolean isHideAdjustmentMeasurement() {
        return hideAdjustmentMeasurement;
    }

    /**
     * Sets the hideAdjustmentMeasurement attribute value.
     *
     * @param hideAdjustmentMeasurement The hideAdjustmentMeasurement to set.
     */
    public void setHideAdjustmentMeasurement(boolean hideAdjustmentMeasurement) {
        this.hideAdjustmentMeasurement = hideAdjustmentMeasurement;
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
     * Gets the hideDetails attribute.
     *
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     *
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * Gets the budgetByAccountMode attribute.
     *
     * @return Returns the budgetByAccountMode.
     */
    public boolean isBudgetByAccountMode() {
        return budgetByAccountMode;
    }

    /**
     * Sets the budgetByAccountMode attribute value.
     *
     * @param budgetByAccountMode The budgetByAccountMode to set.
     */
    public void setBudgetByAccountMode(boolean budgetByAccountMode) {
        this.budgetByAccountMode = budgetByAccountMode;
    }

    /**
     * Gets the singleAccountMode attribute.
     *
     * @return Returns the singleAccountMode.
     */
    public boolean isSingleAccountMode() {
        return singleAccountMode;
    }

    /**
     * Sets the singleAccountMode attribute value.
     *
     * @param singleAccountMode The singleAccountMode to set.
     */
    public void setSingleAccountMode(boolean singleAccountMode) {
        this.singleAccountMode = singleAccountMode;
    }

    /**
     * Gets the appointmentFundings attribute.
     *
     * @return Returns the appointmentFundings.
     */
    public abstract List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings();

    /**
     * Gets the appointmentRequestedCsfAmountTotal.
     *
     * @return Returns the appointmentRequestedCsfAmountTotal.
     */
    public KualiInteger getAppointmentRequestedCsfAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfAmountTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfTimePercentTotal.
     *
     * @return Returns the appointmentRequestedCsfTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedCsfTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfTimePercentTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the appointmentRequestedCsfStandardHoursTotal.
     *
     * @return Returns the appointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedCsfStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfStandardHoursTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantityTotal.
     *
     * @return Returns the appointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfFteQuantityTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the appointmentRequestedAmountTotal.
     *
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiInteger getAppointmentRequestedAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedAmountTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedTimePercentTotal.
     *
     * @return Returns the appointmentRequestedTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedTimePercentTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the appointmentRequestedStandardHoursTotal.
     *
     * @return Returns the appointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedStandardHoursTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal.
     *
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedFteQuantityTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfAmountTotal.
     *
     * @return Returns the csfAmountTotal.
     */
    public KualiInteger getCsfAmountTotal() {
        return SalarySettingCalculator.getCsfAmountTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfTimePercentTotal.
     *
     * @return Returns the csfTimePercentTotal.
     */
    public BigDecimal getCsfTimePercentTotal() {
        return SalarySettingCalculator.getCsfTimePercentTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfStandardHoursTotal.
     *
     * @return Returns the csfStandardHoursTotal.
     */
    public BigDecimal getCsfStandardHoursTotal() {
        return SalarySettingCalculator.getCsfStandardHoursTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal.
     *
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return SalarySettingCalculator.getCsfFullTimeEmploymentQuantityTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the percentChangeTotal attribute.
     *
     * @return Returns the percentChangeTotal.
     */
    public KualiDecimal getPercentChangeTotal() {
        KualiInteger csfAmountTotal = this.getCsfAmountTotal();
        KualiInteger requestedAmountTotal = this.getAppointmentRequestedAmountTotal();

        return SalarySettingCalculator.getPercentChange(csfAmountTotal, requestedAmountTotal);
    }

    /**
     * Gets the EffectivePendingBudgetConstructionAppointmentFunding.
     *
     * @return Returns the EffectivePendingBudgetConstructionAppointmentFunding.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getEffectivePendingBudgetConstructionAppointmentFunding() {
        return SalarySettingCalculator.getEffectiveAppointmentFundings(this.getAppointmentFundings());
    }

    /**
     * Gets the salarySettingFieldsHolder attribute.
     *
     * @return Returns the salarySettingFieldsHolder.
     */
    public SalarySettingFieldsHolder getSalarySettingFieldsHolder() {
        if (salarySettingFieldsHolder == null) {
            salarySettingFieldsHolder = new SalarySettingFieldsHolder();
            ObjectUtil.buildObject(salarySettingFieldsHolder, this);
        }

        return salarySettingFieldsHolder;
    }

    /**
     * Gets the person attribute.
     *
     * @return Returns the person.
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Gets the salarySettingClosed attribute.
     *
     * @return Returns the salarySettingClosed.
     */
    public boolean isSalarySettingClosed() {
        return salarySettingClosed;
    }

    /**
     * Sets the salarySettingClosed attribute value.
     *
     * @param salarySettingClosed The salarySettingClosed to set.
     */
    public void setSalarySettingClosed(boolean salarySettingClosed) {
        this.salarySettingClosed = salarySettingClosed;
    }

    /**
     * Gets the viewOnlyEntry attribute. System view only trumps all, overriding methods should call this first and check the
     * results for !viewOnly before continuing.
     *
     * @return Returns the viewOnlyEntry.
     */
    public boolean isViewOnlyEntry() {
        return isSystemViewOnly();
    }

    /**
     * Gets the payrollIncumbentFeedIndictor attribute.
     *
     * @return Returns the payrollIncumbentFeedIndictor.
     */
    public boolean isPayrollIncumbentFeedIndictor() {
        return BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
    }

    /**
     * Gets the payrollPositionFeedIndicator attribute.
     *
     * @return Returns the payrollPositionFeedIndicator.
     */
    public boolean isPayrollPositionFeedIndicator() {
        return BudgetParameterFinder.getPayrollPositionFeedIndicator();
    }

    /**
     * Gets the dashSubAccountNumber attribute.
     *
     * @return Returns the dashSubAccountNumber
     */

    public String getDashSubAccountNumber() {
        return dashSubAccountNumber;
    }

    /**
     * Sets the dashSubAccountNumber attribute.
     *
     * @param dashSubAccountNumber The dashSubAccountNumber to set.
     */
    public void setDashSubAccountNumber(String dashSubAccountNumber) {
        this.dashSubAccountNumber = dashSubAccountNumber;
    }

    /**
     * Gets the dashFinancialSubObjectCode attribute.
     *
     * @return Returns the dashFinancialSubObjectCode
     */

    public String getDashFinancialSubObjectCode() {
        return dashFinancialSubObjectCode;
    }

    /**
     * Sets the dashFinancialSubObjectCode attribute.
     *
     * @param dashFinancialSubObjectCode The dashFinancialSubObjectCode to set.
     */
    public void setDashFinancialSubObjectCode(String dashFinancialSubObjectCode) {
        this.dashFinancialSubObjectCode = dashFinancialSubObjectCode;
    }
}
