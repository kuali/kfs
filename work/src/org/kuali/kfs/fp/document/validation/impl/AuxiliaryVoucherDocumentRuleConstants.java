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
 * Holds constants for <code>{@link org.kuali.kfs.fp.document.AuxiliaryVoucherDocument}</code> business rules.
 */
public interface AuxiliaryVoucherDocumentRuleConstants extends AccountingDocumentRuleBaseConstants {

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String RESTRICTED_COMBINED_CODES = "COMBINATION_OBJECT_TYPE_OBJECT_SUB_TYPE_OBJECT_LEVEL";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "OBJECT_SUB_TYPES";
    public static final String RESTRICTED_PERIOD_CODES = "ACCOUNTING_PERIODS";
    public static final String GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE = "GLPE_OFFSET_OBJECT_CODE";
    public static final String AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD = "ACCOUNTING_PERIOD_GRACE_PERIOD";
}
