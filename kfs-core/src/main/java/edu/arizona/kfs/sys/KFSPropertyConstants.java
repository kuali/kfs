package edu.arizona.kfs.sys;

public class KFSPropertyConstants extends org.kuali.kfs.sys.KFSPropertyConstants {

    public static final String EXTENSION_INVOICE_NUMBER = "extension.invoiceNumber";
    public static final String DV_PAYEE_DETAIL_PAYEE_ID_NUMBER = DV_PAYEE_DETAIL + "." + DISB_VCHR_PAYEE_ID_NUMBER;
    public static final String DV_PAYEE_DETAIL_PAYEE_TYPE_CODE = DV_PAYEE_DETAIL + ".disbursementVoucherPayeeTypeCode";
    public static final String UPPER_SOURCE_ACCOUNTING_LINES_EXTENSION_INVOICE_NUMBER = "upper("+SOURCE_ACCOUNTING_LINES + "." + EXTENSION + ".invoiceNumber)";
    public static final String UPPER_INVOICE_NUMBER = "upper(invoiceNumber)";
    public static final String DOC_ROUTE_STATUS = "docRouteStatus"; //This should really be a Rice property constant.
    public static final String FINALIZED_DATE = "finalizedDate"; //This should really be a Rice property constant.
    public static final String BATCH_FILE_UPLOADS_BATCH_FILE_NAME = "batchFileName";
    public static final String BATCH_FILE_UPLOADS_FILE_PROCESS_TIMESTAMP = "fileProcessTimestamp";

    public static final String GEC_DOCUMENT_NUMBER = "gecDocumentNumber";

}
