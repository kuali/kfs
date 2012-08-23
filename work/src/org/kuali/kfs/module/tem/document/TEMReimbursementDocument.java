/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import static org.kuali.kfs.module.tem.TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE;

import java.util.List;

import javax.persistence.Column;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.DisbursementVoucherPaymentMethods;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.beans.BeanUtils;

public abstract class TEMReimbursementDocument extends TravelDocumentBase {
    
    private String paymentMethod = DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE;
    
    @Column(name = "PAYMENT_METHOD", nullable = true, length = 15)
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);
        
        //modify for any of the reimbursement doc to use the payment option instead of default
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(getPaymentMethod());
    }

    /**
     * Perform business rules common to all TEM documents when generating general ledger pending entries. Do not generate the
     * entries if the card type is of ACTUAL Expense.  This entry will be generated per document using the TravelClearingAccount
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
//    @Override
//    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
//        LOG.info("processGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - start");
//
//        boolean success = true;
//        boolean doGenerate = true;
//
//        // special handling for TEMAccountingLine
//        if (glpeSourceDetail instanceof TemAccountingLine) {
//
//            // salesTax seems to be a problem on loading the doc, just filtering this from the output
//            LOG.info(new ReflectionToStringBuilder(glpeSourceDetail, ToStringStyle.MULTI_LINE_STYLE).setExcludeFieldNames(new String[] { "salesTax" }).toString());
//
//            // check by cardType
//            String cardType = ((TemAccountingLine) glpeSourceDetail).getCardType();
//            // do not generate individual entries for each ACTUAL Expenses - generateDocumentGeneralLedgerPendingEntries will handle it
//            doGenerate = !TemConstants.ACTUAL_EXPENSE.equals(cardType);
//
//            if (!doGenerate) {
//                LOG.debug("GLPE processing was skipped for " + glpeSourceDetail + "\n for card type" + cardType);
//            }
//        }
//
//        success = doGenerate ? super.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper) : success;
//
//        LOG.info("processGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - end");
//        return success;
//    }
    
    /**
     * change to generate the GLPE on ACTUAL EXPENSE based on Reimbursable minus Advance and its only ONE entry to
     * DV clearning account
     * 
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#generateDocumentGeneralLedgerPendingEntries
     * (org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - start");

        boolean success = true;
        boolean doGenerate = true;
        
        success = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        
        KualiDecimal reimbursableToTraveler = getTravelReimbursementService().getReimbursableToTraveler(this);
        
        //if there is any at all reimbursable to the traveler, there will be a DV generated later, so we need to funnel through
        // the travel clearing
        if (reimbursableToTraveler.isPositive()){
            //get a source accounting line for DV clearing details
            SourceAccountingLine sourceDetail = getTravelDisbursementService().getTravelClearingGLPESourceDetail();
            sourceDetail.setAmount(reimbursableToTraveler);
            TemSourceAccountingLine temSouceDetail = new TemSourceAccountingLine();
            BeanUtils.copyProperties(sourceDetail, temSouceDetail);
            
            success &= super.generateGeneralLedgerPendingEntries(temSouceDetail, sequenceHelper);
        }
        
        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - end");
        
        return success;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReimbursableTotal()
     */
    @Override
    public KualiDecimal getReimbursableTotal() {
        KualiDecimal eligible = getEligibleAmount();
        final KualiDecimal expenseLimit = getExpenseLimit();
        
        if (expenseLimit != null && expenseLimit.doubleValue() > 0) {
            return eligible.isGreaterThan(expenseLimit) ? expenseLimit : eligible;
        }
        
        return eligible;
    }
    
    /**
     * Get eligible amount
     * 
     * @return
     */
    public KualiDecimal getEligibleAmount(){
        return getApprovedAmount().subtract(getCTSTotal()).subtract(getCorporateCardTotal());
    }
    
    /**
     * Total amount to be paid to Vendor
     * 
     * @return
     */
    public KualiDecimal getTotalPaidAmountToVendor() {
        KualiDecimal totalPaidAmountToVendor = KualiDecimal.ZERO;
        List<Document> relatedDisbursementList = getTravelDocumentService().getDocumentsRelatedTo(this, DISBURSEMENT_VOUCHER_DOCTYPE);
        for (Document document : relatedDisbursementList) {
            if (document instanceof DisbursementVoucherDocument) {
                DisbursementVoucherDocument dv = (DisbursementVoucherDocument) document;
                if (dv.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                    totalPaidAmountToVendor = totalPaidAmountToVendor.add(dv.getDisbVchrCheckTotalAmount());
                }
            }
        }
        return totalPaidAmountToVendor;
    }

    /**
     * Total requested to be paid
     * 
     * @return
     */
    public KualiDecimal getTotalPaidAmountToRequests() {
        KualiDecimal totalPaidAmountToRequests = KualiDecimal.ZERO;
            
        List<Document> relatedRequisitionDocuments = getTravelDocumentService().getDocumentsRelatedTo(this, 
                TemConstants.REQUISITION_DOCTYPE);
        
        try {
            for (Document document : relatedRequisitionDocuments) {
                PurApRelatedViews relatedviews = ((RequisitionDocument) document).getRelatedViews();
                if (relatedviews != null && relatedviews.getRelatedPaymentRequestViews() != null && relatedviews.getRelatedPaymentRequestViews().size() > 0) {
                    List<PaymentRequestView> preqViews = relatedviews.getRelatedPaymentRequestViews();
                    for (PaymentRequestView preqView : preqViews) {
                        PaymentRequestDocument preqDocument;
                        
                        preqDocument = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(preqView.getDocumentNumber());
                        if (preqDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                            totalPaidAmountToRequests = totalPaidAmountToRequests.add(preqDocument.getVendorInvoiceAmount());
                        }
                    }
                }
            }
        } catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return totalPaidAmountToRequests;
    }

    public KualiDecimal getReimbursableGrandTotal() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;
        grandTotal = getApprovedAmount().add(getTotalPaidAmountToVendor()).add(getTotalPaidAmountToRequests());
               
        if (KualiDecimal.ZERO.isGreaterThan(grandTotal)) {
            return KualiDecimal.ZERO;
        }
        
        return grandTotal;
    }   
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPerDiemAdjustment()
     */
    @Override
    public KualiDecimal getPerDiemAdjustment() {
        //do not use per diem adjustment
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPerDiemAdjustment(org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void setPerDiemAdjustment(KualiDecimal perDiemAdjustment) {
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getExpenseTypeCode()
     */
    @Override
    public String getExpenseTypeCode() {
        return TemConstants.ACTUAL_EXPENSE;
    }
}
