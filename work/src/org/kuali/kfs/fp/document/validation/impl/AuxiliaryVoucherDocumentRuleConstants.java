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
