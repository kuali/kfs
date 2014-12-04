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
package org.kuali.kfs.module.ld.util;

import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class provides a set of utilities that handle the debit credit logic.
 */
public class DebitCreditUtil {

    /**
     * Determine the Debit Credit code based on the given amount. Normally (isReverse flag is set as false), the debit code returns
     * if the amount is positive while the credit code returns if the amount is negative. When isReverse flag is set as true, the
     * credit returns for positive amount and the debit code for negative amount.
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param isReversed a flag that indicates if normal accounting practice is used. False for normal accoutning practice; true for
     *        reverse.
     * @return the Debit Credit code based on the given transaction amount and the value of isReversed
     */
    public static String getDebitCreditCode(KualiDecimal amount, boolean isReversed) {
        return getDebitCreditCode(amount, "", isReversed);
    }

    /**
     * Determine the Debit Credit code based on the given amount. Normally (isReverse flag is set as false), the debit code returns
     * if the amount is positive while the credit code returns if the amount is negative. When isReverse flag is set as true, the
     * credit returns for positive amount and the debit code for negative amount.
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param currentDebitCreditCode the current debit credit code
     * @param isReversed a flag that indicates if normal accounting practice is used. False for normal accoutning practice; true for
     *        reverse.
     * @return the Debit Credit code based on the given transaction amount and the value of isReversed
     */
    public static String getDebitCreditCode(KualiDecimal amount, String currentDebitCreditCode, boolean isReversed) {
        String debitCreditCode = null;
        if (amount.isNegative()) {
            if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
        }
        else {
            if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
        }

        if (isReversed) {
            debitCreditCode = getReverseDebitCreditCode(debitCreditCode);
        }
        return debitCreditCode;
    }

    /**
     * Determines the Debit Credit code for the expense accountine line (Salary Expense and Benefit Expense documents).
     * 
     * @param accountingLine - line to determine code for
     * @return String representing the debit/credit code for the line
     */
    public static String getDebitCreditCodeForExpenseDocument(ExpenseTransferAccountingLine accountingLine) {
        String debitCreditCode = null;
        boolean isPositiveAmount = accountingLine.getAmount().isPositive();

        if (accountingLine.isSourceAccountingLine()) {
            if (isPositiveAmount) {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
        }
        else if (accountingLine.isTargetAccountingLine()) {
            if (isPositiveAmount) {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
        }
        else {
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            throw new IllegalStateException(isDebitUtils.getInvalidLineTypeIllegalArgumentExceptionMessage());
        }

        return debitCreditCode;
    }

    /**
     * get the reversed debit credit code of the given code
     * 
     * @param currentDebitCreditCode the current debit credit code
     * @return the reversed debit credit code of the given code
     */
    public static String getReverseDebitCreditCode(String currentDebitCreditCode) {
        if (KFSConstants.GL_DEBIT_CODE.equals(currentDebitCreditCode)) {
            return KFSConstants.GL_CREDIT_CODE;
        }

        if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
            return KFSConstants.GL_DEBIT_CODE;
        }

        return KFSConstants.GL_CREDIT_CODE;
    }

    /**
     * Determine the actual amount based on Debit Credit code. If the code is credit code, then change the sign of the given amount;
     * otherwise, do nothing
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param currentDebitCreditCode the current debit credit code
     * @return the actual numeric amount of the given amount
     */
    public static KualiDecimal getNumericAmount(KualiDecimal amount, String currentDebitCreditCode) {
        KualiDecimal actualAmount = amount;

        if (amount == null) {
            actualAmount = KualiDecimal.ZERO;
        }
        else if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
            actualAmount = actualAmount.multiply(new KualiDecimal(-1));
        }
        return actualAmount;
    }
}
