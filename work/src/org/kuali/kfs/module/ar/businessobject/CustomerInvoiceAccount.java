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

package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceAccountInformation;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents an association between an award and an account with reference to invoices. way an award can maintain a
 * collection of these references instead of owning accounts directly.
 */
public class CustomerInvoiceAccount extends PersistableBusinessObjectBase implements MutableInactivatable, AccountsReceivableCustomerInvoiceAccountInformation {

    private String customerNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String objectCode;
    private String subObjectCode;
    private String subAccountNumber;
    private String accountNumber;
    private boolean active = true;
    private String accountType;
    private Account account;
    private Chart chartOfAccounts;
    private ObjectCode object;
    private ProjectCode project;
    private SubObjectCode subObject;
    private SubAccount subAccount;
//    private ContractsAndGrantsCGBAward award;
    private Customer customer;


    /**
     * Default constructor.
     */
    public CustomerInvoiceAccount() {
        // to set fiscal year to current for now. To be updated when period 13 logic comes in.
        universityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

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

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the objectCode attribute.
     * 
     * @return Returns the objectCode.
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


    /**
     * Gets the subObjectCode attribute.
     * 
     * @return Returns the subObjectCode.
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


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
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
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the object attribute.
     * 
     * @return Returns the object.
     */
    public ObjectCode getObject() {
        return object;
    }


    /**
     * Sets the object attribute value.
     * 
     * @param object The object to set.
     */
    public void setObject(ObjectCode object) {
        this.object = object;
    }


    /**
     * Gets the project attribute.
     * 
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }


    /**
     * Sets the project attribute value.
     * 
     * @param project The project to set.
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }


    /**
     * Gets the subObject attribute.
     * 
     * @return Returns the subObject.
     */
    public SubObjectCode getSubObject() {
        return subObject;
    }


    /**
     * Sets the subObject attribute value.
     * 
     * @param subObject The subObject to set.
     */
    public void setSubObject(SubObjectCode subObject) {
        this.subObject = subObject;
    }


    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }


    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getAccount()
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

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsGrantsAwardInvoiceAccountInformation#getChartOfAccounts()
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.customerNumber != null) {
            m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber.toString());
        }
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
        return m;
    }

//    /***
//     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAward()
//     */
//    public ContractsAndGrantsCGBAward getAward() {
//        return award;
//    }
//
//    /**
//     * This method sets the associated award to the value provided.
//     * 
//     * @param award Value to be assigned to the associated Award object.
//     */
//    public void setAward(ContractsAndGrantsCGBAward award) {
//        this.award = award;
//    }

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


    public String getCustomerNumber() {
        return customerNumber;
    }


    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    public Customer getCustomer() {
        return customer;
    }


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
