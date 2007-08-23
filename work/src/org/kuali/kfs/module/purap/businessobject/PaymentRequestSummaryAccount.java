package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCodeCurrent;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.purap.document.PaymentRequestDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PaymentRequestSummaryAccount extends PersistableBusinessObjectBase {

	private Integer accountIdentifier;
	private Integer purapDocumentIdentifier;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialSubObjectCode;
	private String financialObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private KualiDecimal amount;
	private BigDecimal accountLinePercent;
	private KualiDecimal disencumberedAmount;

    private Chart chart;
	private SubAccount subAccount;
	private ObjectCodeCurrent objectCode;
	private Account account;
    private PaymentRequestDocument paymentRequest;
    
	/**
	 * Default constructor.
	 */
	public PaymentRequestSummaryAccount() {

	}

	/**
	 * Gets the accountIdentifier attribute.
	 * 
	 * @return Returns the accountIdentifier
	 * 
	 */
	public Integer getAccountIdentifier() { 
		return accountIdentifier;
	}

	/**
	 * Sets the accountIdentifier attribute.
	 * 
	 * @param accountIdentifier The accountIdentifier to set.
	 * 
	 */
	public void setAccountIdentifier(Integer accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}


	/**
	 * Gets the purapDocumentIdentifier attribute.
	 * 
	 * @return Returns the purapDocumentIdentifier
	 * 
	 */
	public Integer getPurapDocumentIdentifier() { 
		return purapDocumentIdentifier;
	}

	/**
	 * Sets the purapDocumentIdentifier attribute.
	 * 
	 * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
	 * 
	 */
	public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
		this.purapDocumentIdentifier = purapDocumentIdentifier;
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
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
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
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

    /**
     * Gets the projectCode attribute. 
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute value.
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
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
	 * Gets the amount attribute.
	 * 
	 * @return Returns the amount
	 * 
	 */
	public KualiDecimal getAmount() { 
		return amount;
	}

	/**
	 * Sets the amount attribute.
	 * 
	 * @param amount The amount to set.
	 * 
	 */
	public void setAmount(KualiDecimal amount) {
		this.amount = amount;
	}


	/**
	 * Gets the accountLinePercent attribute.
	 * 
	 * @return Returns the accountLinePercent
	 * 
	 */
	public BigDecimal getAccountLinePercent() { 
		return accountLinePercent;
	}

	/**
	 * Sets the accountLinePercent attribute.
	 * 
	 * @param accountLinePercent The accountLinePercent to set.
	 * 
	 */
	public void setAccountLinePercent(BigDecimal accountLinePercent) {
		this.accountLinePercent = accountLinePercent;
	}


	/**
	 * Gets the disencumberedAmount attribute.
	 * 
	 * @return Returns the disencumberedAmount
	 * 
	 */
	public KualiDecimal getDisencumberedAmount() { 
		return disencumberedAmount;
	}

	/**
	 * Sets the disencumberedAmount attribute.
	 * 
	 * @param disencumberedAmount The disencumberedAmount to set.
	 * 
	 */
	public void setDisencumberedAmount(KualiDecimal disencumberedAmount) {
		this.disencumberedAmount = disencumberedAmount;
	}


	/**
	 * Gets the chart attribute.
	 * 
	 * @return Returns the chart
	 * 
	 */
	public Chart getChart() { 
		return chart;
	}

	/**
	 * Sets the chart attribute.
	 * 
	 * @param chart The chart to set.
	 * @deprecated
	 */
	public void setChart(Chart chart) {
		this.chart = chart;
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
	 * Gets the objectCode attribute.
	 * 
	 * @return Returns the objectCode
	 * 
	 */
	public ObjectCodeCurrent getObjectCode() { 
		return objectCode;
	}

	/**
	 * Sets the objectCode attribute.
	 * 
	 * @param objectCode The objectCode to set.
	 * @deprecated
	 */
	public void setObjectCode(ObjectCodeCurrent objectCode) {
		this.objectCode = objectCode;
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
     * Gets the paymentRequest attribute. 
     * @return Returns the paymentRequest.
     */
    public PaymentRequestDocument getPaymentRequest() {
        return paymentRequest;
    }

    /**
     * Sets the paymentRequest attribute value.
     * @param paymentRequest The paymentRequest to set.
     * @deprecated
     */
    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.accountIdentifier != null) {
            m.put("accountIdentifier", this.accountIdentifier.toString());
        }
	    return m;
    }

}
