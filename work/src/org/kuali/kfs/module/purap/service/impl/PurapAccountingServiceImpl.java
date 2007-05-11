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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.service.PurapAccountingService;

public class PurapAccountingServiceImpl implements PurapAccountingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    // TODO PURAP:  Should we use a static scale such as below?  Should we be allowed to pass it in and get it per doc from the data dictionary??
    private static final int SCALE = 340;
    // TODO PURAP:  Should we use Kuali's formally supported rounding mode (from BigDecimalFormatter or KualiDecimal) below?
    private static final int BIG_DECIMAL_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    // local constants
    private static final boolean ITEM_TYPES_INCLUDED_VALUE = true;
    private static final boolean ITEM_TYPES_EXCLUDED_VALUE = false;
    private static final boolean ZERO_TOTALS_RETURNED_VALUE = true;
    private static final boolean ZERO_TOTALS_NOT_RETURNED_VALUE = false;
    
    // below works perfectly for ROUND_HALF_UP
    private BigDecimal getLowestPossibleRoundUpNumber() {
        BigDecimal startingDigit = new BigDecimal(0.5);
        if (SCALE != 0) {
            startingDigit = startingDigit.movePointLeft(SCALE);
        }
        return startingDigit;
    }
    
    private void throwRuntimeException(String methodName, String errorMessage) {
        LOG.error(methodName + "  " + errorMessage);
        throw new RuntimeException(errorMessage);
    }
    
    /**
     * This method...
     * @param accounts
     * @param totalAmount
     * @param percentScale
     * @return
     */
    // TODO PURAP: Needs Unit Tests
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<PurApAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale) {
        String methodName = "generateAccountDistributionForProration()";
        LOG.debug(methodName + " started");
        List<PurApAccountingLine> newAccounts = new ArrayList();
        
        if (totalAmount.isZero()) {
            throwRuntimeException(methodName,"Purchasing/Accounts Payable account distribution for proration does not allow zero dollar total.");
        }
        
        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal totalAmountBigDecimal = totalAmount.bigDecimalValue();
        for(PurApAccountingLine accountingLine : accounts) {
            LOG.debug(methodName + " " + accountingLine.getAccountNumber() + " " + accountingLine.getAmount() + "/" + totalAmountBigDecimal);
            
            BigDecimal pct = accountingLine.getAmount().bigDecimalValue().divide(totalAmountBigDecimal,SCALE,BIG_DECIMAL_ROUNDING_MODE);
            pct = pct.multiply(ONE_HUNDRED).stripTrailingZeros();

            LOG.debug(methodName + " pct = " + pct + "  (trailing zeros removed)");

            BigDecimal lowestPossible = this.getLowestPossibleRoundUpNumber();
            if (lowestPossible.compareTo(pct) <= 0) {
                PurApAccountingLine newAccountingLine = accountingLine.createBlankAmountsCopy();
                // da.setDistributedPercent(pct.setScale(da.getTotalPercentForDocument().scale(),BigDecimal.ROUND_HALF_UP));
                newAccountingLine.setAccountLinePercent(pct);
                LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
                newAccounts.add(newAccountingLine);
                percentTotal = percentTotal.add(newAccountingLine.getAccountLinePercent());
//                total += da.getDistributedPercent().doubleValue();
                LOG.debug(methodName + " total = " + percentTotal);
            }
        }
        
        if ((percentTotal.compareTo(BigDecimal.ZERO)) == 0) {
            /*   This means there are so many accounts or so strange a distribution
             *   that we can't round properly... not sure of viable solution
             */
            throwRuntimeException(methodName, "Can't round properly due to number of accounts");
        }
        
        // Now deal with rounding
        if ((ONE_HUNDRED.compareTo(percentTotal)) < 0) {
            /*  The total percent is greater than one hundred
             * 
             *  Here we find the account that occurs latest in our list with a percent
             *  that is higher than the difference and we subtract off the difference
             */
            BigDecimal difference = percentTotal.subtract(ONE_HUNDRED);
            LOG.debug(methodName + " Rounding up by " + difference);

            boolean foundAccountToUse = false;
            int currentNbr = newAccounts.size() - 1;
            while ( currentNbr >= 0 ) {
                PurApAccountingLine potentialSlushAccount = (PurApAccountingLine)newAccounts.get(currentNbr);
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
                /*  We could not find any account in our list where the percent of that account
                 *  was greater than that of the difference... doing so on just any account could
                 *  result in a negative percent value
                 */
                throwRuntimeException(methodName, "Can't round properly due to math calculation error");
            }
        
        } else if ((ONE_HUNDRED.compareTo(percentTotal)) > 0) {
            /*  The total percent is less than one hundred
             * 
             *  Here we find the last account in our list and add the remaining required percent
             *  to it's already calculated percent
             */
            BigDecimal difference = ONE_HUNDRED.subtract(percentTotal);
            LOG.debug(methodName + " Rounding down by " + difference);
            PurApAccountingLine slushAccount = (PurApAccountingLine)newAccounts.get(newAccounts.size() - 1);
            slushAccount.setAccountLinePercent((slushAccount.getAccountLinePercent().add(difference)).stripTrailingZeros());
        }
        return newAccounts;
    }

    /**
     * This method...
     * @param accounts
     * @param percentScale
     * @return
     */
    // TODO PURAP: Needs Unit Tests
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(List<PurApAccountingLine> accounts, Integer percentScale) {
        String methodName = "generateAccountDistributionForProrationWithZeroTotal()";
        LOG.debug(methodName + " started");

        // find the total percent and strip trailing zeros
        BigDecimal totalPercentValue = BigDecimal.ZERO;
        for(PurApAccountingLine accountingLine : accounts) {
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
        for(PurApAccountingLine accountingLine : accounts) {
            i++;
            BigDecimal percentToUse = BigDecimal.ZERO;
            LOG.debug(methodName + " " + accountingLine.getChartOfAccountsCode() + "-" + accountingLine.getAccountNumber() + " " + accountingLine.getAmount() + "/" + percentToUse);
            
            // if it's the last account make up the leftover percent
            BigDecimal acctPercent = accountingLine.getAccountLinePercent();
            if ((i != accountListSize) || (accountListSize == 1)) {
              // this account is not the last account or there is only one account
              percentToUse = (acctPercent.divide(totalPercentValue, SCALE, BIG_DECIMAL_ROUNDING_MODE)).multiply(ONE_HUNDRED);
              percentUsed = percentUsed.add(((acctPercent.divide(totalPercentValue, SCALE, BIG_DECIMAL_ROUNDING_MODE))).multiply(ONE_HUNDRED));
            } else {
              // this account is the last account so we have to makeup whatever is left out of 100
              percentToUse = ONE_HUNDRED.subtract(percentUsed);
            }

            PurApAccountingLine newAccountingLine = accountingLine.createBlankAmountsCopy();
            LOG.debug(methodName + " pct = " + percentToUse);
            newAccountingLine.setAccountLinePercent(percentToUse.setScale(accountingLine.getAccountLinePercent().scale(),BIG_DECIMAL_ROUNDING_MODE));
            LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
            newAccounts.add(newAccountingLine);
            logDisplayOnlyTotal = logDisplayOnlyTotal.add(newAccountingLine.getAccountLinePercent());
            LOG.debug(methodName + " total = " + logDisplayOnlyTotal);
        }
        return newAccounts;
    }

    /**
     * This method...
     * @param accounts
     * @return
     */
    public List<SourceAccountingLine> generateSummary(List<PurchasingApItem> items) {
        String methodName = "generateSummary()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE);
    }

    /**
     * This method...
     * @param accounts
     * @return
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurchasingApItem> items) {
        String methodName = "generateSummaryWithNoZeroTotals()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE);
    }

    /**
     * This method...
     * @param accounts
     * @param excludedItemTypeCodes
     * @return
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurchasingApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypes()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE);
    }

    /**
     * This method...
     * @param accounts
     * @param excludedItemTypeCodes
     * @return
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurchasingApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryExcludeItemTypesAndNoZeroTotals()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE);
    }
    
    /**
     * This method...
     * @param accounts
     * @param includedItemTypeCodes
     * @return
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurchasingApItem> items, Set includedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypes()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, includedItemTypeCodes, ITEM_TYPES_INCLUDED_VALUE, ZERO_TOTALS_RETURNED_VALUE);
    }

    /**
     * This method...
     * @param accounts
     * @param includedItemTypeCodes
     * @return
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurchasingApItem> items, Set excludedItemTypeCodes) {
        String methodName = "generateSummaryIncludeItemTypesAndNoZeroTotals()";
        LOG.debug(methodName + " started");
        return generateAccountSummary(items, excludedItemTypeCodes, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE);
    }
    
    private List<SourceAccountingLine> generateAccountSummary(List<PurchasingApItem> items, Set itemTypeCodes, boolean itemTypeCodesAreIncluded, boolean useZeroTotals) {
        Map accountMap = new HashMap();
        Map percentMap = new HashMap();
        return new ArrayList<SourceAccountingLine>();
        
//        SourceAccountingLine temp = new SourceAccountingLine();
//        temp.isLike(other)
        
        
        
//        List items = getDisplayItems(itemTypeCodes,itemTypeCodesAreIncluded);

        // Make a unique list of accounts with the total amount on the account

    }
}
