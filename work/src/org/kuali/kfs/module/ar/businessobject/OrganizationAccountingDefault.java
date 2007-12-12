package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationAccountingDefault extends PersistableBusinessObjectBase {

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
	private String defaultPaymentProjectCode;
	private String defaultPaymentOrganizationReferenceIdentifier;
	private String writeoffObjectCode;

    private ObjectCode defaultInvoiceFinancialObject;
	private SubObjCd defaultInvoiceFinancialSubObject;
	private ObjectCode writeoffObject;
	private ObjectCode organizationLateChargeObject;
	private Chart chartOfAccounts;
	private Org organization;
	private Chart defaultInvoiceChartOfAccounts;
	private SubAccount defaultInvoiceSubAccount;
	private Account defaultInvoiceAccount;
	private ProjectCode defaultInvoiceProject;
	private Account defaultPaymentAccount;
	private Chart defaultPaymentChartOfAccounts;
	private SubAccount defaultPaymentSubAccount;
	private ProjectCode defaultPaymentProject;
	private Options universityFiscal;

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
	 * Gets the writeoffObjectCode attribute.
	 * 
	 * @return Returns the writeoffObjectCode
	 * 
	 */
	public String getWriteoffObjectCode() { 
		return writeoffObjectCode;
	}

	/**
	 * Sets the writeoffObjectCode attribute.
	 * 
	 * @param writeoffObjectCode The writeoffObjectCode to set.
	 * 
	 */
	public void setWriteoffObjectCode(String writeoffObjectCode) {
		this.writeoffObjectCode = writeoffObjectCode;
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
	public SubObjCd getDefaultInvoiceFinancialSubObject() { 
		return defaultInvoiceFinancialSubObject;
	}

	/**
	 * Sets the defaultInvoiceFinancialSubObject attribute.
	 * 
	 * @param defaultInvoiceFinancialSubObject The defaultInvoiceFinancialSubObject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceFinancialSubObject(SubObjCd defaultInvoiceFinancialSubObject) {
		this.defaultInvoiceFinancialSubObject = defaultInvoiceFinancialSubObject;
	}

	/**
	 * Gets the writeoffObject attribute.
	 * 
	 * @return Returns the writeoffObject
	 * 
	 */
	public ObjectCode getWriteoffObject() { 
		return writeoffObject;
	}

	/**
	 * Sets the writeoffObject attribute.
	 * 
	 * @param writeoffObject The writeoffObject to set.
	 * @deprecated
	 */
	public void setWriteoffObject(ObjectCode writeoffObject) {
		this.writeoffObject = writeoffObject;
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
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
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
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
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
        m.put("organizationCode", this.organizationCode);
	    return m;
    }
}
