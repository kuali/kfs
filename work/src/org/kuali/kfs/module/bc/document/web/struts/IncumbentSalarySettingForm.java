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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;


/**
 * This class...
 * TODO May want to refactor PositionSalarySettingForm and IncumbentSalarySettingForm to extend
 * from new class DetailSalarySettingForm and put common code there. Or use something like
 * DetailSalarySettingFormHelper?
 */
public class IncumbentSalarySettingForm extends DetailSalarySettingForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncumbentSalarySettingForm.class);

    private BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
    private PendingBudgetConstructionAppointmentFunding newBCAFLine;

    //TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;

    //TODO not sure editingMode is valid here since context is account,subaccount (document)
    //maybe bcdoc needs to have an editingMode map and an ojb ref to bcdoc added in bcaf?
    protected Map editingMode;

    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;

    private Integer universityFiscalYear;
    private String emplid;

    //set and pass these when budgetByAccountMode to prefill the add line
    private boolean addLine;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;

    //pass the value of this as a url parm, false setting means budget by organization
    //this also controls where we return the user when done
    private boolean budgetByAccountMode;

    //TODO these should be moved to BudgetConstructionIntendedIncumbent.java to be consistent with
    //SalarySettingExpansion.java and BudgetConstructionPosition.java totaling
    //totals
    private KualiDecimal csfAmountTotal = new KualiDecimal(0.00);
    private BigDecimal csfFullTimeEmploymentQuantityTotal = new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR);
    private BigDecimal csfStandardHoursTotal = new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR);
    private KualiDecimal appointmentRequestedAmountTotal = new KualiDecimal(0.00);
    private BigDecimal appointmentRequestedFteQuantityTotal = new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR);
    private BigDecimal appointmentRequestedStandardHoursTotal = new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR);
    private KualiDecimal appointmentRequestedCsfAmountTotal = new KualiDecimal(0.00);
    private BigDecimal appointmentRequestedCsfFteQuantityTotal = new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR);
    private BigDecimal appointmentRequestedCsfStandardHoursTotal = new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR);

    public IncumbentSalarySettingForm(){
        super();
        setBudgetConstructionIntendedIncumbent(new BudgetConstructionIntendedIncumbent());
        this.editingMode = new HashMap();
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        
        zeroTotals();

        //TODO add insert line populate call here

        populateBCAFLines();
    }

    /**
     * This method iterates over all of the BCAF lines for the BudgetConstructionPosition
     * TODO verify this - and calls prepareAccountingLineForValidationAndPersistence on each one.
     * This is called to refresh ref objects for use by validation
     */
    public void populateBCAFLines(){

        //TODO add bcaf totaling here??

        Iterator bcafLines = this.getBudgetConstructionIntendedIncumbent().getPendingBudgetConstructionAppointmentFunding().iterator();
        while (bcafLines.hasNext()){
            PendingBudgetConstructionAppointmentFunding bcafLine = (PendingBudgetConstructionAppointmentFunding) bcafLines.next();
            this.populateBCAFLine(bcafLine);
        }
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     * 
     * @param line
     */
    private void populateBCAFLine(PendingBudgetConstructionAppointmentFunding line){

//        final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject", "financialSubObject", "laborObject", "budgetConstructionMonthly"}));
      final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {"chartOfAccounts", "account", "subAccount", "financialObject", "financialSubObject", "bcnCalculatedSalaryFoundationTracker", "budgetConstructionDuration"}));
