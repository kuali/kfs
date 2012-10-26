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
package org.kuali.kfs.module.purap.service;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
    @Deprecated
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
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapdoc);

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
     * This creates summary accounts based on a list of items excluding zero totals and use tax.
     * @param document the document to generate the summary accounts from
     * @return a list of summary accounts.
     */
    public List<SummaryAccount> generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document);


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
     * Generates an account summary with only taxable accounts.
     *
     * @param items
     * @return
     */
    public List<SourceAccountingLine> generateSummaryTaxableAccounts(List<PurApItem> items);

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
     * $0 amounts and use tax
     *
     * @param items the items to generate source accounts from
     * @return a list of source accounts "rolled up" from the purap accounts
     */
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items);

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
     * Updates account amounts based on the percents.  If this is a preq past full entry it updates the
     * percents based on the amounts instead
     *
     * @param document the document
     */
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document);

    /**
     * Updates a single items account amounts
     *
     * @param item
     */
    public void updateItemAccountAmounts(PurApItem item);

    /**
     * Updates a single preq item accounts amounts
     *
     * @param item
     */
    public void updatePreqItemAccountAmounts(PurApItem item);

    /**
     * Updates a single preq item accounts amounts
     *
     * @param item
     */
    public void updatePreqProportionalItemAccountAmounts(PurApItem item);

    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item);

    /**
     * Deletes the ap summary accounts by the id of the doc type (Payment Request - PREQ or Credit Memo - CM)
     *
     * @param purapDocumentIdentifier  The purapDocumentIdentifier of the document whose summary accounts are to be deleted.
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier, String docType);

    /**
     * Retrieves the ap summary accounts by the id of the doc type (Payment Request - PREQ or Credit Memo - CM)
     *
     * @param purapDocumentIdentifier  The purapDocumentIdentifier of the document.
     */
    public List getAccountsPayableSummaryAccounts(Integer purapDocumentIdentifier, String docType);

    /**
     *
     * This method generates summary accounts for a vendor payment.
     * @param document
     * @return   This will get the proper amount on the items that is sent to the vendor
     */
    public List<SourceAccountingLine> generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document);

    /**
     * Converts the amount to percent and updates the percent field on the CreditMemoAccount
     *
     * @param pr The payment request document containing the accounts whose percentage would be set.
     */
    public void convertMoneyToPercent(PaymentRequestDocument pr);

    /**
     * Generates use tax helper class for a purap document
     * @param document
     * @return useTaxContainer
     */
    public List<UseTaxContainer> generateUseTaxAccount(PurchasingAccountsPayableDocument document);

    /**
     * Checks whether the specified accounting line in the specified PurAP document is used for tax withholding.
     * This applies only to PaymentRequestDocument; otherwise it always returns false.
     * @param document the specified PurAP document
     * @param account the specified accounting line
     * @return true if the accounting line is a tax account
     */
    public boolean isTaxAccount(PurchasingAccountsPayableDocument document, SourceAccountingLine account);

    /**
     * calculates values for a list of accounting lines based on an amount
     *
     * @param <T>
     * @param sourceAccountingLines
     * @param totalAmount
     */
    public <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount);

    /**
     * calculates values for a list of accounting lines based on an amount taking discount into account
     *
     * @param <T>
     * @param sourceAccountingLines
     * @param totalAmount
     * @param discountAmount
     */
    public <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount, KualiDecimal discountAmount);

    /**
     * calculates values for a list of accounting lines based on an amount on preq for sequential method.
     *
     * @param <T>
     * @param sourceAccountingLines
     * @param totalAmount
     */
    public <T extends PurApAccountingLine> void updatePreqAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount);

    /**
     * calculates values for a list of accounting lines based on an amount on preq for proportional method.
     *
     * @param <T>
     * @param sourceAccountingLines
     * @param totalAmount
     */
    public <T extends PurApAccountingLine> void updatePreqProporationalAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount);

    /**
     * Merges list 2 into list 1
     *
     * @param list1
     * @param list2
     * @return
     */
    public List<SourceAccountingLine> mergeAccountingLineLists(List<SourceAccountingLine> accountingLines1, List<SourceAccountingLine> accountingLines2);

    /**
     * Retrieves the summary accounts by payment request document id.
     *
     * @param paymentRequestIdentifier - payment request document id
     * @return List of SummaryAccounts
     */
    public List getSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier);

    /**
     * Retrieves the summary accounts by credit memo document id.
     *
     * @param creditMemoIdentifier - credit memo document id
     * @return List of SummaryAccounts
     */
    public List getSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier);

}
