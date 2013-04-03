/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants.NRATaxParameters;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.validation.event.PurchasingAccountsPayableItemPreCalculateEvent;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contains a number of helper methods to deal with accounts on Purchasing Accounts Payable Documents
 */

@NonTransactional
public class PurapAccountingServiceImpl implements PurapAccountingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);

    protected static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    protected static final int SCALE = 340;
    protected static final int BIG_DECIMAL_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    protected static final int BIG_DECIMAL_SCALE = 2;

    // local constants
    protected static final Boolean ITEM_TYPES_INCLUDED_VALUE = Boolean.TRUE;;
    protected static final Boolean ITEM_TYPES_EXCLUDED_VALUE = Boolean.FALSE;
    protected static final Boolean ZERO_TOTALS_RETURNED_VALUE = Boolean.TRUE;
    protected static final Boolean ZERO_TOTALS_NOT_RETURNED_VALUE = Boolean.FALSE;
    protected static final Boolean ALTERNATE_AMOUNT_USED = Boolean.TRUE;
    protected static final Boolean ALTERNATE_AMOUNT_NOT_USED = Boolean.FALSE;
    protected static final Boolean USE_TAX_INCLUDED = Boolean.TRUE;
    protected static final Boolean USE_TAX_EXCLUDED = Boolean.FALSE;

    protected ParameterService parameterService;
    protected PurapService purapService;
    protected PurApAccountingDao purApAccountingDao;
    protected BusinessObjectService businessObjectService;

    /**
     * gets the lowest possible number for rounding, it works for ROUND_HALF_UP
     *
     * @return a BigDecimal representing the lowest possible number for rounding
     */
    protected BigDecimal getLowestPossibleRoundUpNumber() {
        BigDecimal startingDigit = new BigDecimal(0.5);
        if (SCALE != 0) {
            startingDigit = startingDigit.movePointLeft(SCALE);
        }
        return startingDigit;
    }

    /**
     * Helper method to log and throw an error
     *
     * @param methodName the method it's coming from
     * @param errorMessage the actual error
     */
    protected void throwRuntimeException(String methodName, String errorMessage) {
        LOG.error(methodName + "  " + errorMessage);
        throw new RuntimeException(errorMessage);
    }

    /**
     * @deprecated
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.Integer)
     */
    @Deprecated
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale) {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.rice.core.api.util.type.KualiDecimal, java.lang.Integer)
     */
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz) {
        String methodName = "generateAccountDistributionForProration()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<PurApAccountingLine> newAccounts = new ArrayList();

        if (totalAmount.isZero()) {
            throwRuntimeException(methodName, "Purchasing/Accounts Payable account distribution for proration does not allow zero dollar total.");
        }

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal totalAmountBigDecimal = totalAmount.bigDecimalValue();
        for (SourceAccountingLine accountingLine : accounts) {
            KualiDecimal amt = KualiDecimal.ZERO;
            if (ObjectUtils.isNotNull(accountingLine.getAmount())) {
                amt = accountingLine.getAmount();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " " + accountingLine.getAccountNumber() + " " + amt + "/" + totalAmountBigDecimal);
            }
            BigDecimal pct = amt.bigDecimalValue().divide(totalAmountBigDecimal, percentScale, BIG_DECIMAL_ROUNDING_MODE);
            pct = pct.stripTrailingZeros().multiply(ONE_HUNDRED);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " pct = " + pct + "  (trailing zeros removed)");
            }

            BigDecimal lowestPossible = this.getLowestPossibleRoundUpNumber();
            if (lowestPossible.compareTo(pct) <= 0) {
                PurApAccountingLine newAccountingLine;
                newAccountingLine = null;

                try {
                    newAccountingLine = (PurApAccountingLine) clazz.newInstance();
                }
                catch (InstantiationException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                PurApObjectUtils.populateFromBaseClass(AccountingLineBase.class, accountingLine, newAccountingLine);
                newAccountingLine.setAccountLinePercent(pct);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
                }
                newAccounts.add(newAccountingLine);
                percentTotal = percentTotal.add(newAccountingLine.getAccountLinePercent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName + " total = " + percentTotal);
                }
            }
        }

        if ((percentTotal.compareTo(BigDecimal.ZERO)) == 0) {
            /*
             * This means there are so many accounts or so strange a distribution that we can't round properly... not sure of viable
             * solution
             */
            throwRuntimeException(methodName, "Can't round properly due to number of accounts");
        }

        // Now deal with rounding
        if ((ONE_HUNDRED.compareTo(percentTotal)) < 0) {
            /*
             * The total percent is greater than one hundred Here we find the account that occurs latest in our list with a percent
             * that is higher than the difference and we subtract off the difference
             */
            BigDecimal difference = percentTotal.subtract(ONE_HUNDRED);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " Rounding up by " + difference);
            }

            boolean foundAccountToUse = false;
            int currentNbr = newAccounts.size() - 1;
            while (currentNbr >= 0) {
                PurApAccountingLine potentialSlushAccount = newAccounts.get(currentNbr);

                BigDecimal linePercent = BigDecimal.ZERO;
                if (ObjectUtils.isNotNull(potentialSlushAccount.getAccountLinePercent())) {
                    linePercent = potentialSlushAccount.getAccountLinePercent();
                }

                if ((difference.compareTo(linePercent)) < 0) {
                    // the difference amount is less than the current accounts percent... use this account
                    // the 'potentialSlushAccount' technically is now the true 'Slush Account'
                    potentialSlushAccount.setAccountLinePercent(linePercent.subtract(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
                    foundAccountToUse = true;
                    break;
                }
                currentNbr--;
            }

            if (!foundAccountToUse) {
                /*
                 * We could not find any account in our list where the percent of that account was greater than that of the
                 * difference... doing so on just any account could result in a negative percent value
                 */
                throwRuntimeException(methodName, "Can't round properly due to math calculation error");
            }

        }
        else if ((ONE_HUNDRED.compareTo(percentTotal)) > 0) {
            /*
             * The total percent is less than one hundred Here we find the last account in our list and add the remaining required
             * percent to its already calculated percent
             */
            BigDecimal difference = ONE_HUNDRED.subtract(percentTotal);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " Rounding down by " + difference);
            }
            PurApAccountingLine slushAccount = newAccounts.get(newAccounts.size() - 1);

            BigDecimal slushLinePercent = BigDecimal.ZERO;
            if (ObjectUtils.isNotNull(slushAccount.getAccountLinePercent())) {
                slushLinePercent = slushAccount.getAccountLinePercent();
            }

            slushAccount.setAccountLinePercent(slushLinePercent.add(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProrationWithZeroTotal(java.util.List,
     *      java.lang.Integer)
     */
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapDoc) {
        String methodName = "generateAccountDistributionForProrationWithZeroTotal()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }

        List<PurApAccountingLine> accounts = generatePercentSummary(purapDoc);

        // find the total percent and strip trailing zeros
        BigDecimal totalPercentValue = BigDecimal.ZERO;
        for (PurApAccountingLine accountingLine : accounts) {
            BigDecimal linePercent = BigDecimal.ZERO;
            if (ObjectUtils.isNotNull(accountingLine.getAccountLinePercent())) {
                linePercent = accountingLine.getAccountLinePercent();
            }

            totalPercentValue = totalPercentValue.add(linePercent).movePointLeft(2).stripTrailingZeros().movePointRight(2);
        }

        if ((BigDecimal.ZERO.compareTo(totalPercentValue.remainder(ONE_HUNDRED))) != 0) {
            throwRuntimeException(methodName, "Invalid Percent Total of '" + totalPercentValue + "' does not allow for account distribution (must be multiple of 100)");
        }

        List newAccounts = new ArrayList();
        BigDecimal logDisplayOnlyTotal = BigDecimal.ZERO;
        BigDecimal percentUsed = BigDecimal.ZERO;
        int accountListSize = accounts.size();
        int i = 0;
        for (PurApAccountingLine accountingLine : accounts) {
            i++;
            BigDecimal percentToUse = BigDecimal.ZERO;
            KualiDecimal amt = KualiDecimal.ZERO;

            if (ObjectUtils.isNotNull(accountingLine.getAmount())) {
                amt = accountingLine.getAmount();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " " + accountingLine.getChartOfAccountsCode() + "-" + accountingLine.getAccountNumber() + " " + amt + "/" + percentToUse);
            }

            // if it's the last account make up the leftover percent
            BigDecimal acctPercent = BigDecimal.ZERO;
            if (ObjectUtils.isNotNull(accountingLine.getAccountLinePercent())) {
                acctPercent = accountingLine.getAccountLinePercent();
            }

            if ((i != accountListSize) || (accountListSize == 1)) {
                // this account is not the last account or there is only one account
                percentToUse = (acctPercent.divide(totalPercentValue, SCALE, BIG_DECIMAL_ROUNDING_MODE)).multiply(ONE_HUNDRED);
                percentUsed = percentUsed.add(((acctPercent.divide(totalPercentValue, SCALE, BIG_DECIMAL_ROUNDING_MODE))).multiply(ONE_HUNDRED));
            }
            else {
                // this account is the last account so we have to makeup whatever is left out of 100
                percentToUse = ONE_HUNDRED.subtract(percentUsed);
            }

            PurApAccountingLine newAccountingLine = accountingLine.createBlankAmountsCopy();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " pct = " + percentToUse);
            }
            newAccountingLine.setAccountLinePercent(percentToUse.setScale(acctPercent.scale(), BIG_DECIMAL_ROUNDING_MODE));
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
            }
            newAccounts.add(newAccountingLine);
            logDisplayOnlyTotal = logDisplayOnlyTotal.add(newAccountingLine.getAccountLinePercent());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " total = " + logDisplayOnlyTotal);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummary(java.util.List)
     */
    @Override
    public List<SourceAccountingLine> generateSummary(List<PurApItem> items) {
        String methodName = "generateSummary()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    @Override
    public List<SourceAccountingLine> generateSummaryTaxableAccounts(List<PurApItem> items) {
        String methodName = "generateSummary()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, true);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccounts(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public List<SummaryAccount> generateSummaryAccounts(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_RETURNED_VALUE, USE_TAX_INCLUDED);
    }


    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccountsWithNoZeroTotals(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_NOT_RETURNED_VALUE, USE_TAX_INCLUDED);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccountsWithNoZeroTotals(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_NOT_RETURNED_VALUE, USE_TAX_EXCLUDED);
    }

    /**
     * This creates summary accounts based on a list of items.
     *
     * @param items a list of PurAp Items.
     * @return a list of summary accounts.
     */
    protected List<SummaryAccount> generateSummaryAccounts(List<PurApItem> items, Boolean useZeroTotals, Boolean useTaxIncluded) {
        String methodName = "generateSummaryAccounts()";
        List<SummaryAccount> returnList = new ArrayList<SummaryAccount>();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }

        List<SourceAccountingLine> sourceLines = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, useZeroTotals, ALTERNATE_AMOUNT_NOT_USED, useTaxIncluded, false);
        for (SourceAccountingLine sourceAccountingLine : sourceLines) {
            SummaryAccount summaryAccount = new SummaryAccount();
            summaryAccount.setAccount((SourceAccountingLine) ObjectUtils.deepCopy(sourceAccountingLine));
            for (PurApItem item : items) {
                List<PurApAccountingLine> itemAccounts = item.getSourceAccountingLines();
                for (PurApAccountingLine purApAccountingLine : itemAccounts) {
                    if (purApAccountingLine.accountStringsAreEqual(summaryAccount.getAccount())) {
                        PurApSummaryItem summaryItem = item.getSummaryItem();
                        // If the summaryItem is null, it means the item is not eligible to
                        // be displayed in the Account Summary tab. If it's not null then
                        // we'll set the estimatedEncumberanceAmount and add the item to the
                        // summaryAccount list to be displayed in the Account Summary tab.
                        KualiDecimal amt = KualiDecimal.ZERO;
                        if (ObjectUtils.isNotNull(purApAccountingLine.getAmount())) {
                            amt = purApAccountingLine.getAmount();
                        }
                        if (summaryItem != null) {
                            summaryItem.setEstimatedEncumberanceAmount(amt);
                            summaryAccount.getItems().add(summaryItem);
                            break;
                        }
                    }

                }
            }
            returnList.add(summaryAccount);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotals(java.util.List)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * calls generateSummary with no use tax included
     */
    @Override
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotalsNoUseTax()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_EXCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }

        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotalsUsingAlternateAmount(java.util.List)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypes(java.util.List, java.util.Set)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypes()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypesAndNoZeroTotals()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypes(java.util.List, java.util.Set)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypes()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    @Override
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypesAndNoZeroTotals()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * Generates an account summary, that is it creates a list of source accounts by rounding up the purap accounts off of the purap
     * items.
     *
     * @param items the items to determ
     * @param itemTypeCodes the item types to determine whether to look at an item in combination with itemTypeCodesAreIncluded
     * @param itemTypeCodesAreIncluded value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     * @param useZeroTotals whether to include items with a zero dollar total
     * @param useAlternateAmount an alternate amount used in certain cases for GL entry
     * @return a list of source accounts
     */
    protected List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set<String> itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals, Boolean useAlternateAmount, Boolean useTaxIncluded, Boolean taxableOnly) {
        List<PurApItem> itemsToProcess = getProcessablePurapItems(items, itemTypeCodes, itemTypeCodesAreIncluded, useZeroTotals);
        Map<PurApAccountingLine, KualiDecimal> accountMap = new HashMap<PurApAccountingLine, KualiDecimal>();

        for (PurApItem currentItem : itemsToProcess) {
            if (PurApItemUtils.checkItemActive(currentItem)) {
                List<PurApAccountingLine> sourceAccountingLines = currentItem.getSourceAccountingLines();

                // skip if item is not taxable and taxable only flag has been set
                if (taxableOnly) {
                    PurchasingAccountsPayableDocument document = currentItem.getPurapDocument();
                    if (!purapService.isTaxableForSummary(document.isUseTaxIndicator(), purapService.getDeliveryState(document), currentItem)) {
                        continue;
                    }
                }

                if (!useTaxIncluded) {
                    // if no use tax set the source accounting lines to a clone so we can update
                    // them to be based on the non tax amount
                    PurApItem cloneItem = (PurApItem) ObjectUtils.deepCopy(currentItem);
                    sourceAccountingLines = cloneItem.getSourceAccountingLines();
                    updateAccountAmountsWithTotal(sourceAccountingLines, currentItem.getTotalRemitAmount());
                }

                for (PurApAccountingLine account : sourceAccountingLines) {

                    // skip account if not taxable and taxable only flag is set
                    if (taxableOnly) {
                        PurchasingAccountsPayableDocument document = currentItem.getPurapDocument();
                        // check if account is not taxable, if not skip this account
                        if (!purapService.isAccountingLineTaxable(account, purapService.isDeliveryStateTaxable(purapService.getDeliveryState(document)))) {
                            continue;
                        }
                    }

                    // getting the total to set on the account
                    KualiDecimal total = KualiDecimal.ZERO;
                    if (accountMap.containsKey(account)) {
                        total = accountMap.get(account);
                    }

                    if (useAlternateAmount) {
                        total = total.add(account.getAlternateAmountForGLEntryCreation());
                    }
                    else {
                        if (ObjectUtils.isNotNull(account.getAmount())) {
                            total = total.add(account.getAmount());
                        }
                    }

                    accountMap.put(account, total);
                }
            }
        }

        // convert list of PurApAccountingLine objects to SourceAccountingLineObjects
        Iterator<PurApAccountingLine> iterator = accountMap.keySet().iterator();
        List<SourceAccountingLine> sourceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator<PurApAccountingLine> iter = iterator; iter.hasNext();) {
            PurApAccountingLine accountToConvert = iter.next();
            if (accountToConvert.isEmpty()) {
                String errorMessage = "Found an 'empty' account in summary generation " + accountToConvert.toString();
                LOG.error("generateAccountSummary() " + errorMessage);
                throw new RuntimeException(errorMessage);
            }
            KualiDecimal sourceLineTotal = accountMap.get(accountToConvert);
            SourceAccountingLine sourceLine = accountToConvert.generateSourceAccountingLine();
            sourceLine.setAmount(sourceLineTotal);
            sourceAccounts.add(sourceLine);
        }

        // sort the sourceAccounts list first by account number, then by object code, ignoring chart code
        Collections.sort(sourceAccounts, new Comparator<SourceAccountingLine>() {
            @Override
            public int compare(SourceAccountingLine sal1, SourceAccountingLine sal2) {
                int compare = 0;
                if (sal1 != null && sal2 != null) {
                    if (sal1.getAccountNumber() != null && sal2.getAccountNumber() != null) {
                        compare = sal1.getAccountNumber().compareTo(sal2.getAccountNumber());
                        if (compare == 0) {
                            if (sal1.getFinancialObjectCode() != null && sal2.getFinancialObjectCode() != null) {
                                compare = sal1.getFinancialObjectCode().compareTo(sal2.getFinancialObjectCode());
                            }
                        }
                    }
                }
                return compare;
            }
        });

        return sourceAccounts;
    }

    /**
     * This method takes a list of {@link PurchasingApItem} objects and parses through them to see if each one should be processed
     * according the the other variables passed in.<br>
     * <br>
     * Example 1:<br>
     * items = "ITEM", "SITM", "FRHT", "SPHD"<br>
     * itemTypeCodes = "FRHT"<br>
     * itemTypeCodesAreIncluded = ITEM_TYPES_EXCLUDED_VALUE<br>
     * return items "ITEM", "SITM", "FRHT", "SPHD"<br>
     * <br>
     * <br>
     * Example 2:<br>
     * items = "ITEM", "SITM", "FRHT", "SPHD"<br>
     * itemTypeCodes = "ITEM","FRHT"<br>
     * itemTypeCodesAreIncluded = ITEM_TYPES_INCLUDED_VALUE<br>
     * return items "ITEM", "FRHT"<br>
     *
     * @param items - list of {@link PurchasingApItem} objects that need to be parsed
     * @param itemTypeCodes - list of {@link org.kuali.kfs.module.purap.businessobject.ItemType} codes used in conjunction with
     *        itemTypeCodesAreIncluded parameter
     * @param itemTypeCodesAreIncluded - value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     *        (see {@link #ITEM_TYPES_INCLUDED_VALUE})
     * @param useZeroTotals - value to tell whether to include zero dollar items (see {@link #ZERO_TOTALS_RETURNED_VALUE})
     * @return a list of {@link PurchasingApItem} objects that should be used for processing by calling method
     */
    protected List<PurApItem> getProcessablePurapItems(List<PurApItem> items, Set itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals) {
        String methodName = "getProcessablePurapItems()";
        List<PurApItem> newItemList = new ArrayList<PurApItem>();
        // error out if we have an invalid 'itemTypeCodesAreIncluded' value
        if ((!(ITEM_TYPES_INCLUDED_VALUE.equals(itemTypeCodesAreIncluded))) && (!(ITEM_TYPES_EXCLUDED_VALUE.equals(itemTypeCodesAreIncluded)))) {
            throwRuntimeException(methodName, "Invalid parameter found while trying to find processable items for dealing with purchasing/accounts payable accounts");
        }
        for (PurApItem currentItem : items) {
            if ((itemTypeCodes != null) && (!(itemTypeCodes.isEmpty()))) {
                // we have at least one entry in our item type code list
                boolean foundMatchInList = false;
                // check to see if this item type code is in the list
                for (Iterator iterator = itemTypeCodes.iterator(); iterator.hasNext();) {
                    String itemTypeCode = (String) iterator.next();
                    // include this item if it's in the included list
                    if (itemTypeCode.equals(currentItem.getItemType().getItemTypeCode())) {
                        foundMatchInList = true;
                        break;
                    }
                }
                // check to see if item type code was found and if the list is describing included or excluded item types
                if ((foundMatchInList) && (ITEM_TYPES_EXCLUDED_VALUE.equals(itemTypeCodesAreIncluded))) {
                    // this item type code is in the list
                    // this item type code is excluded so we skip it
                    continue; // skips current item
                }
                else if ((!foundMatchInList) && (ITEM_TYPES_INCLUDED_VALUE.equals(itemTypeCodesAreIncluded))) {
                    // this item type code is not in the list
                    // this item type code is not included so we skip it
                    continue; // skips current item
                }
            }
            else {
                // the item type code list is empty
                if (ITEM_TYPES_INCLUDED_VALUE.equals(itemTypeCodesAreIncluded)) {
                    // the item type code list is empty and the list is supposed to contain the item types to include
                    throwRuntimeException(methodName, "Invalid parameter and list of items found while trying to find processable items for dealing with purchasing/accounts payable accounts");
                }
            }
            if ((ZERO_TOTALS_NOT_RETURNED_VALUE.equals(useZeroTotals)) && (ObjectUtils.isNull(currentItem.getExtendedPrice()) || ((KualiDecimal.ZERO.compareTo(currentItem.getExtendedPrice())) == 0))) {
                // if we don't return zero dollar items then skip this one
                continue;
            }
            newItemList.add(currentItem);
        }
        return newItemList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updateAccountAmounts(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document) {

        PurchasingAccountsPayableDocumentBase purApDocument = (PurchasingAccountsPayableDocumentBase) document;
        String accountDistributionMethod = purApDocument.getAccountDistributionMethod();

        KualiRuleService kualiRuleService = SpringContext.getBean(KualiRuleService.class);

        // the percent at fiscal approve
        // don't update if past the AP review level
        if ((document instanceof PaymentRequestDocument) && purapService.isFullDocumentEntryCompleted(document)) {
            // update the percent but don't update the amounts if preq and past full entry
            convertMoneyToPercent((PaymentRequestDocument) document);
            return;
        }
        document.fixItemReferences();

      //if distribution method is sequential and document is PREQ or VCM then...
        if (((document instanceof PaymentRequestDocument) || (document instanceof VendorCreditMemoDocument)) && PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            if (document instanceof VendorCreditMemoDocument) {
                VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) document;
                cmDocument.updateExtendedPriceOnItems();
            }

            // update the accounts amounts for PREQ and distribution method = sequential
            for (PurApItem item : document.getItems()) {
                updatePreqItemAccountAmounts(item);
            }

            return;
        }

        //if distribution method is proportional and document is PREQ or VCM then...
        if (((document instanceof PaymentRequestDocument) || (document instanceof VendorCreditMemoDocument)) && PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            // update the accounts amounts for PREQ and distribution method = sequential
            if (document instanceof VendorCreditMemoDocument) {
                VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) document;
                cmDocument.updateExtendedPriceOnItems();
            }

            for (PurApItem item : document.getItems()) {
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= kualiRuleService.applyRules(new PurchasingAccountsPayableItemPreCalculateEvent(document, item));

                if (rulePassed) {
                    updatePreqProportionalItemAccountAmounts(item);
                }
            }

            return;
        }

        // Recalculate if the account distribution method code is equal to "S" sequential ON REQ or POs..
        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            for (PurApItem item : document.getItems()) {
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= kualiRuleService.applyRules(new PurchasingAccountsPayableItemPreCalculateEvent(document, item));

                //  Calculate the amount on account line.
                if (rulePassed) {
                    updatePreqProportionalItemAccountAmounts(item);
                }
                return;
            }
        }

        //do recalculate only if the account distribution method code is not equal to "S" sequential method.
        if (!PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            for (PurApItem item : document.getItems()) {
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= kualiRuleService.applyRules(new PurchasingAccountsPayableItemPreCalculateEvent(document, item));

                if (rulePassed) {
                    updateItemAccountAmounts(item);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updateItemAccountAmounts(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public void updateItemAccountAmounts(PurApItem item) {
        List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
        KualiDecimal totalAmount = item.getTotalAmount();
        updateAccountAmountsWithTotal(sourceAccountingLines, totalAmount);
    }

    /**
     * calculates values for a list of accounting lines based on an amount
     *
     * @param sourceAccountingLines
     * @param totalAmount
     */
    @Override
    public <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount) {
        updateAccountAmountsWithTotal(sourceAccountingLines, totalAmount, new KualiDecimal(0));
    }

    /**
     * calculates values for a list of accounting lines based on an amount taking discount into account
     *
     * @param sourceAccountingLines
     * @param totalAmount
     * @param discountAmount
     */
    @Override
    public <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount, KualiDecimal discountAmount) {

        // if we have a discount, then we need to base the amounts on the discount, but the percent on the total
        boolean noDiscount = true;
        if ((discountAmount != null) && KualiDecimal.ZERO.compareTo(discountAmount) != 0) {
            noDiscount = false;
        }


        if ((totalAmount != null) && KualiDecimal.ZERO.compareTo(totalAmount) != 0) {

            KualiDecimal accountTotal = KualiDecimal.ZERO;
            BigDecimal accountTotalPercent = BigDecimal.ZERO;
            T lastAccount = null;

            for (T account : sourceAccountingLines) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent()) || ObjectUtils.isNotNull(account.getAmount())) {
                    if (ObjectUtils.isNotNull(account.getAmount()) && account.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                        KualiDecimal amt = account.getAmount();
                        KualiDecimal calculatedPercent = new KualiDecimal(amt.multiply(new KualiDecimal(100)).divide(totalAmount).toString());
                        account.setAccountLinePercent(calculatedPercent.bigDecimalValue().setScale(BIG_DECIMAL_SCALE));
                    }

                    if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                        BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                        if (noDiscount) {
                            if (ObjectUtils.isNull(account.getAmount()) || account.getAmount().isZero()) {
                                account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(totalAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
                            }
                        } else {
                            account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(discountAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
                        }
                    }
                }

                if (ObjectUtils.isNotNull(account.getAmount())) {
                    accountTotal = accountTotal.add(account.getAmount());
                }
                if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                    accountTotalPercent = accountTotalPercent.add(account.getAccountLinePercent());
                }

                lastAccount = account;
            }

            // put excess on last account
            if (lastAccount != null) {
                KualiDecimal difference = new KualiDecimal(0);
                if (noDiscount) {
                    difference = totalAmount.subtract(accountTotal);
                } else {
                    difference = discountAmount.subtract(accountTotal);
                }
                if (ObjectUtils.isNotNull(lastAccount.getAmount())) {
                    lastAccount.setAmount(lastAccount.getAmount().add(difference));
                }

                BigDecimal percentDifference = new BigDecimal(100).subtract(accountTotalPercent).setScale(BIG_DECIMAL_SCALE);
                if (ObjectUtils.isNotNull(lastAccount.getAccountLinePercent())) {
                    lastAccount.setAccountLinePercent(lastAccount.getAccountLinePercent().add(percentDifference));
                }
            }
        }
        else {
            // zero out if extended price is zero
            for (T account : sourceAccountingLines) {
                account.setAmount(KualiDecimal.ZERO);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updatePreqProportionalItemAccountAmounts(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public void updatePreqProportionalItemAccountAmounts(PurApItem item) {
        List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
        KualiDecimal totalAmount = item.getTotalAmount();

        updatePreqProporationalAccountAmountsWithTotal(sourceAccountingLines, totalAmount);
    }

    /**
     * calculates values for a list of accounting lines based on an amount for proportional method
     *
     * @param sourceAccountingLines
     * @param totalAmount
     */
    @Override
    public <T extends PurApAccountingLine> void updatePreqProporationalAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount) {
        if ((totalAmount != null) && KualiDecimal.ZERO.compareTo(totalAmount) != 0) {
            KualiDecimal accountTotal = KualiDecimal.ZERO;
            BigDecimal accountTotalPercent = BigDecimal.ZERO;
            T lastAccount = null;

            for (T account : sourceAccountingLines) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent()) || ObjectUtils.isNotNull(account.getAmount())) {
                    if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                        BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                        account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(totalAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
                    }
                }

                if (ObjectUtils.isNotNull(account.getAmount())) {
                    accountTotal = accountTotal.add(account.getAmount());
                }
                if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                    accountTotalPercent = accountTotalPercent.add(account.getAccountLinePercent());
                }

                lastAccount = account;
            }

            // put excess on last account
            if (lastAccount != null) {
                KualiDecimal difference = totalAmount.subtract(accountTotal);
                if (ObjectUtils.isNotNull(lastAccount.getAmount())) {
                    lastAccount.setAmount(lastAccount.getAmount().add(difference));
                }

                BigDecimal percentDifference = new BigDecimal(100).subtract(accountTotalPercent).setScale(BIG_DECIMAL_SCALE);
                if (ObjectUtils.isNotNull(lastAccount.getAccountLinePercent())) {
                    lastAccount.setAccountLinePercent(lastAccount.getAccountLinePercent().add(percentDifference));
                }
            }
        } else {
            for (T account : sourceAccountingLines) {
                account.setAmount(KualiDecimal.ZERO);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updatePreqItemAccountAmounts(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public void updatePreqItemAccountAmounts(PurApItem item) {
        List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
        KualiDecimal totalAmount = item.getTotalAmount();

        updatePreqAccountAmountsWithTotal(sourceAccountingLines, totalAmount);
    }

    /**
     * calculates values for a list of accounting lines based on an amount.  Preq item's extended
     * cost is distributed to the accounting lines.
     *
     * @param sourceAccountingLines
     * @param totalAmount
     */
    @Override
    public <T extends PurApAccountingLine> void updatePreqAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount) {
        if ((totalAmount != null) && KualiDecimal.ZERO.compareTo(totalAmount) != 0) {
            KualiDecimal accountTotal = KualiDecimal.ZERO;
            BigDecimal accountTotalPercent = BigDecimal.ZERO;
            T lastAccount = null;

            for (T account : sourceAccountingLines) {
                //look at lines where amount is non-zero..
                if (account.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    if (totalAmount.isZero()) {
                        account.setAmount(KualiDecimal.ZERO);
                    } else {
                        if (account.getAmount().isGreaterThan(totalAmount)) {
                            account.setAmount(totalAmount);
                        }
                    }
                }

                totalAmount = totalAmount.subtract(account.getAmount());
            }

            if (totalAmount.isGreaterThan(KualiDecimal.ZERO)) {
                for (T account : sourceAccountingLines) {
                    if (account.getAmount().isZero() || account.getAccountLinePercent().compareTo(BigDecimal.ZERO) == 1) {
                        KualiDecimal priorAmount = account.getAmount();
                        account.setAmount(account.getAmount().add(new KualiDecimal(account.getAccountLinePercent()).multiply(totalAmount).divide(new KualiDecimal(100))));
                        accountTotal = accountTotal.add(account.getAmount().subtract(priorAmount));
                        lastAccount = account;
                    }
                }
            }

            accountTotal = totalAmount.subtract(accountTotal);

            if (accountTotal.isGreaterThan(KualiDecimal.ZERO) && ObjectUtils.isNotNull(lastAccount)) {
                //add the difference to the last overage account....
                lastAccount.setAmount(lastAccount.getAmount().add(accountTotal));
            }
        } else {
            for (T account : sourceAccountingLines) {
                account.setAmount(KualiDecimal.ZERO);
            }
        }
    }

    public List<PurApAccountingLine> generatePercentSummary(PurchasingAccountsPayableDocument purapDoc) {
        List<PurApAccountingLine> accounts = new ArrayList<PurApAccountingLine>();
        for (PurApItem currentItem : purapDoc.getItems()) {
            if (PurApItemUtils.checkItemActive(currentItem)) {
                for (PurApAccountingLine account : currentItem.getSourceAccountingLines()) {
                    boolean thisAccountAlreadyInSet = false;
                    for (Iterator iter = accounts.iterator(); iter.hasNext();) {
                        PurApAccountingLine alreadyAddedAccount = (PurApAccountingLine) iter.next();
                        if (alreadyAddedAccount.accountStringsAreEqual(account)) {
                            BigDecimal alreadyAddedAccountLinePercent = BigDecimal.ZERO;
                            if (ObjectUtils.isNotNull(alreadyAddedAccount.getAccountLinePercent())) {
                                alreadyAddedAccountLinePercent = alreadyAddedAccount.getAccountLinePercent();
                            }
                            BigDecimal accountLinePercent = BigDecimal.ZERO;
                            if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                                accountLinePercent = account.getAccountLinePercent();
                            }

                            alreadyAddedAccount.setAccountLinePercent(alreadyAddedAccountLinePercent.add(accountLinePercent));

                            thisAccountAlreadyInSet = true;
                            break;
                        }
                    }
                    if (!thisAccountAlreadyInSet) {
                        PurApAccountingLine accountToAdd = (PurApAccountingLine) ObjectUtils.deepCopy(account);
                        accounts.add(accountToAdd);
                    }
                }
            }
        }
        return accounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#convertMoneyToPercent(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void convertMoneyToPercent(PaymentRequestDocument pr) {
        LOG.debug("convertMoneyToPercent() started");

        int itemNbr = 0;

        for (Iterator<PaymentRequestItem> iter = pr.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = iter.next();

            itemNbr++;
            String identifier = item.getItemIdentifierString();

            if (item.getTotalAmount() != null && item.getTotalAmount().isNonZero()) {
                int numOfAccounts = item.getSourceAccountingLines().size();
                BigDecimal percentTotal = BigDecimal.ZERO;
                BigDecimal percentTotalRoundUp = BigDecimal.ZERO;
                KualiDecimal accountTotal = KualiDecimal.ZERO;
                int accountIdentifier = 0;

                for (Iterator<PurApAccountingLine> iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                    accountIdentifier++;
                    PaymentRequestAccount account = (PaymentRequestAccount) iterator.next();

                    // account.getAmount returns the wrong value for trade in source accounting lines...
                    KualiDecimal accountAmount = KualiDecimal.ZERO;
                    if (ObjectUtils.isNotNull(account.getAmount())) {
                        accountAmount = account.getAmount();
                    }

                    BigDecimal tmpPercent = BigDecimal.ZERO;
                    KualiDecimal extendedPrice = item.getTotalAmount();
                    tmpPercent = accountAmount.bigDecimalValue().divide(extendedPrice.bigDecimalValue(), PurapConstants.CREDITMEMO_PRORATION_SCALE.intValue(), KualiDecimal.ROUND_BEHAVIOR);

                    if (accountIdentifier == numOfAccounts) {
                        // if on last account, calculate the percent by subtracting current percent total from 1
                        tmpPercent = BigDecimal.ONE.subtract(percentTotal);
                    }

                    // test that the above amount is correct, if so just check that the total of all these matches the item total
                    BigDecimal calcAmountBd = tmpPercent.multiply(extendedPrice.bigDecimalValue());
                    calcAmountBd = calcAmountBd.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR);
                    KualiDecimal calcAmount = new KualiDecimal(calcAmountBd);
                    if (calcAmount.compareTo(accountAmount) != 0) {
                        // rounding error
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("convertMoneyToPercent() Rounding error on " + account);
                        }
                        String param1 = identifier + "." + accountIdentifier;
                        String param2 = calcAmount.bigDecimalValue().subtract(accountAmount.bigDecimalValue()).toString();
                        GlobalVariables.getMessageMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_ROUNDING, param1, param2);
                        account.setAmount(calcAmount);
                    }

                    // update percent
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("convertMoneyToPercent() updating percent to " + tmpPercent);
                    }
                    account.setAccountLinePercent(tmpPercent.multiply(new BigDecimal(100)));
                    // handle 33.33% issue
                    if (accountIdentifier == numOfAccounts) {
                        account.setAccountLinePercent(new BigDecimal(100).subtract(percentTotalRoundUp));
                    }

                    // check total based on adjusted amount
                    accountTotal = accountTotal.add(calcAmount);
                    percentTotal = percentTotal.add(tmpPercent);
                    percentTotalRoundUp = percentTotalRoundUp.add(account.getAccountLinePercent());
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#deleteSummaryAccounts(java.lang.Integer, java.lang.String)
     */
    @Override
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier, String docType) {
        if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            purApAccountingDao.deleteSummaryAccountsbyPaymentRequestIdentifier(purapDocumentIdentifier);
        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            purApAccountingDao.deleteSummaryAccountsbyCreditMemoIdentifier(purapDocumentIdentifier);
        }
    }

    @Override
    public List getAccountsPayableSummaryAccounts(Integer purapDocumentIdentifier, String docType) {
        if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            return getSummaryAccountsbyPaymentRequestIdentifier(purapDocumentIdentifier);
        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            getSummaryAccountsbyCreditMemoIdentifier(purapDocumentIdentifier);
        }
        return null;
    }

    @Override
    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item) {
        return purApAccountingDao.getAccountingLinesForItem(item);
    }

    @Override
    public List<SourceAccountingLine> generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document) {
        // correct initial amounts or percents
        updateAccountAmounts(document);
        List<SourceAccountingLine> vendorSummaryAccounts = new ArrayList<SourceAccountingLine>();

        // update accounts here with amounts to send to vendor
        vendorSummaryAccounts = generateSummaryWithNoZeroTotalsNoUseTax(document.getItems());

        return vendorSummaryAccounts;
    }

    /**
     * gets sum total of accounts
     *
     * @param accounts
     * @return
     */

    protected KualiDecimal calculateSumTotal(List<SourceAccountingLine> accounts) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (SourceAccountingLine accountingLine : accounts) {
            KualiDecimal amt = KualiDecimal.ZERO;
            if (ObjectUtils.isNotNull(accountingLine.getAmount())) {
                amt = accountingLine.getAmount();
            }
            total = total.add(amt);
        }
        return total;
    }

    /**
     * Replaces amount field with prorated tax amount in list
     *
     * @param accounts list of accounts
     * @param useTax tax to be allocated to these accounts
     * @param newSourceLines rewrites the source account lines
     */

    protected void convertAmtToTax(List<PurApAccountingLine> accounts, KualiDecimal useTax, List<SourceAccountingLine> newSourceLines) {
        final BigDecimal HUNDRED = new BigDecimal(100);
        PurApAccountingLine purApAccountingLine;
        BigDecimal proratedAmtBD;
        KualiDecimal proratedAmt;
        // convert back to source
        KualiDecimal total = KualiDecimal.ZERO;
        int last = accounts.size() - 1;
        for (int i = 0; i < last; i++) {
            purApAccountingLine = accounts.get(i);
            BigDecimal linePercent = BigDecimal.ZERO;
            if (ObjectUtils.isNotNull(purApAccountingLine.getAccountLinePercent())) {
                linePercent = purApAccountingLine.getAccountLinePercent();
            }

            proratedAmtBD = useTax.bigDecimalValue().multiply(linePercent);
            // last object takes the rest of the amount
            // proratedAmt = (accounts.indexOf(purApAccountingLine) == last) ? useTax.subtract(total) : proratedAmt.divide(HUNDRED);
            proratedAmtBD = proratedAmtBD.divide(HUNDRED);
            proratedAmt = new KualiDecimal(proratedAmtBD);
            SourceAccountingLine acctLine = purApAccountingLine.generateSourceAccountingLine();
            acctLine.setAmount(proratedAmt);
            newSourceLines.add(acctLine);
            total = total.add(proratedAmt);
        }
        // update last object with remaining balance
        proratedAmt = useTax.subtract(total);
        purApAccountingLine = accounts.get(last);
        SourceAccountingLine acctLine = purApAccountingLine.generateSourceAccountingLine();
        acctLine.setAmount(proratedAmt);
        newSourceLines.add(acctLine);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateUseTaxAccount(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public List<UseTaxContainer> generateUseTaxAccount(PurchasingAccountsPayableDocument document) {
        List<UseTaxContainer> useTaxAccounts = new ArrayList<UseTaxContainer>();

        HashMap<PurApItemUseTax, UseTaxContainer> useTaxItemMap = new HashMap<PurApItemUseTax, UseTaxContainer>();
        Class accountingLineClass = null;
        if (!document.isUseTaxIndicator()) {
            // not useTax, return
            return useTaxAccounts;
        }
        for (PurApItem purApItem : document.getItems()) {
            if (!purApItem.getUseTaxItems().isEmpty()) {
                if (accountingLineClass == null) {
                    accountingLineClass = purApItem.getAccountingLineClass();
                }
                UseTaxContainer useTaxContainer = new UseTaxContainer();
                for (PurApItemUseTax itemUseTax : purApItem.getUseTaxItems()) {
                    if (useTaxItemMap.containsKey(itemUseTax)) {
                        useTaxContainer = useTaxItemMap.get(itemUseTax);
                        PurApItemUseTax exisitingItemUseTax = useTaxContainer.getUseTax();
                        // if already in set we need to add on the old amount
                        KualiDecimal tax = exisitingItemUseTax.getTaxAmount();
                        tax = tax.add(itemUseTax.getTaxAmount());
                        exisitingItemUseTax.setTaxAmount(tax);

                        List<PurApItem> items = useTaxContainer.getItems();
                        items.add(purApItem);
                        useTaxContainer.setItems(items);

                    }
                    else {
                        useTaxContainer = new UseTaxContainer(itemUseTax, purApItem);
                        useTaxItemMap.put(itemUseTax, useTaxContainer);
                        useTaxAccounts.add(useTaxContainer);
                    }
                }
            }
        }
        // iterate over useTaxAccounts and set summary accounts using proration
        for (UseTaxContainer useTaxContainer : useTaxAccounts) {

            // create summary from items
            List<SourceAccountingLine> origSourceAccounts = this.generateSummaryWithNoZeroTotals(useTaxContainer.getItems());
            KualiDecimal totalAmount = calculateSumTotal(origSourceAccounts);
            List<PurApAccountingLine> accountingLines = generateAccountDistributionForProration(origSourceAccounts, totalAmount, PurapConstants.PRORATION_SCALE, accountingLineClass);


            List<SourceAccountingLine> newSourceLines = new ArrayList<SourceAccountingLine>();
            // convert back to source
            convertAmtToTax(accountingLines, useTaxContainer.getUseTax().getTaxAmount(), newSourceLines);

            // do we need an update accounts here?
            useTaxContainer.setAccounts(newSourceLines);
        }

        useTaxAccounts = new ArrayList<UseTaxContainer>(useTaxItemMap.values());
        return useTaxAccounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#isTaxAccount(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument,
     *      org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public boolean isTaxAccount(PurchasingAccountsPayableDocument document, SourceAccountingLine account) {
        boolean isTaxAccount = false;

        // check if the summary account is for tax withholding
        if (document instanceof PaymentRequestDocument) {
            String incomeClassCode = ((PaymentRequestDocument) document).getTaxClassificationCode();
            if (StringUtils.isNotEmpty(incomeClassCode)) {

                String federalChartCode = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                String federalAccountNumber = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                String federalObjectCode = parameterService.getSubParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                String stateChartCode = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                String stateAccountNumber = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                String stateObjectCode = parameterService.getSubParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                String chartCode = account.getChartOfAccountsCode();
                String accountNumber = account.getAccountNumber();
                String objectCode = account.getFinancialObjectCode();

                boolean isFederalAccount = StringUtils.equals(federalChartCode, chartCode);
                isFederalAccount &= StringUtils.equals(federalAccountNumber, accountNumber);
                isFederalAccount &= StringUtils.equals(federalObjectCode, objectCode);

                boolean isStateAccount = StringUtils.equals(stateChartCode, chartCode);
                isStateAccount &= StringUtils.equals(stateAccountNumber, accountNumber);
                isStateAccount &= StringUtils.equals(stateObjectCode, objectCode);

                isTaxAccount = isFederalAccount || isStateAccount;
            }
        }

        return isTaxAccount;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPurApAccountingDao(PurApAccountingDao purApAccountingDao) {
        this.purApAccountingDao = purApAccountingDao;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    @Override
    public List<SourceAccountingLine> mergeAccountingLineLists(List<SourceAccountingLine> accountingLines1, List<SourceAccountingLine> accountingLines2) {

        KualiDecimal totalAmount = KualiDecimal.ZERO;
        List<SourceAccountingLine> mergedAccountList = new ArrayList();

        for (SourceAccountingLine line1 : accountingLines1) {
            KualiDecimal line1Amount = KualiDecimal.ZERO;
            if (ObjectUtils.isNotNull(line1.getAmount())) {
                line1Amount = line1.getAmount();
            }

            for (SourceAccountingLine line2 : accountingLines2) {
                KualiDecimal line2Amount = KualiDecimal.ZERO;
                if (ObjectUtils.isNotNull(line2.getAmount())) {
                    line2Amount = line2.getAmount();
                }

                // if we find a match between lists, then merge amounts
                if (line1.equals(line2)) {
                    // add the two amounts
                    totalAmount = line1Amount.add(line2Amount);
                    line1.setAmount(totalAmount);
                }
            }

            mergedAccountList.add(line1);
        }

        return mergedAccountList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#getSummaryAccountsbyPaymentRequestIdentifier(java.lang.Integer)
     */
    @Override
    public List getSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        if (paymentRequestIdentifier != null) {
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.PAYMENT_REQUEST_ID, paymentRequestIdentifier);
            return new ArrayList(businessObjectService.findMatching(AccountsPayableSummaryAccount.class, fieldValues));
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#getSummaryAccountsbyCreditMemoIdentifier(java.lang.Integer)
     */
    @Override
    public List getSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier) {
        if (creditMemoIdentifier != null) {
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.CREDIT_MEMO_ID, creditMemoIdentifier);
            return new ArrayList(businessObjectService.findMatching(AccountsPayableSummaryAccount.class, fieldValues));
        }
        return null;
    }

    /**
     * Sest the businessObjectService.
     *
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
