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

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount extends PersistableBusinessObjectBase implements CGProjectDirector, MutableInactivatable, ContractsAndGrantsAccountAwardInformation, ContractsAndGrantsCGBAwardAccount {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String principalId;
    private boolean active = true;
    private boolean finalBilledIndicator;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;// Amount to Draw when awards are pulled up for review.
    private boolean locReviewIndicator;// The indicator set if the award account amountToDraw is modified by the user.
    private String invoiceDocumentStatus; // This would the status of the invoice document associated with the award account.

    private Account account;
    private Chart chartOfAccounts;
    private Person projectDirector;
    private Award award;

    /**
     * Gets the finalBilledIndicator attribute.
     *
     * @return Returns the finalBilledIndicator.
     */
    @Override
    public boolean isFinalBilledIndicator() {
        return finalBilledIndicator;
    }

    /**
     * Sets the finalBilledIndicator attribute value.
     *
     * @param finalBilledIndicator The finalBilledIndicator to set.
     */
    public void setFinalBilledIndicator(boolean finalBilledIndicator) {
        this.finalBilledIndicator = finalBilledIndicator;
    }

    /**
     * Default constructor.
     */
    public AwardAccount() {
        // Struts needs this instance to populate the secondary key, principalName.
//        try {
//            projectDirector = (Person) SpringContext.getBean(PersonService.class).getPersonImplementationClass().newInstance();
//        }
//        catch (Exception e) {
//        }
    }

    /***
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
    @Override
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
    @Override
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
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
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
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirector()
     */
    @Override
    public Person getProjectDirector() {
        projectDirector = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        return projectDirector;
    }

    /**
     * Sets the project director attribute
     *
     * @param projectDirector The projectDirector to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Override
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
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, this.principalId);
        m.put(KFSPropertyConstants.ACTIVE, this.active);
        m.put("finalBilledIndicator", this.finalBilledIndicator);
        m.put("currentLastBilledDate", this.currentLastBilledDate);
        m.put("previousLastBilledDate", this.previousLastBilledDate);
        m.put("amountToDraw", this.amountToDraw);
        m.put("locReviewIndicator", this.locReviewIndicator);
        m.put("invoiceDocumentStatus", this.invoiceDocumentStatus);
        return m;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAward()
     */
    public Award getAward() {
        return award;
    }

    /**
     * This method sets the associated award to the value provided.
     *
     * @param award Value to be assigned to the associated Award object.
     */
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
        this.active = active;
    }

    /**
     * Gets the locReviewIndicator attribute.
     *
     * @return Returns the locReviewIndicator.
     */
    @Override
    public boolean isLocReviewIndicator() {
        return locReviewIndicator;
    }

    /**
     * Sets the locReviewIndicator attribute value.
     *
     * @param locReviewIndicator The locReviewIndicator to set.
     */
    public void setLocReviewIndicator(boolean locReviewIndicator) {
        this.locReviewIndicator = locReviewIndicator;
    }

    /**
     * Gets the amountToDraw attribute.
     *
     * @return Returns the amountToDraw.
     */
    @Override
    public KualiDecimal getAmountToDraw() {
        return amountToDraw;
    }

    /**
     * Sets the amountToDraw attribute value.
     *
     * @param amountToDraw The amountToDraw to set.
     */
    public void setAmountToDraw(KualiDecimal amountToDraw) {
        this.amountToDraw = amountToDraw;
    }

    /**
     * Gets the currentLastBilledDate attribute.
     *
     * @return Returns the currentLastBilledDate.
     */
    @Override
    public Date getCurrentLastBilledDate() {
        return currentLastBilledDate;
    }

    /**
     * Sets the currentLastBilledDate attribute value.
     *
     * @param currentLastBilledDate The currentLastBilledDate to set.
     */
    public void setCurrentLastBilledDate(Date currentLastBilledDate) {
        this.currentLastBilledDate = currentLastBilledDate;
    }

    /**
     * Gets the previousLastBilledDate attribute.
     *
     * @return Returns the previousLastBilledDate.
     */
    @Override
    public Date getPreviousLastBilledDate() {
        return previousLastBilledDate;
    }

    /**
     * Sets the previousLastBilledDate attribute value.
     *
     * @param previousLastBilledDate The previousLastBilledDate to set.
     */
    public void setPreviousLastBilledDate(Date previousLastBilledDate) {
        this.previousLastBilledDate = previousLastBilledDate;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirectorName()
     */
    @Override
    public String getProjectDirectorName() {
        if (!ObjectUtils.isNull(projectDirector)) {
            return projectDirector.getName();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount#getInvoiceDocumentStatus()
     */
    @Override
    public String getInvoiceDocumentStatus() {
        return invoiceDocumentStatus;
    }

    /**
     * @param invoiceDocumentStatus
     */
    public void setInvoiceDocumentStatus(String invoiceDocumentStatus) {
        this.invoiceDocumentStatus = invoiceDocumentStatus;
    }


}
