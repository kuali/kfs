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
package org.kuali.kfs.sys;

public class KfsAuthorizationConstants  {

    public static class TransactionalEditMode{
        public static final String EXPENSE_ENTRY = "expenseEntry";
        public static final String IMMEDIATE_DISBURSEMENT_ENTRY = "immediateDisbursementEntryMode";
        public static final String FRN_ENTRY = "frnEntry";
        public static final String WIRE_ENTRY = "wireEntry";
    }

    public static class DisbursementVoucherEditMode {
        public static final String PAYEE_ENTRY = "payeeEntry";
        public static final String TAX_ENTRY = "taxEntry";
        public static final String TRAVEL_ENTRY = "travelEntry";
        public static final String FULL_ENTRY = "fullEntry";
        public static final String PAYMENT_HANDLING_ENTRY = "paymentHandlingEntry";
        public static final String VOUCHER_DEADLINE_ENTRY = "voucherDeadlineEntry";
        public static final String SPECIAL_HANDLING_CHANGING_ENTRY = "specialHandlingChangingEntry";
        public static final String PAYMENT_REASON_EDIT_MODE = "paymentReasonEditMode";
        public static final String EXTRACT_NOW = "extractNow";
    }

    public static class DistributionOfIncomeAndExpenseEditMode {
        public static final String SOURCE_LINE_READ_ONLY_MODE = "sourceLinesReadOnlyMode";
    }

    public static class CashReceiptEditMode {
        public static final String CASH_MANAGER_CONFIRM_MODE = "cmConfirm";
        public static final String CHANGE_REQUEST_MODE = "changeRequestOn";
    }

    public static class CashManagementEditMode {
        public static final String ALLOW_ADDITIONAL_DEPOSITS = "allowAdditionalDeposits";
        public static final String ALLOW_CANCEL_DEPOSITS = "allowCancelDeposits";
    }

    public static class BudgetAdjustmentEditMode {
        public static final String BASE_AMT_ENTRY = "baseAmtEntry";
    }

    public static class CustomerInvoiceEditMode extends TransactionalEditMode {
        public static final String PROCESSING_ORGANIZATION_MODE = "processingOrganizationMode";
    }
}
