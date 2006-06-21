/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 *
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 *
 * You may obtain a copy of the License at:
 *
 * http://kualiproject.org/license.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;


/**
 * Holds constants for <code>{@link org.kuali.module.financial.document.AuxiliaryVoucherDocument}</code>
 * business rules.
 *
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface AuxiliaryVoucherDocumentRuleConstants 
    extends TransactionalDocumentRuleBaseConstants {
    //  Security grouping constants used to do application parameter lookups
    public static final String AUXILIARY_VOUCHER_SECURITY_GROUPING = 
        "Kuali.FinancialTransactionProcessing.AuxiliaryVoucherDocument";

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final String RESTRICTED_COMBINED_CODES = "RestrictedCombinationOfCodes";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_CODES = "RestrictedObjectSubTypeCodes";
    public static final String RESTRICTED_EXPENSE_OBJECT_TYPE_CODES = "RestrictedExpenseObjectTypeCodes";
    public static final String RESTRICTED_INCOME_OBJECT_TYPE_CODES = "RestrictedIncomeObjectTypeCodes";
    public static final String RESTRICTED_PERIOD_CODES = "RestrictedPeriodCodes";
	public static final String GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE = "GeneralLedgerPendingEntryOffsetObjectCode";
}
