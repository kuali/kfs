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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.JstlConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Holds constants for PURAP.
 */
public class PurapConstants extends JstlConstants {

    // special user used in the special ap cancel action
    public static final String SYSTEM_AP_USER = KFSConstants.SYSTEM_USER;

    public static final KualiDecimal HUNDRED = new KualiDecimal(100);

    public static final String B2_B_ALLOW_COPY_DAYS = "B2_B_ALLOW_COPY_DAYS";

    public static final String DEFAULT_FUNDING_SOURCE = "DEFAULT_FUNDING_SOURCE";

    // STANDARD PARAMETER PREFIXES
    public static class QuoteTypes {
        public static final String COMPETITIVE = "COMP";
        public static final String PRICE_CONFIRMATION = "CONF";
    }

    public static class QuoteTransmitTypes {
        public static final String PRINT = "PRINT";
        public static final String FAX = "FAX";
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

    public static final String QUESTION_INDEX = "questionIndex";
    public static final String REMOVE_ACCOUNTS_QUESTION = "RemoveAccounts";
    public static final String CLEAR_COMMODITY_CODES_QUESTION = "ClearCommodityCodes";
    public static final String QUESTION_ROUTE_DOCUMENT_TO_COMPLETE = "Completing this document will remove it from your Action List.<br/><br/>  Are you sure you want to continue?";
    public static final String QUESTION_REMOVE_ACCOUNTS = "question.document.purap.removeAccounts";
    public static final String QUESTION_CLEAR_ALL_COMMODITY_CODES = "question.document.pur.clearCommodityCodes";
    public static final String CONFIRM_CHANGE_DFLT_RVNG_ADDR = "confirm.change.dflt.rcvng.addr";
    public static final String CONFIRM_CHANGE_DFLT_RVNG_ADDR_TXT = "Setting this receiving address to be default will unset the current default address. Do you want to proceed?";
    
    public static final String REQ_REASON_NOT_APO = "Requisition did not become an APO because: ";
    public static final String REQ_UNABLE_TO_CREATE_NOTE = "Unable to create a note on this document.";

    // Delivery Tag
    public static final String DELIVERY_BUILDING_OTHER = "Other";
    public static final String DELIVERY_BUILDING_OTHER_CODE = "OTH";

    // PDF KFSConstants
    public static final String IMAGE_TEMP_PATH = "PDF_IMAGE_TEMP_PATH";
    public static final String PDF_DIRECTORY = "PDF_DIRECTORY";
    public static final String STATUS_INQUIRY_URL = "PDF_STATUS_INQUIRY_URL";
    public static final String PURCHASING_DIRECTOR_IMAGE_PREFIX = "PDF_DIRECTOR_IMAGE_PREFIX";
    public static final String PURCHASING_DIRECTOR_IMAGE_EXTENSION = "PDF_DIRECTOR_IMAGE_EXTENSION";
    public static final String CONTRACT_MANAGER_IMAGE_PREFIX = "PDF_IMAGE_PREFIX";
    public static final String CONTRACT_MANAGER_IMAGE_EXTENSION = "PDF_IMAGE_EXTENSION";
    public static final String LOGO_IMAGE_PREFIX = "PDF_LOGO_IMAGE_PREFIX";
    public static final String LOGO_IMAGE_EXTENSION = "PDF_LOGO_IMAGE_EXTENSION";
    public static final String PDF_IMAGES_AVAILABLE_INDICATOR = "PDF_IMAGES_AVAILABLE_IND";

