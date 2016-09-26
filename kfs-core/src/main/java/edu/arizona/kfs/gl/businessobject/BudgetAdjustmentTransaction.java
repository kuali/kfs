package edu.arizona.kfs.gl.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a budget adjustment transaction for a specific fiscal year, COA code, account number,
 * sub account number, object code, sub-object code, balance type code, object type code,
 * fiscal accounting period, document type code, document number, transaction sequence number
 */
public class BudgetAdjustmentTransaction extends PersistableBusinessObjectBase {

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
	private Date transactionDate;
	private String organizationDocumentNumber;
	private String projectCode;
	
	private Account account;
	private SystemOptions option;
	
	public BudgetAdjustmentTransaction() {
		super();
	}
	
	public BudgetAdjustmentTransaction(OriginEntryFull originEntryFull) {
		setUniversityFiscalYear(originEntryFull.getUniversityFiscalYear());
		setChartOfAccountsCode(originEntryFull.getChartOfAccountsCode());
		setAccountNumber(originEntryFull.getAccountNumber());
		setSubAccountNumber(originEntryFull.getSubAccountNumber());
		setFinancialObjectCode(originEntryFull.getFinancialObjectCode());
		setFinancialSubObjectCode(originEntryFull.getFinancialSubObjectCode());
		setFinancialBalanceTypeCode(originEntryFull.getFinancialBalanceTypeCode());
		setFinancialObjectTypeCode(originEntryFull.getFinancialObjectTypeCode());
		setUniversityFiscalPeriodCode(originEntryFull.getUniversityFiscalPeriodCode());
		setFinancialDocumentTypeCode(originEntryFull.getFinancialDocumentTypeCode());
		setFinancialSystemOriginationCode(originEntryFull.getFinancialSystemOriginationCode());
		setDocumentNumber(originEntryFull.getDocumentNumber());
		setTransactionLedgerEntrySequenceNumber(originEntryFull.getTransactionLedgerEntrySequenceNumber());
		setTransactionLedgerEntryDescription(originEntryFull.getTransactionLedgerEntryDescription());
		setTransactionLedgerEntryAmount(originEntryFull.getTransactionLedgerEntryAmount());
		setTransactionDate(originEntryFull.getTransactionDate());
		setOrganizationDocumentNumber(originEntryFull.getOrganizationDocumentNumber());
		setProjectCode(originEntryFull.getProjectCode());
	}
	
	public Integer getUniversityFiscalYear() {
		return universityFiscalYear;
	}
	
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}
	
	public String getChartOfAccountsCode() {
		return chartOfAccountsCode;
	}
	
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getSubAccountNumber() {
		return subAccountNumber;
	}
	
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}
	
	public String getFinancialObjectCode() {
		return financialObjectCode;
	}
	
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}
	
	public String getFinancialSubObjectCode() {
		return financialSubObjectCode;
	}
	
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}
	
	public String getFinancialBalanceTypeCode() {
		return financialBalanceTypeCode;
	}
	
	public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
		this.financialBalanceTypeCode = financialBalanceTypeCode;
	}

	public String getFinancialObjectTypeCode() {
		return financialObjectTypeCode;
	}

	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}

	public String getUniversityFiscalPeriodCode() {
		return universityFiscalPeriodCode;
	}

	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}

	public String getFinancialDocumentTypeCode() {
		return financialDocumentTypeCode;
	}

	public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
		this.financialDocumentTypeCode = financialDocumentTypeCode;
	}
		
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }
   
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }    
   
	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Integer getTransactionLedgerEntrySequenceNumber() {
		return transactionLedgerEntrySequenceNumber;
	}

	public void setTransactionLedgerEntrySequenceNumber(
			Integer transactionLedgerEntrySequenceNumber) {
		this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
	}

	public String getTransactionLedgerEntryDescription() {
		return transactionLedgerEntryDescription;
	}

	public void setTransactionLedgerEntryDescription(
			String transactionLedgerEntryDescription) {
		this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
	}

	public KualiDecimal getTransactionLedgerEntryAmount() {
		return transactionLedgerEntryAmount;
	}

	public void setTransactionLedgerEntryAmount(
			KualiDecimal transactionLedgerEntryAmount) {
		this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getOrganizationDocumentNumber() {
		return organizationDocumentNumber;
	}

	public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
		this.organizationDocumentNumber = organizationDocumentNumber;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public SystemOptions getOption() {
		return option;
	}

	public void setOption(SystemOptions option) {
		this.option = option;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>(); 
		m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear.toString());
		m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
		m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
		m.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.subAccountNumber);
		m.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.financialObjectCode);
		m.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, this.financialSubObjectCode);
		m.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.financialBalanceTypeCode);
		m.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, this.financialObjectTypeCode);
		m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, this.universityFiscalPeriodCode);
		m.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, this.financialDocumentTypeCode);
		m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
		m.put(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, this.transactionLedgerEntrySequenceNumber.toString());
		m.put(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, this.organizationDocumentNumber);
		return m;
	}
}