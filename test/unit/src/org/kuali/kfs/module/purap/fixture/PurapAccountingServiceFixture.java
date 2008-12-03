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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

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
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH),;
    
    KualiDecimal totalAmount;
    Integer percentScale;
    Class accountClass;
    List<SourceAccountingLine> sourceAccountingLineList = new ArrayList<SourceAccountingLine>();
    List<PurApAccountingLine> purApAccountingLineList = new ArrayList<PurApAccountingLine>();
    AccountingLineFixture accountingLineFixture[] = {
            AccountingLineFixture.PURAP_LINE1,
            AccountingLineFixture.PURAP_LINE2,
            AccountingLineFixture.PURAP_LINE3};

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
            purApAcctLine.setAmount(totalAmount.multiply(new KualiDecimal(pct)));
            purApAccountingLineList.add(purApAcctLine);
            sourceAccountingLineList.add(purApAcctLine.generateSourceAccountingLine());
        }
    }
    
    private RequisitionDocument augmentRequisitionDocument(RequisitionDocument req) {
        List<RequisitionItem> augmentedItems = new TypedArrayList(RequisitionItem.class);
        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            item.setTotalAmount(this.totalAmount);
            item.setSourceAccountingLines(purApAccountingLineList);        
            augmentedItems.add(item);
        }
        req.setItems(augmentedItems);
        return req;
    }
    
    public RequisitionDocument generateRequisitionDocument_OneItem() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        return augmentRequisitionDocument(req);
    }
    
    public RequisitionDocument generateRequisitionDocument_TwoItems() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_TWO_ITEMS.createRequisitionDocument();
        return augmentRequisitionDocument(req);
    }
    
    private PaymentRequestDocument augmentPaymentRequestDocument(PaymentRequestDocument preq) {
        List<PaymentRequestItem> augmentedItems = new TypedArrayList(PaymentRequestItem.class);
        for(PaymentRequestItem item : (List<PaymentRequestItem>)preq.getItems()) {
            item.setTotalAmount(this.totalAmount);
            item.setSourceAccountingLines(purApAccountingLineList);        
            augmentedItems.add(item);
        }
        preq.setItems(augmentedItems);
        return preq;
    }
    
    public PaymentRequestDocument generatePaymentRequestDocument_OneItem() {
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_ONLY_REQUIRED_FIELDS.createPaymentRequestDocument();
        return augmentPaymentRequestDocument(preq);
    }
    
    public PaymentRequestDocument generatePaymentRequestDocument_TwoItems() {
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_TWO_ITEM.createPaymentRequestDocument();
        return augmentPaymentRequestDocument(preq);
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
}
