/*
 * Copyright 2006-2009 The Kuali Foundation
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

package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;

/**
 * This class represents an association between an award and an account with reference to invoices. way an award can maintain a
 * collection of these references instead of owning accounts directly.
 */
public class AwardInvoiceAccount implements ContractsGrantsAwardInvoiceAccountInformation {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private Integer universityFiscalYear;
    private String objectCode;
    private String subObjectCode;
    private String subAccountNumber;
    private String accountNumber;
    private boolean active = true;
    private String accountType;


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getProposalNumber()
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
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getChartOfAccountsCode()
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

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getAccountNumber()
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


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getObjectCode()
     */
    public String getObjectCode() {
        return objectCode;
    }


    /**
     * Sets the objectCode attribute value.
     * 
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getSubObjectCode()
     */
    public String getSubObjectCode() {
        return subObjectCode;
    }


    /**
     * Sets the subObjectCode attribute value.
     * 
     * @param subObjectCode The subObjectCode to set.
     */
    public void setSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getSubAccountNumber()
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
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


    public ObjectCode getObject() {
        return null;
    }

    public SubAccount getSubAccount() {
        return null;
    }

    public SubObjectCode getSubObject() {
        return null;
    }

    public ProjectCode getProject() {
        return null;
    }

    public Account getAccount() {
        return null;
    }

    public Chart getChartOfAccounts() {
        return null;
    }


    /**
     * Gets the accountType attribute.
     * 
     * @return Returns the accountType.
     */
    public String getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType attribute value.
     * 
     * @param accountType The accountType to set.
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    public void prepareForWorkflow() {
    }

    public void refresh() {
    }


}
