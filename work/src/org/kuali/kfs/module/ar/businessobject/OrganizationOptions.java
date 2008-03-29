package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.State;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationOptions extends PersistableBusinessObjectBase {

	private String chartOfAccountsCode;
	private String organizationCode;
	private String processingChartOfAccountCode;
	private String processingOrganizationCode;
	private String printInvoiceIndicator;
	private String organizationPaymentTermsText;
	private String organizationMessageText;
	private String organizationRemitToAddressName;
	private String organizationRemitToLine1StreetAddress;
	private String organizationRemitToLine2StreetAddress;
	private String organizationRemitToCityName;
	private String organizationRemitToStateCode;
	private String organizationRemitToZipCode;
	private String organizationPhoneNumber;
	private String organization800PhoneNumber;
	private String organizationFaxNumber;
	private String universityName;
	private String organizationCheckPayableToName;

    private Org organization;
	private Chart chartOfAccounts;
	private Chart processingChartOfAccount;
	private Org processingOrganization;
    private State organizationRemitToState;
    
	/**
	 * Default constructor.
	 */
	public OrganizationOptions() {

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
	 * Gets the printInvoiceIndicator attribute.
	 * 
	 * @return Returns the printInvoiceIndicator
	 * 
	 */
	public String getPrintInvoiceIndicator() { 
		return printInvoiceIndicator;
	}

	/**
	 * Sets the printInvoiceIndicator attribute.
	 * 
	 * @param printInvoiceIndicator The printInvoiceIndicator to set.
	 * 
	 */
	public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
		this.printInvoiceIndicator = printInvoiceIndicator;
	}


	/**
	 * Gets the organizationPaymentTermsText attribute.
	 * 
	 * @return Returns the organizationPaymentTermsText
	 * 
	 */
	public String getOrganizationPaymentTermsText() { 
		return organizationPaymentTermsText;
	}

	/**
	 * Sets the organizationPaymentTermsText attribute.
	 * 
	 * @param organizationPaymentTermsText The organizationPaymentTermsText to set.
	 * 
	 */
	public void setOrganizationPaymentTermsText(String organizationPaymentTermsText) {
		this.organizationPaymentTermsText = organizationPaymentTermsText;
	}


	/**
	 * Gets the organizationMessageText attribute.
	 * 
	 * @return Returns the organizationMessageText
	 * 
	 */
	public String getOrganizationMessageText() { 
		return organizationMessageText;
	}

	/**
	 * Sets the organizationMessageText attribute.
	 * 
	 * @param organizationMessageText The organizationMessageText to set.
	 * 
	 */
	public void setOrganizationMessageText(String organizationMessageText) {
		this.organizationMessageText = organizationMessageText;
	}


	/**
	 * Gets the organizationRemitToAddressName attribute.
	 * 
	 * @return Returns the organizationRemitToAddressName
	 * 
	 */
	public String getOrganizationRemitToAddressName() { 
		return organizationRemitToAddressName;
	}

	/**
	 * Sets the organizationRemitToAddressName attribute.
	 * 
	 * @param organizationRemitToAddressName The organizationRemitToAddressName to set.
	 * 
	 */
	public void setOrganizationRemitToAddressName(String organizationRemitToAddressName) {
		this.organizationRemitToAddressName = organizationRemitToAddressName;
	}


	/**
	 * Gets the organizationRemitToLine1StreetAddress attribute.
	 * 
	 * @return Returns the organizationRemitToLine1StreetAddress
	 * 
	 */
	public String getOrganizationRemitToLine1StreetAddress() { 
		return organizationRemitToLine1StreetAddress;
	}

	/**
	 * Sets the organizationRemitToLine1StreetAddress attribute.
	 * 
	 * @param organizationRemitToLine1StreetAddress The organizationRemitToLine1StreetAddress to set.
	 * 
	 */
	public void setOrganizationRemitToLine1StreetAddress(String organizationRemitToLine1StreetAddress) {
		this.organizationRemitToLine1StreetAddress = organizationRemitToLine1StreetAddress;
	}


	/**
	 * Gets the organizationRemitToLine2StreetAddress attribute.
	 * 
	 * @return Returns the organizationRemitToLine2StreetAddress
	 * 
	 */
	public String getOrganizationRemitToLine2StreetAddress() { 
		return organizationRemitToLine2StreetAddress;
	}

	/**
	 * Sets the organizationRemitToLine2StreetAddress attribute.
	 * 
	 * @param organizationRemitToLine2StreetAddress The organizationRemitToLine2StreetAddress to set.
	 * 
	 */
	public void setOrganizationRemitToLine2StreetAddress(String organizationRemitToLine2StreetAddress) {
		this.organizationRemitToLine2StreetAddress = organizationRemitToLine2StreetAddress;
	}


	/**
	 * Gets the organizationRemitToCityName attribute.
	 * 
	 * @return Returns the organizationRemitToCityName
	 * 
	 */
	public String getOrganizationRemitToCityName() { 
		return organizationRemitToCityName;
	}

	/**
	 * Sets the organizationRemitToCityName attribute.
	 * 
	 * @param organizationRemitToCityName The organizationRemitToCityName to set.
	 * 
	 */
	public void setOrganizationRemitToCityName(String organizationRemitToCityName) {
		this.organizationRemitToCityName = organizationRemitToCityName;
	}


	/**
	 * Gets the organizationRemitToStateCode attribute.
	 * 
	 * @return Returns the organizationRemitToStateCode
	 * 
	 */
	public String getOrganizationRemitToStateCode() { 
		return organizationRemitToStateCode;
	}

	/**
	 * Sets the organizationRemitToStateCode attribute.
	 * 
	 * @param organizationRemitToStateCode The organizationRemitToStateCode to set.
	 * 
	 */
	public void setOrganizationRemitToStateCode(String organizationRemitToStateCode) {
		this.organizationRemitToStateCode = organizationRemitToStateCode;
	}


	/**
	 * Gets the organizationRemitToZipCode attribute.
	 * 
	 * @return Returns the organizationRemitToZipCode
	 * 
	 */
	public String getOrganizationRemitToZipCode() { 
		return organizationRemitToZipCode;
	}

	/**
	 * Sets the organizationRemitToZipCode attribute.
	 * 
	 * @param organizationRemitToZipCode The organizationRemitToZipCode to set.
	 * 
	 */
	public void setOrganizationRemitToZipCode(String organizationRemitToZipCode) {
		this.organizationRemitToZipCode = organizationRemitToZipCode;
	}

	/**
	 * Gets the organizationPhoneNumber attribute.
	 * 
	 * @return Returns the organizationPhoneNumber
	 * 
	 */
	public String getOrganizationPhoneNumber() { 
		return organizationPhoneNumber;
	}

	/**
	 * Sets the organizationPhoneNumber attribute.
	 * 
	 * @param organizationPhoneNumber The organizationPhoneNumber to set.
	 * 
	 */
	public void setOrganizationPhoneNumber(String organizationPhoneNumber) {
		this.organizationPhoneNumber = organizationPhoneNumber;
	}


	/**
	 * Gets the organization800PhoneNumber attribute.
	 * 
	 * @return Returns the organization800PhoneNumber
	 * 
	 */
	public String getOrganization800PhoneNumber() { 
		return organization800PhoneNumber;
	}

	/**
	 * Sets the organization800PhoneNumber attribute.
	 * 
	 * @param organization800PhoneNumber The organization800PhoneNumber to set.
	 * 
	 */
	public void setOrganization800PhoneNumber(String organization800PhoneNumber) {
		this.organization800PhoneNumber = organization800PhoneNumber;
	}


	/**
	 * Gets the organizationFaxNumber attribute.
	 * 
	 * @return Returns the organizationFaxNumber
	 * 
	 */
	public String getOrganizationFaxNumber() { 
		return organizationFaxNumber;
	}

	/**
	 * Sets the organizationFaxNumber attribute.
	 * 
	 * @param organizationFaxNumber The organizationFaxNumber to set.
	 * 
	 */
	public void setOrganizationFaxNumber(String organizationFaxNumber) {
		this.organizationFaxNumber = organizationFaxNumber;
	}


	/**
	 * Gets the universityName attribute.
	 * 
	 * @return Returns the universityName
	 * 
	 */
	public String getUniversityName() { 
		return universityName;
	}

	/**
	 * Sets the universityName attribute.
	 * 
	 * @param universityName The universityName to set.
	 * 
	 */
	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}


	/**
	 * Gets the organizationCheckPayableToName attribute.
	 * 
	 * @return Returns the organizationCheckPayableToName
	 * 
	 */
	public String getOrganizationCheckPayableToName() { 
		return organizationCheckPayableToName;
	}

	/**
	 * Sets the organizationCheckPayableToName attribute.
	 * 
	 * @param organizationCheckPayableToName The organizationCheckPayableToName to set.
	 * 
	 */
	public void setOrganizationCheckPayableToName(String organizationCheckPayableToName) {
		this.organizationCheckPayableToName = organizationCheckPayableToName;
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
     * Gets the organizationRemitToState attribute. 
     * @return Returns the organizationRemitToState.
     */
    public State getOrganizationRemitToState() {
        return organizationRemitToState;
    }

    /**
     * Sets the organizationRemitToState attribute value.
     * @param organizationRemitToState The organizationRemitToState to set.
     * @deprecated
     */
    public void setOrganizationRemitToState(State organizationRemitToState) {
        this.organizationRemitToState = organizationRemitToState;
    }    
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
	    return m;
    }
    
    /**
     * This method (a hack by any other name...) returns a string so that an organization options can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Organization Options"
     */
    public String getOrganizationOptionsViewer() {
        return "View Organization Options";
    }

}
