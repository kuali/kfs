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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.PersistenceService;

/**
 * ActionForm that supports the Organization Selection Tree page
 */
public class OrganizationSelectionTreeForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSelectionTreeForm.class);

    private BudgetConstructionOrganizationReports pointOfViewOrg;
    private List<BudgetConstructionPullup> selectionSubTreeOrgs;
    private List<BudgetConstructionPullup> previousBranchOrgs;
    private boolean hideDetails = false;
    private String operatingModeTitle;
    private String operatingModePullFlagLabel;

    private String currentPointOfViewKeyCode;
    private String previousPointOfViewKeyCode;
    private List<KeyValue> pullFlagKeyLabels;

    // passed parms
    private String operatingMode;

    private boolean accountSummaryConsolidation;
    private boolean accountObjectDetailConsolidation;
    private boolean monthObjectSummaryConsolidation;

    private String reportMode;

    // used to flag reset() not to reset check box values when returning from child expansion screen
    private boolean noResetOnReturn = false;

    /**
     * Constructs a OrganizationSelectionTreeForm.java.
     */
    public OrganizationSelectionTreeForm() {
        super();
        this.setPointOfViewOrg(new BudgetConstructionOrganizationReports());
        this.setSelectionSubTreeOrgs(new ArrayList<BudgetConstructionPullup>());
        this.setPreviousBranchOrgs(new ArrayList<BudgetConstructionPullup>());
        this.setPullFlagKeyLabels(new ArrayList<KeyValue>());

    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        if (!this.isLostSessionDetected(request)){
            super.populate(request);
            populatePreviousBranchOrgs();
            populateSelectionSubTreeOrgs();
        }
    }

    public void populateSelectionSubTreeOrgs() {

        Iterator<BudgetConstructionPullup> selectionOrgs = getSelectionSubTreeOrgs().iterator();
        while (selectionOrgs.hasNext()) {
            BudgetConstructionPullup selectionOrg = (BudgetConstructionPullup) selectionOrgs.next();

            final List<String> REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "organization" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(selectionOrg, REFRESH_FIELDS);
        }

    }

    public void populatePreviousBranchOrgs() {

        Iterator<BudgetConstructionPullup> previousBranchOrgs = getPreviousBranchOrgs().iterator();
        while (previousBranchOrgs.hasNext()) {
            BudgetConstructionPullup previousBranchOrg = (BudgetConstructionPullup) previousBranchOrgs.next();
            final List<String> REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "organization" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(previousBranchOrg, REFRESH_FIELDS);
        }

    }

    /**
     * Gets the pointOfViewOrg attribute.
     * 
     * @return Returns the pointOfViewOrg.
     */
    public BudgetConstructionOrganizationReports getPointOfViewOrg() {
        return pointOfViewOrg;
    }

    /**
     * Sets the pointOfViewOrg attribute value.
     * 
     * @param pointOfViewOrg The pointOfViewOrg to set.
     */
    public void setPointOfViewOrg(BudgetConstructionOrganizationReports pointOfViewOrg) {
        this.pointOfViewOrg = pointOfViewOrg;
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
     * Gets the operatingMode attribute.
     * 
     * @return Returns the operatingMode.
     */
    public String getOperatingMode() {
        return operatingMode;
    }

    /**
     * Sets the operatingMode attribute value.
     * 
     * @param operatingMode The operatingMode to set.
     */
    public void setOperatingMode(String operatingMode) {
        this.operatingMode = operatingMode;
    }

    /**
     * Gets the operatingModeTitle attribute.
     * 
     * @return Returns the operatingModeTitle.
     */
    public String getOperatingModeTitle() {
        return operatingModeTitle;
    }

    /**
     * Sets the operatingModeTitle attribute value.
     * 
     * @param operatingModeTitle The operatingModeTitle to set.
     */
    public void setOperatingModeTitle(String operatingModeTitle) {
        this.operatingModeTitle = operatingModeTitle;
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
     * Gets the previousPointOfViewKeyCode attribute.
     * 
     * @return Returns the previousPointOfViewKeyCode.
     */
    public String getPreviousPointOfViewKeyCode() {
        return previousPointOfViewKeyCode;
    }

    /**
     * Sets the previousPointOfViewKeyCode attribute value.
     * 
     * @param previousPointOfViewKeyCode The previousPointOfViewKeyCode to set.
     */
    public void setPreviousPointOfViewKeyCode(String previousPointOfViewKeyCode) {
        this.previousPointOfViewKeyCode = previousPointOfViewKeyCode;
    }

    /**
     * Gets the selectionSubTree attribute.
     * 
     * @return Returns the selectionSubTree.
     */
    public List<BudgetConstructionPullup> getSelectionSubTreeOrgs() {
        return selectionSubTreeOrgs;
    }

    /**
     * Sets the selectionSubTree attribute value.
     * 
     * @param selectionSubTree The selectionSubTree to set.
     */
    public void setSelectionSubTreeOrgs(List<BudgetConstructionPullup> selectionSubTree) {
        this.selectionSubTreeOrgs = selectionSubTree;
    }

    /**
     * Gets the previousBranchOrgs attribute.
     * 
     * @return Returns the previousBranchOrgs.
     */
    public List<BudgetConstructionPullup> getPreviousBranchOrgs() {
        return previousBranchOrgs;
    }

    /**
     * Sets the previousBranchOrgs attribute value.
     * 
     * @param previousBranchOrgs The previousBranchOrgs to set.
     */
    public void setPreviousBranchOrgs(List<BudgetConstructionPullup> previousBranchOrgs) {
        this.previousBranchOrgs = previousBranchOrgs;
    }

    /**
     * Gets the pullFlagKeyLabels attribute.
     * 
     * @return Returns the pullFlagKeyLabels.
     */
    public List<KeyValue> getPullFlagKeyLabels() {
        return pullFlagKeyLabels;
    }

    /**
     * Sets the pullFlagKeyLabels attribute value.
     * 
     * @param pullFlagKeyLabels The pullFlagKeyLabels to set.
     */
    public void setPullFlagKeyLabels(List<KeyValue> pullFlagKeyLabels) {
        this.pullFlagKeyLabels = pullFlagKeyLabels;
    }

    /**
     * Gets the operatingModePullFlagLabel attribute.
     * 
     * @return Returns the operatingModePullFlagLabel.
     */
    public String getOperatingModePullFlagLabel() {
        return operatingModePullFlagLabel;
    }

    /**
     * Sets the operatingModePullFlagLabel attribute value.
     * 
     * @param operatingModePullFlagLabel The operatingModePullFlagLabel to set.
     */
    public void setOperatingModePullFlagLabel(String operatingModePullFlagLabel) {
        this.operatingModePullFlagLabel = operatingModePullFlagLabel;
    }

    public boolean isAccountSummaryConsolidation() {
        return accountSummaryConsolidation;
    }

    public void setAccountSummaryConsolidation(boolean accountSummaryConsolidation) {
        this.accountSummaryConsolidation = accountSummaryConsolidation;
    }

    public String getReportMode() {
        return reportMode;
    }

    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }

    public boolean isAccountObjectDetailConsolidation() {
        return accountObjectDetailConsolidation;
    }

    public void setAccountObjectDetailConsolidation(boolean accountObjectDetailConsolidation) {
        this.accountObjectDetailConsolidation = accountObjectDetailConsolidation;
    }

    public boolean isMonthObjectSummaryConsolidation() {
        return monthObjectSummaryConsolidation;
    }

    public void setMonthObjectSummaryConsolidation(boolean monthObjectSummaryConsolidation) {
        this.monthObjectSummaryConsolidation = monthObjectSummaryConsolidation;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {

        if (methodToCallParameterValue.equalsIgnoreCase("performBuildPointOfView")) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest) resets check box fields if not returning from a child expansion screen
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        if (this.isNoResetOnReturn()) {
            this.setNoResetOnReturn(false);
        }
        else {
            super.reset(mapping, request);
            for (BudgetConstructionPullup selectionSubTreeOrg : selectionSubTreeOrgs) {
                selectionSubTreeOrg.setPullFlag(0);
            }
            this.setAccountSummaryConsolidation(false);
            this.setAccountObjectDetailConsolidation(false);
            this.setMonthObjectSummaryConsolidation(false);
        }

        this.getMessages().clear();
    }

    /**
     * Gets the noResetOnReturn attribute.
     * 
     * @return Returns the noResetOnReturn.
     */
    public boolean isNoResetOnReturn() {
        return noResetOnReturn;
    }

    /**
     * Sets the noResetOnReturn attribute value.
     * 
     * @param noResetOnReturn The noResetOnReturn to set.
     */
    public void setNoResetOnReturn(boolean noResetOnReturn) {
        this.noResetOnReturn = noResetOnReturn;
    }

}
