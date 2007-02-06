/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.bo.ProjectType;
import org.kuali.module.kra.routingform.bo.Purpose;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocOrg;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocPerson;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocWorkgroup;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.bo.SubmissionType;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

public class RoutingForm extends ResearchDocumentFormBase {
    
    private boolean auditActivated;
    
    //Main Page
    private RoutingFormKeyword newRoutingFormKeyword;
    private RoutingFormPersonnel newRoutingFormPersonnel;
    private RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent;
    private List<String> selectedRoutingFormProjectTypes;
    
    private List<SubmissionType> submissionTypes;
    private List<ProjectType> projectTypes;
    private List<Purpose> purposes;
    
    //Project Details 
    private RoutingFormInstitutionCostShare newRoutingFormInstitutionCostShare;
    private RoutingFormOtherCostShare newRoutingFormOtherCostShare;
    private RoutingFormSubcontractor newRoutingFormSubcontractor;
    private RoutingFormOrganization newRoutingFormOrganization;
    
    //Module Links
    private String[] selectedBudgetPeriods;
    private boolean allPeriodsSelected;
    private List<BudgetOverviewFormHelper> periodBudgetOverviewFormHelpers;
    private BudgetOverviewFormHelper summaryBudgetOverviewFormHelper;
    
    //Template properties
    private boolean templateAddress;
    private boolean templateAdHocPermissions;
    private boolean templateAdHocApprovers;
    
    //Permissions
    private RoutingFormAdHocPerson newAdHocPerson;
    private RoutingFormAdHocOrg newAdHocOrg;
    private RoutingFormAdHocWorkgroup newAdHocWorkgroup;
    
