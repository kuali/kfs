/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.fixture.PurchasingAccountsPayableDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;


public class PurApLineServiceTest extends KualiTestBase {

    private PurApLineService purApLineService;
    private List<PurchasingAccountsPayableDocument> purApDocuments;
    private PurchasingAccountsPayableItemAsset percentItemAsset;
    private PurchasingAccountsPayableDocument preqDocumentWithSingleItemSingleAccount;
    private PurchasingAccountsPayableDocument preqDocumentWithTwoItemsSingleAccount;
    private PurchasingAccountsPayableDocument cmDocumentWithSingleItemTwoAccounts;

    private String ERROR_MERGE_OBJECT_SUB_TYPES_DIFFERENT = "objectSubTypes are different for Merge";
    private String ERROR_MERGE_OBJECT_SUB_TYPES_SAME = "objectSubTypes are the same for Merge";
    private String ERROR_ALLOCATE_OBJECT_SUB_TYPES_DIFFERENT = "objectSubTypes are different for Allocate";
    private String ERROR_ALLOCATE_OBJECT_SUB_TYPES_SAME = "objectSubTypes are the same for Allocate";
    private String ERROR_PROCESS_SPLIT_SINGLE_ACCOUNT = "process split error happens when one item has one account";
    private String ERROR_PROCESS_SPLIT_MULTIPLE_ACCOUNT = "process split error happens when one item has multiple accounts";
    private String ERROR_PROCESS_MERGE = "process merge error";
    private String ERROR_PROCESS_ALLOCATE = "process allocate error";

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        purApLineService = SpringContext.getBean(PurApLineService.class);

