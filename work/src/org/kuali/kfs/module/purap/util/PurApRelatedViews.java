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
package org.kuali.kfs.module.purap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AbstractRelatedView;
import org.kuali.kfs.module.purap.businessobject.BulkReceivingView;
import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingView;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectView;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingView;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApGenericAttributes;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.RequisitionView;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurApRelatedViews {
    private String documentNumber;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    private transient List<RequisitionView> relatedRequisitionViews;
    private transient List<PurchaseOrderView> relatedPurchaseOrderViews;
    private transient List<PaymentRequestView> relatedPaymentRequestViews;
    private transient List<PaymentRequestView> paymentHistoryPaymentRequestViews;
    private transient List<CreditMemoView> relatedCreditMemoViews;
    private transient List<CreditMemoView> paymentHistoryCreditMemoViews;
    private transient List<LineItemReceivingView> relatedLineItemReceivingViews;
    private transient List<CorrectionReceivingView> relatedCorrectionReceivingViews;
    private transient List<BulkReceivingView> relatedBulkReceivingViews;
    private transient List<PurchaseOrderViewGroup> groupedRelatedPurchaseOrderViews;
    private transient List<ReceivingViewGroup> groupedRelatedReceivingViews;
    private transient List<ElectronicInvoiceRejectView> relatedRejectViews;

    public PurApRelatedViews(String documentNumber, Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        super();
        this.documentNumber = documentNumber;
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    /**
     * Reset all related view lists to null.
     */
    public void resetRelatedViews() {
        relatedRequisitionViews = null;
        relatedPurchaseOrderViews = null;
        relatedPaymentRequestViews = null;
        paymentHistoryPaymentRequestViews = null;
        relatedCreditMemoViews = null;
        paymentHistoryCreditMemoViews = null;
        relatedLineItemReceivingViews = null;
        relatedCorrectionReceivingViews = null;
        relatedBulkReceivingViews = null;
        groupedRelatedPurchaseOrderViews = null;
        groupedRelatedReceivingViews = null;
        relatedRejectViews = null;
    }

    public List updateRelatedView(Class<?> clazz, List<? extends AbstractRelatedView> relatedList, boolean removeCurrentDocument) {
        if (relatedList == null) {
            relatedList = SpringContext.getBean(PurapService.class).getRelatedViews(clazz, accountsPayablePurchasingDocumentLinkIdentifier);
            if (removeCurrentDocument) {
                for (AbstractRelatedView view : relatedList) {
                    //KFSMI-4576 Mask/Unmask purapDocumentIdentifier field value
                    maskPONumberIfUnapproved(view);
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
     * masks the po number if the po is unappoved yet.  If the document status is not FINAL then
     * check for permission for purapDocumentIdentifier field.  If NOT permitted to view the value
     * then mask the value with * and setting this value in poNumberMasked property.
     *
     * @param view
     */
    protected void maskPONumberIfUnapproved(AbstractRelatedView view) {
        Document document = findDocument(view.getDocumentNumber());

        String poIDstr = "";

        if (ObjectUtils.isNotNull(view.getPurapDocumentIdentifier())) {
            poIDstr = view.getPurapDocumentIdentifier().toString();
        }

        if (PurapConstants.PurapDocTypeCodes.PO_DOCUMENT.equals(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName())) {
            if ((document != null) && (document.getDocumentHeader().getWorkflowDocument() != null)) {
                if (!document.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {

                    String principalId =  GlobalVariables.getUserSession().getPrincipalId();
                    
                    String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
                    String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;

                    Map<String,String> roleQualifiers = new HashMap<String,String>();

                    Map<String,String> permissionDetails = new HashMap<String,String>();
                    permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, PurchaseOrderDocument.class.getSimpleName());
                    permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, PurapPropertyConstants.PURAP_DOC_ID);

                    IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
                    Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
                    if (!isAuthorized) {
                        //not authorized to see... so mask the po number string
                        poIDstr = "";
                        int strLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(PurApGenericAttributes.class.getName(), PurapPropertyConstants.PURAP_DOC_ID);
                        for (int i = 0; i < strLength; i++) {
                            poIDstr = poIDstr.concat("*");
                        }
                    }
                }
            }
        }

        view.setPoNumberMasked(poIDstr);
    }

    /**
     * This method finds the document for the given document header id
     * @param documentHeaderId
     * @return document The document in the workflow that matches the document header id.
     */
    protected Document findDocument(String documentHeaderId) {
        Document document = null;

        try {
            document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentHeaderId);
        }
        catch (WorkflowException ex) {
        } catch (UnknownDocumentTypeException ex) {
            // don't blow up just because a document type is not installed (but don't return it either)
        }

        return document;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<RequisitionView> getRelatedRequisitionViews() {
        relatedRequisitionViews = updateRelatedView(RequisitionView.class, relatedRequisitionViews, true);
        return relatedRequisitionViews;
    }

    public List<ElectronicInvoiceRejectView> getRelatedRejectViews() {
        relatedRejectViews = updateRelatedView(ElectronicInvoiceRejectView.class, relatedRejectViews, true);
        return relatedRejectViews;
    }

    /**
     * Obtains a list of related PurchaseOrderViews, first ordered by POIDs descending, then by document numbers descending;
     * thus POs with newer POIDs will be in the front, and within the same POID, the current PO will be in the front.
     *
     * @return  A list of <PurchaseOrderView> with newer POs in the front.
     */
    public List<PurchaseOrderView> getRelatedPurchaseOrderViews() {
        if (relatedPurchaseOrderViews != null) {
            return relatedPurchaseOrderViews;
        }

        // Obtain a list which is sorted by workflow document ID descending.
        relatedPurchaseOrderViews = updateRelatedView(PurchaseOrderView.class, relatedPurchaseOrderViews, true);

        // Sort the list.
        Collections.sort(relatedPurchaseOrderViews,
                new Comparator<PurchaseOrderView>() {
                    @Override
                    public int compare(PurchaseOrderView v1, PurchaseOrderView v2) {
                        if ((v1 != null) && (v2 != null) &&
                            (v1.getPurapDocumentIdentifier() != null) &&
                            (v2.getPurapDocumentIdentifier() != null)) {
                            // sort by POID descending
                            int compare = -v1.getPurapDocumentIdentifier().compareTo(v2.getPurapDocumentIdentifier());
                            // if POIDs are the same, sort by document number descending; usually current PO has biggest documentNumber
                            if (compare == 0) {
                                compare = v1.getPurchaseOrderCurrentIndicator() ? -1 :
                                    v2.getPurchaseOrderCurrentIndicator() ? 1 :
                                    -v1.getDocumentNumber().compareTo(v2.getDocumentNumber());
                            }
                            return compare;
                        }
                        return 0;
                    }
                }
        );

        return relatedPurchaseOrderViews;
    }

    /**
     * Groups related PurchaseOrderViews by POIDs descending, and within each group order POs by document numbers descending;
     * thus groups of newer POIDs will be in the front, and within each group, more current POs will be in the front.
     *
     * @return  A list of <PurchaseOrderViewGroup> with newer POs in the front.
     * @see org.kuali.kfs.module.purap.util.PurApRelatedViews.getRelatedPurchaseOrderViews
     * @see org.kuali.kfs.module.purap.businessobject.PurchaseOrderView
     */
    public List<PurchaseOrderViewGroup> getGroupedRelatedPurchaseOrderViews() {
        if (groupedRelatedPurchaseOrderViews != null) {
            return groupedRelatedPurchaseOrderViews;
        }

        /*
         * This extra layer of grouping is necessary in order to display the notes for a group of
         * related POChange documents (which should have identical POID) after that group,
         * and before any other related groups which may result from PO splitting (with different POIDs).
         * With direct use of relatedPurchaseOrderViews, location of the end of the group is problematic.
         */
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
    public List<LineItemReceivingView> getRelatedLineItemReceivingViews() {
        relatedLineItemReceivingViews = updateRelatedView(LineItemReceivingView.class, relatedLineItemReceivingViews, true);
        return relatedLineItemReceivingViews;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument#getRelatedRequisitionViews()
     */
    public List<CorrectionReceivingView> getRelatedCorrectionReceivingViews() {
        relatedCorrectionReceivingViews = updateRelatedView(CorrectionReceivingView.class, relatedCorrectionReceivingViews, true);
        return relatedCorrectionReceivingViews;
    }

    public List<BulkReceivingView> getRelatedBulkReceivingViews() {
        relatedBulkReceivingViews = updateRelatedView(BulkReceivingView.class, relatedBulkReceivingViews, true);
        return relatedBulkReceivingViews;
    }

    /**
     * Groups related LineItemReceivingView and its CorrectionReceivingViews, with more recent receiving groups in the front;
     * and within each group, with more recent corrections in the front.
     *
     * @return  A list of ReceivingCorrectionViewGroups.
     */
    public List<ReceivingViewGroup> getGroupedRelatedReceivingViews() {
        if (groupedRelatedReceivingViews != null) {
            return groupedRelatedReceivingViews;
        }

        groupedRelatedReceivingViews = new ArrayList<ReceivingViewGroup>();
        PurapService purapService = SpringContext.getBean(PurapService.class);
        List<LineItemReceivingView> liviews = purapService.getRelatedViews(LineItemReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);
        List<CorrectionReceivingView> crviews = purapService.getRelatedViews(CorrectionReceivingView.class, accountsPayablePurchasingDocumentLinkIdentifier);

        // both LineItemReceivingViews and CorrectionReceivingViews are already in order with most recent first, so no need to sort
        for (LineItemReceivingView liview : liviews) {
            ReceivingViewGroup group = new ReceivingViewGroup();
            group.lineItemView = liview; // could be current document
            for (CorrectionReceivingView crview : crviews) {
                if (StringUtils.equals(crview.getLineItemReceivingDocumentNumber(), liview.getDocumentNumber()) &&
                        !documentNumber.equals(crview.getDocumentNumber())) {// exclude current document
                    group.addCorrectionView(crview);
                }
            }
            groupedRelatedReceivingViews.add(group);
        }

        return groupedRelatedReceivingViews;
    }

    /**
     * A container for a List<PurchaseOrderView>, to be used by a nested c:forEach tag
     * in relatedPurchaseOrderDocumentsDetail.tag.
     */
    public class PurchaseOrderViewGroup {
        protected List<PurchaseOrderView> views = new ArrayList<PurchaseOrderView>();

        protected PurchaseOrderViewGroup() {
        }

        public List<PurchaseOrderView> getViews() {
            return views;
        }
    }

    /**
     * A container for a LineItemReceivingView and a list of its associated CorrectionReceivingViews.
     */
    public class ReceivingViewGroup {
        protected LineItemReceivingView lineItemView;
        protected List<CorrectionReceivingView> correctionViews = new ArrayList<CorrectionReceivingView>();

        protected ReceivingViewGroup() {
        }

        public LineItemReceivingView getLineItemView() {
            return lineItemView;
        }

        public List<CorrectionReceivingView> getCorrectionViews() {
            return correctionViews;
        }

        public void addCorrectionView(CorrectionReceivingView correctionView) {
            correctionViews.add(correctionView);
        }

        public boolean getIsLineItemViewCurrentDocument() {
            return (lineItemView != null && documentNumber.equals(lineItemView.getDocumentNumber()));
        }
    }

}
