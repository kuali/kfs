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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

    @Override
    public void populate(HttpServletRequest request) {

        if (!this.isLostSessionDetected(request)){
            super.populate(request);
        }
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
}
