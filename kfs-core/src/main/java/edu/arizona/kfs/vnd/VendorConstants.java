package edu.arizona.kfs.vnd;

import java.text.SimpleDateFormat;

/**
 * Constants used by the core Vendor module.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */

public class VendorConstants extends org.kuali.kfs.vnd.VendorConstants {
    public static final String DOCUMENT_TYPE_NAME_PVEN = "PVEN";

    public static final String FILE_EXTENSION_DATA = ".data";
    public static final String FILE_EXTENSION_DONE = ".done";
    public static final String FILE_EXTENSION_COUNT = ".count";
    public static final String ECUSTOMS_FILENAME = "eCustomsCompliance";
    public static final SimpleDateFormat ECUSTOMS_FILENAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static final String ECUSTOMS_BATCH_DAILY = "ECUSTOMS DAILY BATCH";
    public static final String ECUSTOMS_BATCH_ANNUAL = "ECUSTOMS ANNUAL BATCH";

    // eCustoms uses a windows file format so enforce carriage return/line feed for new line. default is to use the server OS line terminator
    public static final String ECUSTOMS_OUTPUT_FILE_LINE_TERMINATOR = "\r\n";
    public static final String ECUSTOMS_OUTPUT_FILE_DELIMITER = "|";
    public static final String ECUSTOMS_UNDERSCORE = "_";
    public static final String ECUSTOMS_DONE_MESSAGE_DAILY = "Ecustoms Daily Step Complete";
    public static final String ECUSTOMS_DONE_MESSAGE_ANNUAL = "Ecustoms Annual Step Complete";
    public static final String ECUSTOMS_DAILY_JOB_NAME = "DAILY";
    public static final String ECUSTOMS_ANNUAL_JOB_NAME = "ANNUAL";

    // these are search string used to find new/updated vendors based on workflow note text
    public static final String ECUSTOMS_ADD_VENDOR_NOTE_TEXT = "Add vendor document ID";
    public static final String ECUSTOMS_CHANGE_VENDOR_NOTE_TEXT = "Change vendor document ID";
    public static final String ECUSTOMS_SENT_TO_ECUSTOMS_NOTE_TEXT = "batch ecustoms sent";
    public static final String ECUSTOMS_SEARCH_FOR_ID = "ID";

    public static final String ECUSTOMS_TOKEN_CITY = "$";
    public static final String ECUSTOMS_TOKEN_STATE_OPEN = "{";
    public static final String ECUSTOMS_TOKEN_STATE_CLOSE = "}";
    
    //these are used on 1099PayeeForm batch 
    public static final int VENDOR_FIRST_PLUS_LAST_NAME_MAXLENGTH = 40;

}
