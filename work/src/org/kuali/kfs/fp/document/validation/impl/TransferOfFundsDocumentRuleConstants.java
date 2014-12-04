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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;

/**
 * Defines constants for the transfer of funds document.
 */
public interface TransferOfFundsDocumentRuleConstants extends AccountingDocumentRuleBaseConstants {
    
    // Security grouping constants used to do application parameter lookups
    public static final String KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.TransferOfFundsDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String TRANSFER_OF_FUNDS_INCOME_OBJECT_TYPE_CODE = "TransferOfFundsIncomeObjectTypeCode";
    public static final String TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE = "TransferOfFundsExpenseObjectTypeCode";

    // Document type constants
    public static final String TRANSFER_OF_FUNDS_DOC_TYPE_CODE = "TF";
    public static final String YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE = "YETF";
}
