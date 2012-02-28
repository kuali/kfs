/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;

/**
 * A collection of methods that help accounting docs determine whether an accounting line represents a debit or not
 */
public interface DebitDeterminerService {
    /**
     * @param debitCreditCode
     * @return true if debitCreditCode equals the the debit constant
     */
    public abstract boolean isDebitCode(String debitCreditCode);

    /**
     * <ol>
     * <li>object type is included in determining if a line is debit or credit.
     * </ol>
     * the following are credits (return false)
     * <ol>
     * <li> (isIncome || isLiability) && (lineAmount > 0)
     * <li> (isExpense || isAsset) && (lineAmount < 0)
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> (isIncome || isLiability) && (lineAmount < 0)
     * <li> (isExpense || isAsset) && (lineAmount > 0)
     * </ol>
     * the following are invalid ( throws an <code>IllegalStateException</code>)
     * <ol>
     * <li> document isErrorCorrection
     * <li> lineAmount == 0
     * <li> ! (isIncome || isLiability || isExpense || isAsset)
     * </ol>
     *
     * @param rule
     * @param accountingDocument
     * @param accountingLine
     * @return boolean
     */
    public abstract boolean isDebitConsideringType(GeneralLedgerPendingEntrySource poster, GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * <ol>
     * <li>object type is not included in determining if a line is debit or credit.
     * <li>accounting line section (source/target) is not included in determining if a line is debit or credit.
     * </ol>
     * the following are credits (return false)
     * <ol>
     * <li> none
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> (isIncome || isLiability || isExpense || isAsset) && (lineAmount > 0)
     * </ol>
     * the following are invalid ( throws an <code>IllegalStateException</code>)
     * <ol>
     * <li> lineAmount <= 0
     * <li> ! (isIncome || isLiability || isExpense || isAsset)
     * </ol>
     *
     * @param rule
     * @param accountingDocument
     * @param accountingLine
     * @return boolean
     */
    public abstract boolean isDebitConsideringNothingPositiveOnly(GeneralLedgerPendingEntrySource poster, GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * <ol>
     * <li>accounting line section (source/target) type is included in determining if a line is debit or credit.
     * <li> zero line amounts are never allowed
     * </ol>
     * the following are credits (return false)
     * <ol>
     * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
     * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
     * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
     * </ol>
     * the following are invalid ( throws an <code>IllegalStateException</code>)
     * <ol>
     * <li> lineAmount == 0
     * <li> ! (isIncome || isLiability || isExpense || isAsset)
     * </ol>
     *
     * @param rule
     * @param accountingDocument
     * @param accountingLine
     * @return boolean
     */
    public abstract boolean isDebitConsideringSection(AccountingDocument accountingDocument, AccountingLine accountingLine);

    /**
     * <ol>
     * <li>accounting line section (source/target) and object type is included in determining if a line is debit or credit.
     * <li> negative line amounts are <b>Only</b> allowed during error correction
     * </ol>
     * the following are credits (return false)
     * <ol>
     * <li> isSourceLine && (isExpense || isAsset) && (lineAmount > 0)
     * <li> isTargetLine && (isIncome || isLiability) && (lineAmount > 0)
     * <li> isErrorCorrection && isSourceLine && (isIncome || isLiability) && (lineAmount < 0)
     * <li> isErrorCorrection && isTargetLine && (isExpense || isAsset) && (lineAmount < 0)
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> isSourceLine && (isIncome || isLiability) && (lineAmount > 0)
     * <li> isTargetLine && (isExpense || isAsset) && (lineAmount > 0)
     * <li> isErrorCorrection && (isExpense || isAsset) && (lineAmount < 0)
     * <li> isErrorCorrection && (isIncome || isLiability) && (lineAmount < 0)
     * </ol>
     * the following are invalid ( throws an <code>IllegalStateException</code>)
     * <ol>
     * <li> !isErrorCorrection && !(lineAmount > 0)
     * </ol>
     *
     * @param rule
     * @param accountingDocument
     * @param accountingLine
     * @return boolean
     */



    public abstract boolean isDebitConsideringSectionAndTypePositiveOnly(AccountingDocument accountingDocument, AccountingLine accountingLine);

    /**
     * This method is to convert amount to positive or negative based on the object type and Debit CreditCode combination.
     *
     */
    public  String getConvertedAmount(String objectType , String debitCreditCode, String amount) ;

    /**
     * throws an <code>IllegalStateException</code> if the document is an error correction. otherwise does nothing
     *
     * @param rule
     * @param accountingDocument
     */
    public abstract void disallowErrorCorrectionDocumentCheck(GeneralLedgerPendingEntrySource poster);

    /**
     * Convience method for determine if a document is an error correction document.
     *
     * @param accountingDocument
     * @return true if document is an error correct
     */
    public abstract boolean isErrorCorrection(GeneralLedgerPendingEntrySource poster);

    /**
     * Determines whether an accounting line is an asset line.
     *
     * @param accountingLine
     * @return boolean True if a line is an asset line.
     */
    public abstract boolean isAsset(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Determines whether an accounting line is a liability line.
     *
     * @param accountingLine
     * @return boolean True if the line is a liability line.
     */
    public abstract boolean isLiability(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Determines whether an accounting line is an income line or not. This goes agains the configurable object type code list in
     * the ApplicationParameter mechanism. This list can be configured externally.
     *
     * @param accountingLine
     * @return boolean True if the line is an income line.
     */
    public abstract boolean isIncome(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Check object code type to determine whether the accounting line is expense.
     *
     * @param accountingLine
     * @return boolean True if the line is an expense line.
     */
    public abstract boolean isExpense(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Determines whether an accounting line is an expense or asset.
     *
     * @param line
     * @return boolean True if it's an expense or asset.
     */
    public abstract boolean isExpenseOrAsset(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Determines whether an accounting line is an income or liability line.
     *
     * @param line
     * @return boolean True if the line is an income or liability line.
     */
    public abstract boolean isIncomeOrLiability(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Check object code type to determine whether the accounting line is revenue.
     *
     * @param line
     * @return boolean True if the line is a revenue line.
     */
    public abstract boolean isRevenue(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Determines whether the <code>objectTypeCode</code> is an asset.
     *
     * @param objectTypeCode
     * @return Is she asset or something completely different?
     */
    public abstract boolean isAssetTypeCode(String objectTypeCode);

    /**
     * Determines whether the <code>objectTypeCode</code> is a liability.
     *
     * @param objectTypeCode
     * @return Is she liability or something completely different?
     */
    public abstract boolean isLiabilityTypeCode(String objectTypeCode);

    /**
     * Gets the isDebitCalculationIllegalStateExceptionMessage attribute.
     * @return Returns the isDebitCalculationIllegalStateExceptionMessage.
     */
    public abstract String getDebitCalculationIllegalStateExceptionMessage();

    /**
     * Gets the isErrorCorrectionIllegalStateExceptionMessage attribute.
     * @return Returns the isErrorCorrectionIllegalStateExceptionMessage.
     */
    public abstract String getErrorCorrectionIllegalStateExceptionMessage();

    /**
     * Gets the isInvalidLineTypeIllegalArgumentExceptionMessage attribute.
     * @return Returns the isInvalidLineTypeIllegalArgumentExceptionMessage.
     */
    public abstract String getInvalidLineTypeIllegalArgumentExceptionMessage();
}
