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
package org.kuali.module.purap.util;

import java.util.List;

import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.AbstractRelatedView;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.ReceivingCorrectionView;
import org.kuali.module.purap.bo.ReceivingLineView;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.service.PurapService;

public class PurApRelatedViews {
    private String documentNumber;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private transient List<RequisitionView> relatedRequisitionViews;
    private transient List<PurchaseOrderView> relatedPurchaseOrderViews;
    private transient List<PaymentRequestView> relatedPaymentRequestViews;
    private transient List<PaymentRequestView> paymentHistoryPaymentRequestViews;
    private transient List<CreditMemoView> relatedCreditMemoViews;
    private transient List<CreditMemoView> paymentHistoryCreditMemoViews;

    private transient List<ReceivingLineView> relatedReceivingLineViews;
    private transient List<ReceivingCorrectionView> relatedReceivingCorrectionViews;

    public PurApRelatedViews(String documentNumber, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        super();
        this.documentNumber = documentNumber;
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public List updateRelatedView(Class<?> clazz, List<? extends AbstractRelatedView> relatedList, boolean removeCurrentDocument) {
        if (relatedList == null) {
            relatedList = SpringContext.getBean(PurapService.class).getRelatedViews(clazz, accountsPayablePurchasingDocumentLinkIdentifier);
            if (removeCurrentDocument) {
                for (AbstractRelatedView view : relatedList) {
                    if (documentNumber.equals(view.getDocumentNumber())) {
                        relatedList.remove(view);
                    }
                }
            }
        }
        return relatedList;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<RequisitionView> getRelatedRequisitionViews() {
        relatedRequisitionViews = updateRelatedView(RequisitionView.class, relatedRequisitionViews, true);
        return relatedRequisitionViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedPurchaseOrderViews()
     */
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        relatedPurchaseOrderViews = updateRelatedView(PurchaseOrderView.class, relatedPurchaseOrderViews, true);
        return relatedPurchaseOrderViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedPaymentRequestViews()
     */
    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        relatedPaymentRequestViews = updateRelatedView(PaymentRequestView.class, relatedPaymentRequestViews, true);
        return relatedPaymentRequestViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedCreditMemoViews()
     */
    public List<CreditMemoView> getRelatedCreditMemoViews() {
        relatedCreditMemoViews = updateRelatedView(CreditMemoView.class, relatedCreditMemoViews, true);
        return relatedCreditMemoViews;
    }

    /**
     * Gets the Payment History Payment Request Views for this document.
     * 
     * @return the list of Payment History Payment Request Views.
     */
    public List<PaymentRequestView> getPaymentHistoryPaymentRequestViews() {
        paymentHistoryPaymentRequestViews = updateRelatedView(PaymentRequestView.class, paymentHistoryPaymentRequestViews, false);
        return paymentHistoryPaymentRequestViews;
    }

    /**
     * Gets the Payment History Credit Memo Views for this document.
     * 
     * @return the list of Payment History Credit Memo Views.
     */
    public List<CreditMemoView> getPaymentHistoryCreditMemoViews() {
        paymentHistoryCreditMemoViews = updateRelatedView(CreditMemoView.class, paymentHistoryCreditMemoViews, false);
        return paymentHistoryCreditMemoViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<ReceivingLineView> getRelatedReceivingLineViews() {
        relatedReceivingLineViews = updateRelatedView(ReceivingLineView.class, relatedReceivingLineViews, true);
        return relatedReceivingLineViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<ReceivingCorrectionView> getRelatedReceivingCorrectionViews() {
        relatedReceivingCorrectionViews = updateRelatedView(ReceivingCorrectionView.class, relatedReceivingCorrectionViews, true);
        return relatedReceivingCorrectionViews;
    }

}
