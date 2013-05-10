/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class TransactionArchiveLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        
        // Initialize values.
        BigDecimal greaterAmt = null;
        BigDecimal lessAmt    = null;
        
        // Get the greater than and less than amounts.
        try {
            greaterAmt = new BigDecimal(fieldValues.remove(EndowPropertyConstants.TRANSACTION_ARCHIVE_GREATER_AMOUNT));
            lessAmt    = new BigDecimal(fieldValues.remove(EndowPropertyConstants.TRANSACTION_ARCHIVE_LESS_AMOUNT));
        }
        catch (NumberFormatException nfe) {}
        
        List<TransactionArchive> transactionArchives = (List<TransactionArchive>)super.getSearchResults(fieldValues);
        List<TransactionArchive> removalList = new ArrayList<TransactionArchive>();
        for (TransactionArchive transactionArchive : transactionArchives) {
            
            // Get principal and income amounts.
            BigDecimal principalAmt = transactionArchive.getPrincipalCashAmount();
            BigDecimal incomeAmt    = transactionArchive.getIncomeCashAmount();
            
            // Case 1: Equal to or greater than.
            if (greaterAmt != null && lessAmt == null) {
                if (!isPrincipalIncomeGreaterEqual(greaterAmt, principalAmt, incomeAmt)) {
                    removalList.add(transactionArchive);
                }
            }
            // Case 2: Equal to or less than.
            else if (greaterAmt == null && lessAmt != null) {
                if (!isPrincipalIncomeLessEqual(lessAmt, principalAmt, incomeAmt)) {
                    removalList.add(transactionArchive);
                }
            }
            // Case 3: Falls within in the range of greater than and less than, inclusive.
            // Case 4: Value is exact.
            else if (greaterAmt != null && lessAmt != null) {
                // If both greater and less than values are the same, just check
                // if either the principal or income value is equal to the
                // greater/less amount.
                if (greaterAmt.compareTo(lessAmt) == 0 && 
                    !isPrincipalIncomeEqual(lessAmt, principalAmt, incomeAmt)) {
                    removalList.add(transactionArchive);
                }
                // Check if either the income or principal value fall within the
                // range of the greater and less than values.
                else if (greaterAmt.compareTo(lessAmt) != 0 &&
                         !isPrincipalIncomeInRange(greaterAmt, lessAmt, principalAmt, incomeAmt)) {
                    removalList.add(transactionArchive);
                }
            }
        }
        
        // Remove items from the list.
        transactionArchives.removeAll(removalList);
        
        return transactionArchives;
    }
    
    /**
     * Determines if either the principal or income amount is equal to the specified amount.
     * 
     * @param amount
     * @param principal
     * @param income
     * @return true if principal or income is equal to the specified amount
     */
    private boolean isPrincipalIncomeEqual(BigDecimal amount, BigDecimal principal, BigDecimal income) {
        return (principal.compareTo(amount) == 0 || income.compareTo(amount) == 0);
    }
    
    /**
     * Determines if the principal or income amount is within the specified range.
     *
     * @param greaterAmount
     * @param lessAmount
     * @param principal
     * @param income
     * @return true if the principal or income amount is within the greater and less than amount.
     */
    private boolean isPrincipalIncomeInRange(BigDecimal greaterAmount, BigDecimal lessAmount, BigDecimal principal, BigDecimal income) {

        return (isPrincipalIncomeGreaterEqual(greaterAmount, principal, income) && 
                isPrincipalIncomeLessEqual(lessAmount, principal, income));
    }
    
    /**
     * Determines if the principal or income amount is less than or equal to the specified amount. 
     *
     * @param amount
     * @param principal
     * @param income
     * @return true if either the principal or income amounts are less than or equal to the specified amount.
     */
    private boolean isPrincipalIncomeLessEqual(BigDecimal amount, BigDecimal principal, BigDecimal income) {
        return (principal.compareTo(amount) <= 0 || income.compareTo(amount) <= 0);
    }
    
    /**
     * Determines if the principal or income amount is less than or equal to the specified amount.
     *
     * @param amount
     * @param principal
     * @param income
     * @return true if either the principal or income amounts are greater than or equal to the specified amount.
     */
    private boolean isPrincipalIncomeGreaterEqual(BigDecimal amount, BigDecimal principal, BigDecimal income) {
        return (principal.compareTo(amount) >= 0 || income.compareTo(amount) >= 0);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {

        return false;
    }

}
