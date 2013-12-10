/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys;


public class KfsAuthorizationConstants  {

    public static class TransactionalEditMode {
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
