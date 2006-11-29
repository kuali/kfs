/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/PurapConstants.java,v $
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

    //Miscellaneous generic constants
    public static final String NONE = "NONE";
    public static final String CREATE_NEW_DIVISION = "Create a New Division";
    public static final String NAME_DELIM = ", ";
    public static final String VENDOR_LOOKUPABLE_IMPL = "vendorLookupable";
    public static final String DASH = "-";
    public static final String VENDOR_HEADER_ATTR = "vendorHeader";

    //Vendor Tax Types
    public static final String TAX_TYPE_FEIN = "FEIN";
    public static final String TAX_TYPE_SSN = "SSN";
    //public static final String TAX_TYPE_ITIN = "ITIN";  //are we implementing this in Kuali??
    
    //North American phone number formats as regular expressions
    public static final String GENERIC_PHONE_FORMAT_1 = "\\d{3}-\\d{3}-\\d{4}";
    public static final String GENERIC_PHONE_FORMAT_2 = "\\(\\d{3}\\)\\s\\d{3}-\\d{4}";
    public static final String GENERIC_PHONE_FORMAT_3 = "\\d{3}\\s\\d{3}\\s\\d{4}";
    
    public static final String[] GENERIC_PHONE_NUMBER_FORMATS = {
        GENERIC_PHONE_FORMAT_1,
        GENERIC_PHONE_FORMAT_2,
        GENERIC_PHONE_FORMAT_3
    };
    public static final int GENERIC_DEFAULT_PHONE_NUM_DIGITS = 10;
      
    //Tax Number formats as  regular expressions
    public static final String TAX_NUM_FORMAT_1 = "\\d{3}\\d{2}\\d{4}";
    public static final String TAX_NUM_FORMAT_2 = "\\d{3}-\\d{2}-\\d{4}";
    
    public static final String[] TAX_NUMBER_FORMATS = {
        TAX_NUM_FORMAT_1,
        TAX_NUM_FORMAT_2,
    };
    
    public static final int DEFAULT_TAX_NUM_DIGITS = 9;
    public static final String NOT_ALLOWED_TAX_NUMBER = "356001673";
    public static final String ALL_ZEROS_TAX_NUMBER = "000000000";
       
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
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "contractManagerCode,document.assignContractManager";
    public static final String ASSIGN_CONTRACT_MANAGER_ERRORS = "contractManagerCode";

}