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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurapAccountingServiceFixture {

    PREQ_PRORATION_ONE_ACCOUNT(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    PREQ_PRORATION_TWO_ACCOUNTS(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT),
    PREQ_PRORATION_THIRDS(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH),
    PREQ_PRORATION_ONE_ACCOUNT_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    PREQ_PRORATION_TWO_ACCOUNTS_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT),
    PREQ_PRORATION_THIRDS_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH),
    REQ_PRORATION_ONE_ACCOUNT(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    REQ_PRORATION_TWO_ACCOUNTS(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT),
    REQ_PRORATION_THIRDS(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH),
    REQ_PRORATION_ONE_ACCOUNT_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    REQ_PRORATION_TWO_ACCOUNTS_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT),
    REQ_PRORATION_THIRDS_ZERO_TOTAL(
            PurapTestConstants.AmountsLimits.ZERO,PurapConstants.PRORATION_SCALE,RequisitionAccount.class,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH),
    CREDIT_MEMO_ONE_ACCOUNT(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,CreditMemoAccount.class,
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    REQ_SUMMARY_ONE_ITEM_ONE_ACCOUNT( new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 },
            new AccountingLineFixture[] { AccountingLineFixture.PURAP_LINE1 },
            new Integer[] {0} ),
    REQ_SUMMARY_ONE_ITEM_TWO_ACCOUNTS( new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 },
            new AccountingLineFixture[] { AccountingLineFixture.PURAP_LINE1,
                                          AccountingLineFixture.PURAP_LINE2},
            new Integer[] {0,0} ),
    REQ_SUMMARY_TWO_ITEMS_ONE_ACCOUNT( new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1,
                                                                      RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_2 },
            new AccountingLineFixture[] { AccountingLineFixture.PURAP_LINE1 },
            new Integer[] {0,1} ),
            ;

    KualiDecimal totalAmount;
    Integer percentScale;
    Class accountClass;
    List<SourceAccountingLine> sourceAccountingLineList = new ArrayList<SourceAccountingLine>();
    List<PurApAccountingLine> purApAccountingLineList = new ArrayList<PurApAccountingLine>();
    AccountingLineFixture accountingLineFixture[] = {
            AccountingLineFixture.PURAP_LINE1,
            AccountingLineFixture.PURAP_LINE2,
            AccountingLineFixture.PURAP_LINE3};
    List<PurApItem> items = new ArrayList<PurApItem>();

    /**
     * Constructs a PurapAccountingServiceFixture.java.
     *
     * @param totalAmount
     * @param percentScale
     * @param accountClass
     * @param purApAcctLineFixtures
     */
    private PurapAccountingServiceFixture(
            KualiDecimal totalAmount,
            Integer percentScale,
            Class accountClass,
            PurApAccountingLineFixture ... purApAcctLineFixtures) {

        this.totalAmount = totalAmount;
        this.percentScale = percentScale;
        this.accountClass = accountClass;

        for( int i = 0; i < purApAcctLineFixtures.length; i++ ) {
            PurApAccountingLineFixture purApAcctLineFixture = purApAcctLineFixtures[i];
            PurApAccountingLine purApAcctLine = purApAcctLineFixture.createPurApAccountingLine(accountClass, accountingLineFixture[i]);
            BigDecimal pct = purApAcctLine.getAccountLinePercent();
            pct = pct.divide(new BigDecimal(100));
            purApAcctLine.setAmount(totalAmount.multiply(new KualiDecimal(pct.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR))));
            purApAccountingLineList.add(purApAcctLine);
            sourceAccountingLineList.add(purApAcctLine.generateSourceAccountingLine());
        }
    }

    /**
     * Constructs a PurapAccountingServiceFixture.java.
     *
     * @param itemFixtures
     */
    private PurapAccountingServiceFixture(RequisitionItemFixture[] itemFixtures, AccountingLineFixture[] acctLineFixtures, Integer[] positions) {
        for(RequisitionItemFixture itemFixture : itemFixtures) {
            PurchasingItem item = itemFixture.createRequisitionItem();
            items.add(item);
        }
        for( int i = 0; i < acctLineFixtures.length; i++ ) {
            AccountingLineFixture acctLineFixture = acctLineFixtures[i];
            SourceAccountingLine sourceLine = null;
            try {
                sourceLine = acctLineFixture.createSourceAccountingLine();
                sourceAccountingLineList.add(sourceLine);
                PurApAccountingLine purApAccountingLine = PurApAccountingLineFixture.BASIC_ACCOUNT_1.createPurApAccountingLine(RequisitionAccount.class, acctLineFixture);
                if(positions[i] != null) {
                    items.get(positions[i].intValue()).getSourceAccountingLines().add(purApAccountingLine);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds an identical total and a list of source accounting lines to each item of a given RequisitionDocument.
     *
     * @param   req     A RequisitionDocument
     * @return          The same document, with the totals and source accounting line lists added to its items
     */
    private RequisitionDocument augmentRequisitionDocument(RequisitionDocument req) {
        List<RequisitionItem> augmentedItems = new ArrayList<RequisitionItem>();
        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            item.setTotalAmount(this.totalAmount);

            // fix amounts
            for (PurApAccountingLine purApAccountingLine: purApAccountingLineList) {
                purApAccountingLine.setAmount(item.calculateExtendedPrice().multiply(new KualiDecimal(purApAccountingLine.getAccountLinePercent())).divide(new KualiDecimal(100)));
            }

            item.setSourceAccountingLines(purApAccountingLineList);
            augmentedItems.add(item);
        }
        req.setItems(augmentedItems);
        return req;
    }

    /**
     * Creates a minimal RequisitionDocument populated with one item with the current total amount and set of
     * source accounting lines.
     *
     * @return  A RequisitionDocument with one, populated item
     */
    public RequisitionDocument generateRequisitionDocument_OneItem() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        return augmentRequisitionDocument(req);
    }

    /**
     * Creates a minimal RequisitionDocument populated with two items, each with the current total amount and
     * set of source accounting lines.
     *
     * @return  A RequisitionDocument with two, populated items
     */
    public RequisitionDocument generateRequisitionDocument_TwoItems() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_TWO_ITEMS.createRequisitionDocument();
        return augmentRequisitionDocument(req);
    }

    /**
     * Adds an identical total and a list of source accounting lines to each item of a given PaymentRequestDocument.
     *
     * @param preq      A PaymentRequestDocument
     * @return          The same document, with the totals and source accounting line lists add to its items
     */
    private PaymentRequestDocument augmentPaymentRequestDocument(PaymentRequestDocument preq) {
        List<PaymentRequestItem> augmentedItems = new ArrayList<PaymentRequestItem>();
        for(PaymentRequestItem item : (List<PaymentRequestItem>)preq.getItems()) {
            item.setTotalAmount(this.totalAmount);
            item.setSourceAccountingLines(purApAccountingLineList);
            augmentedItems.add(item);
        }
        preq.setItems(augmentedItems);
        return preq;
    }

    /**
     * Creates a minimal PaymentRequestDocument populated with one item with the current total amount and set of
     * source accounting lines.
     *
     * @return  A PaymentRequestDocument with one, populated item
     */
    public PaymentRequestDocument generatePaymentRequestDocument_OneItem() {
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_ONLY_REQUIRED_FIELDS.createPaymentRequestDocument();
        return augmentPaymentRequestDocument(preq);
    }

    /**
     * Creates a minimal PaymentRequestDocument populated with two items, each with the current total amount and
     * set of source accounting lines.
     *
     * @return  A PaymentRequestDocument with two, populated items
     */
    public PaymentRequestDocument generatePaymentRequestDocument_TwoItems() {
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_TWO_ITEM.createPaymentRequestDocument();
        return augmentPaymentRequestDocument(preq);
    }


    private VendorCreditMemoDocument augmentVendorCreditMemoDocument(VendorCreditMemoDocument vcm) {
        List<CreditMemoItem> augmentedItems = new ArrayList<CreditMemoItem>();
        for(CreditMemoItem item : (List<CreditMemoItem>)vcm.getItems()) {
            item.setTotalAmount(this.totalAmount);
            item.setSourceAccountingLines(purApAccountingLineList);
            augmentedItems.add(item);
        }
        vcm.setItems(augmentedItems);
        return vcm;
    }

    public VendorCreditMemoDocument generateVendorCreditMemoDocument_OneItem() {
        VendorCreditMemoDocument vcm = CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
        return augmentVendorCreditMemoDocument(vcm);
    }

    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPercentScale() {
        return percentScale;
    }

    public void setPercentScale(Integer percentScale) {
        this.percentScale = percentScale;
    }

    public Class getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(Class accountClass) {
        this.accountClass = accountClass;
    }

    public List<SourceAccountingLine> getSourceAccountingLineList() {
        return sourceAccountingLineList;
    }

    public void setSourceAccountingLineList(List<SourceAccountingLine> sourceAccountingLineList) {
        this.sourceAccountingLineList = sourceAccountingLineList;
    }

    public List<PurApAccountingLine> getPurApAccountingLineList() {
        return purApAccountingLineList;
    }

    public void setPurApAccountingLineList(List<PurApAccountingLine> purApAccountingLineList) {
        this.purApAccountingLineList = purApAccountingLineList;
    }

    public List<PurApItem> getItems() {
        return items;
    }

    public void setItems(List<PurApItem> items) {
        this.items = items;
    }

}
