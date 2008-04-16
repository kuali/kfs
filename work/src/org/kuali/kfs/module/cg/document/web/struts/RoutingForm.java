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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.HeaderNavigation;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.web.format.PhoneNumberFormatter;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

public class RoutingForm extends ResearchDocumentFormBase {

    private boolean auditActivated;
    private int numAuditErrors;

    // Main Page
    private RoutingFormPersonnel newRoutingFormProjectDirector;
    private RoutingFormPersonnel newRoutingFormOtherPerson;

    private RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent;

    // Project Details
    private RoutingFormInstitutionCostShare newRoutingFormInstitutionCostShare;
    private RoutingFormOtherCostShare newRoutingFormOtherCostShare;
    private RoutingFormSubcontractor newRoutingFormSubcontractor;
    private RoutingFormOrganization newRoutingFormOrganization;

    // Module Links
    private String[] selectedBudgetPeriods;
    private boolean allPeriodsSelected;
    private List<BudgetOverviewFormHelper> periodBudgetOverviewFormHelpers;
    private BudgetOverviewFormHelper summaryBudgetOverviewFormHelper;

    // Template properties
    private boolean templateAddress;
    private boolean templateAdHocPermissions;
    private boolean templateAdHocApprovers;

    // Approvals
    private String approvalsMessage;

    private Map systemParametersMap;

    /**
     * Used to indicate which result set we're using when refreshing/returning from a multi-value lookup
     */
    private String lookupResultsSequenceNumber;
    /**
     * The type of result returned by the multi-value lookup TODO: to be persisted in the lookup results service instead? See
     * KULRNE/Using+multiple+value+lookups
     */
    private String lookupResultsBOClassName;

    public RoutingForm() {
        super();

        DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.module.kra.routingform.document.RoutingFormDocument.class.getName());
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderNavigationList().toArray( new HeaderNavigation[] {} ));

        setDocument(new RoutingFormDocument());

        periodBudgetOverviewFormHelpers = new ArrayList();

        setFormatterType("document.routingFormPersonnel.personPhoneNumber", PhoneNumberFormatter.class);
        setFormatterType("document.routingFormPersonnel.personFaxNumber", PhoneNumberFormatter.class);
    }

    @Override
    public ResearchDocument getResearchDocument() {
        return this.getRoutingFormDocument();
    }

    public RoutingFormDocument getRoutingFormDocument() {
        return (RoutingFormDocument) this.getDocument();
    }

    public boolean isAuditActivated() {
        return auditActivated;
    }

    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    public int getNumAuditErrors() {
        return numAuditErrors;
    }

    public void setNumAuditErrors(int numAuditErrors) {
        this.numAuditErrors = numAuditErrors;
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

    public RoutingFormPersonnel getNewRoutingFormProjectDirector() {
        return newRoutingFormProjectDirector;
    }

    public void setNewRoutingFormProjectDirector(RoutingFormPersonnel newRoutingFormProjectDirector) {
        this.newRoutingFormProjectDirector = newRoutingFormProjectDirector;
    }

    public RoutingFormPersonnel getNewRoutingFormOtherPerson() {
        return newRoutingFormOtherPerson;
    }

    public void setNewRoutingFormOtherPerson(RoutingFormPersonnel newRoutingFormOtherPerson) {
        this.newRoutingFormOtherPerson = newRoutingFormOtherPerson;
    }

    public RoutingFormOrganizationCreditPercent getNewRoutingFormOrganizationCreditPercent() {
        return newRoutingFormOrganizationCreditPercent;
    }

    public void setNewRoutingFormOrganizationCreditPercent(RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent) {
        this.newRoutingFormOrganizationCreditPercent = newRoutingFormOrganizationCreditPercent;
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

    public String getApprovalsMessage() {
        return approvalsMessage;
    }

    public void setApprovalsMessage(String approvalsMessage) {
        this.approvalsMessage = approvalsMessage;
    }

    /**
     * Gets the two column size of routingFormProjectTypes, zero based. The result will be rounded up so that the left column has an
     * additional element for odd sized lists.
     * 
     * @return half size of routingFormProjectTypes, rounded up, zero based
     */
    public int getRoutingFormProjectTypesSizeByTwoColumns() {
        return new Double(Math.ceil(getRoutingFormDocument().getRoutingFormProjectTypes().size() / 2.0)).intValue() - 1;
    }

    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * Lazy gets the systemParametersMap attribute. Use it on a jsp or tag as such:
     * ${KualiForm.systemParametersMap[KraConstants.foobar]}
     * 
     * @return Returns the systemParametersMap.
     */
    public Map getSystemParametersMap() {
        if (systemParametersMap == null) {
            systemParametersMap = new HashMap();

            ParameterService parameterService = SpringContext.getBean(ParameterService.class);
            systemParametersMap.put(KraConstants.SUBMISSION_TYPE_CHANGE, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.SUBMISSION_TYPE_CHANGE));
            systemParametersMap.put(KraConstants.PROJECT_TYPE_OTHER, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_OTHER));
            systemParametersMap.put(KraConstants.PURPOSE_RESEARCH, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PURPOSE_RESEARCH));
            systemParametersMap.put(KraConstants.PURPOSE_OTHER, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PURPOSE_OTHER));

            systemParametersMap.put(KraConstants.CO_PROJECT_DIRECTOR_PARAM, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.CO_PROJECT_DIRECTOR_PARAM));
            systemParametersMap.put(KraConstants.PROJECT_DIRECTOR_PARAM, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_DIRECTOR_PARAM));
            systemParametersMap.put(KraConstants.OTHER_PERSON_PARAM, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.OTHER_PERSON_PARAM));
            systemParametersMap.put(KraConstants.CONTACT_PERSON_PARAM, parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.CONTACT_PERSON_PARAM));
        }

        return systemParametersMap;
    }
}
