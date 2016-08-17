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
    
    // Batch File System
    public static final String CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE = "gl_bacashtrans";
    public static final String BATRAN_POSTER_INPUT_FILE = "gl_sortbatran";
    public static final String BATRAN_POSTER_ERROR_OUTPUT_FILE = "gl_batranerrs";
    
}
