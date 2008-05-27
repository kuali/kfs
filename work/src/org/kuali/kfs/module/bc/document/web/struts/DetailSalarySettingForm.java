/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.form;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionDetail;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;

/**
 * the base struts form for the salary setting
 */
public abstract class DetailSalarySettingForm extends KualiForm {

    private BudgetConstructionDetail budgetConstructionDetail;
    private PendingBudgetConstructionAppointmentFunding newBCAFLine;

    // TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;

    // TODO not sure editingMode is valid here since context is account,subaccount (document)
    // maybe bcdoc needs to have an editingMode map and an ojb ref to bcdoc added in bcaf?
    protected Map<String, String> editingMode;

    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;

    // set and pass these when budgetByAccountMode to prefill the add line
    private boolean addLine;

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String positionNumber;
    private String emplid;

    // pass the value of this as a url parm, false setting means budget by organization
    // this also controls where we return the user when done
    private boolean budgetByAccountMode;
    private boolean orgSalSetClose = false;

    // detail salary setting screen totals used in position and incumbent salary setting
    private KualiInteger bcafAppointmentRequestedCsfAmountTotal;
    private BigDecimal bcafAppointmentRequestedCsfTimePercentTotal;
    private BigDecimal bcafAppointmentRequestedCsfStandardHoursTotal;
    private BigDecimal bcafAppointmentRequestedCsfFteQuantityTotal;
    private KualiInteger bcafAppointmentRequestedAmountTotal;
    private BigDecimal bcafAppointmentRequestedTimePercentTotal;
    private BigDecimal bcafAppointmentRequestedStandardHoursTotal;
    private BigDecimal bcafAppointmentRequestedFteQuantityTotal;
    private KualiInteger bcsfCsfAmountTotal;
    private BigDecimal bcsfCsfTimePercentTotal;
    private BigDecimal bcsfCsfStandardHoursTotal;
    private BigDecimal bcsfCsfFullTimeEmploymentQuantityTotal;

    /**
     * Constructs a DetailSalarySettingForm.java.
     */
    public DetailSalarySettingForm() {
        super();

        setEditingMode(new HashMap<String, String>());
    }

