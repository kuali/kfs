package edu.arizona.kfs.module.cr;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * This class houses the miscellaneous constants used within the Check Reconciliation Module.
 */
public class CrConstants {

    public static final SimpleDateFormat FILE_TIMESTAMP = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat CHECK_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    public static final SimpleDateFormat MM_DD_YYYY = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat MMM_YYYY = new SimpleDateFormat("MMM/yyyy");

    public static final DecimalFormat REPORT_DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00");
    public static final String REPORT_MESSAGES_CLASSPATH = "edu/arizona/kfs/module/cr/report/CrReport";
    public static final String REPORT_TEMPLATE_CLASSPATH = "edu/arizona/kfs/module/cr/report/CheckReconciliationReportTemplate";
    public static final String CR_REPORT_FILE_NAME = "CheckReconciliationReport";

    public static final String MASKED_BANK_ACCOUNT_NUMBER = "********";
    public static final String BALANCE_TYPE_ACTUAL = "AC";

    public static class CheckReconciliationStatusCodes {
        public static final String CLEARED = "CLRD";
        public static final String CANCELLED = "CDIS";
        public static final String ISSUED = "ISSD";
        public static final String VOIDED = "VOID";
        public static final String STALE = "STAL";
        public static final String STOP = "STOP";
        public static final String EXCP = "EXCP";
    }

    public static class CheckReconciliationStatusName {
        public static final String CLEARED = "Cleared";
        public static final String CANCELLED = "Cancelled";
        public static final String ISSUED = "Issued";
        public static final String VOIDED = "Voided";
        public static final String STALE = "Stale";
        public static final String STOP = "Stopped";
        public static final String EXCP = "Exception";
    }

    public static class CheckReconciliationSourceCodes {
        public static final String PDP_SRC = "P";
        public static final String BFL_SRC = "B";
        public static final String MAN_SRC = "M";
    }

    public static class DisbursementTypeCodes {
        public static final String CHECK = "CHCK";
        public static final String ACH = "ACH";
    }

    public static class CheckReconciliationImportColumns {
        public static final Integer ACCOUNT_NUMBER = 0;
        public static final Integer BAI_TYPE = 1;
        public static final Integer CHECK_AMOUNT = 2;
        public static final Integer CHECK_NUMBER = 3;
        public static final Integer CHECK_DATE = 4;
        public static final Integer BANK_REFERENCE = 5;
        public static final Integer DESCRIPTIVE_TEXT_6 = 6;
        public static final Integer DESCRIPTIVE_TEXT_7 = 7;
        public static final Integer STATUS = 8;
    }

    public static class GlIndicatorOptions {
        public static final String YES = "Y";
        public static final String NO = "N";
    }

    public static class DocumentTypeCodes {
        public static final String CANCEL_CHECK = "CHKC";
        public static final String STOP_CHECK = "CHKS";
        public static final String STALE_CHECK = "CHKL";
    }

}
