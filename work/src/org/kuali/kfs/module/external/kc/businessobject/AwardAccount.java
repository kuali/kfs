/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.external.kc.dto.AwardAccountDTO;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount implements ContractsAndGrantsBillingAwardAccount, MutableInactivatable {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String principalId;
    private boolean active = true;
    private boolean newCollectionRecord;
    private boolean federalSponsor;

    private boolean finalBilledIndicator;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;// Amount to Draw when awards are pulled up for review.
    private boolean letterOfCreditReviewIndicator;// The indicator set if the award account amountToDraw is modified by the user.

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
    public AwardAccount(AwardAccountDTO awardAccountDTO, String accountNumber, String chartOfAccountsCode, String cfdaNumber) {
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
        this.setFederalSponsor(awardAccountDTO.isFederalSponsor());

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

        finalBilledIndicator = awardAccountDTO.isFinalBill();
        currentLastBilledDate = (!ObjectUtils.isNull(awardAccountDTO.getLastBilledDate())) ?
                new Date(awardAccountDTO.getLastBilledDate().getTime())
                : null;
        previousLastBilledDate = (!ObjectUtils.isNull(awardAccountDTO.getPreviousLastBilledDate())) ?
                new Date(awardAccountDTO.getPreviousLastBilledDate().getTime())
                : null;
        amountToDraw = new KualiDecimal(awardAccountDTO.getAmountToDraw());
        letterOfCreditReviewIndicator = awardAccountDTO.isLetterOfCreditReviewIndicator();
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

    @Override
    public Date getCurrentLastBilledDate() {
        return currentLastBilledDate;
    }

    @Override
    public Date getPreviousLastBilledDate() {
        return previousLastBilledDate;
    }

    @Override
    public boolean isFinalBilledIndicator() {
        return finalBilledIndicator;
    }

    @Override
    public KualiDecimal getAmountToDraw() {
        return amountToDraw;
    }

    @Override
    public boolean isLetterOfCreditReviewIndicator() {
        return letterOfCreditReviewIndicator;
    }
}
