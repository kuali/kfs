/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

/**
 * This class is the detail business object for Sub Account Import Global Maintenance Document
 */
public class ProjectCodeImportDetail extends MassImportLineBase {

    private static final Logger LOG = Logger.getLogger(ProjectCodeImportDetail.class);
    private String documentNumber;
    private String projectCode;
    private String projectName;
    private String projectDescription;
    private String projectManagerUniversalId;
    private String chartOfAccountsCode;
    private String organizationCode;
    private boolean active;

    private Person projectManagerUniversal;
    private Chart chartOfAccounts;
    private Organization organization;

    // transient
    private String projectManagerPrincipalName;



    /**
     * Default constructor.
     */
    public ProjectCodeImportDetail() {

    }


    /**
     * Gets the projectManagerPrincipalName attribute.
     * @return Returns the projectManagerPrincipalName.
     */
    public String getProjectManagerPrincipalName() {
        return projectManagerPrincipalName;
    }



    /**
     * Sets the projectManagerPrincipalName attribute value.
     * @param projectManagerPrincipalName The projectManagerPrincipalName to set.
     */
    public void setProjectManagerPrincipalName(String projectManagerPrincipalName) {
        this.projectManagerPrincipalName = projectManagerPrincipalName;
    }



    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the projectCode attribute.
     *
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }


    /**
     * Sets the projectCode attribute value.
     *
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    /**
     * Gets the projectName attribute.
     *
     * @return Returns the projectName.
     */
    public String getProjectName() {
        return projectName;
    }


    /**
     * Sets the projectName attribute value.
     *
     * @param projectName The projectName to set.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    /**
     * Gets the projectDescription attribute.
     *
     * @return Returns the projectDescription.
     */
    public String getProjectDescription() {
        return projectDescription;
    }


    /**
     * Sets the projectDescription attribute value.
     *
     * @param projectDescription The projectDescription to set.
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


    /**
     * Gets the projectManagerUniversalId attribute.
     *
     * @return Returns the projectManagerUniversalId.
     */
    public String getProjectManagerUniversalId() {
        return projectManagerUniversalId;
    }


    /**
     * Sets the projectManagerUniversalId attribute value.
     *
     * @param projectManagerUniversalId The projectManagerUniversalId to set.
     */
    public void setProjectManagerUniversalId(String projectManagerUniversalId) {
        this.projectManagerUniversalId = projectManagerUniversalId;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute value.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }


    /**
     * Sets the organizationCode attribute value.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the projectManagerUniversal attribute.
     *
     * @return Returns the projectManagerUniversal.
     */
    public Person getProjectManagerUniversal() {
        projectManagerUniversal = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(projectManagerUniversalId, projectManagerUniversal);
        return projectManagerUniversal;
    }


    /**
     * Sets the projectManagerUniversal attribute value.
     *
     * @param projectManagerUniversal The projectManagerUniversal to set.
     */
    public void setProjectManagerUniversal(Person projectManagerUniversal) {
        this.projectManagerUniversal = projectManagerUniversal;
    }


    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }


    /**
     * Sets the chartOfAccounts attribute value.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }


    /**
     * Gets the organization attribute.
     *
     * @return Returns the organization.
     */
    public Organization getOrganization() {
        return organization;
    }


    /**
     * Sets the organization attribute value.
     *
     * @param organization The organization to set.
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("projectCode", this.projectCode);
        return m;
    }
}
