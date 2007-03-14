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

import java.util.HashMap;

/**
 * Holds constants for PURAP.
 * 
 */
public class PurapConstants {

    public static class Workgroups {
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = "PURAP.WORKGROUP.ACCOUNTS_PAYABLE"; 
        public static final String WORKGROUP_PURCHASING = "PURAP.WORKGROUP.PURCHASING"; 
        public static final String WORKGROUP_TAXNBR_ACCESSIBLE = "PURAP.WORKGROUP.TAXNBR_ACCESSIBLE"; 
    }
    
    //Miscellaneous generic constants
    public static final String NONE = "NONE";
    public static final String CREATE_NEW_DIVISION = "create division";
    public static final String NAME_DELIM = ", ";
    public static final String VENDOR_LOOKUPABLE_IMPL = "vendorLookupable";
    public static final String DASH = "-";
    public static final String VENDOR_HEADER_ATTR = "vendorHeader";
    public static final String DOC_ADHOC_NODE_NAME = "Adhoc Routing";
    public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";

    //Vendor Tax Types
    public static final String TAX_TYPE_FEIN = "FEIN";
    public static final String TAX_TYPE_SSN = "SSN";
    //public static final String TAX_TYPE_ITIN = "ITIN";  //are we implementing this in Kuali??

    //VENDOR PHONE TYPES
    public static class PhoneTypes {
        public static final String TOLL_FREE = "TF";
        public static final String PHONE = "PH";
        public static final String FAX = "FX";
    }
    
    public static class RequisitionStatuses {
        public static String IN_PROCESS = "INPR";
        public static String CANCELLED = "CANC";
        public static String CLOSED = "CLOS";
        public static String AWAIT_CONTENT_APRVL = "ACNT";
        public static String AWAIT_SUB_ACCT_APRVL = "ASUB";
        public static String AWAIT_FISCAL_APRVL = "AFIS";
        public static String AWAIT_CHART_APRVL = "ACHA";
        public static String AWAIT_SEP_OF_DUTY_APRVL = "ASOD";
        public static String DAPRVD_CONTENT = "DCNT";
        public static String DAPRVD_SUB_ACCT = "DSUB";
        public static String DAPRVD_FISCAL = "DFIS";
        public static String DAPRVD_CHART = "DCHA";
        public static String DAPRVD_SEP_OF_DUTY = "DSOD";
        public static String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR";
        public static String CONTRACT_MANAGER_ASSGN = "CMRA";
    }

    public static class POCostSources {
        public static String ESTIMATE = "EST";
    }
    
    public static class POTransmissionMethods {
        public static String FAX = "FAX";
        public static String PRINT = "PRIN";
        public static String NOPRINT = "NOPR";
        public static String ELECTRONIC = "ELEC";
    }
    
    public static int REQ_B2B_ALLOW_COPY_DAYS = 5;
    
    public static class RequisitionSources {
        public static String STANDARD_ORDER = "STAN";
        public static String B2B = "B2B";
    }

    // Requisition Tab Errors
    public static final String DELIVERY_TAB_ERRORS = "document.delivery*";
    public static final String VENDOR_ERRORS = "document.vendor*";
    public static final String ADDITIONAL_TAB_ERRORS = "document.requestor*,document.purchaseOrderTransmissionMethodCode,document.chartOfAccountsCode,document.organizationCode,document.purchaseOrderCostSourceCode,document.purchaseOrderTotalLimit";
    
    // Assign Contract Manager Tab Errors
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "document.unassignedRequisition*";

    public static class PurchaseOrderStatuses {
        public static String IN_PROCESS = "INPR";
        public static String WAITING_FOR_VENDOR = "WVEN";
        public static String WAITING_FOR_DEPARTMENT = "WDPT";
        public static String OPEN = "OPEN";
        public static String CLOSED = "CLOS";
        public static String CANCELLED = "CANC";
        public static String PAYMENT_HOLD = "PHOL";
        public static String AWAIT_TAX_APRVL = "WTAX";
        public static String AWAIT_BUDGET_APRVL = "WBUD";
        public static String AWAIT_CONTRACTS_GRANTS_APRVL = "WCG";
        public static String AWAIT_PURCHASING_APRVL = "WPUR";
        public static String AWAIT_SPECIAL_APRVL = "WSPC";
        public static String DAPRVD_TAX = "DTAX";
        public static String DAPRVD_BUDGET = "DBUD";
        public static String DAPRVD_CONTRACTS_GRANTS = "DCG";
        public static String DAPRVD_PURCHASING = "DPUR";
        public static String DAPRVD_SPECIAL = "DSPC";
        public static String CXML_ERROR = "CXER";
        public static String PENDING_CXML = "CXPE";
        public static String PENDING_FAX = "FXPE";
        public static String PENDING_PRINT = "PRPE";
        public static String QUOTE = "QUOT";
        public static String VOID = "VOID";
        public static String AMENDMENT = "AMND";
    }
    
	
    public static class ItemTypeCodes {
        // ITEM TYPES
        public static String ITEM_TYPE_ITEM_CODE = "ITEM";
        public static String ITEM_TYPE_FREIGHT_CODE = "FRHT";
        public static String ITEM_TYPE_SHIP_AND_HAND_CODE = "SPHD";
        public static String ITEM_TYPE_TRADE_IN_CODE = "TRDI";
        public static String ITEM_TYPE_ORDER_DISCOUNT_CODE = "ORDS";        
    }
    
    //Item constants
    public static int DOLLAR_AMOUNT_MIN_SCALE = 2;
    public static int UNIT_PRICE_MAX_SCALE = 4;
    
    public static class PurchaseOrderDocTypes {
        public static String PURCHASE_ORDER_REOPEN_DOCUMENT  = "KualiPurchaseOrderReopenDocument";
        public static String PURCHASE_ORDER_CLOSE_DOCUMENT  = "KualiPurchaseOrderCloseDocument";
        public static String PURCHASE_ORDER_DOCUMENT  = "KualiPurchaseOrderDocument";
    }

    private static HashMap<String, String> purchaseOrderDocTypes()
    {
        HashMap<String,String> mapSLF; 
        mapSLF =  new HashMap<String,String>();
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, "purchaseOrderPostProcessorCloseService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, "purchaseOrderPostProcessorReopenService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT, "purchaseOrderPostProcessorService");
        return mapSLF;
    }
    public final static HashMap<String,String> PURCHASE_ORDER_DOC_TYPE_MAP =
                        purchaseOrderDocTypes();
    
    public static class PODocumentsStrings {
        public static String PURCHASE_ORDER_CLOSE_QUESTION = "POClose";
        public static String PURCHASE_ORDER_CLOSE_CONFIRM = "POCloseConfirm";
        public static String REOPEN_PO_QUESTION = "ReopenPO";
        public static String CONFIRM_REOPEN_QUESTION = "ConfirmReopen";
        public static String SINGLE_CONFIRMATION_QUESTION = "singleConfirmationQuestion";
    }
}