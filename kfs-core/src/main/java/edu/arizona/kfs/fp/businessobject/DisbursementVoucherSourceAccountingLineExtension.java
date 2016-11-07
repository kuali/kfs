package edu.arizona.kfs.fp.businessobject;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

public class DisbursementVoucherSourceAccountingLineExtension extends PersistableBusinessObjectExtensionBase {

    private static final long serialVersionUID = -4295791634480404590L;

    private String documentNumber;
    private Integer sequenceNumber;
    private String financialDocumentLineTypeCode;
    private String invoiceNumber;

    public DisbursementVoucherSourceAccountingLineExtension() {
        financialDocumentLineTypeCode = KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
