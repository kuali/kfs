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
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSelect;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;

public class OrganizationReportSelectionForm extends KualiForm {
    
    private List<BudgetConstructionSubFundPick> bcSubfundList = new ArrayList();
    private List<String> selectedSubfundGroupCode = new ArrayList();  
    private String operatingModeTitle;
    private Integer universityFiscalYear; 
    private String backLocation;
    private String returnAnchor;
    private String docFormKey;
    private String operatingMode;
    private String accSumConsolidation;
    

    @Override
    public void populate(HttpServletRequest request) {
        
        super.populate(request);
        
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        
        bcSubfundList = (List) SpringContext.getBean(BudgetConstructionOrganizationReportsService.class).getBySearchCriteria(BudgetConstructionSubFundPick.class, searchCriteria);
    }

    public OrganizationReportSelectionForm(){
        super();
        setOperatingModeTitle(BCConstants.Report.SELECTION_OPMODE_TITLE);
    }

    /**
     * Gets the bcSubfundList
     * 
     * @return Returns the bcSubfundList
     */
    
    public List<BudgetConstructionSubFundPick> getBcSubfundList() {
        return bcSubfundList;
    }

    /**
     * Sets the bcSubfundList
     * 
     * @param bcSubfundList The bcSubfundList to set.
     */
    public void setBcSubfundList(List<BudgetConstructionSubFundPick> bcSubfundList) {
        this.bcSubfundList = bcSubfundList;
    }

    /**
     * Gets the selectedSubfundGroupCode
     * 
     * @return Returns the selectedSubfundGroupCode
     */
    public List<String> getSelectedSubfundGroupCode() {
        return selectedSubfundGroupCode;
    }

    /**
     * Sets the selectedSubfundGroupCode
     * 
     * @param selectedSubfundGroupCode The selectedSubfundGroupCode to set.
     */
    public void setSelectedSubfundGroupCode(List<String> selectedSubfundGroupCode) {
        this.selectedSubfundGroupCode = selectedSubfundGroupCode;
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
     * Gets the operatingMode
     * 
     * @return Returns the operatingMode
     */
    public String getOperatingMode() {
        return operatingMode;
    }

    /**
     * Sets the operatingMode
     * 
     * @param operatingMode The operatingMode to set.
     */
    public void setOperatingMode(String operatingMode) {
        this.operatingMode = operatingMode;
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

    /**
     * Gets the accSumConsolidation
     * 
     * @return Returns the accSumConsolidation
     */
    public String getAccSumConsolidation() {
        return accSumConsolidation;
    }

    /**
     * Sets the accSumConsolidation
     * 
     * @param accSumConsolidation The accSumConsolidation to set.
     */
    public void setAccSumConsolidation(String accSumConsolidation) {
        this.accSumConsolidation = accSumConsolidation;
    }


  
}