    public static class RequisitionStatuses {
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED = "CANC";
        public static final String CLOSED = "CLOS";
        public static final String AWAIT_CONTENT_REVIEW = "ACNT";
        public static final String AWAIT_SUB_ACCT_REVIEW = "ASUB";
        public static final String AWAIT_FISCAL_REVIEW = "AFIS";
        public static final String AWAIT_CHART_REVIEW = "ACHA";
        public static final String AWAIT_COMMODITY_CODE_REVIEW = "WCOM";
        public static final String AWAIT_SEP_OF_DUTY_REVIEW = "ASOD";
        public static final String DAPRVD_CONTENT = "DCNT";
        public static final String DAPRVD_SUB_ACCT = "DSUB";
        public static final String DAPRVD_FISCAL = "DFIS";
        public static final String DAPRVD_CHART = "DCHA";
        public static final String DAPRVD_COMMODITY_CODE = "DCOM";
        public static final String DAPRVD_SEP_OF_DUTY = "DSOD";
        public static final String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR";
        
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
    public static final String VENDOR_ERRORS = "document.vendor*,document.purchaseOrderVendorChoiceCode, document.alternateVendorName";
    public static final String ADDITIONAL_TAB_ERRORS = "document.requestor*,document.purchaseOrderTransmissionMethodCode,document.purchaseOrderCostSourceCode,document.purchaseOrderTotalLimit";
    public static final String ITEM_TAB_ERRORS = "document.item*,accountDistribution*,newPurchasingItemLine*,itemQuantity";
    public static final String ITEM_TAB_ERROR_PROPERTY = ITEM_TAB_ERRORS; // used to be "newPurchasingItemLine"
    public static final String ACCOUNT_SUMMARY_TAB_ERRORS = "document.accountSummary*";
    public static final String ACCOUNT_DISTRIBUTION_ERROR_KEY = "accountDistribution";
    public static final String RELATED_DOCS_TAB_ERRORS = "";
    public static final String PAYMENT_HISTORY_TAB_ERRORS = "";
    public static final String PAYMENT_INFO_ERRORS = "document.paymentInfo";
    public static final String PAYMENT_INFO_TAB_ERRORS = "document.paymentInfo*,document.purchaseOrderBeginDate,document.purchaseOrderEndDate";
    public static final String CAPITAL_ASSET_TAB_ERRORS = "document.capitalAsset";
    public static final String SPLIT_PURCHASE_ORDER_TAB_ERRORS = "document.splitPurchaseOrder";

    // PO/Quotes Tab Constants
    public static final String QUOTE_TAB_ERRORS = "document.quote*,quote*,purchaseOrderVendorQuotes*";

    // Assign Contract Manager
    public static final String ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC = "Contract Manager Assigned";
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "document.unassignedRequisition*";

    public static class PurchaseOrderStatuses {
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
        public static final String AWAIT_COMMODITY_CODE_REVIEW = "WCOM";
        public static final String DAPRVD_TAX = "DTAX";
        public static final String DAPRVD_BUDGET = "DBUD";
        public static final String DAPRVD_CONTRACTS_GRANTS = "DCG";
        public static final String DAPRVD_PURCHASING = "DPUR";
        public static final String DAPRVD_COMMODITY_CODE = "DCOM";
        public static final String CXML_ERROR = "CXER";
        public static final String PENDING_CXML = "CXPE";
        public static final String PENDING_FAX = "FXPE";
        public static final String PENDING_PRINT = "PRPE";
        public static final String QUOTE = "QUOT";
        public static final String VOID = "VOID";
        public static final String AMENDMENT = "AMND";

        public static final String RETIRED_VERSION = "RTVN";
        public static final String CHANGE_IN_PROCESS = "CGIN";
        public static final String CANCELLED_CHANGE = "CNCG";
        public static final String DISAPPROVED_CHANGE = "DACG";
        public static final String PENDING_VOID = "VOPE";
        public static final String PENDING_PAYMENT_HOLD = "PHPE";
        public static final String PENDING_RETRANSMIT = "RTPE";
        public static final String PENDING_CLOSE = "CLPE";
        public static final String PENDING_REOPEN = "ROPE";
        public static final String PENDING_REMOVE_HOLD = "RHPE";

