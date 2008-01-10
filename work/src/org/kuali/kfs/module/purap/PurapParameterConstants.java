/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap;

import org.kuali.kfs.ParameterKeyConstants;

/**
 * Holds constants for PURAP business parameters.
 */
public class PurapParameterConstants implements ParameterKeyConstants {

    // PARAMETER NAMES
    public static final String PURAP_OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_PREQ_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_REQ_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_VENDOR_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT = "DEFAULT_POS_APRVL_LMT";
    public static final String PURAP_PDP_EPIC_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
    public static final String PURAP_PDP_EPIC_SBUNT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
    public static final String PURAP_PDP_USER_ID = "PRE_DISBURSEMENT_EXTRACT_USER";
    public static final String PURAP_DEFAULT_PO_TRANSMISSION_CODE = "DEFAULT_TRANSMISSION_CODE";
    public static final String PURAP_PREQ_REQUIRE_ATTACHMENT = "REQUIRE_ATTACHMENT_IND";
    public static final String PURAP_CM_REQUIRE_ATTACHMENT = "REQUIRE_ATTACHMENT_IND";

    public static final String PURAP_PDP_PREQ_CANCEL_NOTE = "CANCEL_NOTE";
    public static final String PURAP_PDP_PREQ_RESET_NOTE = "RESET_NOTE";
    public static final String PURAP_PDP_CM_CANCEL_NOTE = "CANCEL_NOTE";
    public static final String PURAP_PDP_CM_RESET_NOTE = "RESET_NOTE";

    public static final String PHONE_NUMBER_FORMATS_PARM_NM = "GENERIC_PHONE_NUMBER_FORMATS";
    public static final String DEFAULT_PHONE_NUMBER_DIGITS_PARM_NM = "GENERIC_DEFAULT_PHONE_NUMBER_LENGTH";
    
    public static class CapitalAsset {
        public static final String CAPITAL_ASSET_OBJECT_LEVELS = "CAPITAL_ASSET_OBJECT_LEVELS";
        public static final String POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS = "POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS";
        public static final String CAPITAL_ASSET_PRICE_THRESHOLD = "CAPITAL_ASSET_PRICE_THRESHOLD";
        public static final String ASSET_NUMBER_CAMS_TRAN_TYPES = "CAPITAL_ASSET_TRANSACTION_TYPES_REQUIRING_ASSET_NUMBERS";
        public static final String QUANTITY_OBJECT_CODE_SUBTYPES = "OBJECT_SUB_TYPES_REQUIRING_QUANTITY";
        public static final String RECURRING_CAMS_TRAN_TYPES = "CAPITAL_ASSET_TRANSACTION_TYPES_REQUIRING_RECURRING_PAYMENT_TERMS";
        
        public static final String OVERRIDE_CAPITAL_ASSET_WARNINGS_IND = "OVERRIDE_CAPITAL_ASSET_WARNINGS_IND";
    }

    public static class Workgroups {
        public static final String SEARCH_SPECIAL_ACCESS = "SEARCH_SPECIAL_ACCESS_GROUP";

        // PURCHASE ORDER DOCUMENT
        public static final String WORKGROUP_PURCHASING = "PURCHASING_GROUP";
        public static final String PURAP_DOCUMENT_PO_INITIATE_ACTION = "INITIATE_ACTION";
        public static final String PURAP_DOCUMENT_PO_ACTIONS = "ACTION_TAKING_GROUP";

        // ACCOUNTS PAYABLE DOCUMENT
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = "ACCOUNTS_PAYABLE_GROUP";
        public static final String WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR = "ACCOUNTS_PAYABLE_SUPERVISOR_GROUP";
    }

    public static class WorkflowParameters {
        public static class RequisitionDocument {
            // config parameters
            public static final String SEPARATION_OF_DUTIES_DOLLAR_AMOUNT = "SEPARATION_OF_DUTIES_DOLLAR_AMOUNT";
            // Workgroups
            public static final String SEPARATION_OF_DUTIES_WORKGROUP_NAME = "SEPARATION_OF_DUTIES_GROUP";
        }

        public static class PurchaseOrderDocument {
            // Config parameter group names
            public static final String CG_RESTRICTED_OBJECT_CODE_RULE_PARM_NM = "CG_ROUTE_OBJECT_CODES_BY_CHART";
            public static final String NO_CG_RESTRICTED_OBJECT_CODE_RULE_PARM_NM = "NO_CG_ROUTE_OBJECT_CODES_BY_CHART";
            // Workgroups
            public static final String CONTRACT_MANAGERS_WORKGROUP_NAME = "CONTRACT_MANAGERS_GROUP";
            public static final String INTERNAL_PURCHASING_WORKGROUP_NAME = "INTERNAL_PURCHASING_REVIEWERS_GROUP";
        }
    }

}
