/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReasonCodePick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSubFundPick;

/**
 * Struts Action Form for the Organization Report Selection Screen.
 */
public class OrganizationReportSelectionForm extends BudgetExpansionForm {
    private String reportMode;
    private boolean reportConsolidation;
    private String currentPointOfViewKeyCode;

    private String operatingModeTitle;

    private List<BudgetConstructionSubFundPick> subFundPickList;
    private List<BudgetConstructionObjectPick> objectCodePickList;
    private List<BudgetConstructionReasonCodePick> reasonCodePickList;

    private BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings;


    /**
     * Constructs a OrganizationReportSelectionForm.java
     */
    public OrganizationReportSelectionForm() {
        super();

        subFundPickList = new ArrayList<BudgetConstructionSubFundPick>();
        objectCodePickList = new ArrayList<BudgetConstructionObjectPick>();
        reasonCodePickList = new ArrayList<BudgetConstructionReasonCodePick>();

        budgetConstructionReportThresholdSettings = new BudgetConstructionReportThresholdSettings();
    }

    /**
     * Gets the subFundPickList
     *
     * @return Returns the subFundPickList
     */
    public List<BudgetConstructionSubFundPick> getSubFundPickList() {
        return subFundPickList;
    }

    /**
     * Sets the subFundPickList
     *
     * @param subFundPickList The subFundPickList to set.
     */
    public void setSubFundPickList(List<BudgetConstructionSubFundPick> bcSubFunds) {
        this.subFundPickList = bcSubFunds;
    }

    /**
     * Gets the objectCodePickList attribute.
     *
     * @return Returns the objectCodePickList.
     */
    public List<BudgetConstructionObjectPick> getObjectCodePickList() {
        return objectCodePickList;
    }

    /**
     * Sets the objectCodePickList attribute value.
     *
     * @param objectCodePickList The objectCodePickList to set.
     */
    public void setObjectCodePickList(List<BudgetConstructionObjectPick> objectCodePickList) {
        this.objectCodePickList = objectCodePickList;
    }

    /**
     * Gets the reasonCodePickList attribute.
     *
     * @return Returns the reasonCodePickList.
     */
    public List<BudgetConstructionReasonCodePick> getReasonCodePickList() {
        return reasonCodePickList;
    }

    /**
     * Sets the reasonCodePickList attribute value.
     *
     * @param reasonCodePickList The reasonCodePickList to set.
     */
    public void setReasonCodePickList(List<BudgetConstructionReasonCodePick> reasonCodePickList) {
        this.reasonCodePickList = reasonCodePickList;
    }

    /**
     * Gets the operatingModeTitle
     *
     * @return Returns the operatingModeTitle
     */
    public String getOperatingModeTitle() {
        return operatingModeTitle;
    }

    /**
     * Sets the operatingModeTitle
     *
     * @param operatingModeTitle The operatingModeTitle to set.
     */
    public void setOperatingModeTitle(String operatingModeTitle) {
        this.operatingModeTitle = operatingModeTitle;
    }

    /**
     * Gets the reportConsolidation
     *
     * @return Returns the reportConsolidation
     */
    public boolean isReportConsolidation() {
        return reportConsolidation;
    }

    /**
     * Sets the reportConsolidation
     *
     * @param reportConsolidation The reportConsolidation to set.
     */
    public void setReportConsolidation(boolean reportConsolidation) {
        this.reportConsolidation = reportConsolidation;
    }

    /**
     * Gets the reportMode
     *
     * @return Returns the reportMode
     */
    public String getReportMode() {
        return reportMode;
    }

    /**
     * Sets the reportMode
     *
     * @param reportMode The reportMode to set.
     */
    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
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
     * Sets the currentPointOfViewKeyCode attribute value.
     *
     * @param currentPointOfViewKeyCode The currentPointOfViewKeyCode to set.
     */
    public void setCurrentPointOfViewKeyCode(String currentPointOfViewKeyCode) {
        this.currentPointOfViewKeyCode = currentPointOfViewKeyCode;
    }

    /**
     * Gets the budgetConstructionReportThresholdSettings attribute.
     *
     * @return Returns the budgetConstructionReportThresholdSettings.
     */
    public BudgetConstructionReportThresholdSettings getBudgetConstructionReportThresholdSettings() {
        return budgetConstructionReportThresholdSettings;
    }

    /**
     * Sets the budgetConstructionReportThresholdSettings attribute value.
     *
     * @param budgetConstructionReportThresholdSettings The budgetConstructionReportThresholdSettings to set.
     */
    public void setBudgetConstructionReportThresholdSettings(BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        this.budgetConstructionReportThresholdSettings = budgetConstructionReportThresholdSettings;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        super.reset(mapping, request);
        for (BudgetConstructionObjectPick budgetConstructionObjectPick : objectCodePickList){
            budgetConstructionObjectPick.setSelectFlag(0);
        }
        for (BudgetConstructionSubFundPick budgetConstructionSubFundPick : subFundPickList){
            budgetConstructionSubFundPick.setReportFlag(0);
        }
        for (BudgetConstructionReasonCodePick budgetConstructionReasonCodePick : reasonCodePickList){
            budgetConstructionReasonCodePick.setSelectFlag(0);
        }
        budgetConstructionReportThresholdSettings.setUseThreshold(false);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#shouldPropertyBePopulatedInForm(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {

        if (requestParameterName.startsWith(BCPropertyConstants.OBJECT_CODE_PICK_LIST)) {
            return true;
        }
        return super.shouldPropertyBePopulatedInForm(requestParameterName, request);
    }

}
