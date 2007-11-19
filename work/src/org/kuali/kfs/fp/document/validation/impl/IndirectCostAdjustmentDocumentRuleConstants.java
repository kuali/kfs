/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.rules;

/**
 * Holds constants for <code>{@link org.kuali.module.financial.document.IndirectCostAdjustmentDocument}</code> business rules.
 */
public interface IndirectCostAdjustmentDocumentRuleConstants {
    public static final String INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.IndirectCostAdjustmentDocument";

    public static final String RESTRICTED_SUB_TYPE_GROUP_CODES = "OBJECT_SUB_TYPES";
    public static final String RESTRICTED_OBJECT_TYPE_CODES = "OBJECT_TYPES";
    public static final String GRANT_OBJECT_CODE = "GRANT_OBJECT_CODE";
    public static final String RECEIPT_OBJECT_CODE = "RECEIPT_OBJECT_CODE";
}
