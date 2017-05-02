package edu.arizona.kfs.sys;

public class KFSPropertyConstants extends org.kuali.kfs.sys.KFSPropertyConstants {

    // 1099 Income Type Business Object and Maintenance Document
    public static class IncomeTypeFields {
        public static final String DOCUMENT_INCOME_TYPES = "document.incomeTypes";
        public static final String NEW_INCOME_TYPE_ERROR1 = "newIncomeTypeError1";
        public static final String NEW_INCOME_TYPE_ERROR2 = "newIncomeTypeError2";
        public static final String NEW_INCOME_TYPE_ERROR3 = "newIncomeTypeError3";
        public static final String INCOME_TYPE_CODE = "incomeTypeCode";
        public static final String INCOME_TYPE_DESCRIPTION = "incomeTypeDescription";
        public static final String INCOME_TYPE_BOX = "incomeTypeBox";
    }

    public static final String EXTENSION_INVOICE_NUMBER = "extension.invoiceNumber";
    public static final String DV_PAYEE_DETAIL_PAYEE_ID_NUMBER = DV_PAYEE_DETAIL + "." + DISB_VCHR_PAYEE_ID_NUMBER;
    public static final String DV_PAYEE_DETAIL_PAYEE_TYPE_CODE = DV_PAYEE_DETAIL + ".disbursementVoucherPayeeTypeCode";
    public static final String UPPER_SOURCE_ACCOUNTING_LINES_EXTENSION_INVOICE_NUMBER = "upper(" + SOURCE_ACCOUNTING_LINES + "." + EXTENSION + ".invoiceNumber)";
    public static final String UPPER_INVOICE_NUMBER = "upper(invoiceNumber)";
    public static final String DOC_ROUTE_STATUS = "docRouteStatus"; // This should really be a Rice property constant.
    public static final String FINALIZED_DATE = "finalizedDate"; // This should really be a Rice property constant.
    public static final String BATCH_FILE_UPLOADS_BATCH_FILE_NAME = "batchFileName";
    public static final String BATCH_FILE_UPLOADS_FILE_PROCESS_TIMESTAMP = "fileProcessTimestamp";
    public static final String INVENTORY_UNIT_CODE = "inventoryUnitCode";

    public static final String GEC_DOCUMENT_NUMBER = "gecDocumentNumber";
    public static final String ENTRY_ID = "entryId";
    public static final String CARD_CYCLE_VOL_LIMIT = "cardCycleVolLimit";

    public static final String PAYMENT_METHOD_CODE = "paymentMethodCode";
    public static final String PAID_DATE = "paidDate";

    public static final String IMMEDIATE_PAYMENT_INDICATOR = "immediatePaymentIndicator";
}
