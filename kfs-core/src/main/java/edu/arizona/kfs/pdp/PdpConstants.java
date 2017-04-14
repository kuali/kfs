package edu.arizona.kfs.pdp;

public class PdpConstants extends org.kuali.kfs.pdp.PdpConstants {
	
	//ACH File constants
	public static class ACHFileConstants {
		public static final int PAYEE_ACH_ACCT_RECORD_LENGTH = 480;

		public static final int MAX_PERSON_SEARCH_ID_COUNT = 950;
		public static final String TEST_PRINCIPAL_PREFIX = "T00000000000000";
		public static final int REPORT_NAME_FIELD_LENGTH = 35;
	}
	
	public static class ACHFilePropertyPositions {
		public static final int START_OF_SYSTEM_SOURCE = 478;
		public static final int END_OF_SYSTEM_SOURCE = 480;
		public static final int START_OF_BANK_ROUTING_NUMBER = 1;
		public static final int LENGTH_OF_BANK_ROUTING_NUMBER = 9;
		public static final int START_OF_BANK_ACCOUNT_NUMBER = 10;
		public static final int LENGTH_OF_BANK_ACCOUNT_NUMBER = 255;
		public static final int START_OF_PAYEE_NAME = 265;
		public static final int LENGTH_OF_PAYEE_NAME = 123;
		public static final int START_OF_PAYEE_EMAIL_ADDRESS = 388;
		public static final int LENGTH_OF_PAYEE_EMAIL_ADDRESS = 43;
		public static final int START_OF_PAYEE_IDENTIFIER_TYPE_CODE = 431;
		public static final int LENGTH_OF_PAYEE_IDENTIFIER_TYPE_CODE = 1;
		public static final int START_OF_ACH_TRANSACTION_TYPE = 432;
		public static final int LENGTH_OF_ACH_TRANSACTION_TYPE = 4;
		public static final int START_OF_ACTIVE_INDICATOR = 436;
		public static final int LENGTH_OF_ACTIVE_INDICATOR = 1;
		public static final int START_OF_BANK_ACCOUNT_TYPE_CODE = 437;
		public static final int LENGTH_OF_BANK_ACCOUNT_TYPE_CODE = 2;
		public static final int START_OF_PAYEE_ID_NUMBER = 439;
		public static final int LENGTH_OF_PAYEE_ID_NUMBER = 40;

	}
	
	public static class PrePaidChecksConstants {
		public static final String PREPAID_PAYMENT_FILE_TYPE_INDENTIFIER = "prepaidChecksInputFileType";
				
		public static final String AUDIT_REPORT_ASTERISK = " * ";
		public static final String AUDIT_REPORT_BAR = " | ";
		public static final String AUDIT_REPORT_FILE_EXTENSION = ".report";
		public static final String AUDIT_REPORT_HEADING = "PREPAID CHECK UPLOAD AUDIT REPORT                  ";
		public static final String AUDIT_REPORT_SEPARATOR = "========================================================================";
		public static final String AUDIT_REPORT_SUB_HEADING = "FILENAME: ";
		public static final String AUDIT_REPORT_SUB_HEADING_ERRORS = "ERRORS:";
		public static final String AUDIT_REPORT_SUB_HEADING_WARNINGS = "WARNINGS:";
		public static final String AUDIT_REPORT_SUB_HEADING_CHECKS = "PREPAID PAYMENTS LOADED";
		public static final String AUDIT_REPORT_TABLE_HEADING_CHECKS = "DISBURSEMENT NBR | DISBURSE DATE |  PAYEE ID   | PAYEE NAME                 |     AMOUNT    | STATUS";
		public static final String AUDIT_REPORT_SEPARATOR_SINGLE = "------------------------------------------------------------------------";
		public static final String AUDIT_REPORT_SEPARATOR_CHECKS = "----------------------------------------------------------------------------------------------------";		
		public static final String SLASH = "/";
		public static final String UNDERSCORE = "_";
		public static final String PAYEE_DETAIL_TABLE_HEADING_0 = "Source Document";
		public static final String PAYEE_DETAIL_TABLE_HEADING_1 = "Payment Detail ID";
		public static final String PAYEE_DETAIL_TABLE_HEADING_2 = "Invoice Number";
		public static final String PAYEE_DETAIL_TABLE_HEADING_3 = "Payee Name";
		public static final String PAYEE_DETAIL_TABLE_HEADING_4 = "Vendor Number";
		public static final String PAYEE_DETAIL_TABLE_HEADING_5 = "Pay Date";
		public static final String PAYEE_DETAIL_TABLE_HEADING_6 = "Disbursement Date";
		public static final String PAYEE_DETAIL_TABLE_HEADING_7 = "Payment Status";
		public static final String PAYEE_DETAIL_TABLE_HEADING_8 = "Disbursement Type";
		public static final String PAYEE_DETAIL_TABLE_HEADING_9 = "Disbursement Number";
		public static final String PAYEE_DETAIL_TABLE_HEADING_10 = "Payment Amount";
		public static final String PAYEE_DETAIL_TABLE_HEADING_11 = "Net Payment Amount";
	}
}
