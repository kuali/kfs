package edu.arizona.kfs.module.tax;

import java.text.SimpleDateFormat;

/**
 * This class houses the miscellaneous constants used within the Tax Module.
 */
public class TaxConstants {

    public static final String TAX_NAMESPACE_CODE = "KFS-TAX";

    public static class DocumentTypes {
        public static final String TAX_1099_PAYER_DOCUMENT = "TXPR";
        public static final String TAX_1099_PAYEE_DOCUMENT = "TXPA";
        public static final String TAX_1099_PAYMENT_DOCUMENT = "TXPM";
        public static final String TAX_1099_INCOME_TYPE_DOCUMENT = "TXIT";
        public static final String PAYMENT_REQUEST_DOCUMENT = "PREQ";
        public static final String PAYMENT_REQUEST_DOCUMENT_NON_CHECK = "PRNC";
        public static final String CREDIT_MEMO_DOCUMENT = "CM";
        public static final String CREDIT_MEMO_DOCUMENT_NON_CHECK = "CMNC";
        public static final String DISBURSEMENT_VOUCHER_DOCUMENT = "DV";
        public static final String DISBURSEMENT_VOUCHER_DOCUMENT_CHECKACH = "DVCA";
        public static final String DISBURSEMENT_VOUCHER_DOCUMENT_NON_CHECK = "DVNC";
    }

    public static class AddressTypes {
        public static final String TAX = "TX";
        public static final String REMIT = "RM";
        public static final String PURCHASE_ORDER = "PO";
    }

    // add Notes/Attachment Tab to 1099 Process Payment Inquiry Screen
    public static class TaxCreateAndUpdateNotePrefixes {
        public static final String ADD = "Add";
        public static final String CHANGE = "Change";
    }

    // Constants used for making the 1099 Forms.
    public static class Form1099 {
        public static final int FORM_1099_CORRECTION_INDICATOR_LENGTH = 8;
        public static final int FORM_1099_DEFAULT_FONT_SIZE = 10;
        public static final String PDF_1099_FILENAME_PREFIX = "f1099msc";
        public static final int PDF_1099_PAGE_COPY_A = 2;
        public static final int PDF_1099_PAGE_COPY_1 = 3;
        public static final int PDF_1099_PAGE_COPY_B = 4;
        public static final int PDF_1099_PAGE_COPY_C = 7;
        public static final int PDF_1099_PAGE_COPY_2 = 6;
    }

    public static class TinTypeFinder {
        public static final String EIN_CODE = "1";
        public static final String SSN_CODE = "2";
        public static final String EIN = "EIN";
        public static final String SSN = "SSN";
    }

    public static class TestTypeFinder {
        public static final String T_CODE = "T";
        public static final String T = "T";
    }

    public static class PaymentTypeFinder {
        // These are related to KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes
        public static final String AMOUNT_CODE_1_TYPE = "Rents";
        public static final String AMOUNT_CODE_2_TYPE = "Royalties";
        public static final String AMOUNT_CODE_3_TYPE = "Other Income";
        public static final String AMOUNT_CODE_4_TYPE = "Fed. Income Tax Witheld";
        public static final String AMOUNT_CODE_5_TYPE = "Fishing Boat";
        public static final String AMOUNT_CODE_6_TYPE = "Medical and Health Care";
        public static final String AMOUNT_CODE_7_TYPE = "Nonemployee Compensation";
        public static final String AMOUNT_CODE_8_TYPE = "Dividends or Interest";
        public static final String AMOUNT_CODE_A_TYPE = "Crop Insurance";
        public static final String AMOUNT_CODE_B_TYPE = "Golden Parachute";
        public static final String AMOUNT_CODE_C_TYPE = "Legal Services";
        public static final String AMOUNT_CODE_D_TYPE = "Sec. 409A Deferrals";
        public static final String AMOUNT_CODE_E_TYPE = "Sec. 409A Income";
    }

    public static final SimpleDateFormat FILE_TIMESTAMP = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final String PAYMENT_METHOD_CODE_P = "P";
    public static final String PAYMENT_METHOD_CODE_A = "A";

    public static final String PAYEE_MASTER_EXTRACT_STEP = "PayeeMasterExtractStep";
    public static final String TAX_USER_ROLE = "UA 1099 Tax Specialist";
    public static final String REPORT_TEMPLATE_CLASSPATH = "edu/arizona/kfs/module/tax/report/";
    public static final String REPORT_MESSAGES_CLASSPATH = "edu/arizona/kfs/module/tax/report/ExceptionReport";
    public static final String ELEC_REPORT_FILE_NAME = "ElectronicFilingReport";

    public static final String DBA_BUSINESS_PREFIX = "DBA";
    public static final String HELD_TAX_ALL = "HTXA";
    public static final String TAX_ADDRESS_TYPE_CD = "TX";
    public static final int VENDOR_MAX_ADDRESS_LENGTH = 40;

}
