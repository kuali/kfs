/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashieringItemInProcess;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.service.CashDrawerService;

/**
 * This class represents the rule used during cash management
 */
public class CashieringTransactionRule {
    private static final Logger LOG = Logger.getLogger(CashieringTransactionRule.class);
    private CashDrawerService cashDrawerService;

    /**
     * Returns true if all application rules called by this method return true
     * 
     * @param cmDoc represents cash management document
     * @return true if all cashiering transaction application rules do not fail
     */
    public boolean processCashieringTransactionApplicationRules(CashManagementDocument cmDoc) {
        boolean success = true;
        success &= checkMoneyInNoNegatives(cmDoc.getCurrentTransaction());
        success &= checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction());
        success &= checkAllPaidBackItemsInProcess(cmDoc.getCurrentTransaction());
        success &= checkNewItemInProcessDoesNotExceedCashDrawer(cmDoc, cmDoc.getCurrentTransaction());
        success &= checkNewItemInProcessInPast(cmDoc.getCurrentTransaction());
        success &= checkTransactionCheckTotalDoesNotExceedCashDrawer(cmDoc, cmDoc.getCurrentTransaction());
        success &= checkItemInProcessIsNotPayingOffItemInProcess(cmDoc, cmDoc.getCurrentTransaction());
        if (success) {
            success = checkEnoughCashForMoneyOut(cmDoc, cmDoc.getCurrentTransaction());
        }
        if (success) {
            success &= checkMoneyInMoneyOutBalance(cmDoc.getCurrentTransaction());
        }
        return success;
    }

    /**
     * Returns true if none of the entered money-in amounts (cash and coin) are not negative in a cashiering transaction
     * 
     * @param trans represents cashiering transaction from document
     * @return true if none of the amounts are negative
     */
    public boolean checkMoneyInNoNegatives(CashieringTransaction trans) {
        boolean success = true;

        // money in currency
        if (trans.getMoneyInCurrency().getFinancialDocumentHundredDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentHundredDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.hundredDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getHundredDollarCount().toString(), "hundred dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentFiftyDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentFiftyDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.fiftyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getFiftyDollarCount().toString(), "fifty dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentTwentyDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentTwentyDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.twentyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getTwentyDollarCount().toString(), "twenty dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentTenDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentTenDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.tenDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getTenDollarCount().toString(), "ten dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentFiveDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentFiveDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.fiveDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getFiveDollarCount().toString(), "five dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentTwoDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentTwoDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.twoDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getTwoDollarCount().toString(), "two dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentOneDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentOneDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.oneDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getOneDollarCount().toString(), "one dollar count" });
            success = false;
        }
        if (trans.getMoneyInCurrency().getFinancialDocumentOtherDollarAmount() != null && trans.getMoneyInCurrency().getFinancialDocumentOtherDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.financialDocumentOtherDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getFinancialDocumentOtherDollarAmount().toString(), "other dollar amount" });
            success = false;
        }

        // money in coin
        if (trans.getMoneyInCoin().getFinancialDocumentHundredCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentHundredCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.hundredCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getHundredCentCount().toString(), "hundred cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentFiftyCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentFiftyCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.fiftyCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getFiftyCentCount().toString(), "fifty cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentTenCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentTenCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.tenCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getTenCentCount().toString(), "ten cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentTwentyFiveCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentTwentyFiveCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.twentyFiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getTwentyFiveCentCount().toString(), "twenty five cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentFiveCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentFiveCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.fiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getFiveCentCount().toString(), "five cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentOneCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentOneCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.oneCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getOneCentCount().toString(), "one cent count" });
            success = false;
        }
        if (trans.getMoneyInCoin().getFinancialDocumentOtherCentAmount() != null && trans.getMoneyInCoin().getFinancialDocumentOtherCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCoin.financialDocumentOtherCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getFinancialDocumentOtherCentAmount().toString(), "other cent amount" });
            success = false;
        }

        // newItemInProcess amount
        if (trans.getNewItemInProcess() != null && trans.getNewItemInProcess().isPopulated() && trans.getNewItemInProcess().getItemAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.newItemInProcess.itemAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_NEW_ITEM_IN_PROCESS_NOT_NEGATIVE, new String[0]);
            success = false;
        }

        // checks
        int count = 0;
        for (Check check : trans.getMoneyInChecks()) {
            if (check.getAmount() != null && check.getAmount().isNegative()) {
                GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInChecks[" + count + "].amount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CHECK_AMOUNT_NOT_NEGATIVE, new String[] { check.getAmount().toString(), check.getDescription() });
                success = false;
            }
            count += 1;
        }

        return success;
    }

    /**
     * Returns true if none of the entered money-out amounts (cash and coin) are not negative in a cashiering transaction
     * 
     * @param trans represents cashiering transaction from document
     * @return true if none of the amounts are negative
     */
    public boolean checkMoneyOutNoNegatives(CashieringTransaction trans) {
        boolean success = true;

        // money out currency
        if (trans.getMoneyOutCurrency().getFinancialDocumentHundredDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentHundredDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.hundredDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCurrency().getHundredDollarCount().toString(), "hundred dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentFiftyDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentFiftyDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.fiftyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCurrency().getFiftyDollarCount().toString(), "fifty dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentTwentyDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentTwentyDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.twentyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCurrency().getTwentyDollarCount().toString(), "twenty dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentTenDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentTenDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.tenDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getTenDollarCount().toString(), "ten dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentFiveDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentFiveDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.fiveDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCurrency().getFiveDollarCount().toString(), "five dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentTwoDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentTwoDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.twoDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCurrency().getTwoDollarCount().toString(), "two dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentOneDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentOneDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.oneDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getOneDollarCount().toString(), "one dollar count" });
            success = false;
        }
        if (trans.getMoneyOutCurrency().getFinancialDocumentOtherDollarAmount() != null && trans.getMoneyOutCurrency().getFinancialDocumentOtherDollarAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentOtherDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCurrency().getFinancialDocumentOtherDollarAmount().toString(), "other dollar amount" });
            success = false;
        }

        // money out coin
        if (trans.getMoneyOutCoin().getFinancialDocumentHundredCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentHundredCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.hundredCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getHundredCentCount().toString(), "hundred cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentFiftyCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentFiftyCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.fiftyCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getFiftyCentCount().toString(), "fifty cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentTenCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentTenCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.tenCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getTenCentCount().toString(), "ten cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentTwentyFiveCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentTwentyFiveCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.twentyFiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getTwentyFiveCentCount().toString(), "twenty five cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentFiveCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentFiveCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.fiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getFiveCentCount().toString(), "five cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentOneCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentOneCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.oneCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyInCoin().getOneCentCount().toString(), "one cent count" });
            success = false;
        }
        if (trans.getMoneyOutCoin().getFinancialDocumentOtherCentAmount() != null && trans.getMoneyOutCoin().getFinancialDocumentOtherCentAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentOtherCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { trans.getMoneyOutCoin().getFinancialDocumentOtherCentAmount().toString(), "other cent amount" });
            success = false;
        }

        // open items in process amounts
        int count = 0;
        if (trans.getOpenItemsInProcess() != null) {
            for (CashieringItemInProcess itemInProc : trans.getOpenItemsInProcess()) {
                if (itemInProc.getCurrentPayment() != null && itemInProc.getCurrentPayment().isNegative()) {
                    GlobalVariables.getErrorMap().putError("document.currentTransaction.openItemsInProcess[" + count + "].currentPayment", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_REDUCED_ITEM_IN_PROCESS_NOT_NEGATIVE, new String[] { itemInProc.getItemIdentifier().toString() });
                    success = false;
                }
                count += 1;
            }
        }

        return success;
    }

    /**
     * Returns true if money-in and money-out are in balance with each other
     * 
     * @param trans represents cashiering transaction from cash management document
     * @return true if money-in and money-out are balanced
     */
    public boolean checkMoneyInMoneyOutBalance(CashieringTransaction trans) {
        boolean success = true;
        if (!trans.getMoneyInTotal().equals(trans.getMoneyOutTotal())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyInCurrency.financialDocumentHundredDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_IN_OUT_DO_NOT_BALANCE, new String[0]);
            success = false;
        }
        return success;
    }

    /**
     * This method returns true if none of the coin (1 cent, 5 cents, etc) and cash increments (1 dollar, 2 dollars, 5 dollars etc. )
     * from ( money-in + cash drawer ) exceed the amount for that increment from the money-out.
     * 
     * @param cmDoc represents cash management document
     * @param trans represents cash transaction from cash management document
     * @return true if none of the coin and cash increments from money-in + cash drawer excreed amount for increments in money-out 
     */
    public boolean checkEnoughCashForMoneyOut(CashManagementDocument cmDoc, CashieringTransaction trans) {
        boolean success = true;

        CashDrawer cashDrawer = cmDoc.getCashDrawer();

        // money out currency
        CurrencyDetail moneyInCurrency = trans.getMoneyInCurrency();
        CurrencyDetail moneyOutCurrency = trans.getMoneyOutCurrency();

        KualiDecimal existingHundredDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentHundredDollarAmount() != null) {
            existingHundredDollarAmount = existingHundredDollarAmount.add(cashDrawer.getFinancialDocumentHundredDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentHundredDollarAmount() != null) {
            existingHundredDollarAmount = existingHundredDollarAmount.add(moneyInCurrency.getFinancialDocumentHundredDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentHundredDollarAmount() != null && existingHundredDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentHundredDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentHundredDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "hundred dollar", moneyOutCurrency.getFinancialDocumentHundredDollarAmount().toString(), cashDrawer.getFinancialDocumentHundredDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingOtherDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOtherDollarAmount() != null) {
            existingOtherDollarAmount = existingOtherDollarAmount.add(cashDrawer.getFinancialDocumentOtherDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentOtherDollarAmount() != null) {
            existingOtherDollarAmount = existingOtherDollarAmount.add(moneyInCurrency.getFinancialDocumentOtherDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentOtherDollarAmount() != null && existingOtherDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentOtherDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentOtherDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "other dollar", moneyOutCurrency.getFinancialDocumentOtherDollarAmount().toString(), cashDrawer.getFinancialDocumentOtherDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwoDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwoDollarAmount() != null) {
            existingTwoDollarAmount = existingTwoDollarAmount.add(cashDrawer.getFinancialDocumentTwoDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTwoDollarAmount() != null) {
            existingTwoDollarAmount = existingTwoDollarAmount.add(moneyInCurrency.getFinancialDocumentTwoDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTwoDollarAmount() != null && existingTwoDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTwoDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTwoDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "two dollar", moneyOutCurrency.getFinancialDocumentTwoDollarAmount().toString(), cashDrawer.getFinancialDocumentTwoDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiftyDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiftyDollarAmount() != null) {
            existingFiftyDollarAmount = existingFiftyDollarAmount.add(cashDrawer.getFinancialDocumentFiftyDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentFiftyDollarAmount() != null) {
            existingFiftyDollarAmount = existingFiftyDollarAmount.add(moneyInCurrency.getFinancialDocumentFiftyDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentFiftyDollarAmount() != null && existingFiftyDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentFiftyDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentFiftyDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "fifty dollar", moneyOutCurrency.getFinancialDocumentFiftyDollarAmount().toString(), cashDrawer.getFinancialDocumentFiftyDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwentyDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwentyDollarAmount() != null) {
            existingTwentyDollarAmount = existingTwentyDollarAmount.add(cashDrawer.getFinancialDocumentTwentyDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTwentyDollarAmount() != null) {
            existingTwentyDollarAmount = existingTwentyDollarAmount.add(moneyInCurrency.getFinancialDocumentTwentyDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTwentyDollarAmount() != null && existingTwentyDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTwentyDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTwentyDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "twenty dollar", moneyOutCurrency.getFinancialDocumentTwentyDollarAmount().toString(), cashDrawer.getFinancialDocumentTwentyDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTenDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTenDollarAmount() != null) {
            existingTenDollarAmount = existingTenDollarAmount.add(cashDrawer.getFinancialDocumentTenDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTenDollarAmount() != null) {
            existingTenDollarAmount = existingTenDollarAmount.add(moneyInCurrency.getFinancialDocumentTenDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTenDollarAmount() != null && existingTenDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTenDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTenDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "ten dollar", moneyOutCurrency.getFinancialDocumentTenDollarAmount().toString(), cashDrawer.getFinancialDocumentTenDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiveDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiveDollarAmount() != null) {
            existingFiveDollarAmount = existingFiveDollarAmount.add(cashDrawer.getFinancialDocumentFiveDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentFiveDollarAmount() != null) {
            existingFiveDollarAmount = existingFiveDollarAmount.add(moneyInCurrency.getFinancialDocumentFiveDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentFiveDollarAmount() != null && existingFiveDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentFiveDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentFiveDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "five dollar", moneyOutCurrency.getFinancialDocumentFiveDollarAmount().toString(), cashDrawer.getFinancialDocumentFiveDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingOneDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOneDollarAmount() != null) {
            existingOneDollarAmount = existingOneDollarAmount.add(cashDrawer.getFinancialDocumentOneDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentOneDollarAmount() != null) {
            existingOneDollarAmount = existingOneDollarAmount.add(moneyInCurrency.getFinancialDocumentOneDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentOneDollarAmount() != null && existingOneDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentOneDollarAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentOneDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "one dollar", moneyOutCurrency.getFinancialDocumentOneDollarAmount().toString(), cashDrawer.getFinancialDocumentOneDollarAmount().toString() });
            success = false;
        }

        // money out coin
        CoinDetail moneyOutCoin = trans.getMoneyOutCoin();
        CoinDetail moneyInCoin = trans.getMoneyInCoin();
        KualiDecimal existingHundredCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentHundredCentAmount() != null) {
            existingHundredCentAmount = existingHundredCentAmount.add(cashDrawer.getFinancialDocumentHundredCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentHundredCentAmount() != null) {
            existingHundredCentAmount = existingHundredCentAmount.add(moneyInCoin.getFinancialDocumentHundredCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentHundredCentAmount() != null && existingHundredCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentHundredCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentHundredCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "hundred cent", moneyOutCoin.getFinancialDocumentHundredCentAmount().toString(), cashDrawer.getFinancialDocumentHundredCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingOtherCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOtherCentAmount() != null) {
            existingOtherCentAmount = existingOtherCentAmount.add(cashDrawer.getFinancialDocumentOtherCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentOtherCentAmount() != null) {
            existingOtherCentAmount = existingOtherCentAmount.add(moneyInCoin.getFinancialDocumentOtherCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentOtherCentAmount() != null && existingOtherCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentOtherCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentOtherCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "other cent", moneyOutCoin.getFinancialDocumentOtherCentAmount().toString(), cashDrawer.getFinancialDocumentOtherCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiftyCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiftyCentAmount() != null) {
            existingFiftyCentAmount = existingFiftyCentAmount.add(cashDrawer.getFinancialDocumentFiftyCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentFiftyCentAmount() != null) {
            existingFiftyCentAmount = existingFiftyCentAmount.add(moneyInCoin.getFinancialDocumentFiftyCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentFiftyCentAmount() != null && existingFiftyCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentFiftyCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentFiftyCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "fifty cent", moneyOutCoin.getFinancialDocumentFiftyCentAmount().toString(), cashDrawer.getFinancialDocumentFiftyCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwentyFiveCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwentyFiveCentAmount() != null) {
            existingTwentyFiveCentAmount = existingTwentyFiveCentAmount.add(cashDrawer.getFinancialDocumentTwentyFiveCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentTwentyFiveCentAmount() != null) {
            existingTwentyFiveCentAmount = existingTwentyFiveCentAmount.add(moneyInCoin.getFinancialDocumentTwentyFiveCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount() != null && existingTwentyFiveCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentTwentyFiveCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "twenty five cent", moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount().toString(), cashDrawer.getFinancialDocumentTwentyFiveCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingTenCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTenCentAmount() != null) {
            existingTenCentAmount = existingTenCentAmount.add(cashDrawer.getFinancialDocumentTenCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentTenCentAmount() != null) {
            existingTenCentAmount = existingTenCentAmount.add(moneyInCoin.getFinancialDocumentTenCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentTenCentAmount() != null && existingTenCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentTenCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentTenCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "ten cent", moneyOutCoin.getFinancialDocumentTenCentAmount().toString(), cashDrawer.getFinancialDocumentTenCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiveCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiveCentAmount() != null) {
            existingFiveCentAmount = existingFiveCentAmount.add(cashDrawer.getFinancialDocumentFiveCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentFiveCentAmount() != null) {
            existingFiveCentAmount = existingFiveCentAmount.add(moneyInCoin.getFinancialDocumentFiveCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentFiveCentAmount() != null && existingFiveCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentFiveCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentFiveCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "five cent", moneyOutCoin.getFinancialDocumentFiveCentAmount().toString(), cashDrawer.getFinancialDocumentFiveCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingOneCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOneCentAmount() != null) {
            existingOneCentAmount = existingOneCentAmount.add(cashDrawer.getFinancialDocumentOneCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentOneCentAmount() != null) {
            existingOneCentAmount = existingOneCentAmount.add(moneyInCoin.getFinancialDocumentOneCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentOneCentAmount() != null && existingOneCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentOneCentAmount())) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentOneCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER, new String[] { "one cent", moneyOutCoin.getFinancialDocumentOneCentAmount().toString(), cashDrawer.getFinancialDocumentOneCentAmount().toString() });
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if the new item in process does not exceed the current amount in the cash drawer reserves
     * 
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if the new item in process does not exceed the current amount in the cash drawer reserves
     */
    public boolean checkNewItemInProcessDoesNotExceedCashDrawer(CashManagementDocument cmDoc, CashieringTransaction trans) {
        boolean success = true;

        if (trans.getNewItemInProcess().getItemAmount() != null && trans.getNewItemInProcess().getItemAmount().isGreaterThan(calculateTotalCashDrawerReserves(cmDoc, trans))) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.newItemInProcess", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_EXCEEDS_DRAWER, new String[] { "new Item In Process", trans.getNewItemInProcess().getItemAmount().toString(), calculateTotalCashDrawerReserves(cmDoc, trans).toString() });
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if check total from transaction does not exceed the current amount in the cash drawer reserves 
     * 
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if check total from transaction does not exceed the current amount in the cash drawer reserves
     */
    public boolean checkTransactionCheckTotalDoesNotExceedCashDrawer(CashManagementDocument cmDoc, CashieringTransaction trans) {
        boolean success = true;

        if (trans.getTotalCheckAmount().isGreaterThan(calculateTotalCashDrawerReserves(cmDoc, trans))) {
            GlobalVariables.getErrorMap().putError("document.currentTransaction.newCheck", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_EXCEEDS_DRAWER, new String[] { "given checks", trans.getTotalCheckAmount().toString(), calculateTotalCashDrawerReserves(cmDoc, trans).toString() });
            success = false;
        }

        return success;
    }
 
    /**
     * This method returns true if the current payment amount for the cashiering item in process does not exceed
     * the actual item amount for the item in process
     * 
     * @param itemInProc cashiering item in process
     * @param cashieringItemNumber cashiering item number
     * @return true if the current payment amount for the cashiering item in process does not exceed
     *          the actual item amount for the item in process
     */
    public boolean checkPaidBackItemInProcessDoesNotExceedTotal(CashieringItemInProcess itemInProc, int cashieringItemNumber) {
        boolean success = true;
        if (itemInProc.getCurrentPayment() != null && itemInProc.getCurrentPayment().isGreaterThan(itemInProc.getItemAmount())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.CASHIERING_TRANSACTION_OPEN_ITEM_IN_PROCESS_PROPERTY + "[" + cashieringItemNumber + "]", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_PAID_BACK_EXCEEDS_AMOUNT_LEFT, new String[] { itemInProc.getItemIdentifier().toString() });
            success = false;
        }
        return success;
    }

    /**
     * This method returns true if a new item in process is populated and none of the open item in process' amounts are greater than zero.
     * 
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if a new item in process is populated and none of the open item in process' amounts are greater than zero.
     */
    public boolean checkItemInProcessIsNotPayingOffItemInProcess(CashManagementDocument cmDoc, CashieringTransaction trans) {
        boolean success = true;
        if (trans.getNewItemInProcess().isPopulated()) {
            int count = 0;
            for (CashieringItemInProcess advance : trans.getOpenItemsInProcess()) {
                if (advance.getCurrentPayment() != null && advance.getCurrentPayment().isGreaterThan(KualiDecimal.ZERO)) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.CASHIERING_TRANSACTION_OPEN_ITEM_IN_PROCESS_PROPERTY + "[" + count + "]", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CANNOT_PAY_OFF_ADVANCE_WITH_ADVANCE, new String[] { advance.getItemIdentifier().toString() });
                    success = false;
                }
                count += 1;
            }
        }
        return success;
    }

    /**
     * This method returns true if all open items in process amounts do not exceed the total for each specific item's amount total
     * 
     * @param trans transaction from cash management document
     * @return true if all open items in process amounts do not exceed the total for each specific item's amount total
     */
    public boolean checkAllPaidBackItemsInProcess(CashieringTransaction trans) {
        boolean success = true;
        int count = 0;
        if (trans.getOpenItemsInProcess() != null) {
            for (CashieringItemInProcess itemInProc : trans.getOpenItemsInProcess()) {
                success &= checkPaidBackItemInProcessDoesNotExceedTotal(itemInProc, count);
                count += 1;
            }
        }
        return success;
    }

    /**
     * This method returns true if the current date is after all new items in process' open dates
     * 
     * @param trans transaction from cash management document
     * @return true if the current date is after all new items in process' open dates
     */
    public boolean checkNewItemInProcessInPast(CashieringTransaction trans) {
        boolean success = true;
        if (trans.getNewItemInProcess().isPopulated()) {
            if (trans.getNewItemInProcess().getItemOpenDate() != null && convertDateToDayYear(trans.getNewItemInProcess().getItemOpenDate()) > convertDateToDayYear(new Date())) {
                GlobalVariables.getErrorMap().putError("document.currentTransaction.newItemInProcess.itemOpenDate", KFSKeyConstants.CashManagement.ERROR_NEW_ITEM_IN_PROCESS_IN_FUTURE, new String[] {});
                success = false;
            }
        }
        return success;
    }

    /**
     * This method calculates the total cash drawer reserves amount
     * 
     * @param cmDoc
     * @param trans
     * @return KualiDecimal as total from cash drawer reserves
     */
    private KualiDecimal calculateTotalCashDrawerReserves(CashManagementDocument cmDoc, CashieringTransaction trans) {
        KualiDecimal reserves = new KualiDecimal(cmDoc.getCashDrawer().getTotalAmount().bigDecimalValue());
        reserves = reserves.add(trans.getMoneyInCurrency().getTotalAmount());
        reserves = reserves.add(trans.getMoneyInCoin().getTotalAmount());
        return reserves;
    }

    /**
     * This method returns the current day of year as an int for a specific date.
     * 
     * @param d date
     * @return int as day of year
     */
    private int convertDateToDayYear(Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        return cal.get(Calendar.YEAR) * 366 + cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Gets the cashDrawerService attribute.
     * 
     * @return Returns the cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }

    /**
     * Sets the cashDrawerService attribute value.
     * 
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }
}
