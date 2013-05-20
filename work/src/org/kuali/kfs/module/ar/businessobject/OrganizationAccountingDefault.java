/*
 * Copyright 2007-2009 The Kuali Foundation
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
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationAccountingDefault extends PersistableBusinessObjectBase implements FiscalYearBasedBusinessObject {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String organizationCode;
	private String organizationLateChargeObjectCode;
	private String defaultInvoiceChartOfAccountsCode;
	private String defaultInvoiceAccountNumber;
	private String defaultInvoiceSubAccountNumber;
	private String defaultInvoiceFinancialObjectCode;
	private String defaultInvoiceFinancialSubObjectCode;
	private String defaultInvoiceProjectCode;
	private String defaultInvoiceOrganizationReferenceIdentifier;
	private String defaultPaymentChartOfAccountsCode;
	private String defaultPaymentAccountNumber;
	private String defaultPaymentSubAccountNumber;
    private String defaultPaymentFinancialObjectCode;
    private String defaultPaymentFinancialSubObjectCode;
    private String defaultPaymentProjectCode;
	private String defaultPaymentOrganizationReferenceIdentifier;
    private String writeoffChartOfAccountsCode;
    private String writeoffAccountNumber;
    private String writeoffSubAccountNumber;
    private String writeoffFinancialObjectCode;
    private String writeoffFinancialSubObjectCode;
    private String writeoffProjectCode;
    private String writeoffOrganizationReferenceIdentifier;
   
    private ObjectCode defaultInvoiceFinancialObject;
	private SubObjectCode defaultInvoiceFinancialSubObject;
	private ObjectCode organizationLateChargeObject;
	private Chart chartOfAccounts;
	private Organization organization;
	private Chart defaultInvoiceChartOfAccounts;
	private SubAccount defaultInvoiceSubAccount;
	private Account defaultInvoiceAccount;
	private ProjectCode defaultInvoiceProject;
	private Account defaultPaymentAccount;
	private Chart defaultPaymentChartOfAccounts;
	private SubAccount defaultPaymentSubAccount;
	private ProjectCode defaultPaymentProject;
	private SystemOptions universityFiscal;
    private ObjectCode defaultPaymentFinancialObject;
    private SubObjectCode defaultPaymentFinancialSubObject;
    private Chart writeoffChartOfAccounts;
    private Account writeoffAccount;
    private SubAccount writeoffSubAccount;
    private ObjectCode writeoffFinancialObject;
    private SubObjectCode writeoffFinancialSubObject;
    private ProjectCode writeoffProject;
    
	/**
	 * Default constructor.
	 */
	public OrganizationAccountingDefault() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the organizationLateChargeObjectCode attribute.
	 * 
	 * @return Returns the organizationLateChargeObjectCode
	 * 
	 */
	public String getOrganizationLateChargeObjectCode() { 
		return organizationLateChargeObjectCode;
	}

	/**
	 * Sets the organizationLateChargeObjectCode attribute.
	 * 
	 * @param organizationLateChargeObjectCode The organizationLateChargeObjectCode to set.
	 * 
	 */
	public void setOrganizationLateChargeObjectCode(String organizationLateChargeObjectCode) {
		this.organizationLateChargeObjectCode = organizationLateChargeObjectCode;
	}


	/**
	 * Gets the defaultInvoiceChartOfAccountsCode attribute.
	 * 
	 * @return Returns the defaultInvoiceChartOfAccountsCode
	 * 
	 */
	public String getDefaultInvoiceChartOfAccountsCode() { 
		return defaultInvoiceChartOfAccountsCode;
	}

	/**
	 * Sets the defaultInvoiceChartOfAccountsCode attribute.
	 * 
	 * @param defaultInvoiceChartOfAccountsCode The defaultInvoiceChartOfAccountsCode to set.
	 * 
	 */
	public void setDefaultInvoiceChartOfAccountsCode(String defaultInvoiceChartOfAccountsCode) {
		this.defaultInvoiceChartOfAccountsCode = defaultInvoiceChartOfAccountsCode;
	}


	/**
	 * Gets the defaultInvoiceAccountNumber attribute.
	 * 
	 * @return Returns the defaultInvoiceAccountNumber
	 * 
	 */
	public String getDefaultInvoiceAccountNumber() { 
		return defaultInvoiceAccountNumber;
	}

	/**
	 * Sets the defaultInvoiceAccountNumber attribute.
	 * 
	 * @param defaultInvoiceAccountNumber The defaultInvoiceAccountNumber to set.
	 * 
	 */
	public void setDefaultInvoiceAccountNumber(String defaultInvoiceAccountNumber) {
		this.defaultInvoiceAccountNumber = defaultInvoiceAccountNumber;
	}


	/**
	 * Gets the defaultInvoiceSubAccountNumber attribute.
	 * 
	 * @return Returns the defaultInvoiceSubAccountNumber
	 * 
	 */
	public String getDefaultInvoiceSubAccountNumber() { 
		return defaultInvoiceSubAccountNumber;
	}

	/**
	 * Sets the defaultInvoiceSubAccountNumber attribute.
	 * 
	 * @param defaultInvoiceSubAccountNumber The defaultInvoiceSubAccountNumber to set.
	 * 
	 */
	public void setDefaultInvoiceSubAccountNumber(String defaultInvoiceSubAccountNumber) {
		this.defaultInvoiceSubAccountNumber = defaultInvoiceSubAccountNumber;
	}


	/**
	 * Gets the defaultInvoiceFinancialObjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialObjectCode
	 * 
	 */
	public String getDefaultInvoiceFinancialObjectCode() { 
		return defaultInvoiceFinancialObjectCode;
	}

	/**
	 * Sets the defaultInvoiceFinancialObjectCode attribute.
	 * 
	 * @param defaultInvoiceFinancialObjectCode The defaultInvoiceFinancialObjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceFinancialObjectCode(String defaultInvoiceFinancialObjectCode) {
		this.defaultInvoiceFinancialObjectCode = defaultInvoiceFinancialObjectCode;
	}


	/**
	 * Gets the defaultInvoiceFinancialSubObjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialSubObjectCode
	 * 
	 */
	public String getDefaultInvoiceFinancialSubObjectCode() { 
		return defaultInvoiceFinancialSubObjectCode;
	}

	/**
	 * Sets the defaultInvoiceFinancialSubObjectCode attribute.
	 * 
	 * @param defaultInvoiceFinancialSubObjectCode The defaultInvoiceFinancialSubObjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceFinancialSubObjectCode(String defaultInvoiceFinancialSubObjectCode) {
		this.defaultInvoiceFinancialSubObjectCode = defaultInvoiceFinancialSubObjectCode;
	}


	/**
	 * Gets the defaultInvoiceProjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceProjectCode
	 * 
	 */
	public String getDefaultInvoiceProjectCode() { 
		return defaultInvoiceProjectCode;
	}

	/**
	 * Sets the defaultInvoiceProjectCode attribute.
	 * 
	 * @param defaultInvoiceProjectCode The defaultInvoiceProjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceProjectCode(String defaultInvoiceProjectCode) {
		this.defaultInvoiceProjectCode = defaultInvoiceProjectCode;
	}


	/**
	 * Gets the defaultInvoiceOrganizationReferenceIdentifier attribute.
	 * 
	 * @return Returns the defaultInvoiceOrganizationReferenceIdentifier
	 * 
	 */
	public String getDefaultInvoiceOrganizationReferenceIdentifier() { 
		return defaultInvoiceOrganizationReferenceIdentifier;
	}

	/**
	 * Sets the defaultInvoiceOrganizationReferenceIdentifier attribute.
	 * 
	 * @param defaultInvoiceOrganizationReferenceIdentifier The defaultInvoiceOrganizationReferenceIdentifier to set.
	 * 
	 */
	public void setDefaultInvoiceOrganizationReferenceIdentifier(String defaultInvoiceOrganizationReferenceIdentifier) {
		this.defaultInvoiceOrganizationReferenceIdentifier = defaultInvoiceOrganizationReferenceIdentifier;
	}


	/**
	 * Gets the defaultPaymentChartOfAccountsCode attribute.
	 * 
	 * @return Returns the defaultPaymentChartOfAccountsCode
	 * 
	 */
	public String getDefaultPaymentChartOfAccountsCode() { 
		return defaultPaymentChartOfAccountsCode;
	}

	/**
	 * Sets the defaultPaymentChartOfAccountsCode attribute.
	 * 
	 * @param defaultPaymentChartOfAccountsCode The defaultPaymentChartOfAccountsCode to set.
	 * 
	 */
	public void setDefaultPaymentChartOfAccountsCode(String defaultPaymentChartOfAccountsCode) {
		this.defaultPaymentChartOfAccountsCode = defaultPaymentChartOfAccountsCode;
	}


	/**
	 * Gets the defaultPaymentAccountNumber attribute.
	 * 
	 * @return Returns the defaultPaymentAccountNumber
	 * 
	 */
	public String getDefaultPaymentAccountNumber() { 
		return defaultPaymentAccountNumber;
	}

	/**
	 * Sets the defaultPaymentAccountNumber attribute.
	 * 
	 * @param defaultPaymentAccountNumber The defaultPaymentAccountNumber to set.
	 * 
	 */
	public void setDefaultPaymentAccountNumber(String defaultPaymentAccountNumber) {
		this.defaultPaymentAccountNumber = defaultPaymentAccountNumber;
	}


	/**
	 * Gets the defaultPaymentSubAccountNumber attribute.
	 * 
	 * @return Returns the defaultPaymentSubAccountNumber
	 * 
	 */
	public String getDefaultPaymentSubAccountNumber() { 
		return defaultPaymentSubAccountNumber;
	}

	/**
	 * Sets the defaultPaymentSubAccountNumber attribute.
	 * 
	 * @param defaultPaymentSubAccountNumber The defaultPaymentSubAccountNumber to set.
	 * 
	 */
	public void setDefaultPaymentSubAccountNumber(String defaultPaymentSubAccountNumber) {
		this.defaultPaymentSubAccountNumber = defaultPaymentSubAccountNumber;
	}

	/**
     * Gets the defaultPaymentFinancialObjectCode attribute. 
     * @return Returns the defaultPaymentFinancialObjectCode.
     */
    public String getDefaultPaymentFinancialObjectCode() {
        return defaultPaymentFinancialObjectCode;
    }

    /**
     * Sets the defaultPaymentFinancialObjectCode attribute value.
     * @param defaultPaymentFinancialObjectCode The defaultPaymentFinancialObjectCode to set.
     */
    public void setDefaultPaymentFinancialObjectCode(String defaultPaymentFinancialObjectCode) {
        this.defaultPaymentFinancialObjectCode = defaultPaymentFinancialObjectCode;
    }

    /**
     * Gets the defaultPaymentFinancialSubObjectCode attribute. 
     * @return Returns the defaultPaymentFinancialSubObjectCode.
     */
    public String getDefaultPaymentFinancialSubObjectCode() {
        return defaultPaymentFinancialSubObjectCode;
    }

    /**
     * Sets the defaultPaymentFinancialSubObjectCode attribute value.
     * @param defaultPaymentFinancialSubObjectCode The defaultPaymentFinancialSubObjectCode to set.
     */
    public void setDefaultPaymentFinancialSubObjectCode(String defaultPaymentFinancialSubObjectCode) {
        this.defaultPaymentFinancialSubObjectCode = defaultPaymentFinancialSubObjectCode;
    }

    /**
	 * Gets the defaultPaymentProjectCode attribute.
	 * 
	 * @return Returns the defaultPaymentProjectCode
	 * 
	 */
	public String getDefaultPaymentProjectCode() { 
		return defaultPaymentProjectCode;
	}

	/**
	 * Sets the defaultPaymentProjectCode attribute.
	 * 
	 * @param defaultPaymentProjectCode The defaultPaymentProjectCode to set.
	 * 
	 */
	public void setDefaultPaymentProjectCode(String defaultPaymentProjectCode) {
		this.defaultPaymentProjectCode = defaultPaymentProjectCode;
	}


	/**
	 * Gets the defaultPaymentOrganizationReferenceIdentifier attribute.
	 * 
	 * @return Returns the defaultPaymentOrganizationReferenceIdentifier
	 * 
	 */
	public String getDefaultPaymentOrganizationReferenceIdentifier() { 
		return defaultPaymentOrganizationReferenceIdentifier;
	}

	/**
	 * Sets the defaultPaymentOrganizationReferenceIdentifier attribute.
	 * 
	 * @param defaultPaymentOrganizationReferenceIdentifier The defaultPaymentOrganizationReferenceIdentifier to set.
	 * 
	 */
	public void setDefaultPaymentOrganizationReferenceIdentifier(String defaultPaymentOrganizationReferenceIdentifier) {
		this.defaultPaymentOrganizationReferenceIdentifier = defaultPaymentOrganizationReferenceIdentifier;
	}

	/**
     * Gets the writeoffAccountNumber attribute. 
     * @return Returns the writeoffAccountNumber.
     */
    public String getWriteoffAccountNumber() {
        return writeoffAccountNumber;
    }

    /**
     * Sets the writeoffAccountNumber attribute value.
     * @param writeoffAccountNumber The writeoffAccountNumber to set.
     */
    public void setWriteoffAccountNumber(String writeoffAccountNumber) {
        this.writeoffAccountNumber = writeoffAccountNumber;
    }

    /**
     * Gets the writeoffChartOfAccountsCode attribute. 
     * @return Returns the writeoffChartOfAccountsCode.
     */
    public String getWriteoffChartOfAccountsCode() {
        return writeoffChartOfAccountsCode;
    }

    /**
     * Sets the writeoffChartOfAccountsCode attribute value.
     * @param writeoffChartOfAccountsCode The writeoffChartOfAccountsCode to set.
     */
    public void setWriteoffChartOfAccountsCode(String writeoffChartOfAccountsCode) {
        this.writeoffChartOfAccountsCode = writeoffChartOfAccountsCode;
    }

    /**
     * Gets the writeoffFinancialObjectCode attribute. 
     * @return Returns the writeoffFinancialObjectCode.
     */
    public String getWriteoffFinancialObjectCode() {
        return writeoffFinancialObjectCode;
    }

    /**
     * Sets the writeoffFinancialObjectCode attribute value.
     * @param writeoffFinancialObjectCode The writeoffFinancialObjectCode to set.
     */
    public void setWriteoffFinancialObjectCode(String writeoffFinancialObjectCode) {
        this.writeoffFinancialObjectCode = writeoffFinancialObjectCode;
    }

    /**
     * Gets the writeoffFinancialSubObjectCode attribute. 
     * @return Returns the writeoffFinancialSubObjectCode.
     */
    public String getWriteoffFinancialSubObjectCode() {
        return writeoffFinancialSubObjectCode;
    }

    /**
     * Sets the writeoffFinancialSubObjectCode attribute value.
     * @param writeoffFinancialSubObjectCode The writeoffFinancialSubObjectCode to set.
     */
    public void setWriteoffFinancialSubObjectCode(String writeoffFinancialSubObjectCode) {
        this.writeoffFinancialSubObjectCode = writeoffFinancialSubObjectCode;
    }

    /**
     * Gets the writeoffOrganizationReferenceIdentifier attribute. 
     * @return Returns the writeoffOrganizationReferenceIdentifier.
     */
    public String getWriteoffOrganizationReferenceIdentifier() {
        return writeoffOrganizationReferenceIdentifier;
    }

    /**
     * Sets the writeoffOrganizationReferenceIdentifier attribute value.
     * @param writeoffOrganizationReferenceIdentifier The writeoffOrganizationReferenceIdentifier to set.
     */
    public void setWriteoffOrganizationReferenceIdentifier(String writeoffOrganizationReferenceIdentifier) {
        this.writeoffOrganizationReferenceIdentifier = writeoffOrganizationReferenceIdentifier;
    }

    /**
     * Gets the writeoffProjectCode attribute. 
     * @return Returns the writeoffProjectCode.
     */
    public String getWriteoffProjectCode() {
        return writeoffProjectCode;
    }

    /**
     * Sets the writeoffProjectCode attribute value.
     * @param writeoffProjectCode The writeoffProjectCode to set.
     */
    public void setWriteoffProjectCode(String writeoffProjectCode) {
        this.writeoffProjectCode = writeoffProjectCode;
    }

    /**
     * Gets the writeoffSubAccountNumber attribute. 
     * @return Returns the writeoffSubAccountNumber.
     */
    public String getWriteoffSubAccountNumber() {
        return writeoffSubAccountNumber;
    }

    /**
     * Sets the writeoffSubAccountNumber attribute value.
     * @param writeoffSubAccountNumber The writeoffSubAccountNumber to set.
     */
    public void setWriteoffSubAccountNumber(String writeoffSubAccountNumber) {
        this.writeoffSubAccountNumber = writeoffSubAccountNumber;
    }

    /**
	 * Gets the defaultInvoiceFinancialObject attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialObject
	 * 
	 */
	public ObjectCode getDefaultInvoiceFinancialObject() { 
		return defaultInvoiceFinancialObject;
	}

	/**
	 * Sets the defaultInvoiceFinancialObject attribute.
	 * 
	 * @param defaultInvoiceFinancialObject The defaultInvoiceFinancialObject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceFinancialObject(ObjectCode defaultInvoiceFinancialObject) {
		this.defaultInvoiceFinancialObject = defaultInvoiceFinancialObject;
	}

	/**
	 * Gets the defaultInvoiceFinancialSubObject attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialSubObject
	 * 
	 */
	public SubObjectCode getDefaultInvoiceFinancialSubObject() { 
		return defaultInvoiceFinancialSubObject;
	}

	/**
	 * Sets the defaultInvoiceFinancialSubObject attribute.
	 * 
	 * @param defaultInvoiceFinancialSubObject The defaultInvoiceFinancialSubObject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceFinancialSubObject(SubObjectCode defaultInvoiceFinancialSubObject) {
		this.defaultInvoiceFinancialSubObject = defaultInvoiceFinancialSubObject;
	}

	/**
	 * Gets the organizationLateChargeObject attribute.
	 * 
	 * @return Returns the organizationLateChargeObject
	 * 
	 */
	public ObjectCode getOrganizationLateChargeObject() { 
		return organizationLateChargeObject;
	}

	/**
	 * Sets the organizationLateChargeObject attribute.
	 * 
	 * @param organizationLateChargeObject The organizationLateChargeObject to set.
	 * @deprecated
	 */
	public void setOrganizationLateChargeObject(ObjectCode organizationLateChargeObject) {
		this.organizationLateChargeObject = organizationLateChargeObject;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Organization getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * Gets the defaultInvoiceChartOfAccounts attribute.
	 * 
	 * @return Returns the defaultInvoiceChartOfAccounts
	 * 
	 */
	public Chart getDefaultInvoiceChartOfAccounts() { 
		return defaultInvoiceChartOfAccounts;
	}

	/**
	 * Sets the defaultInvoiceChartOfAccounts attribute.
	 * 
	 * @param defaultInvoiceChartOfAccounts The defaultInvoiceChartOfAccounts to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceChartOfAccounts(Chart defaultInvoiceChartOfAccounts) {
		this.defaultInvoiceChartOfAccounts = defaultInvoiceChartOfAccounts;
	}

	/**
	 * Gets the defaultInvoiceSubAccount attribute.
	 * 
	 * @return Returns the defaultInvoiceSubAccount
	 * 
	 */
	public SubAccount getDefaultInvoiceSubAccount() { 
		return defaultInvoiceSubAccount;
	}

	/**
	 * Sets the defaultInvoiceSubAccount attribute.
	 * 
	 * @param defaultInvoiceSubAccount The defaultInvoiceSubAccount to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceSubAccount(SubAccount defaultInvoiceSubAccount) {
		this.defaultInvoiceSubAccount = defaultInvoiceSubAccount;
	}

	/**
	 * Gets the defaultInvoiceAccount attribute.
	 * 
	 * @return Returns the defaultInvoiceAccount
	 * 
	 */
	public Account getDefaultInvoiceAccount() { 
		return defaultInvoiceAccount;
	}

	/**
	 * Sets the defaultInvoiceAccount attribute.
	 * 
	 * @param defaultInvoiceAccount The defaultInvoiceAccount to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceAccount(Account defaultInvoiceAccount) {
		this.defaultInvoiceAccount = defaultInvoiceAccount;
	}

	/**
	 * Gets the defaultInvoiceProject attribute.
	 * 
	 * @return Returns the defaultInvoiceProject
	 * 
	 */
	public ProjectCode getDefaultInvoiceProject() { 
		return defaultInvoiceProject;
	}

	/**
	 * Sets the defaultInvoiceProject attribute.
	 * 
	 * @param defaultInvoiceProject The defaultInvoiceProject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceProject(ProjectCode defaultInvoiceProject) {
		this.defaultInvoiceProject = defaultInvoiceProject;
	}

	/**
	 * Gets the defaultPaymentAccount attribute.
	 * 
	 * @return Returns the defaultPaymentAccount
	 * 
	 */
	public Account getDefaultPaymentAccount() { 
		return defaultPaymentAccount;
	}

	/**
	 * Sets the defaultPaymentAccount attribute.
	 * 
	 * @param defaultPaymentAccount The defaultPaymentAccount to set.
	 * @deprecated
	 */
	public void setDefaultPaymentAccount(Account defaultPaymentAccount) {
		this.defaultPaymentAccount = defaultPaymentAccount;
	}

	/**
	 * Gets the defaultPaymentChartOfAccounts attribute.
	 * 
	 * @return Returns the defaultPaymentChartOfAccounts
	 * 
	 */
	public Chart getDefaultPaymentChartOfAccounts() { 
		return defaultPaymentChartOfAccounts;
	}

	/**
	 * Sets the defaultPaymentChartOfAccounts attribute.
	 * 
	 * @param defaultPaymentChartOfAccounts The defaultPaymentChartOfAccounts to set.
	 * @deprecated
	 */
	public void setDefaultPaymentChartOfAccounts(Chart defaultPaymentChartOfAccounts) {
		this.defaultPaymentChartOfAccounts = defaultPaymentChartOfAccounts;
	}

	/**
	 * Gets the defaultPaymentSubAccount attribute.
	 * 
	 * @return Returns the defaultPaymentSubAccount
	 * 
	 */
	public SubAccount getDefaultPaymentSubAccount() { 
		return defaultPaymentSubAccount;
	}

	/**
	 * Sets the defaultPaymentSubAccount attribute.
	 * 
	 * @param defaultPaymentSubAccount The defaultPaymentSubAccount to set.
	 * @deprecated
	 */
	public void setDefaultPaymentSubAccount(SubAccount defaultPaymentSubAccount) {
		this.defaultPaymentSubAccount = defaultPaymentSubAccount;
	}

	/**
	 * Gets the defaultPaymentProject attribute.
	 * 
	 * @return Returns the defaultPaymentProject
	 * 
	 */
	public ProjectCode getDefaultPaymentProject() { 
		return defaultPaymentProject;
	}

	/**
	 * Sets the defaultPaymentProject attribute.
	 * 
	 * @param defaultPaymentProject The defaultPaymentProject to set.
	 * @deprecated
	 */
	public void setDefaultPaymentProject(ProjectCode defaultPaymentProject) {
		this.defaultPaymentProject = defaultPaymentProject;
	}
	
	/**
     * Gets the defaultPaymentFinancialObject attribute. 
     * @return Returns the defaultPaymentFinancialObject.
     */
    public ObjectCode getDefaultPaymentFinancialObject() {
        return defaultPaymentFinancialObject;
    }

    /**
     * Sets the defaultPaymentFinancialObject attribute value.
     * @param defaultPaymentFinancialObject The defaultPaymentFinancialObject to set.
     * @deprecated
     */
    public void setDefaultPaymentFinancialObject(ObjectCode defaultPaymentFinancialObject) {
        this.defaultPaymentFinancialObject = defaultPaymentFinancialObject;
    }

    /**
     * Gets the defaultPaymentFinancialSubObject attribute. 
     * @return Returns the defaultPaymentFinancialSubObject.
     */
    public SubObjectCode getDefaultPaymentFinancialSubObject() {
        return defaultPaymentFinancialSubObject;
    }

    /**
     * Sets the defaultPaymentFinancialSubObject attribute value.
     * @param defaultPaymentFinancialSubObject The defaultPaymentFinancialSubObject to set.
     * @deprecated
     */
    public void setDefaultPaymentFinancialSubObject(SubObjectCode defaultPaymentFinancialSubObject) {
        this.defaultPaymentFinancialSubObject = defaultPaymentFinancialSubObject;
    }

    /**
     * Gets the writeoffAccount attribute. 
     * @return Returns the writeoffAccount.
     */
    public Account getWriteoffAccount() {
        return writeoffAccount;
    }

    /**
     * Sets the writeoffAccount attribute value.
     * @param writeoffAccount The writeoffAccount to set.
     * @deprecated
     */
    public void setWriteoffAccount(Account writeoffAccount) {
        this.writeoffAccount = writeoffAccount;
    }

    /**
     * Gets the writeoffChartOfAccounts attribute. 
     * @return Returns the writeoffChartOfAccounts.
     */
    public Chart getWriteoffChartOfAccounts() {
        return writeoffChartOfAccounts;
    }

    /**
     * Sets the writeoffChartOfAccounts attribute value.
     * @param writeoffChartOfAccounts The writeoffChartOfAccounts to set.
     * @deprecated
     */
    public void setWriteoffChartOfAccounts(Chart writeoffChartOfAccounts) {
        this.writeoffChartOfAccounts = writeoffChartOfAccounts;
    }

    /**
     * Gets the writeoffFinancialObject attribute. 
     * @return Returns the writeoffFinancialObject.
     */
    public ObjectCode getWriteoffFinancialObject() {
        return writeoffFinancialObject;
    }

    /**
     * Sets the writeoffFinancialObject attribute value.
     * @param writeoffFinancialObject The writeoffFinancialObject to set.
     * @deprecated
     */
    public void setWriteoffFinancialObject(ObjectCode writeoffFinancialObject) {
        this.writeoffFinancialObject = writeoffFinancialObject;
    }

    /**
     * Gets the writeoffFinancialSubObject attribute. 
     * @return Returns the writeoffFinancialSubObject.
     */
    public SubObjectCode getWriteoffFinancialSubObject() {
        return writeoffFinancialSubObject;
    }

    /**
     * Sets the writeoffFinancialSubObject attribute value.
     * @param writeoffFinancialSubObject The writeoffFinancialSubObject to set.
     * @deprecated
     */
    public void setWriteoffFinancialSubObject(SubObjectCode writeoffFinancialSubObject) {
        this.writeoffFinancialSubObject = writeoffFinancialSubObject;
    }

    /**
     * Gets the writeoffProject attribute. 
     * @return Returns the writeoffProject.
     */
    public ProjectCode getWriteoffProject() {
        return writeoffProject;
    }

    /**
     * Sets the writeoffProject attribute value.
     * @param writeoffProject The writeoffProject to set.
     * @deprecated
     */
    public void setWriteoffProject(ProjectCode writeoffProject) {
        this.writeoffProject = writeoffProject;
    }

    /**
     * Gets the writeoffSubAccount attribute. 
     * @return Returns the writeoffSubAccount.
     */
    public SubAccount getWriteoffSubAccount() {
        return writeoffSubAccount;
    }

    /**
     * Sets the writeoffSubAccount attribute value.
     * @param writeoffSubAccount The writeoffSubAccount to set.
     * @deprecated
     */
    public void setWriteoffSubAccount(SubAccount writeoffSubAccount) {
        this.writeoffSubAccount = writeoffSubAccount;
    }

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.universityFiscalYear != null) {
            m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear.toString());
        }
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ORGANIZATION_CODE, this.organizationCode);
	    return m;
    }
    
     /**
     * This method returns a string so that an organization accounting default can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Organization Accounting Default"
     */
    public String getOrganizationAccountingDefaultViewer() {
        return "View Organization Accounting Default";
    }
}
