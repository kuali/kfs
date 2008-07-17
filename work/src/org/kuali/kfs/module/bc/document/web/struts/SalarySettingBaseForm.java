/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * the base Struts form for salary setting
 */
public abstract class SalarySettingBaseForm extends BudgetExpansionForm {
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;

    private boolean hideAdjustmentMeasurement = true;
    private String adjustmentMeasurement;
    private KualiDecimal adjustmentAmount;

    private boolean hideDetails = false;
    private Map<String, String> editingMode;

    private boolean budgetByAccountMode;
    private boolean orgSalSetClose = false;
    
    public SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);

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
     * do some operations on the appointment funding lines. The operations may be included updating and sorting.
     */
    public void postProcessBCAFLines() {
        this.populateBCAFLines();
        
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            boolean vacatable = salarySettingService.canBeVacant(appointmentFundings, appointmentFunding);
            appointmentFunding.setVacatable(vacatable);
            
            // TODO: apply locking somewhere
            salarySettingService.updateAppointmentFundingByUserLevel(appointmentFunding, currentUser.getPersonUniversalIdentifier());
        }

        DynamicCollectionComparator.sort(appointmentFundings, KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.EMPLID);
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    public void refreshBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);
    }

    /**
     * Updates authorization-related form fields based on the current form contents
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        useBCAuthorizer(documentAuthorizer);

        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", this.getAccountNumber() + ", " + this.getSubAccountNumber());
        }
    }

    /**
     * setup the budget construction authorization
     */
    public void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {       
        if (this.isBudgetByAccountMode()) {
            UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

            this.setEditingMode(documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser));
        }
        else {
            // user got here through organization salary setting - check that the user is a BC org approver somewhere
            this.setEditingMode(documentAuthorizer.getEditMode());
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
     * Gets the editingMode attribute.
     * 
     * @return Returns the editingMode.
     */
    public Map<String, String> getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * 
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map<String, String> editingMode) {
        this.editingMode = editingMode;
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
     * Gets the orgSalSetClose attribute.
     * 
     * @return Returns the orgSalSetClose.
     */
    public boolean isOrgSalSetClose() {
        return orgSalSetClose;
    }

    /**
     * Sets the orgSalSetClose attribute value.
     * 
     * @param orgSalSetClose The orgSalSetClose to set.
     */
    public void setOrgSalSetClose(boolean orgSalSetClose) {
        this.orgSalSetClose = orgSalSetClose;
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
        return SalarySettingCalculator.getAppointmentRequestedCsfTimePercentTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedCsfStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfStandardHoursTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfFteQuantityTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
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
        return SalarySettingCalculator.getAppointmentRequestedTimePercentTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedStandardHoursTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedFteQuantityTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
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
        return SalarySettingCalculator.getCsfTimePercentTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfStandardHoursTotal.
     * 
     * @return Returns the csfStandardHoursTotal.
     */
    public BigDecimal getCsfStandardHoursTotal() {
        return SalarySettingCalculator.getCsfStandardHoursTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal.
     * 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return SalarySettingCalculator.getCsfFullTimeEmploymentQuantityTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
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
}