//        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);
        
        addBCAFLineToTotals(line);

    }

    /**
     * Gets the budgetConstructionIntendedIncumbent attribute. 
     * @return Returns the budgetConstructionIntendedIncumbent.
     */
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent() {
        return budgetConstructionIntendedIncumbent;
    }

    /**
     * Sets the budgetConstructionIntendedIncumbent attribute value.
     * @param budgetConstructionIntendedIncumbent The budgetConstructionIntendedIncumbent to set.
     */
    public void setBudgetConstructionIntendedIncumbent(BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent) {
        this.budgetConstructionIntendedIncumbent = budgetConstructionIntendedIncumbent;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the addLine attribute. 
     * @return Returns the addLine.
     */
    public boolean isAddLine() {
        return addLine;
    }

    /**
     * Sets the addLine attribute value.
     * @param addLine The addLine to set.
     */
    public void setAddLine(boolean addLine) {
        this.addLine = addLine;
    }

    /**
     * Gets the budgetByAccountMode attribute. 
     * @return Returns the budgetByAccountMode.
     */
    public boolean isBudgetByAccountMode() {
        return budgetByAccountMode;
    }

    /**
     * Sets the budgetByAccountMode attribute value.
     * @param budgetByAccountMode The budgetByAccountMode to set.
     */
    public void setBudgetByAccountMode(boolean budgetByAccountMode) {
        this.budgetByAccountMode = budgetByAccountMode;
    }

    /**
     * Gets the editingMode attribute. 
     * @return Returns the editingMode.
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Gets the emplid attribute. 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * Updates authorization-related form fields based on the current form contents
     * TODO should probably move this to extension class
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {

        useBCAuthorizer(documentAuthorizer);

        //TODO probably need BCAuthorizationConstants extension
        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", this.getAccountNumber()+", "+this.getSubAccountNumber());
        }

/*
//TODO from KualiDocumentFormBase - remove when ready
        if (isFormDocumentInitialized()) {
            useBCAuthorizer(documentAuthorizer);

            // graceless hack which takes advantage of the fact that here and only here will we have guaranteed access to the
            // correct DocumentAuthorizer
            if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
                throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", this.getAccountNumber()+", "+this.getSubAccountNumber());
            }
        }
*/
    }

    /*
     * TODO should probably move this to extension class
     * 
     */
    protected void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
        
        if (this.isBudgetByAccountMode()){
            // user got here by opening a BC doc - check using the entire BC security model checking manager, delegate, orgreviewhierachy
            setEditingMode(documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser));
        } else {
            // user got here through organization salary setting - check that the user is a BC org approver somewhere
            setEditingMode(documentAuthorizer.getEditMode());
        }
        
