package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Lockbox extends PersistableBusinessObjectBase {

	private Long invoiceSequenceNumber;
	private String lockboxNumber;
	private String customerNumber;
	private String financialDocumentReferenceInvoiceNumber;
	private Date billingDate;
	private KualiDecimal invoiceTotalAmount;
	private KualiDecimal invoicePaidOrAppliedAmount;
	private Date scannedInvoiceDate;
	private String customerPaymentMediumCode;
	private Date processedInvoiceDate;
	private Integer batchSequenceNumber;

    private PaymentMedium customerPaymentMedium;

	/**
	 * Default constructor.
	 */
	public Lockbox() {

	}

	/**
	 * Gets the invoiceSequenceNumber attribute.
	 * 
	 * @return Returns the invoiceSequenceNumber
	 * 
	 */
	public Long getInvoiceSequenceNumber() { 
		return invoiceSequenceNumber;
	}

	/**
	 * Sets the invoiceSequenceNumber attribute.
	 * 
	 * @param invoiceSequenceNumber The invoiceSequenceNumber to set.
	 * 
	 */
	public void setInvoiceSequenceNumber(Long invoiceSequenceNumber) {
		this.invoiceSequenceNumber = invoiceSequenceNumber;
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
	 * Gets the customerNumber attribute.
	 * 
	 * @return Returns the customerNumber
	 * 
	 */
	public String getCustomerNumber() { 
		return customerNumber;
	}

	/**
	 * Sets the customerNumber attribute.
	 * 
	 * @param customerNumber The customerNumber to set.
	 * 
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
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
	 * Gets the billingDate attribute.
	 * 
	 * @return Returns the billingDate
	 * 
	 */
	public Date getBillingDate() { 
		return billingDate;
	}

	/**
	 * Sets the billingDate attribute.
	 * 
	 * @param billingDate The billingDate to set.
	 * 
	 */
	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}


	/**
	 * Gets the invoiceTotalAmount attribute.
	 * 
	 * @return Returns the invoiceTotalAmount
	 * 
	 */
	public KualiDecimal getInvoiceTotalAmount() { 
		return invoiceTotalAmount;
	}

	/**
	 * Sets the invoiceTotalAmount attribute.
	 * 
	 * @param invoiceTotalAmount The invoiceTotalAmount to set.
	 * 
	 */
	public void setInvoiceTotalAmount(KualiDecimal invoiceTotalAmount) {
		this.invoiceTotalAmount = invoiceTotalAmount;
	}


	/**
	 * Gets the invoicePaidOrAppliedAmount attribute.
	 * 
	 * @return Returns the invoicePaidOrAppliedAmount
	 * 
	 */
	public KualiDecimal getInvoicePaidOrAppliedAmount() { 
		return invoicePaidOrAppliedAmount;
	}

	/**
	 * Sets the invoicePaidOrAppliedAmount attribute.
	 * 
	 * @param invoicePaidOrAppliedAmount The invoicePaidOrAppliedAmount to set.
	 * 
	 */
	public void setInvoicePaidOrAppliedAmount(KualiDecimal invoicePaidOrAppliedAmount) {
		this.invoicePaidOrAppliedAmount = invoicePaidOrAppliedAmount;
	}


	/**
	 * Gets the scannedInvoiceDate attribute.
	 * 
	 * @return Returns the scannedInvoiceDate
	 * 
	 */
	public Date getScannedInvoiceDate() { 
		return scannedInvoiceDate;
	}

	/**
	 * Sets the scannedInvoiceDate attribute.
	 * 
	 * @param scannedInvoiceDate The scannedInvoiceDate to set.
	 * 
	 */
	public void setScannedInvoiceDate(Date scannedInvoiceDate) {
		this.scannedInvoiceDate = scannedInvoiceDate;
	}


	/**
	 * Gets the customerPaymentMediumCode attribute.
	 * 
	 * @return Returns the customerPaymentMediumCode
	 * 
	 */
	public String getCustomerPaymentMediumCode() { 
		return customerPaymentMediumCode;
	}

	/**
	 * Sets the customerPaymentMediumCode attribute.
	 * 
	 * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
	 * 
	 */
	public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
		this.customerPaymentMediumCode = customerPaymentMediumCode;
	}


	/**
	 * Gets the processedInvoiceDate attribute.
	 * 
	 * @return Returns the processedInvoiceDate
	 * 
	 */
	public Date getProcessedInvoiceDate() { 
		return processedInvoiceDate;
	}

	/**
	 * Sets the processedInvoiceDate attribute.
	 * 
	 * @param processedInvoiceDate The processedInvoiceDate to set.
	 * 
	 */
	public void setProcessedInvoiceDate(Date processedInvoiceDate) {
		this.processedInvoiceDate = processedInvoiceDate;
	}


	/**
	 * Gets the batchSequenceNumber attribute.
	 * 
	 * @return Returns the batchSequenceNumber
	 * 
	 */
	public Integer getBatchSequenceNumber() { 
		return batchSequenceNumber;
	}

	/**
	 * Sets the batchSequenceNumber attribute.
	 * 
	 * @param batchSequenceNumber The batchSequenceNumber to set.
	 * 
	 */
	public void setBatchSequenceNumber(Integer batchSequenceNumber) {
		this.batchSequenceNumber = batchSequenceNumber;
	}


	/**
	 * Gets the customerPaymentMedium attribute.
	 * 
	 * @return Returns the customerPaymentMedium
	 * 
	 */
	public PaymentMedium getCustomerPaymentMedium() { 
		return customerPaymentMedium;
	}

	/**
	 * Sets the customerPaymentMedium attribute.
	 * 
	 * @param customerPaymentMedium The customerPaymentMedium to set.
	 * @deprecated
	 */
	public void setCustomerPaymentMedium(PaymentMedium customerPaymentMedium) {
		this.customerPaymentMedium = customerPaymentMedium;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceSequenceNumber != null) {
            m.put("invoiceSequenceNumber", this.invoiceSequenceNumber.toString());
        }
	    return m;
    }
}
