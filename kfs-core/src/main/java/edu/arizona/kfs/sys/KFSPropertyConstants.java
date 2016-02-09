package edu.arizona.kfs.sys;

public class KFSPropertyConstants extends org.kuali.kfs.sys.KFSPropertyConstants {

    public static final String EXTENSION_INVOICE_NUMBER = "extension.invoiceNumber";
    public static final String DV_PAYEE_DETAIL_PAYEE_ID_NUMBER = DV_PAYEE_DETAIL + "." + DISB_VCHR_PAYEE_ID_NUMBER;
    public static final String DV_PAYEE_DETAIL_PAYEE_TYPE_CODE = DV_PAYEE_DETAIL + ".disbursementVoucherPayeeTypeCode";
    public static final String UPPER_SOURCE_ACCOUNTING_LINES_EXTENSION_INVOICE_NUMBER = "upper("+SOURCE_ACCOUNTING_LINES + "." + EXTENSION + ".invoiceNumber)";
    public static final String UPPER_INVOICE_NUMBER = "upper(invoiceNumber)";

}
