package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.OriginationCode;
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
public class PaymentDocument extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentLineNumber;
	private Date paymentApplicationDate;
	private Integer financialDocumentPostingYear;
	private String financialDocumentPostingPeriodCode;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private KualiDecimal accountChargeAmount;
	private String expenditureFinancialSystemOriginationCode;
	private String expenditureFinancialDocumentNumber;
	private String expenditureFinancialDocumentTypeCode;
	private Date expenditureFinancialDocumentPostedDate;
	private String purchaseOrderNumber;
	private String requisitionNumber;
	private String financialDocumentOverrideCode;
	private KualiDecimal primaryDepreciationPaymentAmount;
	private KualiDecimal secondaryDepreciationPaymentAmount;
	private boolean transferPaymentIndicator;

    private ObjectCode financialObject;
	private SubObjCd financialSubObject;
	private Account account;
	private SubAccount subAccount;
	private Chart chartOfAccounts;
	private ProjectCode project;
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentHeader documentHeader;
    private DocumentHeader expenditureFinancialDocument;
    private OriginationCode expenditureFinancialSystemOrigination;
    
	/**
	 * Default constructor.
	 */
	public PaymentDocument() {

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
	 * Gets the paymentApplicationDate attribute.
	 * 
	 * @return Returns the paymentApplicationDate
	 * 
	 */
	public Date getPaymentApplicationDate() { 
		return paymentApplicationDate;
	}

	/**
	 * Sets the paymentApplicationDate attribute.
	 * 
	 * @param paymentApplicationDate The paymentApplicationDate to set.
	 * 
	 */
	public void setPaymentApplicationDate(Date paymentApplicationDate) {
		this.paymentApplicationDate = paymentApplicationDate;
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
	 * Gets the financialDocumentPostingPeriodCode attribute.
	 * 
	 * @return Returns the financialDocumentPostingPeriodCode
	 * 
	 */
	public String getFinancialDocumentPostingPeriodCode() { 
		return financialDocumentPostingPeriodCode;
	}

	/**
	 * Sets the financialDocumentPostingPeriodCode attribute.
	 * 
	 * @param financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
	 * 
	 */
	public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
		this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
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
	 * Gets the accountChargeAmount attribute.
	 * 
	 * @return Returns the accountChargeAmount
	 * 
	 */
	public KualiDecimal getAccountChargeAmount() { 
		return accountChargeAmount;
	}

	/**
	 * Sets the accountChargeAmount attribute.
	 * 
	 * @param accountChargeAmount The accountChargeAmount to set.
	 * 
	 */
	public void setAccountChargeAmount(KualiDecimal accountChargeAmount) {
		this.accountChargeAmount = accountChargeAmount;
	}


	/**
	 * Gets the expenditureFinancialSystemOriginationCode attribute.
	 * 
	 * @return Returns the expenditureFinancialSystemOriginationCode
	 * 
	 */
	public String getExpenditureFinancialSystemOriginationCode() { 
		return expenditureFinancialSystemOriginationCode;
	}

	/**
	 * Sets the expenditureFinancialSystemOriginationCode attribute.
	 * 
	 * @param expenditureFinancialSystemOriginationCode The expenditureFinancialSystemOriginationCode to set.
	 * 
	 */
	public void setExpenditureFinancialSystemOriginationCode(String expenditureFinancialSystemOriginationCode) {
		this.expenditureFinancialSystemOriginationCode = expenditureFinancialSystemOriginationCode;
	}


	/**
	 * Gets the expenditureFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the expenditureFinancialDocumentNumber
	 * 
	 */
	public String getExpenditureFinancialDocumentNumber() { 
		return expenditureFinancialDocumentNumber;
	}

	/**
	 * Sets the expenditureFinancialDocumentNumber attribute.
	 * 
	 * @param expenditureFinancialDocumentNumber The expenditureFinancialDocumentNumber to set.
	 * 
	 */
	public void setExpenditureFinancialDocumentNumber(String expenditureFinancialDocumentNumber) {
		this.expenditureFinancialDocumentNumber = expenditureFinancialDocumentNumber;
	}


	/**
	 * Gets the expenditureFinancialDocumentTypeCode attribute.
	 * 
	 * @return Returns the expenditureFinancialDocumentTypeCode
	 * 
	 */
	public String getExpenditureFinancialDocumentTypeCode() { 
		return expenditureFinancialDocumentTypeCode;
	}

	/**
	 * Sets the expenditureFinancialDocumentTypeCode attribute.
	 * 
	 * @param expenditureFinancialDocumentTypeCode The expenditureFinancialDocumentTypeCode to set.
	 * 
	 */
	public void setExpenditureFinancialDocumentTypeCode(String expenditureFinancialDocumentTypeCode) {
		this.expenditureFinancialDocumentTypeCode = expenditureFinancialDocumentTypeCode;
	}


	/**
	 * Gets the expenditureFinancialDocumentPostedDate attribute.
	 * 
	 * @return Returns the expenditureFinancialDocumentPostedDate
	 * 
	 */
	public Date getExpenditureFinancialDocumentPostedDate() { 
		return expenditureFinancialDocumentPostedDate;
	}

	/**
	 * Sets the expenditureFinancialDocumentPostedDate attribute.
	 * 
	 * @param expenditureFinancialDocumentPostedDate The expenditureFinancialDocumentPostedDate to set.
	 * 
	 */
	public void setExpenditureFinancialDocumentPostedDate(Date expenditureFinancialDocumentPostedDate) {
		this.expenditureFinancialDocumentPostedDate = expenditureFinancialDocumentPostedDate;
	}


	/**
	 * Gets the purchaseOrderNumber attribute.
	 * 
	 * @return Returns the purchaseOrderNumber
	 * 
	 */
	public String getPurchaseOrderNumber() { 
		return purchaseOrderNumber;
	}

	/**
	 * Sets the purchaseOrderNumber attribute.
	 * 
	 * @param purchaseOrderNumber The purchaseOrderNumber to set.
	 * 
	 */
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}


	/**
	 * Gets the requisitionNumber attribute.
	 * 
	 * @return Returns the requisitionNumber
	 * 
	 */
	public String getRequisitionNumber() { 
		return requisitionNumber;
	}

	/**
	 * Sets the requisitionNumber attribute.
	 * 
	 * @param requisitionNumber The requisitionNumber to set.
	 * 
	 */
	public void setRequisitionNumber(String requisitionNumber) {
		this.requisitionNumber = requisitionNumber;
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
	 * Gets the primaryDepreciationPaymentAmount attribute.
	 * 
	 * @return Returns the primaryDepreciationPaymentAmount
	 * 
	 */
	public KualiDecimal getPrimaryDepreciationPaymentAmount() { 
		return primaryDepreciationPaymentAmount;
	}

	/**
	 * Sets the primaryDepreciationPaymentAmount attribute.
	 * 
	 * @param primaryDepreciationPaymentAmount The primaryDepreciationPaymentAmount to set.
	 * 
	 */
	public void setPrimaryDepreciationPaymentAmount(KualiDecimal primaryDepreciationPaymentAmount) {
		this.primaryDepreciationPaymentAmount = primaryDepreciationPaymentAmount;
	}


	/**
	 * Gets the secondaryDepreciationPaymentAmount attribute.
	 * 
	 * @return Returns the secondaryDepreciationPaymentAmount
	 * 
	 */
	public KualiDecimal getSecondaryDepreciationPaymentAmount() { 
		return secondaryDepreciationPaymentAmount;
	}

	/**
	 * Sets the secondaryDepreciationPaymentAmount attribute.
	 * 
	 * @param secondaryDepreciationPaymentAmount The secondaryDepreciationPaymentAmount to set.
	 * 
	 */
	public void setSecondaryDepreciationPaymentAmount(KualiDecimal secondaryDepreciationPaymentAmount) {
		this.secondaryDepreciationPaymentAmount = secondaryDepreciationPaymentAmount;
	}


	/**
	 * Gets the transferPaymentIndicator attribute.
	 * 
	 * @return Returns the transferPaymentIndicator
	 * 
	 */
	public boolean isTransferPaymentIndicator() { 
		return transferPaymentIndicator;
	}

	/**
	 * Sets the transferPaymentIndicator attribute.
	 * 
	 * @param transferPaymentIndicator The transferPaymentIndicator to set.
	 * 
	 */
	public void setTransferPaymentIndicator(boolean transferPaymentIndicator) {
		this.transferPaymentIndicator = transferPaymentIndicator;
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
     * Gets the expenditureFinancialDocument attribute. 
     * @return Returns the expenditureFinancialDocument.
     */
    public DocumentHeader getExpenditureFinancialDocument() {
        return expenditureFinancialDocument;
    }

    /**
     * Sets the expenditureFinancialDocument attribute value.
     * @param expenditureFinancialDocument The expenditureFinancialDocument to set.
     * @deprecated
     */
    public void setExpenditureFinancialDocument(DocumentHeader expenditureFinancialDocument) {
        this.expenditureFinancialDocument = expenditureFinancialDocument;
    }

    /**
     * Gets the expenditureFinancialSystemOrigination attribute. 
     * @return Returns the expenditureFinancialSystemOrigination.
     */
    public OriginationCode getExpenditureFinancialSystemOrigination() {
        return expenditureFinancialSystemOrigination;
    }

    /**
     * Sets the expenditureFinancialSystemOrigination attribute value.
     * @param expenditureFinancialSystemOrigination The expenditureFinancialSystemOrigination to set.
     * @deprecated
     */
    public void setExpenditureFinancialSystemOrigination(OriginationCode expenditureFinancialSystemOrigination) {
        this.expenditureFinancialSystemOrigination = expenditureFinancialSystemOrigination;
    }

    /**
     * Gets the financialDocumentPostingPeriod attribute. 
     * @return Returns the financialDocumentPostingPeriod.
     */
    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }

    /**
     * Sets the financialDocumentPostingPeriod attribute value.
     * @param financialDocumentPostingPeriod The financialDocumentPostingPeriod to set.
     * @deprecated
     */
    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
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
