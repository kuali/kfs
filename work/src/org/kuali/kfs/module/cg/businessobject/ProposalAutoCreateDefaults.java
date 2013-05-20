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
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import java.util.ArrayList;

/**
 * Proposal Auto Create Defaults
 */
public class ProposalAutoCreateDefaults extends PersistableBusinessObjectBase implements MutableInactivatable {
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
    private ProposalAwardType proposalAwardType;
    private String proposalAwardTypeCode;
    private ProposalPurpose proposalPurpose;
    private String proposalPurposeCode;
    
    private boolean active;

    private ContractsAndGrantsUnit unitDTO;

    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;


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
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;

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
            projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
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
     * @return
     */
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * @param proposalAwardTypeCode
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    /**
     * @return
     */
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * @param proposalPurposeCode
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }

    
    public ProposalAwardType getProposalAwardType() {
        return proposalAwardType;
    }

    public void setProposalAwardType(ProposalAwardType proposalAwardType) {
        this.proposalAwardType = proposalAwardType;
    }

    public ProposalPurpose getProposalPurpose() {
        return proposalPurpose;
    }

    public void setProposalPurpose(ProposalPurpose proposalPurpose) {
        this.proposalPurpose = proposalPurpose;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalDefaultId", proposalDefaultId);
        m.put("kcUnit", kcUnit);
        m.put(KFSPropertyConstants.ACTIVE, active);
        m.put("proposalIndirectCostAmount", proposalIndirectCostAmount);
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        m.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalId);

        return m;
    }
}
