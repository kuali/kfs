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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.Constants;

/**
 * Holds constants for PURAP.
 */
public class PurapConstants {

    // STANDARD PARAMETER PREFIXES
    private static final String PURAP_PARAM_PREFIX = "PURAP";
    private static final String STANDARD_SEPARATOR = ".";

    public static class WorkflowConstants {
        // PARAMETER NAMES

        // Global
        public static final String DOC_ADHOC_NODE_NAME = "Adhoc Routing";

        public static class AssignContractManagerDocument {
            public static final String WORKFLOW_DOCUMENT_TITLE = "Contract Manager Assignment";
            public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";
        }

        public static class RequisitionDocument {
            // Node Names
            public static class NodeDetails {
                public static final String CONTENT_REVIEW = "";
                public static final String SUB_ACCOUNT_REVIEW = "";
                public static final String ACCOUNT_REVIEW = "";
                public static final String ORG_REVIEW = "";
                public static final String SEPARAION_OF_DUTIES_REVIEW = "";
                
                public static Map<String,String> REQUISITION_STATUS_BY_NODE_NAME = new HashMap<String,String>();
                public static Map<String,String> REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME = new HashMap<String,String>();
                static {
                    REQUISITION_STATUS_BY_NODE_NAME.put(CONTENT_REVIEW, PurapConstants.RequisitionStatuses.AWAIT_CONTENT_APRVL);
                    REQUISITION_STATUS_BY_NODE_NAME.put(SUB_ACCOUNT_REVIEW, PurapConstants.RequisitionStatuses.AWAIT_SUB_ACCT_APRVL);
                    REQUISITION_STATUS_BY_NODE_NAME.put(ACCOUNT_REVIEW, PurapConstants.RequisitionStatuses.AWAIT_FISCAL_APRVL);
                    REQUISITION_STATUS_BY_NODE_NAME.put(ORG_REVIEW, PurapConstants.RequisitionStatuses.AWAIT_CHART_APRVL);
                    REQUISITION_STATUS_BY_NODE_NAME.put(SEPARAION_OF_DUTIES_REVIEW, PurapConstants.RequisitionStatuses.AWAIT_SEP_OF_DUTY_APRVL);

                    REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME.put(CONTENT_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_CONTENT);
                    REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME.put(SUB_ACCOUNT_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_SUB_ACCT);
                    REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME.put(ACCOUNT_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_FISCAL);
                    REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME.put(ORG_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_CHART);
                    REQUISITION_DISAPPROVAL_STATUS_BY_NODE_NAME.put(SEPARAION_OF_DUTIES_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_SEP_OF_DUTY);
}
            }
            
            public static final String SEPARATION_OF_DUTIES_DOLLAR_AMOUNT = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "SEPARATION_OF_DUTIES_DOLLAR_AMOUNT";

            // Workgroups
            public static final String SEPARATION_OF_DUTIES_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "WORKGROUP.SEPARATION_OF_DUTIES";
        }

        public static class PurchaseOrderDocument {
            public static final String CG_RESTRICTED_OBJECT_CODE_RULE_GROUP_NAME = "PurAp.CG_Restricted_Object_Codes";
            // Workgroups
            public static final String INTERNAL_PURCHASING_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "INTERNAL_PURCHASING_REVIEWERS";
            public static final String VENDOR_EMPLOYEE_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PO_EMPLOYEE_VENDOR_REVIEWERS";
            public static final String VENDOR_NRA_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PO_NRA_VENDOR_REVIEWERS";
            public static final String VENDOR_NRA_EMPLOYEE_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PO_NRA_EMPLOYEE_VENDOR_REVIEWERS";
        }

