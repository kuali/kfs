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
package org.kuali.module.purap.service;

import java.util.List;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.util.SummaryAccount;

/**
 * This class is used to generate Account Summaries for the Purchasing Accounts Payable Module account lists as well as to generate account lists that can be
 * used for distribution to below the line items or any other items that may require distribution
 */
public interface PurapAccountingService {

    /**
     * unused see other generateAccountDistribution methods
     * @deprecated
     * @param accounts
     * @param totalAmount
     * @param percentScale
     * @return
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale);

    /**
     * 
     * Determines an appropriate account distribution for a particular Purchasing Accounts Payable list of Accounts.  It does this by looking at the accounts that were provided
     * which should be generated from a generateSummary method.  It then builds up a list of PurApAccountingLines (specified by the Class variable) and tries to determine the 
     * appropriate percents to use on the new accounts, this may require some moving of percents to the last account as a slush. 
     * 
     * @param accounts the incoming source accounts from generateSummary
     * @param totalAmount the total amount of the document
     * @param percentScale the scale to round to
     * @param clazz the class of the Purchasing Accounts Payable Account
     * @return a list of new Purchasing Accounts Payable Accounts
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz);

    /**
     * 
     * Determines an appropriate account distribution for a particular Purchasing Accounts Payable list of Accounts.  It does this by looking at the accounts that were provided
     * which should be generated from a generateSummary method.  It then builds up a list of PurApAccountingLines (specified by the Class variable) and tries to determine the 
     * appropriate percents to use on the new accounts, this may require some moving of percents to the last account as a slush.  This is called when a document has a zero dollar
     * total

     * @param accounts the incoming source accounts from generateSummary
     * @param percentScale the scale to round to
     * @param clazz the class of the Purchasing Accounts Payable Account
     * @return a list of new Purchasing Accounts Payable Accounts
     */
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(List<PurApAccountingLine> accounts, Integer percentScale);

    /**
     * 
     * This creates summary accounts based on a list of items.
     * @param document the document to generate the summary accounts from
     * @return a list of summary accounts.
     */
    public List<SummaryAccount> generateSummaryAccounts(PurchasingAccountsPayableDocument document);

    /**
     * 
     * This creates summary accounts based on a list of items excluding zero totals.
     * @param document the document to generate the summary accounts from
     * @return a list of summary accounts.
     */
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document);

    /**
     * 
     * Generates an account summary, that is it creates a list of source accounts
     * by rounding up the Purchasing Accounts Payable accounts off of the Purchasing Accounts Payable items.
     *
     * @param document the document to generate the summary from
     * @return a list of source accounts
     */
    public List<SourceAccountingLine> generateSummary(List<PurApItem> items);
    
    /**
     * 
     * convenience method that generates a list of source accounts while excluding items with
     * $0 amounts
     * 
     * @param items the items to generate source accounts from
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurApItem> items);
    
    /**
     * 
     * convenience method that generates a list of source accounts while excluding items with
     * $0 amounts and using the alternate amount
     * 
     * @param items the items to generate source accounts from
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items);
    
    /**
     * 
     * convenience method that generates a list of source accounts while excluding items with
     * the specified item types
     * 
     * @param items the items to generate source accounts from
     * @param excludedItemTypeCodes the item types to exclude
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes);

    /**
     * 
     * convenience method that generates a list of source accounts while excluding items with
     * the specified item types and not including items with zero totals
     * 
     * @param items the items to generate source accounts from
     * @param excludedItemTypeCodes the item types to exclude
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes);

    /**
     * 
     * convenience method that generates a list of source accounts while only including items with
     * the specified item types
     * 
     * @param items the items to generate source accounts from
     * @param excludedItemTypeCodes the item types to include
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurApItem> items, Set includedItemTypeCodes);

    /**
     * 
     * convenience method that generates a list of source accounts while only including items with
     * the specified item types and not including items with zero totals
     * 
     * @param items the items to generate source accounts from
     * @param excludedItemTypeCodes the item types to include
     * @return a list of source accounts "rolled up" from the purap accounts 
     */
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes);

    /**
     * This method updates account amounts based on the percents.
     * 
     * @param document the document
     */
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document);
    /**
     * This method updates a single items account amounts
     * 
     * @param item
     */
    public void updateItemAccountAmounts(PurApItem item);

    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item);
    
    /**
     * Deletes the payment request summary accounts by purapDocumentIdentifier
     * 
     * @param purapDocumentIdentifier  The purapDocumentIdentifier of the payment request document whose summary accounts are to be deleted.
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier);
}
