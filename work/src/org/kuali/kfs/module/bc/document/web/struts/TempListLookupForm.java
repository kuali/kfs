/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action Form for budget special lookup screens.
 */
public class TempListLookupForm extends LookupForm {
    private Integer universityFiscalYear;
    private String principalId;
    private String reportMode;
    private String currentPointOfViewKeyCode;
    private boolean buildControlList;
    private boolean reportConsolidation;
    private boolean showInitialResults;
    private int tempListLookupMode = BCConstants.TempListLookupMode.DEFAULT_LOOKUP_MODE;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private List<String> messages;
    private boolean forceToAccountListScreen;
    private boolean showSalaryByPositionAction;
    private boolean showSalaryByIncumbentAction;
    private boolean addLine;
    private boolean budgetByAccountMode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private boolean mainWindow = true;

    /**
     * Checks if the get new button for retrieving a position from the payroll service should be enabled.
     *
     * @return true if button should be enabled, false otherwise
     */
    public boolean isGetNewPositionEnabled() {
        // check if position data is maintained externally
        boolean positionDataExternal = BudgetParameterFinder.getPayrollPositionFeedIndicator();
        if (!positionDataExternal) {
            return false;
        }

        // check if budget updates and updates from HR are enabled
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        boolean budgetUpdatesAllowed = fiscalYearFunctionControlService.isBudgetUpdateAllowed(getUniversityFiscalYear());
        boolean updatesFromHumanResourcedAllowed = fiscalYearFunctionControlService.isApplicationUpdateFromHumanResourcesAllowed(getUniversityFiscalYear());

        return budgetUpdatesAllowed && updatesFromHumanResourcedAllowed;
    }

