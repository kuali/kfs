/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

/**
 * Holds constants for <code>{@link org.kuali.module.financial.document.ProcurementCardDocument}</code> business rules.
 * 
 * 
 */
public class ProcurementCardDocumentRuleConstants {

    public static String PCARD_DOCUMENT_PARAMETERS_SEC_GROUP = "Kuali.FinancialTransactionProcessing.ProcurementCardDocument";
    public static String SINGLE_TRANSACTION_IND_PARM_NM = "SINGLE_TRANSACTION_INDICATOR";
    public static String ERROR_TRANS_CHART_CODE_PARM_NM = "ERROR_TRANSACTION_CHART_CODE";
    public static String ERROR_TRANS_ACCOUNT_PARM_NM = "ERROR_TRANSACTION_ACCOUNT_NUMBER";
    public static String DEFAULT_TRANS_CHART_CODE_PARM_NM = "DEFAULT_TRANSACTION_CHART_CODE";
    public static String DEFAULT_TRANS_ACCOUNT_PARM_NM = "DEFAULT_TRANSACTION_ACCOUNT_NUMBER";
    public static String DEFAULT_TRANS_OBJECT_CODE_PARM_NM = "DEFAULT_TRANSACTION_OBJECT_CODE";
    public static String DISPUTE_URL_PARM_NM = "DISPUTE_URL";
    public static String AUTO_APPROVE_DOCUMENTS_IND = "AUTO_APPROVE_DOCUMENTS_IND";
    public static String AUTO_APPROVE_NUMBER_OF_DAYS = "AUTO_APPROVE_NUMBER_OF_DAYS";

    public static String MCC_OBJECT_CODE_GROUP_NM = "PCMccObjectCodeRestrictions";
    public static String MCC_OBJECT_SUB_TYPE_GROUP_NM = "PCMccObjectSubTypeRestrictions";
    public static String GLOBAL_FIELD_RESTRICTIONS_GROUP_NM = "PCGlobalFieldRestrictions";
    public static String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPE_RESTRICTIONS";
    public static String OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_SUB_TYPE_RESTRICTIONS";
    public static String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVEL_RESTRICTIONS";
    public static String OBJECT_CONSOLIDATION_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_CONSOLIDATION_RESTRICTIONS";
    public static String OBJECT_CODE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_CODE_RESTRICTIONS";
    public static String ACCOUNT_NUMBER_GLOBAL_RESTRICTION_PARM_NM = "ACCOUNT_NUMBER_RESTRICTIONS";
    public static String SUB_FUND_GLOBAL_RESTRICTION_PARM_NM = "SUB_FUND_RESTRICTIONS";
    public static String FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM = "FUNCTION_CODE_RESTRICTIONS";
    public static String MCC_PARM_PREFIX = "MCC_";

}
