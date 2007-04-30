package org.kuali.module.labor.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
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

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LaborJournalVoucherDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentLineNumber;
	private String chartOfAccountsCode;
	private String accountNumber;
	private Integer financialDocumentPostingYear;
	private String financialObjectCode;
	private String financialBalanceTypeCode;
	private KualiDecimal financialDocumentLineAmount;
	private String subAccountNumber;
	private String financialSubObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private String referenceFinancialSystemOriginationCode;
	private String referenceFinancialDocumentNumber;
	private String referenceFinancialDocumentTypeCode;
	private String financialDocumentOverrideCode;
	private String financialDocumentLineDescription;
	private String finDocumentLnDebitOrCreditCd;
	private String transactionEncumbranceUpdateCode;
	private String financialObjectTypeCode;
	private String budgetYear;
	private String positionNumber;
	private Date payPeriodEndDate;
	private KualiDecimal transactionTotalHours;
	private Integer payrollEndDateFiscalYear;
	private String payrollEndDateFiscalPeriodCode;
	private String emplid;
	private Integer employeeRecord;
	private String earnCode;
	private String payGroup;
	private String salaryAdministrationPlan;
	private String grade;
	private String runIdentifier;
	private String laborLedgerOriginalChartOfAccountsCode;
	private String laborLedgerOriginalAccountNumber;
	private String laborLedgerOriginalSubAccountNumber;
	private String laborLedgerOriginalFinancialObjectCode;
	private String laborLedgerOriginalFinancialSubObjectCode;
	private String hrmsCompany;
	private String setid;

	private Chart chartOfAccounts;
	private SubAccount subAccount;
	private SubObjCd financialSubObject;
	private ObjectCode financialObject;
	private Account account;
	private BalanceTyp financialBalanceType;
	private ProjectCode project;
	private ObjectType financialObjectType;
	private AccountingPeriod payrollEndDateFiscalPeriod;
    private LaborJournalVoucherDocument laborJournalVoucherDocument;
    private DocumentType referenceDocumentType;
    private Options options;
    private OriginationCode referenceOriginationCode;
    private DocumentHeader referenceFinancialDocument;
   
	/**
	 * Default constructor.
	 */
	public LaborJournalVoucherDetail() {

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
	 * Gets the financialDocumentLineNumber attribute.
	 *
	 * @return Returns the financialDocumentLineNumber
	 *
	 */
	public Integer getFinancialDocumentLineNumber() {
		return financialDocumentLineNumber;
	}

	/**
	 * Sets the financialDocumentLineNumber attribute.
	 *
	 * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
	 *
	 */
	public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
		this.financialDocumentLineNumber = financialDocumentLineNumber;
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
	 * Gets the financialDocumentPostingYear attribute.
	 *
	 * @return Returns the financialDocumentPostingYear
	 *
	 */
	public Integer getFinancialDocumentPostingYear() {
		return financialDocumentPostingYear;
	}

	/**
	 * Sets the financialDocumentPostingYear attribute.
	 *
	 * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
	 *
	 */
	public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
		this.financialDocumentPostingYear = financialDocumentPostingYear;
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
	 * Gets the financialDocumentLineAmount attribute.
	 *
	 * @return Returns the financialDocumentLineAmount
	 *
	 */
	public KualiDecimal getFinancialDocumentLineAmount() {
		return financialDocumentLineAmount;
	}

	/**
	 * Sets the financialDocumentLineAmount attribute.
	 *
	 * @param financialDocumentLineAmount The financialDocumentLineAmount to set.
	 *
	 */
	public void setFinancialDocumentLineAmount(KualiDecimal financialDocumentLineAmount) {
		this.financialDocumentLineAmount = financialDocumentLineAmount;
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
	 * Gets the financialDocumentOverrideCode attribute.
	 *
	 * @return Returns the financialDocumentOverrideCode
	 *
	 */
	public String getFinancialDocumentOverrideCode() {
		return financialDocumentOverrideCode;
	}

	/**
	 * Sets the financialDocumentOverrideCode attribute.
	 *
	 * @param financialDocumentOverrideCode The financialDocumentOverrideCode to set.
	 *
	 */
	public void setFinancialDocumentOverrideCode(String financialDocumentOverrideCode) {
		this.financialDocumentOverrideCode = financialDocumentOverrideCode;
	}


	/**
	 * Gets the financialDocumentLineDescription attribute.
	 *
	 * @return Returns the financialDocumentLineDescription
	 *
	 */
	public String getFinancialDocumentLineDescription() {
		return financialDocumentLineDescription;
	}

	/**
	 * Sets the financialDocumentLineDescription attribute.
	 *
	 * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
	 *
	 */
	public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
		this.financialDocumentLineDescription = financialDocumentLineDescription;
	}


	/**
	 * Gets the finDocumentLnDebitOrCreditCd attribute.
	 *
	 * @return Returns the finDocumentLnDebitOrCreditCd
	 *
	 */
	public String getFinDocumentLnDebitOrCreditCd() {
		return finDocumentLnDebitOrCreditCd;
	}

	/**
	 * Sets the finDocumentLnDebitOrCreditCd attribute.
	 *
	 * @param finDocumentLnDebitOrCreditCd The finDocumentLnDebitOrCreditCd to set.
	 *
	 */
	public void setFinDocumentLnDebitOrCreditCd(String finDocumentLnDebitOrCreditCd) {
		this.finDocumentLnDebitOrCreditCd = finDocumentLnDebitOrCreditCd;
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
	 * Gets the budgetYear attribute.
	 *
	 * @return Returns the budgetYear
	 *
	 */
	public String getBudgetYear() {
		return budgetYear;
	}

	/**
	 * Sets the budgetYear attribute.
	 *
	 * @param budgetYear The budgetYear to set.
	 *
	 */
	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}


	/**
	 * Gets the positionNumber attribute.
	 *
	 * @return Returns the positionNumber
	 *
	 */
	public String getPositionNumber() {
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 *
	 * @param positionNumber The positionNumber to set.
	 *
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the payPeriodEndDate attribute.
	 *
	 * @return Returns the payPeriodEndDate
	 *
	 */
	public Date getPayPeriodEndDate() {
		return payPeriodEndDate;
	}

	/**
	 * Sets the payPeriodEndDate attribute.
	 *
	 * @param payPeriodEndDate The payPeriodEndDate to set.
	 *
	 */
	public void setPayPeriodEndDate(Date payPeriodEndDate) {
		this.payPeriodEndDate = payPeriodEndDate;
	}


	/**
	 * Gets the transactionTotalHours attribute.
	 *
	 * @return Returns the transactionTotalHours
	 *
	 */
	public KualiDecimal getTransactionTotalHours() {
		return transactionTotalHours;
	}

	/**
	 * Sets the transactionTotalHours attribute.
	 *
	 * @param transactionTotalHours The transactionTotalHours to set.
	 *
	 */
	public void setTransactionTotalHours(KualiDecimal transactionTotalHours) {
		this.transactionTotalHours = transactionTotalHours;
	}


	/**
	 * Gets the payrollEndDateFiscalYear attribute.
	 *
	 * @return Returns the payrollEndDateFiscalYear
	 *
	 */
	public Integer getPayrollEndDateFiscalYear() {
		return payrollEndDateFiscalYear;
	}

	/**
	 * Sets the payrollEndDateFiscalYear attribute.
	 *
	 * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
	 *
	 */
	public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
		this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
	}


	/**
	 * Gets the payrollEndDateFiscalPeriodCode attribute.
	 *
	 * @return Returns the payrollEndDateFiscalPeriodCode
	 *
	 */
	public String getPayrollEndDateFiscalPeriodCode() {
		return payrollEndDateFiscalPeriodCode;
	}

	/**
	 * Sets the payrollEndDateFiscalPeriodCode attribute.
	 *
	 * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
	 *
	 */
	public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
		this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
	}


	/**
	 * Gets the emplid attribute.
	 *
	 * @return Returns the emplid
	 *
	 */
	public String getEmplid() {
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 *
	 * @param emplid The emplid to set.
	 *
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the employeeRecord attribute.
	 *
	 * @return Returns the employeeRecord
	 *
	 */
	public Integer getEmployeeRecord() {
		return employeeRecord;
	}

	/**
	 * Sets the employeeRecord attribute.
	 *
	 * @param employeeRecord The employeeRecord to set.
	 *
	 */
	public void setEmployeeRecord(Integer employeeRecord) {
		this.employeeRecord = employeeRecord;
	}


	/**
	 * Gets the earnCode attribute.
	 *
	 * @return Returns the earnCode
	 *
	 */
	public String getEarnCode() {
		return earnCode;
	}

	/**
	 * Sets the earnCode attribute.
	 *
	 * @param earnCode The earnCode to set.
	 *
	 */
	public void setEarnCode(String earnCode) {
		this.earnCode = earnCode;
	}


	/**
	 * Gets the payGroup attribute.
	 *
	 * @return Returns the payGroup
	 *
	 */
	public String getPayGroup() {
		return payGroup;
	}

	/**
	 * Sets the payGroup attribute.
	 *
	 * @param payGroup The payGroup to set.
	 *
	 */
	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}


	/**
	 * Gets the salaryAdministrationPlan attribute.
	 *
	 * @return Returns the salaryAdministrationPlan
	 *
	 */
	public String getSalaryAdministrationPlan() {
		return salaryAdministrationPlan;
	}

	/**
	 * Sets the salaryAdministrationPlan attribute.
	 *
	 * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
	 *
	 */
	public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
		this.salaryAdministrationPlan = salaryAdministrationPlan;
	}


	/**
	 * Gets the grade attribute.
	 *
	 * @return Returns the grade
	 *
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * Sets the grade attribute.
	 *
	 * @param grade The grade to set.
	 *
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}


	/**
	 * Gets the runIdentifier attribute.
	 *
	 * @return Returns the runIdentifier
	 *
	 */
	public String getRunIdentifier() {
		return runIdentifier;
	}

	/**
	 * Sets the runIdentifier attribute.
	 *
	 * @param runIdentifier The runIdentifier to set.
	 *
	 */
	public void setRunIdentifier(String runIdentifier) {
		this.runIdentifier = runIdentifier;
	}


	/**
	 * Gets the laborLedgerOriginalChartOfAccountsCode attribute.
	 *
	 * @return Returns the laborLedgerOriginalChartOfAccountsCode
	 *
	 */
	public String getLaborLedgerOriginalChartOfAccountsCode() {
		return laborLedgerOriginalChartOfAccountsCode;
	}

	/**
	 * Sets the laborLedgerOriginalChartOfAccountsCode attribute.
	 *
	 * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
	 *
	 */
	public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
		this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
	}


	/**
	 * Gets the laborLedgerOriginalAccountNumber attribute.
	 *
	 * @return Returns the laborLedgerOriginalAccountNumber
	 *
	 */
	public String getLaborLedgerOriginalAccountNumber() {
		return laborLedgerOriginalAccountNumber;
	}

	/**
	 * Sets the laborLedgerOriginalAccountNumber attribute.
	 *
	 * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
	 *
	 */
	public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
		this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
	}


	/**
	 * Gets the laborLedgerOriginalSubAccountNumber attribute.
	 *
	 * @return Returns the laborLedgerOriginalSubAccountNumber
	 *
	 */
	public String getLaborLedgerOriginalSubAccountNumber() {
		return laborLedgerOriginalSubAccountNumber;
	}

	/**
	 * Sets the laborLedgerOriginalSubAccountNumber attribute.
	 *
	 * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
	 *
	 */
	public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
		this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
	}


	/**
	 * Gets the laborLedgerOriginalFinancialObjectCode attribute.
	 *
	 * @return Returns the laborLedgerOriginalFinancialObjectCode
	 *
	 */
	public String getLaborLedgerOriginalFinancialObjectCode() {
		return laborLedgerOriginalFinancialObjectCode;
	}

	/**
	 * Sets the laborLedgerOriginalFinancialObjectCode attribute.
	 *
	 * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
	 *
	 */
	public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
		this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
	}


	/**
	 * Gets the laborLedgerOriginalFinancialSubObjectCode attribute.
	 *
	 * @return Returns the laborLedgerOriginalFinancialSubObjectCode
	 *
	 */
	public String getLaborLedgerOriginalFinancialSubObjectCode() {
		return laborLedgerOriginalFinancialSubObjectCode;
	}

	/**
	 * Sets the laborLedgerOriginalFinancialSubObjectCode attribute.
	 *
	 * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
	 *
	 */
	public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
		this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
	}


	/**
	 * Gets the hrmsCompany attribute.
	 *
	 * @return Returns the hrmsCompany
	 *
	 */
	public String getHrmsCompany() {
		return hrmsCompany;
	}

	/**
	 * Sets the hrmsCompany attribute.
	 *
	 * @param hrmsCompany The hrmsCompany to set.
	 *
	 */
	public void setHrmsCompany(String hrmsCompany) {
		this.hrmsCompany = hrmsCompany;
	}


	/**
	 * Gets the setid attribute.
	 *
	 * @return Returns the setid
	 *
	 */
	public String getSetid() {
		return setid;
	}

	/**
	 * Sets the setid attribute.
	 *
	 * @param setid The setid to set.
	 *
	 */
	public void setSetid(String setid) {
		this.setid = setid;
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
	 * Gets the subAccount attribute.
	 *
	 * @return Returns the subAccount
	 *
	 */
	public SubAccount getSubAccount() {
		return subAccount;
	}

	/**
	 * Sets the subAccount attribute.
	 *
	 * @param subAccount The subAccount to set.
	 * @deprecated
	 */
	public void setSubAccount(SubAccount subAccount) {
		this.subAccount = subAccount;
	}

	/**
	 * Gets the financialSubObject attribute.
	 *
	 * @return Returns the financialSubObject
	 *
	 */
	public SubObjCd getFinancialSubObject() {
		return financialSubObject;
	}

	/**
	 * Sets the financialSubObject attribute.
	 *
	 * @param financialSubObject The financialSubObject to set.
	 * @deprecated
	 */
	public void setFinancialSubObject(SubObjCd financialSubObject) {
		this.financialSubObject = financialSubObject;
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
	 * @deprecated
	 */
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
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the financialBalanceType attribute.
	 *
	 * @return Returns the financialBalanceType
	 *
	 */
	public BalanceTyp getFinancialBalanceType() {
		return financialBalanceType;
	}

	/**
	 * Sets the financialBalanceType attribute.
	 *
	 * @param financialBalanceType The financialBalanceType to set.
	 * @deprecated
	 */
	public void setFinancialBalanceType(BalanceTyp financialBalanceType) {
		this.financialBalanceType = financialBalanceType;
	}

	/**
	 * Gets the project attribute.
	 *
	 * @return Returns the project
	 *
	 */
	public ProjectCode getProject() {
		return project;
	}

	/**
	 * Sets the project attribute.
	 *
	 * @param project The project to set.
	 * @deprecated
	 */
	public void setProject(ProjectCode project) {
		this.project = project;
	}

	/**
	 * Gets the financialObjectType attribute.
	 *
	 * @return Returns the financialObjectType
	 *
	 */
	public ObjectType getFinancialObjectType() {
		return financialObjectType;
	}

	/**
	 * Sets the financialObjectType attribute.
	 *
	 * @param financialObjectType The financialObjectType to set.
	 * @deprecated
	 */
	public void setFinancialObjectType(ObjectType financialObjectType) {
		this.financialObjectType = financialObjectType;
	}

	/**
	 * Gets the payrollEndDateFiscalPeriod attribute.
	 *
	 * @return Returns the payrollEndDateFiscalPeriod
	 *
	 */
	public AccountingPeriod getPayrollEndDateFiscalPeriod() {
		return payrollEndDateFiscalPeriod;
	}

	/**
	 * Sets the payrollEndDateFiscalPeriod attribute.
	 *
	 * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
	 * @deprecated
	 */
	public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
		this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
	}

	/**
     * Gets the laborJournalVoucherDocument attribute. 
     * @return Returns the laborJournalVoucherDocument.
     */
    public LaborJournalVoucherDocument getLaborJournalVoucherDocument() {
        return laborJournalVoucherDocument;
    }

    /**
     * Sets the laborJournalVoucherDocument attribute value.
     * @param laborJournalVoucherDocument The laborJournalVoucherDocument to set.
     * @deprecated
     */
    public void setLaborJournalVoucherDocument(LaborJournalVoucherDocument laborJournalVoucherDocument) {
        this.laborJournalVoucherDocument = laborJournalVoucherDocument;
    }

    /**
     * Gets the options attribute. 
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * @param options The options to set.
     * @deprecated
     */
    public void setOptions(Options options) {
        this.options = options;
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
     * @deprecated
     */
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
     * @deprecated
     */
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
     * @deprecated
     */
    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode) {
        this.referenceOriginationCode = referenceOriginationCode;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
	    return m;
    }
}
