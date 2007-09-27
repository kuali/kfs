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
    
    public static final String PDP_NAMESPACE = "KFS-PD";
    
    public static class Components {
        public static final String LOAD_FED_RESERVE_BANK_DATA_STEP = "LoadFederalReserveBankDataStep";
    }
    
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
        public static String CANCEL_GROUP = "KUALI_PDP_CANCEL";
        public static String HOLD_GROUP = "KUALI_PDP_HOLD";
        public static String LIMITEDVIEW_GROUP = "KUALI_PDP_LIMITEDVIEW";
        public static String PROCESS_GROUP = "KUALI_PDP_PROCESS";
        public static String RANGES_GROUP = "KUALI_PDP_RANGES";
        public static String SUBMIT_GROUP = "KUALI_PDP_SUBMIT";
        public static String SYSADMIN_GROUP = "KUALI_PDP_SYSADMIN";
        public static String TAXHOLDERS_GROUP = "KUALI_PDP_TAXHOLDERS";
        public static String VIEWALL_GROUP = "KUALI_PDP_VIEWALL";
        public static String VIEWID_GROUP = "KUALI_PDP_VIEWID";
        public static String VIEWBANK_GROUP = "KUALI_PDP_VIEWBANK";        
    }

    public static class ApplicationParameterKeys {
        public static String SEARCH_RESULTS_TOTAL = "RESULTS_LIMIT";
        public static String SEARCH_RESULTS_PER_PAGE = "RESULTS_PER_PAGE";

        public static String ACH_BANK_INPUT_FILE = "ACH_BANK_INPUT_FILE";

        public static String BATCH_OUTPUT_DIR = "BATCH_OUTPUT_DIR";

        public static String HARD_EDIT_CC = "HARD_EDIT_TO_ADDRESS";
        public static String SOFT_EDIT_CC = "SOFT_EDIT_TO_ADDRESS";
        public static String NO_PAYMENT_FILE_EMAIL = "NO_PAYMENT_FILE_EMAIL";
        public static String TAX_GROUP_EMAIL_ADDRESS = "TAX_GROUP_EMAIL_ADDRESS";
        public static String TAX_CANCEL_EMAIL_LIST = "TAX_CANCEL_EMAIL_LIST";

        public static String MAX_NOTE_LINES = "MAX_NOTE_LINES";
        public static String FORMAT_SUMMARY_ROWS = "FORMAT_SUMMARY_REVIEW_RESULTS_PER_PAGE";

        public static String DISBURSEMENT_ACTION_EXPIRATION_DAYS = "DISBURSEMENT_CANCELLATION_DAYS";
    }

    public static class PaymentStatusCodes {
        public static String FORMAT = "FORM";
        public static String OPEN = "OPEN";
        public static String CANCEL_DISBURSEMENT = "CDIS";
        public static String CANCEL_PAYMENT = "CPAY";
        public static String EXTRACTED = "EXTR";
    }
}
