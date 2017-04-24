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
package edu.arizona.kfs.module.purap;


/**
 * Defines constants used in authorization-related code.
 */
public class PurapAuthorizationConstants extends org.kuali.kfs.module.purap.PurapAuthorizationConstants {

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
        public static String EDIT_VENDOR_ADDR_EDIT_MODE = "editVendorAddress";
    }


}
