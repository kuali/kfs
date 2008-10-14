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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.ExistsCriteria;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.sys.context.SpringContext;
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
    private PurchaseOrderService purchaseOrderService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineDocumentService#inActivateDocument(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument)
     */
    public void inActivateDocument(PurchasingAccountsPayableDocument selectedDoc) {
        for (PurchasingAccountsPayableItemAsset item : selectedDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.isActive()) {
                return;
            }
        }
        selectedDoc.setActive(false);
    }

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
    public boolean processAllocate(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, PurApLineForm purApForm, PurApLineSession purApLineSession) {
        boolean allocatedIndicator = true;
        // Maintain this account List for update. So accounts already allocated won't take effect for account not allocated yet.
        List<PurchasingAccountsPayableLineAssetAccount> newAccountList = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableActionHistory> actionsTakeHistory = new ArrayList<PurchasingAccountsPayableActionHistory>();

        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : selectedLineItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            // Get allocate to target account list
            List<PurchasingAccountsPayableLineAssetAccount> targetAccounts = getAllocateTargetAccounts(sourceAccount, allocateTargetLines, selectedLineItem.isAdditionalChargeNonTradeInIndicator() | selectedLineItem.isTradeInAllowance());
            if (!targetAccounts.isEmpty()) {
                // Percentage amount to each target account
                allocateByItemAccountAmount(sourceAccount, targetAccounts, newAccountList, actionsTakeHistory, selectedLineItem.getAccountsPayableItemQuantity());
            }
            else {
                allocatedIndicator = false;
                break;
            }
        }

        if (allocatedIndicator) {
            postAllocateProcess(selectedLineItem, allocateTargetLines, purApForm, newAccountList);
            // add to the action history
            purApLineSession.getActionsTakenHistory().addAll(actionsTakeHistory);
        }

        return allocatedIndicator;
    }

    /**
     * Process after allocate.
     * 
     * @param selectedLineItem
     * @param allocateTargetLines
     * @param purApForm
     * @param newAccountList
     * @param actionsTake
     */
    private void postAllocateProcess(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, PurApLineForm purApForm, List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
        // add new account into each line
        addNewAccountToItemList(newAccountList);

        // update total cost and unit cost.
        updateLineItemsCost(allocateTargetLines);

        // remove allocate source line item.
        if (ObjectUtils.isNotNull(selectedLineItem.getPurchasingAccountsPayableDocument())) {
            selectedLineItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(selectedLineItem);
            inActivateDocument(selectedLineItem.getPurchasingAccountsPayableDocument());
        }

        // Set create asset and apply payment indicator.
        if (selectedLineItem.isAdditionalChargeNonTradeInIndicator() | selectedLineItem.isTradeInAllowance()) {
            setAssetIndicator(purApForm);
        }

        // uncheck select check box
        resetSelectedValue(purApForm);
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
            if (ObjectUtils.isNotNull(lineItem) && ObjectUtils.isNotNull(lineItem.getPurchasingAccountsPayableLineAssetAccounts())) {
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
    private void allocateByItemAccountAmount(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, List<PurchasingAccountsPayableLineAssetAccount> newAccountList, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, KualiDecimal sourceQty) {
        KualiDecimal targetAccountsTotalAmount = KualiDecimal.ZERO;
        KualiDecimal sourceAccountTotalAmount = sourceAccount.getItemAccountTotalAmount();
        KualiDecimal amountAllocated = KualiDecimal.ZERO;
        KualiDecimal additionalAmount = null;

        // Calculate the targetAccountTotalAmount.
        for (PurchasingAccountsPayableLineAssetAccount targetAccount : targetAccounts) {
            targetAccountsTotalAmount = targetAccountsTotalAmount.add(targetAccount.getItemAccountTotalAmount());
        }

        for (Iterator<PurchasingAccountsPayableLineAssetAccount> iterator = targetAccounts.iterator(); iterator.hasNext();) {
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

            // Code below mainly handle grouping account lines if they're from the same GL.
            PurchasingAccountsPayableLineAssetAccount newAccount = getMatchingFromAccountList(targetAccounts, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
            if (newAccount != null) {
                // If exists the same account line and GL entry, update its itemAccountTotalAmount. This account line could be other
                // than targetAccount, but must belong to the same line item.
                updateAccountAmount(additionalAmount, newAccount);
            }
            else {
                // If exist account just created, grouping them and update the account amount.
                newAccount = getMatchingFromAccountList(newAccountList, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
                if (newAccount != null) {
                    updateAccountAmount(additionalAmount, newAccount);
                }
                else {
                    // If the target account is a different GL entry, create a new account and attach to this line item.
                    newAccount = new PurchasingAccountsPayableLineAssetAccount(targetAccount.getPurchasingAccountsPayableItemAsset(), sourceAccount.getGeneralLedgerAccountIdentifier());
                    newAccount.setItemAccountTotalAmount(additionalAmount);
                    newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
                    newAccountList.add(newAccount);
                }
            }

            // add to action history
            addAllocateHistory(sourceAccount, actionsTakeHistory, additionalAmount, newAccount);
        }
    }

    private void addAllocateHistory(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount newAccount) {
        if (ObjectUtils.isNull(sourceAccount.getPurchasingAccountsPayableItemAsset())) {
            sourceAccount.refreshReferenceObject("purchasingAccountsPayableItemAsset");
        }
        if (ObjectUtils.isNull(newAccount.getPurchasingAccountsPayableItemAsset())) {
            newAccount.refreshReferenceObject("purchasingAccountsPayableItemAsset");
        }
        PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(sourceAccount.getPurchasingAccountsPayableItemAsset(), newAccount.getPurchasingAccountsPayableItemAsset(), CabConstants.Actions.ALLOCATE);
        newAction.setGeneralLedgerAccountIdentifier(sourceAccount.getGeneralLedgerAccountIdentifier());
        newAction.setItemAccountTotalAmount(additionalAmount);
        newAction.setAccountsPayableItemQuantity(sourceAccount.getPurchasingAccountsPayableItemAsset().getAccountsPayableItemQuantity());
        actionsTakeHistory.add(newAction);
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
     * Update targetAccount by additionalAmount.
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
        for (PurchasingAccountsPayableLineAssetAccount account : accountList) {
            if (StringUtils.equalsIgnoreCase(targetAccount.getDocumentNumber(), account.getDocumentNumber()) && targetAccount.getAccountsPayableLineItemIdentifier().equals(account.getAccountsPayableLineItemIdentifier()) && targetAccount.getCapitalAssetBuilderLineNumber().equals(account.getCapitalAssetBuilderLineNumber()) && glIdentifier.equals(account.getGeneralLedgerAccountIdentifier())) {
                return account;
            }
        }
        return null;
    }

    /**
     * Get the target account lines which will be used for allocate.
     * 
     * @param sourceAccount
     * @param lineItems
     * @param addtionalCharge
     * @return
     */
    private List<PurchasingAccountsPayableLineAssetAccount> getAllocateTargetAccounts(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableItemAsset> lineItems, boolean addtionalCharge) {
        GeneralLedgerEntry candidateEntry = null;
        GeneralLedgerEntry sourceEntry = sourceAccount.getGeneralLedgerEntry();
        List<PurchasingAccountsPayableLineAssetAccount> matchingAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableLineAssetAccount> allAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        // For additional charge allocation, target account selection is based on account lines with the same account number and
        // object code. If no matching, select all account lines. For line item to line items, select all account lines as target.
        for (PurchasingAccountsPayableItemAsset item : lineItems) {
            for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                candidateEntry = account.getGeneralLedgerEntry();
                account.setPurchasingAccountsPayableItemAsset(item);

                if (ObjectUtils.isNotNull(candidateEntry)) {
                    allAccounts.add(account);
                    // For additional charge, select matching account when account number and object code both match.
                    if (addtionalCharge & StringUtils.equalsIgnoreCase(sourceEntry.getAccountNumber(), candidateEntry.getAccountNumber()) & StringUtils.equalsIgnoreCase(sourceEntry.getFinancialObjectCode(), candidateEntry.getFinancialObjectCode())) {
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
                // If selected Line is additional charge line, get target lines from the same document.
                // Else if selected Line is trade in allowance, target lines are item lines with TI indicator set.
                // Otherwise, select items with check box set.
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

        boolean excludeTradeInAllowance = false;

        // Handle one of the merge all situation when TI allowance exist without TI indicator set lines.
        if (mergeAll && !isTradeInIndicatorExist(purApLineForm) & isTradeInAllowanceExist(purApLineForm)) {
            excludeTradeInAllowance = true;
        }

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // If not merge all action, select items are the merge lines. If it is merge all action, trade-in allowance is
                // exception when trade-in indicator not exists.
                if ((!mergeAll & item.isSelectedValue()) || (mergeAll && (!excludeTradeInAllowance || !item.isTradeInAllowance()))) {
                    mergeLines.add(item);
                    item.setPurchasingAccountsPayableDocument(purApDoc);
                }
            }
        }
        return mergeLines;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isTradeInAllowanceExist(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isTradeInAllowanceExist(PurApLineForm purApLineForm) {
        boolean tradeInAllowance = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isTradeInAllowance()) {
                    tradeInAllowance = true;
                    break;
                }
            }
        }
        return tradeInAllowance;
    }

    /**
     * Check if TI indicator exists in all form lines
     * 
     * @param purApLineForm
     * @return
     */
    private boolean isTradeInIndicatorExist(PurApLineForm purApLineForm) {
        boolean tradeInIndicator = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isItemAssignedToTradeInIndicator()) {
                    tradeInIndicator = true;
                    break;
                }
            }
        }
        return tradeInIndicator;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isTradeInIndicatorExist(java.util.List)
     */
    public boolean isTradeInIndicatorExist(List<PurchasingAccountsPayableItemAsset> itemAssets) {
        boolean tradeInIndicator = false;
        for (PurchasingAccountsPayableItemAsset item : itemAssets) {
            if (item.isItemAssignedToTradeInIndicator()) {
                tradeInIndicator = true;
                break;
            }
        }
        return tradeInIndicator;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargePending(java.util.List,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isAdditionalChargePending(List<PurchasingAccountsPayableItemAsset> itemAssets) {
        boolean additionalChargePending = false;
        Boolean diffDocment = false;
        PurchasingAccountsPayableItemAsset firstAsset = itemAssets.get(0);
        PurchasingAccountsPayableItemAsset lastAsset = itemAssets.get(itemAssets.size() - 1);

        // Check if itemAssets are in different PurAp Document. itemAssets is a sorted list which has item assets from the same
        // document grouping together.
        if (ObjectUtils.isNotNull(firstAsset) && ObjectUtils.isNotNull(lastAsset) && !firstAsset.getDocumentNumber().equalsIgnoreCase(lastAsset.getDocumentNumber())) {
            diffDocment = true;
        }

        // check if item assets from different document have additional charges not allocated yet.
        if (diffDocment) {
            for (PurchasingAccountsPayableItemAsset item : itemAssets) {
                if (ObjectUtils.isNotNull(item.getPurchasingAccountsPayableDocument())) {
                    for (PurchasingAccountsPayableItemAsset itemLine : item.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets()) {
                        if (itemLine.isAdditionalChargeNonTradeInIndicator()) {
                            additionalChargePending = true;
                            return additionalChargePending;
                        }
                    }
                }
            }
        }
        return additionalChargePending;
    }

    /**
     * Check if the merge action is merge all.
     * 
     * @param purApLineForm
     * @return
     */
    public boolean isMergeAllAction(PurApLineForm purApLineForm) {
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
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargeExist(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isAdditionalChargeExist(PurApLineForm purApForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isAdditionalChargeNonTradeInIndicator()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processMerge(java.util.List)
     */
    public void processMerge(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm, PurApLineSession purApLineSession, Pretag mergeTargetPretag) {
        PurchasingAccountsPayableItemAsset firstItem = mergeLines.get(0);
        List<PurchasingAccountsPayableLineAssetAccount> firstAccountList = firstItem.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableItemAsset item = null;
        PurchasingAccountsPayableLineAssetAccount targetAccount = null;

        preMergeProcess(firstItem, purApForm);

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

        // update action history, remove lines before merge and clean up the user input
        postMergeProcess(mergeLines, purApForm, purApLineSession, mergeTargetPretag);
    }

    /**
     * Process before merge.
     * 
     * @param firstItem
     * @param purApForm
     */
    private void preMergeProcess(PurchasingAccountsPayableItemAsset firstItem, PurApLineForm purApForm) {
        // Set new value for quantity and description.
        firstItem.setAccountsPayableItemQuantity(purApForm.getMergeQty());
        firstItem.setAccountsPayableLineItemDescription(purApForm.getMergeDesc());
    }

    /**
     * Process after merge.
     * 
     * @param mergeLines
     * @param purApForm
     */
    private void postMergeProcess(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm, PurApLineSession purApLineSession, Pretag mergeTargetPretag) {
        boolean isMergeAllAction = isMergeAllAction(purApForm);
        boolean existCreateAsset = false;
        boolean existApplyPayment = false;
        PurchasingAccountsPayableItemAsset firstItem = mergeLines.get(0);
        setLineItemCost(firstItem);
        firstItem.setItemAssignedToTradeInIndicator(false);
        for (int i = 1; i < mergeLines.size(); i++) {
            PurchasingAccountsPayableItemAsset item = mergeLines.get(i);
            existCreateAsset |= item.isCreateAssetIndicator();
            existApplyPayment |= item.isApplyPaymentIndicator();
            // Add into action history.
            addMergeHistory(purApLineSession, isMergeAllAction, firstItem, item);
            // remove mergeLines except the first one.
            if (ObjectUtils.isNotNull(item.getPurchasingAccountsPayableDocument())) {
                item.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(item);
                inActivateDocument(item.getPurchasingAccountsPayableDocument());
            }
        }
        // set the target preTag
        if (mergeTargetPretag != null) {
            firstItem.setItemLineNumber(mergeTargetPretag.getItemLineNumber());
        }

        // set create asset/ apply payment indicator if any of the merged lines has the indicator set.
        firstItem.setCreateAssetIndicator(firstItem.isCreateAssetIndicator() | existCreateAsset);
        firstItem.setApplyPaymentIndicator(firstItem.isApplyPaymentIndicator() | existApplyPayment);

        // reset user input values.
        resetSelectedValue(purApForm);
        purApForm.setMergeQty(null);
        purApForm.setMergeDesc(null);
    }

    /**
     * Add merge action to the action history.
     * 
     * @param purApLineSession
     * @param isMergeAllAction
     * @param firstItem
     * @param item
     */
    private void addMergeHistory(PurApLineSession purApLineSession, boolean isMergeAllAction, PurchasingAccountsPayableItemAsset firstItem, PurchasingAccountsPayableItemAsset item) {
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(item, firstItem, isMergeAllAction ? CabConstants.Actions.MERGE_ALL : CabConstants.Actions.MERGE);
            newAction.setAccountsPayableItemQuantity(item.getAccountsPayableItemQuantity());
            newAction.setItemAccountTotalAmount(account.getItemAccountTotalAmount());
            newAction.setGeneralLedgerAccountIdentifier(account.getGeneralLedgerAccountIdentifier());
            purApLineSession.getActionsTakenHistory().add(newAction);
        }
    }

    /**
     * Calculate item total cost.
     * 
     * @param targetItems
     * @return
     */
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
    public void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        KualiDecimal newQty = new KualiDecimal(1);
        // update quantity and unit cost.
        if (oldQty.isLessThan(newQty)) {
            itemAsset.setAccountsPayableItemQuantity(newQty);
            itemAsset.setTotalCost(calculateItemAssetTotalCost(itemAsset));
            // unit cost will be the same value as total cost since quantity is updated to 1.
            itemAsset.setUnitCost(itemAsset.getTotalCost());
        }
        // add to action history
        addPercentPaymentHistory(actionsTakenHistory, itemAsset, oldQty);
    }

    /**
     * Update action history for the percent payment action.
     * 
     * @param actionsTaken
     * @param item
     * @param oldQty
     */
    private void addPercentPaymentHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory, PurchasingAccountsPayableItemAsset item, KualiDecimal oldQty) {
        PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(item, item, CabConstants.Actions.PERCENT_PAYMENT);
        newAction.setAccountsPayableItemQuantity(oldQty);
        actionsTakenHistory.add(newAction);
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSplit(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public PurchasingAccountsPayableItemAsset processSplit(PurchasingAccountsPayableItemAsset currentItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory) {
        // create a new item asset from the current item asset.
        PurchasingAccountsPayableItemAsset newItemAsset = new PurchasingAccountsPayableItemAsset(currentItemAsset);

        Integer maxCabLineNbr = purApLineDao.getMaxCabLineNumber(currentItemAsset.getDocumentNumber(), currentItemAsset.getAccountsPayableLineItemIdentifier());
        newItemAsset.setCapitalAssetBuilderLineNumber(++maxCabLineNbr);
        newItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getSplitQty());

        // Set account list for new item asset and update current account amount value.
        createAccountsForNewItemAsset(currentItemAsset, newItemAsset);

        setLineItemCost(newItemAsset);

        // Adjust current item asset quantity, total cost and unit cost
        currentItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getAccountsPayableItemQuantity().subtract(currentItemAsset.getSplitQty()));
        setLineItemCost(currentItemAsset);
        currentItemAsset.setSplitQty(null);
        // Add to action history
        addSplitHistory(currentItemAsset, newItemAsset, actionsTakeHistory);
        return newItemAsset;
    }

    /**
     * Update action history for a split action.
     * 
     * @param currentItemAsset
     * @param newItemAsset
     * @param actionsTaken
     */
    private void addSplitHistory(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        for (PurchasingAccountsPayableLineAssetAccount account : newItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(currentItemAsset, newItemAsset, CabConstants.Actions.SPLIT);
            newAction.setGeneralLedgerAccountIdentifier(account.getGeneralLedgerAccountIdentifier());
            newAction.setAccountsPayableItemQuantity(newItemAsset.getAccountsPayableItemQuantity());
            newAction.setItemAccountTotalAmount(account.getItemAccountTotalAmount());
            actionsTakenHistory.add(newAction);
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#saveBusinessObject(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void processSaveBusinessObjects(PurApLineForm purApLineForm, PurApLineSession purApLineSession) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            businessObjectService.save(purApDoc);
        }
        if (purApLineSession != null) {
            // save for action history
            List<PurchasingAccountsPayableActionHistory> historyList = purApLineSession.getActionsTakenHistory();
            if (!historyList.isEmpty()) {
                businessObjectService.save(historyList);
                historyList.removeAll(historyList);
            }

            // save for generalLedgerEntry list
            List<GeneralLedgerEntry> glUpdateList = purApLineSession.getGlEntryUpdateList();
            if (!glUpdateList.isEmpty()) {
                businessObjectService.save(glUpdateList);
                glUpdateList.retainAll(glUpdateList);
            }
        }
    }


    /**
     * Create asset account list for new item asset and update the current account amount.
     * 
     * @param oldItemAsset old line item.
     * @param newItemAsset new line item.
     */
    private void createAccountsForNewItemAsset(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset) {
        KualiDecimal currentQty = currentItemAsset.getAccountsPayableItemQuantity();
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
                break;
            }
        }
        item.setFirstFincialObjectCode(firstFinancialObjectCode);
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#setPurchaseOrderInfo(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void setPurchaseOrderInfo(PurApLineForm purApLineForm) {
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(purApLineForm.getPurchaseOrderIdentifier());
        // Set contact email address.
        if (purchaseOrderDocument.getInstitutionContactEmailAddress() != null) {
            purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getInstitutionContactEmailAddress());
        }
        else if (purchaseOrderDocument.getRequestorPersonEmailAddress() != null) {
            purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getRequestorPersonEmailAddress());
        }

        // Set contact phone number.
        if (purchaseOrderDocument.getInstitutionContactPhoneNumber() != null) {
            purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getInstitutionContactPhoneNumber());
        }
        else if (purchaseOrderDocument.getRequestorPersonPhoneNumber() != null) {
            purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getRequestorPersonPhoneNumber());
        }

        // set reqs_id
        purApLineForm.setRequisitionIdentifier(purchaseOrderDocument.getRequisitionIdentifier());

    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#buildPurApItemAssetsList(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void buildPurApItemAssetsList(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // set fields from PurAp tables
                setCabItemFieldsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // if pre-asset tagging information exists for the line item, the description is populated from the pre-asset
                // tagging.
                populateDescriptionFromPreTag(item, purApLineForm);

                // set line item unit cost and total cost
                setLineItemCost(item);

                // set financial object code
                setFirstFinancialObjectCode(item);
            }

            Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
        }

        // set CAMS Transaction type from PurAp
        setCamsTransaction(purApLineForm);

        // set create asset/apply payment indicator
        setAssetIndicator(purApLineForm);

    }

    /**
     * Populate item description from pre-asset tagging table.
     * 
     * @param item
     */
    private void populateDescriptionFromPreTag(PurchasingAccountsPayableItemAsset item, PurApLineForm purApForm) {
        Pretag preTag = getPreTagLineItem(purApForm.getPurchaseOrderIdentifier(), item.getItemLineNumber());
        if (preTag != null && StringUtils.isNotBlank(preTag.getAssetTopsDescription())) {
            item.setAccountsPayableLineItemDescription(preTag.getAssetTopsDescription());
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getPreTagLineItem(java.lang.String, java.lang.Integer)
     */
    public Pretag getPreTagLineItem(Integer purchaseOrderIdentifier, Integer lineItemNumber) {

        if (purchaseOrderIdentifier == null || lineItemNumber == null) {
            return null;
        }

        Map<String, Object> pKeys = new HashMap<String, Object>();

        pKeys.put(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, purchaseOrderIdentifier);
        pKeys.put(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER, lineItemNumber);
        return (Pretag) businessObjectService.findByPrimaryKey(Pretag.class, pKeys);
    }

    /**
     * Set create asset and apply payment indicator.
     * 
     * @param purApLineForm
     */
    private void setAssetIndicator(PurApLineForm purApLineForm) {
        boolean existTradeInAllowance = isTradeInAllowanceExist(purApLineForm);
        boolean existTradeInIndicator = isTradeInIndicatorExist(purApLineForm);
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            boolean existAdditionalCharge = false;
            boolean existItemAsset = false;
            // check if within the same document, exist both additional charge lines and normal line items.
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                existAdditionalCharge |= item.isAdditionalChargeNonTradeInIndicator();
                existItemAsset |= !item.isAdditionalChargeNonTradeInIndicator() & !item.isTradeInAllowance();
            }

            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (!item.isCreateAssetIndicator() | !item.isApplyPaymentIndicator()) {
                    if ((item.isAdditionalChargeNonTradeInIndicator() & !existItemAsset) || (item.isTradeInAllowance() & !existTradeInIndicator)) {
                        // If the only line item on the purchase order is the additional charge, or trade-in allowance then we can
                        // add a payment, or create an asset.
                        item.setCreateAssetIndicator(true);
                        item.setApplyPaymentIndicator(true);
                    }
                    else if (!existAdditionalCharge & (!item.isAdditionalChargeNonTradeInIndicator() & !item.isTradeInAllowance() & !item.isItemAssignedToTradeInIndicator() | !existTradeInAllowance)) {
                        // for normal line item without trade-in indicator or with trade-in indicator but no trade-in allowance.
                        item.setCreateAssetIndicator(true);
                        item.setApplyPaymentIndicator(true);
                    }
                }
            }
        }

    }

    /**
     * Set CAMS transaction type code the user entered in PurAp
     * 
     * @param poId
     */
    private void setCamsTransaction(PurApLineForm purApLineForm) {
        Integer poId = purApLineForm.getPurchaseOrderIdentifier();
        PurchaseOrderDocument purApdocument = (PurchaseOrderDocument) purchaseOrderService.getCurrentPurchaseOrder(poId);
        String capitalAssetSystemTypeCode = purApdocument.getCapitalAssetSystemTypeCode();

        if (CabConstants.INDIVIDUAL_ASSETS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setIndividualAssetsFromPurAp(poId, purApLineForm.getPurApDocs());
        }
        else if (CabConstants.ONE_SYSTEM.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setOneSystemFromPurAp(poId, purApLineForm.getPurApDocs());

        }
        else if (CabConstants.MULTIPLE_SYSTEMS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setMultipleSystemFromPurAp(poId, purApLineForm.getPurApDocs());
        }
    }

    /**
     * Set Multiple system capital asset transaction type code and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setMultipleSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<CapitalAssetSystem> capitalAssetSystems = purchaseOrderService.retrieveCapitalAssetSystemsForMultipleSystem(poId);
        if (ObjectUtils.isNotNull(capitalAssetSystems)) {
            // TODO: currently PurAp multiple system in fact return one system.
            CapitalAssetSystem capitalAssetSystem = capitalAssetSystems.get(0);
            if (ObjectUtils.isNotNull(capitalAssetSystem)) {
                List<Long> capitalAssetNumbers = getAssetNumbersFromItemCapitalAssets(capitalAssetSystem.getItemCapitalAssets());
                String capitalAssetTransactionType = getCapitalAssetTransTypeForOneSystem(poId);

                if (StringUtils.isNotBlank(capitalAssetTransactionType) || (capitalAssetNumbers != null && !capitalAssetNumbers.isEmpty())) {
                    for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                            item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionType);
                            item.setCapitalAssetNumbers(capitalAssetNumbers);
                        }
                    }
                }
            }

        }
    }

    /**
     * Set One System capital asset transaction type code and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setOneSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs) {
        CapitalAssetSystem capitalAssetSystem = purchaseOrderService.retrieveCapitalAssetSystemForOneSystem(poId);
        List<Long> capitalAssetNumbers = getAssetNumbersFromItemCapitalAssets(capitalAssetSystem.getItemCapitalAssets());
        String capitalAssetTransactionType = getCapitalAssetTransTypeForOneSystem(poId);

        if (StringUtils.isNotBlank(capitalAssetTransactionType) || (capitalAssetNumbers != null && !capitalAssetNumbers.isEmpty())) {
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                    item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionType);
                    item.setCapitalAssetNumbers(capitalAssetNumbers);
                }
            }
        }
    }

    /**
     * Get capitalAssetTransactionTypeCode for one system from PurAp.
     * 
     * @param poId
     * @return
     */
    private String getCapitalAssetTransTypeForOneSystem(Integer poId) {
        PurchaseOrderDocument poDoc = purchaseOrderService.getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(poDoc)) {
            List<PurchasingCapitalAssetItem> capitalAssetItems = poDoc.getPurchasingCapitalAssetItems();

            if (ObjectUtils.isNotNull(capitalAssetItems) && capitalAssetItems.get(0) != null) {
                capitalAssetItems.get(0).getCapitalAssetTransactionTypeCode();
            }
        }
        return null;
    }

    /**
     * Set Individual system asset transaction type and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setIndividualAssetsFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<PurchasingCapitalAssetItem> capitalAssetItems = purchaseOrderService.retrieveCapitalAssetItemsForIndividual(poId);
        String capitalAssetTransactionTypeCode = null;
        List<Long> capitalAssetNumbers = null;

        for (PurchasingCapitalAssetItem purchasingCapitalAssetItem : capitalAssetItems) {
            // get matching purchasingAccountsPayableItemAssets
            List<PurchasingAccountsPayableItemAsset> matchingItems = getMatchingItems(purchasingCapitalAssetItem.getItemIdentifier(), purApDocs);
            capitalAssetNumbers = getAssetNumbersFromItemCapitalAssets(purchasingCapitalAssetItem.getPurchasingCapitalAssetSystem().getItemCapitalAssets());
            capitalAssetTransactionTypeCode = purchasingCapitalAssetItem.getCapitalAssetTransactionTypeCode();

            // set capitalAssetTransactionTypeCode and capitalAssetNumbers for each matching item
            for (PurchasingAccountsPayableItemAsset itemAsset : matchingItems) {
                itemAsset.setCapitalAssetTransactionTypeCode(capitalAssetTransactionTypeCode);
                itemAsset.setCapitalAssetNumbers(capitalAssetNumbers);
            }
        }
    }

    /**
     * Get asset number list from ItemCapitalAsset list.
     * 
     * @param itemCapitalAssets
     * @return
     */
    private List<Long> getAssetNumbersFromItemCapitalAssets(List<ItemCapitalAsset> itemCapitalAssets) {
        List<Long> assetNumbers = new ArrayList<Long>();

        for (ItemCapitalAsset asset : itemCapitalAssets) {
            if (asset.getCapitalAssetNumber() != null && !isAssetNumberDuplicate(asset.getCapitalAssetNumber(),assetNumbers)) {
                assetNumbers.add(asset.getCapitalAssetNumber());
            }
        }
        return assetNumbers;
    }

    /**
     * Check if given capitalAssetNumber is an duplicate number in assetNumbers list.
     * 
     * @param capitalAssetNumber
     * @param assetNumbers
     * @return
     */
    private boolean isAssetNumberDuplicate(Long candidateNumber, List<Long> assetNumbers) {
        for(Long existingNumber: assetNumbers) {
            if (existingNumber.equals(candidateNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finding out the matching PREQ/CM items originating from the same PurchaseOrderItem.
     * 
     * @param itemIdentifier
     * @param purApDocs
     * @return
     */
    private List getMatchingItems(Integer itemIdentifier, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<PurchasingAccountsPayableItemAsset> matchingItems = new ArrayList<PurchasingAccountsPayableItemAsset>();

        if (itemIdentifier != null) {
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                    if (itemIdentifier.equals(item.getPurchaseOrderItemIdentifier())) {
                        matchingItems.add(item);
                    }
                }
            }
        }
        return matchingItems;
    }

    /**
     * Set CAMS transaction type code the user entered in PurAp
     * 
     * @param purApLineForm
     */
    // private void setCamsTransaction(PurApLineForm purApLineForm) {
    // if (purApLineForm.getRequisitionIdentifier() == null) {
    // return;
    // }
    //
    // for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
    // for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
    // if (item.getItemLineNumber() != null) {
    // item.setCapitalAssetTransactionTypeCode(getCapitalAssetTransTypeCode(purApLineForm.getRequisitionIdentifier(),
    // item.getItemLineNumber()));
    // }
    // }
    // }
    // }
    // TODO: Diffrenciate between PREQ and CM?
    /**
     * Get transaction type code if defined in PurAp.
     * 
     * @param requisitionIdentifier
     * @param itemLineNumber
     * @return
     */
    // private String getCapitalAssetTransTypeCode(Integer requisitionIdentifier, Integer itemLineNumber) {
    // Map<String, Object> cols = new HashMap<String, Object>();
    // cols.put(PurapPropertyConstants.PURAP_DOC_ID, requisitionIdentifier);
    // cols.put(CabPropertyConstants.purApRequisitionItem.ITEM_LINE_NUMBER, itemLineNumber);
    // Collection<RequisitionItem> reqItems = businessObjectService.findMatching(RequisitionItem.class, cols);
    //
    // if (!reqItems.isEmpty()) {
    // for (RequisitionItem reqItem : reqItems) {
    // if (ObjectUtils.isNotNull(reqItem.getPurchasingCapitalAssetItem())) {
    // return reqItem.getPurchasingCapitalAssetItem().getCapitalAssetTransactionTypeCode();
    // }
    // }
    // }
    //
    // return null;
    // }
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
            // for PREQ document
            PaymentRequestItem item = (PaymentRequestItem) businessObjectService.findByPrimaryKey(PaymentRequestItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            PurchaseOrderItem poi = item.getPurchaseOrderItem();
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(item.getPurchaseOrderItem().getItemIdentifier());
            }
        }
        else {
            // for CM document
            CreditMemoItem item = (CreditMemoItem) businessObjectService.findByPrimaryKey(CreditMemoItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            PurchaseOrderItem poi = getPurchaseOrderItemfromCreditMemoItem(item);
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(poi.getItemIdentifier());
            }
        }
        pKeys.clear();
    }


    /**
     * Retreives a purchase order item for a given CreditMemoItem by inspecting the item type to see if its above the line or below
     * the line and returns the appropriate type.
     * 
     * @param item
     * @return
     */
    private PurchaseOrderItem getPurchaseOrderItemfromCreditMemoItem(CreditMemoItem item) {
        if (ObjectUtils.isNotNull(item.getPurapDocumentIdentifier())) {
            if (ObjectUtils.isNull(item.getPurapDocument())) {
                item.refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
            }
        }
        // ideally we should do this a different way - maybe move it all into the service or save this info somehow (make sure and
        // update though)
        if (item.getPurapDocument() != null) {
            PurchaseOrderDocument po = ((CreditMemoDocument)item.getPurapDocument()).getPurchaseOrderDocument();
            PurchaseOrderItem poi = null;
            if (item.getItemType().isLineItemIndicator()) {
                poi = (PurchaseOrderItem) po.getItem(item.getItemLineNumber().intValue() - 1);
            }
            else {
                poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(item.getPurapDocument(), item.getItemType());
            }
            if (poi != null) {
                return poi;
            }
            else {
                LOG.debug("getPurchaseOrderItemfromCreditMemoItem() Returning null because PurchaseOrderItem object for line number" + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
                return null;
            }
        }
        else {

            LOG.error("getPurchaseOrderItemfromCreditMemoItem() Returning null because paymentRequest object is null");
            throw new PurError("Credit Memo Object in Purchase Order item line number " + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
        }
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the purApLineDao attribute.
     * 
     * @return Returns the purApLineDao.
     */
    public PurApLineDao getPurApLineDao() {
        return purApLineDao;
    }


    /**
     * Sets the purApLineDao attribute value.
     * 
     * @param purApLineDao The purApLineDao to set.
     */
    public void setPurApLineDao(PurApLineDao purApLineDao) {
        this.purApLineDao = purApLineDao;
    }

    /**
     * Gets the purchaseOrderService attribute.
     * 
     * @return Returns the purchaseOrderService.
     */
    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    /**
     * Sets the purchaseOrderService attribute value.
     * 
     * @param purchaseOrderService The purchaseOrderService to set.
     */
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }


}
