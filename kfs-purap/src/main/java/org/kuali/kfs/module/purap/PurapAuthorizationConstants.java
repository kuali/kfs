/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap;


/**
 * Defines constants used in authorization-related code.
 */
public class PurapAuthorizationConstants {

    public static class RequisitionEditMode {
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
        public static final String ENABLE_COMMODITY_CODE = "enableCommodityCode";
        public static final String DISABLE_REMOVE_ACCTS = "disableRemoveAccounts";
        public static final String DISABLE_SETUP_ACCT_DISTRIBUTION = "disableSetupAccountDistribution";
        public static final String ALLOW_CAPITAL_ASSET_EDITS = "allowCapitalAssetEdit";
    }

    public static class PurchaseOrderEditMode {
        public static final String ALLOW_POSTING_YEAR_ENTRY = "allowPostingYearEntry";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String LOCK_INTERNAL_PURCHASING_ENTRY = "lockInternalPurchasingEntry";
        public static final String DISPLAY_RETRANSMIT_TAB = "displayRetransmitTab";
        public static final String AMENDMENT_ENTRY = "amendmentEntry";
        public static final String PRE_ROUTE_CHANGEABLE = "preRoute";
        public static final String DISPLAY_RECEIVING_ADDRESS = "displayReceivingAddress";
        public static final String SPLITTING_ITEM_SELECTION = "splittingItemSelection";
        public static final String UNORDERED_ITEM_ACCOUNT_ENTRY = "unorderedItemAccountEntry";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String LOCK_B2B_ENTRY = "lockB2BEntry";
        public static final String PRINT_PURCHASE_ORDER = "printPurchaseOrder";
        public static final String PREVIEW_PRINT_PURCHASE_ORDER = "previewPrintPurchaseOrder";
        public static final String RESEND_PURCHASE_ORDER = "resendPurchaseOrder";
        public static final String ASSIGN_SENSITIVE_DATA = "assignSensitiveData";
        public static final String ENABLE_COMMODITY_CODE = "enableCommodityCode";
        public static final String DISABLE_REMOVE_ACCTS = "disableRemoveAccounts";
    }

    public static class PaymentRequestEditMode {
        public static final String PURAP_TAX_ENABLED = "purapTaxEnabled";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
        public static final String EDIT_PRE_EXTRACT = "editPreExtract";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String TAX_INFO_VIEWABLE = "taxInfoViewable";
        public static final String TAX_AREA_EDITABLE = "taxAreaEditable";
        public static final String RESTRICT_FISCAL_ENTRY = "restrictFiscalEntry";
        public static final String ALLOW_CLOSE_PURCHASE_ORDER = "allowClosePurchaseOrder";
        public static final String ACCOUNTS_PAYABLE_PROCESSOR_CANCEL = "processorCancel";
        public static final String ACCOUNTS_PAYABLE_MANAGER_CANCEL = "managerCancel";
        public static final String REQUEST_CANCEL = "requestPaymentRequestCancel";
        public static final String HOLD = "requestPaymentRequestHold";
        public static final String REMOVE_HOLD = "paymentRequestHoldCancelRemoval";
        public static final String REMOVE_REQUEST_CANCEL = "paymentRequestHoldCancelRemoval";
        public static final String FULL_DOCUMENT_ENTRY_COMPLETED = "fullDocumentEntryCompleted";
    }

    public static class CreditMemoEditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String EDIT_PRE_EXTRACT = "editPreExtract";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
        public static final String LOCK_TAX_AMOUNT_ENTRY = "lockTaxAmountEntry";
        public static final String CLEAR_ALL_TAXES = "clearAllTaxes";
        public static final String ALLOW_REOPEN_PURCHASE_ORDER = "allowReopenPurchaseOrder";
        public static final String HOLD = "requestVendorCreditMemoHold";
        public static final String REMOVE_HOLD = "vendorCreditMemoHoldRemoval";
        public static final String ACCOUNTS_PAYABLE_PROCESSOR_CANCEL = "processorCancel";
        public static final String FULL_DOCUMENT_ENTRY_COMPLETED = "fullDocumentEntryCompleted";
    }

    public static class LineItemReceivingEditMode {
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
    }

    public static class CorrectionReceivingEditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
    }

    public static class BulkReceivingEditMode {
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String LOCK_PO_DETAILS = "lockPODetails";
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
    }

    public static final String PURAP_TAX_ENABLED = "purapTaxEnabled";


}
