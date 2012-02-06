/*
 * Copyright 2005-2006 The Kuali Foundation
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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * 
 */
public class ProjectCode extends KualiCodeBase implements MutableInactivatable {

    private static final long serialVersionUID = 4529316062843227897L;

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "ProjectCode";
    
    private String projectDescription;
    private String projectManagerUniversalId;
    private String chartOfAccountsCode;
    private String organizationCode;

    private Person projectManagerUniversal;
    private Chart chartOfAccounts;
    private Organization organization;

    /**
     * Default no-arg constructor.
     */
    public ProjectCode() {
    }

    /**
     * Gets the projectDescription attribute.
     * 
     * @return Returns the projectDescription
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Sets the projectDescription attribute.
     * 
     * @param projectDescription The projectDescription to set.
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Person getProjectManagerUniversal() {
        projectManagerUniversal = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(projectManagerUniversalId, projectManagerUniversal);
        return projectManagerUniversal;
    }

    /**
     * Sets the projectManagerUniversal attribute.
     * 
     * @param projectManagerUniversal The projectManagerUniversal to set.
     */
    public void setProjectManagerUniversal(Person projectManagerUniversal) {
        this.projectManagerUniversal = projectManagerUniversal;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the projectManagerUniversalId.
     */
    public String getProjectManagerUniversalId() {
        return projectManagerUniversalId;
    }

    /**
     * @param projectManagerUniversalId The projectManagerUniversalId to set.
     */
    public void setProjectManagerUniversalId(String projectManagerUniversalId) {
        this.projectManagerUniversalId = projectManagerUniversalId;
    }

}