        public static final Set<String> INCOMPLETE_STATUSES = new HashSet<String>();
        static {
            INCOMPLETE_STATUSES.add(AWAIT_TAX_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_BUDGET_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_CONTRACTS_GRANTS_REVIEW);
            INCOMPLETE_STATUSES.add(AWAIT_PURCHASING_REVIEW);
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

        /**
         * Do not include 'OPEN' status in this map. The 'OPEN' status is the default status that is set when no status exists for a
         * particular pending transmission type code.
         * 
         * @see {@link org.kuali.module.purap.service.PurchaseOrderService#completePurchaseOrder(org.kuali.module.purap.document.PurchaseOrderDocument)}
         */
        private static final Map<String, String> getStatusesByTransmissionType() {
            Map<String, String> statusByTrans = new HashMap<String, String>();
            statusByTrans.put(PurapConstants.POTransmissionMethods.PRINT, PENDING_PRINT);
            statusByTrans.put(PurapConstants.POTransmissionMethods.ELECTRONIC, PENDING_CXML);
            statusByTrans.put(PurapConstants.POTransmissionMethods.FAX, PENDING_FAX);
            return Collections.unmodifiableMap(statusByTrans);
        }

        public static final Map<String, String> STATUSES_BY_TRANSMISSION_TYPE = getStatusesByTransmissionType();

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
        public static final String ITEM_TYPE_UNORDERED_ITEM_CODE = "UNOR";

        public static final Set<String> EXCLUDED_ITEM_TYPES = new HashSet<String>();
        static {
            EXCLUDED_ITEM_TYPES.add(ITEM_TYPE_UNORDERED_ITEM_CODE);
        }
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
        public static final String PURCHASE_ORDER_SPLIT_DOCUMENT = "PurchaseOrderSplitDocument";
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
        
        public static final String SPLIT_QUESTION = "POSplit";
        public static final String SPLIT_CONFIRM = "POSplitConfirm";
        public static final String SPLIT_NOTE_PREFIX_OLD_DOC = "Note entered while splitting this Purchase Order : ";
        public static final String SPLIT_NOTE_PREFIX_NEW_DOC = "Note entered while being split from Purchase Order ";
        public static final String SPLIT_ADDL_CHARGES_WARNING_LABEL = "WARNING";
        public static final String SPLIT_ADDL_CHARGES_WARNING = "* ADDITIONAL CHARGES EXIST *";

        public static final String CONFIRM_AWARD_QUESTION = "POConfirmAward";
        public static final String CONFIRM_AWARD_RETURN = "completeQuote";

        public static final String CONFIRM_CANCEL_QUESTION = "POCancelQuote";
        public static final String CONFIRM_CANCEL_RETURN = "cancelQuote";

        public static final String SINGLE_CONFIRMATION_QUESTION = "singleConfirmationQuestion";

        public static final String MANUAL_STATUS_CHANGE_QUESTION = "manualStatusChangeQuestion";
        public static final String OPEN_STATUS = "Open";
        
        public static final String POSTAL_CODE = "Postal Code";
        public static final String ALTERNATE_PAYEE_VENDOR = "Alternate Payee Vendor";
    }

    public static final String PO_OVERRIDE_NOT_TO_EXCEED_QUESTION = "OverrideNotToExceed";
    public static final String FIX_CAPITAL_ASSET_WARNINGS = "FixCapitalAssetWarnings";

    // ACCOUNTS PAYABLE
    public static final String AP_OVERRIDE_INVOICE_NOMATCH_QUESTION = "OverrideInvoiceNoMatch";

    public static class AccountsPayableDocumentStrings {
        public static final String CANCEL_NOTE_PREFIX = "Note entered while canceling document: ";
    }

    // PAYMENT REQUEST
    public static final String PAYMENT_REQUEST_ACTION_NAME = "PaymentRequest";
    public static final String PREQ_PAY_DATE_DAYS = "days";
    public static final String PREQ_PAY_DATE_DATE = "date";
    public static final int PREQ_PAY_DATE_EMPTY_TERMS_DEFAULT_DAYS = 28;
    public static final int PREQ_PAY_DATE_DAYS_BEFORE_WARNING = 60;

    //TAB ERROR KEYS
    public static final String PAYMENT_REQUEST_INIT_TAB_ERRORS = "document.purchaseOrderIdentifier,document.invoiceNumber,document.invoiceDate,document.vendorInvoiceAmount,document.specialHandlingInstructionLine1Text,document.specialHandlingInstructionLine2Text,document.specialHandlingInstructionLine3Text";
    public static final String RECEIVING_LINE_INIT_TAB_ERRORS = "document.purchaseOrderIdentifier,document.shipmentReceivedDate,document.shipmentPackingSlipNumber,document.shipmentBillOfLadingNumber,document.carrierCode";
    