    /**
     * Checks if the get new button for retrieving an incumbent from the payroll service should be enabled.
     *
     * @return true if button should be enabled, false otherwise
     */
    public boolean isGetNewIncumbentEnabled() {
        // check if incumbent data is maintained externally
        boolean incumbentDataExternal = BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
        if (!incumbentDataExternal) {
            return false;
        }

        // check if budget updates and updates from HR are enabled
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        boolean budgetUpdatesAllowed = fiscalYearFunctionControlService.isBudgetUpdateAllowed(getUniversityFiscalYear());
        boolean updatesFromHumanResourcedAllowed = fiscalYearFunctionControlService.isApplicationUpdateFromHumanResourcesAllowed(getUniversityFiscalYear());

        return budgetUpdatesAllowed && updatesFromHumanResourcedAllowed;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.LookupForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        String refreshCaller = getParameter(request, KRADConstants.REFRESH_CALLER);
        if (StringUtils.isNotBlank(refreshCaller) && KRADConstants.QUESTION_REFRESH.equals(refreshCaller) ) {
            setMethodToCall(WebUtils.parseMethodToCall(this, request));
            if (BCConstants.TEMP_LIST_UNLOCK_METHOD.equals(getMethodToCall())) {
                String lookupImplID = SpringContext.getBean(BusinessObjectDictionaryService.class).getLookupableID(BudgetConstructionLockSummary.class);
                if (lookupImplID == null) {
                    lookupImplID = "kualiLookupable";
                }

                setLookupableImplServiceName(lookupImplID);
            }
        }

        // don't try to populate if no BO - the case when we lose session
        String localBusinessObjectClassName = getParameter(request, KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
        if (ObjectUtils.isNotNull(localBusinessObjectClassName)) {
            super.populate(request);
        }
    }

    public boolean isForceToAccountListScreen() {
        return forceToAccountListScreen;
    }

    public void setForceToAccountListScreen(boolean forceToAccountListScreen) {
        this.forceToAccountListScreen = forceToAccountListScreen;
    }

    /**
     * Gets the currentPointOfViewKeyCode attribute.
     *
     * @return Returns the currentPointOfViewKeyCode.
     */
    public String getCurrentPointOfViewKeyCode() {
        return currentPointOfViewKeyCode;
    }

    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Gets the reportMode attribute.
     *
     * @return Returns the reportMode.
     */
    public String getReportMode() {
        return reportMode;
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
     * Gets the buildControlList attribute.
     *
     * @return Returns the buildControlList.
     */
    public boolean isBuildControlList() {
        return buildControlList;
    }

    /**
     * Gets the reportConsolidation attribute.
     *
     * @return Returns the reportConsolidation.
     */
    public boolean isReportConsolidation() {
        return reportConsolidation;
    }

    /**
     * Gets the showInitialResults attribute.
     *
     * @return Returns the showInitialResults.
     */
    public boolean isShowInitialResults() {
        return showInitialResults;
    }

    /**
     * Sets the buildControlList attribute value.
     *
     * @param buildControlList The buildControlList to set.
     */
    public void setBuildControlList(boolean buildControlList) {
        this.buildControlList = buildControlList;
    }

    /**
     * Sets the currentPointOfViewKeyCode attribute value.
     *
     * @param currentPointOfViewKeyCode The currentPointOfViewKeyCode to set.
     */
    public void setCurrentPointOfViewKeyCode(String currentPointOfViewKeyCode) {
        this.currentPointOfViewKeyCode = currentPointOfViewKeyCode;
    }

    /**
     * Sets the principalId attribute value.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Sets the reportConsolidation attribute value.
     *
     * @param reportConsolidation The reportConsolidation to set.
     */
    public void setReportConsolidation(boolean reportConsolidation) {
        this.reportConsolidation = reportConsolidation;
    }

    /**
     * Sets the reportMode attribute value.
     *
     * @param reportMode The reportMode to set.
     */
    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }

    /**
     * Sets the showInitialResults attribute value.
     *
     * @param showInitialResults The showInitialResults to set.
     */
    public void setShowInitialResults(boolean showInitialResults) {
        this.showInitialResults = showInitialResults;
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
     * Gets the tempListLookupMode attribute.
     *
     * @return Returns the tempListLookupMode.
     */
    public int getTempListLookupMode() {
        return tempListLookupMode;
    }

    /**
     * Sets the tempListLookupMode attribute value.
     *
     * @param tempListLookupMode The tempListLookupMode to set.
     */
    public void setTempListLookupMode(int tempListLookupMode) {
        this.tempListLookupMode = tempListLookupMode;
    }

    /**
     * Gets the messages attribute.
     *
     * @return Returns the messages.
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Sets the messages attribute value.
     *
     * @param messages The messages to set.
     */
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * Adds a message to the form message list.
     *
     * @param message - message text to add
     */
    public void addMessage(String message) {
        if (getMessages() == null) {
            setMessages(new ArrayList<String>());
        }
        getMessages().add(message);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the showSalaryByPositionAction attribute.
     *
     * @return Returns the showSalaryByPositionAction.
     */
    public boolean isShowSalaryByPositionAction() {
        return showSalaryByPositionAction;
    }

    /**
     * Sets the showSalaryByPositionAction attribute value.
     *
     * @param showSalaryByPositionAction The showSalaryByPositionAction to set.
     */
    public void setShowSalaryByPositionAction(boolean showSalaryByPositionAction) {
        this.showSalaryByPositionAction = showSalaryByPositionAction;
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
     * Gets the showSalaryByIncumbentAction attribute.
     *
     * @return Returns the showSalaryByIncumbentAction.
     */
    public boolean isShowSalaryByIncumbentAction() {
        return showSalaryByIncumbentAction;
    }

    /**
     * Sets the showSalaryByIncumbentAction attribute value.
     *
     * @param showSalaryByIncumbentAction The showSalaryByIncumbentAction to set.
     */
    public void setShowSalaryByIncumbentAction(boolean showSalaryByIncumbentAction) {
        this.showSalaryByIncumbentAction = showSalaryByIncumbentAction;
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
     * gets finanical object code
     *
     * @return
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * sets financial object code
     *
     * @param financialObjectCode
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * gets financial subobject code
     *
     * @return
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * sets financial subobject code
     *
     * @param financialSubObjectCode
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the mainWindow attribute.
     * @return Returns the mainWindow.
     */
    public boolean isMainWindow() {
        return mainWindow;
    }

    /**
     * Sets the mainWindow attribute value.
     * @param mainWindow The mainWindow to set.
     */
    public void setMainWindow(boolean mainWindow) {
        this.mainWindow = mainWindow;
    }

}

