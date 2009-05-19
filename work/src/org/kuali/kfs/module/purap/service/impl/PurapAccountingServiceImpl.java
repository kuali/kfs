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
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapParameterConstants.NRATaxParameters;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
/**
 * 
 * Contains a number of helper methods to deal with accounts on Purchasing Accounts Payable Documents
 */

@NonTransactional
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
    private static final Boolean USE_TAX_INCLUDED = Boolean.TRUE;
    private static final Boolean USE_TAX_EXCLUDED = Boolean.FALSE;

    private ParameterService parameterService;    
    private PurApAccountingDao purApAccountingDao;
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
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.rice.kns.util.KualiDecimal, java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale) {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProration(java.util.List,
     *      org.kuali.rice.kns.util.KualiDecimal, java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz) {
        String methodName = "generateAccountDistributionForProration()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<PurApAccountingLine> newAccounts = new ArrayList();

        if (totalAmount.isZero()) {
            throwRuntimeException(methodName, "Purchasing/Accounts Payable account distribution for proration does not allow zero dollar total.");        
        }

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal totalAmountBigDecimal = totalAmount.bigDecimalValue();
        for (SourceAccountingLine accountingLine : accounts) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " " + accountingLine.getAccountNumber() + " " + accountingLine.getAmount() + "/" + totalAmountBigDecimal);
            }
            BigDecimal pct = accountingLine.getAmount().bigDecimalValue().divide(totalAmountBigDecimal, percentScale, BIG_DECIMAL_ROUNDING_MODE);
            pct = pct.stripTrailingZeros().multiply(ONE_HUNDRED);

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
                    potentialSlushAccount.setAccountLinePercent(potentialSlushAccount.getAccountLinePercent().subtract(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
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
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " Rounding down by " + difference);
            }
            PurApAccountingLine slushAccount = (PurApAccountingLine) newAccounts.get(newAccounts.size() - 1);
            slushAccount.setAccountLinePercent(slushAccount.getAccountLinePercent().add(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateAccountDistributionForProrationWithZeroTotal(java.util.List,
     *      java.lang.Integer)
     */
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapDoc) {
        String methodName = "generateAccountDistributionForProrationWithZeroTotal()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }

        List<PurApAccountingLine> accounts = generatePercentSummary(purapDoc);
        
        // find the total percent and strip trailing zeros
        BigDecimal totalPercentValue = BigDecimal.ZERO;
        for (PurApAccountingLine accountingLine : accounts) {
            totalPercentValue = totalPercentValue.add(accountingLine.getAccountLinePercent()).movePointLeft(2).stripTrailingZeros().movePointRight(2);
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
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummary(java.util.List)
     */
    public List<SourceAccountingLine> generateSummary(List<PurApItem> items) {
        String methodName = "generateSummary()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccounts(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public List<SummaryAccount> generateSummaryAccounts(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_RETURNED_VALUE, USE_TAX_INCLUDED);
    }
    
    

    /**
     * 
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccountsWithNoZeroTotals(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_NOT_RETURNED_VALUE, USE_TAX_INCLUDED);
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryAccountsWithNoZeroTotals(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document) {
        // always update the amounts first
        updateAccountAmounts(document);
        return generateSummaryAccounts(document.getItems(), ZERO_TOTALS_NOT_RETURNED_VALUE, USE_TAX_EXCLUDED);
    }
    
    /**
     * 
     * This creates summary accounts based on a list of items.
     * @param items a list of PurAp Items.
     * @return a list of summary accounts.
     */
    private List<SummaryAccount> generateSummaryAccounts(List<PurApItem> items, Boolean useZeroTotals, Boolean useTaxIncluded) {
        String methodName = "generateSummaryAccounts()";
        List<SummaryAccount> returnList = new ArrayList<SummaryAccount>();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        
        List<SourceAccountingLine> sourceLines = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, useZeroTotals, ALTERNATE_AMOUNT_NOT_USED, useTaxIncluded);
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
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotals(java.util.List)
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * calls generateSummary with no use tax included
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotalsNoUseTax()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_EXCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        
        return returnList;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryWithNoZeroTotalsUsingAlternateAmount(java.util.List)
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypes(java.util.List, java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypes()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypesAndNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryIncludeItemTypes(java.util.List, java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypes()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateSummaryExcludeItemTypesAndNoZeroTotals(java.util.List,
     *      java.util.Set)
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypesAndNoZeroTotals()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        List<SourceAccountingLine> returnList = generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_NOT_USED, USE_TAX_INCLUDED);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " ended");
        }
        return returnList;
    }

    /**
     * Generates an account summary, that is it creates a list of source accounts
     * by rounding up the purap accounts off of the purap items.
     * @param items the items to determ
     * @param itemTypeCodes the item types to determine whether to look at an item in combination with itemTypeCodesAreIncluded 
     * @param itemTypeCodesAreIncluded value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     * @param useZeroTotals whether to include items with a zero dollar total
     * @param useAlternateAmount an alternate amount used in certain cases for GL entry
     * @return a list of source accounts
     */
    private List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set<String> itemTypeCodes, Boolean itemTypeCodesAreIncluded, 
            Boolean useZeroTotals, Boolean useAlternateAmount, Boolean useTaxIncluded) {
        List<PurApItem> itemsToProcess = getProcessablePurapItems(items, itemTypeCodes, itemTypeCodesAreIncluded, useZeroTotals);
        Map<PurApAccountingLine,KualiDecimal> accountMap = new HashMap<PurApAccountingLine,KualiDecimal>();

        for (PurApItem currentItem : items) {
            if (PurApItemUtils.checkItemActive(currentItem)) {
                List<PurApAccountingLine> sourceAccountingLines = currentItem.getSourceAccountingLines();
                
                if (!useTaxIncluded) {
                    //if no use tax set the source accounting lines to a clone so we can update
                    //them to be based on the non tax amount
                    PurApItem cloneItem = (PurApItem)ObjectUtils.deepCopy(currentItem);
                    sourceAccountingLines = cloneItem.getSourceAccountingLines();
                    updateAccountAmountsWithTotal(sourceAccountingLines, currentItem.getTotalRemitAmount());
                }
                
                for (PurApAccountingLine account : sourceAccountingLines) {
                    // getting the total to set on the account
                    KualiDecimal total = KualiDecimal.ZERO;
                    if (accountMap.containsKey(account)) {
                        total = accountMap.get(account);
                    }
                    
                    if (useAlternateAmount) {
                        total = total.add(account.getAlternateAmountForGLEntryCreation());
                    }
                    else {
                        total = total.add(account.getAmount());
                    }

                    accountMap.put(account, total);
                }
            }
        }

        // convert list of PurApAccountingLine objects to SourceAccountingLineObjects
        Iterator<PurApAccountingLine> iterator = accountMap.keySet().iterator();
        List<SourceAccountingLine> sourceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator<PurApAccountingLine> iter = iterator; iter.hasNext();) {
            PurApAccountingLine accountToConvert = (PurApAccountingLine) iter.next();
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
        Collections.sort(sourceAccounts, 
                new Comparator<SourceAccountingLine>() {
                    public int compare(SourceAccountingLine sal1, SourceAccountingLine sal2) {
                        int compare = 0;
                        if (sal1 != null && sal2 != null) {
                            if (sal1.getAccountNumber() != null && sal2.getAccountNumber() != null) {                        
                                compare = sal1.getAccountNumber().compareTo(sal2.getAccountNumber());    
                                if (compare == 0) {
                                    if (sal1.getFinancialObjectCode() != null && sal2.getFinancialObjectCode() != null)
                                        compare =  sal1.getFinancialObjectCode().compareTo(sal2.getFinancialObjectCode());
                                }
                            }
                        }
                        return compare;
                    }
                }
        );
        
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
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updateAccountAmounts(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document) {
        // the percent at fiscal approve
        // don't update if past the AP review level
        if ((document instanceof PaymentRequestDocument) && purapService.isFullDocumentEntryCompleted(document)) {
            //update the percent but don't update the amounts if preq and past full entry
            convertMoneyToPercent((PaymentRequestDocument)document);
            return;
        }
        document.fixItemReferences();
        for (PurApItem item : document.getItems()) {
            updateItemAccountAmounts(item);
        }

    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#updateItemAccountAmounts(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public void updateItemAccountAmounts(PurApItem item) {
        List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
        KualiDecimal totalAmount = item.getTotalAmount();
        
        updateAccountAmountsWithTotal(sourceAccountingLines, totalAmount);
    }

    /**
     * calculates values for a list of accounting lines based on an amount
     * @param sourceAccountingLines
     * @param totalAmount
     */
    private <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount) {
        if ((totalAmount != null) && KualiDecimal.ZERO.compareTo(totalAmount) != 0) {

            KualiDecimal accountTotal = KualiDecimal.ZERO;
            T lastAccount = null;

            
            for (T account : sourceAccountingLines) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                    BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                    account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(totalAmount.toString()))));
                }
                else {
                    account.setAmount(KualiDecimal.ZERO);
                }
                accountTotal = accountTotal.add(account.getAmount());
                lastAccount = account;
            }

            // put excess on last account
            if (lastAccount != null) {
                KualiDecimal difference = totalAmount.subtract(accountTotal);
                lastAccount.setAmount(lastAccount.getAmount().add(difference));
            }
        }
        else {
            // zero out if extended price is zero
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

                            alreadyAddedAccount.setAccountLinePercent(alreadyAddedAccount.getAccountLinePercent().add(account.getAccountLinePercent()));
                            
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
    public void convertMoneyToPercent(PaymentRequestDocument pr) {
        LOG.debug("convertMoneyToPercent() started");

        int itemNbr = 0;

        for (Iterator<PaymentRequestItem> iter = pr.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) iter.next();

            itemNbr++;
            String identifier = item.getItemIdentifierString();

            if (item.getTotalAmount()!=null && item.getTotalAmount().isNonZero()) {

                KualiDecimal accountTotal = KualiDecimal.ZERO;
                int accountIdentifier = 0;
                for (Iterator<PurApAccountingLine> iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                    accountIdentifier++;
                    PaymentRequestAccount account = (PaymentRequestAccount) iterator.next();
                    KualiDecimal accountAmount = account.getAmount();
                    BigDecimal tmpPercent = BigDecimal.ZERO;
                    KualiDecimal extendedPrice = item.getTotalAmount();
                    tmpPercent = accountAmount.bigDecimalValue().divide(extendedPrice.bigDecimalValue(), PurapConstants.CREDITMEMO_PRORATION_SCALE.intValue(), KualiDecimal.ROUND_BEHAVIOR);
                    // test that the above amount is correct, if so just check that the total of all these matches the item total

                    KualiDecimal calcAmount = new KualiDecimal(tmpPercent.multiply(extendedPrice.bigDecimalValue()));
                    if (calcAmount.compareTo(accountAmount) != 0) {
                        // rounding error
                        LOG.debug("convertMoneyToPercent() Rounding error on " + account);
                        String param1 = identifier + "." + accountIdentifier;
                        String param2 = calcAmount.bigDecimalValue().subtract(accountAmount.bigDecimalValue()).toString();
                        GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_ROUNDING, param1, param2);
                        account.setAmount(calcAmount);
                    }

                    // update percent
                    LOG.debug("convertMoneyToPercent() updating percent to " + tmpPercent);
                    account.setAccountLinePercent(tmpPercent.multiply(new BigDecimal(100)));

                    // check total based on adjusted amount
                    accountTotal = accountTotal.add(calcAmount);

                }
                if (!accountTotal.equals(item.getTotalAmount())) {
                    GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_DOLLAR_TOTAL, identifier, accountTotal.toString(), item.getTotalAmount()+"");
                    LOG.debug("Invalid Totals");
                }
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#deleteSummaryAccounts(java.lang.Integer, java.lang.String)
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier, String docType) {
        if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            purApAccountingDao.deleteSummaryAccountsbyPaymentRequestIdentifier(purapDocumentIdentifier);
        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            purApAccountingDao.deleteSummaryAccountsbyCreditMemoIdentifier(purapDocumentIdentifier);
        }
    }
    
    public List getAccountsPayableSummaryAccounts(Integer purapDocumentIdentifier, String docType) {
        if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            return purApAccountingDao.getSummaryAccountsbyPaymentRequestIdentifier(purapDocumentIdentifier);
        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            purApAccountingDao.getSummaryAccountsbyCreditMemoIdentifier(purapDocumentIdentifier);
        }
        return null;
    }

    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item) {
        return purApAccountingDao.getAccountingLinesForItem(item);
    }

    public List<SourceAccountingLine> generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document) {
        //correct initial amounts or percents
        updateAccountAmounts(document);
        List<SourceAccountingLine> vendorSummaryAccounts = new ArrayList<SourceAccountingLine>();
        
        //update accounts here with amounts to send to vendor
        vendorSummaryAccounts = generateSummaryWithNoZeroTotalsNoUseTax(document.getItems());

        return vendorSummaryAccounts;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#generateUseTaxAccount(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public List<UseTaxContainer> generateUseTaxAccount(PurchasingAccountsPayableDocument document) {
        List<UseTaxContainer> useTaxAccounts = new ArrayList<UseTaxContainer>();
        
        HashMap<PurApItemUseTax,UseTaxContainer> useTaxItemMap = new  HashMap<PurApItemUseTax,UseTaxContainer>();
        Class accountingLineClass = null;
          if(!document.isUseTaxIndicator()) {
              //not useTax, return
              return useTaxAccounts;
          }
        for (PurApItem purApItem : document.getItems()) {
            if(!purApItem.getUseTaxItems().isEmpty()) {
                if(accountingLineClass==null) {
                    accountingLineClass = purApItem.getAccountingLineClass();
                }
                UseTaxContainer useTaxContainer=new UseTaxContainer();
                for (PurApItemUseTax itemUseTax : purApItem.getUseTaxItems()) {
                    if(useTaxItemMap.containsKey(itemUseTax)) {
                       useTaxContainer = useTaxItemMap.get(itemUseTax);
                       PurApItemUseTax exisitingItemUseTax = useTaxContainer.getUseTax();
                       //if already in set we need to add on the old amount
                       exisitingItemUseTax.getTaxAmount().add(itemUseTax.getTaxAmount());
                       useTaxContainer.getItems().add(purApItem);
                    } else {
                        useTaxContainer = new UseTaxContainer(itemUseTax,purApItem);
                        useTaxItemMap.put(itemUseTax, useTaxContainer);
                    }
                    
                }
                useTaxAccounts.add(useTaxContainer);
            }
        }
        // iterate over useTaxAccounts and set summary accounts using proration
        for (UseTaxContainer useTaxContainer : useTaxAccounts) {
            
            //create summary from items
            List<SourceAccountingLine> origSourceAccounts = this.generateSummaryWithNoZeroTotals(useTaxContainer.getItems());
            //prorate to update amounts and come back with one list
            List<PurApAccountingLine> accountingLines = generateAccountDistributionForProration(origSourceAccounts, useTaxContainer.getUseTax().getTaxAmount(), PurapConstants.PRORATION_SCALE, 
                                                        accountingLineClass);
            List<SourceAccountingLine> newSourceLines = new ArrayList<SourceAccountingLine>();
            //convert back to source
            for (PurApAccountingLine purApAccountingLine : accountingLines) {
                newSourceLines.add(purApAccountingLine.generateSourceAccountingLine());
            }
            //do we need an update accounts here?
            useTaxContainer.setAccounts(newSourceLines);
        }
        
        
        useTaxAccounts=new ArrayList<UseTaxContainer>(useTaxItemMap.values());
        return useTaxAccounts;
//        List<PurApItem> taxableItems = new ArrayList<PurApItem>();
//        List<SourceAccountingLine> sourceAccounts = new ArrayList<SourceAccountingLine>();
//        Set<PurApItemUseTax> useTaxSet = new HashSet<PurApItemUseTax>(10);
//        if(!document.isUseTaxIndicator()) {
//            //not use, return
//            return useTaxAccounts;
//        }
//        for (PurApItem purApItem : document.getItems()) {
//            if(!purApItem.getUseTaxItems().isEmpty()) {
//                taxableItems.add(purApItem);
//            }
//        }
//        if(taxableItems.isEmpty()) {
//            //no taxable items? return I guess
//            return useTaxAccounts;
//        }
//        sourceAccounts = generateSummaryWithNoZeroTotals(taxableItems);
//        boolean firstItem = true;
//        //FIXME: don't seem to be gaining much from the set here
//        for (PurApItem item : taxableItems) {
//            for (PurApItemUseTax useTaxItem : item.getUseTaxItems()) {
//                PurApItemUseTax foundUseTaxItem = null;
//                if(useTaxSet.contains(useTaxItem)) {
//                    for (PurApItemUseTax purApItemUseTax : useTaxSet) {
//                        if(useTaxItem.equals(purApItemUseTax)) {
//                            foundUseTaxItem = purApItemUseTax;
//                            break;
//                        }
//                    }
//                    if(foundUseTaxItem!=null) {
//                        foundUseTaxItem.setTaxAmount(foundUseTaxItem.getTaxAmount().add(useTaxItem.getTaxAmount()));
//                    } else {
//                        LOG.error("PurApAccountingService.generateUseTaxAmount(): broken assumption, guessing .equals != .hashcode on useTaxItem"); 
//                    }
//                } else {
//                    if(firstItem){
//                        useTaxSet.add(useTaxItem);
//                    } else {
//                        LOG.error("PurApAccountingService.generateUseTaxAmount(): broken assumption, use tax items not the same on all items");
//                        //TODO: should this be runtime exception?
//                    }
//                }
//            }
//            firstItem=false;
//        }
//        //TODO: refactor above to get all (nonzero?) items associated with a specific useTaxItem
//        //consider using MultiValueMap
//        MultiValueMap useTaxItemMap = new MultiValueMap();
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountingService#isTaxAccount(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument, org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    public boolean isTaxAccount(PurchasingAccountsPayableDocument document, SourceAccountingLine account) {
        boolean isTaxAccount = false;

        // check if the summary account is for tax withholding
        if (document instanceof PaymentRequestDocument) {
            String incomeClassCode = ((PaymentRequestDocument)document).getTaxClassificationCode();
            if (StringUtils.isNotEmpty(incomeClassCode)) {
                
                String federalChartCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                String federalAccountNumber = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                String federalObjectCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                String stateChartCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
                String stateAccountNumber = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
                String stateObjectCode = parameterService.getParameterValue(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, incomeClassCode);

                String chartCode = account.getChartOfAccountsCode();
                String accountNumber = account.getAccountNumber();
                String objectCode = account.getFinancialObjectCode();

                boolean isFederalAccount = StringUtils.equals(federalChartCode, chartCode);
                isFederalAccount = isFederalAccount && StringUtils.equals(federalAccountNumber, accountNumber);
                isFederalAccount = isFederalAccount && StringUtils.equals(federalObjectCode, objectCode);
                
                boolean isStateAccount = StringUtils.equals(stateChartCode, chartCode);
                isStateAccount = isStateAccount && StringUtils.equals(stateAccountNumber, accountNumber);
                isStateAccount = isStateAccount && StringUtils.equals(stateObjectCode, objectCode);
                
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

}
