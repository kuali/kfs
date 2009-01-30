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
package org.kuali.kfs.module.purap;

import org.kuali.kfs.sys.ParameterKeyConstants;

/**
 * Holds constants for PURAP business parameters.
 */
public class PurapParameterConstants implements ParameterKeyConstants {

    // PARAMETER NAMES
    public static final String PURAP_OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_CM_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_PO_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_PREQ_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_OVERRIDE_REQ_DOC_TITLE = "OVERRIDE_DOCUMENT_TITLE_IND";
    public static final String PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT = "DEFAULT_POS_APRVL_LMT";
    public static final String PURAP_DEFAULT_PO_TRANSMISSION_CODE = "DEFAULT_TRANSMISSION_CODE";
    public static final String PURAP_PREQ_REQUIRE_ATTACHMENT = "REQUIRE_ATTACHMENT_IND";
    public static final String PURAP_CM_REQUIRE_ATTACHMENT = "REQUIRE_ATTACHMENT_IND";
    public static final String PURAP_PREQ_PAY_DATE_DEFAULT_NUMBER_OF_DAYS = "NUMBER_OF_DAYS_USED_TO_CALCULATE_DEFAULT_PAY_DATE";
    public static final String PURAP_PO_RETRANSMIT_TRANSMISSION_METHOD_TYPES = "RETRANSMIT_TRANSMISSION_METHOD_TYPES";
    public static final String PURAP_PO_PRINT_PREVIEW_TRANSMISSION_METHOD_TYPES = "PRINT_PREVIEW_TRANSMISSION_METHOD_TYPES";
    public static final String PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT = "ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT";
    
    public static final String PURAP_PDP_PREQ_CANCEL_NOTE = "CANCEL_NOTE";
    public static final String PURAP_PDP_PREQ_RESET_NOTE = "RESET_NOTE";
    public static final String PURAP_PDP_CM_CANCEL_NOTE = "CANCEL_NOTE";
    public static final String PURAP_PDP_CM_RESET_NOTE = "RESET_NOTE";

    public static final String DEFAULT_QUANTITY_ITEM_TYPE = "DEFAULT_QUANTITY_ITEM_TYPE";
    public static final String DEFAULT_NON_QUANTITY_ITEM_TYPE = "DEFAULT_NON_QUANTITY_ITEM_TYPE";
    
    public static final String ENABLE_RECEIVING_ADDRESS_IND = "ENABLE_RECEIVING_ADDRESS_IND";
    public static final String ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND = "ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND";
    
    public static final String VALIDATE_ACCOUNT_DISTRIBUTION_IND = "VALIDATE_ACCOUNT_DISTRIBUTION_IND";
    
    public static final String SHOW_CLEAR_AND_LOAD_QTY_BUTTONS = "SHOW_CLEAR_AND_LOAD_QTY_BUTTONS";
    
    public static final String ENABLE_DEFAULT_CONTRACT_MANAGER_IND = "ENABLE_DEFAULT_CONTRACT_MANAGER_IND";
    public static final String AUTO_CLOSE_RECURRING_PO_DATE = "AUTO_CLOSE_RECURRING_PO_DATE";
    public static final String AUTO_CLOSE_RECURRING_PO_TO_EMAIL_ADDRESSES = "AUTO_CLOSE_RECURRING_PO_TO_EMAIL_ADDRESSES";
   
    public static final String ENABLE_SALES_TAX_IND = "ENABLE_SALES_TAX_IND";
    
    public static final String BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS = "BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS";
    
    //Vendor Choices
    public static final String DEFAULT_APO_VENDOR_CHOICE = "DEFAULT_APO_VENDOR_CHOICE";
    public static final String DEFAULT_B2B_VENDOR_CHOICE = "DEFAULT_B2B_VENDOR_CHOICE";
    
    public static class CapitalAsset {
        public static final String ASSET_NUMBER_CAMS_TRAN_TYPES = "CAPITAL_ASSET_TRANSACTION_TYPES_REQUIRING_ASSET_NUMBERS";
        public static final String QUANTITY_OBJECT_CODE_SUBTYPES = "OBJECT_SUB_TYPES_REQUIRING_QUANTITY";
        public static final String CAMS_DOC_ITEM_CREATION_TRAN_TYPES = "CAMS_DOC_ITEM_CREATION_TRAN_TYPES";
        public static final String CAMS_DOC_CAPITAL_OC_SUBTYPES = "CAMS_DOC_CAPITAL_OC_SUBTYPES";
        public static final String PURCHASING_OBJECT_SUB_TYPES = "PURCHASING_OBJECT_SUB_TYPES";
        public static final String PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR = "PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR";
        
        public static final String CHARTS_REQUIRING_ASSET_NUMBER_ON_REQUISITION = "CHARTS_REQUIRING_ASSET_NUMBER_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_ASSET_TRANSACTION_TYPE_ON_REQUISITION = "CHARTS_REQUIRING_ASSET_TRANSACTION_TYPE_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_ASSET_TYPE_ON_REQUISITION = "CHARTS_REQUIRING_ASSET_TYPE_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_COMMENTS_ON_REQUISITION = "CHARTS_REQUIRING_COMMENTS_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_DESCRIPTION_ON_REQUISITION = "CHARTS_REQUIRING_DESCRIPTION_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_REQUISITION = "CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_LOCATIONS_QUANTITY_ON_REQUISITION = "CHARTS_REQUIRING_LOCATIONS_QUANTITY_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_MANUFACTURER_ON_REQUISITION = "CHARTS_REQUIRING_MANUFACTURER_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_MODEL_ON_REQUISITION = "CHARTS_REQUIRING_MODEL_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_NOT_CURRENT_FISCAL_YEAR_ON_REQUISITION = "CHARTS_REQUIRING_NOT_CURRENT_FISCAL_YEAR_ON_REQUISITION";
        public static final String CHARTS_REQUIRING_NUMBER_OF_ASSETS_ON_REQUISITION = "CHARTS_REQUIRING_NUMBER_OF_ASSETS_ON_REQUISITION";

