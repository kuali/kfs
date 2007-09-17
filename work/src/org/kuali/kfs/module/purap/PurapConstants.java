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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.JstlConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderStatusHistory;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase;

/**
 * Holds constants for PURAP.
 */
public class PurapConstants extends JstlConstants {

    // special user used in the special ap cancel action
    public static final String SYSTEM_AP_USER = RiceConstants.SYSTEM_USER;
    
    public static final KualiDecimal HUNDRED = new KualiDecimal(100);
    
    // STANDARD PARAMETER PREFIXES
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
        // TODO delyea - check statuses for use/MDS entry
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED = "CANC";
        public static final String CLOSED = "CLOS";
        public static final String AWAIT_CONTENT_REVIEW = "ACNT";
        public static final String AWAIT_SUB_ACCT_REVIEW = "ASUB";
        public static final String AWAIT_FISCAL_REVIEW = "AFIS";
        public static final String AWAIT_CHART_REVIEW = "ACHA";
        public static final String AWAIT_SEP_OF_DUTY_REVIEW = "ASOD";
        public static final String DAPRVD_CONTENT = "DCNT";
        public static final String DAPRVD_SUB_ACCT = "DSUB";
        public static final String DAPRVD_FISCAL = "DFIS";
        public static final String DAPRVD_CHART = "DCHA";
        public static final String DAPRVD_SEP_OF_DUTY = "DSOD";
        public static final String DAPRVD_SPECIAL = "DGEN";
        public static final String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR";
        public static final String CONTRACT_MANAGER_ASSGN = "CMRA";
    }

    public static class POCostSources {
        public static final String ESTIMATE = "EST";
    }

    public static final class POTransmissionMethods {
        public static final String FAX = "FAX";
        public static final String PRINT = "PRIN";
        public static final String NOPRINT = "NOPR";
        public static final String ELECTRONIC = "ELEC";
    }

    public static final String REQ_B2B_ALLOW_COPY_DAYS = "5";

    public static class RequisitionSources {
        public static final String STANDARD_ORDER = "STAN";
        public static final String B2B = "B2B";
    }

    // PURCHASE ORDER VENDOR CHOICE CODES
    public static class VendorChoice {
        public static final String CONTRACTED_PRICE = "CONT";
        public static final String SMALL_ORDER = "SMAL";
        public static final String PROFESSIONAL_SERVICE = "PROF";
        public static final String SUBCONTRACT = "SUBC";
    }

    public static Integer APO_CONTRACT_MANAGER = new Integer(99);

    // Requisition/Purchase Order Tab Errors
    public static final String DELIVERY_TAB_ERRORS = "document.delivery*";
    public static final String VENDOR_ERRORS = "document.vendor*";
    public static final String ADDITIONAL_TAB_ERRORS = "document.requestor*,document.purchaseOrderTransmissionMethodCode,document.chartOfAccountsCode,document.organizationCode,document.purchaseOrderCostSourceCode,document.purchaseOrderTotalLimit";
    public static final String ITEM_TAB_ERRORS = "document.item*,accountDistribution*";
    public static final String ITEM_TAB_ERROR_PROPERTY = ITEM_TAB_ERRORS;  // used to be "newPurchasingItemLine"
    public static final String ACCOUNT_SUMMARY_TAB_ERRORS = "document.accountSummary*";
    public static final String STATUS_HISTORY_TAB_ERRORS = "document.statusHistories*";
    public static final String ACCOUNT_DISTRIBUTION_ERROR_KEY = "accountDistribution";
    public static final String RELATED_DOCS_TAB_ERRORS = "";
    public static final String PAYMENT_HISTORY_TAB_ERRORS = "";

    // PO/Quotes Tab Constants
    public static final String QUOTE_TAB_ERRORS = "document.quote*,quote*,purchaseOrderVendorQuotes*";

    // Assign Contract Manager
    public static final String ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC = "Contract Manager Assigned";
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "document.unassignedRequisition*";
    
    // Credit Memo Tab Constants

    public static class PurchaseOrderStatuses {
        // TODO delyea - check statuses for use/MDS entry
        // TODO delyea - add statuses for awaiting and disapproved 'change' docs?
        public static final String IN_PROCESS = "INPR";
        public static final String WAITING_FOR_VENDOR = "WVEN";
        public static final String WAITING_FOR_DEPARTMENT = "WDPT";
        public static final String OPEN = "OPEN";
        public static final String CLOSED = "CLOS";
        public static final String CANCELLED = "CANC";
        public static final String PAYMENT_HOLD = "PHOL";
        public static final String AWAIT_TAX_REVIEW = "WTAX";
        public static final String AWAIT_BUDGET_REVIEW = "WBUD";
        public static final String AWAIT_CONTRACTS_GRANTS_REVIEW = "WCG";
        public static final String AWAIT_PURCHASING_REVIEW = "WPUR";
        @Deprecated
        public static final String AWAIT_SPECIAL_REVIEW = "WSPC";
        public static final String DAPRVD_TAX = "DTAX";
        public static final String DAPRVD_BUDGET = "DBUD";
        public static final String DAPRVD_CONTRACTS_GRANTS = "DCG";
        public static final String DAPRVD_PURCHASING = "DPUR";
        public static final String DAPRVD_SPECIAL = "DSPC";
        public static final String CXML_ERROR = "CXER";
        public static final String PENDING_CXML = "CXPE";
        public static final String PENDING_FAX = "FXPE";
        public static final String PENDING_PRINT = "PRPE";
        public static final String QUOTE = "QUOT";
        public static final String VOID = "VOID";
        public static final String AMENDMENT = "AMND";
        
        public static final Set<String> INCOMPLETE_STATUSES = new HashSet<String>();
        public static final Set<String> CONTRACT_MANAGER_CHANGEABLE_STATUSES = new HashSet<String>();
        static {
            INCOMPLETE_STATUSES.add(AWAIT_TAX_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_BUDGET_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_CONTRACTS_GRANTS_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_PURCHASING_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_SPECIAL_REVIEW);
            INCOMPLETE_STATUSES.add(QUOTE);
            INCOMPLETE_STATUSES.add(CXML_ERROR);
            INCOMPLETE_STATUSES.add(PENDING_CXML);
            INCOMPLETE_STATUSES.add(IN_PROCESS);
            INCOMPLETE_STATUSES.add(PAYMENT_HOLD);
            INCOMPLETE_STATUSES.add(PENDING_FAX);
            INCOMPLETE_STATUSES.add(PENDING_PRINT);
            INCOMPLETE_STATUSES.add(WAITING_FOR_VENDOR);
            INCOMPLETE_STATUSES.add(WAITING_FOR_DEPARTMENT);
            
            CONTRACT_MANAGER_CHANGEABLE_STATUSES.add(IN_PROCESS);
            CONTRACT_MANAGER_CHANGEABLE_STATUSES.add(WAITING_FOR_VENDOR);
            CONTRACT_MANAGER_CHANGEABLE_STATUSES.add(WAITING_FOR_DEPARTMENT);
            
        }
        
        /**
         *  Do not include 'OPEN' status in this map.  The 'OPEN' status
         *  is the default status that is set when no status exists for 
         *  a particular pending transmission type code.
         *  
         *  @see {@link org.kuali.module.purap.service.PurchaseOrderService#completePurchaseOrder(org.kuali.module.purap.document.PurchaseOrderDocument)}
         */
        private static final Map<String,String> getStatusesByTransmissionType() {
            Map<String,String> statusByTrans = new HashMap<String,String>();
            statusByTrans.put(PurapConstants.POTransmissionMethods.PRINT, PENDING_PRINT);
            statusByTrans.put(PurapConstants.POTransmissionMethods.ELECTRONIC, PENDING_CXML);
            statusByTrans.put(PurapConstants.POTransmissionMethods.FAX, PENDING_FAX);
            return Collections.unmodifiableMap(statusByTrans);
    }
        public static final Map<String,String> STATUSES_BY_TRANSMISSION_TYPE = getStatusesByTransmissionType();

    }

    public static final class ItemTypeCodes {
        // ITEM TYPES
        public static final String ITEM_TYPE_ITEM_CODE = "ITEM";
        public static final String ITEM_TYPE_SERVICE_CODE = "SRVC";
        public static final String ITEM_TYPE_FREIGHT_CODE = "FRHT";
        public static final String ITEM_TYPE_SHIP_AND_HAND_CODE = "SPHD";
        public static final String ITEM_TYPE_TRADE_IN_CODE = "TRDI";
        public static final String ITEM_TYPE_ORDER_DISCOUNT_CODE = "ORDS";
        public static final String ITEM_TYPE_MIN_ORDER_CODE = "MNOR";
        public static final String ITEM_TYPE_MISC_CODE = "MISC";
        public static final String ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE = "DISC";
        public static final String ITEM_TYPE_FEDERAL_TAX_CODE = "FDTX";
        public static final String ITEM_TYPE_STATE_TAX_CODE = "STTX";
        public static final String ITEM_TYPE_FEDERAL_GROSS_CODE = "FDGR";
        public static final String ITEM_TYPE_STATE_GROSS_CODE = "STGR";
        public static final String ITEM_TYPE_RESTCK_FEE_CODE = "RSTO";
        public static final String ITEM_TYPE_MISC_CRDT_CODE = "MSCR";

    }

    // Item constants
    public static final int DOLLAR_AMOUNT_MIN_SCALE = 2;
    public static final int UNIT_PRICE_MAX_SCALE = 4;
    public static final int PREQ_DESC_LENGTH = 500;
    public static final String PREQ_DISCOUNT_MULT = "-0.01";

    public static class PurchaseOrderDocTypes {
        public static final String PURCHASE_ORDER_REOPEN_DOCUMENT = "PurchaseOrderReopenDocument";
        public static final String PURCHASE_ORDER_CLOSE_DOCUMENT = "PurchaseOrderCloseDocument";
        public static final String PURCHASE_ORDER_DOCUMENT = "PurchaseOrderDocument";
        public static final String PURCHASE_ORDER_RETRANSMIT_DOCUMENT = "PurchaseOrderRetransmitDocument";
        public static final String PURCHASE_ORDER_PRINT_DOCUMENT = "PurchaseOrderPrintDocument";
        public static final String PURCHASE_ORDER_VOID_DOCUMENT = "PurchaseOrderVoidDocument";
        public static final String PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT = "PurchaseOrderPaymentHoldDocument";
        public static final String PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT = "PurchaseOrderRemoveHoldDocument";
        public static final String PURCHASE_ORDER_AMENDMENT_DOCUMENT = "PurchaseOrderAmendmentDocument";
    }

    public static class PODocumentsStrings {
        public static final String CLOSE_QUESTION = "POClose";
        public static final String CLOSE_CONFIRM = "POCloseConfirm";
        public static final String CLOSE_NOTE_PREFIX = "Note entered while closing a Purchase Order :";

        public static final String REOPEN_PO_QUESTION = "ReopenPO";
        public static final String CONFIRM_REOPEN_QUESTION = "ConfirmReopen";
        public static final String REOPEN_NOTE_PREFIX = "Note entered while reopening a Purchase Order : ";

        public static final String VOID_QUESTION = "POVoid";
        public static final String VOID_CONFIRM = "POVoidConfirm";
        public static final String VOID_NOTE_PREFIX = "Note entered while voiding a Purchase Order :";

        public static final String PAYMENT_HOLD_QUESTION = "POPaymentHold";
        public static final String PAYMENT_HOLD_CONFIRM = "POPaymentHoldConfirm";
        public static final String PAYMENT_HOLD_NOTE_PREFIX = "Note entered while putting a Purchase Order on payment hold :";

        public static final String REMOVE_HOLD_QUESTION = "PORemoveHold";
        public static final String REMOVE_HOLD_CONFIRM = "PORemoveHoldConfirm";
        public static final String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing a Purchase Order from payment hold :";
        public static final String REMOVE_HOLD_FYI = "This document was taken off Payment Hold status.";

        public static final String AMENDMENT_PO_QUESTION = "AmendmentPO";
        public static final String CONFIRM_AMENDMENT_QUESTION = "ConfirmAmendment";
        public static final String AMENDMENT_NOTE_PREFIX = "Note entered while amending a Purchase Order : ";

        public static final String CONFIRM_AWARD_QUESTION = "POConfirmAward";
        public static final String CONFIRM_AWARD_RETURN = "completeQuote";

        public static final String CONFIRM_CANCEL_QUESTION = "POCancelQuote";
        public static final String CONFIRM_CANCEL_RETURN = "cancelQuote";

        public static final String SINGLE_CONFIRMATION_QUESTION = "singleConfirmationQuestion";       
        
        public static final String MANUAL_STATUS_CHANGE_QUESTION = "manualStatusChangeQuestion";
        public static final String OPEN_STATUS = "Open";
    }
    
    public static final String PO_OVERRIDE_NOT_TO_EXCEED_QUESTION = "OverrideNotToExceed";

    // ACCOUNTS PAYABLE
    public static final String AP_OVERRIDE_INVOICE_NOMATCH_QUESTION = "OverrideInvoiceNoMatch";
    
    public static class AccountsPayableDocumentStrings {
        public static final String CANCEL_NOTE_PREFIX = "Note entered while canceling document: ";
    }
    // PAYMENT REQUEST
    public static final String PAYMENT_REQUEST_ACTION_NAME = "PaymentRequest";
    public static final String PAYMENT_REQUEST_DOCUMENT_DOC_TYPE = "PaymentRequestDocument";
    public static final int PREQ_PAY_DATE_DEFAULT_NUMBER_OF_DAYS = 10;
    public static final String PREQ_PAY_DATE_DAYS = "days";
    public static final String PREQ_PAY_DATE_DATE = "date";
    public static final int PREQ_PAY_DATE_EMPTY_TERMS_DEFAULT_DAYS = 28;
    public static final int PREQ_PAY_DATE_DAYS_BEFORE_WARNING = 60;

    // Weird PaymentTermsType is due on either the 10th or 25th with no discount
    public static final String PMT_TERMS_TYP_NO_DISCOUNT_CD = "00N2T";

    public static final String PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS = "PURAP.AP_SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS";
    public static final String PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS = "PURAP.AP_SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS";

    public static final class PaymentRequestIndicatorText {
        public static final String HOLD = "HOLD";
        public static final String REQUEST_CANCEL = "REQUEST CANCEL";
    }
    
    public static final class PaymentRequestStatuses {
        // TODO delyea - check statuses for use/MDS entry
        public static final String INITIATE = "INIT"; 
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED_IN_PROCESS = "CIPR";
        public static final String CANCELLED_PRIOR_TO_AP_APPROVAL = "VOID";
        public static final String CANCELLED_POST_AP_APPROVE = "CANC";
        public static final String DEPARTMENT_APPROVED = "DPTA";
        public static final String AUTO_APPROVED = "AUTO";        
        public static final String AWAITING_ACCOUNTS_PAYABLE_REVIEW = "APAD";   // Waiting for Accounts Payable approval
        public static final String AWAITING_SUB_ACCT_MGR_REVIEW = "ASAA";   // Waiting for Sub Acct Manager approval
        public static final String AWAITING_FISCAL_REVIEW = "AFOA";   // Waiting for Fiscal Officer approval
        public static final String AWAITING_ORG_REVIEW = "ACHA";   // Waiting for Chart/Org approval
        public static final String AWAITING_TAX_REVIEW = "ATAX";   // Waiting for Vendor Tax approval

        //keep these in the order of potential routing
        //Note it doesn't make much sense to compare auto_approved and dept_approved but this is 
        //easier than two enums plus this should primarily be used for user enterred areas
        public enum STATUS_ORDER{
            INITIATE (PurapConstants.PaymentRequestStatuses.INITIATE,true),
            IN_PROCESS (PurapConstants.PaymentRequestStatuses.IN_PROCESS,true),
            CANCELLED_IN_PROCESS (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS,false),
            CANCELLED_PRIOR_TO_AP_APPROVAL (PurapConstants.PaymentRequestStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL,false),
            CANCELLED_POST_AP_APPROVE (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE,false),
            AWAITING_ACCOUNTS_PAYABLE_REVIEW (PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW,false),
            AWAITING_SUB_ACCT_MGR_REVIEW (PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW,false),
            AWAITING_FISCAL_REVIEW (PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_REVIEW,false),
            AWAITING_ORG_REVIEW (PurapConstants.PaymentRequestStatuses.AWAITING_ORG_REVIEW,false),
            AWAITING_TAX_REVIEW (PurapConstants.PaymentRequestStatuses.AWAITING_TAX_REVIEW,false),
            DEPARTMENT_APPROVED (PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED,false),
            AUTO_APPROVED (PurapConstants.PaymentRequestStatuses.AUTO_APPROVED,false),
            ;
            
            private String statusCode = new String();
            private boolean fullEntryAllowed = false;
            
            STATUS_ORDER(String statusCode,boolean fullEntry) {
                this.statusCode = statusCode;
                this.fullEntryAllowed = fullEntry;
        }

            public static STATUS_ORDER getByStatusCode(String statusCode)
            {
                for (STATUS_ORDER status : STATUS_ORDER.values()) {
                    if(StringUtils.equals(status.statusCode,statusCode)) {
                        return status;
                    }
                }
                return null;
            }
            public static boolean isFullDocumentEntryCompleted(String status) {
                return !getByStatusCode(status).fullEntryAllowed;
            }
        }

        
        public static final String[] PREQ_STATUSES_FOR_AUTO_APPROVE = {
            AWAITING_SUB_ACCT_MGR_REVIEW,
            AWAITING_FISCAL_REVIEW,
            AWAITING_ORG_REVIEW
            };

        public static final String[] STATUSES_ALLOWED_FOR_EXTRACTION = {
            AUTO_APPROVED,
            DEPARTMENT_APPROVED
            };

        public static final Set CANCELLED_STATUSES = new HashSet();
        public static final Set STATUSES_DISALLOWING_HOLD = new HashSet();
        public static final Set STATUSES_DISALLOWING_REQUEST_CANCEL = new HashSet();
        static {
            CANCELLED_STATUSES.add(CANCELLED_IN_PROCESS);
            CANCELLED_STATUSES.add(CANCELLED_PRIOR_TO_AP_APPROVAL);
            CANCELLED_STATUSES.add(CANCELLED_POST_AP_APPROVE);
            
            STATUSES_DISALLOWING_HOLD.add(INITIATE);
            STATUSES_DISALLOWING_HOLD.add(IN_PROCESS);
            STATUSES_DISALLOWING_HOLD.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));

            STATUSES_DISALLOWING_REQUEST_CANCEL.add(INITIATE);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(IN_PROCESS);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(DEPARTMENT_APPROVED);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(AUTO_APPROVED);
            STATUSES_DISALLOWING_REQUEST_CANCEL.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));
        }
        /*
         * Modify as required: public static final String CANCELLED = "CANC"; public static final String CLOSED = "CLOS"; public static final String
         * AWAIT_CONTENT_APRVL = "ACNT"; public static final String AWAIT_SUB_ACCT_APRVL = "ASUB"; public static final String AWAIT_FISCAL_APRVL =
         * "AFIS"; public static final String AWAIT_CHART_APRVL = "ACHA"; public static final String AWAIT_SEP_OF_DUTY_APRVL = "ASOD"; public
         * static String DAPRVD_CONTENT = "DCNT"; public static final String DAPRVD_SUB_ACCT = "DSUB"; public static final String DAPRVD_FISCAL =
         * "DFIS"; public static final String DAPRVD_CHART = "DCHA"; public static final String DAPRVD_SEP_OF_DUTY = "DSOD"; public static
         * String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR"; public static final String CONTRACT_MANAGER_ASSGN = "CMRA";
         */
        /*
         * // PAYMENT REQUEST STATUSES public static final String PREQ_STAT_IN_PROCESS = "INPR"; // In process (not routed yet) public
         * static String PREQ_STAT_CANCELLED_POST_APPROVE = "CANC"; public static final String PREQ_STAT_CANCELLED_IN_PROCESS = "VOID";
         * public static final String PREQ_STAT_AP_APPROVED = "APAD"; public static final String PREQ_STAT_AUTO_APPROVED = "AUTO"; public static
         * String PREQ_STAT_DEPARTMENT_APPROVED = "DPTA"; public static final String PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL = "ASAA"; //
         * Waiting for Sub Acct Manager approval public static final String PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL = "AFOA"; // Waiting for
         * Fiscal Officer approval public static final String PREQ_STAT_AWAIT_CHART_APRVL = "ACHA"; // Waiting for Chart/Org approval
         * public static final String PREQ_STAT_AWAIT_TAX_APRVL = "ATAX"; // Waiting for Tax approval public static final String
         * PREQ_STAT_PENDING_E_INVOICE = "PEIN"; // PAYMENT REQUEST STATUSES TO BE AUTO APPROVED public static final String[]
         * PREQ_STATUSES_FOR_AUTO_APPROVE =
         * {PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL,PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL,PREQ_STAT_AWAIT_CHART_APRVL}; // PAYMENT REQUEST
         * PAY DATE CALCULATION DAYS public static int PREQ_PAY_DATE_CALCULATION_DAYS = 28; // PREQ CANCEL FORWARDS public static
         * String PREQ_CANCEL_FORWARD_DOC_HANDLER = "dochandler"; public static final String PREQ_CANCEL_FORWARD_TAB_PAGE = "editpreq";
         */
    }

    public static class PREQDocumentsStrings {
        public static final String DUPLICATE_INVOICE_QUESTION = "PREQDuplicateInvoice";
        public static final String HOLD_PREQ_QUESTION = "HoldPREQ";
        public static final String CONFIRM_HOLD_QUESTION = "ConfirmHold";
        public static final String HOLD_NOTE_PREFIX = "Note entered while placing Payment Request on hold : ";
        public static final String REMOVE_HOLD_PREQ_QUESTION = "RemoveHoldPREQ";
        public static final String CONFIRM_REMOVE_HOLD_QUESTION = "ConfirmRemoveHold";
        public static final String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing a hold on Payment Request : ";
        public static final String CANCEL_PREQ_QUESTION = "CancelPREQ";
        public static final String CONFIRM_CANCEL_QUESTION = "ConfirmCancel";
        public static final String CANCEL_NOTE_PREFIX = "Note entered while requesting cancel on Payment Request : ";
        public static final String REMOVE_CANCEL_PREQ_QUESTION = "RemoveCancelPREQ";
        public static final String CONFIRM_REMOVE_CANCEL_QUESTION = "ConfirmRemoveCancel";
        public static final String REMOVE_CANCEL_NOTE_PREFIX = "Note entered while removing a request cancel on Payment Request : ";
        public static final String PURCHASE_ORDER_ID = "Purchase Order Identifier";
        public static final String INVOICE_DATE = "Invoice Date";
        public static final String INVOICE_NUMBER = "Invoice Number";
        public static final String IN_PROCESS = "In Process";
        public static final String THRESHOLD_DAYS_OVERRIDE_QUESTION = "Threshold Days Override Question";
        public static final String VENDOR_INVOICE_AMOUNT = "Vendor Invoice Amount";
        public static final String VENDOR_STATE = "State";
        public static final String VENDOR_POSTAL_CODE = "Postal Code";
    }

    public static final String BELOW_THE_LINES_PARAMETER = "BELOW_THE_LINE_ITEMS";
    public static final String ITEM_ALLOWS_ZERO = "ALLOWS_ZERO";
    public static final String ITEM_ALLOWS_POSITIVE = "ALLOWS_POSITIVE";
    public static final String ITEM_ALLOWS_NEGATIVE = "ALLOWS_NEGATIVE";
    public static final String ITEM_REQUIRES_USER_ENTERED_DESCRIPTION = "REQUIRES_USER_ENTERED_DESCRIPTION";

    public static class ItemFields {
        // TODO - can we not use the DataDictionaryService to get the labels (either standard, short, or error) instead of using these below
        public static final String QUANTITY = "Quantity";
        public static final String UNIT_OF_MEASURE = "Unit of Measure";
        public static final String DESCRIPTION = "Description";
        public static final String UNIT_COST = "Unit Cost";
        public static final String INVOICE_QUANTITY = "Qty Invoiced";
        public static final String OPEN_QUANTITY = "Open Qty";
        public static final String INVOICE_EXTENDED_PRICE = "Total Inv Cost";
    }
    
    // CREDIT MEMO DOCUMENT
    public static final String CREDIT_MEMO_ACTION_NAME = "CreditMemo";
    public static final String CREDIT_MEMO_DOCUMENT_DOC_TYPE = "CreditMemoDocument";

    public static class CreditMemoStatuses {
        // TODO delyea - check statuses for use/MDS entry
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED_IN_PROCESS = "CIPR";
        public static final String CANCELLED_PRIOR_TO_AP_APPROVAL = "VOID";
        public static final String CANCELLED_POST_AP_APPROVE = "CANC";
        public static final String COMPLETE = "CMPT";
        public static final String AWAITING_ACCOUNTS_PAYABLE_REVIEW = "APAD";   // Waiting for Accounts Payable approval
        public static final String AWAITING_FISCAL_REVIEW = "AFOA";   // Waiting for Fiscal Officer approval
        //TODO: Chris - these methods are the same as in PaymentRequestStatus.STATUS_ORDER combine
        public enum STATUS_ORDER{
            INITIATE (PurapConstants.CreditMemoStatuses.INITIATE,true),
            IN_PROCESS (PurapConstants.CreditMemoStatuses.IN_PROCESS,true),
            CANCELLED_IN_PROCESS (PurapConstants.CreditMemoStatuses.CANCELLED_IN_PROCESS,false),
            CANCELLED_PRIOR_TO_AP_APPROVAL (PurapConstants.CreditMemoStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL,false),
            CANCELLED_POST_AP_APPROVE (PurapConstants.CreditMemoStatuses.CANCELLED_POST_AP_APPROVE,false),
            AWAITING_ACCOUNTS_PAYABLE_REVIEW (PurapConstants.CreditMemoStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW,false),
            AWAITING_FISCAL_REVIEW (PurapConstants.CreditMemoStatuses.AWAITING_FISCAL_REVIEW,false),
            COMPLETE (PurapConstants.CreditMemoStatuses.COMPLETE,false),
            ;
        
            private String statusCode = new String();
            private boolean fullEntryAllowed = false;
            
            STATUS_ORDER(String statusCode,boolean fullEntry) {
                this.statusCode = statusCode;
                this.fullEntryAllowed = fullEntry;
            }
            
            public static STATUS_ORDER getByStatusCode(String statusCode)
            {
                for (STATUS_ORDER status : STATUS_ORDER.values()) {
                    if(StringUtils.equals(status.statusCode,statusCode)) {
                        return status;
                    }
                }
                return null;
            }
            public static boolean isFullDocumentEntryCompleted(String status) {
                return !getByStatusCode(status).fullEntryAllowed;
            }
        }
        
        public static final String[] STATUSES_ALLOWED_FOR_EXTRACTION = {
            COMPLETE
            };

        public static final Set CANCELLED_STATUSES = new HashSet();
        public static final Set STATUSES_DISALLOWING_HOLD = new HashSet();
        public static final Set STATUSES_NOT_REQUIRING_ENTRY_REVERSAL = new HashSet();
        static {
            CANCELLED_STATUSES.add(CANCELLED_IN_PROCESS);
            CANCELLED_STATUSES.add(CANCELLED_PRIOR_TO_AP_APPROVAL);
            CANCELLED_STATUSES.add(CANCELLED_POST_AP_APPROVE);

            STATUSES_DISALLOWING_HOLD.add(INITIATE);
            STATUSES_DISALLOWING_HOLD.add(IN_PROCESS);
            STATUSES_DISALLOWING_HOLD.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));
            
            STATUSES_NOT_REQUIRING_ENTRY_REVERSAL.add(INITIATE);
            STATUSES_NOT_REQUIRING_ENTRY_REVERSAL.add(IN_PROCESS);
            STATUSES_NOT_REQUIRING_ENTRY_REVERSAL.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));
        }
    }
    
    public static class CMDocumentsStrings {
        public static final String DUPLICATE_CREDIT_MEMO_QUESTION = "CMDuplicateInvoice";
        public static final String HOLD_CM_QUESTION = "HoldCM";
        public static final String HOLD_NOTE_PREFIX = "Note entered while placing Credit Memo on hold: ";
        public static final String CANCEL_CM_QUESTION = "CancelCM";
        public static final String REMOVE_HOLD_CM_QUESTION = "RemoveCM";
        public static final String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing hold on Credit Memo: ";
    }
    
    public static final class CREDIT_MEMO_TYPE_LABELS {
        public static final String TYPE_PO = "PO";
        public static final String TYPE_PREQ = "PREQ";
        public static final String TYPE_VENDOR = "Vendor";
        
    }
    
    private static final HashMap<String,Class> uncopyableFields() {
        HashMap<String,Class> fields = new HashMap<String,Class>();
        fields.put(KFSConstants.VERSION_NUMBER, null);
        fields.put("LOG", null);
        // TODO delyea/cck - add this back into the list?  Needs testing in PO Change Doc creation
//        fields.put("serialVersionUID", null);
        return fields;
    }
    /*
     * Fields that shouldn't be copied by our reflective copy method.
     * This should only contain fields that are known throughout objects not
     * item/doc specific ones
     */
    public static final HashMap<String,Class> KNOWN_UNCOPYABLE_FIELDS = uncopyableFields();
    
    private static final HashMap<String,Class> uncopyableItemFields() {
        HashMap<String,Class> fields = new HashMap<String,Class>();
        fields.put(PurapPropertyConstants.ITEM_IDENTIFIER, null);
        fields.put(PurapPropertyConstants.ACCOUNTS, null);
        return fields;
    }
    /*
     * Fields that shouldn't be copied by our reflective copy method.
     * This should only contain fields that are known throughout objects not
     * item/doc specific ones
     */
    public static final HashMap<String,Class> ITEM_UNCOPYABLE_FIELDS = uncopyableItemFields();

    private static final HashMap<String,Class> uncopyablePREQItemFields() {
        HashMap<String,Class> fields = new HashMap<String,Class>(ITEM_UNCOPYABLE_FIELDS);
        fields.put(PurapPropertyConstants.QUANTITY, null);
        fields.put(PurapPropertyConstants.EXTENDED_PRICE,null);
        return fields;
    }
    /*
     * fields that shouldn't be copied on PREQ item
     */
    public static final HashMap<String,Class> PREQ_ITEM_UNCOPYABLE_FIELDS = uncopyablePREQItemFields();
    
    private static final Map<String,Class> uncopyableFieldsForPurchaseOrder() {
        Map<String,Class> returnMap = new HashMap<String, Class>();
        returnMap.put(RicePropertyConstants.DOCUMENT_NUMBER, null);
        returnMap.put(PurapPropertyConstants.ITEM_IDENTIFIER, PurchaseOrderItem.class);
        returnMap.put(PurapPropertyConstants.ACCOUNT_IDENTIFIER, PurchaseOrderAccount.class);
        returnMap.put(PurapPropertyConstants.STATUS_HISTORY_IDENTIFIER, PurchaseOrderStatusHistory.class);
        returnMap.put(PurapPropertyConstants.PURCHASE_ORDER_VENDOR_QUOTE_IDENTIFIER, PurchaseOrderVendorQuote.class);
        returnMap.put("relatedRequisitionViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedPurchaseOrderViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedPaymentRequestViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedCreditMemoViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("paymentHistoryPaymentRequestViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("paymentHistoryCreditMemoViews", PurchasingAccountsPayableDocumentBase.class);
        return returnMap;
    }
    public static final Map<String,Class> UNCOPYABLE_FIELDS_FOR_PO = uncopyableFieldsForPurchaseOrder();
    
    public static final String PURAP_ORIGIN_CODE = "EP";

    public static final class PurapDocTypeCodes {
        public static final String PAYMENT_REQUEST_DOCUMENT = "PREQ";
        public static final String CREDIT_MEMO_DOCUMENT = "CM";
        public static final String PO_DOCUMENT = "PO";
        public static final String PO_AMENDMENT_DOCUMENT = "POA";
        public static final String PO_CLOSE_DOCUMENT = "POC";
        public static final String PO_REOPEN_DOCUMENT = "POR";
        public static final String PO_VOID_DOCUMENT = "POV";
    }    
    
    public static final Integer PRORATION_SCALE = new Integer(6);
    
    //variables moved down so doc types are defined before this call, KULPURAP-1185
    private static final HashMap<String, String> itemTypes()
    {
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put("RequisitionDocument", "Kuali.PURAP.RequisitionDocument");
        map.put("PurchaseOrderDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderCloseDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderReopenDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderAmendmentDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderPaymentHoldDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderRemoveHoldDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderRetransmitDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put("PurchaseOrderVoidDocument", "Kuali.PURAP.PurchaseOrderDocument");
        map.put(PAYMENT_REQUEST_DOCUMENT_DOC_TYPE, "Kuali.PURAP.PaymentRequestDocument");
        map.put(CREDIT_MEMO_DOCUMENT_DOC_TYPE, "Kuali.PURAP.CreditMemoDocument");
        return map;
}	    
	public static final HashMap<String,String> ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP =
                        itemTypes();

}