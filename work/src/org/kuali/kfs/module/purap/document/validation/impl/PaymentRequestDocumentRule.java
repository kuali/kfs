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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.ContinueAccountsPayableRule;

public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase implements ContinueAccountsPayableRule {

    private static KualiDecimal zero = new KualiDecimal(0);

    /**
     * Tabs included on Payment Request Documents are: Invoice
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processInvoiceValidation((PaymentRequestDocument) purapDocument);
        valid &= processPurchaseOrderIDValidation((PaymentRequestDocument) purapDocument);
        valid &= processPaymentRequestDateValidation((PaymentRequestDocument) purapDocument);
        return valid;
    }

    public boolean processContinueAccountsPayableBusinessRules(AccountsPayableDocument apDocument) {
        boolean valid = true;
        valid &= processInvoiceValidation((PaymentRequestDocument) apDocument);
        valid &= processPurchaseOrderIDValidation((PaymentRequestDocument) apDocument);
        valid &= processPaymentRequestDateValidation((PaymentRequestDocument) apDocument);
        return valid;
    }
    
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
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
        // TODO code validation here
        return valid;
    }

    boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {

        boolean valid = true;

        Integer POID = document.getPurchaseOrderIdentifier();

        // I think only the current PO can have the pending action indicator to be "Y". For all the other POs with the same PO
        // number the pending indicator
        // should be always "N". So I think we only need to check if for the current PO the Pending indicator is "Y" and it is not a
        // Retransmit doc, then
        // we don't allow users to create a PREQ. Correct?
        // given a PO number the use enters in the Init screen, for the rule "Error if the PO is not open" we also only need to
        // check this rule against the current PO, Correct?
        PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
        if (ObjectUtils.isNull(purchaseOrderDocument)) {

            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        }
        else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it:
            // 2007-04-19 15:50:40,750 [http-8080-Processor23] ERROR
            // org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/kuali-dev].[action] :: Servlet.service() for servlet
            // action threw exception
            // java.lang.RuntimeException: transient FlexDoc is null - this should never happen
            // java.lang.RuntimeException: transient FlexDoc is null - this should never happen
            // org.kuali.core.bo.DocumentHeader.getWorkflowDocument(DocumentHeader.java:67)
            // } else if (purchaseOrderDocument.isPendingActionIndicator() &
            // !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(),
            // PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
            // GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER,
            // PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        // }
        // else if (purchaseOrderDocument.isPendingActionIndicator() &
        // !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(),
        // PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
        // GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER,
        // PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
            // valid &= false;
        }

        // Verify that there exists at least 1 item left to be invoiced
        boolean zeroDollar = true;
        for (Iterator itemIter = purchaseOrderDocument.getItems().iterator(); itemIter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) itemIter.next();
            KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? zero : poi.getItemOutstandingEncumberedQuantity();
            if (encumberedQuantity.compareTo(zero) == 1) {
                zeroDollar = false;
                break;
            }
        }
        if (zeroDollar) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_NO_ITEMS_TO_INVOICE);
            valid &= false;
        }

        return valid;
    }

    boolean processPaymentRequestDateValidation(PaymentRequestDocument document) {

        boolean valid = true;
        if (SpringServiceLocator.getPaymentRequestService().isInvoiceDateAfterToday(document.getInvoiceDate())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE);
            valid &= false;
        }
        return valid;
    }
   
    /**
     * This method performs item validations for the rules that are only applicable to Payment Request Document.
     * 
     * @param purapDocument
     * @return
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);

        for (Iterator itemIter = purapDocument.getItems().iterator(); itemIter.hasNext(); ) { 
            PaymentRequestItem item = (PaymentRequestItem)itemIter.next(); 
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription()); 
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) { 
                valid &= validateItemTypeItems((PaymentRequestItem) item, identifierString); 
            } 
        }
        return valid;
    }

    private boolean validateItemTypeItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        // Currently Quantity is allowed to be NULL on screen;
        // must be either a positive number or NULL for DB
        if (ObjectUtils.isNotNull(item.getItemQuantity())) {
            if (item.getItemQuantity().isNegative()) {
                // if quantity is negative give an error
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.INVOICE_QUANTITY, identifierString);
            }
            if (item.getPoOutstandingQuantity().isLessThan(item.getItemQuantity())) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_QUANTITY_TOO_MANY, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }
        if (item.getExtendedPrice().isPositive() && ObjectUtils.isNotNull(item.getPoOutstandingQuantity()) && item.getPoOutstandingQuantity().isPositive()) {

            // here we must require the user to enter some value for quantity if they want a credit amount associated
            if (ObjectUtils.isNull(item.getItemQuantity()) || item.getItemQuantity().isZero()) {
                // here we have a user not entering a quantity with an extended amount but the PO has a quantity... require user to
                // enter a quantity
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_QUANTITY_REQUIRED, ItemFields.INVOICE_QUANTITY, identifierString, ItemFields.OPEN_QUANTITY);
            }
        }

        // TODO: Is the following comment right ? It was copied directly from EPIC, but doesn't look right (service items ???).
        // check that service items (no quantity on PO item) are not trying to pay on a zero encumbrance amount (check only prior to
        // ap approval)
        if ( (ObjectUtils.isNull(item.getPaymentRequest().getPurapDocumentIdentifier())) || 
             (PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(item.getPaymentRequest().getStatusCode())) ) {
            if ( (ObjectUtils.isNull(item.getPoOutstandingQuantity()) || item.getPoOutstandingQuantity().isZero()) && 
                 ((item.getExtendedPrice() != null) && item.getExtendedPrice().isNonZero()) ) {
                if (item.getPoOutstandingAmount() == null || item.getPoOutstandingAmount().isZero()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_AMOUNT_ALREADY_PAID, identifierString);
                }
            }
        }

        return valid;
    }
    
    private boolean validateNonItemTypeItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;
        return valid;
    }

}



/**
 * @param purchaseOrderId
 * @param invoiceNumber
 * @param invoiceAmount
 * @param invoiceDate
 * @param expiredAccounts
 * @param closedAccounts
 * @return
 */
/*
public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
    Integer purchaseOrderId, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
    List expiredAccounts, List closedAccounts, User u) {
  SERVICELOG.debug("verifyPreqInitialization() started");
    LOG.debug("verifyPreqInitialization started");
  List messages = new ArrayList();
  List expirAcctList = new ArrayList();
  List closeAcctList = new ArrayList();
  
  PaymentRequestInitializationValidationErrors initValidationErrors = new PaymentRequestInitializationValidationErrors();

  PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(purchaseOrderId,u);
  
  if (po == null) {
    // no PO was found in the system - notify the user
    messages.add("errors.po.not.exist");
    initValidationErrors.errorMessages = messages;
    SERVICELOG.debug("verifyPreqInitialization() ended");    
    return initValidationErrors;
  }
  
  // Verify that there exists at least 1 item left to be invoiced
  initValidationErrors.setPurchaseOrderNumberToUse(purchaseOrderId);
  boolean zeroDollar = true;
  for (Iterator itemIter = po.getItems().iterator(); itemIter.hasNext();) {
    PurchaseOrderItem poi = (PurchaseOrderItem) itemIter.next();
    BigDecimal encumberedQuantity = poi.getOutstandingEncumberedQuantity() == null ? zero : poi.getOutstandingEncumberedQuantity();
    if (encumberedQuantity.compareTo(zero) == 1) {
      zeroDollar = false;
      break;
    }
  }
  
  // if messages exist now there is no need to check anything else
  if (!messages.isEmpty()) {
    initValidationErrors.errorMessages = messages;
    SERVICELOG.debug("verifyPreqInitialization() ended");
    return initValidationErrors;
  }
  
  // Check invoice date to make sure it is today or before
  if (this.isInvoiceDateAfterToday(invoiceDate)) {
    messages.add("errors.invalid.invoice.date");
  }
//  Timestamp now = new Timestamp( (new Date()).getTime() );
//  if ( invoiceDate.getTime() > now.getTime() ) {
//    messages.add("errors.invalid.invoice.date");
//  }
  
  if (EpicConstants.PO_STAT_OPEN.equals(po.getPurchaseOrderStatus().getCode())) {
    //then check that there are no other non-cancelled PREQs for this vendor number and invoice number
    Integer vendorDetailAssignedId = po.getVendorDetailAssignedId();
    Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedId();

    List preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId,vendorDetailAssignedId,invoiceNumber);
    if (preqs.size() > 0) {
      boolean addedError = false;
      List cancelled = new ArrayList();
      List voided = new ArrayList();
      for (Iterator iter = preqs.iterator(); iter.hasNext();) {
        PaymentRequest testPREQ = (PaymentRequest) iter.next();
        if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
             (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
          messages.add("errors.duplicate.vendor.invoice");
          addedError = true;
          break;
        } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
          voided.add(testPREQ);
        } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
          cancelled.add(testPREQ);
        }
      }
      // custom error message for duplicates related to cancelled/voided PREQs
      if (!addedError) {
        if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
          messages.add("errors.duplicate.vendor.invoice.cancelledOrVoided");
        } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
          messages.add("errors.duplicate.vendor.invoice.voided");
        } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
          messages.add("errors.duplicate.vendor.invoice.cancelled");
        }
      }
    }
    
    //check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
    preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(po.getId(), invoiceAmount, invoiceDate);
    if (preqs.size() > 0) {
      boolean addedError = false;
      List cancelled = new ArrayList();
      List voided = new ArrayList();
      for (Iterator iter = preqs.iterator(); iter.hasNext();) {
        PaymentRequest testPREQ = (PaymentRequest) iter.next();
        if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
             (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
          messages.add("errors.duplicate.invoice.date.amount");
          addedError = true;
          break;
        } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
          voided.add(testPREQ);
        } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
          cancelled.add(testPREQ);
        }
      }
      // custom error message for duplicates related to cancelled/voided PREQs
      if (!addedError) {
        if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
          messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
        } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
          messages.add("errors.duplicate.invoice.date.amount.voided");
        } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
          messages.add("errors.duplicate.invoice.date.amount.cancelled");
        }
      }
    }
    
    this.checkForExpiredOrClosedAccounts(po, initValidationErrors, closedAccounts, expiredAccounts);
    
  } else if (EpicConstants.PO_STAT_PAYMENT_HOLD.equals(po.getPurchaseOrderStatus().getCode())) {
    //PO is not open - notify the user
    messages.add("errors.po.status.hold");
  } else {
    //PO is not open - notify the user
    messages.add("errors.po.not.open");
  }

  if ( 1 == 2 ) {
    // TODO 2006: delyea PREQ CLOSE PO: Add code to return encumberances check for auto close of PO
    initValidationErrors.setCanAutoClosePO(false);
    //initValidationErrors.setCanAutoClosePO(generalLedgerService.isPOAutoCloseEligible(prd.getPreq()) && (prd.getPreq().getPurchaseOrder().getRecurringPaymentType() == null));
  }

  initValidationErrors.errorMessages = messages;
  SERVICELOG.debug("verifyPreqInitialization() ended");    
  return initValidationErrors;
}
*/
