/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

/**
 * Holds constants for <code>{@link org.kuali.kfs.fp.document.ProcurementCardDocument}</code> business rules.
 */
public class ProcurementCardDocumentRuleConstants {

    public static final String PCARD_DOCUMENT_PARAMETERS_SEC_GROUP = "Kuali.FinancialTransactionProcessing.ProcurementCardDocument";
    public static final String SINGLE_TRANSACTION_IND_PARM_NM = "SINGLE_TRANSACTION_IND";
    public static final String ERROR_TRANS_CHART_CODE_PARM_NM = "ERROR_TRANSACTION_CHART";
    public static final String ERROR_TRANS_ACCOUNT_PARM_NM = "ERROR_TRANSACTION_ACCOUNT_NUMBER";
    public static final String DEFAULT_TRANS_CHART_CODE_PARM_NM = "DEFAULT_TRANSACTION_CHART";
    public static final String DEFAULT_TRANS_ACCOUNT_PARM_NM = "DEFAULT_TRANSACTION_ACCOUNT";
    public static final String DEFAULT_TRANS_OBJECT_CODE_PARM_NM = "DEFAULT_TRANSACTION_OBJECT_CODE";
    public static final String DISPUTE_URL_PARM_NM = "DISPUTE_URL";
    public static final String AUTO_APPROVE_DOCUMENTS_IND = "AUTO_APPROVE_IND";
    public static final String AUTO_APPROVE_NUMBER_OF_DAYS = "AUTO_APPROVE_NUMBER_OF_DAYS";

    public static final String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPES";
    public static final String OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_SUB_TYPES";
    public static final String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVEL_RESTRICTIONS";
    public static final String OBJECT_CONSOLIDATION_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_CONSOLIDATIONS";
    public static final String OBJECT_CODE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_CODE_RESTRICTIONS";
    public static final String SUB_FUND_GLOBAL_RESTRICTION_PARM_NM = "SUB_FUND_RESTRICTIONS";
    public static final String FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM = "FUNCTION_CODE_RESTRICTIONS";

    public static final String VALID_OBJECTS_BY_MCC_CODE_PARM_NM = "VALID_OBJECT_CODES_BY_MCC_CODE";
    public static final String INVALID_OBJECTS_BY_MCC_CODE_PARM_NM = "INVALID_OBJECT_CODES_BY_MCC_CODE";
    public static final String VALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM = "VALID_OBJECT_SUB_TYPES_BY_MCC_CODE";
    public static final String INVALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM = "INVALID_OBJECT_SUB_TYPES_BY_MCC_CODE";
}
