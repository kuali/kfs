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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * This class represents an association between an award and an organization. It's
 * like a reference to the organization from the award. This way an award can
 * maintain a collection of these references instead of owning organizations 
 * directly.
 */
public class AwardOrganization extends PersistableBusinessObjectBase implements Primaryable, Inactivateable {

    private String chartOfAccountsCode;
    private String organizationCode;
    private Long proposalNumber;
    private boolean awardPrimaryOrganizationIndicator;
    private boolean active;
    
    private Chart chartOfAccounts;
    private Org organization;

    /**
     * Default no-args constructor.
     */
    public AwardOrganization() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    
    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }
    
    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the awardPrimaryOrganizationIndicator attribute.
     * 
     * @return Returns the awardPrimaryOrganizationIndicator
     */
    public boolean isAwardPrimaryOrganizationIndicator() {
        return awardPrimaryOrganizationIndicator;
    }

    /**
     * Sets the awardPrimaryOrganizationIndicator attribute.
     * 
     * @param awardPrimaryOrganizationIndicator The awardPrimaryOrganizationIndicator to set.
     */
    public void setAwardPrimaryOrganizationIndicator(boolean awardPrimaryOrganizationIndicator) {
        this.awardPrimaryOrganizationIndicator = awardPrimaryOrganizationIndicator;
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
     * @deprecated Setter is required by OJB, but should not be used to modify 
     * this attribute. This attribute is set on the initial creation of the 
     * object and should not be changed.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated Setter is required by OJB, but should not be used to modify 
     * this attribute. This attribute is set on the initial creation of the 
     * object and should not be changed.
     */
    @Deprecated
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    public boolean isPrimary() {
        return isAwardPrimaryOrganizationIndicator();
    }
    
    /**
     * @see org.kuali.core.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return organization.isActive();
    }

    /**
     * @see org.kuali.core.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        organization.setActive(active);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }
}
