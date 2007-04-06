/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase {

/**
     * Tabs included on Payment Request Documents are:
     *   Invoice
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processInvoiceValidation((PaymentRequestDocument)purapDocument);
        valid &= processPurchaseOrderIDValidation((PaymentRequestDocument)purapDocument);
        return valid;
    }

//  TODO should we call our validation here?
//  @Override
//  protected boolean processCustomSaveDocumentBusinessRules(Document document) {
//      boolean isValid = true;
//      PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
//      return isValid &= processValidation(purapDocument);
//  }

   
          @Override
          protected boolean processCustomSaveDocumentBusinessRules(Document document) {
              boolean isValid = true;
              PaymentRequestDocument  paymentRequestDocument = (PaymentRequestDocument) document;
              return isValid &= processValidation(paymentRequestDocument);
          }

      
/**
     * This method performs any validation for the Invoice tab.
     * 
     * @param preqDocument
     * @return
     */
    public boolean processInvoiceValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        //TODO code validation here
        return valid;
    }
    
    boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {
       
        boolean valid = true;
       
        Integer POID = document.getPurchaseOrderIdentifier();
       
       // I think only the current PO can have the pending action indicator to be "Y". For all the other POs with the same PO number the pending indicator should be always "N". So I think we only need to check if for the current PO the Pending indicator is "Y" and it is not a Retransmit doc, then we don't allow users to create a PREQ. Correct? 
      //given a PO number the use enters in the Init screen, for the rule "Error if the PO is not open" we also only need to check this rule against the current PO, Correct? 
       PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
       if (ObjectUtils.isNull(purchaseOrderDocument)) {
            //GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
       } else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(),PurapConstants.PurchaseOrderStatuses.OPEN)){
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it:
      // } else if (purchaseOrderDocument.isPendingActionIndicator() & !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
      //      GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
       //     valid &= false;
       }
       
         
        return valid;
    }
    
    
    boolean processPaymentRequestDuplicateValidation(PaymentRequestDocument document){  
        
        boolean valid = true;
        //  check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        Integer POID = document.getPurchaseOrderIdentifier();
        List<PaymentRequestDocument> preqs = SpringServiceLocator.getPaymentRequestService().getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(POID, document.getVendorInvoiceAmount(), document.getInvoiceDate());
        
        if (preqs.size() > 0) {
         // boolean addedError = false;
          List cancelled = new ArrayList();
          List voided = new ArrayList();
          for (Iterator iter = preqs.iterator(); iter.hasNext();) {
            PaymentRequestDocument testPREQ = (PaymentRequestDocument) iter.next();
            if ( (!(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode()))) && 
                 (!(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode()))) ) {
                 GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT);
              //addedError = true;
                valid &= false;
              break;
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode())) {
              voided.add(testPREQ);
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode())) {
              cancelled.add(testPREQ);
            }
          }
          // custom error message for duplicates related to cancelled/voided PREQs
         //if (!addedError) {
          if (valid) {
            if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_CANCELLEDORVOIDED);
              valid &= false;
            } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
              //messages.add("errors.duplicate.invoice.date.amount.voided");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_VOIDED);
              valid &= false;
            } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelled");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_CANCELLED);
              valid &= false;
            }
          }
        }
        return valid;
    } 

}
