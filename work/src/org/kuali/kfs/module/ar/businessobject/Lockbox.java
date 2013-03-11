/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.batch.FlatFileTransactionInformation;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Lockbox extends PersistableBusinessObjectBase implements Comparable<Lockbox> {

    private Long invoiceSequenceNumber; //a unique number assigned to the invoice/payment processed.
	private String lockboxNumber; //a unique number assigned to each processing organization.
	private String customerNumber; //customer number.
	private String financialDocumentReferenceInvoiceNumber; //document number of the invoice being processed.
	private Date billingDate; //the date when the customer was billed.
	private KualiDecimal invoiceTotalAmount; //the total amount an invoice was billed for.
	private KualiDecimal invoicePaidOrAppliedAmount; //the amount paid by the customer.
	private Date scannedInvoiceDate; //the date when the customer paid the invoice.
	private String customerPaymentMediumCode; //Cash/Check/Credit. It will always be check for lockbox.
	private Date processedInvoiceDate; //the date when the invoices/payments were processed.
	private Integer batchSequenceNumber; //a batch of invoices/payments processed.
	private String proxyInitiator;
	private String bankCode; //a unique code used to identify the bank associated with this lockbox.

	private KualiDecimal headerTransactionBatchTotal;
    private Integer headerTransactionBatchCount;
    private List<LockboxDetail> lockboxDetails;
    private FlatFileTransactionInformation fileTransactionInformation;

    private PaymentMedium customerPaymentMedium;
    private Bank bank;

	/**
	 * Default constructor.
	 */
	public Lockbox() {
        lockboxDetails = new ArrayList<LockboxDetail>();

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
	@Deprecated
    public void setCustomerPaymentMedium(PaymentMedium customerPaymentMedium) {
		this.customerPaymentMedium = customerPaymentMedium;
	}


	@Override
    public int compareTo(Lockbox lockbox) {
	    if (lockbox == null) {
	        return -1;
	    }
	    if (org.springframework.util.ObjectUtils.nullSafeEquals(this.getBatchSequenceNumber(), lockbox.getBatchSequenceNumber())) {
	        if (org.springframework.util.ObjectUtils.nullSafeEquals(this.getProcessedInvoiceDate(), lockbox.getProcessedInvoiceDate())) {
	            return 0;
	        }
	    }
	    return -1;
    }

    /**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        if (this.invoiceSequenceNumber != null) {
            m.put("invoiceSequenceNumber", this.invoiceSequenceNumber.toString());
        }
	    return m;
    }

    /**
     *
     * This method...
     * @return
     */
    public String getProxyInitiator() {
        return proxyInitiator;
    }

    /**
     *
     * This method...
     * @param proxyInitiator
     */
    public void setProxyInitiator(String proxyInitiator) {
        this.proxyInitiator = proxyInitiator;
    }

    /**
     * Gets the bankCode attribute.
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public List<LockboxDetail> getLockboxDetails() {
        return lockboxDetails;
    }

    public void setLockboxDetails(List<LockboxDetail> lockboxDetails) {
        this.lockboxDetails = lockboxDetails;
    }

    public KualiDecimal getHeaderTransactionBatchTotal() {
        return headerTransactionBatchTotal;
    }

    public void setHeaderTransactionBatchTotal(KualiDecimal headerTransactionBatchTotal) {
        this.headerTransactionBatchTotal = headerTransactionBatchTotal;
    }

    public Integer getHeaderTransactionBatchCount() {
        return headerTransactionBatchCount;
    }

    public void setHeaderTransactionBatchCount(Integer headerTransactionBatchCount) {
        this.headerTransactionBatchCount = headerTransactionBatchCount;
    }


    public FlatFileTransactionInformation getFlatFileTransactionInformation() {
        if (fileTransactionInformation == null ) {
          this.fileTransactionInformation = new FlatFileTransactionInformation(getLockboxNumber());
        }
        return fileTransactionInformation;
    }

}