        public static final String CHARTS_REQUIRING_ASSET_NUMBER_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_ASSET_NUMBER_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_ASSET_TRANSACTION_TYPE_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_ASSET_TRANSACTION_TYPE_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_ASSET_TYPE_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_ASSET_TYPE_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_COMMENTS_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_COMMENTS_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_DESCRIPTION_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_DESCRIPTION_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_LOCATIONS_QUANTITY_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_LOCATIONS_QUANTITY_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_MANUFACTURER_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_MANUFACTURER_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_MODEL_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_MODEL_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_NOT_CURRENT_FISCAL_YEAR_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_NOT_CURRENT_FISCAL_YEAR_ON_PURCHASE_ORDER";
        public static final String CHARTS_REQUIRING_NUMBER_OF_ASSETS_ON_PURCHASE_ORDER = "CHARTS_REQUIRING_NUMBER_OF_ASSETS_ON_PURCHASE_ORDER";
        
    }

    public static final String PRE_DISBURSEMENT_EXTRACT_CUTOFF_TIME = "PRE_DISBURSEMENT_EXTRACT_CUTOFF_TIME";
    
    public static class Workgroups {
        public static final String SEARCH_SPECIAL_ACCESS = "SEARCH_SPECIAL_ACCESS_GROUP";

        // PURCHASE ORDER DOCUMENT
        public static final String WORKGROUP_PURCHASING = "PURCHASING_GROUP";
        public static final String PURAP_DOCUMENT_PO_INITIATE_ACTION = "INITIATE_ACTION";
        public static final String PURAP_DOCUMENT_PO_ACTIONS = "ACTION_TAKING_GROUP";

        // ACCOUNTS PAYABLE DOCUMENT
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = "ACCOUNTS_PAYABLE_GROUP";
        public static final String WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR = "ACCOUNTS_PAYABLE_SUPERVISOR_GROUP";
        
        // Tax Review Workgroups
        public static final String PA_NONRESIDENT_ALIEN_TAX_REVIEWERS = "PA_NON-RESIDENT_ALIEN_TAX_REVIEWERS";
        public static final String PA_EMPLOYEE_VENDOR_REVIEWERS = "PA_EMPLOYEE_VENDOR_REVIEWERS";
        public static final String PA_EMPLOYEE_AND_NONRESIDENT_ALIEN_VENDOR_TAX_REVIEWERS = "PA_EMPLOYEE_AND_NON-RESIDENT_ALIEN_VENDOR_TAX_REVIEWERS";
        
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
    
    public static class B2BParameters{
        public static final String PUNCHOUT_URL = "B2B_PUNCHOUT_URL";
        public static final String PUNCHBACK_URL = "B2B_PUNCHBACK_URL";
        public static final String ENVIRONMENT = "B2B_ENVIRONMENT";
        public static final String USER_AGENT = "B2B_USER_AGENT";
        public static final String PASSWORD = "B2B_SHOPPING_PASSWORD";
        public static final String PO_PASSWORD = "B2B_PURCHASE_ORDER_PASSWORD";
        public static final String PO_URL = "B2B_PURCHASE_ORDER_URL";
    }

    public static class ElectronicInvoiceParameters{
        public static final String FILE_MOVE_AFTER_LOAD_IND = "FILE_MOVE_AFTER_LOAD_IND";
        public static final String REQUISITION_SOURCES_REQUIRING_CATALOG_MATCHING = "REQUISITION_SOURCES_REQUIRING_CATALOG_MATCHING";
        public static final String DAILY_SUMMARY_REPORT_FROM_EMAIL_ADDRESS = "DAILY_SUMMARY_REPORT_FROM_EMAIL_ADDRESS";
        public static final String DAILY_SUMMARY_REPORT_TO_EMAIL_ADDRESSES = "DAILY_SUMMARY_REPORT_TO_EMAIL_ADDRESSES";
        public static final String SALES_TAX_UPPER_VARIANCE_PERCENT = "SALES_TAX_UPPER_VARIANCE_PERCENT";
        public static final String SALES_TAX_LOWER_VARIANCE_PERCENT = "SALES_TAX_LOWER_VARIANCE_PERCENT";
    }
    
    public static class NRATaxParameters {
        public static final String FEDERAL_TAX_PARM_PREFIX = "NON_RESIDENT_ALIEN_TAX_FEDERAL_";
        public static final String STATE_TAX_PARM_PREFIX = "NON_RESIDENT_ALIEN_TAX_STATE_";
        public static final String TAX_PARM_ACCOUNT_SUFFIX = "ACCOUNT";
        public static final String TAX_PARM_CHART_SUFFIX = "CHART";
        public static final String TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX = "OBJECT_CODE_BY_INCOME_CLASS";
    }        
}
