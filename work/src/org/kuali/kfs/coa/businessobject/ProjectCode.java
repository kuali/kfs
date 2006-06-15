package org.kuali.module.chart.bo;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import org.kuali.core.bo.KualiCodeBase;
import org.kuali.core.bo.user.KualiUser;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ProjectCode extends KualiCodeBase {

    private static final long serialVersionUID = 4529316062843227897L;

    private String projectDescription;
    private String projectManagerUniversalId;
    private String chartOfAccountsCode;
    private String organizationCode;

    private KualiUser projectManagerUniversal;
    private Chart chartOfAccounts;
    private Org organization;

    /**
     * Default no-arg constructor.
     */
    public ProjectCode() {
    }

    /**
     * Gets the projectDescription attribute.
     * 
     * @return - Returns the projectDescription
     * 
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Sets the projectDescription attribute.
     * 
     * @param projectDescription The projectDescription to set.
     * 
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * Gets the projectManagerUniversal attribute.
     * 
     * @return - Returns the projectManagerUniversal
     * 
     */
    public KualiUser getProjectManagerUniversal() {
        return projectManagerUniversal;
    }

    /**
     * Sets the projectManagerUniversal attribute.
     * 
     * @param projectManagerUniversal The projectManagerUniversal to set.
     * 
     */
    public void setProjectManagerUniversal(KualiUser projectManagerUniversal) {
        this.projectManagerUniversal = projectManagerUniversal;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return - Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * 
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return - Returns the organization
     * 
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * 
     */
    public void setOrganization(Org organization) {
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