        public static class PaymentRequestDocument {
            // Workgroups
            public static final String VENDOR_EMPLOYEE_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PREQ_EMPLOYEE_VENDOR_REVIEWERS";
            public static final String VENDOR_NRA_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PREQ_NRA_VENDOR_REVIEWERS";
            public static final String VENDOR_NRA_EMPLOYEE_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PREQ_NRA_EMPLOYEE_VENDOR_REVIEWERS";
            public static final String ACCOUNTS_PAYABLE_REVIEWER_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "PREQ_AP_REVIEWERS";
        }

        public static class CreditMemoDocument {
            // Workgroups
            public static final String ACCOUNTS_PAYABLE_REVIEWER_WORKGROUP_NAME = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "CM_AP_REVIEWERS";
        }
    }

    public static class QuoteTypes {
        public static final String COMPETITIVE = "COMP";
        public static final String PRICE_CONFIRMATION = "CONF";
    }

    public static class QuoteTransmitTypes {
        public static final String PRINT = "PRINT";
//        public static final String FAX = "FAX";
    }

    public static class QuoteStatusCode {
        public static final String DELV = "DELV";
        public static final String FUIP = "FUIP";
        public static final String IIQ = "IIQ";
        public static final String LEXP = "LEXP";
        public static final String MULT = "MULT";
        public static final String NORS = "NORS";
        public static final String PTFE = "PTFE";
        public static final String RECV = "RECV";
        public static final String RIR = "RIR";
        public static final String RECL = "RECL";
        public static final String RNLB = "RNLB";
        public static final String RNLN = "RNLN";
        public static final String NOBD = "NOBD";
        public static final String SQNA = "SQNA";
        public static final String TINC = "TINC";
    }

    public static class Workgroups {
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = "PURAP.WORKGROUP.ACCOUNTS_PAYABLE";
        //TODO: need an accounts payable supervisor group
        public static final String WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR = "PURAP.WORKGROUP.ACCOUNTS_PAYABLE";
        public static final String WORKGROUP_PURCHASING = "PURAP.WORKGROUP.PURCHASING";
        public static final String WORKGROUP_TAXNBR_ACCESSIBLE = "PURAP.WORKGROUP.TAXNBR_ACCESSIBLE";
        
        public static final String SEARCH_SPECIAL_ACCESS = PURAP_PARAM_PREFIX + STANDARD_SEPARATOR + "SEARCH_SPECIAL_ACCESS";
    }

    public static final String NOTE_TAB_WARNING = "noteWarning";
    public static final String STATUS_HISTORY_TAB_WARNING = "statusHistoryWarning";

    public static final String QUESTION_INDEX = "questionIndex";
    public static final String REMOVE_ACCOUNTS_QUESTION = "RemoveAccounts";
    public static final String QUESTION_ROUTE_DOCUMENT_TO_COMPLETE = "Completing this document will remove it from your Action List.<br/><br/>  Are you sure you want to continue?";
    public static final String QUESTION_REMOVE_ACCOUNTS = "question.document.purap.removeAccounts";
    
    public static final String REQ_REASON_NOT_APO = "Requisition did not become an APO because: ";
    public static final String REQ_UNABLE_TO_CREATE_NOTE = "Unable to create a note on this document.";

    // Delivery Tag
    public static final String DELIVERY_BUILDING_OTHER = "Other";
    public static final String DELIVERY_BUILDING_OTHER_CODE = "OTH";

