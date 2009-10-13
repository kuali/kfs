/*
 * Copyright 2006 The Kuali Foundation
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
