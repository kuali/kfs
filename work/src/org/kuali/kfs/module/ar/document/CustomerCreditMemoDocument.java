package org.kuali.module.ar.document;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditMemoDocument extends AccountingDocumentBase {

	private String documentNumber;
	private Integer financialDocumentLineNumber;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String projectCode;
	private Integer financialDocumentPostingYear;
	private String universityFiscalPeriodCode;
	private String financialDocumentReferenceInvoiceNumber;
	private String financialDocumentShortDescription;
	private KualiDecimal financialDocumentLineAmount;
	private String financialDocumentOverrideCode;

    private ObjectCode financialObject;
	private Chart chartOfAccounts;
	private SubAccount subAccount;
	private Account account;
	private SubObjCd financialSubObject;
	private ProjectCode project;
	private AccountingPeriod universityFiscalPeriod;
    private DocumentHeader documentHeader;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private NonAppliedHolding nonAppliedHolding;
   
	/**
	 * Default constructor.
	 */
	public CustomerCreditMemoDocument() {
	    super();
	}
	
	@Override
	public void handleRouteStatusChange() {
	    // TODO Auto-generated method stub
	    super.handleRouteStatusChange();
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
	 * Gets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @return Returns the financialDocumentReferenceInvoiceNumber
	 * 
	 */
	public String getFinancialDocumentReferenceInvoiceNumber() { 
		return financialDocumentReferenceInvoiceNumber;
	}

	/**
	 * Sets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
	 * 
	 */
	public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
		this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
	}


	/**
	 * Gets the financialDocumentShortDescription attribute.
	 * 
	 * @return Returns the financialDocumentShortDescription
	 * 
	 */
	public String getFinancialDocumentShortDescription() { 
		return financialDocumentShortDescription;
	}

	/**
	 * Sets the financialDocumentShortDescription attribute.
	 * 
	 * @param financialDocumentShortDescription The financialDocumentShortDescription to set.
	 * 
	 */
	public void setFinancialDocumentShortDescription(String financialDocumentShortDescription) {
		this.financialDocumentShortDescription = financialDocumentShortDescription;
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
	 * Gets the universityFiscalPeriod attribute.
	 * 
	 * @return Returns the universityFiscalPeriod
	 * 
	 */
	public AccountingPeriod getUniversityFiscalPeriod() { 
		return universityFiscalPeriod;
	}

	/**
	 * Sets the universityFiscalPeriod attribute.
	 * 
	 * @param universityFiscalPeriod The universityFiscalPeriod to set.
	 * @deprecated
	 */
	public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
		this.universityFiscalPeriod = universityFiscalPeriod;
	}

    /**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }    
    
    /**
     * Gets the accountsReceivableDocumentHeader attribute. 
     * @return Returns the accountsReceivableDocumentHeader.
     */
    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    /**
     * Sets the accountsReceivableDocumentHeader attribute value.
     * @param accountsReceivableDocumentHeader The accountsReceivableDocumentHeader to set.
     * @deprecated
     */
    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    /**
     * Gets the nonAppliedHolding attribute. 
     * @return Returns the nonAppliedHolding.
     */
    public NonAppliedHolding getNonAppliedHolding() {
        return nonAppliedHolding;
    }

    /**
     * Sets the nonAppliedHolding attribute value.
     * @param nonAppliedHolding The nonAppliedHolding to set.
     * @deprecated
     */
    public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
        this.nonAppliedHolding = nonAppliedHolding;
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
    

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPendingEntrySourceDetail) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