    // PDF KFSConstants
    public static final String IMAGE_TEMP_PATH = "PURAP.IMAGE.TEMP.PATH";
    public static final String PDF_DIRECTORY = "PURAP.PDF.DIRECTORY";
    public static final String STATUS_INQUIRY_URL = "PURAP.STATUS.INQUIRY.URL";
    public static final String PURCHASING_DIRECTOR_IMAGE_PREFIX = "PURAP.PUR.DIRECTOR.IMAGE.PREFIX";
    public static final String PURCHASING_DIRECTOR_IMAGE_EXTENSION = "PURAP.PUR.DIRECTOR.IMAGE.EXTENSION";
    public static final String CONTRACT_MANAGER_IMAGE_PREFIX = "PURAP.CONTRACT.MGR.IMAGE.PREFIX";
    public static final String CONTRACT_MANAGER_IMAGE_EXTENSION = "PURAP.CONTRACT.MGR.IMAGE.EXTENSION";
    public static final String LOGO_IMAGE_PREFIX = "PURAP.PDF.LOGO.IMAGE.PREFIX";
    public static final String LOGO_IMAGE_EXTENSION = "PURAP.PDF.LOGO.IMAGE.EXTENSION";
    public static final String PDF_IMAGES_AVAILABLE_INDICATOR = "PURAP.PDF.IMAGES.AVAILABLE.INDICATOR";

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

    public static String REQ_B2B_ALLOW_COPY_DAYS = "5";

    public static class RequisitionSources {
        public static String STANDARD_ORDER = "STAN";
        public static String B2B = "B2B";
    }

    // PURCHASE ORDER VENDOR CHOICE CODES
    public static class VendorChoice {
        public static String CONTRACTED_PRICE = "CONT";
        public static String SMALL_ORDER = "SMAL";
        public static String PROFESSIONAL_SERVICE = "PROF";
        public static String SUBCONTRACT = "SUBC";
    }

    public static Integer APO_CONTRACT_MANAGER = new Integer(99);

    // Requisition/Purchase Order Tab Errors
    public static final String DELIVERY_TAB_ERRORS = "document.delivery*";
    public static final String VENDOR_ERRORS = "document.vendor*";
    public static final String ADDITIONAL_TAB_ERRORS = "document.requestor*,document.purchaseOrderTransmissionMethodCode,document.chartOfAccountsCode,document.organizationCode,document.purchaseOrderCostSourceCode,document.purchaseOrderTotalLimit";
    public static final String ITEM_TAB_ERRORS = "document.item*,accountDistribution*";
    public static final String ACCOUNT_SUMMARY_TAB_ERRORS = "document.accountSummary*";
    public static final String ACCOUNT_DISTRIBUTION_ERROR_KEY = "accountDistribution";

    // PO/Quotes Tab Constants
    public static final String QUOTE_TAB_ERRORS = "document.quote*,quote*";

    // Assign Contract Manager
    public static final String ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC = "Contract Manager Assigned";
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "document.unassignedRequisition*";
    
    // Credit Memo Tab Constants

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
        
