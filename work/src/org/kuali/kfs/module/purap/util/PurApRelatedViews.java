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
package org.kuali.kfs.module.purap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.AbstractRelatedView;
import org.kuali.kfs.module.purap.businessobject.BulkReceivingView;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.ReceivingCorrectionView;
import org.kuali.kfs.module.purap.businessobject.ReceivingLineView;
import org.kuali.kfs.module.purap.businessobject.RequisitionView;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;

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
    private transient List<BulkReceivingView> relatedBulkReceivingViews;
    
    private transient List<PurchaseOrderViewGroup> groupedRelatedPurchaseOrderViews;
    
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
                        break;
                    }
                }
            }
        }
        return relatedList;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<RequisitionView> getRelatedRequisitionViews() {
        relatedRequisitionViews = updateRelatedView(RequisitionView.class, relatedRequisitionViews, true);
        return relatedRequisitionViews;
    }

    /**
     * Obtains from the database a list of PurchaseOrderViews in the standard order
     * for such views and sorts them.  They are sorted by POID, and then by current
     * indicator, with the current documents at the top.
     * 
     * @return  A sorted List<PurchaseOrderView>
     */
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        // Obtain a list which is sorted by workflow document ID.
        relatedPurchaseOrderViews = updateRelatedView(PurchaseOrderView.class, relatedPurchaseOrderViews, true);
        // Sort the list.
        Collections.sort(relatedPurchaseOrderViews, 
                new Comparator<PurchaseOrderView>() {
                    public int compare(PurchaseOrderView a, PurchaseOrderView b) {
                        if ((a != null) && (b != null) && 
                            (a.getPurapDocumentIdentifier() != null) &&
                            (b.getPurapDocumentIdentifier() != null)) {
                            // Sort on basis of POID...
                            int poIdResult = a.getPurapDocumentIdentifier().compareTo(b.getPurapDocumentIdentifier());
                            // unless POIDs are the same; then current PO should be on top.
                            return ( (poIdResult != 0) ? poIdResult :
                                (a.getPurchaseOrderCurrentIndicator() ? -1 :
                                (b.getPurchaseOrderCurrentIndicator() ? 1 : 0)));
                        }
                        return 0;
                    }
                }
        );
        return relatedPurchaseOrderViews;
    }
    
    /**
     * Groups PurchaseOrderViews from relatedPurchaseOrderViews by PurchaseOrderIdentifier.  Assumes
     * the sorting by POID and by current indicator accomplished by getRelatedPurchaseOrderViews.  
     * 
     * This extra layer of grouping is necessary in order to display the notes for a group of related 
     * POChange documents (which should be identical) after that group from within 
     * relatedPurchaseOrderDocumentsDetail.tag, and before any other related groups which may result 
     * from PO splitting, and have different PuchaseOrderIdentifiers.  With direct use 
     * of relatedPurchaseOrderViews, location of the end of the group is problematic.
     * 
     * @return  A List<PurchaseOrderViewGroup>
     * @see org.kuali.kfs.module.purap.util.PurApRelatedViews.getRelatedPurchaseOrderViews
     * @see org.kuali.kfs.module.purap.businessobject.PurchaseOrderView
     */
    public List<PurchaseOrderViewGroup> getGroupedRelatedPurchaseOrderViews() {
        groupedRelatedPurchaseOrderViews = new ArrayList<PurchaseOrderViewGroup>();
        PurchaseOrderViewGroup group = new PurchaseOrderViewGroup();
        int previousPOID = 0;
        relatedPurchaseOrderViews = getRelatedPurchaseOrderViews();
        for(PurchaseOrderView view : relatedPurchaseOrderViews) {
            if (previousPOID == 0) {
                previousPOID = view.getPurapDocumentIdentifier();
                
            }
            if( view.getPurapDocumentIdentifier() == previousPOID ) {
                group.getViews().add(view);
            }
            else {
                groupedRelatedPurchaseOrderViews.add(group);
                group = new PurchaseOrderViewGroup();
                group.getViews().add(view);
                previousPOID = view.getPurapDocumentIdentifier();
            }
            if (relatedPurchaseOrderViews.size() == relatedPurchaseOrderViews.indexOf(view) + 1) {
                groupedRelatedPurchaseOrderViews.add(group);
            }
        }
        return groupedRelatedPurchaseOrderViews;
    }
    
    /**
     * A container for a List<PurchaseOrderView>, to be used by a nested c:forEach tag
     * in relatedPurchaseOrderDocumentsDetail.tag.
     */
    protected class PurchaseOrderViewGroup {
        private List<PurchaseOrderView> views = new ArrayList<PurchaseOrderView>();
        
        protected PurchaseOrderViewGroup() {
        }

        public List<PurchaseOrderView> getViews() {
            return views;
        }

        public void setViews(List<PurchaseOrderView> views) {
            this.views = views;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedPaymentRequestViews()
     */
    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        relatedPaymentRequestViews = updateRelatedView(PaymentRequestView.class, relatedPaymentRequestViews, true);
        return relatedPaymentRequestViews;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedCreditMemoViews()
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
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<ReceivingLineView> getRelatedReceivingLineViews() {
        relatedReceivingLineViews = updateRelatedView(ReceivingLineView.class, relatedReceivingLineViews, true);
        return relatedReceivingLineViews;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<ReceivingCorrectionView> getRelatedReceivingCorrectionViews() {
        relatedReceivingCorrectionViews = updateRelatedView(ReceivingCorrectionView.class, relatedReceivingCorrectionViews, true);
        return relatedReceivingCorrectionViews;
    }
    
    public List<BulkReceivingView> getRelatedBulkReceivingViews() {
        relatedBulkReceivingViews = updateRelatedView(BulkReceivingView.class, relatedBulkReceivingViews, true);
        return relatedBulkReceivingViews;
    }


}
