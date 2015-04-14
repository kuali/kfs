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
package org.kuali.kfs.module.ar;

public class ArAuthorizationConstants {

    public static class CustomerInvoiceDocumentEditMode {
        public static final String DISPLAY_PRINT_BUTTON = "displayPrintButton";
    }

    public static class ContractsGrantsInvoiceDocumentEditMode {
        public static final String MODIFY_TRANSMISSION_DATE = "modifyTransmissionDate";
    }

    public static class CashControlDocumentEditMode {
        public static final String EDIT_DETAILS = "editDetails";
        public static final String EDIT_PAYMENT_MEDIUM = "editPaymentMedium";
        public static final String EDIT_REF_DOC_NBR = "editRefDocNbr";
        public static final String EDIT_PAYMENT_APP_DOC = "editPaymentAppDoc";
        public static final String EDIT_BANK_CODE = "editBankCode";
        public static final String SHOW_GENERATE_BUTTON = "showGenerateButton";
        public static final String SHOW_BANK_CODE = "showBankCode";
    }

    public static class CustomerCreditMemoEditMode {
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String DISPLAY_PRINT_BUTTON = "displayPrintButton";
    }

    public static final String SALES_TAX_ENABLED = "salesTaxEnabled";

    public static class ContractsGrantsLetterOfCreditReviewDocumentEditMode {

        public static final String HIDE_RECALCULATE_BUTTON = "hideRecalculateButton";
        public static final String DISABLE_AMT_TO_DRAW = "disableAmountToDraw";
    }

    public static final String VIEW_CONTRACTS_GRANTS_INVOICE_IN_BILLING_REPORTS_PERMISSION = "View Contracts & Grants Invoice in Billing Reports";

}