        prepareTestDataRecords();
    }

    private void prepareTestDataRecords() throws Exception {
        // create 2 PREQ documents and 1 CM document matching the same PO. the first PREQ document has two line items and all the
        // other document has one line item in each document. Each item has one accounting line associated with.
        purApDocuments = PurchasingAccountsPayableDocumentFixture.createPurApDocuments();
        preqDocumentWithTwoItemsSingleAccount = purApDocuments.get(0);
        preqDocumentWithSingleItemSingleAccount = purApDocuments.get(1);
        cmDocumentWithSingleItemTwoAccounts = purApDocuments.get(2);
        percentItemAsset = preqDocumentWithTwoItemsSingleAccount.getPurchasingAccountsPayableItemAssets().get(0);
    }

    public void testProcessPercentPayment() throws Exception {
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();
        purApLineService.processPercentPayment(percentItemAsset, actionsTaken);
        assertEquals(percentItemAsset.getAccountsPayableItemQuantity(), new KualiDecimal(1));
        assertEquals(percentItemAsset.getTotalCost(), getTotalCost(percentItemAsset));
        assertEquals(percentItemAsset.getUnitCost(), getTotalCost(percentItemAsset));
        // check action taken history
        assertEquals(actionsTaken.size(), 1);
        assertEquals(actionsTaken.get(0).getActionTypeCode(), CabConstants.Actions.PERCENT_PAYMENT);

    }

    public void testProcessPercentPayment_noAction() throws Exception {
        percentItemAsset.setAccountsPayableItemQuantity(new KualiDecimal(2));
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();
        purApLineService.processPercentPayment(percentItemAsset, actionsTaken);
        assertEquals(percentItemAsset.getAccountsPayableItemQuantity(), new KualiDecimal(2));
        // check action taken history
        assertEquals(actionsTaken.size(), 0);
    }

    private KualiDecimal getTotalCost(PurchasingAccountsPayableItemAsset item) {
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            totalCost = totalCost.add(account.getItemAccountTotalAmount());
        }
        return totalCost;
    }

    public void testMergeLinesHasDifferentObjectSubTypes_True() {
        List<PurchasingAccountsPayableItemAsset> items = new ArrayList<PurchasingAccountsPayableItemAsset>();

        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(1));
        items.add(purApDocuments.get(1).getPurchasingAccountsPayableItemAssets().get(0));

        assertTrue(ERROR_MERGE_OBJECT_SUB_TYPES_DIFFERENT, purApLineService.mergeLinesHasDifferentObjectSubTypes(items));
    }

    public void testMergeLinesHasDifferentObjectSubTypes_False() {
        List<PurchasingAccountsPayableItemAsset> items = new ArrayList<PurchasingAccountsPayableItemAsset>();

        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(0));
        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(1));

        assertFalse(ERROR_MERGE_OBJECT_SUB_TYPES_SAME, purApLineService.mergeLinesHasDifferentObjectSubTypes(items));
    }

    public void testAllocateLinesHasDifferentObjectSubTypes_Ture() {
        List<PurchasingAccountsPayableItemAsset> items = new ArrayList<PurchasingAccountsPayableItemAsset>();

        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(0));
        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(1));

        PurchasingAccountsPayableItemAsset allocateSourceItem = purApDocuments.get(1).getPurchasingAccountsPayableItemAssets().get(0);
        assertTrue(ERROR_ALLOCATE_OBJECT_SUB_TYPES_DIFFERENT, purApLineService.allocateLinesHasDifferentObjectSubTypes(items, allocateSourceItem));
    }

    public void testAllocateLinesHasDifferentObjectSubTypes_False() {
        List<PurchasingAccountsPayableItemAsset> items = new ArrayList<PurchasingAccountsPayableItemAsset>();

        items.add(purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(0));
        items.add(purApDocuments.get(2).getPurchasingAccountsPayableItemAssets().get(0));

        PurchasingAccountsPayableItemAsset allocateSourceItem = purApDocuments.get(0).getPurchasingAccountsPayableItemAssets().get(1);
        assertFalse(ERROR_ALLOCATE_OBJECT_SUB_TYPES_SAME, purApLineService.allocateLinesHasDifferentObjectSubTypes(items, allocateSourceItem));
    }

    public void testProcessSplit_ItemHasSingleAccount() {
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();

        PurchasingAccountsPayableItemAsset splitItemAsset = preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().get(0);
        int oldItemSize = preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().size();
        KualiDecimal oldQuantity = splitItemAsset.getAccountsPayableItemQuantity();
        KualiDecimal oldTotalCost = getTotalCost(splitItemAsset);

        splitItemAsset.setSplitQty(new KualiDecimal(1));
        purApLineService.processSplit(splitItemAsset, actionsTaken);

        // check new item created
        assertTrue(ERROR_PROCESS_SPLIT_SINGLE_ACCOUNT, ++oldItemSize == preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().size());
        // check split item new quantity
        assertTrue(ERROR_PROCESS_SPLIT_SINGLE_ACCOUNT, splitItemAsset.getAccountsPayableItemQuantity().equals(oldQuantity.subtract(new KualiDecimal(1))));
        // check the total quantity after split does not change
        KualiDecimal newQuantity = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableItemAsset item : preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets()) {
            newQuantity = newQuantity.add(item.getAccountsPayableItemQuantity());
        }
        assertEquals(oldQuantity, newQuantity);

        // check the total cost after split does not change
        KualiDecimal newTotalCost = KualiDecimal.ZERO;
        newTotalCost = newTotalCost.add(getTotalCost(preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().get(0)));
        newTotalCost = newTotalCost.add(getTotalCost(preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().get(1)));
        assertTrue(ERROR_PROCESS_SPLIT_SINGLE_ACCOUNT, oldTotalCost.equals(newTotalCost));

        // check the actionsTakenHistory has one entry for each account moved from source item to new
        assertTrue(ERROR_PROCESS_SPLIT_SINGLE_ACCOUNT, actionsTaken.size() == 1);
    }

    public void testProcessSplit_ItemHasTwoAccounts() {
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();

        PurchasingAccountsPayableItemAsset splitItemAsset = cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets().get(0);
        int oldItemSize = cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets().size();
        KualiDecimal oldQuantity = splitItemAsset.getAccountsPayableItemQuantity();
        KualiDecimal oldTotalCost = getTotalCost(splitItemAsset);

        splitItemAsset.setSplitQty(new KualiDecimal(1));
        purApLineService.processSplit(splitItemAsset, actionsTaken);

        // check new item created
        assertTrue(ERROR_PROCESS_SPLIT_MULTIPLE_ACCOUNT, ++oldItemSize == cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets().size());
        // check split item new quantity
        assertTrue(ERROR_PROCESS_SPLIT_MULTIPLE_ACCOUNT, splitItemAsset.getAccountsPayableItemQuantity().equals(oldQuantity.subtract(new KualiDecimal(1))));
        // check the total quantity after split does not change
        KualiDecimal newQuantity = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableItemAsset item : cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets()) {
            newQuantity = newQuantity.add(item.getAccountsPayableItemQuantity());
        }
        assertEquals(oldQuantity, newQuantity);

        // check the total cost after split does not change
        KualiDecimal newTotalCost = KualiDecimal.ZERO;
        newTotalCost = newTotalCost.add(getTotalCost(cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets().get(0)));
        newTotalCost = newTotalCost.add(getTotalCost(cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets().get(1)));
        assertTrue(ERROR_PROCESS_SPLIT_MULTIPLE_ACCOUNT, oldTotalCost.equals(newTotalCost));

        // check the actionsTakenHistory has two entries since the source split item has two accounts.
        assertTrue(ERROR_PROCESS_SPLIT_MULTIPLE_ACCOUNT, actionsTaken.size() == 2);
    }

    public void testProcessMerge_NotMergeAll() {
        List<PurchasingAccountsPayableActionHistory> actionsTakeHistory = new ArrayList<PurchasingAccountsPayableActionHistory>();
        List<PurchasingAccountsPayableItemAsset> mergeLines = new ArrayList<PurchasingAccountsPayableItemAsset>();
        mergeLines.addAll(preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets());
        mergeLines.addAll(preqDocumentWithTwoItemsSingleAccount.getPurchasingAccountsPayableItemAssets());

        // calculate the total cost of all merge lines before action
        KualiDecimal oldTotalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableItemAsset item : mergeLines) {
            oldTotalCost = oldTotalCost.add(getTotalCost(item));
        }

        purApLineService.processMerge(mergeLines, actionsTakeHistory, false);

        // check all lines merge into the first item.
        assertTrue(ERROR_PROCESS_MERGE, preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().size() == 1);
        assertTrue(ERROR_PROCESS_MERGE, preqDocumentWithTwoItemsSingleAccount.getPurchasingAccountsPayableItemAssets().isEmpty());

        // check the total cost after merge does not change
        KualiDecimal newTotalCost = getTotalCost(preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().get(0));
        assertTrue(ERROR_PROCESS_MERGE, oldTotalCost.equals(newTotalCost));

        // check document is inactive since all its items are merged into another document item
        assertTrue(ERROR_PROCESS_MERGE, !preqDocumentWithTwoItemsSingleAccount.isActive());

        // check the actionsTakeHistory
        assertTrue(ERROR_PROCESS_MERGE, actionsTakeHistory.size() == 2);
    }

    public void testProcessAllocate_AllocateOneItemToAllTheOtherItems() {
        List<PurchasingAccountsPayableActionHistory> actionsTakeHistory = new ArrayList<PurchasingAccountsPayableActionHistory>();
        PurchasingAccountsPayableItemAsset sourceLineItem = preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().get(0);
        List<PurchasingAccountsPayableItemAsset> allocateTargetLines = new ArrayList<PurchasingAccountsPayableItemAsset>();
        // preserve the total cost before allocate.
        KualiDecimal oldTotalCost = getTotalCost(sourceLineItem);
        

        // build the allocate target item list
        for (PurchasingAccountsPayableItemAsset item : preqDocumentWithTwoItemsSingleAccount.getPurchasingAccountsPayableItemAssets()) {
            allocateTargetLines.add(item);
            oldTotalCost = oldTotalCost.add(getTotalCost(item));
        }

        for (PurchasingAccountsPayableItemAsset item : cmDocumentWithSingleItemTwoAccounts.getPurchasingAccountsPayableItemAssets()) {
            allocateTargetLines.add(item);
            oldTotalCost = oldTotalCost.add(getTotalCost(item));
        }

        purApLineService.processAllocate(sourceLineItem, allocateTargetLines, actionsTakeHistory, purApDocuments, false);

        // check the source item is removed from the document
        assertTrue("PurchasingAccountsPayableItemAssets was not empty", preqDocumentWithSingleItemSingleAccount.getPurchasingAccountsPayableItemAssets().isEmpty());
        // check the source is inactive
        assertFalse("preqDocumentWithSingleItemSingleAccount was active", preqDocumentWithSingleItemSingleAccount.isActive());

        // check total amount of all documents doesn't change after allocate
        KualiDecimal newTotalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableItemAsset item : allocateTargetLines) {
            newTotalCost = newTotalCost.add(getTotalCost(item));
        }
        assertTrue(ERROR_PROCESS_ALLOCATE, oldTotalCost.equals(newTotalCost));
        
        // check the actionTakenHistory, allocate based on target account amount. so the number of actions taken equals the target accounts.
        assertTrue(ERROR_PROCESS_ALLOCATE, actionsTakeHistory.size() == 4);
    }
}
