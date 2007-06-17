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
    public static final String PDP_APPLICATION = "PDP";

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
        public static String SEARCH_RESULTS_TOTAL = "SEARCH_RESULTS_TOTAL";
        public static String SEARCH_RESULTS_PER_PAGE = "SEARCH_RESULTS_PER_PAGE";

        public static String BATCH_OUTPUT_DIR = "BATCH_OUTPUT_DIR";

        public static String HARD_EDIT_CC = "HARD_EDIT_CC";
        public static String SOFT_EDIT_CC = "SOFT_EDIT_CC";
        public static String NO_PAYMENT_FILE_EMAIL = "NO_PAYMENT_FILE_EMAIL";
        public static String TAX_GROUP_EMAIL_ADDRESS = "TAX_GROUP_EMAIL_ADDRESS";
        public static String TAX_CANCEL_EMAIL_LIST = "TAX_CANCEL_EMAIL_LIST";

        public static String DISBURSEMENT_ACTION_EXPIRATION_DAYS = "DISBURSEMENT_ACTION_EXPIRATION_DAYS";
    }
}
