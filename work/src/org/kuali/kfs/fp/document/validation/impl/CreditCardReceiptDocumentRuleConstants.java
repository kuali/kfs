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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DELIMITER;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

/**
 * Contains Credit Card Receipt rule related constants
 */
public class CreditCardReceiptDocumentRuleConstants {
    // Security grouping constants used to do application parameter lookups
    public static final String KUALI_TRANSACTION_PROCESSING_CREDIT_CARD_RECEIPT_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.CreditCardReceiptDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String CREDIT_CARD_RECEIPT_PREFIX = DOCUMENT_ERROR_PREFIX + "creditCardReceipt" + DELIMITER;
}
