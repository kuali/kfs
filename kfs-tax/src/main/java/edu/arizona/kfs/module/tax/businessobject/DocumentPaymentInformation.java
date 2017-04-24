package edu.arizona.kfs.module.tax.businessobject;

import java.util.Date;

import edu.arizona.kfs.module.tax.TaxConstants;

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
        return (TaxConstants.PAYMENT_METHOD_CODE_A.equals(paymentMethodCode) || TaxConstants.PAYMENT_METHOD_CODE_P.equals(paymentMethodCode));
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

}
