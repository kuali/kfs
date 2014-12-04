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
