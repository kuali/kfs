/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.rice.kns.authorization.AuthorizationConstants;

/**
 * Defines constants used in authorization-related code.
 */
public class PurapAuthorizationConstants extends AuthorizationConstants {

    public static class RequisitionEditMode extends EditMode {
        public static final String ALLOW_POSTING_YEAR_ENTRY = "allowPostingYearEntry";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String LOCK_CONTENT_ENTRY = "lockContentEntry";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
        public static final String ALLOW_ITEM_ENTRY = "allowItemEntry";
        public static final String DISPLAY_RECEIVING_ADDRESS = "displayReceivingAddress";
        public static final String LOCK_ADDRESS_TO_VENDOR = "lockAddressToVendor";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";   
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String LOCK_B2B_ENTRY = "lockB2BEntry";
        public static final String RESTRICT_FISCAL_ENTRY = "restrictFiscalEntry";
    }

    public static class PurchaseOrderEditMode extends EditMode {
        public static final String ALLOW_POSTING_YEAR_ENTRY = "allowPostingYearEntry";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String LOCK_INTERNAL_PURCHASING_ENTRY = "lockInternalPurchasingEntry";
        public static final String DISPLAY_RETRANSMIT_TAB = "displayRetransmitTab";
        public static final String AMENDMENT_ENTRY = "amendmentEntry";
        public static final String PRE_ROUTE_CHANGEABLE = "preRouteChangeable";
        public static final String DISPLAY_RECEIVING_ADDRESS = "displayReceivingAddress";
        public static final String SPLITTING_ITEM_SELECTION = "splittingItemSelection";
        public static final String UNORDERED_ITEM_ACCOUNT_ENTRY = "unorderedItemAccountEntry";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String LOCK_B2B_ENTRY = "lockB2BEntry";
    }

    public static class PaymentRequestEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
        public static final String SHOW_AMOUNT_ONLY = "showAmountOnly";
        public static final String EDIT_PRE_EXTRACT = "editPreExtract";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String TAX_INFO_VIEWABLE = "taxInfoViewable";
        public static final String TAX_AREA_EDITABLE = "taxAreaEditable";
        public static final String RESTRICT_FISCAL_ENTRY = "restrictFiscalEntry";
        public static final String ALLOW_CLOSE_PURCHASE_ORDER = "allowClosePurchaseOrder";
        public static final String ACCOUNTS_PAYABLE_ENTRY = "accountsPayableEntry";
        public static final String ACCOUNTS_PAYABLE_PROCESSOR_CANCEL = "processorCancel";
        public static final String ACCOUNTS_PAYABLE_MANAGER_CANCEL = "managerCancel";
    }

    public static class CreditMemoEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String EDIT_PRE_EXTRACT = "editPreExtract";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String ALLOW_REOPEN_PURCHASE_ORDER = "allowReopenPurchaseOrder";
    }

    public static class LineItemReceivingEditMode extends EditMode {
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
    }

    public static class CorrectionReceivingEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
    }
    
    public static class BulkReceivingEditMode extends EditMode {
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String LOCK_PO_DETAILS = "lockPODetails";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
    }
    
    public static final String PURAP_TAX_ENABLED = "purapTaxEnabled"; 
    
    public static class PermissionNames {
        //TODO check default template names
        //select * from KRIM_PERM_ATTR_V where PERM_NAMESPACE_CODE = 'KFS-PURAP';
        //select * from KRIM_ROLE_PERM_V where ROLE_NAMESPACE = 'KFS-PURAP';
        //public static final String PREVIEW_PRINT_PO = "Preview Print PO";
        //public static final String PRINT_FIRST_TRANSMIT_PO = "Print First Transmit PO";
        //public static final String PRINT_RETRANSMITTED_NON_APO = "Print Retransmitted Non APO";
        public static final String PRINT_PO = "Print Purchase Order";
        public static final String RESEND_PO = "Resend Purchase Order";
        public static final String ASSIGN_SENSITIVE_DATA = "Assign Sensitive Data";
        public static final String HOLD_PREQ = "Add Payment Request Hold";
        public static final String REMOVE_HOLD_PREQ = "Remove Payment Request Hold";
        public static final String REMOVE_CANCEL_PREQ = "Remove Payment Request Cancel";
    }
}
