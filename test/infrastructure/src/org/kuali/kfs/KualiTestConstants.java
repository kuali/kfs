/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.test;

import org.kuali.core.util.KualiDecimal;


/**
 * 
 * provides centralized storage of constants that occur throughout the tests
 * 
 * 
 */
public interface KualiTestConstants {
    /**
     * contains Test related constants
     * 
     * 
     */
    public final class TestConstants {
        private static final String HOST = "localhost";
        private static final String PORT = "8080";
        public static final String BASE_PATH = "http://" + HOST + ":" + PORT + "/";
        public static final String MESSAGE = "JUNIT test entry. If this exist after the tests are not cleaning up correctly. Created by class";

        /**
         * group of data values that should work if used togther
         * 
         * 
         */
        public static class Data1 {
            public final static String ACCOUNT_NUMBER = "9544900";
            public final static String CHART_OF_ACCOUNTS_CODE = "BA";
            public final static String OBJECT_CODE = "9912";
            public final static String ORGANIZATION_CODE = "PARK";
            public final static String PERSON_UNIVERSAL_IDENTIFIER = "4219606069";
            public final static Integer UNIVERSITY_FISCAL_YEAR = new Integer(2004);

            public static final String OBJECT_TYPE_CODE = "AS";
        }

        public static class Data2 {
            public final static String AUTHENTICATION_USER_ID = "KHUNTLEY";
            public final static String AUTHENTICATION_USER_ID_THAT_IS_SUPERVISOR = "HEAGLE";
            public static final String KUALI_JV_USER_WORKGROUP = "KUALI_ROLE_JOURNAL_VOUCHER_DOCUMENT_USER";
            public static final String WORKFLOW_ADMIN_WORKGROUP = "WorkflowAdmin";
            public static final String KUALI_DV_ADMIN_WORKGROUP = "KUALI_ROLE_DV_ADMIN";
        }

        public static class Data3 {
            public static final Long DOC_HDR_ID = new Long(100000);
            public static final String CHART = "UA";
            public static final String ACCOUNT = "1912610";
            public static final String SUBACCOUNT = "AUCAP";
            public static final String OBJCODE_SOURCE = "4166";
            public static final String SUBOBJCODE_SOURCE = "FIS";
            public static final String OBJCODE_TARGET = "5000";
            public static final String SUBOBJCODE_TARGET = "A/R";
            public static final String PROJECT = "BOB";
            public static final KualiDecimal LINEAMT = new KualiDecimal("21.12");
            public static final String OBJECT_TYPE_CODE = "AS";
            public static final String DEBIT_CREDIT_CODE = "D";
            public static final String ENCUMBRANCE_UPDATE_CODE = "Y";

            public static final Integer BILLING_ITEM_QUANTITY = new Integer(5);
            public static final String BILLING_ITEM_STOCK_DESCRIPTION = "steer";
            public static final String BILLIING_ITEM_STOCK_NUMBER = "M000";
            public static final Double BILLING_ITEM_UNIT_AMOUNT = new Double("2.0");
            public static final String BILLING_ITEM_UNIT_OF_MEASUREMENT_CODE = "hd";
            public static final Integer POSTING_YEAR = new Integer(2004);
            public static final Integer SEQUENCE_NUMBER = new Integer(1);
        }

        public static class Data4 {
            public static final String ACCOUNT = "1031400";
            public static final String ACCOUNT2 = "5731402";
            public static final KualiDecimal AMOUNT = new KualiDecimal("2.50");
            public static final String BALANCE_TYPE_CODE = "AC";
            public static final String CHART_CODE = "BL";
            public static final String CHART_CODE_UA = "UA";
            public static final String CHART_CODE_BA = "BA";
            public static final String DOC_HDR_ID = "1005";
            public static final String OBJECT_CODE = "3000";
            public static final String OBJECT_CODE2 = "5099";
            public static final Integer POSTING_YEAR = new Integer(2004);
            public static final String PROJECT_CODE = "KUL";
            public static final Integer SEQUENCE_NUMBER = new Integer(1);
            public static final String SUBACCOUNT = "AUCAP";
            public static final String SUBACCOUNT2 = "ADV";
            public static final String SUBOBJECT_CODE = "WTS";
            public static final String OBJECT_TYPE_CODE = "AS";
            public static final String DEBIT_CREDIT_CODE = "D";
            public static final String ENCUMBRANCE_UPDATE_CODE = "Y";
            public static final String ORG_REFERENCE_ID = "12345678";
            public static final String OVERRIDE_CODE = "O";
            public static final String REF_NUMBER = "123456789";
            public static final String REF_ORIGIN_CODE = "AB";
            public static final String REF_TYPE_CODE = "ABCD";
            public static final String USER_ID1 = "VPUTMAN";
            public static final String USER_ID2 = "KHUNTLEY";
        }

        public static class Data5 {
            public static final String BUDGET_AGGREGATION_CODE1 = "O";
            public static final String BUDGET_AGGREGATION_NAME1 = "OBJECT";
            public static final String BUDGET_AGGREGATION_CODE2 = "L";
            public static final String BUDGET_AGGREGATION_NAME2 = "OBJECT LEVEL";

            public static final String FEDERAL_FUNDED_CODE1 = "F";
            public static final String FEDERAL_FUNDED_NAME1 = "FEDERALLY FUNDED AND OWNED";
            public static final String FEDERAL_FUNDED_CODE_BAD = "A";
            public static final String FEDERAL_FUNDED_NAME_BAD = "This is a bad code name";
        }
    }
}
