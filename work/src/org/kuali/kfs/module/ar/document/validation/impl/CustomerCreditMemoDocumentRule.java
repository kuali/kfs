/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDetailRule;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;

/**
 * This class holds the business rules for the AR Credit Memo Document
 */
public class CustomerCreditMemoDocumentRule extends AccountingDocumentRuleBase implements RecalculateCustomerCreditMemoDetailRule<AccountingDocument>{
  
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)document;
        isValid = checkReferenceInvoiceNumber(cmDocument);
           
        
        return isValid;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)document;
        if (isValid) {
            
        }
        return isValid;
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)approveEvent.getDocument();
        if (isValid) {
            
        }
        return isValid;
    }
      
    private boolean checkReferenceInvoiceNumber(CustomerCreditMemoDocument document) {
        boolean isValid = false;
        return isValid;
        
    }
    
    /**
     * @see org.kuali.module.ar.rule.RecalculateCustomerInvoiceDetailRule#processRecalculateCustomerInvoiceDetailRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public boolean processRecalculateCustomerCreditMemoDetailRules(AccountingDocument financialDocument, CustomerCreditMemoDetail customerCreditMemoDetail) {
        boolean success = true;
 
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)financialDocument;
        String inputKey = isQtyOrItemAmountEntered(customerCreditMemoDetail);
        
        // 'Qty' was entered
        if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY,inputKey)) {
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemQuantity());
            // have to change this rule taking into account discounts
            success &= isCustomerCreditMemoQtyGreaterThanInvoiceQty(customerCreditMemoDetail,customerCreditMemoDocument);
        }
        // 'Item Amount' was entered
        else if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT,inputKey)) { 
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemTotalAmount());
            success &= isCustomerCreditMemoItemAmountGreaterThanInvoiceItemAmount(customerCreditMemoDetail,customerCreditMemoDocument);
        }
//      if there is no input or if both 'Qty' and 'Item Amount' were entered -> wrong input
        else
            success = false;
        
        return success;
    }
    
    private String isQtyOrItemAmountEntered(CustomerCreditMemoDetail customerCreditMemoDetail) {

            BigDecimal customerCreditMemoItemQty = customerCreditMemoDetail.getCreditMemoItemQuantity();
            KualiDecimal customerCreditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            String inputKey = "";
           
            if (ObjectUtils.isNotNull(customerCreditMemoItemQty) && ObjectUtils.isNotNull(customerCreditMemoItemAmount)) {
                inputKey = ArConstants.CustomerCreditMemoConstants.BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
            } else if (ObjectUtils.isNotNull(customerCreditMemoItemQty))
                inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY;
            else if (ObjectUtils.isNotNull(customerCreditMemoItemAmount))
                inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT;
            
            return inputKey;
    }
    
    private boolean isValueGreaterThanZero(BigDecimal value) {
        boolean validValue = (value.compareTo(BigDecimal.ZERO) == 1 ?true:false);
        if (!validValue)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO); 
        return validValue;
    }
    
    private boolean isValueGreaterThanZero(KualiDecimal value) {
        boolean validValue = value.isPositive();
        if (!validValue)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO); 
        return validValue;
    }
    
    // have to validate against open invoice amount -> have to change
    private boolean isCustomerCreditMemoItemAmountGreaterThanInvoiceItemAmount(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerCreditMemoDocument customerCreditMemoDocument){
        int lineNumber = customerCreditMemoDetail.getReferenceInvoiceItemNumber().intValue();
        KualiDecimal invoiceOpenAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount();
        KualiDecimal creditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        
        boolean validItemAmount = creditMemoItemAmount.isLessEqual(invoiceOpenAmount);
        if (!validItemAmount)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_GREATER_THAN_INVOICE_ITEM_AMOUNT);
            
        return validItemAmount;
    }
    
    //TODO: I have to take into account applied discount ???
    private boolean isCustomerCreditMemoQtyGreaterThanInvoiceQty(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerCreditMemoDocument customerCreditMemoDocument) {
        Integer refInvoiceItemNumber = customerCreditMemoDetail.getReferenceInvoiceItemNumber();
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetail(invDocumentNumber,refInvoiceItemNumber);

        BigDecimal customerInvoiceItemQty = customerInvoiceDetail.getInvoiceItemQuantity();
        BigDecimal customerCreditMemoItemQty = customerCreditMemoDetail.getCreditMemoItemQuantity();
        
        // customer credit memo quantity must not be greater than parent quantity
        boolean validQuantity = (customerCreditMemoItemQty.compareTo(customerInvoiceItemQty) < 1?true:false);
        if (!validQuantity)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY);
        
        return validQuantity; 
    }
    
}
