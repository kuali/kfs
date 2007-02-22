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

import org.kuali.core.util.KualiDecimal;

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
        //TODO: Do we need the rest of these in here?  They're in a table (PUR_PO_STAT_T).
    }
}