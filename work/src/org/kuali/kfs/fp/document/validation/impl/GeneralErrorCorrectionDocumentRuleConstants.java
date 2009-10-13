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
 * Holds constants for <code>{@link org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument}</code> business rules.
 */
public interface GeneralErrorCorrectionDocumentRuleConstants extends AccountingDocumentRuleBaseConstants {
    // Security grouping constants used to do application parameter lookups
    public static final String GENERAL_ERROR_CORRECTION_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.GeneralErrorCorrectionDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String RESTRICTED_OBJECT_TYPE_CODES = "OBJECT_TYPES";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "OBJECT_SUB_TYPES";
    public static final String INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";
    public static final String VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";

    public static final String TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER = "+";
}
