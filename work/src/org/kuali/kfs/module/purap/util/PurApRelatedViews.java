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
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
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

    public PurApRelatedViews(String documentNumber, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        super();
        this.documentNumber = documentNumber;
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<RequisitionView> getRelatedRequisitionViews() {
        if (relatedRequisitionViews == null) {
            relatedRequisitionViews = new TypedArrayList(RequisitionView.class);
            List<RequisitionView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(RequisitionView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (RequisitionView view : tmpViews) {
                if (!documentNumber.equals(view.getDocumentNumber())) {
                    relatedRequisitionViews.add(view);
                }
            }
        }
        return relatedRequisitionViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedPurchaseOrderViews()
     */
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        if (relatedPurchaseOrderViews == null) {
            relatedPurchaseOrderViews = new TypedArrayList(PurchaseOrderView.class);
            List<PurchaseOrderView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (PurchaseOrderView view : tmpViews) {
                if (!documentNumber.equals(view.getDocumentNumber())) {
                    if (view.getPurchaseOrderCurrentIndicator()) {
                        // If this is the current purchase order, we'll add it at the front of the List
                        relatedPurchaseOrderViews.add(0, view);
                    }
                    else {
                        // If this is not the current purchase order, we'll just add it to the List
                        relatedPurchaseOrderViews.add(view);
                    }
                }
            }
        }
        return relatedPurchaseOrderViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedPaymentRequestViews()
     */
    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        if (relatedPaymentRequestViews == null) {
            relatedPaymentRequestViews = new TypedArrayList(PaymentRequestView.class);
            List<PaymentRequestView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(PaymentRequestView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (PaymentRequestView view : tmpViews) {
                if (!documentNumber.equals(view.getDocumentNumber())) {
                    relatedPaymentRequestViews.add(view);
                }
            }
        }
        return relatedPaymentRequestViews;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getRelatedCreditMemoViews()
     */
    public List<CreditMemoView> getRelatedCreditMemoViews() {
        relatedCreditMemoViews = new TypedArrayList(CreditMemoView.class);
        List<CreditMemoView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(CreditMemoView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        for (CreditMemoView view : tmpViews) {
            if (!this.documentNumber.equals(view.getDocumentNumber())) {
                relatedCreditMemoViews.add(view);
            }
        }
        return relatedCreditMemoViews;
    }

    /**
     * Gets the Payment History Payment Request Views for this document.
     * 
     * @return the list of Payment History Payment Request Views.
     */
    public List<PaymentRequestView> getPaymentHistoryPaymentRequestViews() {
        if (paymentHistoryPaymentRequestViews == null) {
            paymentHistoryPaymentRequestViews = new TypedArrayList(PaymentRequestView.class);
            List<PaymentRequestView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(PaymentRequestView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (PaymentRequestView view : tmpViews) {
                paymentHistoryPaymentRequestViews.add(view);
            }
        }
        return paymentHistoryPaymentRequestViews;
    }

    /**
     * Gets the Payment History Credit Memo Views for this document.
     * 
     * @return the list of Payment History Credit Memo Views.
     */
    public List<CreditMemoView> getPaymentHistoryCreditMemoViews() {
        if (paymentHistoryCreditMemoViews == null) {
            paymentHistoryCreditMemoViews = new TypedArrayList(CreditMemoView.class);
            List<CreditMemoView> tmpViews = SpringContext.getBean(PurapService.class).getRelatedViews(CreditMemoView.class, accountsPayablePurchasingDocumentLinkIdentifier);
            for (CreditMemoView view : tmpViews) {
                paymentHistoryCreditMemoViews.add(view);
            }
        }
        return paymentHistoryCreditMemoViews;
    }

}
