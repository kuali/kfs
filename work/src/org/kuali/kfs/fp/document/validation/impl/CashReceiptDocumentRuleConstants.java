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
 * Holds constants for cash receipts document.
 */
public interface CashReceiptDocumentRuleConstants extends AccountingDocumentRuleBaseConstants {
    // Security grouping constants used to do application parameter lookups
    public static final String KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.CashReceiptDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String RESTRICTED_OBJECT_TYPE_CODES = "OBJECT_TYPES";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "OBJECT_SUB_TYPES";
    public static final String RESTRICTED_CONSOLIDATED_OBJECT_CODES = "OBJECT_CONSOLIDATIONS";
}
