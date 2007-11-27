package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OpenPaymentDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
//    private String financialDocumentTypeCode;
//    private Date financialDocumentApprovedDate;    
    private Date entryDate;  
    private String customerNumber;    
    private String processingChartOfAccountCode;
    private String processingOrganizationCode;    
    private String customerName;    
    private KualiDecimal financialDocumentLineAmount;
    private KualiDecimal paymentAppliedAmount;    
    private String financialDocumentDescription; 

    private Chart processingChartOfAccount;
    private Org processingOrganization;
    private Customer customer;
    
    /**
	 * Default constructor.
	 */
	public OpenPaymentDetail() {

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
     * Gets the customerName attribute. 
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerNumber attribute. 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the entryDate attribute. 
     * @return Returns the entryDate.
     */
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * Sets the entryDate attribute value.
     * @param entryDate The entryDate to set.
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * Gets the financialDocumentTypeCode attribute. 
     * @return Returns the financialDocumentTypeCode.
     */
//    public String getFinancialDocumentTypeCode() {
//        return financialDocumentTypeCode;
//    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
//    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
//        this.financialDocumentTypeCode = financialDocumentTypeCode;
//    }

    /**
     * Gets the processingChartOfAccountCode attribute. 
     * @return Returns the processingChartOfAccountCode.
     */
    public String getProcessingChartOfAccountCode() {
        return processingChartOfAccountCode;
    }

    /**
     * Sets the processingChartOfAccountCode attribute value.
     * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
     */
    public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
        this.processingChartOfAccountCode = processingChartOfAccountCode;
    }

    /**
     * Gets the processingOrganizationCode attribute. 
     * @return Returns the processingOrganizationCode.
     */
    public String getProcessingOrganizationCode() {
        return processingOrganizationCode;
    }

    /**
     * Sets the processingOrganizationCode attribute value.
     * @param processingOrganizationCode The processingOrganizationCode to set.
     */
    public void setProcessingOrganizationCode(String processingOrganizationCode) {
        this.processingOrganizationCode = processingOrganizationCode;
    }

    /**
     * Gets the financialDocumentApprovedDate attribute. 
     * @return Returns the financialDocumentApprovedDate.
     */
//    public Date getFinancialDocumentApprovedDate() {
//        return financialDocumentApprovedDate;
//    }

    /**
     * Sets the financialDocumentApprovedDate attribute value.
     * @param financialDocumentApprovedDate The financialDocumentApprovedDate to set.
     */
//    public void setFinancialDocumentApprovedDate(Date financialDocumentApprovedDate) {
//        this.financialDocumentApprovedDate = financialDocumentApprovedDate;
//    }

    /**
     * Gets the financialDocumentDescription attribute. 
     * @return Returns the financialDocumentDescription.
     */
    public String getFinancialDocumentDescription() {
        return financialDocumentDescription;
    }

    /**
     * Sets the financialDocumentDescription attribute value.
     * @param financialDocumentDescription The financialDocumentDescription to set.
     */
    public void setFinancialDocumentDescription(String financialDocumentDescription) {
        this.financialDocumentDescription = financialDocumentDescription;
    }

    /**
     * Gets the financialDocumentLineAmount attribute. 
     * @return Returns the financialDocumentLineAmount.
     */
    public KualiDecimal getFinancialDocumentLineAmount() {
        return financialDocumentLineAmount;
    }

    /**
     * Sets the financialDocumentLineAmount attribute value.
     * @param financialDocumentLineAmount The financialDocumentLineAmount to set.
     */
    public void setFinancialDocumentLineAmount(KualiDecimal financialDocumentLineAmount) {
        this.financialDocumentLineAmount = financialDocumentLineAmount;
    }

    /**
     * Gets the paymentAppliedAmount attribute. 
     * @return Returns the paymentAppliedAmount.
     */
    public KualiDecimal getPaymentAppliedAmount() {
        return paymentAppliedAmount;
    }

    /**
     * Sets the paymentAppliedAmount attribute value.
     * @param paymentAppliedAmount The paymentAppliedAmount to set.
     */
    public void setPaymentAppliedAmount(KualiDecimal paymentAppliedAmount) {
        this.paymentAppliedAmount = paymentAppliedAmount;
    }

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the processingChartOfAccount attribute. 
     * @return Returns the processingChartOfAccount.
     */
    public Chart getProcessingChartOfAccount() {
        return processingChartOfAccount;
    }

    /**
     * Sets the processingChartOfAccount attribute value.
     * @param processingChartOfAccount The processingChartOfAccount to set.
     * @deprecated
     */
    public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
        this.processingChartOfAccount = processingChartOfAccount;
    }

    /**
     * Gets the processingOrganization attribute. 
     * @return Returns the processingOrganization.
     */
    public Org getProcessingOrganization() {
        return processingOrganization;
    }

    /**
     * Sets the processingOrganization attribute value.
     * @param processingOrganization The processingOrganization to set.
     * @deprecated
     */
    public void setProcessingOrganization(Org processingOrganization) {
        this.processingOrganization = processingOrganization;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }    
    
}
