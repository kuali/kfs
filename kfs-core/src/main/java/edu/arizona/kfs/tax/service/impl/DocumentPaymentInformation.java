package edu.arizona.kfs.tax.service.impl;

import java.util.Date;

public class DocumentPaymentInformation {
	private String paymentMethodCode;
	private Date payDate;
	private String invoiceNumber;

	public String getPaymentMethodCode() {
		return paymentMethodCode;
	}

	public void setPaymentMethodCode(String paymentMethodCode) {
		this.paymentMethodCode = paymentMethodCode;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public boolean isCheckACHPayment() {
		return ("A".equals(paymentMethodCode) || "P".equals(paymentMethodCode));
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

}
