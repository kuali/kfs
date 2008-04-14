/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp;


public class PdpConstants {
    /**
     * Used as the file prefix for temp files for ManualUploadFileAction
     */
    public static final String PDP_MANUAL_FILE_UPLOAD_TEMP_FILE_PREFIX = "pdp-manual-upload-file-";

    public static class PayeeTypeCodes {
        public static String VENDOR = "V";
    }

    // TODO Probably should become a table
    public static class PayeeIdTypeCodes {
        public static String PAYEE_ID = "P";
        public static String SSN = "S";
        public static String EMPLOYEE_ID = "E";
        public static String FEIN = "F";
        public static String VENDOR_ID = "V";
        public static String OTHER = "X";
    }

    public static class Groups {
        // Security Workgroup names
        public static String CANCEL_GROUP = "PD_CANCEL_USERS";
        public static String HOLD_GROUP = "PD_HOLD_USERS";
        public static String LIMITEDVIEW_GROUP = "PD_LIMITED_VIEW";
        public static String PROCESS_GROUP = "PD_PROCESS_PAYMENTS";
        public static String RANGES_GROUP = "PD_RANGE_USERS";
        public static String SUBMIT_GROUP = "PD_SUBMIT_PAYMENT_FILE_USERS";
        public static String SYSADMIN_GROUP = "PD_SUPER_USERS";
        public static String TAXHOLDERS_GROUP = "PD_TAX_HOLD_USERS";
        public static String VIEWALL_GROUP = "PD_VIEW_ALL";
        public static String VIEWID_GROUP = "PD_VIEW_ID";
        public static String VIEWBANK_GROUP = "PD_VIEW_BANK";
    }

    public static class ApplicationParameterKeys {
        public static String SEARCH_RESULTS_TOTAL = "RESULTS_LIMIT";
        public static String SEARCH_RESULTS_PER_PAGE = "RESULTS_PER_PAGE";

        public static String CHECK_EXTRACT_FILE = "CHECK_EXTRACT_FILE";
        public static String ACH_EXTRACT_FILE = "ACH_EXTRACT_FILE";
        public static String CHECK_CANCEL_EXTRACT_FILE = "CHECK_CANCEL_EXTRACT_FILE";
        public static String ACH_BANK_INPUT_FILE = "ACH_BANK_INPUT_FILE";

        public static String EXTRACT_PROCESS_ID = "EXTRACT_PROCESS_ID";

        public static String HARD_EDIT_CC = "HARD_EDIT_TO_EMAIL_ADDRESSES";
        public static String SOFT_EDIT_CC = "SOFT_EDIT_TO_EMAIL_ADDRESSES";
        public static String NO_PAYMENT_FILE_EMAIL = "NO_PAYMENT_FILE_TO_EMAIL_ADDRESSES";
        public static String PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL = "PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL_ADDRESSES";

        public static String TAX_GROUP_EMAIL_ADDRESS = "TAX_GROUP_TO_EMAIL_ADDRESSES";
        public static String TAX_CANCEL_EMAIL_LIST = "TAX_CANCEL_TO_EMAIL_ADDRESSES";

        public static String MAX_NOTE_LINES = "MAX_NOTE_LINES";
        public static String FORMAT_SUMMARY_ROWS = "FORMAT_SUMMARY_REVIEW_RESULTS_PER_PAGE";

        public static String DISBURSEMENT_ACTION_EXPIRATION_DAYS = "DISBURSEMENT_CANCELLATION_DAYS";
    }

    public static class DisbursementTypeCodes {
        public static String CHECK = "CHCK";
        public static String ACH = "ACH";
    }

    public static class PaymentChangeCodes {
        public static String CANCEL_DISBURSEMENT = "CD";
        public static String CANCEL_REISSUE_DISBURSEMENT = "CRD";
    }

    public static class PaymentStatusCodes {
        public static String FORMAT = "FORM";
        public static String OPEN = "OPEN";
        public static String CANCEL_DISBURSEMENT = "CDIS";
        public static String CANCEL_PAYMENT = "CPAY";
        public static String EXTRACTED = "EXTR";
        public static String PENDING_ACH = "PACH";
    }
}
