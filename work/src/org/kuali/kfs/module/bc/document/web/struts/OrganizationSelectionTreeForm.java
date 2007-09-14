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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants.OrgSelControlOption;
import org.kuali.module.budget.BCConstants.OrgSelOpMode;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.bo.BudgetConstructionPullup;

/**
 * This class...
 */
public class OrganizationSelectionTreeForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSelectionTreeForm.class);
    
    private BudgetConstructionOrganizationReports pointOfViewOrg;
    private List <BudgetConstructionPullup> selectionSubTreeOrgs; 
    private List <BudgetConstructionPullup> previousBranchOrgs; 
    private boolean hideDetails = false;
    private String operatingModeTitle;
    private String operatingModePullFlagLabel;

    private String currentPointOfViewKeyCode;
    private String previousPointOfViewKeyCode;
    private List pullFlagKeyLabels;

    //passed parms
    private String returnAnchor;
    private String returnFormKey;
    private String operatingMode;

    //holds the BC fiscal year that is currently active 
    private Integer universityFiscalYear;

    /**
     * Constructs a OrganizationSelectionTreeForm.java.
     */
    public OrganizationSelectionTreeForm() {
        super();
        this.setPointOfViewOrg(new BudgetConstructionOrganizationReports());
        this.setSelectionSubTreeOrgs(new TypedArrayList(BudgetConstructionPullup.class));
        this.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));
        this.setPullFlagKeyLabels(new TypedArrayList(KeyLabelPair.class));
        
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);

        OrgSelOpMode opMode = OrgSelOpMode.valueOf(getOperatingMode());  
        switch (opMode){
            case SALSET:
                setOperatingModeTitle("Budget Salary Setting Organization Selection");
                setOperatingModePullFlagLabel("Selected");
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.NO.getKey(), OrgSelControlOption.NO.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.YES.getKey(), OrgSelControlOption.YES.getLabel()));
                break;
            case REPORTS:
                setOperatingModeTitle("BC Reports Organization Selection");
                setOperatingModePullFlagLabel("Selected");
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.NO.getKey(), OrgSelControlOption.NO.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.YES.getKey(), OrgSelControlOption.YES.getLabel()));
                break;
            case PULLUP:
                setOperatingModeTitle("BC Pull Up Organization Selection");
                setOperatingModePullFlagLabel("Pull Up Type");
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.NOTSEL.getKey(), OrgSelControlOption.NOTSEL.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.ORG.getKey(), OrgSelControlOption.ORG.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.SUBORG.getKey(), OrgSelControlOption.SUBORG.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.BOTH.getKey(), OrgSelControlOption.BOTH.getLabel()));
                break;
            case PUSHDOWN:
                setOperatingModeTitle("BC Push Down Organization Selection");
                setOperatingModePullFlagLabel("Push Down Type");
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.NOTSEL.getKey(), OrgSelControlOption.NOTSEL.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.ORGLEV.getKey(), OrgSelControlOption.ORGLEV.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.MGRLEV.getKey(), OrgSelControlOption.MGRLEV.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.ORGMGRLEV.getKey(), OrgSelControlOption.ORGMGRLEV.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.LEVONE.getKey(), OrgSelControlOption.LEVONE.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.LEVZERO.getKey(), OrgSelControlOption.LEVZERO.getLabel()));
                break;
            default:
                // default to ACCOUNT operating mode
                setOperatingModeTitle("Budgeted Account List Search Organization Selection");
            setOperatingModePullFlagLabel("Selected");
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.NO.getKey(), OrgSelControlOption.NO.getLabel()));
                getPullFlagKeyLabels().add(new KeyLabelPair(OrgSelControlOption.YES.getKey(), OrgSelControlOption.YES.getLabel()));
                break;
        }

    }

    public void populateSelectionSubTreeOrgs(){
        
        Iterator selectionOrgs = getSelectionSubTreeOrgs().iterator();
        while (selectionOrgs.hasNext()){
            BudgetConstructionPullup selectionOrg = (BudgetConstructionPullup) selectionOrgs.next();
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "organization" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(selectionOrg, REFRESH_FIELDS);
        }
        
    }

    public void populatePreviousBranchOrgs(){
        
        Iterator previousBranchOrgs = getPreviousBranchOrgs().iterator();
        while (previousBranchOrgs.hasNext()){
            BudgetConstructionPullup previousBranchOrg = (BudgetConstructionPullup) previousBranchOrgs.next();
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "organization" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(previousBranchOrg, REFRESH_FIELDS);
        }
        
    }

    /**
     * Gets the pointOfViewOrg attribute. 
     * @return Returns the pointOfViewOrg.
     */
    public BudgetConstructionOrganizationReports getPointOfViewOrg() {
        return pointOfViewOrg;
    }

    /**
     * Sets the pointOfViewOrg attribute value.
     * @param pointOfViewOrg The pointOfViewOrg to set.
     */
    public void setPointOfViewOrg(BudgetConstructionOrganizationReports pointOfViewOrg) {
        this.pointOfViewOrg = pointOfViewOrg;
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
     * Gets the operatingMode attribute. 
     * @return Returns the operatingMode.
     */
    public String getOperatingMode() {
        return operatingMode;
    }

    /**
     * Sets the operatingMode attribute value.
     * @param operatingMode The operatingMode to set.
     */
    public void setOperatingMode(String operatingMode) {
        this.operatingMode = operatingMode;
    }

    /**
     * Gets the operatingModeTitle attribute. 
     * @return Returns the operatingModeTitle.
     */
    public String getOperatingModeTitle() {
        return operatingModeTitle;
    }

    /**
     * Sets the operatingModeTitle attribute value.
     * @param operatingModeTitle The operatingModeTitle to set.
     */
    public void setOperatingModeTitle(String operatingModeTitle) {
        this.operatingModeTitle = operatingModeTitle;
    }

    /**
     * Gets the currentPointOfViewKeyCode attribute. 
     * @return Returns the currentPointOfViewKeyCode.
     */
    public String getCurrentPointOfViewKeyCode() {
        return currentPointOfViewKeyCode;
    }

    /**
     * Sets the currentPointOfViewKeyCode attribute value.
     * @param currentPointOfViewKeyCode The currentPointOfViewKeyCode to set.
     */
    public void setCurrentPointOfViewKeyCode(String currentPointOfViewKeyCode) {
        this.currentPointOfViewKeyCode = currentPointOfViewKeyCode;
    }

    /**
     * Gets the previousPointOfViewKeyCode attribute. 
     * @return Returns the previousPointOfViewKeyCode.
     */
    public String getPreviousPointOfViewKeyCode() {
        return previousPointOfViewKeyCode;
    }

    /**
     * Sets the previousPointOfViewKeyCode attribute value.
     * @param previousPointOfViewKeyCode The previousPointOfViewKeyCode to set.
     */
    public void setPreviousPointOfViewKeyCode(String previousPointOfViewKeyCode) {
        this.previousPointOfViewKeyCode = previousPointOfViewKeyCode;
    }

    /**
     * Gets the selectionSubTree attribute. 
     * @return Returns the selectionSubTree.
     */
    public List<BudgetConstructionPullup> getSelectionSubTreeOrgs() {
        return selectionSubTreeOrgs;
    }

    /**
     * Sets the selectionSubTree attribute value.
     * @param selectionSubTree The selectionSubTree to set.
     */
    public void setSelectionSubTreeOrgs(List<BudgetConstructionPullup> selectionSubTree) {
        this.selectionSubTreeOrgs = selectionSubTree;
    }

    /**
     * Gets the previousBranchOrgs attribute. 
     * @return Returns the previousBranchOrgs.
     */
    public List<BudgetConstructionPullup> getPreviousBranchOrgs() {
        return previousBranchOrgs;
    }

    /**
     * Sets the previousBranchOrgs attribute value.
     * @param previousBranchOrgs The previousBranchOrgs to set.
     */
    public void setPreviousBranchOrgs(List<BudgetConstructionPullup> previousBranchOrgs) {
        this.previousBranchOrgs = previousBranchOrgs;
    }

    /**
     * Gets the pullFlagKeyLabels attribute. 
     * @return Returns the pullFlagKeyLabels.
     */
    public List getPullFlagKeyLabels() {
        return pullFlagKeyLabels;
    }

    /**
     * Sets the pullFlagKeyLabels attribute value.
     * @param pullFlagKeyLabels The pullFlagKeyLabels to set.
     */
    public void setPullFlagKeyLabels(List pullFlagKeyLabels) {
        this.pullFlagKeyLabels = pullFlagKeyLabels;
    }

    /**
     * Gets the operatingModePullFlagLabel attribute. 
     * @return Returns the operatingModePullFlagLabel.
     */
    public String getOperatingModePullFlagLabel() {
        return operatingModePullFlagLabel;
    }

    /**
     * Sets the operatingModePullFlagLabel attribute value.
     * @param operatingModePullFlagLabel The operatingModePullFlagLabel to set.
     */
    public void setOperatingModePullFlagLabel(String operatingModePullFlagLabel) {
        this.operatingModePullFlagLabel = operatingModePullFlagLabel;
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


}
