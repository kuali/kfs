/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.ContinueCustomerCreditMemoDocumentRule;
import org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDetailRule;
import org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDocumentRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class holds the business rules for the AR Credit Memo Document
 */

public class CustomerCreditMemoDocumentRule extends TransactionalDocumentRuleBase implements RecalculateCustomerCreditMemoDetailRule<TransactionalDocument>, RecalculateCustomerCreditMemoDocumentRule<TransactionalDocument>, ContinueCustomerCreditMemoDocumentRule<TransactionalDocument> {

    protected static final BigDecimal ALLOWED_QTY_DEVIATION = new BigDecimal("0.10");

    public CustomerCreditMemoDocumentRule() {
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        GlobalVariables.getMessageMap().addToErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);
        isValid &= processRecalculateCustomerCreditMemoDocumentRules((TransactionalDocument) document, true);
        GlobalVariables.getMessageMap().removeFromErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDetailRule#processRecalculateCustomerCreditMemoDetailRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail)
     */
    @Override
    public boolean processRecalculateCustomerCreditMemoDetailRules(TransactionalDocument document, CustomerCreditMemoDetail customerCreditMemoDetail) {
        boolean success = true;

        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        customerCreditMemoDocument.refreshReferenceObject("invoice");
        String inputKey = isQtyOrItemAmountEntered(customerCreditMemoDetail);

        // refresh InvoiceOpenItemAmount and InvoiceOpenAmountQuantity if changed by any other transaction
        customerCreditMemoDetail.setInvoiceOpenItemAmount(customerCreditMemoDetail.getCustomerInvoiceDetail().getAmountOpen());
        customerCreditMemoDetail.setInvoiceOpenItemQuantity(customerCreditMemoDocument.getInvoiceOpenItemQuantity(customerCreditMemoDetail, customerCreditMemoDetail.getCustomerInvoiceDetail()));

        // 'Qty' was entered
        if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY, inputKey)) {
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemQuantity());
            success &= isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(customerCreditMemoDetail);
        }
        // 'Item Amount' was entered
        else if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT, inputKey)) {
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemTotalAmount());
            success &= isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(customerCreditMemoDocument, customerCreditMemoDetail);
        }
        // both 'Qty' and 'Item Amount' were entered -> validate
        else if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED, inputKey)) {
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemTotalAmount());
            success &= isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(customerCreditMemoDocument, customerCreditMemoDetail);
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemQuantity());
            success &= isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(customerCreditMemoDetail);
            success &= checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(customerCreditMemoDetail, customerCreditMemoDetail.getCustomerInvoiceDetail().getInvoiceItemUnitPrice());
        }
        // if there is no input -> wrong input
        else {
            success = false;
        }
        return success;
    }

    public String isQtyOrItemAmountEntered(CustomerCreditMemoDetail customerCreditMemoDetail) {

        BigDecimal customerCreditMemoItemQty = customerCreditMemoDetail.getCreditMemoItemQuantity();
        KualiDecimal customerCreditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        String inputKey = "";

        if (ObjectUtils.isNotNull(customerCreditMemoItemQty) && ObjectUtils.isNotNull(customerCreditMemoItemAmount)) {
            inputKey = ArConstants.CustomerCreditMemoConstants.BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED;
        }
        else if (ObjectUtils.isNotNull(customerCreditMemoItemQty)) {
            inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY;
        }
        else if (ObjectUtils.isNotNull(customerCreditMemoItemAmount)) {
            inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT;
        }

        return inputKey;
    }

    public boolean isValueGreaterThanZero(BigDecimal value) {
        boolean validValue = (value.compareTo(BigDecimal.ZERO) == 1 ? true : false);
        if (!validValue) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO);
        }
        return validValue;
    }

    public boolean isValueGreaterThanZero(KualiDecimal value) {
        boolean validValue = value.isPositive();
        if (!validValue) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
        }
        return validValue;
    }

    public boolean isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(CustomerCreditMemoDocument customerCreditMemoDocument, CustomerCreditMemoDetail customerCreditMemoDetail) {

        KualiDecimal invoiceOpenItemAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount();
        KualiDecimal creditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();

        boolean validItemAmount = creditMemoItemAmount.isLessEqual(invoiceOpenItemAmount);
        if (!validItemAmount) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_GREATER_THAN_INVOICE_ITEM_AMOUNT);
        }

        return validItemAmount;
    }

    public boolean isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal invoiceOpenItemQty = customerCreditMemoDetail.getInvoiceOpenItemQuantity();
        KualiDecimal customerCreditMemoItemQty = new KualiDecimal(customerCreditMemoDetail.getCreditMemoItemQuantity());

        // customer credit memo quantity must not be greater than invoice open item quantity
        boolean validQuantity = (customerCreditMemoItemQty.compareTo(invoiceOpenItemQty) < 1 ? true : false);
        if (!validQuantity) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY);
        }

        return validQuantity;
    }

    public boolean checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(CustomerCreditMemoDetail customerCreditMemoDetail, BigDecimal unitPrice) {
        KualiDecimal creditAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        BigDecimal creditQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();

        // if unit price is zero, leave this validation, as it will cause an exception below by attempting to divide by zero
        if (unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            // no need to report error, because it is already recorded by another validation check.
            return false;
        }

        // determine the expected exact total credit memo quantity, based on actual credit amount entered
        BigDecimal expectedCreditQuantity = creditAmount.bigDecimalValue().divide(unitPrice, ArConstants.ITEM_QUANTITY_SCALE, BigDecimal.ROUND_HALF_UP);

        // return false if the expected quantity is 0 while the actual quantity is not
        if (expectedCreditQuantity.compareTo(BigDecimal.ZERO) == 0 && creditQuantity.compareTo(BigDecimal.ZERO) != 0) {
            return false;
        }

        // determine the deviation percentage that the actual creditQuantity has from expectedCreditQuantity
        BigDecimal deviationPercentage = creditQuantity.subtract(expectedCreditQuantity).divide(expectedCreditQuantity, ArConstants.ITEM_QUANTITY_SCALE, BigDecimal.ROUND_HALF_UP).abs();

        // only allow a certain deviation of creditQuantity from the expectedCreditQuantity
        boolean validFlag = deviationPercentage.compareTo(ALLOWED_QTY_DEVIATION) < 1;

        if (!validFlag) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
        }
        return validFlag;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDocumentRule#processRecalculateCustomerCreditMemoDocumentRules(org.kuali.kfs.sys.document.AccountingDocument)
     */
    @Override
    public boolean processRecalculateCustomerCreditMemoDocumentRules(TransactionalDocument document, boolean printErrMsgFlag) {
        boolean success = true;
        boolean crmDataEnteredFlag = false;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        int i = 0;
        String propertyName;

        for (CustomerCreditMemoDetail customerCreditMemoDetail : customerCreditMemoDetails) {
            propertyName = KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(propertyName);

            // validate only if there is input data
            if (!isQtyOrItemAmountEntered(customerCreditMemoDetail).equals(StringUtils.EMPTY)) {
                crmDataEnteredFlag = true;
                success &= processRecalculateCustomerCreditMemoDetailRules(customerCreditMemoDocument, customerCreditMemoDetail);
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(propertyName);
            i++;
        }

        success &= crmDataEnteredFlag;

        // print error message if 'Submit'/'Save'/'Blanket Approved' button is pressed and there is no CRM data entered
        if (!crmDataEnteredFlag && printErrMsgFlag) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_NO_DATA_TO_SUBMIT);
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.ContinueCustomerCreditMemoDocumentRule#processContinueCustomerCreditMemoDocumentRules(org.kuali.kfs.sys.document.AccountingDocument)
     */
    @Override
    public boolean processContinueCustomerCreditMemoDocumentRules(TransactionalDocument document) {
        boolean success;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;

        success = checkIfInvoiceNumberIsFinal(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        if (success) {
            success = checkIfThereIsNoAnotherCRMInRouteForTheInvoice(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        }
        if (success) {
            success = checkInvoiceForErrorCorrection(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        }

        return success;
    }

    public boolean checkIfInvoiceNumberIsFinal(String invDocumentNumber) {
        boolean success = true;

        if (StringUtils.isBlank(invDocumentNumber)) {
            success &= false;
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED);
        }
        else {
            CustomerInvoiceDocumentService service = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument customerInvoiceDocument = service.getInvoiceByInvoiceDocumentNumber(invDocumentNumber);
            if (ObjectUtils.isNull(customerInvoiceDocument)) {
                success &= false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER);
            }
            else if (!SpringContext.getBean(CustomerInvoiceDocumentService.class).checkIfInvoiceNumberIsFinal(invDocumentNumber)) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_NOT_FINAL);
                success &= false;
            }
        }
        return success;
    }

    /**
     * This method checks if there is no another CRM in route for the invoice not in route if CRM status is one of the following:
     * processed, cancelled, or disapproved
     *
     * @param invoice
     * @return
     */
    public boolean checkIfThereIsNoAnotherCRMInRouteForTheInvoice(String invoiceDocumentNumber) {

        WorkflowDocument workflowDocument;
        boolean success = true;

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceDocumentNumber);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Collection<CustomerCreditMemoDocument> customerCreditMemoDocuments = businessObjectService.findMatching(CustomerCreditMemoDocument.class, fieldValues);

        // no CRMs associated with the invoice are found
        if (customerCreditMemoDocuments.isEmpty()) {
            return success;
        }

        String userId = GlobalVariables.getUserSession().getPrincipalId();
        for(CustomerCreditMemoDocument customerCreditMemoDocument : customerCreditMemoDocuments) {
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, customerCreditMemoDocument.getDocumentNumber());
            if (!(workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE);
                success = false;
                break;
            }
        }
        return success;
    }

    /**
     * This method checks if the Invoice has been error corrected or is an error correcting invoice
     *
     * @param invoice
     * @return
     */
    public boolean checkInvoiceForErrorCorrection(String invoiceDocumentNumber) {
        CustomerInvoiceDocumentService service = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        CustomerInvoiceDocument customerInvoiceDocument = service.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);

        DocumentHeader documentHeader = SpringContext.getBean(FinancialSystemDocumentHeaderDao.class).getCorrectingDocumentHeader(invoiceDocumentNumber);

        // invoice has been corrected
        if (ObjectUtils.isNotNull(documentHeader)) {
            if (StringUtils.isNotBlank(documentHeader.getDocumentNumber())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_CORRECTED_INVOICE);
                return false;
            }
        }
        // this is a correcting invoice
        if (customerInvoiceDocument.isInvoiceReversal()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_CORRECTING_INVOICE);
            return false;
        }
        return true;
    }

    @Override
    public boolean isDocumentAttributesValid(Document document, boolean validateRequired) {
        //refresh GLPE nonupdateable business object references....
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;

        for (CustomerCreditMemoDetail customerDetail : customerCreditMemoDocument.getCreditMemoDetails()) {
            customerDetail.getCustomerInvoiceDetail().refreshNonUpdateableReferences();
        }

        for (GeneralLedgerPendingEntry glpe : customerCreditMemoDocument.getGeneralLedgerPendingEntries()) {
            glpe.refreshNonUpdateableReferences();
        }

        return super.isDocumentAttributesValid(document, validateRequired);
    }

}
