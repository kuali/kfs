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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount extends PersistableBusinessObjectBase implements CGProjectDirector, Inactivateable, ContractsAndGrantsAccountAwardInformation, ContractsAndGrantsCGBAwardAccount {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardAccount.class);
    
    protected Long proposalNumber;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String principalId;
    protected boolean active = true;
    
    private boolean finalBilled;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;// Amount to Draw when awards are pulled up for review.
    private boolean locReviewIndicator;// The indicator set if the award account amountToDraw is modified by the user.
    private String invoiceDocumentStatus; // This would the status of the invoice document associated with the award account.

    protected Account account;
    protected Chart chartOfAccounts;
    protected Person projectDirector;
    protected Award award;

    /**
     * Gets the finalBilled attribute.
     * 
     * @return Returns the finalBilled.
     */
    public boolean isFinalBilled() {
        return finalBilled;
    }

    /**
     * Sets the finalBilled attribute value.
     * 
     * @param finalBilled The finalBilled to set.
     */
    public void setFinalBilled(boolean finalBilled) {
        this.finalBilled = finalBilled;
    }

    /**
     * Default constructor.
     */
    public AwardAccount() {
        // Struts needs this instance to populate the secondary key, principalName.
        if ( SpringContext.isInitialized() ) {
            try {
                projectDirector = (Person)SpringContext.getBean(PersonService.class).getPersonImplementationClass().newInstance();
            }
            catch (Exception ex) {
                LOG.error( "Unable to create a template person object.", ex );
            }
        }
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProposalNumber()
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

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccountsCode()
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
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccountNumber()
     */
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

    /**
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getPrincipalId()
     */
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

    /**
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccount()
     */
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

    /**
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccounts()
     */
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

    /**
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirector()
     */
    public Person getProjectDirector() {
        projectDirector = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("principalId", this.principalId);
        m.put("active", this.active);
        m.put("finalBilled", this.finalBilled);
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
     * Gets the amountToDraw attribute.
     * 
     * @return Returns the amountToDraw.
     */
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
     * Gets the locReviewIndicator attribute.
     * 
     * @return Returns the locReviewIndicator.
     */
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
     * Gets the currentLastBilledDate attribute.
     * 
     * @return Returns the currentLastBilledDate.
     */
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
    public String getProjectDirectorName() {
        if (!ObjectUtils.isNull(projectDirector)) {
            return projectDirector.getName();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount#getInvoiceDocumentStatus()
     */
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

