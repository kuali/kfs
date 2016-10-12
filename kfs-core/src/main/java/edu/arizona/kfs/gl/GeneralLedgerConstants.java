package edu.arizona.kfs.gl;

public class GeneralLedgerConstants extends org.kuali.kfs.gl.GeneralLedgerConstants {

    public class GeneralErrorCorrectionGroupParameters {
        public static final String RESTRICTED_OBJECT_TYPE_CODES = "OBJECT_TYPES";
        public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "OBJECT_SUB_TYPES";

        public static final String INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";
        public static final String VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";

        public static final String DOCUMENT_TYPES = "DOCUMENT_TYPES";
        public static final String ORIGIN_CODES = "ORIGIN_CODES";
        public static final String CAPITALIZATION_OBJECT_CODE_BY_OBJECT_SUB_TYPE = "CAPITALIZATION_OBJECT_CODE_BY_OBJECT_SUB_TYPE";
        public static final String CAPITALIZATION_OBJECT_SUB_TYPES = "CAPITALIZATION_OBJECT_SUB_TYPES";
        public static final String CAPITALIZATION_OFFSET_BY_CHART = "CAPITALIZATION_OFFSET_BY_CHART";

    }
    
    public static final String CASH_TRANSFER_ORIGINATION_CODES = "ORIGINATION";
    public static final String CASH_TRANSFER_DOC_TYPE_CODES = "DOCUMENT_TYPES_REQUIRING_CREATION_OF_CASH_TRANSFER_ENTRIES";

    public static class BatchFileSystem {
        static final public String EXTENSION = ".data";
        static final public String DONE_FILE_EXTENSION = ".done";

        //static final public String GL_ENTRY = "";
        static final public String ENTERPRISE_FEED = "gl_glentry_entp";
        static final public String COLLECTOR_OUTPUT = "gl_glentry_coll";
        //TODO:- move to OriginEntrySource??
        static final public String SCRUBBER_ERROR_PREFIX = "scrberr2";

        static final public String AUTO_DISAPPROVE_DOCUMENTS_OUTPUT_FILE = "sys_autoDisapprove_output";
        static final public String AUTO_DISAPPROVE_DOCUMENTS_ERROR_OUTPUT_FILE = "sys_autoDisapprove_errs";
        static final public String TEXT_EXTENSION = ".txt";

        static final public String BACKUP_FILE = "gl_glbackup";

        static final public String  COLLECTOR_BACKUP_FILE = "col_glbackup";

        static final public String COLLECTOR_PRE_SCRUBBER = "col_prescrub";
        static final public String COLLECTOR_SCRUBBER_INPUT_FILE = "col_sortscrb";
        static final public String COLLECTOR_SCRUBBER_VALID_OUTPUT_FILE = "col_scrbout1";
        static final public String COLLECTOR_SCRUBBER_ERROR_OUTPUT_FILE = "col_scrberr1";
        static final public String COLLECTOR_SCRUBBER_EXPIRED_OUTPUT_FILE = "col_expaccts";
        static final public String COLLECTOR_SCRUBBER_ERROR_SORTED_FILE = "col_sorterr1";
        static final public String COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE = "col_scrbout2";
        static final public String COLLECTOR_DEMERGER_ERROR_OUTPUT_FILE = "col_scrberr2";

        static final public String PRE_SCRUBBER_FILE = "gl_prescrub";
        static final public String SCRUBBER_INPUT_FILE = "gl_sortscrb";
        static final public String SCRUBBER_VALID_OUTPUT_FILE = "gl_scrbout1";
        static final public String SCRUBBER_ERROR_OUTPUT_FILE = "gl_scrberr1";
        static final public String SCRUBBER_EXPIRED_OUTPUT_FILE = "gl_expaccts";
        static final public String SCRUBBER_ERROR_SORTED_FILE = "gl_sorterr1";
        static final public String DEMERGER_VAILD_OUTPUT_FILE = "gl_scrbout2";
        static final public String DEMERGER_ERROR_OUTPUT_FILE = "gl_scrberr2";

        static final public String POSTER_INPUT_FILE = "gl_sortpost";
        static final public String REVERSAL_POSTER_VALID_OUTPUT_FILE = "gl_workfile";
        static final public String REVERSAL_POSTER_ERROR_OUTPUT_FILE = "gl_workerrs";
        static final public String POSTER_VALID_OUTPUT_FILE = "gl_postout";

        static final public String POSTER_ERROR_OUTPUT_FILE = "gl_posterrs";
        static final public String ICR_TRANSACTIONS_OUTPUT_FILE = "gl_icrtrans";
        //todo - create sort step
        static final public String ICR_POSTER_INPUT_FILE = "gl_sorticr";

        static final public String ICR_POSTER_ERROR_OUTPUT_FILE = "gl_icrerrs";

        static final public String ICR_ENCUMBRANCE_OUTPUT_FILE = "gl_icrencmb";
        static final public String ICR_ENCUMBRANCE_POSTER_INPUT_FILE = "gl_sorticrencmb";
        static final public String ICR_ENCUMBRANCE_POSTER_ERROR_OUTPUT_FILE = "gl_icrencmberrs";
		static final public String NIGHTLY_OUT_FILE = "gl_glentry_kfs";
		static final public String EXTRACT_TRANSACTION_FILE = "gl_glentry_pdp";

		//year end part
		static final public String ORGANIZATION_REVERSION_PRE_CLOSING_FILE = "gl_org_reversion_pre_closing";
		static final public String ORGANIZATION_REVERSION_CLOSING_FILE = "gl_org_reversion_closing";
		static final public String ENCUMBRANCE_FORWARD_FILE = "gl_encumbrance_forward";
		static final public String CLOSE_NOMINAL_ACTIVITY_FILE = "gl_close_nominal_activity";
		static final public String BALANCE_FORWARDS_FILE = "gl_balance_forwards";
		static final public String BALANCE_FORWARDS_CLOSED_FILE = "gl_balance_forwards_closed";

		static final public String LABOR_GL_ENTRY_FILE = "gl_glentry_lab";
		
		//Budget Adjustment Transaction
		static final public String BUDGET_ADJUSTMENT_OUTPUT_FILE = "gl_sortbatrans";
		static final public String BUDGET_ADJUSTMENT_MERGE_FILE = "gl_mergebatrans";
		static final public String CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE = "gl_bacashtrans";
		static final public String CASH_TRANSFER_TRANSACTIONS_ERROR_OUTPUT_FILE = "gl_batranerrs";
    }
}
