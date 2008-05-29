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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurApSummaryItem;
import org.kuali.module.purap.dao.PurApAccountingDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.module.purap.util.PurApObjectUtils;
import org.kuali.module.purap.util.SummaryAccount;
/**
 * 
 * Contains a number of helper methods to deal with accounts on Purchasing Accounts Payable Documents
 */
public class PurapAccountingServiceImpl implements PurapAccountingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private static final int SCALE = 340;

    private static final int BIG_DECIMAL_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    // local constants
    private static final Boolean ITEM_TYPES_INCLUDED_VALUE = Boolean.TRUE;;
    private static final Boolean ITEM_TYPES_EXCLUDED_VALUE = Boolean.FALSE;
    private static final Boolean ZERO_TOTALS_RETURNED_VALUE = Boolean.TRUE;
    private static final Boolean ZERO_TOTALS_NOT_RETURNED_VALUE = Boolean.FALSE;
    private static final Boolean ALTERNATE_AMOUNT_USED = Boolean.TRUE;
    private static final Boolean ALTERNATE_AMOUNT_NOT_USED = Boolean.FALSE;

    // Spring injection
    PurApAccountingDao purApAccountingDao;

    private PurapService purapService;
    
    /**
     * 
     * gets the lowest possible number for rounding, it works for ROUND_HALF_UP
     * @return a BigDecimal representing the lowest possible number for rounding
     */
    private BigDecimal getLowestPossibleRoundUpNumber() {
        BigDecimal startingDigit = new BigDecimal(0.5);
        if (SCALE != 0) {
            startingDigit = startingDigit.movePointLeft(SCALE);
        }
        return startingDigit;
    }

    /**
     * 
     * Helper method to log and throw an error
     * @param methodName the method it's coming from
     * @param errorMessage the actual error
     */
    private void throwRuntimeException(String methodName, String errorMessage) {
        LOG.error(methodName + "  " + errorMessage);
        throw new RuntimeException(errorMessage);
    }

    /**
     * @deprecated
     * @see org.kuali.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.core.util.KualiDecimal, java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale) {
        return null;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.core.util.KualiDecimal, java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz) {
        String methodName = "generateAccountDistributionForProration()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<PurApAccountingLine> newAccounts = new ArrayList();

        if (totalAmount.isZero()) {
            throwRuntimeException(methodName, "Purchasing/Accounts Payable account distribution for proration does not allow zero dollar total.");        }

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal totalAmountBigDecimal = totalAmount.bigDecimalValue();
        for (SourceAccountingLine accountingLine : accounts) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " " + accountingLine.getAccountNumber() + " " + accountingLine.getAmount() + "/" + totalAmountBigDecimal);
            }
            BigDecimal pct = accountingLine.getAmount().bigDecimalValue().divide(totalAmountBigDecimal, percentScale, BIG_DECIMAL_ROUNDING_MODE);
            pct = pct.multiply(ONE_HUNDRED).stripTrailingZeros();

            if ( LOG.isDebugEnabled() ) {
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
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
                }
                newAccounts.add(newAccountingLine);
                percentTotal = percentTotal.add(newAccountingLine.getAccountLinePercent());
                if ( LOG.isDebugEnabled() ) {
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
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " Rounding up by " + difference);
            }

            boolean foundAccountToUse = false;
            int currentNbr = newAccounts.size() - 1;
            while (currentNbr >= 0) {
                PurApAccountingLine potentialSlushAccount = (PurApAccountingLine) newAccounts.get(currentNbr);
                if ((difference.compareTo(potentialSlushAccount.getAccountLinePercent())) < 0) {
                    // the difference amount is less than the current accounts percent... use this account
                    // the 'potentialSlushAccount' technically is now the true 'Slush Account'
                    potentialSlushAccount.setAccountLinePercent((potentialSlushAccount.getAccountLinePercent().subtract(difference)).stripTrailingZeros());
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
             * percent to it's already calculated percent
             */
            BigDecimal difference = ONE_HUNDRED.subtract(percentTotal);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " Rounding down by " + difference);
            }
            PurApAccountingLine slushAccount = (PurApAccountingLine) newAccounts.get(newAccounts.size() - 1);
            slushAccount.setAccountLinePercent((slushAccount.getAccountLinePercent().add(difference)).stripTrailingZeros());
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateAccountDistributionForProrationWithZeroTotal(java.util.List,
     *      java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(List<PurApAccountingLine> accounts, Integer percentScale) {
        String methodName = "generateAccountDistributionForProrationWithZeroTotal()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }

        // find the total percent and strip trailing zeros
        BigDecimal totalPercentValue = BigDecimal.ZERO;
        for (PurApAccountingLine accountingLine : accounts) {
            totalPercentValue = (totalPercentValue.add(accountingLine.getAccountLinePercent())).stripTrailingZeros();
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
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " " + accountingLine.getChartOfAccountsCode() + "-" + accountingLine.getAccountNumber() + " " + accountingLine.getAmount() + "/" + percentToUse);
            }

            // if it's the last account make up the leftover percent
            BigDecimal acctPercent = accountingLine.getAccountLinePercent();
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
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " pct = " + percentToUse);
            }
            newAccountingLine.setAccountLinePercent(percentToUse.setScale(accountingLine.getAccountLinePercent().scale(), BIG_DECIMAL_ROUNDING_MODE));
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
            }
            newAccounts.add(newAccountingLine);
            logDisplayOnlyTotal = logDisplayOnlyTotal.add(newAccountingLine.getAccountLinePercent());
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " total = " + logDisplayOnlyTotal);
            }
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummary(java.util.List)
     */
    public List<SourceAccountingLine> generateSummary(List<PurApItem> items) {
        String methodName = "generateSummary()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryAccounts(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public List<SummaryAccount> generateSummaryAccounts(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems());
    }

    /**
     * 
     * This creates summary accounts based on a list of items.
     * @param items a list of PurAp Items.
     * @return a list of summary accounts.
     */
    private List<SummaryAccount> generateSummaryAccounts(List<PurApItem> items) {
        String methodName = "generateSummaryAccounts()";
        List<SummaryAccount> returnList = new ArrayList<SummaryAccount>();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> sourceLines = generateSummary(items);
        for (SourceAccountingLine sourceAccountingLine : sourceLines) {
            SummaryAccount summaryAccount = new SummaryAccount();
            summaryAccount.setAccount((SourceAccountingLine) ObjectUtils.deepCopy(sourceAccountingLine));
            for (PurApItem item : items) {
                List<PurApAccountingLine> itemAccounts = item.getSourceAccountingLines();
                for (PurApAccountingLine purApAccountingLine : itemAccounts) {
                    if (purApAccountingLine.accountStringsAreEqual(summaryAccount.getAccount())) {
                        PurApSummaryItem summaryItem = item.getSummaryItem();
                        //If the summaryItem is null, it means the item is not eligible to
                        //be displayed in the Account Summary tab. If it's not null then
                        //we'll set the estimatedEncumberanceAmount and add the item to the
                        //summaryAccount list to be displayed in the Account Summary tab.
                        if (summaryItem != null) {
                            summaryItem.setEstimatedEncumberanceAmount(purApAccountingLine.getAmount());
                            summaryAccount.getItems().add(summaryItem);
                            break;
                        }
                    }

                }
            }
            returnList.add(summaryAccount);
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotals(java.util.List)
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotalsUsingAlternateAmount(java.util.List)
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypes(java.util.List, java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypes()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypesAndNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypes(java.util.List, java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypes()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypesAndNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * 
     * Generates an account summary, that is it creates a list of source accounts
     * by rounding up the purap accounts off of the purap items.
     * @param items the items to determ
     * @param itemTypeCodes the item types to determine whether to look at an item in combination with itemTypeCodesAreIncluded 
     * @param itemTypeCodesAreIncluded value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     * @param useZeroTotals whether to include items with a zero dollar total
     * @param useAlternateAmount an alternate amount used in certain cases for GL entry
     * @return a list of source accounts
     */
    private List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals, Boolean useAlternateAmount) {
        List<PurApItem> itemsToProcess = getProcessablePurapItems(items, itemTypeCodes, itemTypeCodesAreIncluded, useZeroTotals);
        Set<PurApAccountingLine> accountSet = new HashSet<PurApAccountingLine>();

        for (PurApItem currentItem : items) {
            if (PurApItemUtils.checkItemActive(currentItem)) {
                for (PurApAccountingLine account : currentItem.getSourceAccountingLines()) {
                    boolean thisAccountAlreadyInSet = false;
                    for (Iterator iter = accountSet.iterator(); iter.hasNext();) {
                        PurApAccountingLine alreadyAddedAccount = (PurApAccountingLine) iter.next();
                        if (alreadyAddedAccount.accountStringsAreEqual(account)) {
                            if (useAlternateAmount) {
                                alreadyAddedAccount.setAlternateAmountForGLEntryCreation(alreadyAddedAccount.getAlternateAmountForGLEntryCreation().add(account.getAlternateAmountForGLEntryCreation()));
                            }
                            else {
                                alreadyAddedAccount.setAmount(alreadyAddedAccount.getAmount().add(account.getAmount()));
                            }
                            thisAccountAlreadyInSet = true;
                            break;
                        }
                    }
                    if (!thisAccountAlreadyInSet) {
                        PurApAccountingLine accountToAdd = (PurApAccountingLine) ObjectUtils.deepCopy(account);
                        accountSet.add(accountToAdd);
                    }
                }
            }
        }

        // convert list of PurApAccountingLine objects to SourceAccountingLineObjects
        List<SourceAccountingLine> sourceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator iter = accountSet.iterator(); iter.hasNext();) {
            PurApAccountingLine accountToAlter = (PurApAccountingLine) iter.next();
            if (accountToAlter.isEmpty()) {
                String errorMessage = "Found an 'empty' account in summary generation " + accountToAlter.toString();
                LOG.error("generateAccountSummary() " + errorMessage);
                throw new RuntimeException(errorMessage);
            }
            SourceAccountingLine sourceLine = accountToAlter.generateSourceAccountingLine();
            if (useAlternateAmount) {
                sourceLine.setAmount(accountToAlter.getAlternateAmountForGLEntryCreation());
            }
            sourceAccounts.add(sourceLine);
        }
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
     * @param itemTypeCodes - list of {@link org.kuali.module.purap.bo.ItemType} codes used in conjunction with
     *        itemTypeCodesAreIncluded parameter
     * @param itemTypeCodesAreIncluded - value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     *        (see {@link #ITEM_TYPES_INCLUDED_VALUE})
     * @param useZeroTotals - value to tell whether to include zero dollar items (see {@link #ZERO_TOTALS_RETURNED_VALUE})
     * @return a list of {@link PurchasingApItem} objects that should be used for processing by calling method
     */
    private List<PurApItem> getProcessablePurapItems(List<PurApItem> items, Set itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals) {
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
     * 
     * @see org.kuali.module.purap.service.PurapAccountingService#updateAccountAmounts(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document) {
        // the percent at fiscal approve
        // don't update if past the AP review level
        if ((document instanceof PaymentRequestDocument) && purapService.isFullDocumentEntryCompleted(document)) {
            return;
        }
        for (PurApItem item : document.getItems()) {
            updateItemAccountAmounts(item);
        }

    }

    /**
     * 
     * @see org.kuali.module.purap.service.PurapAccountingService#updateItemAccountAmounts(org.kuali.module.purap.bo.PurApItem)
     */
    public void updateItemAccountAmounts(PurApItem item) {
        if ((item.getExtendedPrice() != null) && KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0) {

            KualiDecimal accountTotal = KualiDecimal.ZERO;
            PurApAccountingLine lastAccount = null;

            for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                    BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                    account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(item.getExtendedPrice().toString()))));
                }
                else {
                    account.setAmount(KualiDecimal.ZERO);
                }
                accountTotal = accountTotal.add(account.getAmount());
                lastAccount = account;
            }

            // put excess on last account
            if (lastAccount != null) {
                KualiDecimal difference = item.getExtendedPrice().subtract(accountTotal);
                lastAccount.setAmount(lastAccount.getAmount().add(difference));
            }
        }
        else {
            // zero out if extended price is zero
            for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                account.setAmount(KualiDecimal.ZERO);
            }
        }
    }

    /**
     * 
     * @see org.kuali.module.purap.service.PurapAccountingService#deleteSummaryAccounts(java.lang.Integer)
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier) {
        purApAccountingDao.deleteSummaryAccounts(purapDocumentIdentifier);
    }
    
    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item) {
        return purApAccountingDao.getAccountingLinesForItem(item);
    }

    public PurApAccountingDao getPurApAccountingDao() {
        return purApAccountingDao;
    }

    public void setPurApAccountingDao(PurApAccountingDao purApAccountingDao) {
        this.purApAccountingDao = purApAccountingDao;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

}
