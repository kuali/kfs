/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabConstants.ActivityStatusCode;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
@Transactional
public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PurApLineDao purApLineDao;
    private PurApInfoService purApInfoService;
    private CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#mergeLinesHasDifferentObjectSubTypes(java.util.List)
     */
    @Override
    public boolean mergeLinesHasDifferentObjectSubTypes(List<PurchasingAccountsPayableItemAsset> mergeLines) {
        boolean invalid = false;
        List<String> objectSubTypeList = new ArrayList<String>();

        // collect all objectSubTypes from item lines
        for (PurchasingAccountsPayableItemAsset itemAsset : mergeLines) {
            for (PurchasingAccountsPayableLineAssetAccount account : itemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
                account.getGeneralLedgerEntry().refreshReferenceObject(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT);
                ObjectCode objCode = account.getGeneralLedgerEntry().getFinancialObject();
                if (ObjectUtils.isNotNull(objCode) && StringUtils.isNotEmpty(objCode.getFinancialObjectSubTypeCode())) {
                    objectSubTypeList.add(objCode.getFinancialObjectSubTypeCode());
                }
            }
        }

        // check if different objectSubTypes exist
        if (!getAssetService().isObjectSubTypeCompatible(objectSubTypeList)) {
            invalid = true;
        }
        return invalid;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#allocateLinesHasDifferentObjectSubTypes(java.util.List,
     *      org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    @Override
    public boolean allocateLinesHasDifferentObjectSubTypes(List<PurchasingAccountsPayableItemAsset> targetLines, PurchasingAccountsPayableItemAsset sourceLine) {
        boolean invalid = false;
        List<String> objectSubTypeList = new ArrayList<String>();

        // collect objectSubTypes from target item lines
        for (PurchasingAccountsPayableItemAsset itemAsset : targetLines) {
            for (PurchasingAccountsPayableLineAssetAccount account : itemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
                account.getGeneralLedgerEntry().refreshReferenceObject(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT);
                ObjectCode objCode = account.getGeneralLedgerEntry().getFinancialObject();
                if (ObjectUtils.isNotNull(objCode) && StringUtils.isNotEmpty(objCode.getFinancialObjectSubTypeCode())) {
                    objectSubTypeList.add(objCode.getFinancialObjectSubTypeCode());
                }
            }
        }

        // collect objectSubTypes from source item line
        if (ObjectUtils.isNotNull(sourceLine)) {
            for (PurchasingAccountsPayableLineAssetAccount account : sourceLine.getPurchasingAccountsPayableLineAssetAccounts()) {
                account.getGeneralLedgerEntry().refreshReferenceObject(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT);
                ObjectCode objCode = account.getGeneralLedgerEntry().getFinancialObject();
                if (ObjectUtils.isNotNull(objCode) && StringUtils.isNotEmpty(objCode.getFinancialObjectSubTypeCode())) {
                    objectSubTypeList.add(objCode.getFinancialObjectSubTypeCode());
                }
            }
        }

        // check if different objectSubTypes exist
        if (!getAssetService().isObjectSubTypeCompatible(objectSubTypeList)) {
            invalid = true;
        }
        return invalid;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineDocumentService#inActivateDocument(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument)
     */
    @Override
    public void conditionallyUpdateDocumentStatusAsProcessed(PurchasingAccountsPayableDocument selectedDoc) {
        for (PurchasingAccountsPayableItemAsset item : selectedDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.isActive()) {
                return;
            }
        }
        // set as processed when allocate/merge and all its items are in CAMs
        selectedDoc.setActivityStatusCode(CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS);
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#resetSelectedValue(java.util.List)
     */
    @Override
    public void resetSelectedValue(List<PurchasingAccountsPayableDocument> purApDocs) {
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                item.setSelectedValue(false);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processAllocate(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      java.util.List, org.kuali.kfs.module.cab.document.web.PurApLineSession, java.util.List)
     */
    @Override
    public boolean processAllocate(PurchasingAccountsPayableItemAsset allocateSourceLine,
            List<PurchasingAccountsPayableItemAsset> allocateTargetLines,
            List<PurchasingAccountsPayableActionHistory> actionsTakeHistory,
            List<PurchasingAccountsPayableDocument> purApDocs,
            boolean initiateFromBatch) {

        boolean allocatedIndicator = true;
        // indicator of additional charge allocation
        boolean allocateAddlChrgIndicator = allocateSourceLine.isAdditionalChargeNonTradeInIndicator() | allocateSourceLine.isTradeInAllowance();
        // Maintain this account List for update. So accounts already allocated won't take effect for account not allocated yet.
        List<PurchasingAccountsPayableLineAssetAccount> newAccountList = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        // For each account in the source item, allocate it to the target items.
        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : allocateSourceLine.getPurchasingAccountsPayableLineAssetAccounts()) {
            sourceAccount.refresh();
            // Get allocate to target account list
            List<PurchasingAccountsPayableLineAssetAccount> targetAccounts = getAllocateTargetAccounts(sourceAccount, allocateTargetLines, allocateAddlChrgIndicator);
            if (!targetAccounts.isEmpty()) {
                // Percentage amount to each target account
                allocateByItemAccountAmount(sourceAccount, targetAccounts, newAccountList, actionsTakeHistory);
            }
            else {
                allocatedIndicator = false;
                break;
            }
        }

        if (allocatedIndicator) {
            postAllocateProcess(allocateSourceLine, allocateTargetLines, purApDocs, newAccountList, initiateFromBatch);
        }

        return allocatedIndicator;
    }


    /**
     * Process after allocate.
     *
     * @param selectedLineItem
     * @param allocateTargetLines
     * @param purApDocs
     * @param newAccountList
     */
    protected void postAllocateProcess(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, List<PurchasingAccountsPayableDocument> purApDocs, List<PurchasingAccountsPayableLineAssetAccount> newAccountList, boolean initiateFromBatch) {
        // add new account into each item list
        addNewAccountToItemList(newAccountList);

        // update total cost and unit cost.
        updateLineItemsCost(allocateTargetLines);

        if (ObjectUtils.isNotNull(selectedLineItem.getPurchasingAccountsPayableDocument())) {
            // remove allocate source line item.
            selectedLineItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(selectedLineItem);
            // when an line is removed from the document, we should check if it is the only active line in the document and
            // in-activate document if yes.
            conditionallyUpdateDocumentStatusAsProcessed(selectedLineItem.getPurchasingAccountsPayableDocument());
        }

        // Adjust create asset and apply payment indicator only when allocate additional charges.
        if (!initiateFromBatch && (selectedLineItem.isAdditionalChargeNonTradeInIndicator() || selectedLineItem.isTradeInAllowance())) {
            setAssetIndicator(purApDocs);
        }

        // update status code as user modified for allocate target lines
        if (!initiateFromBatch) {
            for (PurchasingAccountsPayableItemAsset allocateTargetItem : allocateTargetLines) {
                // KFSCNTRB-1676 / FSKD-5487
                updateItemStatusAsUserModified(allocateTargetItem, true);
            }
        }
    }

    /**
     * Build removable asset lock map from the processedItems list. We need to remove all asset locks hold be items which has been
     * merged or allocated to other lines.
     *
     * @param processedItems
     * @return
     */
    protected Map<String, Set> getRemovableAssetLocks(List<PurchasingAccountsPayableItemAsset> processedItems) {
        Map<String, Set> removableAssetLocks = new HashMap<String, Set>();

        for (PurchasingAccountsPayableItemAsset processedItem : processedItems) {
            // For the time being, only for individual system, each item has its own asset numbers.
            if (processedItem.getLockingInformation() != null && !CamsConstants.defaultLockingInformation.equals(processedItem.getLockingInformation())) {
                addAssetLock(removableAssetLocks, processedItem);
            }
            else if (ObjectUtils.isNotNull(processedItem.getPurchasingAccountsPayableDocument())) {
                // check other items if they are fully processed and can release the lock
                List<PurchasingAccountsPayableItemAsset> remainingItems = processedItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets();
                boolean fullyProcessed = true;
                for (PurchasingAccountsPayableItemAsset itemAsset : remainingItems) {
                    if (!CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equalsIgnoreCase(itemAsset.getActivityStatusCode())) {
                        fullyProcessed = false;
                        break;
                    }
                }
                if (fullyProcessed) {
                    // All the items are either merged or allocated to other document item. We should remove the asset lock
                    // retained by this document.
                    addAssetLock(removableAssetLocks, processedItem);
                }
            }
        }

        processedItems.clear();
        return removableAssetLocks;
    }

    protected void addAssetLock(Map<String, Set> removableAssetLocks, PurchasingAccountsPayableItemAsset processedItem) {
        if (processedItem.getLockingInformation() == null) {
            processedItem.setLockingInformation(CamsConstants.defaultLockingInformation);
        }
        if (removableAssetLocks.containsKey(processedItem.getDocumentNumber())) {
            Set lockingInfoList = removableAssetLocks.get(processedItem.getDocumentNumber());
            lockingInfoList.add(processedItem.getLockingInformation());
        }
        else {
            Set lockingInfoList = new HashSet<String>();
            lockingInfoList.add(processedItem.getLockingInformation());
            removableAssetLocks.put(processedItem.getDocumentNumber(), lockingInfoList);
        }
    }

    /**
     * Reset item total cost and unit cost for each item.
     *
     * @param allocateTargetLines
     */
    protected void updateLineItemsCost(List<PurchasingAccountsPayableItemAsset> lineItems) {
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
    protected void addNewAccountToItemList(List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
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
    protected void setLineItemCost(PurchasingAccountsPayableItemAsset item) {
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
    protected void allocateByItemAccountAmount(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, List<PurchasingAccountsPayableLineAssetAccount> newAccountList, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory) {
        KualiDecimal targetAccountsTotalAmount = KualiDecimal.ZERO;
        KualiDecimal sourceAccountTotalAmount = sourceAccount.getItemAccountTotalAmount();
        KualiDecimal amountAllocated = KualiDecimal.ZERO;
        KualiDecimal additionalAmount = null;

        // Calculate the targetAccountTotalAmount. Ignore the sign of the account amount when proportionally allocate based on
        // account amount
        for (PurchasingAccountsPayableLineAssetAccount targetAccount : targetAccounts) {
            targetAccountsTotalAmount = targetAccountsTotalAmount.add(targetAccount.getItemAccountTotalAmount().abs());
        }

        for (Iterator<PurchasingAccountsPayableLineAssetAccount> iterator = targetAccounts.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableLineAssetAccount targetAccount = iterator.next();
            if (iterator.hasNext()) {
                // Not working on the last node. Calculate additional charge amount by percentage. Ignore the sign of the account
                // amount when proportionally allocate based on account amount
                additionalAmount = targetAccount.getItemAccountTotalAmount().abs().multiply(sourceAccountTotalAmount).divide(targetAccountsTotalAmount);
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
                    newAccount.setGeneralLedgerEntry(sourceAccount.getGeneralLedgerEntry());
                    newAccountList.add(newAccount);
                }
            }

            // add to action history
            addAllocateHistory(sourceAccount, actionsTakeHistory, additionalAmount, newAccount);
        }
    }

    /**
     * Save allocate action into session object.
     *
     * @param sourceAccount
     * @param actionsTakeHistory
     * @param additionalAmount
     * @param newAccount
     */
    protected void addAllocateHistory(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount newAccount) {
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
    protected PurchasingAccountsPayableLineAssetAccount getFromTargetAccountList(List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, Long glIdentifier) {
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
    protected void updateAccountAmount(KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount targetAccount) {
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
    protected PurchasingAccountsPayableLineAssetAccount getMatchingFromAccountList(List<PurchasingAccountsPayableLineAssetAccount> accountList, Long glIdentifier, PurchasingAccountsPayableLineAssetAccount targetAccount) {
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
    protected List<PurchasingAccountsPayableLineAssetAccount> getAllocateTargetAccounts(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, boolean addtionalCharge) {
        GeneralLedgerEntry candidateEntry = null;
        GeneralLedgerEntry sourceEntry = sourceAccount.getGeneralLedgerEntry();
        List<PurchasingAccountsPayableLineAssetAccount> matchingAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableLineAssetAccount> allAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        // For additional charge allocation, target account selection is based on account lines with the same account number and
        // object code. If no matching, select all account lines. For line item to line items, select all account lines as target.
        for (PurchasingAccountsPayableItemAsset item : allocateTargetLines) {
            for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                //KFSMI-5122: We need to refresh account general ledger entry so that the gl entries become visible as candidateEntry
                account.refreshReferenceObject("generalLedgerEntry");
                candidateEntry = account.getGeneralLedgerEntry();

                if (ObjectUtils.isNotNull(candidateEntry)) {
                    // For additional charge, select matching account when account number and object code both match.
                    if (addtionalCharge && StringUtils.equalsIgnoreCase(sourceEntry.getChartOfAccountsCode(), candidateEntry.getChartOfAccountsCode()) && StringUtils.equalsIgnoreCase(sourceEntry.getAccountNumber(), candidateEntry.getAccountNumber()) && StringUtils.equalsIgnoreCase(sourceEntry.getFinancialObjectCode(), candidateEntry.getFinancialObjectCode())) {
                        matchingAccounts.add(account);
                    }
                }

                allAccounts.add(account);
            }
        }

        return matchingAccounts.isEmpty() ? allAccounts : matchingAccounts;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getAllocateTargetLines(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      java.util.List)
     */
    @Override
    public List<PurchasingAccountsPayableItemAsset> getAllocateTargetLines(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<PurchasingAccountsPayableItemAsset> targetLineItems = new ArrayList<PurchasingAccountsPayableItemAsset>();

        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // If selected Line is additional charge line, get target lines from the same document.
                // Else if selected Line is trade in allowance, target lines are item lines with TI indicator set.
                // Otherwise, select items with check box set.
                if (item.isActive() && item != selectedLineItem && ((selectedLineItem.isAdditionalChargeNonTradeInIndicator() && !item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && StringUtils.equalsIgnoreCase(selectedLineItem.getDocumentNumber(), item.getDocumentNumber())) || (selectedLineItem.isTradeInAllowance() && item.isItemAssignedToTradeInIndicator()) || (item.isSelectedValue()))) {
                    targetLineItems.add(item);
                }
            }
        }
        return targetLineItems;
    }

    /**
     * Get selected merge lines. If this is merge all action, we need to manually append all additional charge lines since no select
     * box associated with them.
     *
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getSelectedMergeLines(boolean, java.util.List)
     */
    @Override
    public List<PurchasingAccountsPayableItemAsset> getSelectedMergeLines(boolean isMergeAll, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<PurchasingAccountsPayableItemAsset> mergeLines = new ArrayList<PurchasingAccountsPayableItemAsset>();
        boolean excludeTradeInAllowance = false;

        // Handle one exception for merge all: when we have TI allowance but no TI indicator line, we should exclude
        if (isMergeAll && !isTradeInIndicatorExistInAllLines(purApDocs) && isTradeInAllowanceExist(purApDocs)) {
            excludeTradeInAllowance = true;
        }

        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // If not merge all action, select items are the merge lines. If it is merge all action, all lines should be
                // candidate merge lines except trade-in allowance line when there is no trade-in indicator line.
                if ((!isMergeAll && item.isSelectedValue()) || (isMergeAll && (!excludeTradeInAllowance || !item.isTradeInAllowance()))) {
                    mergeLines.add(item);
                    // setup non-persistent relationship from item to document.
                    item.setPurchasingAccountsPayableDocument(purApDoc);
                }
            }
        }
        return mergeLines;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isTradeInAllowanceExist(java.util.List)
     */
    @Override
    public boolean isTradeInAllowanceExist(List<PurchasingAccountsPayableDocument> purApDocs) {
        boolean tradeInAllowance = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // KFSCNTRB-1676/FSKD-5487
                if (item.isTradeInAllowance() && item.isActive()) {
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
     * @param purApDocs
     * @return
     */
    protected boolean isTradeInIndicatorExistInAllLines(List<PurchasingAccountsPayableDocument> purApDocs) {
        boolean tradeInIndicator = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // KFSCNTRB-1676/FSKD-5487
                if (item.isItemAssignedToTradeInIndicator() && item.isActive()) {
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
    @Override
    public boolean isTradeInIndExistInSelectedLines(List<PurchasingAccountsPayableItemAsset> itemAssets) {
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
     * If item assets are from the same document, we can ignore additional charges pending.
     *
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargePending(java.util.List)
     */
    @Override
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

        // check if item assets from different document have additional charges not allocated yet. Bring all lines in
        // the same document as checking candidate.
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
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isMergeAllAction(java.util.List)
     */
    @Override
    public boolean isMergeAllAction(List<PurchasingAccountsPayableDocument> purApDocs) {
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // When there is one item line not selected, mergeAll is false
                if (!item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && !item.isSelectedValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargeExistInAllLines(java.util.List)
     */
    @Override
    public boolean isAdditionalChargeExistInAllLines(List<PurchasingAccountsPayableDocument> purApDocs) {
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // KFSCNTRB-1676/FSKD-5487
                if (item.isAdditionalChargeNonTradeInIndicator() && item.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processMerge(java.util.List)
     */
    @Override
    public void processMerge(List<PurchasingAccountsPayableItemAsset> mergeLines, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, boolean isMergeAll) {
        PurchasingAccountsPayableItemAsset sourceItem = null;
        PurchasingAccountsPayableLineAssetAccount targetAccount = null;
        // use the first item and its accounts as the target
        PurchasingAccountsPayableItemAsset firstItem = mergeLines.get(0);
        List<PurchasingAccountsPayableLineAssetAccount> firstAccountList = firstItem.getPurchasingAccountsPayableLineAssetAccounts();

        // Merge accounts starting from the second item to the last one.
        for (int i = 1; i < mergeLines.size(); i++) {
            sourceItem = mergeLines.get(i);
            for (PurchasingAccountsPayableLineAssetAccount account : sourceItem.getPurchasingAccountsPayableLineAssetAccounts()) {
                // Check if we can grouping the accounts. If yes, update the account amount without moving account line.
                targetAccount = getFromTargetAccountList(firstAccountList, account.getGeneralLedgerAccountIdentifier());
                if (targetAccount != null) {
                    updateAccountAmount(account.getItemAccountTotalAmount(), targetAccount);
                }
                else {
                    // move account from source to target
                    account.setDocumentNumber(firstItem.getDocumentNumber());
                    account.setAccountsPayableLineItemIdentifier(firstItem.getAccountsPayableLineItemIdentifier());
                    account.setCapitalAssetBuilderLineNumber(firstItem.getCapitalAssetBuilderLineNumber());
                    account.setPurchasingAccountsPayableItemAsset(firstItem);
                    firstAccountList.add(account);
                }
            }
        }

        // update action history, remove lines before merge and clean up the user input
        postMergeProcess(mergeLines, actionsTakeHistory, isMergeAll);
    }

    /**
     * Process after merge.
     *
     * @param mergeLines
     * @param purApLineSession
     * @param isMergeAll
     */
    protected void postMergeProcess(List<PurchasingAccountsPayableItemAsset> mergeLines, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, boolean isMergeAll) {
        String actionTypeCode = isMergeAll ? CabConstants.Actions.MERGE_ALL : CabConstants.Actions.MERGE;
        PurchasingAccountsPayableItemAsset targetItem = mergeLines.get(0);

        // set unit cost and total cost
        setLineItemCost(targetItem);
        targetItem.setItemAssignedToTradeInIndicator(false);

        // For all merge source lines(the first line is considered as target technically), remove it from the document and update in
        // the action history.
        for (int i = 1; i < mergeLines.size(); i++) {
            PurchasingAccountsPayableItemAsset sourceItem = mergeLines.get(i);

            // Update the action history.
            addMergeHistory(actionsTakeHistory, actionTypeCode, sourceItem, targetItem);

            if (ObjectUtils.isNotNull(sourceItem.getPurchasingAccountsPayableDocument())) {
                // remove mergeLines from the document
                sourceItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(sourceItem);
                // if all active lines are merged to other line, we need to in-activate the current document
                conditionallyUpdateDocumentStatusAsProcessed(sourceItem.getPurchasingAccountsPayableDocument());
            }
        }
        // set the target item itemLineNumber for pre-tagging
        Integer poId = targetItem.getPurchasingAccountsPayableDocument().getPurchaseOrderIdentifier();
        if (poId != null) {
            Pretag targetPretag = getTargetPretag(mergeLines, poId);
            if (targetPretag != null) {
                targetItem.setItemLineNumber(targetPretag.getItemLineNumber());
            }
        }

        // update create asset/ apply payment indicator if any of the merged lines has the indicator set.
        updateAssetIndicatorAfterMerge(mergeLines);

        // update activity status code as modified
        // KFSCNTRB-1676 / FSKD-5487
        updateItemStatusAsUserModified(targetItem, false);
    }

    /**
     * Update create asset and apply payment indicators after merge.
     *
     * @param mergeLines
     */
    protected void updateAssetIndicatorAfterMerge(List<PurchasingAccountsPayableItemAsset> mergeLines) {
        boolean existCreateAsset = false;
        boolean existApplyPayment = false;
        PurchasingAccountsPayableItemAsset targetItem = mergeLines.get(0);
        // set indicator if any of the source item set it or target item set it.
        for (int i = 1; i < mergeLines.size(); i++) {
            PurchasingAccountsPayableItemAsset sourceItem = mergeLines.get(i);
            existCreateAsset |= sourceItem.isCreateAssetIndicator();
            existApplyPayment |= sourceItem.isApplyPaymentIndicator();
        }
        targetItem.setCreateAssetIndicator(targetItem.isCreateAssetIndicator() | existCreateAsset);
        targetItem.setApplyPaymentIndicator(targetItem.isApplyPaymentIndicator() | existApplyPayment);
    }

    /**
     * Get the first pre-tag for given itemLines
     *
     * @param itemLines
     * @param purchaseOrderIdentifier
     * @return
     */
    protected Pretag getTargetPretag(List<PurchasingAccountsPayableItemAsset> itemLines, Integer purchaseOrderIdentifier) {
        for (PurchasingAccountsPayableItemAsset item : itemLines) {
            Pretag newTag = getPreTagLineItem(purchaseOrderIdentifier, item.getItemLineNumber());

            if (isPretaggingExisting(newTag)) {
                return newTag;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isPretaggingExisting(org.kuali.kfs.module.cab.businessobject.Pretag)
     */
    @Override
    public boolean isPretaggingExisting(Pretag newTag) {
        return ObjectUtils.isNotNull(newTag) && newTag.getPretagDetails() != null && !newTag.getPretagDetails().isEmpty();
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isMultipleTagExisting(java.lang.Integer, java.util.Set)
     */
    @Override
    public boolean isMultipleTagExisting(Integer purchaseOrderIdentifier, Set<Integer> itemLineNumbers) {
        Pretag firstTag = null;
        for (Iterator iterator = itemLineNumbers.iterator(); iterator.hasNext();) {
            Integer itemLineNumber = (Integer) iterator.next();
            Pretag newTag = getPreTagLineItem(purchaseOrderIdentifier, itemLineNumber);

            if (isPretaggingExisting(newTag)) {
                if (firstTag != null) {
                    // find the second preTagging item
                    return true;
                }
                else {
                    firstTag = newTag;
                }
            }
        }

        return false;
    }

    /**
     * Add merge action to the action history.
     *
     * @param purApLineSession
     * @param isMergeAllAction
     * @param firstItem
     * @param item
     */
    protected void addMergeHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory, String actionTypeCode, PurchasingAccountsPayableItemAsset sourceItem, PurchasingAccountsPayableItemAsset targetItem) {
        // create action history records for each account from the source lines.
        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : sourceItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(sourceItem, targetItem, actionTypeCode);
            newAction.setAccountsPayableItemQuantity(sourceItem.getAccountsPayableItemQuantity());
            newAction.setItemAccountTotalAmount(sourceAccount.getItemAccountTotalAmount());
            newAction.setGeneralLedgerAccountIdentifier(sourceAccount.getGeneralLedgerAccountIdentifier());
            actionsTakenHistory.add(newAction);
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processPercentPayment(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    @Override
    public void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        KualiDecimal newQty = new KualiDecimal(1);
        // update quantity, total cost and unit cost.
        if (oldQty.isLessThan(newQty)) {
            itemAsset.setAccountsPayableItemQuantity(newQty);
            setLineItemCost(itemAsset);
            // add to action history
            addPercentPaymentHistory(actionsTakenHistory, itemAsset, oldQty);
            // update status code
            // KFSCNTRB-1676 / FSKD-5487
            updateItemStatusAsUserModified(itemAsset, false);
        }
    }

    /**
     * KFSCNTRB-1676 / FSKD-5487
     * Updates activity status code when percent payment/split/allocate/merge action taken,
     * based on whether or not the current action is an allocation.
     * @param itemAsset itemAsset for which action status is to be modified
     * @param performingAllocate indicates whether the current action is an allocation.
     */
    protected void updateItemStatusAsUserModified(PurchasingAccountsPayableItemAsset itemAsset, boolean performingAllocate) {
        // if current action is an allocation, always set to MODIFIED (allocated)
        if (performingAllocate) {
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
        }
        // otherwise if the previous status is NEW, set it to MODIFIED_NOT_ALLOCATED
        else if (StringUtils.endsWithIgnoreCase(itemAsset.getActivityStatusCode(), CabConstants.ActivityStatusCode.NEW)) {
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED_NOT_ALLOCATED);
        }
        // otherwise the previous status could be MODIFIED_NOT_ALLOCATED or MODIFIED, and we just leave it as is

        for (PurchasingAccountsPayableLineAssetAccount account : itemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);
        }

        itemAsset.getPurchasingAccountsPayableDocument().setActivityStatusCode(CabConstants.ActivityStatusCode.MODIFIED);

    }

    /**
     * Update action history for the percent payment action.
     *
     * @param actionsTaken
     * @param item
     * @param oldQty
     */
    protected void addPercentPaymentHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory, PurchasingAccountsPayableItemAsset item, KualiDecimal oldQty) {
        // create and set up one action history record for this action
        PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(item, item, CabConstants.Actions.PERCENT_PAYMENT);
        // record quantity before percent payment into action history
        newAction.setAccountsPayableItemQuantity(oldQty);
        actionsTakenHistory.add(newAction);
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSplit(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    @Override
    public void processSplit(PurchasingAccountsPayableItemAsset splitItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory) {
        PurchasingAccountsPayableDocument purApDoc = splitItemAsset.getPurchasingAccountsPayableDocument();
        // update activity status code for split item. it will be propogated to new created item and its accounts.
        // KFSCNTRB-1676 / FSKD-5487
        updateItemStatusAsUserModified(splitItemAsset, false);

        // create a new item asset from the current item asset.
        PurchasingAccountsPayableItemAsset newItemAsset = new PurchasingAccountsPayableItemAsset(splitItemAsset);

        // set cab line number
        newItemAsset.setCapitalAssetBuilderLineNumber(getMaxCabLineNumber(splitItemAsset, purApDoc) + 1);

        newItemAsset.setAccountsPayableItemQuantity(splitItemAsset.getSplitQty());

        // Set account list for new item asset and update current account amount value.
        createAccountsForNewItemAsset(splitItemAsset, newItemAsset);
        // set unit cost and total cost in new item
        setLineItemCost(newItemAsset);

        // Adjust current item asset quantity, total cost and unit cost
        splitItemAsset.setAccountsPayableItemQuantity(splitItemAsset.getAccountsPayableItemQuantity().subtract(splitItemAsset.getSplitQty()));
        setLineItemCost(splitItemAsset);

        // add the new item into document and sort.
        purApDoc.getPurchasingAccountsPayableItemAssets().add(newItemAsset);
        Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());

        // Add to action history
        addSplitHistory(splitItemAsset, newItemAsset, actionsTakeHistory);

        // clear up user input
        splitItemAsset.setSplitQty(null);
    }


    /**
     * Get the max cab line #. As part of the primary key, it should be the max value among the form item list and DB.
     *
     * @param splitItemAsset
     * @param purApDoc
     * @return
     */
    protected int getMaxCabLineNumber(PurchasingAccountsPayableItemAsset splitItemAsset, PurchasingAccountsPayableDocument purApDoc) {
        // get the max CAB line number in DB.
        Integer maxDBCabLineNbr = purApLineDao.getMaxCabLineNumber(splitItemAsset.getDocumentNumber(), splitItemAsset.getAccountsPayableLineItemIdentifier());
        // get the max CAB line number in form.
        int availableCabLineNbr = getMaxCabLineNbrForItemInForm(purApDoc, splitItemAsset);

        if (maxDBCabLineNbr.intValue() > availableCabLineNbr) {
            availableCabLineNbr = maxDBCabLineNbr.intValue();
        }
        return availableCabLineNbr;
    }

    /**
     * Search the current active items and return the max CAB line # for split new item .
     *
     * @param purApDoc
     * @param currentItemAsset
     * @return
     */
    protected int getMaxCabLineNbrForItemInForm(PurchasingAccountsPayableDocument purApDoc, PurchasingAccountsPayableItemAsset currentItemAsset) {
        int maxCabLineNbr = 0;
        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.getDocumentNumber().equalsIgnoreCase(currentItemAsset.getDocumentNumber()) && item.getAccountsPayableLineItemIdentifier().equals(currentItemAsset.getAccountsPayableLineItemIdentifier()) && item.getCapitalAssetBuilderLineNumber().intValue() > maxCabLineNbr) {
                maxCabLineNbr = item.getCapitalAssetBuilderLineNumber().intValue();
            }

        }
        return maxCabLineNbr;
    }

    /**
     * Update action history for a split action.
     *
     * @param currentItemAsset
     * @param newItemAsset
     * @param actionsTaken
     */
    protected void addSplitHistory(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        // for each account moved from original item to new item, create one action history record
        for (PurchasingAccountsPayableLineAssetAccount account : newItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(currentItemAsset, newItemAsset, CabConstants.Actions.SPLIT);
            newAction.setGeneralLedgerAccountIdentifier(account.getGeneralLedgerAccountIdentifier());
            // quantity moved from original item to new item
            newAction.setAccountsPayableItemQuantity(newItemAsset.getAccountsPayableItemQuantity());
            // account amount moved to new item
            newAction.setItemAccountTotalAmount(account.getItemAccountTotalAmount());
            actionsTakenHistory.add(newAction);
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSaveBusinessObjects(java.util.List,
     *      org.kuali.kfs.module.cab.document.web.PurApLineSession)
     */
    @Override
    public void processSaveBusinessObjects(List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession) {
        // Get removable asset locks which could be generated by allocate or merge when items removed and the lock should be
        // released.
        Map<String, Set> removableAssetLocks = getRemovableAssetLocks(purApLineSession.getProcessedItems());
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            // auto save items(including deleted items) and accounts due to auto-update setting in OJB.
            businessObjectService.save(purApDoc);
        }
        // remove asset locks
        for (String lockingDocumentNbr : removableAssetLocks.keySet()) {
            Set<String> lockingInfoList = removableAssetLocks.get(lockingDocumentNbr);
            for (String lockingInfo : lockingInfoList) {
                if (this.getCapitalAssetManagementModuleService().isAssetLockedByCurrentDocument(lockingDocumentNbr, lockingInfo)) {
                    this.getCapitalAssetManagementModuleService().deleteAssetLocks(lockingDocumentNbr, lockingInfo);
                }
            }
        }

        if (purApLineSession != null) {
            // save to action history table
            List<PurchasingAccountsPayableActionHistory> historyList = purApLineSession.getActionsTakenHistory();
            if (historyList != null && !historyList.isEmpty()) {
                businessObjectService.save(historyList);
                historyList.clear();
            }

            // save to generalLedgerEntry table
            List<GeneralLedgerEntry> glUpdateList = purApLineSession.getGlEntryUpdateList();
            if (glUpdateList != null && !glUpdateList.isEmpty()) {
                businessObjectService.save(glUpdateList);
                glUpdateList.clear();
            }

        }
    }


    /**
     * Create asset account list for new item asset and update the current account amount.
     *
     * @param oldItemAsset old line item.
     * @param newItemAsset new line item.
     */
    protected void createAccountsForNewItemAsset(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset) {
        KualiDecimal currentQty = currentItemAsset.getAccountsPayableItemQuantity();
        KualiDecimal splitQty = currentItemAsset.getSplitQty();
        List<PurchasingAccountsPayableLineAssetAccount> newAccountsList = newItemAsset.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableLineAssetAccount newAccount;
        for (PurchasingAccountsPayableLineAssetAccount currentAccount : currentItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            // create accounts for new item asset.
            newAccount = new PurchasingAccountsPayableLineAssetAccount(newItemAsset, currentAccount.getGeneralLedgerAccountIdentifier());
            newAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().multiply(splitQty).divide(currentQty));
            newAccount.setGeneralLedgerEntry(currentAccount.getGeneralLedgerEntry());
            newAccountsList.add(newAccount);

            // Adjust current account amount for split item( subtract new account amount for original amount)
            currentAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().subtract(newAccount.getItemAccountTotalAmount()));
        }
    }


    /**
     * Set object code by the first one from the accounting lines.
     *
     * @param item Selected line item.
     */
    protected void setFirstFinancialObjectCode(PurchasingAccountsPayableItemAsset item) {
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
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#buildPurApItemAssetList(java.util.List)
     */
    @Override
    public void buildPurApItemAssetList(List<PurchasingAccountsPayableDocument> purApDocs) {
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {

                // set item non-persistent fields from PurAp PREQ/CM item tables
                purApInfoService.setAccountsPayableItemsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // set line item unit cost and total cost
                setLineItemCost(item);

                // set financial object code
                setFirstFinancialObjectCode(item);

                // KFSMI-5337
                // Adding the following code to populate item description from PreAsset tagging
                updateAssetDescriptionFromPreTag(item, purApDoc);
            }
            // For display purpose, move additional charges including trade-in below item lines.
            Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
        }

        // set CAMS Transaction type from PurAp
        purApInfoService.setCamsTransactionFromPurAp(purApDocs);

        // set create asset/apply payment indicator which are used to control display two buttons.
        setAssetIndicator(purApDocs);
    }

    private void updateAssetDescriptionFromPreTag(PurchasingAccountsPayableItemAsset item, PurchasingAccountsPayableDocument purApDoc){
        Pretag preTag = null;
        if (item.isActive()) {
            preTag = getPreTagLineItem(purApDoc.getPurchaseOrderIdentifier(), item.getItemLineNumber());
            if (ObjectUtils.isNotNull(preTag) && StringUtils.isNotBlank(preTag.getAssetTopsDescription())) {
                item.setAccountsPayableLineItemDescription(preTag.getAssetTopsDescription());
            }
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getPreTagLineItem(java.lang.String, java.lang.Integer)
     */
    @Override
    public Pretag getPreTagLineItem(Integer purchaseOrderIdentifier, Integer lineItemNumber) {

        if (purchaseOrderIdentifier == null || lineItemNumber == null) {
            return null;
        }

        Map<String, Object> pKeys = new HashMap<String, Object>();

        pKeys.put(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, purchaseOrderIdentifier);
        pKeys.put(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER, lineItemNumber);
        return businessObjectService.findByPrimaryKey(Pretag.class, pKeys);
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Sets create asset and apply payment indicators. These two indicators are referenced by jsp to control display of these two
     * action links. How to set these two indicators is based on the business rules. We need to put the following situations into
     * consideration. Since we move allocate additional charge allocation to CAB batch, we bring over additional charge lines only
     * when they are the only items in the document, or they are trade-in allowances, or they are from cancelled PREQ or FO changes.
     * To accommodate this, we relax the rules and defined as:
     *
1. If the PREQ has only ITEM type lines, then display the 2 actions (create/pay) for these lines.
2. If the PREQ has only ADDITIONAL type lines, then display the 2 actions (create/pay) for these lines.
3. If the PREQ has both ITEM and ADDITIONAL item lines:
3.1 An ADDITIONAL line will be allocated during the cab extract batch job, if both of the following 2 conditions are met:
a) The ADDITIONAL line is NOT TRDI, and
b) The PREQ has no FO changes and is not cancelled.
    If all ADDITIONAL lines in the PREQ fall into above category, then there will be no pending allocation on any ITEM line, so
all ITEM lines and ADDITIONAL lines shall have the 2 actions showing.
3.2 If there's any ADDITIONAL line in the PREQ that's not allocated during the batch, i.e. it falls into one of the 2 cases below:
a) The ADDITIONAL line is TRDI, or
b) The PREQ has FO changes or it's cancelled.
3.2.1 In this case, these un-allocated ADDITIONAL lines will need to be allocated manually first, before the create & pay actions show up for them.
3.2.2 Meanwhile, for all ITEM lines, the create & pay actions will not show up either until after the above ADDITIONAL lines are allocated.
     *
     * @param purApDocs
     */
    protected void setAssetIndicator(List<PurchasingAccountsPayableDocument> purApDocs) {
        // we should check pending allocation per document, instead of all documents under same PO; since the rule applies
        // to each document independently; pending allocations in other documents should not block current document.
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            // Based on the business rules above, we can simplify the checking to the following:
            // The only case in which we shouldn't display create asset or apply payment action for any line is when there're un-allocated
            // additional lines, as well as ITEM type lines in the documents; otherwise, display both for all active lines.
            boolean existUnAllocatedAdditionalLines = existUnAllocatedAdditionalLines(purApDoc);
            boolean existActiveItemLines = existActiveItemLines(purApDoc);
            if (!existUnAllocatedAdditionalLines || !existActiveItemLines) {
                for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                    // set the indicators to true for all active lines
                    if (item.isActive()) {
                        item.setCreateAssetIndicator(true);
                        item.setApplyPaymentIndicator(true);
                    }
                }
            }
        }
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Checks whether there exists any un-allocated additional charge asset line in the specified documents.
     */
    protected boolean existUnAllocatedAdditionalLines(PurchasingAccountsPayableDocument purApDoc) {
        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
            // We use activityStatusCode being NEW or MODIFIED_NOT_ALLOCATED to indicate the line hasn't been allocated yet
            if ((item.isAdditionalChargeNonTradeInIndicator() || item.isTradeInAllowance()) &&
                    (ActivityStatusCode.NEW.equalsIgnoreCase(item.getActivityStatusCode()) ||
                            ActivityStatusCode.MODIFIED_NOT_ALLOCATED.equalsIgnoreCase(item.getActivityStatusCode()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Checks whether there exists any active ITEM type asset line in the specified documents.
     */
    protected boolean existActiveItemLines(PurchasingAccountsPayableDocument purApDoc) {
        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.isActive() && !item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Set item asset unit cost.
     *
     * @param item line item
     * @param totalCost total cost for this line item.
     */
    protected void setItemAssetUnitCost(PurchasingAccountsPayableItemAsset item, KualiDecimal totalCost) {
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
            if (account.getItemAccountTotalAmount() != null) {
                totalCost = totalCost.add(account.getItemAccountTotalAmount());
            }
        }
        return totalCost;
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
     * Gets the purApInfoService attribute.
     *
     * @return Returns the purApInfoService.
     */
    public PurApInfoService getPurApInfoService() {
        return purApInfoService;
    }

    /**
     * Sets the purApInfoService attribute value.
     *
     * @param purApInfoService The purApInfoService to set.
     */
    public void setPurApInfoService(PurApInfoService purApInfoService) {
        this.purApInfoService = purApInfoService;
    }

    /**
     * get CAMS AssetService.
     *
     * @return
     */
    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    /**
     * Gets the capitalAssetManagementModuleService attribute.
     *
     * @return Returns the capitalAssetManagementModuleService.
     */
    public CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return capitalAssetManagementModuleService;
    }

    /**
     * Sets the capitalAssetManagementModuleService attribute value.
     *
     * @param capitalAssetManagementModuleService The capitalAssetManagementModuleService to set.
     */
    public void setCapitalAssetManagementModuleService(CapitalAssetManagementModuleService capitalAssetManagementModuleService) {
        this.capitalAssetManagementModuleService = capitalAssetManagementModuleService;
    }


}
