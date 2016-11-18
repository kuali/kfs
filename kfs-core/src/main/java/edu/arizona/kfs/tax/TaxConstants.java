package edu.arizona.kfs.tax;

import java.util.ArrayList;
import java.util.List;

public class TaxConstants {

	public static final String PAYEE_MASTER_EXTRACT_STEP = "PayeeMasterExtractStep";
    public static final String TAX_USER_ROLE = "UA 1099 Tax Specialist";
    public static final String NMSPC_CD = "KFS-TAX";
    public static final String REPORT_TEMPLATE_CLASSPATH = "com/rsmart/kuali/kfs/tax/report/";
    public static final String REPORT_MESSAGES_CLASSPATH = REPORT_TEMPLATE_CLASSPATH + "ExceptionReport.properties";
    public static final String ELEC_REPORT_FILE_NAME = "ElectronicFilingReport";
    public static final String AMOUNT_CODE_A = "A"; // Crop Insurance
    public static final String AMOUNT_CODE_B = "B"; // Golden Parachute
    public static final String AMOUNT_CODE_C = "C"; // Legal Services
    public static final String AMOUNT_CODE_D = "D"; // Sec. 409A Deferrals
    public static final String AMOUNT_CODE_E = "E"; // Sec. 409A Inc
    public static final String AMOUNT_CODE_1 = "1"; // Rents
    public static final String AMOUNT_CODE_2 = "2"; // Royalties
    public static final String AMOUNT_CODE_3 = "3"; // Other Income
    public static final String AMOUNT_CODE_4 = "4"; // Fed. Income Tax Witheld
    public static final String AMOUNT_CODE_5 = "5"; // Fishing
    public static final String AMOUNT_CODE_6 = "6"; // Medical and Health Care
    public static final String AMOUNT_CODE_7 = "7"; // Nonemployee Compensation
    public static final String AMOUNT_CODE_8 = "8"; // Dividends
    
    // Tag and JSP for DV, PREQ and CM Documents.. 
    public static final String FINANCIAL_PROCESSING_NAMESPACE_CODE = "KFS-FP"; 
    public static final String FSO_FINANCIAL_MANAGEMENT_GROUP = "UA FSO Financial Management Managers";
    public static final String EDIT_1099_DOCUMENT_INCOME_TYPE = "Edit 1099 Document Income Type";
    public static final String NON_REPORTABLE_INCOME_CODE = "NA"; // non-reportable
    public static final String PAYMENT_METHOD_AP_CREDIT_CARD = "C"; // non-reportable
    public static final String DBA_BUSINESS_PREFIX = "DBA";
    public static List<String> VALID_AMOUNT_CODES = new ArrayList<String>();
    
    static {
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_A);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_B);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_C);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_D);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_E);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_1);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_2);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_3);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_4);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_5);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_6);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_7);
        VALID_AMOUNT_CODES.add(AMOUNT_CODE_8);
    }

    public static class Form1099 {
        public static final String PROPERTY_INCOME_THRESHOLD = "1099_TOTAL_TAX_AMOUNT";
        public static final String PROPERTY_OBJECT_CODES = "1099_OBJECT_CODES";
        public static final String PROPERTY_TAX_YEAR = "1099_REPORTING_PERIOD";
        public static final String PROPERTY_PAYMENT_PERIOD_START = "1099_PAYMENT_PERIOD_START";
        public static final String PROPERTY_PAYMENT_PERIOD_END = "1099_PAYMENT_PERIOD_END";
        public static final String PROPERTY_VENDOR_OWNERSHIP_CODES = "1099_VENDOR_OWNER_CODES";
        public static final String PROPERTY_ADDR_TYPE_CD = "1099_PAYEE_ADDR_TYPE_CODES";
        public static final String PROPERTY_REPLACE_DATA_DURING_LOAD = "1099_REP_DATA_LOAD_IND";
        public static final String PROPERTY_EXTRACT_TYPE = "1099_EXTRACT_TYPE";
        public static final String PROPERTY_EXTRACT_OBJECT_LEVELS = "1099_EXTRACT_OBJECT_LEVELS";
        public static final String PROPERTY_EXTRACT_OBJECT_CODES = "1099_EXTRACT_OBJECT_CODES";
        public static final String PROPERTY_EXTRACT_CONS_CODES = "1099_EXTRACT_CONS_CODES";
        public static final String PROPERTY_OBJECT_CODES_OVERRIDING_RESTRICTIONS = "1099_OBJECT_CODES_OVERRIDING_RESTRICTIONS";
        public static final String PAYMENT_TYPE_OVERRIDE_CODES = "1099_EXTRACT_OVERRIDE_PMT_TYPE_CODE";
        public static final String LEVEL = "LEVEL";
        public static final String OBJECT = "OBJECT";
        public static final String CONS = "CONS";
		public static final String PROPERTY_PAYEMENT_PERIOD_START = "1099_PAYMENT_PERIOD_START";
    }
    
    // add Notes/Attachment Tab to 1099 Process Payment Inquiry Screen
    public static class TaxCreateAndUpdateNotePrefixes {
        public static final String ADD = "Add";
        public static final String CHANGE = "Change";
    }

}
