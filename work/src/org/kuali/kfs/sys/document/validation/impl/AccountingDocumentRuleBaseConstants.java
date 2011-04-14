/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Holds common constants for all Transaction Processing eDoc rule classes.
 */
public interface AccountingDocumentRuleBaseConstants {

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final class APPLICATION_PARAMETER {
        public static final String RESTRICTED_OBJECT_CODES = "OBJECT_CODES";
        public static final String RESTRICTED_OBJECT_TYPE_CODES = "OBJECT_TYPES";
        public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "OBJECT_SUB_TYPES";
        public static final String RESTRICTED_OBJECT_LEVELS = "OBJECT_LEVELS";
        public static final String RESTRICTED_OBJECT_CONSOLIDATIONS = "OBJECT_CONSOLIDATIONS";
        public static final String RESTRICTED_FUND_GROUP_CODES = "FUND_GROUPS";
        public static final String RESTRICTED_SUB_FUND_GROUP_CODES = "SUB_FUND_GROUPS";
        public static final String MANDATORY_TRANSFER_SUBTYPE_CODES = "MANDATORY_TRANSFER_OBJECT_SUB_TYPES";
        public static final String NONMANDATORY_TRANSFER_SUBTYPE_CODES = "NON_MANDATORY_TRANSFER_OBJECT_SUB_TYPES";
        public static final String FUND_GROUP_BALANCING_SET = "FUND_GROUP_BALANCING_SET";
        // doctype parameter
        public static final String DOCTYPE_SALES_TAX_CHECK = "SALES_TAX_APPLICABLE_DOCUMENT_TYPES";

        // combination object code and account parameter
        public static final String SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES = "SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES";
    }

    /**
     * Constant to statically define reusable error paths
     */
    public static final class ERROR_PATH {
        public static final String DELIMITER = ".";
        public static final String DOCUMENT_ERROR_PREFIX = "document" + DELIMITER;
    }

    // GLPE KFSConstants
    public static final class GENERAL_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        private static String BLANK_PROJECT_STRING = null; // Max length is 10 for this field
        private static String BLANK_SUB_OBJECT_CODE = null; // Max length is 3 for this field
        private static String BLANK_SUB_ACCOUNT_NUMBER = null; // Max length is 5 for this field
        private static String BLANK_OBJECT_CODE = null; // Max length is 4 for this field
        private static String BLANK_OBJECT_TYPE_CODE = null; // Max length is 4 for this field
        public static final int GLPE_DESCRIPTION_MAX_LENGTH = 40;

        public static String getBlankProjectCode() {
            if (BLANK_PROJECT_STRING == null) {
                BLANK_PROJECT_STRING = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(GeneralLedgerPendingEntry.class, KFSPropertyConstants.PROJECT_CODE), '-');
            }
            return BLANK_PROJECT_STRING;
        }

        public static String getBlankFinancialSubObjectCode() {
            if (BLANK_SUB_OBJECT_CODE == null) {
                BLANK_SUB_OBJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(GeneralLedgerPendingEntry.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), '-');
            }
            return BLANK_SUB_OBJECT_CODE;
        }

        public static String getBlankSubAccountNumber() {
            if (BLANK_SUB_ACCOUNT_NUMBER == null) {
                BLANK_SUB_ACCOUNT_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(GeneralLedgerPendingEntry.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER), '-');
            }
            return BLANK_SUB_ACCOUNT_NUMBER;
        }

        public static String getBlankFinancialObjectCode() {
            if (BLANK_OBJECT_CODE == null) {
                BLANK_OBJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(GeneralLedgerPendingEntry.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE), '-');
            }
            return BLANK_OBJECT_CODE;
        }

        public static String getBlankFinancialObjectType() {
            if (BLANK_OBJECT_TYPE_CODE == null) {
                BLANK_OBJECT_TYPE_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(GeneralLedgerPendingEntry.class, KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE), '-');
            }
            return BLANK_OBJECT_TYPE_CODE;
        }
    }

    // account constants
    public static final class ACCOUNT_NUMBER {
        public static final String BUDGET_LEVEL_NO_BUDGET = "N";
    }
}
