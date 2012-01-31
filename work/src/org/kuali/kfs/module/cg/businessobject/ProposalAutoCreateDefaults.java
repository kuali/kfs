/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Proposal Auto Create Defaults
 */
public class ProposalAutoCreateDefaults extends PersistableBusinessObjectBase implements Inactivateable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProposalAutoCreateDefaults.class);

    private Integer proposalDefaultId;
    private String kcUnit;
    private String kcUnitName;
    private KualiDecimal proposalIndirectCostAmount;
    private Chart chartOfAccounts;
    private String chartOfAccountsCode;
    private Organization organization;
    private String organizationCode;
    private String principalId;
    private Person projectDirector;
    private boolean active;

    private ContractsAndGrantsUnit unitDTO;

    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;

    private List<AwardAccountDefaults> awardAccountDefaults;

    public ProposalAutoCreateDefaults() {
        awardAccountDefaults = new TypedArrayList(AwardAccountDefaults.class);
    }

    /**
     * @return
     */
    public Integer getProposalDefaultId() {
        return proposalDefaultId;
    }

    /**
     * @param proposalDefaultId
     */
    public void setProposalDefaultId(Integer proposalDefaultId) {
        this.proposalDefaultId = proposalDefaultId;
    }

    /**
     * @return
     */
    public String getKcUnit() {
        return kcUnit;
    }

    /**
     * @param kcUnit
     */
    public void setKcUnit(String kcUnit) {
        this.kcUnit = kcUnit;
    }

    /**
     * @return
     */
    public String getKcUnitName() {
        return kcUnitName;
    }

    /**
     * @param kcUnitName
     */
    public void setKcUnitName(String kcUnitName) {
        this.kcUnitName = kcUnitName;
    }

    /**
     * @return
     */
    public KualiDecimal getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }

    /**
     * @param proposalIndirectCostAmount
     */
    public void setProposalIndirectCostAmount(KualiDecimal proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }

    /**
     * @return
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * @param chartOfAccounts
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @return
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;

    }

    /**
     * @return
     */
    public List<AwardAccountDefaults> getAwardAccountDefaults() {
        return awardAccountDefaults;
    }

    /**
     * @param awardAccountDefaults
     */
    public void setAwardAccountDefaults(List<AwardAccountDefaults> awardAccountDefaults) {
        this.awardAccountDefaults = awardAccountDefaults;
    }

    /**
     * @return
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @param principalId
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @return
     */
    public Person getProjectDirector() {
        if (principalId != null) {
            projectDirector = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        }
        return projectDirector;
    }

    /**
     * @param projectDirector
     */
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @return
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }
    
    /**
     * @return
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * @return
     */
    public ContractsAndGrantsUnit getUnitDTO() {
        return unitDTO = (ContractsAndGrantsUnit) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).retrieveExternalizableBusinessObjectIfNecessary(this, unitDTO, "unitDTO");
    }

    /**
     * @param unitDTO
     */
    public void setUnitDTO(ContractsAndGrantsUnit unitDTO) {
        this.unitDTO = unitDTO;
    }

     /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalDefaultId", proposalDefaultId);
        m.put("kcUnit", kcUnit);
        m.put("active", active);
        m.put("proposalIndirectCostAmount", proposalIndirectCostAmount);
        m.put("chartOfAccountsCode", chartOfAccountsCode);
        m.put("organizationCode", organizationCode);
        m.put("principalId", principalId);

        return m;
    }
}
