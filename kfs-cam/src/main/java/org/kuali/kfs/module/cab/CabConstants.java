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
package org.kuali.kfs.module.cab;


public class CabConstants {
    public static final String PREQ = "PREQ";
    public static final String CM = "CM";

    public static final String TRADE_IN_TYPE_CODE = "TRDI";

    public static final String CB_INVOICE_LINE_ACTION_URL = "../cabPurApLine.do";
    public static final String DOT_DOC = ".doc";
    public static final String DOT_LINE = ".line";

    public static final String TRADE_IN_INDICATOR_QUESTION = "TradeInIndicatorConfirmation";
    public static final String SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL_QUESTION = "SkipAssetNumbersToAssetGlobalConfirmation";
    public static final String PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION = "PaymentDifferentObjectSubTypeQuestion";;
    public static final String CAPITAL_ASSET_SYSTEM_STATE_CODE_NEW = "NEW";
    public static final String PO_STATUS_CODE_OPEN = "Open";

    public static final String CAB_PURAP_SESSION = "CABPurAp";

    public static class DateFormats {
        public static final String MONTH_DAY_YEAR = "MM/dd/yyyy";
        public static final String MILITARY_TIME = "HH:mm:ss";
    }

    public static class Parameters {
        public static final String LAST_EXTRACT_TIME = "LAST_EXTRACT_TIME";
        public static final String SUB_FUND_GROUPS = "SUB_FUND_GROUPS";
        public static final String FISCAL_PERIODS = "FISCAL_PERIODS";
        public static final String DOCUMENT_TYPES = "DOCUMENT_TYPES";
        public static final String CHARTS = "CHARTS";
        public static final String OBJECT_SUB_TYPES = "OBJECT_SUB_TYPES";
        public static final String BALANCE_TYPES = "BALANCE_TYPES";
        public static final String NAMESPACE = "KFS-CAB";
        public static final String DETAIL_TYPE_BATCH = "Batch";
        public static final String DETAIL_TYPE_DOCUMENT = "Document";
        public static final String LAST_EXTRACT_DATE = "LAST_EXTRACT_DATE";
        public static final String DETAIL_TYPE_PRE_ASSET_TAGGING_STEP = "PreAssetTaggingStep";
    }

    public static class Actions {
        public static final String PROCESS = "process";
        public static final String VIEW = "view";
        public static final String START = "start";
        public static final String SPLIT = "split";
        public static final String MERGE = "merge";
        public static final String MERGE_ALL = "merge all";
        public static final String ALLOCATE = "allocate";
        public static final String CREATE_ASSET = "createAsset";
        public static final String APPLY_PAYMENT = "applyPayment";
        public static final String PERCENT_PAYMENT = "percent payment";
        public static final String VIEW_DOC = "viewDoc";
    }

    public static class ValidationStrings {
        public static final String CAPITAL = "Capital";
        public static final String EXPENSE = "Expense";
        public static final String RECURRING = "Recurring";
        public static final String NON_RECURRING = "Non-recurring";
    }

    public static class ActivityStatusCode {
        public static final String NEW = "N";
        public static final String MODIFIED = "M";
        public static final String ENROUTE = "E";
        public static final String PROCESSED_IN_CAMS = "P";
    }
}
