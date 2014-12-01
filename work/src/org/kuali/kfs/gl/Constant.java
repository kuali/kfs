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
package org.kuali.kfs.gl;


/**
 * This class contains the constants being used by various general ledger components
 */
public final class Constant {
    public static final String EMPTY_STRING = "";

    public static final String EXCLUDE_CG_BEGINNING_BALANCE_ONLY_OPTION = "dummyBusinessObject.cgBeginningBalanceExcludeOption";

    public static final String PENDING_ENTRY_OPTION = "dummyBusinessObject.pendingEntryOption";
    public static final String APPROVED_PENDING_ENTRY = "Approved";
    public static final String ALL_PENDING_ENTRY = "All";
    public static final String NO_PENDING_ENTRY = "No";

    public static final String CONSOLIDATION_OPTION = "dummyBusinessObject.consolidationOption";
    public static final String CONSOLIDATION = "Consolidation";
    public static final String DETAIL = "Detail";
    public static final String EXCLUDE_SUBACCOUNTS = "Exclude Sub-Accounts";

    public static final String AMOUNT_VIEW_OPTION = "dummyBusinessObject.amountViewOption";
    public static final String MONTHLY = "Monthly";
    public static final String ACCUMULATE = "Accumulate";

    public static final String BLANK_LINE_OPTION = "dummyBusinessObject.blankLineOption";
    public static final String SHOW_BLANK_LINE = "Yes";
    public static final String NOT_SHOW_BLANK_LINE = "No";

    public static final String COST_SHARE_OPTION = "dummyBusinessObject.costShareOption";
    public static final String COST_SHARE_EXCLUDE = "Exclude";
    public static final String COST_SHARE_INCLUDE = "Include";

    public static final String DEBIT_CREDIT_OPTION = "dummyBusinessObject.debitCreditOption";
    public static final String DEBIT_CREDIT_INCLUDE = "Include";
    public static final String DEBIT_CREDIT_EXCLUDE = "Exclude";

    public static final String ZERO_ENCUMBRANCE_OPTION = "dummyBusinessObject.zeroEncumbranceOption";
    public static final String ZERO_ENCUMBRANCE_INCLUDE = "Include";
    public static final String ZERO_ENCUMBRANCE_EXCLUDE = "Exclude";

    public static final String SUB_ACCOUNT_OPTION = "subAccountNumber";

    public static final String DOCUMENT_APPROVED_CODE_APPROVED = "A";
    public static final String DOCUMENT_APPROVED_CODE_PENDING = "N";
    public static final String DOCUMENT_APPROVED_CODE_PROCESSED = "X";

    public static final String BALANCE_TYPE_PE = "PE";
    public static final String BALANCE_TYPE_CB = "CB";

    public static final String CONSOLIDATED_SUB_ACCOUNT_NUMBER = "*ALL*";
    public static final String CONSOLIDATED_SUB_OBJECT_CODE = "*ALL*";
    public static final String CONSOLIDATED_OBJECT_TYPE_CODE = "*ALL*";

    public static final String GL_LOOKUPABLE_ACCOUNT_BALANCE = "glAccountBalanceLookupable";
    public static final String GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_CONSOLIDATION = "glAccountBalanceByConsolidationLookupable";
    public static final String GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_LEVEL = "glAccountBalanceByLevelLookupable";
    public static final String GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_OBJECT = "glAccountBalanceByObjectLookupable";
    public static final String GL_LOOKUPABLE_BALANCE = "glBalanceLookupable";
    public static final String GL_LOOKUPABLE_CASH_BALANCE = "glCashBalanceLookupable";
    public static final String GL_LOOKUPABLE_ENCUMBRANCE = "glEncumbranceLookupable";
    public static final String GL_LOOKUPABLE_ENTRY = "glEntryLookupable";
    public static final String GL_LOOKUPABLE_PENDING_ENTRY = "glPendingEntryLookupable";

    public static final String RETURN_LOCATION_VALUE = "portal.do";
    public static final String LOOKUP_BUTTON_VALUE = "Drill Down";

    public static final String TOTAL_ACCOUNT_BALANCE_INCOME = "Income";
    public static final String TOTAL_ACCOUNT_BALANCE_EXPENSE_GROSS = "Expense (Gross)";
    public static final String TOTAL_ACCOUNT_BALANCE_EXPENSE_IN = "Expense (Net Transfer In)";
    public static final String TOTAL_ACCOUNT_BALANCE_AVAILABLE = "Avaliable Balance";

    public static final String SEARCH_RESULTS = "searchResults";

    public enum DocumentApprovedCode{
        APPROVED(DOCUMENT_APPROVED_CODE_APPROVED, "APPROVED"), PENDING(DOCUMENT_APPROVED_CODE_PENDING, "PENDING"), PROCESSED(DOCUMENT_APPROVED_CODE_PROCESSED, "PROCESSED");

        public String code;
        public String description;
        private DocumentApprovedCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getDescription(String code) {
            for(DocumentApprovedCode approvedCode : DocumentApprovedCode.values()) {
                if(approvedCode.code.equals(code)) {
                    return approvedCode.description;
                }
            }

            return null;
        }
    }
}
