/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.external.kc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAwardAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount implements ContractsAndGrantsAccountAwardInformation {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String principalId;
    private boolean active = true;
    private boolean newCollectionRecord;
    private boolean federalSponsor;
    private Account account;
    private Chart chartOfAccounts;
    private Person projectDirector;
    private Award award;

    /**
     * Default constructor.
     */
    public AwardAccount() {
        // Struts needs this instance to populate the secondary key, principalName.
        try {
            //projectDirector = (Person) SpringContext.getBean(PersonService.class).getPersonImplementationClass().newInstance();
        }
        catch (Exception e) {
        }
    }

    /**
     * @param awardAccountDTO
     * @param accountNumber
     * @param chartOfAccountsCode
     * @param cfdaNumber
     */
    public AwardAccount(ContractsAndGrantsAwardAccount awardAccountDTO, String accountNumber, String chartOfAccountsCode, String cfdaNumber) {
        // Struts needs this instance to populate the secondary key, principalName.
        try {
            projectDirector = SpringContext.getBean(PersonService.class).getPersonImplementationClass().newInstance();
        }
        catch (Exception e) {
        }


        // setup this class from DTO
        Proposal proposal = new Proposal();
        Award award = new Award();
        Agency agency = new Agency();
        Agency primeAgency = new Agency();

        this.setAccountNumber(accountNumber);
        this.setChartOfAccountsCode(chartOfAccountsCode);
        this.setPrincipalId(awardAccountDTO.getProjectDirector());
        this.setProposalNumber(awardAccountDTO.getAwardId());
        this.setActive(true);
        this.setFederalSponsor(awardAccountDTO.getFederalSponsor());

        award.setAwardNumber(awardAccountDTO.getProposalNumber());
        award.setProposalNumber(awardAccountDTO.getAwardId());
        award.setAgencyNumber(awardAccountDTO.getSponsorCode());
        award.setAwardTitle(awardAccountDTO.getAwardTitle());
        award.setGrantNumber(awardAccountDTO.getGrantNumber());
        award.setCfdaNumber(cfdaNumber);

        proposal.setFederalPassThroughAgencyNumber(awardAccountDTO.getProposalFederalPassThroughAgencyNumber());
        proposal.setProposalNumber(awardAccountDTO.getAwardId());

        proposal.setAward(award);
        this.setAward(award);
        this.getAward().setProposal(proposal);

        agency.setAgencyNumber(awardAccountDTO.getSponsorCode());
        agency.setReportingName(awardAccountDTO.getSponsorName());
        primeAgency.setAgencyNumber(awardAccountDTO.getPrimeSponsorCode());
        primeAgency.setReportingName(awardAccountDTO.getPrimeSponsorName());
        this.getAward().setAgency(agency);
        this.getAward().setPrimeAgency(primeAgency);
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProposalNumber()
     */
    @Override
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


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccountsCode()
     */
    @Override
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


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccountNumber()
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccount()
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     */
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccounts()
     */
    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirector()
     */
    public Person getProjectDirector() {
        projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        return projectDirector;
    }

    /**
     * Sets the project director attribute
     *
     * @param projectDirector The projectDirector to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("principalId", this.principalId);
        m.put("active", this.active);
        m.put("federalSponsor", this.federalSponsor);
        m.put("newCollectionRecord", this.newCollectionRecord);

        return m;
    }

    public Award getAward() {
        return award;
    }

    public void setAward(Award award) {
        this.award = award;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = true;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirectorName()
     */
    @Override
    public String getProjectDirectorName() {
        if (!ObjectUtils.isNull(getProjectDirector())) {
            return getProjectDirector().getName();
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#prepareForWorkflow()
     */
    public void prepareForWorkflow() {

    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
    }

    public boolean isNewCollectionRecord() {
        return false;
    }

    public void setNewCollectionRecord(boolean newCollectionRecord) {
        this.newCollectionRecord = newCollectionRecord;
    }

    public boolean isFederalSponsor() {
        return federalSponsor;
    }

    public void setFederalSponsor(boolean federalSponsor) {
        this.federalSponsor = federalSponsor;
    }
}
