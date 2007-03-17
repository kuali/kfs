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

package org.kuali.module.labor.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.Entry;
import org.kuali.PropertyConstants;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LaborGeneralLedgerEntry extends Entry {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String financialBalanceTypeCode;
	private String financialObjectTypeCode;
	private String universityFiscalPeriodCode;
	private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
	private Integer transactionLedgerEntrySequenceNumber;
	private String transactionLedgerEntryDescription;
	private KualiDecimal transactionLedgerEntryAmount;
	private String transactionDebitCreditCode;
	private Date transactionDate;
	private String organizationDocumentNumber;
	private String projectCode;
	private String organizationReferenceId;
	private String referenceFinancialDocumentTypeCode;
	private String referenceFinancialSystemOriginationCode;
	private String referenceFinancialDocumentNumber;
	private Date financialDocumentReversalDate;
	private String transactionEncumbranceUpdateCode;

    private ObjectCode financialObject;
	private Account account;
	private Chart chartOfAccounts;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;
    private BalanceTyp balanceType;
    private ProjectCode project;
    private ObjectType financialObjectType;
    private AccountingPeriod universityFiscalPeriod;
    private DocumentType documentType;
    private DocumentType referenceDocumentType;
    private OriginationCode referenceOriginationCode;
    private DocumentHeader financialDocument;
    private DocumentHeader referenceFinancialDocument;
    private OriginationCode financialSystemOrigination;
    
	/**
	 * Default constructor.
	 */
	public LaborGeneralLedgerEntry() {

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
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the financialBalanceTypeCode attribute.
	 * 
	 * @return Returns the financialBalanceTypeCode
	 * 
	 */
	public String getFinancialBalanceTypeCode() { 
		return financialBalanceTypeCode;
	}

	/**
	 * Sets the financialBalanceTypeCode attribute.
	 * 
	 * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
	 * 
	 */
	public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
		this.financialBalanceTypeCode = financialBalanceTypeCode;
	}


	/**
	 * Gets the financialObjectTypeCode attribute.
	 * 
	 * @return Returns the financialObjectTypeCode
	 * 
	 */
	public String getFinancialObjectTypeCode() { 
		return financialObjectTypeCode;
	}

	/**
	 * Sets the financialObjectTypeCode attribute.
	 * 
	 * @param financialObjectTypeCode The financialObjectTypeCode to set.
	 * 
	 */
	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}


	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 * 
	 * @return Returns the universityFiscalPeriodCode
	 * 
	 */
	public String getUniversityFiscalPeriodCode() { 
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 * 
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 * 
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}


	/**
	 * Gets the financialDocumentTypeCode attribute.
	 * 
	 * @return Returns the financialDocumentTypeCode
	 * 
	 */
	public String getFinancialDocumentTypeCode() { 
		return financialDocumentTypeCode;
	}

	/**
	 * Sets the financialDocumentTypeCode attribute.
	 * 
	 * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
	 * 
	 */
	public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
		this.financialDocumentTypeCode = financialDocumentTypeCode;
	}

    /**
     * Gets the financialSystemOriginationCode attribute. 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute value.
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }     
    
	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @return Returns the transactionLedgerEntrySequenceNumber
	 * 
	 */
	public Integer getTransactionLedgerEntrySequenceNumber() { 
		return transactionLedgerEntrySequenceNumber;
	}

	/**
	 * Sets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
	 * 
	 */
	public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
		this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
	}


	/**
	 * Gets the transactionLedgerEntryDescription attribute.
	 * 
	 * @return Returns the transactionLedgerEntryDescription
	 * 
	 */
	public String getTransactionLedgerEntryDescription() { 
		return transactionLedgerEntryDescription;
	}

	/**
	 * Sets the transactionLedgerEntryDescription attribute.
	 * 
	 * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
	 * 
	 */
	public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
		this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
	}


	/**
	 * Gets the transactionLedgerEntryAmount attribute.
	 * 
	 * @return Returns the transactionLedgerEntryAmount
	 * 
	 */
	public KualiDecimal getTransactionLedgerEntryAmount() { 
		return transactionLedgerEntryAmount;
	}

	/**
	 * Sets the transactionLedgerEntryAmount attribute.
	 * 
	 * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
	 * 
	 */
	public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
		this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
	}


	/**
	 * Gets the transactionDebitCreditCode attribute.
	 * 
	 * @return Returns the transactionDebitCreditCode
	 * 
	 */
	public String getTransactionDebitCreditCode() { 
		return transactionDebitCreditCode;
	}

	/**
	 * Sets the transactionDebitCreditCode attribute.
	 * 
	 * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
	 * 
	 */
	public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
		this.transactionDebitCreditCode = transactionDebitCreditCode;
	}


	/**
	 * Gets the transactionDate attribute.
	 * 
	 * @return Returns the transactionDate
	 * 
	 */
	public Date getTransactionDate() { 
		return transactionDate;
	}

	/**
	 * Sets the transactionDate attribute.
	 * 
	 * @param transactionDate The transactionDate to set.
	 * 
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	/**
	 * Gets the organizationDocumentNumber attribute.
	 * 
	 * @return Returns the organizationDocumentNumber
	 * 
	 */
	public String getOrganizationDocumentNumber() { 
		return organizationDocumentNumber;
	}

	/**
	 * Sets the organizationDocumentNumber attribute.
	 * 
	 * @param organizationDocumentNumber The organizationDocumentNumber to set.
	 * 
	 */
	public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
		this.organizationDocumentNumber = organizationDocumentNumber;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentTypeCode
	 * 
	 */
	public String getReferenceFinancialDocumentTypeCode() { 
		return referenceFinancialDocumentTypeCode;
	}

	/**
	 * Sets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
	 * 
	 */
	public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
		this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
	}


	/**
	 * Gets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @return Returns the referenceFinancialSystemOriginationCode
	 * 
	 */
	public String getReferenceFinancialSystemOriginationCode() { 
		return referenceFinancialSystemOriginationCode;
	}

	/**
	 * Sets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
	 * 
	 */
	public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
		this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
	}


	/**
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the financialDocumentReversalDate attribute.
	 * 
	 * @return Returns the financialDocumentReversalDate
	 * 
	 */
	public Date getFinancialDocumentReversalDate() { 
		return financialDocumentReversalDate;
	}

	/**
	 * Sets the financialDocumentReversalDate attribute.
	 * 
	 * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
	 * 
	 */
	public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
		this.financialDocumentReversalDate = financialDocumentReversalDate;
	}


	/**
	 * Gets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @return Returns the transactionEncumbranceUpdateCode
	 * 
	 */
	public String getTransactionEncumbranceUpdateCode() { 
		return transactionEncumbranceUpdateCode;
	}

	/**
	 * Sets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
	 * 
	 */
	public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
		this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param financialObject The financialObject to set.
	 */
    @Deprecated
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
	 * 
	 */
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
	 */
    @Deprecated
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the balanceType attribute. 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * @param balanceType The balanceType to set.
     */
    @Deprecated
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the documentType attribute. 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     */
    @Deprecated
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the financialDocument attribute. 
     * @return Returns the financialDocument.
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute value.
     * @param financialDocument The financialDocument to set.
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     */
    @Deprecated
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject attribute. 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * @param financialSubObject The financialSubObject to set.
     */
    @Deprecated
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the project attribute. 
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project attribute value.
     * @param project The project to set.
     */
    @Deprecated
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the referenceDocumentType attribute. 
     * @return Returns the referenceDocumentType.
     */
    public DocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    /**
     * Sets the referenceDocumentType attribute value.
     * @param referenceDocumentType The referenceDocumentType to set.
     */
    @Deprecated
    public void setReferenceDocumentType(DocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    /**
     * Gets the referenceFinancialDocument attribute. 
     * @return Returns the referenceFinancialDocument.
     */
    public DocumentHeader getReferenceFinancialDocument() {
        return referenceFinancialDocument;
    }

    /**
     * Sets the referenceFinancialDocument attribute value.
     * @param referenceFinancialDocument The referenceFinancialDocument to set.
     */
    @Deprecated
    public void setReferenceFinancialDocument(DocumentHeader referenceFinancialDocument) {
        this.referenceFinancialDocument = referenceFinancialDocument;
    }

    /**
     * Gets the referenceOriginationCode attribute. 
     * @return Returns the referenceOriginationCode.
     */
    public OriginationCode getReferenceOriginationCode() {
        return referenceOriginationCode;
    }

    /**
     * Sets the referenceOriginationCode attribute value.
     * @param referenceOriginationCode The referenceOriginationCode to set.
     */
    @Deprecated
    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode) {
        this.referenceOriginationCode = referenceOriginationCode;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the universityFiscalPeriod attribute. 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute value.
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the financialSystemOrigination attribute. 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination attribute value.
     * @param financialSystemOrigination The financialSystemOrigination to set.
     */
    @Deprecated
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }    
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.transactionLedgerEntrySequenceNumber != null) {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
        return m;
    }


}
