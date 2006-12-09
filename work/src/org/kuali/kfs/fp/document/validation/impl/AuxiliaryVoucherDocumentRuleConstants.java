/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rules;


/**
 * Holds constants for <code>{@link org.kuali.module.financial.document.AuxiliaryVoucherDocument}</code> business rules.
 * 
 * 
 */
public interface AuxiliaryVoucherDocumentRuleConstants extends TransactionalDocumentRuleBaseConstants {
    // Security grouping constants used to do application parameter lookups
    public static final String AUXILIARY_VOUCHER_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.AuxiliaryVoucherDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String RESTRICTED_COMBINED_CODES = "RestrictedCombinationOfCodes";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "RestrictedObjectSubTypeCodes";
    public static final String RESTRICTED_EXPENSE_OBJECT_TYPE_CODES = "RestrictedExpenseObjectTypeCodes";
    public static final String RESTRICTED_INCOME_OBJECT_TYPE_CODES = "RestrictedIncomeObjectTypeCodes";
    public static final String RESTRICTED_PERIOD_CODES = "RestrictedPeriodCodes";
    public static final String GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE = "GeneralLedgerPendingEntryOffsetObjectCode";
}