    // Weird PaymentTermsType is due on either the 10th or 25th with no discount
    public static final String PMT_TERMS_TYP_NO_DISCOUNT_CD = "00N2T";

    public static final String PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS = "SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS_IND";
    public static final String PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS = "SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS_IND";

    public static final class PaymentRequestIndicatorText {
        public static final String HOLD = "HOLD";
        public static final String REQUEST_CANCEL = "REQUEST CANCEL";
    }
    
    public static final class PaymentRequestStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED_IN_PROCESS = "CIPR";
        public static final String CANCELLED_PRIOR_TO_AP_APPROVAL = "VOID";
        public static final String CANCELLED_POST_AP_APPROVE = "CANC";
        public static final String DEPARTMENT_APPROVED = "DPTA";
        public static final String AUTO_APPROVED = "AUTO";
        public static final String AWAITING_ACCOUNTS_PAYABLE_REVIEW = "APAD"; // Waiting for Accounts Payable approval
        public static final String AWAITING_SUB_ACCT_MGR_REVIEW = "ASAA"; // Waiting for Sub Acct Manager approval
        public static final String AWAITING_FISCAL_REVIEW = "AFOA"; // Waiting for Fiscal Officer approval
        public static final String AWAITING_ORG_REVIEW = "ACHA"; // Waiting for Chart/Org approval
        public static final String AWAITING_TAX_REVIEW = "ATAX"; // Waiting for Vendor Tax approval

        // keep these in the order of potential routing
        // Note it doesn't make much sense to compare auto_approved and dept_approved but this is
        // easier than two enums plus this should primarily be used for user enterred areas
        public enum STATUS_ORDER {
            CANCELLED_IN_PROCESS(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS, false), CANCELLED_PRIOR_TO_AP_APPROVAL(PurapConstants.PaymentRequestStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL, false), CANCELLED_POST_AP_APPROVE(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, false), INITIATE(PurapConstants.PaymentRequestStatuses.INITIATE, true), IN_PROCESS(PurapConstants.PaymentRequestStatuses.IN_PROCESS, true), AWAITING_ACCOUNTS_PAYABLE_REVIEW(PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, false), AWAITING_SUB_ACCT_MGR_REVIEW(PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW, false), AWAITING_FISCAL_REVIEW(PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_REVIEW, false), AWAITING_ORG_REVIEW(PurapConstants.PaymentRequestStatuses.AWAITING_ORG_REVIEW, false), AWAITING_TAX_REVIEW(PurapConstants.PaymentRequestStatuses.AWAITING_TAX_REVIEW, false), DEPARTMENT_APPROVED(
                    PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED, false), AUTO_APPROVED(PurapConstants.PaymentRequestStatuses.AUTO_APPROVED, false), ;

            private String statusCode = new String();
            private boolean fullEntryAllowed = false;

            STATUS_ORDER(String statusCode, boolean fullEntry) {
                this.statusCode = statusCode;
                this.fullEntryAllowed = fullEntry;
            }

            public static STATUS_ORDER getByStatusCode(String statusCode) {
                for (STATUS_ORDER status : STATUS_ORDER.values()) {
                    if (StringUtils.equals(status.statusCode, statusCode)) {
                        return status;
                    }
                }
                return null;
            }

            public static boolean isFullDocumentEntryCompleted(String status) {
                return !getByStatusCode(status).fullEntryAllowed;
            }

            public static STATUS_ORDER getPreviousStatus(String statusCode) {
                STATUS_ORDER statusOrder = STATUS_ORDER.getByStatusCode(statusCode);
                if (statusOrder.ordinal() > 0) {
                    return STATUS_ORDER.values()[statusOrder.ordinal() - 1];
                }
                return null;
            }

