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
package org.kuali.kfs.module.ar;

public class ArAuthorizationConstants {

    public static class CustomerInvoiceDocumentEditMode {
        public static final String SHOW_RECEIVABLE_FAU = "showReceivableFAU";
        public static final String DISPLAY_PRINT_BUTTON = "displayPrintButton";
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


}