        public static Set INCOMPLETE_STATUSES = new HashSet();
        static {
            INCOMPLETE_STATUSES.add(AWAIT_TAX_APRVL);
            INCOMPLETE_STATUSES.add(AWAIT_BUDGET_APRVL);
            INCOMPLETE_STATUSES.add(AWAIT_CONTRACTS_GRANTS_APRVL);
            INCOMPLETE_STATUSES.add(AWAIT_PURCHASING_APRVL);
            INCOMPLETE_STATUSES.add(AWAIT_SPECIAL_APRVL);
            INCOMPLETE_STATUSES.add(QUOTE);
            INCOMPLETE_STATUSES.add(CXML_ERROR);
            INCOMPLETE_STATUSES.add(PENDING_CXML);
            INCOMPLETE_STATUSES.add(IN_PROCESS);
            INCOMPLETE_STATUSES.add(PAYMENT_HOLD);
            INCOMPLETE_STATUSES.add(PENDING_FAX);
            INCOMPLETE_STATUSES.add(PENDING_PRINT);
            INCOMPLETE_STATUSES.add(WAITING_FOR_VENDOR);
            INCOMPLETE_STATUSES.add(WAITING_FOR_DEPARTMENT);
        }
        
    }


    public static class ItemTypeCodes {
        // ITEM TYPES
        public static String ITEM_TYPE_ITEM_CODE = "ITEM";
        public static String ITEM_TYPE_FREIGHT_CODE = "FRHT";
        public static String ITEM_TYPE_SHIP_AND_HAND_CODE = "SPHD";
        public static String ITEM_TYPE_TRADE_IN_CODE = "TRDI";
        public static String ITEM_TYPE_ORDER_DISCOUNT_CODE = "ORDS";
        public static String ITEM_TYPE_SERVICE_CODE = "SRVC";
        public static String ITEM_TYPE_MIN_ORDER_CODE = "MNOR";
        public static String ITEM_TYPE_MISC_CODE = "MISC";
        public static String ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE = "DISC";
        public static String ITEM_TYPE_FEDERAL_TAX_CODE = "FDTX";
        public static String ITEM_TYPE_STATE_TAX_CODE = "STTX";
        public static String ITEM_TYPE_FEDERAL_GROSS_CODE = "FDGR";
        public static String ITEM_TYPE_STATE_GROSS_CODE = "STGR";
        public static String ITEM_TYPE_RESTCK_FEE_CODE = "RSTO";
        public static String ITEM_TYPE_MISC_CRDT_CODE = "MSCR";

    }

    // Item constants
    public static int DOLLAR_AMOUNT_MIN_SCALE = 2;
    public static int UNIT_PRICE_MAX_SCALE = 4;
    public static int PREQ_DESC_LENGTH = 500;

    public static class PurchaseOrderDocTypes {
        public static String PURCHASE_ORDER_REOPEN_DOCUMENT = "KualiPurchaseOrderReopenDocument";
        public static String PURCHASE_ORDER_CLOSE_DOCUMENT = "KualiPurchaseOrderCloseDocument";
        public static String PURCHASE_ORDER_DOCUMENT = "KualiPurchaseOrderDocument";
        public static String PURCHASE_ORDER_RETRANSMIT_DOCUMENT = "KualiPurchaseOrderRetransmitDocument";
        public static String PURCHASE_ORDER_PRINT_DOCUMENT = "KualiPurchaseOrderPrintDocument";
        public static String PURCHASE_ORDER_VOID_DOCUMENT = "KualiPurchaseOrderVoidDocument";
        public static String PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT = "KualiPurchaseOrderPaymentHoldDocument";
        public static String PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT = "KualiPurchaseOrderRemoveHoldDocument";
        public static String PURCHASE_ORDER_AMENDMENT_DOCUMENT = "KualiPurchaseOrderAmendmentDocument";
    }


    private static HashMap<String, String> purchaseOrderDocTypes() {
        HashMap<String, String> mapSLF;
        mapSLF = new HashMap<String, String>();
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, "purchaseOrderPostProcessorCloseService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, "purchaseOrderPostProcessorReopenService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, "purchaseOrderPostProcessorVoidService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, "purchaseOrderPostProcessorPrintService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, "purchaseOrderPostProcessorRetransmitService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, "purchaseOrderPostProcessorPaymentHoldService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, "purchaseOrderPostProcessorRemoveHoldService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, "purchaseOrderPostProcessorAmendmentService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT, "purchaseOrderPostProcessorService");
        return mapSLF;
    }

    public final static HashMap<String, String> PURCHASE_ORDER_DOC_TYPE_MAP = purchaseOrderDocTypes();

    public static class PODocumentsStrings {
        public static String CLOSE_QUESTION = "POClose";
        public static String CLOSE_CONFIRM = "POCloseConfirm";
        public static String CLOSE_NOTE_PREFIX = "Note entered while closing a Purchase Order :";

        public static String REOPEN_PO_QUESTION = "ReopenPO";
        public static String CONFIRM_REOPEN_QUESTION = "ConfirmReopen";
        public static String REOPEN_NOTE_PREFIX = "Note entered while reopening a Purchase Order : ";

        public static String VOID_QUESTION = "POVoid";
        public static String VOID_CONFIRM = "POVoidConfirm";
        public static String VOID_NOTE_PREFIX = "Note entered while voiding a Purchase Order :";

        public static String PAYMENT_HOLD_QUESTION = "POPaymentHold";
        public static String PAYMENT_HOLD_CONFIRM = "POPaymentHoldConfirm";
        public static String PAYMENT_HOLD_NOTE_PREFIX = "Note entered while putting a Purchase Order on payment hold :";

        public static String REMOVE_HOLD_QUESTION = "PORemoveHold";
        public static String REMOVE_HOLD_CONFIRM = "PORemoveHoldConfirm";
        public static String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing a Purchase Order from payment hold :";
        public static String REMOVE_HOLD_FYI = "This document was taken off Payment Hold status.";

        public static String AMENDMENT_PO_QUESTION = "AmendmentPO";
        public static String CONFIRM_AMENDMENT_QUESTION = "ConfirmAmendment";
        public static String AMENDMENT_NOTE_PREFIX = "Note entered while amending a Purchase Order : ";

        public static String CONFIRM_AWARD_QUESTION = "POConfirmAward";
        public static String CONFIRM_AWARD_RETURN = "completeQuote";

        public static String CONFIRM_CANCEL_QUESTION = "POCancelQuote";
        public static String CONFIRM_CANCEL_RETURN = "cancelQuote";

        public static String SINGLE_CONFIRMATION_QUESTION = "singleConfirmationQuestion";
    }

    // PAYMENT REQUEST
    public static String PAYMENT_REQUEST_DOCUMENT = "KualiPaymentRequestDocument";
    public static String PAYMENT_REQUEST_DOCUMENT_DOC_TYPE = "PaymentRequestDocument";
    
    public static int PREQ_PAY_DATE_CALCULATION_DAYS = 28;

    // Weird PaymentTermsType is due on either the 10th or 25th with no discount
    public static String PMT_TERMS_TYP_NO_DISCOUNT_CD = "00N2T";

    public static class PaymentRequestIndicatorText {
        public static String HOLD = "HOLD";
        public static String REQUEST_CANCEL = "REQUEST CANCEL";
    }
    
    public static class PaymentRequestStatuses {
        public static String INITIATE = "INIT";
        public static String IN_PROCESS = "INPR";
        public static String CANCELLED_POST_APPROVE = "CANC";
        public static String CANCELLED_IN_PROCESS = "VOID";
        public static String AP_APPROVED = "APAD";
        public static String AWAITING_SUB_ACCT_MGR_APPROVAL = "ASAA";
        public static String AWAITING_FISCAL_APPROVAL = "AFOA";
        public static String DEPARTMENT_APPROVED = "DPTA";
        public static String AUTO_APPROVED = "AUTO";        
        public static String AWAITING_CHART_REVIEW = "ACHA";
        public static String AWAITING_TAX_APPROVAL = "ATAX";
        /*
         * Modify as required: public static String CANCELLED = "CANC"; public static String CLOSED = "CLOS"; public static String
         * AWAIT_CONTENT_APRVL = "ACNT"; public static String AWAIT_SUB_ACCT_APRVL = "ASUB"; public static String AWAIT_FISCAL_APRVL =
         * "AFIS"; public static String AWAIT_CHART_APRVL = "ACHA"; public static String AWAIT_SEP_OF_DUTY_APRVL = "ASOD"; public
         * static String DAPRVD_CONTENT = "DCNT"; public static String DAPRVD_SUB_ACCT = "DSUB"; public static String DAPRVD_FISCAL =
         * "DFIS"; public static String DAPRVD_CHART = "DCHA"; public static String DAPRVD_SEP_OF_DUTY = "DSOD"; public static
         * String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR"; public static String CONTRACT_MANAGER_ASSGN = "CMRA";
         */
        /*
         * // PAYMENT REQUEST STATUSES public static String PREQ_STAT_IN_PROCESS = "INPR"; // In process (not routed yet) public
         * static String PREQ_STAT_CANCELLED_POST_APPROVE = "CANC"; public static String PREQ_STAT_CANCELLED_IN_PROCESS = "VOID";
         * public static String PREQ_STAT_AP_APPROVED = "APAD"; public static String PREQ_STAT_AUTO_APPROVED = "AUTO"; public static
         * String PREQ_STAT_DEPARTMENT_APPROVED = "DPTA"; public static String PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL = "ASAA"; //
         * Waiting for Sub Acct Manager approval public static String PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL = "AFOA"; // Waiting for
         * Fiscal Officer approval public static String PREQ_STAT_AWAIT_CHART_APRVL = "ACHA"; // Waiting for Chart/Org approval
         * public static String PREQ_STAT_AWAIT_TAX_APRVL = "ATAX"; // Waiting for Tax approval public static String
         * PREQ_STAT_PENDING_E_INVOICE = "PEIN"; // PAYMENT REQUEST STATUSES TO BE AUTO APPROVED public static String[]
         * PREQ_STATUSES_FOR_AUTO_APPROVE =
         * {PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL,PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL,PREQ_STAT_AWAIT_CHART_APRVL}; // PAYMENT REQUEST
         * PAY DATE CALCULATION DAYS public static int PREQ_PAY_DATE_CALCULATION_DAYS = 28; // PREQ CANCEL FORWARDS public static
         * String PREQ_CANCEL_FORWARD_DOC_HANDLER = "dochandler"; public static String PREQ_CANCEL_FORWARD_TAB_PAGE = "editpreq";
         */
    }

    public static class PREQDocumentsStrings {
        public static String DUPLICATE_INVOICE_QUESTION = "PREQDuplicateInvoice";
        public static String HOLD_PREQ_QUESTION = "HoldPREQ";
        public static String CONFIRM_HOLD_QUESTION = "ConfirmHold";
        public static String HOLD_NOTE_PREFIX = "Note entered while placing Payment Request on hold : ";
        public static String CANCEL_PREQ_QUESTION = "CancelPREQ";
        public static String CONFIRM_CANCEL_QUESTION = "ConfirmCancel";
        public static String CANCEL_NOTE_PREFIX = "Note entered while requesting cancel on Payment Request : ";
        public static String REMOVE_CANCEL_PREQ_QUESTION = "RemoveCancelPREQ";
        public static String CONFIRM_REMOVE_CANCEL_QUESTION = "ConfirmRemoveCancel";
        public static String REMOVE_CANCEL_NOTE_PREFIX = "Note entered while removing a request cancel on Payment Request : ";
        public static String PURCHASE_ORDER_ID = "Purchase Order Identifier";
        public static String INVOICE_DATE = "Invoice Date";
        public static String INVOICE_NUMBER = "Invoice Number";
        public static String VENDOR_INVOICE_AMOUNT = "Vendor Invoice Amount";
    }

    private static HashMap<String, String> itemTypes()
    {
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put("RequisitionDocument", "Kuali.FinancialTransactionProcessing.RequisitionDocument");
        map.put("PurchaseOrderDocument", "Kuali.FinancialTransactionProcessing.PurchaseOrderDocument");
        map.put("PaymentRequestDocument", "Kuali.FinancialTransactionProcessing.PaymentRequestDocument");
        map.put("CreditMemoDocumentFromVendor", "Kuali.FinancialTransactionProcessing.CreditMemoDocument.FromVendor");
        map.put("CreditMemoDocumentFromPurchaseOrder", "Kuali.FinancialTransactionProcessing.CreditMemoDocument.FromPurchaseOrder");
        map.put("CreditMemoDocumentFromPaymentRequest", "Kuali.FinancialTransactionProcessing.CreditMemoDocument.FromPaymentRequest");
        return map;
    }
    public final static HashMap<String,String> ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP =
                        itemTypes();

    public static final String BELOW_THE_LINES_PARAMETER = "BELOW_THE_LINE_ITEMS";
    public static final String ITEM_ALLOWS_ZERO = "ALLOWS_ZERO";
    public static final String ITEM_ALLOWS_POSITIVE = "ALLOWS_POSITIVE";
    public static final String ITEM_ALLOWS_NEGATIVE = "ALLOWS_NEGATIVE";
    public static final String ITEM_REQUIRES_USER_ENTERED_DESCRIPTION = "REQUIRES_USER_ENTERED_DESCRIPTION";

    public static class ItemFields {
        public static final String QUANTITY = "Quantity";
        public static final String UNIT_OF_MEASURE = "Unit of Measure";
        public static final String DESCRIPTION = "Description";
        public static final String UNIT_COST = "Unit Cost";
        public static final String INVOICE_QUANTITY = "Qty Invoiced";
        public static final String OPEN_QUANTITY = "Open Qty";
        public static final String INVOICE_EXTENDED_PRICE = "Total Inv Cost";
    }
    
    // PAYMENT REQUEST
    public static String CREDIT_MEMO_DOCUMENT = "KualiCreditMemoDocument";
    public static String CREDIT_MEMO_DOCUMENT_DOC_TYPE = "CreditMemoDocument";
    
    public static class CreditMemoStatuses {
        public static String INITIATE = "INIT";
        public static String IN_PROCESS = "INPR";
        public static String CANCELLED_POST_APPROVE = "CANC";
        public static String CANCELLED_IN_PROCESS = "VOID";
    }
    
    public static class CreditMemoTypes{
        public static final String TYPE_VENDOR = "Vendor";
        public static final String TYPE_PO = "PO";
        public static final String TYPE_PREQ = "PREQ";
    }
    
    public static class CMDocumentsStrings {
        public static String DUPLICATE_CREDIT_MEMO_QUESTION = "CMDuplicateInvoice";
        public static String HOLD_CREDIT_MEMO_QUESTION = "HoldPREQ";
        public static String CONFIRM_HOLD_QUESTION = "ConfirmHold";
        public static String HOLD_NOTE_PREFIX = "Note entered while placing Payment Request on hold : ";
    }
    
    private static HashMap<String,String> uncopyableFields() {
        HashMap<String,String> fields = new HashMap<String,String>();
        fields.put(Constants.VERSION_NUMBER, "");
        return fields;
    }
    /*
     * Fields that shouldn't be copied by our reflective copy method.
     * This should only contain fields that are known throughout objects not
     * item/doc specific ones
     */
    public final static HashMap<String,String> KNOWN_UNCOPYABLE_FIELDS = uncopyableFields();
    
    private static HashMap<String,String> uncopyableItemFields() {
        HashMap<String,String> fields = new HashMap<String,String>();
        fields.put(PurapPropertyConstants.ITEM_IDENTIFIER, "");
        fields.put(PurapPropertyConstants.ACCOUNTS, "");
        return fields;
    }
    /*
     * Fields that shouldn't be copied by our reflective copy method.
     * This should only contain fields that are known throughout objects not
     * item/doc specific ones
     */
    public final static HashMap<String,String> ITEM_UNCOPYABLE_FIELDS = uncopyableItemFields();

    private static HashMap<String,String> uncopyablePREQItemFields() {
        HashMap<String,String> fields = new HashMap<String,String>(ITEM_UNCOPYABLE_FIELDS);
        fields.put(PurapPropertyConstants.QUANTITY, "");
        fields.put(PurapPropertyConstants.EXTENDED_PRICE,"");
        return fields;
    }
    /*
     * fields that shouldn't be copied on PREQ item
     */
    public final static HashMap<String,String> PREQ_ITEM_UNCOPYABLE_FIELDS = uncopyablePREQItemFields();
    
    public final static String PO_DOC_TYPE_CODE = "PO";
    public final static String PURAP_ORIGIN_CODE = "EP";
    
}