            public static boolean isFirstFullEntryStatus(String statusCode) {
                // NOTE this won't work if there endsup being two ways to get to the first full entry status (i.e. like AUTO/DEPT
                // for final)
                return getByStatusCode(statusCode).fullEntryAllowed && !getPreviousStatus(statusCode).fullEntryAllowed;
            }
        }


        public static final String[] PREQ_STATUSES_FOR_AUTO_APPROVE = { AWAITING_SUB_ACCT_MGR_REVIEW, AWAITING_FISCAL_REVIEW, AWAITING_ORG_REVIEW };

        public static final String[] STATUSES_ALLOWED_FOR_EXTRACTION = { AUTO_APPROVED, DEPARTMENT_APPROVED };

        public static final String[] STATUSES_POTENTIALLY_ACTIVE = { IN_PROCESS, DEPARTMENT_APPROVED, AUTO_APPROVED, AWAITING_ACCOUNTS_PAYABLE_REVIEW, AWAITING_SUB_ACCT_MGR_REVIEW, AWAITING_FISCAL_REVIEW, AWAITING_ORG_REVIEW, AWAITING_TAX_REVIEW };
        
        public static final Set CANCELLED_STATUSES = new HashSet();
        public static final Set STATUSES_DISALLOWING_HOLD = new HashSet();
        public static final Set STATUSES_DISALLOWING_REMOVE_HOLD = new HashSet();
        public static final Set STATUSES_DISALLOWING_REQUEST_CANCEL = new HashSet();
        public static final Set STATUSES_DISALLOWING_REMOVE_REQUEST_CANCEL = new HashSet();
        static {
            CANCELLED_STATUSES.add(CANCELLED_IN_PROCESS);
            CANCELLED_STATUSES.add(CANCELLED_PRIOR_TO_AP_APPROVAL);
            CANCELLED_STATUSES.add(CANCELLED_POST_AP_APPROVE);

            STATUSES_DISALLOWING_HOLD.add(INITIATE);
            STATUSES_DISALLOWING_HOLD.add(IN_PROCESS);
            STATUSES_DISALLOWING_HOLD.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));

            STATUSES_DISALLOWING_REMOVE_HOLD.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));

