/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
@Transactional
public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PurApLineDao purApLineDao;


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#resetSelectedValue(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void resetSelectedValue(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                item.setSelectedValue(false);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processAdditionalChargeAllocate(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean processAllocate(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines) {
        boolean allocatedIndicator = true;
        // Maintain this account List for update. So accounts already allocated won't take effect for account not allocated yet.
        List<PurchasingAccountsPayableLineAssetAccount> newAccountList = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : selectedLineItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            // Get allocate to target account list
            List<PurchasingAccountsPayableLineAssetAccount> targetAccounts = getAllocationTargetAccounts(sourceAccount, allocateTargetLines, selectedLineItem.isAdditionalChargeNonTradeInIndicator() | selectedLineItem.isTradeInAllowance());
            if (targetAccounts.isEmpty()) {
                allocatedIndicator = false;
                break;
            }
            else {
                // Percentage amount to each target account
                allocateByItemAccountAmount(sourceAccount, targetAccounts, newAccountList);
            }
        }

        if (allocatedIndicator) {
            addNewAccountToItemList(newAccountList);
            updateLineItemsCost(allocateTargetLines);
        }

        return allocatedIndicator;
    }

    /**
     * Reset item total cost and unit cost for each item.
     * 
     * @param allocateTargetLines
     */
    private void updateLineItemsCost(List<PurchasingAccountsPayableItemAsset> lineItems) {
        // update target item unit cost and total cost
        for (PurchasingAccountsPayableItemAsset item : lineItems) {
            setLineItemCost(item);
        }
    }

    /**
     * update account list for each line item
     * 
     * @param updatedAccountList
     */
    private void addNewAccountToItemList(List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
        PurchasingAccountsPayableItemAsset lineItem = null;
        for (PurchasingAccountsPayableLineAssetAccount newAccount : newAccountList) {
            lineItem = newAccount.getPurchasingAccountsPayableItemAsset();
            if (ObjectUtils.isNotNull(lineItem) && ObjectUtils.isNotNull(lineItem.getPurchasingAccountsPayableAssetDetails())) {
                lineItem.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount);
            }
        }
    }

    /**
     * Set line item total cost and unit cost.
     * 
     * @param item
     */
    private void setLineItemCost(PurchasingAccountsPayableItemAsset item) {
        KualiDecimal totalCost = calculateItemAssetTotalCost(item);
        item.setTotalCost(totalCost);
        setItemAssetUnitCost(item, totalCost);
    }

    /**
     * Allocate one account line to target account lines percentage based on the account line amount.
     * 
     * @param sourceAccount Account line to be allocated.
     * @param targetAccounts Account lines which accept amount.
     */
    private void allocateByItemAccountAmount(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
        KualiDecimal targetAccountsTotalAmount = KualiDecimal.ZERO;
        KualiDecimal sourceAccountTotalAmount = sourceAccount.getItemAccountTotalAmount();
        KualiDecimal amountAllocated = KualiDecimal.ZERO;
        KualiDecimal additionalAmount = null;

        // Calculate the targetAccountTotalAmount.
        for (PurchasingAccountsPayableLineAssetAccount targetAccount : targetAccounts) {
            targetAccountsTotalAmount = targetAccountsTotalAmount.add(targetAccount.getItemAccountTotalAmount());
        }

        for (Iterator iterator = targetAccounts.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableLineAssetAccount targetAccount = (PurchasingAccountsPayableLineAssetAccount) iterator.next();
            if (iterator.hasNext()) {
                // Not working on the last node. Calculate additional charge amount by percentage
                additionalAmount = targetAccount.getItemAccountTotalAmount().multiply(sourceAccountTotalAmount).divide(targetAccountsTotalAmount);
                amountAllocated = amountAllocated.add(additionalAmount);
            }
            else {
                // Working on the last node, set the additional charge amount to the rest of sourceAccountTotalAmount.
                additionalAmount = sourceAccountTotalAmount.subtract(amountAllocated);
            }

            PurchasingAccountsPayableLineAssetAccount newAccount = getMatchingFromAccountList(targetAccounts, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
            if (newAccount != null) {
                // If exists the same GL entry, update the newItemAccountTotalAmount
                updateAccountAmount(additionalAmount, newAccount);
            }
            else {
                newAccount = getMatchingFromAccountList(newAccountList, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
                if (newAccount != null) {
                    updateAccountAmount(additionalAmount, newAccount);
                }
                else {
                    // If the target account is a different GL entry, create a new account and attach to this line item.
                    targetAccount = new PurchasingAccountsPayableLineAssetAccount(targetAccount.getPurchasingAccountsPayableItemAsset(), sourceAccount.getGeneralLedgerAccountIdentifier());
                    targetAccount.setItemAccountTotalAmount(additionalAmount);
                    targetAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
                    newAccountList.add(targetAccount);
                }
            }
        }
    }

    /**
     * Search matching account in targetAccounts by glIdentifier.
     * 
     * @param targetAccounts
     * @param glIdentifier
     * @return
     */
    private PurchasingAccountsPayableLineAssetAccount getFromTargetAccountList(List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, Long glIdentifier) {
        for (PurchasingAccountsPayableLineAssetAccount account : targetAccounts) {
            if (account.getGeneralLedgerAccountIdentifier().equals(glIdentifier)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Update targetAccount by additionalAmount. This method...
     * 
     * @param additionalAmount
     * @param targetAccount
     */
    private void updateAccountAmount(KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount targetAccount) {
        KualiDecimal baseAmount = targetAccount.getItemAccountTotalAmount();
        targetAccount.setItemAccountTotalAmount(baseAmount != null ? baseAmount.add(additionalAmount) : additionalAmount);
    }


    /**
     * Searching in accountList by glIdentifier for matching account which associated with the same item as targetAccount.
     * 
     * @param accountList
     * @param glIdentifier
     * @param targetAccount
     * @return
     */
    private PurchasingAccountsPayableLineAssetAccount getMatchingFromAccountList(List<PurchasingAccountsPayableLineAssetAccount> accountList, Long glIdentifier, PurchasingAccountsPayableLineAssetAccount targetAccount) {
        for (PurchasingAccountsPayableLineAssetAccount newAccount : accountList) {
            if (StringUtils.equalsIgnoreCase(targetAccount.getDocumentNumber(), newAccount.getDocumentNumber()) && targetAccount.getAccountsPayableLineItemIdentifier().equals(newAccount.getAccountsPayableLineItemIdentifier()) && targetAccount.getCapitalAssetBuilderLineNumber().equals(newAccount.getCapitalAssetBuilderLineNumber()) && glIdentifier.equals(newAccount.getGeneralLedgerAccountIdentifier())) {
                return newAccount;
            }
        }
        return null;
    }

    /**
     * For additional charge allocation, target account selection is based on account lines with the same account number and object
     * code. If no matching, select all account lines. For line item to line items, select all account lines as target.
     * 
     * @param sourceAccount
     * @param lineItems
     * @param addtionalCharge
     * @return
     */
    private List<PurchasingAccountsPayableLineAssetAccount> getAllocationTargetAccounts(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableItemAsset> lineItems, boolean addtionalCharge) {
        GeneralLedgerEntry candidateEntry = null;
        GeneralLedgerEntry sourceEntry = sourceAccount.getGeneralLedgerEntry();
        List<PurchasingAccountsPayableLineAssetAccount> matchingAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableLineAssetAccount> allAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        for (PurchasingAccountsPayableItemAsset item : lineItems) {
            for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                candidateEntry = account.getGeneralLedgerEntry();
                account.setPurchasingAccountsPayableItemAsset(item);

                if (ObjectUtils.isNotNull(candidateEntry)) {
                    allAccounts.add(account);
                    if (addtionalCharge & isValidTargetAccount(sourceEntry, candidateEntry)) {
                        matchingAccounts.add(account);
                    }
                }
            }
        }

        return matchingAccounts.isEmpty() ? allAccounts : matchingAccounts;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getAllocateTargetLines(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public List<PurchasingAccountsPayableItemAsset> getAllocateTargetLines(PurchasingAccountsPayableItemAsset selectedLineItem, PurApLineForm purApForm) {
        List<PurchasingAccountsPayableItemAsset> targetLineItems = new TypedArrayList(PurchasingAccountsPayableItemAsset.class);

        for (PurchasingAccountsPayableDocument purApDoc : purApForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item != selectedLineItem && ((selectedLineItem.isAdditionalChargeNonTradeInIndicator() && !item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && StringUtils.equalsIgnoreCase(selectedLineItem.getDocumentNumber(), item.getDocumentNumber())) || (selectedLineItem.isTradeInAllowance() && item.isItemAssignedToTradeInIndicator()) || (item.isSelectedValue()))) {
                    targetLineItems.add(item);
                }
            }
        }
        return targetLineItems;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getSelectedMergeLines(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public List<PurchasingAccountsPayableItemAsset> getSelectedMergeLines(PurApLineForm purApLineForm) {
        List<PurchasingAccountsPayableItemAsset> mergeLines = new TypedArrayList(PurchasingAccountsPayableItemAsset.class);
        boolean mergeAll = isMergeAllAction(purApLineForm);

        if (mergeAll) {
            boolean tradeInPending = isTradeInAllocPending(purApLineForm);
        }

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if ((!mergeAll && item.isSelectedValue()) || (mergeAll && !item.isTradeInAllowance())) {
                    mergeLines.add(item);
                    item.setPurchasingAccountsPayableDocument(purApDoc);
                }
            }
        }
        return mergeLines;
    }

    /**
     * Check if there is pending allocation for trade-in allowance.
     * 
     * @param purApLineForm
     * @return
     */
    private boolean isTradeInAllocPending(PurApLineForm purApLineForm) {
        boolean tradeInIndicator = false;
        boolean tradeInAllowance = false;

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                tradeInAllowance |= item.isTradeInAllowance();
                tradeInIndicator |= item.isItemAssignedToTradeInIndicator();

                if (tradeInAllowance & tradeInIndicator) {
                    break;
                }
            }
        }
        // When both trade-in allowance and trade-in indicator exist, trade-in allocation is pending.
        return tradeInAllowance & tradeInIndicator;
    }

    /**
     * Check if the merge action is merge all.
     * 
     * @param purApLineForm
     * @return
     */
    private boolean isMergeAllAction(PurApLineForm purApLineForm) {
        boolean mergeAll = true;

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // When there is one item line not selected, mergeAll is false
                if (!item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && !item.isSelectedValue()) {
                    mergeAll = false;
                    break;
                }
            }
        }
        return mergeAll;
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processMerge(java.util.List)
     */
    public void processMerge(List<PurchasingAccountsPayableItemAsset> mergeLines) {
        PurchasingAccountsPayableItemAsset firstItem = mergeLines.get(0);
        List<PurchasingAccountsPayableLineAssetAccount> firstAccountList = firstItem.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableItemAsset item = null;
        PurchasingAccountsPayableLineAssetAccount targetAccount = null;

        // Merge item accounts to the first line.
        for (int i = 1; i < mergeLines.size(); i++) {
            item = mergeLines.get(i);
            for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                // Check if we can grouping the accounts. If yes, update the account amount without moving account line.
                targetAccount = getFromTargetAccountList(firstAccountList, account.getGeneralLedgerAccountIdentifier());
                if (targetAccount != null) {
                    updateAccountAmount(account.getItemAccountTotalAmount(), targetAccount);
                }
                else {
                    account.setDocumentNumber(firstItem.getDocumentNumber());
                    account.setAccountsPayableLineItemIdentifier(firstItem.getAccountsPayableLineItemIdentifier());
                    account.setCapitalAssetBuilderLineNumber(firstItem.getCapitalAssetBuilderLineNumber());
                    firstAccountList.add(account);
                }
            }
        }
        setLineItemCost(firstItem);
    }

    /**
     * Validation based on account number and object code.
     * 
     * @param sourceEntry
     * @param candidateEntry
     * @return
     */
    private boolean isValidTargetAccount(GeneralLedgerEntry sourceEntry, GeneralLedgerEntry candidateEntry) {
        if (!StringUtils.equalsIgnoreCase(sourceEntry.getAccountNumber(), candidateEntry.getAccountNumber()) || !StringUtils.equalsIgnoreCase(sourceEntry.getFinancialObjectCode(), candidateEntry.getFinancialObjectCode())) {
            return false;
        }
        return true;
    }

    private KualiDecimal getItemsTotalCost(List<PurchasingAccountsPayableItemAsset> targetItems) {
        KualiDecimal totalCost = KualiDecimal.ZERO;

        for (PurchasingAccountsPayableItemAsset itemAsset : targetItems) {
            totalCost = totalCost.add(itemAsset.getTotalCost());
        }

        return totalCost;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processPercentPayment(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset) {
        KualiDecimal oldQty = itemAsset.getCapitalAssetBuilderQuantity();
        KualiDecimal newQty = new KualiDecimal(1);
        // update quantity and unit cost.
        if (oldQty.isLessThan(newQty)) {
            itemAsset.setCapitalAssetBuilderQuantity(newQty);
            itemAsset.setTotalCost(calculateItemAssetTotalCost(itemAsset));
            // unit cost will be the same value as total cost since quantity is updated to 1.
            itemAsset.setUnitCost(itemAsset.getTotalCost());
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSplit(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public PurchasingAccountsPayableItemAsset processSplit(PurchasingAccountsPayableItemAsset currentItemAsset) {
        // create a new item asset from the current item asset.
        PurchasingAccountsPayableItemAsset newItemAsset = new PurchasingAccountsPayableItemAsset(currentItemAsset);

        Integer maxCabLineNbr = purApLineDao.getMaxCabLineNumber(currentItemAsset.getDocumentNumber(), currentItemAsset.getAccountsPayableLineItemIdentifier());
        newItemAsset.setCapitalAssetBuilderLineNumber(++maxCabLineNbr);
        newItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getSplitQty());

        // Set account list for new item asset and update current account amount value.
        createAccountsForNewItemAsset(currentItemAsset, newItemAsset);

        setLineItemCost(newItemAsset);

        // Adjust current item asset quantity, total cost and unit cost
        currentItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getCapitalAssetBuilderQuantity().subtract(currentItemAsset.getSplitQty()));
        setLineItemCost(currentItemAsset);
        currentItemAsset.setSplitQty(null);

        return newItemAsset;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#saveBusinessObject(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void processSaveBusinessObjects(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            businessObjectService.save(purApDoc);
        }
        // Removed below codes together with PurApLineSession when using buildListOfDeletionAwareLists is approved.
        // List itemAssets = purApDoc.getPurchasingAccountsPayableItemAssets();
        // if (itemAssets != null && !itemAssets.isEmpty()) {
        // businessObjectService.save(itemAssets);
        // }
    }

    //
    // // delete allocated items
    // if (purApLineSession != null) {
    // List deletedItems = purApLineSession.getDeletedItemAssets();
    // if (deletedItems != null && !deletedItems.isEmpty()) {
    // businessObjectService.delete(deletedItems);
    // purApLineSession.getDeletedItemAssets().removeAll(deletedItems);
    // }
    // }
    // }

    /**
     * Create asset account list for new item asset and update the current account amount.
     * 
     * @param oldItemAsset old line item.
     * @param newItemAsset new line item.
     */
    private void createAccountsForNewItemAsset(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset) {
        KualiDecimal currentQty = currentItemAsset.getCapitalAssetBuilderQuantity();
        KualiDecimal splitQty = currentItemAsset.getSplitQty();
        List<PurchasingAccountsPayableLineAssetAccount> accountsList = newItemAsset.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableLineAssetAccount newAccount;
        for (PurchasingAccountsPayableLineAssetAccount currentAccount : currentItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            // create accounts for new item asset.
            newAccount = new PurchasingAccountsPayableLineAssetAccount(currentItemAsset, currentAccount.getGeneralLedgerAccountIdentifier());
            newAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().multiply(splitQty).divide(currentQty));
            newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
            newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.PURAP_ITEM_ASSET);
            accountsList.add(newAccount);

            // Adjust account amount for split item
            currentAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().subtract(newAccount.getItemAccountTotalAmount()));
        }
    }


    /**
     * Set object code by the first one from the accounting lines.
     * 
     * @param item Selected line item.
     */
    private void setFirstFinancialObjectCode(PurchasingAccountsPayableItemAsset item) {
        String firstFinancialObjectCode = null;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            if (ObjectUtils.isNotNull(account.getGeneralLedgerEntry())) {
                firstFinancialObjectCode = account.getGeneralLedgerEntry().getFinancialObjectCode();
            }
        }
        item.setFirstFincialObjectCode(firstFinancialObjectCode);
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#setPurchaseOrderInfo(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void setPurchaseOrderInfo(PurApLineForm purApLineForm) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(PurapPropertyConstants.PURAP_DOC_ID, purApLineForm.getPurchaseOrderIdentifier());
        cols.put(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");
        Collection<PurchaseOrderDocument> poDocs = businessObjectService.findMatching(PurchaseOrderDocument.class, cols);

        for (PurchaseOrderDocument purchaseOrderDocument : poDocs) {
            if (purchaseOrderDocument.getInstitutionContactEmailAddress() != null) {
                purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getInstitutionContactEmailAddress());
            }
            else if (purchaseOrderDocument.getRequestorPersonEmailAddress() != null) {
                purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getRequestorPersonEmailAddress());
            }

            if (purchaseOrderDocument.getInstitutionContactPhoneNumber() != null) {
                purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getInstitutionContactPhoneNumber());
            }
            else if (purchaseOrderDocument.getRequestorPersonPhoneNumber() != null) {
                purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getRequestorPersonPhoneNumber());
            }

            break;
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#buildPurApItemAssetsList(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void buildPurApItemAssetsList(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // set fields from PurAp tables
                setCabItemFieldsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // set line item unit cost and total cost
                setLineItemCost(item);

                // set financial object code
                setFirstFinancialObjectCode(item);
            }

            Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
        }

    }

    /**
     * Set item asset unit cost.
     * 
     * @param item line item
     * @param totalCost total cost for this line item.
     */
    private void setItemAssetUnitCost(PurchasingAccountsPayableItemAsset item, KualiDecimal totalCost) {
        // set unit cost
        KualiDecimal quantity = item.getAccountsPayableItemQuantity();
        if (quantity != null && quantity.isNonZero()) {
            item.setUnitCost(totalCost.divide(quantity));
        }
    }

    /**
     * Calculate item asset total cost
     * 
     * @param item
     * @return line item total cost
     */
    public KualiDecimal calculateItemAssetTotalCost(PurchasingAccountsPayableItemAsset item) {
        // Calculate and set total cost
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            totalCost = totalCost.add(account.getItemAccountTotalAmount());
        }
        return totalCost;
    }


    /**
     * Set CAB line item information from PurAp PaymentRequestItem or CreditMemoItem.
     * 
     * @param purchasingAccountsPayableItemAsset
     * @param docTypeCode
     */
    private void setCabItemFieldsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());

        // Access PurAp data based on item type(PREQ or CM).
        if (CabConstants.PREQ.equalsIgnoreCase(docTypeCode)) {
            PaymentRequestItem item = (PaymentRequestItem) businessObjectService.findByPrimaryKey(PaymentRequestItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
        }
        else {
            CreditMemoItem item = (CreditMemoItem) businessObjectService.findByPrimaryKey(CreditMemoItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
        }
        pKeys.clear();
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public PurApLineDao getPurApLineDao() {
        return purApLineDao;
    }

    public void setPurApLineDao(PurApLineDao purApLineDao) {
        this.purApLineDao = purApLineDao;
    }

}
