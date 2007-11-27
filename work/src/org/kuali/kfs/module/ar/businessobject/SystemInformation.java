package org.kuali.module.ar.bo;

import org.kuali.module.chart.bo.Account;
import org.kuali.core.bo.user.*;
import org.kuali.module.kra.routingform.bo.*;
import java.util.LinkedHashMap;
import org.kuali.module.financial.bo.*;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.cg.bo.*;
import org.kuali.module.chart.bo.codes.*;
import org.kuali.module.purap.bo.*;
import org.kuali.module.cams.bo.*;
import org.kuali.kfs.bo.*;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.*;
import org.kuali.module.kra.budget.bo.*;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.ar.bo.*;
import org.kuali.module.budget.bo.*;
import org.kuali.module.labor.bo.*;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.core.bo.*;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SystemInformation extends PersistableBusinessObjectBase {

	private Integer universityFiscalYear;
	private String processingChartOfAccountCode;
	private String processingOrganizationCode;
	private String universityFederalEmployerIdentificationNumber;
	private String salesTaxAccountNumber;
	private String salesTaxFinancialObjectCode;
	private String refundFinancialObjectCode;
	private String badDebtExpenseFinancialObjectCode;
	private String universityClearingChartOfAccountsCode;
	private String universityClearingAccountNumber;
	private String universityClearingSubAccountNumber;
	private String universityClearingObjectCode;
	private String universityClearingSubObjectCode;
	private String paymentClearingFinancialObjectCode;
	private String lockboxNumber;
	private boolean active;

    private ObjectCode badDebtExpenseFinancialObject;
	private ObjectCode salesTaxFinancialObject;
	private ObjectCode paymentClearingFinancialObject;
	private SubObjCd universityClearingSubObject;
	private ObjectCode universityClearingObject;
	private ObjectCode refundFinancialObject;
	private Org processingOrganization;
	private Chart processingChartOfAccount;
	private Account salesTaxAccount;
	private Account universityClearingAccount;
	private Chart universityClearingChartOfAccounts;
    private SubAccount universityClearingSubAccount;
    
	/**
	 * Default constructor.
	 */
	public SystemInformation() {

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
	 * Gets the processingChartOfAccountCode attribute.
	 * 
	 * @return Returns the processingChartOfAccountCode
	 * 
	 */
	public String getProcessingChartOfAccountCode() { 
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 * 
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 * 
	 */
	public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 * 
	 * @return Returns the processingOrganizationCode
	 * 
	 */
	public String getProcessingOrganizationCode() { 
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 * 
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 * 
	 */
	public void setProcessingOrganizationCode(String processingOrganizationCode) {
		this.processingOrganizationCode = processingOrganizationCode;
	}


	/**
	 * Gets the universityFederalEmployerIdentificationNumber attribute.
	 * 
	 * @return Returns the universityFederalEmployerIdentificationNumber
	 * 
	 */
	public String getUniversityFederalEmployerIdentificationNumber() { 
		return universityFederalEmployerIdentificationNumber;
	}

	/**
	 * Sets the universityFederalEmployerIdentificationNumber attribute.
	 * 
	 * @param universityFederalEmployerIdentificationNumber The universityFederalEmployerIdentificationNumber to set.
	 * 
	 */
	public void setUniversityFederalEmployerIdentificationNumber(String universityFederalEmployerIdentificationNumber) {
		this.universityFederalEmployerIdentificationNumber = universityFederalEmployerIdentificationNumber;
	}


	/**
	 * Gets the salesTaxAccountNumber attribute.
	 * 
	 * @return Returns the salesTaxAccountNumber
	 * 
	 */
	public String getSalesTaxAccountNumber() { 
		return salesTaxAccountNumber;
	}

	/**
	 * Sets the salesTaxAccountNumber attribute.
	 * 
	 * @param salesTaxAccountNumber The salesTaxAccountNumber to set.
	 * 
	 */
	public void setSalesTaxAccountNumber(String salesTaxAccountNumber) {
		this.salesTaxAccountNumber = salesTaxAccountNumber;
	}


	/**
	 * Gets the salesTaxFinancialObjectCode attribute.
	 * 
	 * @return Returns the salesTaxFinancialObjectCode
	 * 
	 */
	public String getSalesTaxFinancialObjectCode() { 
		return salesTaxFinancialObjectCode;
	}

	/**
	 * Sets the salesTaxFinancialObjectCode attribute.
	 * 
	 * @param salesTaxFinancialObjectCode The salesTaxFinancialObjectCode to set.
	 * 
	 */
	public void setSalesTaxFinancialObjectCode(String salesTaxFinancialObjectCode) {
		this.salesTaxFinancialObjectCode = salesTaxFinancialObjectCode;
	}


	/**
	 * Gets the refundFinancialObjectCode attribute.
	 * 
	 * @return Returns the refundFinancialObjectCode
	 * 
	 */
	public String getRefundFinancialObjectCode() { 
		return refundFinancialObjectCode;
	}

	/**
	 * Sets the refundFinancialObjectCode attribute.
	 * 
	 * @param refundFinancialObjectCode The refundFinancialObjectCode to set.
	 * 
	 */
	public void setRefundFinancialObjectCode(String refundFinancialObjectCode) {
		this.refundFinancialObjectCode = refundFinancialObjectCode;
	}


	/**
	 * Gets the badDebtExpenseFinancialObjectCode attribute.
	 * 
	 * @return Returns the badDebtExpenseFinancialObjectCode
	 * 
	 */
	public String getBadDebtExpenseFinancialObjectCode() { 
		return badDebtExpenseFinancialObjectCode;
	}

	/**
	 * Sets the badDebtExpenseFinancialObjectCode attribute.
	 * 
	 * @param badDebtExpenseFinancialObjectCode The badDebtExpenseFinancialObjectCode to set.
	 * 
	 */
	public void setBadDebtExpenseFinancialObjectCode(String badDebtExpenseFinancialObjectCode) {
		this.badDebtExpenseFinancialObjectCode = badDebtExpenseFinancialObjectCode;
	}


	/**
	 * Gets the universityClearingChartOfAccountsCode attribute.
	 * 
	 * @return Returns the universityClearingChartOfAccountsCode
	 * 
	 */
	public String getUniversityClearingChartOfAccountsCode() { 
		return universityClearingChartOfAccountsCode;
	}

	/**
	 * Sets the universityClearingChartOfAccountsCode attribute.
	 * 
	 * @param universityClearingChartOfAccountsCode The universityClearingChartOfAccountsCode to set.
	 * 
	 */
	public void setUniversityClearingChartOfAccountsCode(String universityClearingChartOfAccountsCode) {
		this.universityClearingChartOfAccountsCode = universityClearingChartOfAccountsCode;
	}


	/**
	 * Gets the universityClearingAccountNumber attribute.
	 * 
	 * @return Returns the universityClearingAccountNumber
	 * 
	 */
	public String getUniversityClearingAccountNumber() { 
		return universityClearingAccountNumber;
	}

	/**
	 * Sets the universityClearingAccountNumber attribute.
	 * 
	 * @param universityClearingAccountNumber The universityClearingAccountNumber to set.
	 * 
	 */
	public void setUniversityClearingAccountNumber(String universityClearingAccountNumber) {
		this.universityClearingAccountNumber = universityClearingAccountNumber;
	}


	/**
	 * Gets the universityClearingSubAccountNumber attribute.
	 * 
	 * @return Returns the universityClearingSubAccountNumber
	 * 
	 */
	public String getUniversityClearingSubAccountNumber() { 
		return universityClearingSubAccountNumber;
	}

	/**
	 * Sets the universityClearingSubAccountNumber attribute.
	 * 
	 * @param universityClearingSubAccountNumber The universityClearingSubAccountNumber to set.
	 * 
	 */
	public void setUniversityClearingSubAccountNumber(String universityClearingSubAccountNumber) {
		this.universityClearingSubAccountNumber = universityClearingSubAccountNumber;
	}


	/**
	 * Gets the universityClearingObjectCode attribute.
	 * 
	 * @return Returns the universityClearingObjectCode
	 * 
	 */
	public String getUniversityClearingObjectCode() { 
		return universityClearingObjectCode;
	}

	/**
	 * Sets the universityClearingObjectCode attribute.
	 * 
	 * @param universityClearingObjectCode The universityClearingObjectCode to set.
	 * 
	 */
	public void setUniversityClearingObjectCode(String universityClearingObjectCode) {
		this.universityClearingObjectCode = universityClearingObjectCode;
	}


	/**
	 * Gets the universityClearingSubObjectCode attribute.
	 * 
	 * @return Returns the universityClearingSubObjectCode
	 * 
	 */
	public String getUniversityClearingSubObjectCode() { 
		return universityClearingSubObjectCode;
	}

	/**
	 * Sets the universityClearingSubObjectCode attribute.
	 * 
	 * @param universityClearingSubObjectCode The universityClearingSubObjectCode to set.
	 * 
	 */
	public void setUniversityClearingSubObjectCode(String universityClearingSubObjectCode) {
		this.universityClearingSubObjectCode = universityClearingSubObjectCode;
	}


	/**
	 * Gets the paymentClearingFinancialObjectCode attribute.
	 * 
	 * @return Returns the paymentClearingFinancialObjectCode
	 * 
	 */
	public String getPaymentClearingFinancialObjectCode() { 
		return paymentClearingFinancialObjectCode;
	}

	/**
	 * Sets the paymentClearingFinancialObjectCode attribute.
	 * 
	 * @param paymentClearingFinancialObjectCode The paymentClearingFinancialObjectCode to set.
	 * 
	 */
	public void setPaymentClearingFinancialObjectCode(String paymentClearingFinancialObjectCode) {
		this.paymentClearingFinancialObjectCode = paymentClearingFinancialObjectCode;
	}


	/**
	 * Gets the lockboxNumber attribute.
	 * 
	 * @return Returns the lockboxNumber
	 * 
	 */
	public String getLockboxNumber() { 
		return lockboxNumber;
	}

	/**
	 * Sets the lockboxNumber attribute.
	 * 
	 * @param lockboxNumber The lockboxNumber to set.
	 * 
	 */
	public void setLockboxNumber(String lockboxNumber) {
		this.lockboxNumber = lockboxNumber;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean getActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * Gets the badDebtExpenseFinancialObject attribute.
	 * 
	 * @return Returns the badDebtExpenseFinancialObject
	 * 
	 */
	public ObjectCode getBadDebtExpenseFinancialObject() { 
		return badDebtExpenseFinancialObject;
	}

	/**
	 * Sets the badDebtExpenseFinancialObject attribute.
	 * 
	 * @param badDebtExpenseFinancialObject The badDebtExpenseFinancialObject to set.
	 * @deprecated
	 */
	public void setBadDebtExpenseFinancialObject(ObjectCode badDebtExpenseFinancialObject) {
		this.badDebtExpenseFinancialObject = badDebtExpenseFinancialObject;
	}

	/**
	 * Gets the salesTaxFinancialObject attribute.
	 * 
	 * @return Returns the salesTaxFinancialObject
	 * 
	 */
	public ObjectCode getSalesTaxFinancialObject() { 
		return salesTaxFinancialObject;
	}

	/**
	 * Sets the salesTaxFinancialObject attribute.
	 * 
	 * @param salesTaxFinancialObject The salesTaxFinancialObject to set.
	 * @deprecated
	 */
	public void setSalesTaxFinancialObject(ObjectCode salesTaxFinancialObject) {
		this.salesTaxFinancialObject = salesTaxFinancialObject;
	}

	/**
	 * Gets the paymentClearingFinancialObject attribute.
	 * 
	 * @return Returns the paymentClearingFinancialObject
	 * 
	 */
	public ObjectCode getPaymentClearingFinancialObject() { 
		return paymentClearingFinancialObject;
	}

	/**
	 * Sets the paymentClearingFinancialObject attribute.
	 * 
	 * @param paymentClearingFinancialObject The paymentClearingFinancialObject to set.
	 * @deprecated
	 */
	public void setPaymentClearingFinancialObject(ObjectCode paymentClearingFinancialObject) {
		this.paymentClearingFinancialObject = paymentClearingFinancialObject;
	}

	/**
	 * Gets the universityClearingSubObject attribute.
	 * 
	 * @return Returns the universityClearingSubObject
	 * 
	 */
	public SubObjCd getUniversityClearingSubObject() { 
		return universityClearingSubObject;
	}

	/**
	 * Sets the universityClearingSubObject attribute.
	 * 
	 * @param universityClearingSubObject The universityClearingSubObject to set.
	 * @deprecated
	 */
	public void setUniversityClearingSubObject(SubObjCd universityClearingSubObject) {
		this.universityClearingSubObject = universityClearingSubObject;
	}

	/**
	 * Gets the universityClearingObject attribute.
	 * 
	 * @return Returns the universityClearingObject
	 * 
	 */
	public ObjectCode getUniversityClearingObject() { 
		return universityClearingObject;
	}

	/**
	 * Sets the universityClearingObject attribute.
	 * 
	 * @param universityClearingObject The universityClearingObject to set.
	 * @deprecated
	 */
	public void setUniversityClearingObject(ObjectCode universityClearingObject) {
		this.universityClearingObject = universityClearingObject;
	}

	/**
	 * Gets the refundFinancialObject attribute.
	 * 
	 * @return Returns the refundFinancialObject
	 * 
	 */
	public ObjectCode getRefundFinancialObject() { 
		return refundFinancialObject;
	}

	/**
	 * Sets the refundFinancialObject attribute.
	 * 
	 * @param refundFinancialObject The refundFinancialObject to set.
	 * @deprecated
	 */
	public void setRefundFinancialObject(ObjectCode refundFinancialObject) {
		this.refundFinancialObject = refundFinancialObject;
	}

	/**
	 * Gets the processingOrganization attribute.
	 * 
	 * @return Returns the processingOrganization
	 * 
	 */
	public Org getProcessingOrganization() { 
		return processingOrganization;
	}

	/**
	 * Sets the processingOrganization attribute.
	 * 
	 * @param processingOrganization The processingOrganization to set.
	 * @deprecated
	 */
	public void setProcessingOrganization(Org processingOrganization) {
		this.processingOrganization = processingOrganization;
	}

	/**
	 * Gets the processingChartOfAccount attribute.
	 * 
	 * @return Returns the processingChartOfAccount
	 * 
	 */
	public Chart getProcessingChartOfAccount() { 
		return processingChartOfAccount;
	}

	/**
	 * Sets the processingChartOfAccount attribute.
	 * 
	 * @param processingChartOfAccount The processingChartOfAccount to set.
	 * @deprecated
	 */
	public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
		this.processingChartOfAccount = processingChartOfAccount;
	}

	/**
	 * Gets the salesTaxAccount attribute.
	 * 
	 * @return Returns the salesTaxAccount
	 * 
	 */
	public Account getSalesTaxAccount() { 
		return salesTaxAccount;
	}

	/**
	 * Sets the salesTaxAccount attribute.
	 * 
	 * @param salesTaxAccount The salesTaxAccount to set.
	 * @deprecated
	 */
	public void setSalesTaxAccount(Account salesTaxAccount) {
		this.salesTaxAccount = salesTaxAccount;
	}

	/**
	 * Gets the universityClearingAccount attribute.
	 * 
	 * @return Returns the universityClearingAccount
	 * 
	 */
	public Account getUniversityClearingAccount() { 
		return universityClearingAccount;
	}

	/**
	 * Sets the universityClearingAccount attribute.
	 * 
	 * @param universityClearingAccount The universityClearingAccount to set.
	 * @deprecated
	 */
	public void setUniversityClearingAccount(Account universityClearingAccount) {
		this.universityClearingAccount = universityClearingAccount;
	}

	/**
	 * Gets the universityClearingChartOfAccounts attribute.
	 * 
	 * @return Returns the universityClearingChartOfAccounts
	 * 
	 */
	public Chart getUniversityClearingChartOfAccounts() { 
		return universityClearingChartOfAccounts;
	}

	/**
	 * Sets the universityClearingChartOfAccounts attribute.
	 * 
	 * @param universityClearingChartOfAccounts The universityClearingChartOfAccounts to set.
	 * @deprecated
	 */
	public void setUniversityClearingChartOfAccounts(Chart universityClearingChartOfAccounts) {
		this.universityClearingChartOfAccounts = universityClearingChartOfAccounts;
	}

    /**
     * Gets the universityClearingSubAccount attribute. 
     * @return Returns the universityClearingSubAccount.
     */
    public SubAccount getUniversityClearingSubAccount() {
        return universityClearingSubAccount;
    }

    /**
     * Sets the universityClearingSubAccount attribute value.
     * @param universityClearingSubAccount The universityClearingSubAccount to set.
     * @deprecated
     */
    public void setUniversityClearingSubAccount(SubAccount universityClearingSubAccount) {
        this.universityClearingSubAccount = universityClearingSubAccount;
    }    
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("processingChartOfAccountCode", this.processingChartOfAccountCode);
        m.put("processingOrganizationCode", this.processingOrganizationCode);
	    return m;
    }

}
