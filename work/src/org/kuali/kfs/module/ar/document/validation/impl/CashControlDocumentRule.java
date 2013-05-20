/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.PaymentMedium;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.authorization.CashControlDocumentPresentationController;
import org.kuali.kfs.module.ar.document.validation.AddCashControlDetailRule;
import org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule;
import org.kuali.kfs.module.ar.document.validation.GenerateReferenceDocumentRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * This class holds the business rules for the AR Cash Control Document
 */
public class CashControlDocumentRule extends TransactionalDocumentRuleBase implements AddCashControlDetailRule<CashControlDocument>, DeleteCashControlDetailRule<CashControlDocument>, GenerateReferenceDocumentRule<CashControlDocument> {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentRule.class);

    /**
     * @see org.kuali.rice.krad.rules.TransactionalDocumentRuleBase#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        ccDocument.refreshReferenceObject("customerPaymentMedium");
        ccDocument.refreshReferenceObject("generalLedgerPendingEntries");

        MessageMap errorMap = GlobalVariables.getMessageMap();

        if (errorMap.hasErrors()) {
            isValid &= checkRefDocNumber(ccDocument);
            isValid &= validateCashControlDetails(ccDocument);
        }

        return isValid;

    }

    /**
     * @see org.kuali.rice.krad.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashControlDocument cashControlDocument = (CashControlDocument) document;

        if (isValid) {
            isValid &= checkPaymentMedium(cashControlDocument);
            isValid &= checkRefDocNumber(cashControlDocument);
            isValid &= validateBankCode(cashControlDocument);
            isValid &= validateCashControlDetails(cashControlDocument);
            isValid &= checkCashControlDocumentHasDetails(cashControlDocument);
        }

        return isValid;

    }

    /**
     * @see org.kuali.rice.krad.rules.TransactionalDocumentRuleBase#processCustomApproveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {

        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CashControlDocument cashControlDocument = (CashControlDocument) approveEvent.getDocument();

        cashControlDocument.refreshReferenceObject("customerPaymentMedium");
        cashControlDocument.refreshReferenceObject("generalLedgerPendingEntries");

        isValid &= checkAllAppDocsApproved(cashControlDocument);
        isValid &= checkGLPEsCreated(cashControlDocument);

        return isValid;

    }

    /**
     * This method checks the CashControlDetail line amount is not zero or negative.
     * 
     * @param document the CashControldocument
     * @param detail the CashControlDetail
     * @return true is amount is valid, false otherwise
     */
    public boolean checkLineAmount(CashControlDocument document, CashControlDetail detail) {

        boolean isValid = true;

        // line amount cannot be zero
        if (detail.getFinancialDocumentLineAmount().isZero()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArKeyConstants.ERROR_LINE_AMOUNT_CANNOT_BE_ZERO);
            isValid = false;
        }
        // line amount cannot be negative
        if (detail.getFinancialDocumentLineAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArKeyConstants.ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE);
            isValid = false;
        }
        return isValid;

    }

    /**
     * This method checks if the CashControlDocument has any details to be processed.
     * 
     * @param cashControlDocument the CashControlDocument
     * @return true if it has details, false otherwise
     */
    public boolean checkCashControlDocumentHasDetails(CashControlDocument cashControlDocument) {

        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        if (cashControlDocument.getCashControlDetails().isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CASH_CONTROL_DETAILS, ArKeyConstants.ERROR_NO_LINES_TO_PROCESS);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;

    }

    /**
     * This method checks that payment medium has a valid value
     * 
     * @param document
     * @return true if valid, false otherwise
     */
    public boolean checkPaymentMedium(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMediumCode = document.getCustomerPaymentMediumCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("customerPaymentMediumCode", paymentMediumCode);

        PaymentMedium paymentMedium = (PaymentMedium) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentMedium.class, criteria);

        if (paymentMedium == null) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.CUSTOMER_PAYMENT_MEDIUM_CODE, ArKeyConstants.ERROR_PAYMENT_MEDIUM_IS_NOT_VALID);
            isValid = false;
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that reference document number is not null when payment medium is Cash.
     * 
     * @param document CashControlDocument
     * @return true if valid, false otherwise
     */
    public boolean checkRefDocNumber(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMedium = document.getCustomerPaymentMediumCode();
        if (ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(paymentMedium)) {
            String refDocNumber = document.getReferenceFinancialDocumentNumber();
            try {
                Long.parseLong(refDocNumber);
                if (StringUtils.isBlank(refDocNumber)) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                    isValid = false;
                }
                else {
                    boolean docExists = SpringContext.getBean(DocumentService.class).documentExists(refDocNumber);
                    if (!docExists) {
                        GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                        isValid = false;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }

        }
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that the GLPEs have been created
     * 
     * @param document CashControlDocument
     * @return true if not null, false otherwise
     */
    public boolean checkGLPEsCreated(CashControlDocument cashControlDocument) {

        boolean isValid = true;
        List<GeneralLedgerPendingEntry> glpes = cashControlDocument.getGeneralLedgerPendingEntries();

        Integer totalGLRecordsCreated = 0;

        if (glpes == null || glpes.isEmpty()) {
            totalGLRecordsCreated = cashControlDocument.getGeneralLedgerEntriesPostedCount();
        }

        // if payment medium is not Cash the general ledger pending entries must not be empty; if payment medium is Cash then a Cash
        // Receipt Document must be created prior to creating the Cash Control document and it's number should be set in Reference
        // Document number
        if (!ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode()) && ((glpes == null || glpes.isEmpty()) && totalGLRecordsCreated.intValue() == 0)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArKeyConstants.ERROR_GLPES_NOT_CREATED);
            isValid = false;
        }

        return isValid;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.AddCashControlDetailRule#processAddCashControlDetailBusinessRules(org.kuali.rice.krad.document.TransactionalDocument,
     *      org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    public boolean processAddCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;

        success &= checkGLPEsNotGenerated(transactionalDocument);
        if (success) {
            GlobalVariables.getMessageMap().removeFromErrorPath(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX);
            success &= validateBankCode(transactionalDocument);
            GlobalVariables.getMessageMap().addToErrorPath(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX);

            success &= validateCashControlDetail(transactionalDocument, cashControlDetail);
        }
        return success;

    }

    /**
     * This method validates CashControlDetail
     * 
     * @param document CashControlDocument
     * @param cashControlDetail CashControlDetail
     * @return true if CashControlDetail is valid, false otherwise
     */
    protected boolean validateCashControlDetail(CashControlDocument document, CashControlDetail cashControlDetail) {

        MessageMap errorMap = GlobalVariables.getMessageMap();

        boolean isValid = true;

        int originalErrorCount = errorMap.getErrorCount();
        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(cashControlDetail);
        isValid = (errorMap.getErrorCount() == originalErrorCount);

        // validate customer number and line amount
        if (isValid) {
            String customerNumber = cashControlDetail.getCustomerNumber();
            // if customer number is not empty check that it is valid
            if (customerNumber != null && !customerNumber.equals("")) {
                isValid &= checkCustomerNumber(customerNumber);
            }
            // check if line amount is valid
            isValid &= checkLineAmount(document, cashControlDetail);
        }

        return isValid;

    }

    /**
     * This method validates cash control document's details
     * 
     * @param cashControlDocument CashControldocument
     * @return true if valid, false otherwise
     */
    public boolean validateCashControlDetails(CashControlDocument cashControlDocument) {

        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;

        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {

            CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(i);
            String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(propertyName);

            isValid &= validateCashControlDetail(cashControlDocument, cashControlDetail);

            GlobalVariables.getMessageMap().removeFromErrorPath(propertyName);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that the customer number is valid and not an inactive customer when it is not empty
     * 
     * @param cashControlDetail
     * @return true if valid, false otherwise
     */
    protected boolean checkCustomerNumber(String customerNumber) {
        boolean isValid = true;

        if (customerNumber != null && !customerNumber.equals("")) {

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("customerNumber", customerNumber);

            Customer customer = (Customer) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Customer.class, criteria);

            if (customer == null) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.CUSTOMER_NUMBER, ArKeyConstants.ERROR_CUSTOMER_NUMBER_IS_NOT_VALID);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * This method checks if GLPEs have been already generated
     * 
     * @param cashControlDocument the cash control document
     * @return true if it was not generated, false otherwise
     */
    public boolean checkGLPEsNotGenerated(CashControlDocument cashControlDocument) {

        boolean success = true;
        List<GeneralLedgerPendingEntry> glpes = cashControlDocument.getGeneralLedgerPendingEntries();

        if (glpes != null && !glpes.isEmpty()) {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArKeyConstants.ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_GLPES_GEN);
        }
        return success;

    }

    /**
     * This method checks if all application documents are in approved or in final state
     * 
     * @param cashControlDocument
     * @return true if all application documents approved/final, false otherwise
     */
    public boolean checkAllAppDocsApproved(CashControlDocument cashControlDocument) {

        boolean allAppDocsApproved = true;

        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {

            CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(i);
            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            WorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (!(workflowDocument.isApproved() || workflowDocument.isFinal())) {
                allAppDocsApproved = false;

                String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                GlobalVariables.getMessageMap().addToErrorPath(propertyName);
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.APPLICATION_DOC_STATUS, ArKeyConstants.ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED);
                GlobalVariables.getMessageMap().removeFromErrorPath(propertyName);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

                break;
            }

        }

        return allAppDocsApproved;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule#processDeleteCashControlDetailBusinessRules(org.kuali.rice.krad.document.TransactionalDocument,
     *      org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    public boolean processDeleteCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;
        success &= checkGLPEsNotGenerated(transactionalDocument);
        return success;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.GenerateReferenceDocumentRule#processGenerateReferenceDocumentBusinessRules(org.kuali.rice.krad.document.TransactionalDocument)
     */
    public boolean processGenerateReferenceDocumentBusinessRules(CashControlDocument transactionalDocument) {

        boolean success = true;
        success &= checkPaymentMedium(transactionalDocument);
        if (success) {
            success &= checkGLPEsNotGenerated(transactionalDocument);
        }
        return success;

    }

    /**
     * validate bankCode
     * 
     * @param document
     * @return
     */
    public boolean validateBankCode(CashControlDocument document) {
        boolean isValid = true;

        // if the EDIT_BANK_CODE isnt enabled, then dont bother checking it, return with success
        CashControlDocumentPresentationController ccPC = (CashControlDocumentPresentationController) SpringContext.getBean(DocumentHelperService.class).getDocumentPresentationController(document);
        if (!ccPC.getEditModes(document).contains(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_BANK_CODE)) {
            return isValid;
        }

        // otherwise, make sure it exists and is valid
        String bankCode = document.getBankCode();
        if (StringUtils.isNotBlank(bankCode)) {
            Bank bank = SpringContext.getBean(BankService.class).getByPrimaryId(bankCode);
            if (ObjectUtils.isNull(bank)) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.BANK_CODE, ArKeyConstants.ERROR_INVALID_BANK_CODE);
            }
            else {
                // make sure the bank is eligible for deposit activity
                if (!bank.isBankDepositIndicator()) {
                    isValid = false;
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.BANK_CODE, ArKeyConstants.ERROR_BANK_NOT_ELIGIBLE_FOR_DEPOSIT_ACTIVITY);
                }
            }
        }
        else {
            if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.BANK_CODE, ArKeyConstants.ERROR_BANK_CODE_REQUIRED);
            }
        }

        return isValid;
    }

}
