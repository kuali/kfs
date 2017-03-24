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
}