//TODO probably don't need these, editingmode drives expansion screen actions
//        setDocumentActionFlags(documentAuthorizer.getDocumentActionFlags(document, kualiUser));
    }

    /**
     * Gets the newBCAFLine attribute. 
     * @return Returns the newBCAFLine.
     */
    public PendingBudgetConstructionAppointmentFunding getNewBCAFLine() {
        if (this.newBCAFLine == null){
            this.setNewBCAFLine(new PendingBudgetConstructionAppointmentFunding());
            this.initNewLine(this.newBCAFLine);
        }
        return newBCAFLine;
    }

    /**
     * Sets the newBCAFLine attribute value.
     * @param newBCAFLine The newBCAFLine to set.
     */
    public void setNewBCAFLine(PendingBudgetConstructionAppointmentFunding newBCAFLine) {
        this.newBCAFLine = newBCAFLine;
    }

    /**
     * This sets the default fields not setable by the user for added lines
     * and any other required initialization
     * 
     * @param line
     */
    private void initNewLine(PendingBudgetConstructionAppointmentFunding line){

//TODO part of this will need to be moved to a form helper setting attributes common to posn and incmbnt salsetting
        BudgetConstructionIntendedIncumbent bcII = this.getBudgetConstructionIntendedIncumbent();
        line.setUniversityFiscalYear(this.getUniversityFiscalYear());
        line.setEmplid(bcII.getEmplid());
        line.setAppointmentFundingDeleteIndicator(false);
//TODO remove this will be set when a user selects and sets the positionNumber
//        line.setAppointmentFundingMonth(bcII.getIuNormalWorkMonths());
        line.setAppointmentRequestedAmount(new KualiInteger(0));
        line.setAppointmentRequestedFteQuantity(new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR));
        line.setAppointmentRequestedTimePercent(new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR));
        line.setAppointmentRequestedPayRate(new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR));
        line.setAppointmentFundingDurationCode(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);
        line.setAppointmentRequestedCsfAmount(new KualiInteger(BigDecimal.ZERO));
        line.setAppointmentRequestedCsfFteQuantity(new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR));
        line.setAppointmentRequestedCsfTimePercent(new BigDecimal(0).setScale(2,KualiDecimal.ROUND_BEHAVIOR));
        line.setAppointmentTotalIntendedAmount(new KualiInteger(BigDecimal.ZERO));
        line.setAppointmentTotalIntendedFteQuantity(new BigDecimal(0).setScale(5,KualiDecimal.ROUND_BEHAVIOR));
        
        
    }

    /**
     * Gets the returnAnchor attribute. 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute. 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the subAccountNumber attribute. 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the appointmentRequestedAmountTotal attribute. 
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiDecimal getAppointmentRequestedAmountTotal() {
        return appointmentRequestedAmountTotal;
    }

    /**
     * Sets the appointmentRequestedAmountTotal attribute value.
     * @param appointmentRequestedAmountTotal The appointmentRequestedAmountTotal to set.
     */
    public void setAppointmentRequestedAmountTotal(KualiDecimal appointmentRequestedAmountTotal) {
        this.appointmentRequestedAmountTotal = appointmentRequestedAmountTotal;
    }

    /**
     * Gets the appointmentRequestedCsfAmountTotal attribute. 
     * @return Returns the appointmentRequestedCsfAmountTotal.
     */
    public KualiDecimal getAppointmentRequestedCsfAmountTotal() {
        return appointmentRequestedCsfAmountTotal;
    }

    /**
     * Sets the appointmentRequestedCsfAmountTotal attribute value.
     * @param appointmentRequestedCsfAmountTotal The appointmentRequestedCsfAmountTotal to set.
     */
    public void setAppointmentRequestedCsfAmountTotal(KualiDecimal appointmentRequestedCsfAmountTotal) {
        this.appointmentRequestedCsfAmountTotal = appointmentRequestedCsfAmountTotal;
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantityTotal attribute. 
     * @return Returns the appointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantityTotal() {
        return appointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Sets the appointmentRequestedCsfFteQuantityTotal attribute value.
     * @param appointmentRequestedCsfFteQuantityTotal The appointmentRequestedCsfFteQuantityTotal to set.
     */
    public void setAppointmentRequestedCsfFteQuantityTotal(BigDecimal appointmentRequestedCsfFteQuantityTotal) {
        this.appointmentRequestedCsfFteQuantityTotal = appointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Gets the appointmentRequestedCsfStandardHoursTotal attribute. 
     * @return Returns the appointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedCsfStandardHoursTotal() {
        return appointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Sets the appointmentRequestedCsfStandardHoursTotal attribute value.
     * @param appointmentRequestedCsfStandardHoursTotal The appointmentRequestedCsfStandardHoursTotal to set.
     */
    public void setAppointmentRequestedCsfStandardHoursTotal(BigDecimal appointmentRequestedCsfStandardHoursTotal) {
        this.appointmentRequestedCsfStandardHoursTotal = appointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal attribute. 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return appointmentRequestedFteQuantityTotal;
    }

    /**
     * Sets the appointmentRequestedFteQuantityTotal attribute value.
     * @param appointmentRequestedFteQuantityTotal The appointmentRequestedFteQuantityTotal to set.
     */
    public void setAppointmentRequestedFteQuantityTotal(BigDecimal appointmentRequestedFteQuantityTotal) {
        this.appointmentRequestedFteQuantityTotal = appointmentRequestedFteQuantityTotal;
    }

    /**
     * Gets the appointmentRequestedStandardHoursTotal attribute. 
     * @return Returns the appointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedStandardHoursTotal() {
        return appointmentRequestedStandardHoursTotal;
    }

    /**
     * Sets the appointmentRequestedStandardHoursTotal attribute value.
     * @param appointmentRequestedStandardHoursTotal The appointmentRequestedStandardHoursTotal to set.
     */
    public void setAppointmentRequestedStandardHoursTotal(BigDecimal appointmentRequestedStandardHoursTotal) {
        this.appointmentRequestedStandardHoursTotal = appointmentRequestedStandardHoursTotal;
    }

    /**
     * Gets the csfAmountTotal attribute. 
     * @return Returns the csfAmountTotal.
     */
    public KualiDecimal getCsfAmountTotal() {
        return csfAmountTotal;
    }

    /**
     * Sets the csfAmountTotal attribute value.
     * @param csfAmountTotal The csfAmountTotal to set.
     */
    public void setCsfAmountTotal(KualiDecimal csfAmountTotal) {
        this.csfAmountTotal = csfAmountTotal;
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal attribute. 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return csfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantityTotal attribute value.
     * @param csfFullTimeEmploymentQuantityTotal The csfFullTimeEmploymentQuantityTotal to set.
     */
    public void setCsfFullTimeEmploymentQuantityTotal(BigDecimal csfFullTimeEmploymentQuantityTotal) {
        this.csfFullTimeEmploymentQuantityTotal = csfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Gets the csfStandardHoursTotal attribute. 
     * @return Returns the csfStandardHoursTotal.
     */
    public BigDecimal getCsfStandardHoursTotal() {
        return csfStandardHoursTotal;
    }

    /**
     * Sets the csfStandardHoursTotal attribute value.
     * @param csfStandardHoursTotal The csfStandardHoursTotal to set.
     */
    public void setCsfStandardHoursTotal(BigDecimal csfStandardHoursTotal) {
        this.csfStandardHoursTotal = csfStandardHoursTotal;
    }

}