    /**
     * Updates authorization-related form fields based on the current form contents
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {

        useBCAuthorizer(documentAuthorizer);

        // TODO probably need BCAuthorizationConstants extension
        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", this.getAccountNumber() + ", " + this.getSubAccountNumber());
        }

        /*
         * //TODO from KualiDocumentFormBase - remove when ready if (isFormDocumentInitialized()) {
         * useBCAuthorizer(documentAuthorizer); // graceless hack which takes advantage of the fact that here and only here will we
         * have guaranteed access to the // correct DocumentAuthorizer if
         * (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) { throw new
         * AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view",
         * this.getAccountNumber()+", "+this.getSubAccountNumber()); } }
         */
    }

    /*
     * TODO should probably move this to extension class
     */
    protected void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        if (this.isBudgetByAccountMode()) {
            // user got here by opening a BC doc - check using the entire BC security model checking manager, delegate,
            // orgreviewhierachy
            setEditingMode(documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser));
        }
        else {
            // user got here through organization salary setting - check that the user is a BC org approver somewhere
            setEditingMode(documentAuthorizer.getEditMode());
        }

        // TODO probably don't need these, editingmode drives expansion screen actions
        // setDocumentActionFlags(documentAuthorizer.getDocumentActionFlags(document, kualiUser));
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        this.initializeTotals();

        this.populateBCAFLines();
    }

    /**
     * This zeros totals displayed on the detail salary setting screen
     */
    protected void initializeTotals() {
        bcafAppointmentRequestedCsfAmountTotal = KualiInteger.ZERO;
        bcafAppointmentRequestedCsfTimePercentTotal = BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedCsfStandardHoursTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedCsfFteQuantityTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedAmountTotal = KualiInteger.ZERO;
        bcafAppointmentRequestedTimePercentTotal = BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedStandardHoursTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedFteQuantityTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfAmountTotal = KualiInteger.ZERO;
        bcsfCsfTimePercentTotal = BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfStandardHoursTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfFullTimeEmploymentQuantityTotal = BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR);
    }

    /**
     * This method iterates over all of the BCAF lines for the BudgetConstructionPosition TODO verify this - and calls
     * prepareAccountingLineForValidationAndPersistence on each one. This is called to refresh ref objects for use by validation
     */
    protected abstract void populateBCAFLines();

    /**
     * Gets the budgetConstructionDetail attribute.
     * 
     * @return Returns the budgetConstructionDetail.
     */
    public abstract BudgetConstructionDetail getBudgetConstructionDetail();
    
    /**
     * get the refresh caller name of the current form
     * 
     * @return the refresh caller name of the current form
     */
    public abstract String getRefreshCallerName();

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    protected void populateBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();

        this.addBCAFLineToTotals(appointmentFunding);
    }

    /**
     * add the given appointment funding to the totals
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void addBCAFLineToTotals(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        List<BudgetConstructionCalculatedSalaryFoundationTracker> csfTrackers = appointmentFunding.getBcnCalculatedSalaryFoundationTracker();
        if (csfTrackers != null && csfTrackers.size() > 0) {
            KualiInteger csfAmount = csfTrackers.get(0).getCsfAmount();
            if (csfAmount != null) {
                setBcsfCsfAmountTotal(getBcsfCsfAmountTotal().add(csfAmount));
            }

            BigDecimal csfTimePercent = csfTrackers.get(0).getCsfTimePercent();
            if (csfTimePercent != null) {
                setBcsfCsfTimePercentTotal(getBcsfCsfTimePercentTotal().add(csfTimePercent));
            }

            BigDecimal csfFteTotal = csfTrackers.get(0).getCsfFullTimeEmploymentQuantity();
            if (csfFteTotal != null) {
                setBcsfCsfFullTimeEmploymentQuantityTotal(getBcsfCsfFullTimeEmploymentQuantityTotal().add(csfFteTotal));
            }
        }

        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (requestedAmount != null) {
            setBcafAppointmentRequestedAmountTotal(getBcafAppointmentRequestedAmountTotal().add(requestedAmount));
        }

        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();
        if (requestedTimePercent != null) {
            setBcafAppointmentRequestedTimePercentTotal(getBcafAppointmentRequestedTimePercentTotal().add(requestedTimePercent));
        }

        BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
        if (requestedFteQuantity != null) {
            setBcafAppointmentRequestedFteQuantityTotal(getBcafAppointmentRequestedFteQuantityTotal().add(requestedFteQuantity));
        }

        KualiInteger requestedCsfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();
        if (requestedCsfAmount != null) {
            setBcafAppointmentRequestedCsfAmountTotal(getBcafAppointmentRequestedCsfAmountTotal().add(requestedCsfAmount));
        }

        BigDecimal requestedCsfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();
        if (requestedCsfTimePercent != null) {
            setBcafAppointmentRequestedCsfTimePercentTotal(getBcafAppointmentRequestedCsfTimePercentTotal().add(requestedCsfTimePercent));
        }

        BigDecimal requestedCsfFteQuantity = appointmentFunding.getAppointmentRequestedCsfFteQuantity();
        if (requestedCsfFteQuantity != null) {
            setBcafAppointmentRequestedCsfFteQuantityTotal(getBcafAppointmentRequestedCsfFteQuantityTotal().add(requestedCsfFteQuantity));
        }
    }

    /**
     * calculate the standard working hours through the given time percent
     * 
     * @param timePercent the given time percent
     * @return the standard working hour calculated from the given time percent
     */
    private BigDecimal getStandarHours(BigDecimal timePercent) {
        BigDecimal standarHours = timePercent.multiply(BCConstants.STANDARD_WEEKLY_WORK_HOUR_AS_DECIMAL).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        return standarHours;
    }

    /**
     * Gets the bcafAppointmentRequestedAmountTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedAmountTotal.
     */
    public KualiInteger getBcafAppointmentRequestedAmountTotal() {
        return bcafAppointmentRequestedAmountTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedAmountTotal attribute value.
     * 
     * @param bcafAppointmentRequestedAmountTotal The bcafAppointmentRequestedAmountTotal to set.
     */
    public void setBcafAppointmentRequestedAmountTotal(KualiInteger bcafAppointmentRequestedAmountTotal) {
        this.bcafAppointmentRequestedAmountTotal = bcafAppointmentRequestedAmountTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfAmountTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfAmountTotal.
     */
    public KualiInteger getBcafAppointmentRequestedCsfAmountTotal() {
        return bcafAppointmentRequestedCsfAmountTotal;
    }


    /**
     * Sets the bcafAppointmentRequestedCsfAmountTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfAmountTotal The bcafAppointmentRequestedCsfAmountTotal to set.
     */
    public void setBcafAppointmentRequestedCsfAmountTotal(KualiInteger bcafAppointmentRequestedCsfAmountTotal) {
        this.bcafAppointmentRequestedCsfAmountTotal = bcafAppointmentRequestedCsfAmountTotal;
    }


    /**
     * Gets the bcafAppointmentRequestedCsfFteQuantityTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfFteQuantityTotal() {
        return bcafAppointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfFteQuantityTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfFteQuantityTotal The bcafAppointmentRequestedCsfFteQuantityTotal to set.
     */
    public void setBcafAppointmentRequestedCsfFteQuantityTotal(BigDecimal bcafAppointmentRequestedCsfFteQuantityTotal) {
        this.bcafAppointmentRequestedCsfFteQuantityTotal = bcafAppointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfStandardHoursTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfStandardHoursTotal() {
        bcafAppointmentRequestedCsfStandardHoursTotal = this.getStandarHours(bcafAppointmentRequestedCsfTimePercentTotal);
        return bcafAppointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfStandardHoursTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfStandardHoursTotal The bcafAppointmentRequestedCsfStandardHoursTotal to set.
     */
    public void setBcafAppointmentRequestedCsfStandardHoursTotal(BigDecimal bcafAppointmentRequestedCsfStandardHoursTotal) {
        this.bcafAppointmentRequestedCsfStandardHoursTotal = bcafAppointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfTimePercentTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfTimePercentTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfTimePercentTotal() {
        return bcafAppointmentRequestedCsfTimePercentTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfTimePercentTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfTimePercentTotal The bcafAppointmentRequestedCsfTimePercentTotal to set.
     */
    public void setBcafAppointmentRequestedCsfTimePercentTotal(BigDecimal bcafAppointmentRequestedCsfTimePercentTotal) {
        this.bcafAppointmentRequestedCsfTimePercentTotal = bcafAppointmentRequestedCsfTimePercentTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedFteQuantityTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getBcafAppointmentRequestedFteQuantityTotal() {
        return bcafAppointmentRequestedFteQuantityTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedFteQuantityTotal attribute value.
     * 
     * @param bcafAppointmentRequestedFteQuantityTotal The bcafAppointmentRequestedFteQuantityTotal to set.
     */
    public void setBcafAppointmentRequestedFteQuantityTotal(BigDecimal bcafAppointmentRequestedFteQuantityTotal) {
        this.bcafAppointmentRequestedFteQuantityTotal = bcafAppointmentRequestedFteQuantityTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedStandardHoursTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getBcafAppointmentRequestedStandardHoursTotal() {
        bcafAppointmentRequestedStandardHoursTotal = this.getStandarHours(bcafAppointmentRequestedTimePercentTotal);
        return bcafAppointmentRequestedStandardHoursTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedStandardHoursTotal attribute value.
     * 
     * @param bcafAppointmentRequestedStandardHoursTotal The bcafAppointmentRequestedStandardHoursTotal to set.
     */
    public void setBcafAppointmentRequestedStandardHoursTotal(BigDecimal bcafAppointmentRequestedStandardHoursTotal) {
        this.bcafAppointmentRequestedStandardHoursTotal = bcafAppointmentRequestedStandardHoursTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedTimePercentTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedTimePercentTotal.
     */
    public BigDecimal getBcafAppointmentRequestedTimePercentTotal() {
        return bcafAppointmentRequestedTimePercentTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedTimePercentTotal attribute value.
     * 
     * @param bcafAppointmentRequestedTimePercentTotal The bcafAppointmentRequestedTimePercentTotal to set.
     */
    public void setBcafAppointmentRequestedTimePercentTotal(BigDecimal bcafAppointmentRequestedTimePercentTotal) {
        this.bcafAppointmentRequestedTimePercentTotal = bcafAppointmentRequestedTimePercentTotal;
    }

    /**
     * Gets the bcsfCsfAmountTotal attribute.
     * 
     * @return Returns the bcsfCsfAmountTotal.
     */
    public KualiInteger getBcsfCsfAmountTotal() {
        return bcsfCsfAmountTotal;
    }

    /**
     * Sets the bcsfCsfAmountTotal attribute value.
     * 
     * @param bcsfCsfAmountTotal The bcsfCsfAmountTotal to set.
     */
    public void setBcsfCsfAmountTotal(KualiInteger bcsfCsfAmountTotal) {
        this.bcsfCsfAmountTotal = bcsfCsfAmountTotal;
    }

    /**
     * Gets the bcsfCsfFullTimeEmploymentQuantityTotal attribute.
     * 
     * @return Returns the bcsfCsfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getBcsfCsfFullTimeEmploymentQuantityTotal() {
        return bcsfCsfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Sets the bcsfCsfFullTimeEmploymentQuantityTotal attribute value.
     * 
     * @param bcsfCsfFullTimeEmploymentQuantityTotal The bcsfCsfFullTimeEmploymentQuantityTotal to set.
     */
    public void setBcsfCsfFullTimeEmploymentQuantityTotal(BigDecimal bcsfCsfFullTimeEmploymentQuantityTotal) {
        this.bcsfCsfFullTimeEmploymentQuantityTotal = bcsfCsfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Gets the bcsfCsfStandardHoursTotal attribute.
     * 
     * @return Returns the bcsfCsfStandardHoursTotal.
     */
    public BigDecimal getBcsfCsfStandardHoursTotal() {
        bcsfCsfStandardHoursTotal = this.getStandarHours(bcsfCsfTimePercentTotal);
        return bcsfCsfStandardHoursTotal;
    }

    /**
     * Sets the bcsfCsfStandardHoursTotal attribute value.
     * 
     * @param bcsfCsfStandardHoursTotal The bcsfCsfStandardHoursTotal to set.
     */
    public void setBcsfCsfStandardHoursTotal(BigDecimal bcsfCsfStandardHoursTotal) {
        this.bcsfCsfStandardHoursTotal = bcsfCsfStandardHoursTotal;
    }

    /**
     * Gets the bcsfCsfTimePercentTotal attribute.
     * 
     * @return Returns the bcsfCsfTimePercentTotal.
     */
    public BigDecimal getBcsfCsfTimePercentTotal() {
        return bcsfCsfTimePercentTotal;
    }

    /**
     * Sets the bcsfCsfTimePercentTotal attribute value.
     * 
     * @param bcsfCsfTimePercentTotal The bcsfCsfTimePercentTotal to set.
     */
    public void setBcsfCsfTimePercentTotal(BigDecimal bcsfCsfTimePercentTotal) {
        this.bcsfCsfTimePercentTotal = bcsfCsfTimePercentTotal;
    }

    /**
     * Gets the newBCAFLine attribute.
     * 
     * @return Returns the newBCAFLine.
     */
    public PendingBudgetConstructionAppointmentFunding getNewBCAFLine() {
        return newBCAFLine;
    }

    /**
     * Sets the newBCAFLine attribute value.
     * 
     * @param newBCAFLine The newBCAFLine to set.
     */
    public void setNewBCAFLine(PendingBudgetConstructionAppointmentFunding newBCAFLine) {
        this.newBCAFLine = newBCAFLine;
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
     * Gets the returnAnchor attribute.
     * 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * 
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute.
     * 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * 
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the addLine attribute.
     * 
     * @return Returns the addLine.
     */
    public boolean isAddLine() {
        return addLine;
    }

    /**
     * Sets the addLine attribute value.
     * 
     * @param addLine The addLine to set.
     */
    public void setAddLine(boolean addLine) {
        this.addLine = addLine;
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
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
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
}