    public RoutingForm() {
        super();
       
        selectedRoutingFormProjectTypes = new ArrayList();
        submissionTypes = new ArrayList();
        projectTypes = new ArrayList();
        purposes = new ArrayList();
        
        DataDictionary dataDictionary = SpringServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.module.kra.routingform.document.RoutingFormDocument.class);
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderTabNavigation());
        
        setDocument(new RoutingFormDocument());
        
        periodBudgetOverviewFormHelpers = new ArrayList();
    }
    
    @Override
    public ResearchDocument getResearchDocument(){
        return this.getRoutingFormDocument();
    }
    
    public RoutingFormDocument getRoutingFormDocument(){
        return (RoutingFormDocument)this.getDocument();
    }
    
    public boolean isAuditActivated() {
        return auditActivated;
    }

    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    public RoutingFormKeyword getNewRoutingFormKeyword() {
        return newRoutingFormKeyword;
    }

    public void setNewRoutingFormKeyword(RoutingFormKeyword newRoutingFormKeyword) {
        this.newRoutingFormKeyword = newRoutingFormKeyword;
    }

    public void setNewRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare newRoutingFormInstitutionCostShare) {
        this.newRoutingFormInstitutionCostShare = newRoutingFormInstitutionCostShare;
    }

    public RoutingFormInstitutionCostShare getNewRoutingFormInstitutionCostShare() {
        return newRoutingFormInstitutionCostShare;
    }

    public RoutingFormOtherCostShare getNewRoutingFormOtherCostShare() {
        return newRoutingFormOtherCostShare;
    }

    public void setNewRoutingFormOtherCostShare(RoutingFormOtherCostShare newRoutingFormOtherCostShare) {
        this.newRoutingFormOtherCostShare = newRoutingFormOtherCostShare;
    }

    public RoutingFormSubcontractor getNewRoutingFormSubcontractor() {
        return newRoutingFormSubcontractor;
    }

    public void setNewRoutingFormSubcontractor(RoutingFormSubcontractor newRoutingFormSubcontractor) {
        this.newRoutingFormSubcontractor = newRoutingFormSubcontractor;
    }

    public RoutingFormPersonnel getNewRoutingFormPersonnel() {
        return newRoutingFormPersonnel;
    }

    public void setNewRoutingFormPersonnel(RoutingFormPersonnel newRoutingFormPersonnel) {
        this.newRoutingFormPersonnel = newRoutingFormPersonnel;
    }

    public RoutingFormOrganizationCreditPercent getNewRoutingFormOrganizationCreditPercent() {
        return newRoutingFormOrganizationCreditPercent;
    }

    public void setNewRoutingFormOrganizationCreditPercent(RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent) {
        this.newRoutingFormOrganizationCreditPercent = newRoutingFormOrganizationCreditPercent;
    }

    public String[] getSelectedRoutingFormProjectTypes() {
        return selectedRoutingFormProjectTypes.toArray(new String[selectedRoutingFormProjectTypes.size()]);
    }
    
    /**
     * This is a work around for a problem with html:multibox. See KULERA-835 for details. Essentially it appears that
     * in Kuali html:multibox doesn't handle string arrays correctly. It only handles the first element of a string array.
     * @param projectTypeCode
     * @return
     */
    public String[] getSelectedRoutingFormProjectTypesMultiboxFix(String projectTypeCode) {
        List<RoutingFormProjectType> routingFormProjectTypes = this.getRoutingFormDocument().getRoutingFormProjectTypes();
        
        for(RoutingFormProjectType routingFormProjectType : routingFormProjectTypes) {
            if (routingFormProjectType.getProjectTypeCode().equals(projectTypeCode)) {
                return new String[] {projectTypeCode};
            }
        }
        
        // don't pass String[0], JSPs don't like that (exception)
        return new String[] {""};
    }

    /**
     * @see org.kuali.module.kra.routingform.web.struts.form.RoutingForm#getSelectedRoutingFormProjectTypesMultiboxFix(String)
     */
    public void setSelectedRoutingFormProjectTypesMultiboxFix(String trash, String[] selectedRoutingFormProjectTypes) {
        this.selectedRoutingFormProjectTypes.add(selectedRoutingFormProjectTypes[0]);
    }

    public List<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public ProjectType getProjectType(int index) {
        while (this.getProjectTypes().size() <= index) {
            this.getProjectTypes().add(new ProjectType());
        }
        return this.getProjectTypes().get(index);
    }
    
    public void setProjectTypes(List<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }
    
    public List<Purpose> getPurposes() {
        return purposes;
    }
    
    public Purpose getPurpose(int index) {
        while (this.getPurposes().size() <= index) {
            this.getPurposes().add(new Purpose());
        }
        return this.getPurposes().get(index);
    }
    
    public void setPurposes(List<Purpose> purposes) {
        this.purposes = purposes;
    }
    
    public List<SubmissionType> getSubmissionTypes() {
        return submissionTypes;
    }

    public SubmissionType getSubmissionType(int index) {
        while (this.getSubmissionTypes().size() <= index) {
            this.getSubmissionTypes().add(new SubmissionType());
        }
        return this.getSubmissionTypes().get(index);
    }
    
    public void setSubmissionTypes(List<SubmissionType> submissionTypes) {
        this.submissionTypes = submissionTypes;
    }
    
    public RoutingFormOrganization getNewRoutingFormOrganization() {
        return newRoutingFormOrganization;
    }

    public void setNewRoutingFormOrganization(RoutingFormOrganization newRotuingFormOrganization) {
        this.newRoutingFormOrganization = newRotuingFormOrganization;
    }

    public boolean isTemplateAddress() {
        return templateAddress;
    }

    public void setTemplateAddress(boolean templateAddress) {
        this.templateAddress = templateAddress;
    }

    public boolean isTemplateAdHocApprovers() {
        return templateAdHocApprovers;
    }

    public void setTemplateAdHocApprovers(boolean templateAdHocApprovers) {
        this.templateAdHocApprovers = templateAdHocApprovers;
    }

    public boolean isTemplateAdHocPermissions() {
        return templateAdHocPermissions;
    }

    public void setTemplateAdHocPermissions(boolean templateAdHocPermissions) {
        this.templateAdHocPermissions = templateAdHocPermissions;
    }

    public List<BudgetOverviewFormHelper> getPeriodBudgetOverviewFormHelpers() {
        return periodBudgetOverviewFormHelpers;
    }

    public void setPeriodBudgetOverviewFormHelpers(List<BudgetOverviewFormHelper> periodBudgetOverviewFormHelpers) {
        this.periodBudgetOverviewFormHelpers = periodBudgetOverviewFormHelpers;
    }

    public BudgetOverviewFormHelper getPeriodBudgetOverviewFormHelper(int index) {
        while (this.getPeriodBudgetOverviewFormHelpers().size() <= index) {
            this.getPeriodBudgetOverviewFormHelpers().add(new BudgetOverviewFormHelper());
        }
        return this.getPeriodBudgetOverviewFormHelpers().get(index);
    }

    public BudgetOverviewFormHelper getSummaryBudgetOverviewFormHelper() {
        return summaryBudgetOverviewFormHelper;
    }

    public void setSummaryBudgetOverviewFormHelper(BudgetOverviewFormHelper summaryBudgetOverviewFormHelper) {
        this.summaryBudgetOverviewFormHelper = summaryBudgetOverviewFormHelper;
    }

    public boolean getAllPeriodsSelected() {
        return allPeriodsSelected;
    }

    public void setAllPeriodsSelected(boolean allPeriodsSelected) {
        this.allPeriodsSelected = allPeriodsSelected;
    }
    
    /**
     * Gets the newAdHocPerson attribute. 
     * @return Returns the newAdHocPerson.
     */
    public RoutingFormAdHocPerson getNewAdHocPerson() {
        return newAdHocPerson;
    }

    /**
     * Sets the newAdHocPerson attribute value.
     * @param newAdHocPerson The newAdHocPerson to set.
     */
    public void setNewAdHocPerson(RoutingFormAdHocPerson newAdHocPerson) {
        this.newAdHocPerson = newAdHocPerson;
    }

    /**
     * Gets the newAdHocOrg attribute. 
     * @return Returns the newAdHocOrg.
     */
    public RoutingFormAdHocOrg getNewAdHocOrg() {
        return newAdHocOrg;
    }

    /**
     * Sets the newAdHocOrg attribute value.
     * @param newAdHocOrg The newAdHocOrg to set.
     */
    public void setNewAdHocOrg(RoutingFormAdHocOrg newAdHocOrg) {
        this.newAdHocOrg = newAdHocOrg;
    }

    /**
     * Gets the newAdHocWorkgroup attribute. 
     * @return Returns the newAdHocWorkgroup.
     */
    public RoutingFormAdHocWorkgroup getNewAdHocWorkgroup() {
        return newAdHocWorkgroup;
    }

    /**
     * Sets the newAdHocWorkgroup attribute value.
     * @param newAdHocWorkgroup The newAdHocWorkgroup to set.
     */
    public void setNewAdHocWorkgroup(RoutingFormAdHocWorkgroup newAdHocWorkgroup) {
        this.newAdHocWorkgroup = newAdHocWorkgroup;
    }
}