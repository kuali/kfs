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

/**
 * Holds constants for PURAP business parameters.
 */
public class PurapParameterConstants {

    private static final String PURAP_PARAM_PREFIX = "PURAP";
    private static final String STANDARD_SEPARATOR = ".";
    
    // GROUP NAMES
    public static final String PURAP_ADMIN_GROUP = "PurapAdminGroup";
    
    // PARAMETER NAMES
    public static final String PURAP_OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE";
    public static final String PURAP_OVERRIDE_PREQ_DOC_TITLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "OVERRIDE_PREQ_DOC_TITLE";
    public static final String PURAP_OVERRIDE_REQ_DOC_TITLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "OVERRIDE_REQ_DOC_TITLE";    
    public static final String PURAP_OVERRIDE_VENDOR_DOC_TITLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "OVERRIDE_VENDOR_DOC_TITLE";
    public static final String PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "DEFAULT_NEG_PMT_RQST_APRVL_LMT";
    public static final String PURAP_PDP_EPIC_ORG_CODE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PDP.EPIC.ORG.CODE";
    public static final String PURAP_PDP_EPIC_SBUNT_CODE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PDP.EPIC.SBUNT.CODE";
    public static final String PURAP_PDP_USER_ID = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PDP.USER.ID";
    public static final String PURAP_DEFAULT_PO_TRANSMISSION_CODE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "DEFAULT_PO_TRANSMISSION_CODE";

    public static class Workgroups {
        public static final String SEARCH_SPECIAL_ACCESS = "WORKGROUP" + STANDARD_SEPARATOR + "SEARCH_SPECIAL_ACCESS";

        // ASSIGN A CONTRACT MANAGER DOCUMENT
        public static final String PURAP_DOCUMENT_ASSIGN_CM_ACTIONS = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "DOCUMENT.ASSIGN.CM.ACTIONS";

        // PURCHASE ORDER DOCUMENT
        public static final String WORKGROUP_PURCHASING = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "WORKGROUP.PURCHASING";
        public static final String PURAP_DOCUMENT_PO_INITIATE_ACTION = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "DOCUMENT.PO.INITIATE.ACTION";
        public static final String PURAP_DOCUMENT_PO_ACTIONS = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "DOCUMENT.PO.ACTIONS";
        // TODO PURAP: Below parameter not being used??
//      public static final String WORKGROUP_TAXNBR_ACCESSIBLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "WORKGROUP.TAXNBR_ACCESSIBLE";

        // ACCOUNTS PAYABLE DOCUMENT
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "WORKGROUP.ACCOUNTS_PAYABLE";
        //TODO PURAP: need an accounts payable supervisor group
        public static final String WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR = WORKGROUP_ACCOUNTS_PAYABLE;
    }
    
    public static class WorkflowParameters {
        public static class RequisitionDocument {
            // config parameters
            public static final String SEPARATION_OF_DUTIES_DOLLAR_AMOUNT = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "SEPARATION_OF_DUTIES_DOLLAR_AMOUNT";
            // Workgroups
            public static final String SEPARATION_OF_DUTIES_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "WORKGROUP.SEPARATION_OF_DUTIES";
        }
        public static class PurchaseOrderDocument {
            // Config parameter group names
            public static final String CG_RESTRICTED_OBJECT_CODE_RULE_GROUP_NAME = "PurAp.CG_Restricted_Object_Codes";
            // Workgroups
            public static final String INTERNAL_PURCHASING_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "INTERNAL_PURCHASING_REVIEWERS";
        }
    }

}
