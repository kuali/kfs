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
package org.kuali.module.budget.web.struts.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.Guid;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetReportsControlListService;

/**
 * Struts Action Form for the Organization Report Selection Screen.
 */
public class OrganizationReportSelectionForm extends KualiForm {

    private List<BudgetConstructionSubFundPick> bcSubFunds = new TypedArrayList(BudgetConstructionSubFundPick.class);
    private String operatingModeTitle;
    private Integer universityFiscalYear;
    private String backLocation;
    private String returnAnchor;
    private String docFormKey;
    private String reportMode;
    private boolean buildControlList;
    private boolean reportConsolidation;
    private boolean refreshSubFundList;


    /**
     * Constructs a OrganizationReportSelectionForm.java
     */
    public OrganizationReportSelectionForm() {
        super();
    }

    /**
     * Gets the bcSubfundList
     * 
     * @return Returns the bcSubfundList
     */
    public List<BudgetConstructionSubFundPick> getBcSubFunds() {
        return bcSubFunds;
    }

    /**
     * Sets the bcSubfundList
     * 
     * @param bcSubfundList The bcSubfundList to set.
     */
    public void setBcSubFunds(List<BudgetConstructionSubFundPick> bcSubFunds) {
        this.bcSubFunds = bcSubFunds;
    }
    
    public BudgetConstructionSubFundPick getBcSubFund(int index) {
        while (getBcSubFunds().size() <= index) {
            getBcSubFunds().add(new BudgetConstructionSubFundPick());
        }

        return (BudgetConstructionSubFundPick) getBcSubFunds().get(index);
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
     * Gets the universityFiscalYear
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the backLocation
     * 
     * @return Returns the backLocation
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * Sets the backLocation
     * 
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }


    /**
     * Gets the returnAnchor
     * 
     * @return Returns the returnAnchor
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor
     * 
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the docFormKey
     * 
     * @return Returns the docFormKey
     */
    public String getDocFormKey() {
        return docFormKey;
    }

    /**
     * Sets the docFormKey
     * 
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        this.docFormKey = docFormKey;
    }

    public boolean isBuildControlList() {
        return buildControlList;
    }

    public void setBuildControlList(boolean buildControlList) {
        this.buildControlList = buildControlList;
    }

    public boolean isReportConsolidation() {
        return reportConsolidation;
    }

    public void setReportConsolidation(boolean reportConsolidation) {
        this.reportConsolidation = reportConsolidation;
    }

    public String getReportMode() {
        return reportMode;
    }

    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }

    public boolean isRefreshListFlag() {
        return refreshSubFundList;
    }

    public void setRefreshListFlag(boolean refreshSubFundList) {
        this.refreshSubFundList = refreshSubFundList;
    }


}