            STATUSES_DISALLOWING_REQUEST_CANCEL.add(INITIATE);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(IN_PROCESS);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(DEPARTMENT_APPROVED);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(AUTO_APPROVED);
            STATUSES_DISALLOWING_REQUEST_CANCEL.add(AWAITING_ACCOUNTS_PAYABLE_REVIEW);
            STATUSES_DISALLOWING_REQUEST_CANCEL.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));

            STATUSES_DISALLOWING_REMOVE_REQUEST_CANCEL.addAll(Arrays.asList(CANCELLED_STATUSES.toArray(new String[CANCELLED_STATUSES.size()])));
        }
    }

    public static class ReceivingLineDocumentStrings{
        public static final String DUPLICATE_RECEIVING_LINE_QUESTION = "DuplicateReceivingLine";
        public static final String VENDOR_DATE = "Vendor Date";
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

    public static final String BELOW_THE_LINES_PARAMETER = "ADDITIONAL_CHARGES_ITEM_TYPES";
    public static final String ITEM_ALLOWS_ZERO = "ITEM_TYPES_ALLOWING_ZERO";
    public static final String ITEM_ALLOWS_POSITIVE = "ITEM_TYPES_ALLOWING_POSITIVE";
    public static final String ITEM_ALLOWS_NEGATIVE = "ITEM_TYPES_ALLOWING_NEGATIVE";
    public static final String ITEM_REQUIRES_USER_ENTERED_DESCRIPTION = "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION";

    public static class ItemFields {
        public static final String QUANTITY = "Quantity";
        public static final String UNIT_OF_MEASURE = "Unit of Measure";
        public static final String DESCRIPTION = "Description";
        public static final String UNIT_COST = "Unit Cost";
        public static final String INVOICE_QUANTITY = "Qty Invoiced";
        public static final String OPEN_QUANTITY = "Open Qty";
        public static final String INVOICE_EXTENDED_PRICE = "Total Inv Cost";
        public static final String COMMODITY_CODE = "Commodity Code";
    }

    // CREDIT MEMO DOCUMENT
    public static final String CREDIT_MEMO_ACTION_NAME = "CreditMemo";

    public static class CreditMemoStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
        public static final String CANCELLED_IN_PROCESS = "CIPR";
        public static final String CANCELLED_PRIOR_TO_AP_APPROVAL = "VOID";
        public static final String CANCELLED_POST_AP_APPROVE = "CANC";
        public static final String COMPLETE = "CMPT";
        public static final String AWAITING_ACCOUNTS_PAYABLE_REVIEW = "APAD"; // Waiting for Accounts Payable approval
        public static final String AWAITING_FISCAL_REVIEW = "AFOA"; // Waiting for Fiscal Officer approval

        public enum STATUS_ORDER {
            CANCELLED_IN_PROCESS(PurapConstants.CreditMemoStatuses.CANCELLED_IN_PROCESS, false), CANCELLED_PRIOR_TO_AP_APPROVAL(PurapConstants.CreditMemoStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL, false), CANCELLED_POST_AP_APPROVE(PurapConstants.CreditMemoStatuses.CANCELLED_POST_AP_APPROVE, false), INITIATE(PurapConstants.CreditMemoStatuses.INITIATE, true), IN_PROCESS(PurapConstants.CreditMemoStatuses.IN_PROCESS, true), AWAITING_ACCOUNTS_PAYABLE_REVIEW(PurapConstants.CreditMemoStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, false), AWAITING_FISCAL_REVIEW(PurapConstants.CreditMemoStatuses.AWAITING_FISCAL_REVIEW, false), COMPLETE(PurapConstants.CreditMemoStatuses.COMPLETE, false), ;

            private String statusCode = new String();
            private boolean fullEntryAllowed = false;

            STATUS_ORDER(String statusCode, boolean fullEntry) {
                this.statusCode = statusCode;
                this.fullEntryAllowed = fullEntry;
            }

            public static STATUS_ORDER getByStatusCode(String statusCode) {
                for (STATUS_ORDER status : STATUS_ORDER.values()) {
                    if (StringUtils.equals(status.statusCode, statusCode)) {
                        return status;
                    }
                }
                return null;
            }

            public static boolean isFullDocumentEntryCompleted(String status) {
                return !getByStatusCode(status).fullEntryAllowed;
            }

            public static STATUS_ORDER getPreviousStatus(String statusCode) {
                STATUS_ORDER statusOrder = STATUS_ORDER.getByStatusCode(statusCode);
                if (statusOrder.ordinal() > 0) {
                    return STATUS_ORDER.values()[statusOrder.ordinal() - 1];
                }
                return null;
            }

            public static boolean isFirstFullEntryStatus(String statusCode) {
                // NOTE this won't work if there endsup being two ways to get to the first full entry status (i.e. like AUTO/DEPT
                // for final)
                return getByStatusCode(statusCode).fullEntryAllowed && !getPreviousStatus(statusCode).fullEntryAllowed;
            }
        }

        public static final String[] STATUSES_ALLOWED_FOR_EXTRACTION = { COMPLETE };

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

    private static final HashMap<String, Class> uncopyableFields() {
        HashMap<String, Class> fields = new HashMap<String, Class>();
        fields.put(KFSConstants.VERSION_NUMBER, null);
        fields.put("LOG", null);
        fields.put(KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRIES, null);
        return fields;
    }

    /*
     * Fields that shouldn't be copied by our reflective copy method. This should only contain fields that are known throughout
     * objects not item/doc specific ones
     */
    public static final HashMap<String, Class> KNOWN_UNCOPYABLE_FIELDS = uncopyableFields();

    private static final HashMap<String, Class> uncopyableItemFields() {
        HashMap<String, Class> fields = new HashMap<String, Class>();
        fields.put(PurapPropertyConstants.ITEM_IDENTIFIER, null);
        fields.put(PurapPropertyConstants.ACCOUNTS, null);
        return fields;
    }

    /*
     * Fields that shouldn't be copied by our reflective copy method. This should only contain fields that are known throughout
     * objects not item/doc specific ones
     */
    public static final HashMap<String, Class> ITEM_UNCOPYABLE_FIELDS = uncopyableItemFields();

    private static final HashMap<String, Class> uncopyablePREQItemFields() {
        HashMap<String, Class> fields = new HashMap<String, Class>(ITEM_UNCOPYABLE_FIELDS);
        fields.put(PurapPropertyConstants.QUANTITY, null);
        fields.put(PurapPropertyConstants.EXTENDED_PRICE, null);
        return fields;
    }

    /*
     * fields that shouldn't be copied on PREQ item
     */
    public static final HashMap<String, Class> PREQ_ITEM_UNCOPYABLE_FIELDS = uncopyablePREQItemFields();

    private static final Map<String, Class> uncopyableFieldsForPurchaseOrder() {
        Map<String, Class> returnMap = new HashMap<String, Class>();
        returnMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, null);
        returnMap.put(PurapPropertyConstants.ITEM_IDENTIFIER, PurchaseOrderItem.class);
        returnMap.put(PurapPropertyConstants.ACCOUNT_IDENTIFIER, PurchaseOrderAccount.class);
        returnMap.put(PurapPropertyConstants.PURCHASE_ORDER_VENDOR_QUOTE_IDENTIFIER, PurchaseOrderVendorQuote.class);
        returnMap.put("relatedRequisitionViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedPurchaseOrderViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedPaymentRequestViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("relatedCreditMemoViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("paymentHistoryPaymentRequestViews", PurchasingAccountsPayableDocumentBase.class);
        returnMap.put("paymentHistoryCreditMemoViews", PurchasingAccountsPayableDocumentBase.class);
        return returnMap;
    }

    public static final Map<String, Class> UNCOPYABLE_FIELDS_FOR_PO = uncopyableFieldsForPurchaseOrder();

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

    private static HashMap<String, String> getPurapParameterDetailTypeCodes() {
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put("RequisitionDocument", RequisitionDocument.class.getName());
        map.put("PurchaseOrderDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderCloseDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderReopenDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderAmendmentDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderPaymentHoldDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderRemoveHoldDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderRetransmitDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderVoidDocument", PurchaseOrderDocument.class.getName());
        map.put("PurchaseOrderSplitDocument", PurchaseOrderDocument.class.getName());
        map.put("PaymentRequestDocument", PaymentRequestDocument.class.getName());
        map.put("CreditMemoDocument", CreditMemoDocument.class.getName());
        return map;
    }

    public static final HashMap<String, String> PURAP_DETAIL_TYPE_CODE_MAP = getPurapParameterDetailTypeCodes();
    
    public static class CAMSValidationStrings {
        public static final String CAPITAL = "Capital";
        public static final String EXPENSE = "Expense";
        public static final String RECURRING = "Recurring";
        public static final String NON_RECURRING = "Non-recurring";
    }
    
    public static class CapitalAssetTabStrings {
        public static final String SYSTEM_DEFINITION = "Definition: A system is any group of line items added together to create one or more identical assets. Systems are further defined as line items that work together to perform one function. Each of the line items must be necessary for the system to function.";
        
        public static final String INDIVIDUAL_ASSETS = "IND";
        public static final String ONE_SYSTEM = "ONE";
        public static final String MULTIPLE_SYSTEMS = "MULT";
        public static final String INDIVIDUAL_ASSETS_DESC = "Each of the lines will be capitalized as INDIVIDUAL ASSETS.";
        public static final String ONE_SYSTEM_DESC = "Line items are being added together to create ONE SYSTEM.";
        public static final String MULTIPLE_SYSTEMS_DESC = "Any of the line items will be added together to create MULTIPLE SYSTEMS.";
        public static final String ASSET_DATA = "Asset data is on Item Tab.";
        
        public static final String QUESTION_SYSTEM_SWITCHING = "question.document.pur.systemTypeSwitching";
        public static final String SYSTEM_SWITCHING_QUESTION = "SystemSwitchingQuestion";
    }

}
