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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.rule.AddCashControlDetailRule;
import org.kuali.module.ar.rule.DeleteCashControlDetailRule;
import org.kuali.module.ar.rule.GenerateReferenceDocumentRule;
import org.kuali.module.ar.service.OrganizationOptionsService;
import org.kuali.module.ar.service.PaymentMediumService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

/**
 * This class holds the business rules for the AR Cash Control Document
 */
public class CashControlDocumentRule extends TransactionalDocumentRuleBase implements AddCashControlDetailRule<CashControlDocument>, DeleteCashControlDetailRule<CashControlDocument>, GenerateReferenceDocumentRule<CashControlDocument> {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentRule.class);

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return true;

    }

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        // isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return isValid;

    }

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomApproveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {

        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CashControlDocument ccDocument = (CashControlDocument) approveEvent.getDocument();

        isValid &= checkAllAppDocsApproved(ccDocument);
        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return isValid;

    }

    /**
     * This method checks the CashControlDetail line amount is not zero and the CashControlDocument doesn't have more than one
     * negative line amount.
     * 
     * @param document the CashControldocument
     * @param detail the CashControlDetail
     * @return true is amount is valid, false otherwise
     */
    public boolean checkLineAmount(CashControlDocument document, CashControlDetail detail) {

        boolean isValid = true;

        // line amount cannot be zero
        if (detail.getFinancialDocumentLineAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, KFSKeyConstants.CashControl.LINE_AMOUNT_CANNOT_BE_ZERO);
            isValid = false;
        }
        // there can be only one negative line amount
        else if (detail.getFinancialDocumentLineAmount().isNegative() && document.isHasNegativeCashControlDetail()) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArConstants.ERROR_ONLY_ONE_NEGATIVE_LINE_AMOUNT_ALLOWED);
            isValid = false;
        }

        // if document doesn't have a negative amount yet and this line amount is negative set hasNegativeCashControlDetail to true
        if (!document.isHasNegativeCashControlDetail() && detail.getFinancialDocumentLineAmount().isNegative()) {
            document.setHasNegativeCashControlDetail(true);
        }

        return isValid;

    }

    /**
     * This method checks that payment medium is not null and has a valid value
     * 
     * @param document
     * @return true if valid, false otherwise
     */
    public boolean checkPaymentMedium(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMediumCode = document.getCustomerPaymentMediumCode();

        PaymentMediumService service = SpringContext.getBean(PaymentMediumService.class);

        if (StringUtils.isBlank(paymentMediumCode)) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.CUSTOMER_PAYMENT_MEDIUM_CODE, ArConstants.ERROR_PAYMENT_MEDIUM_CANNOT_BE_NULL);
            isValid = false;
        }
        else if (null == service.getByPrimaryKey(paymentMediumCode)) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.CUSTOMER_PAYMENT_MEDIUM_CODE, ArConstants.ERROR_PAYMENT_MEDIUM_IS_NOT_VALID);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that organization document number is not null when payment medium is Cash.
     * 
     * @param document CashControlDocument
     * @return true if valid, false otherwise
     */
    public boolean checkOrgDocNumber(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME);
        String paymentMedium = document.getCustomerPaymentMediumCode();
        if (ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(paymentMedium)) {
            String orgDocNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
            if (StringUtils.isBlank(orgDocNumber)) {
                GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.ORGANIZATION_DOC_NBR, ArConstants.ERROR_ORGANIZATION_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }
            else {
                boolean docExists = SpringContext.getBean(DocumentService.class).documentExists(orgDocNumber);
                if (!docExists) {
                    GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.ORGANIZATION_DOC_NBR, ArConstants.ERROR_ORGANIZATION_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                    isValid = false;
                }

            }
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME);
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that reference document number is not null
     * 
     * @param document CashControlDocument
     * @return true if not null, false otherwise
     */
    public boolean checkReferenceDocument(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        String refDocNumber = document.getReferenceFinancialDocumentNumber();

        // if payment medium is not Cash the reference document number must not be null; if payment medium is Cash a Cash Receipt
        // Document must be created prior to creating the Cash Control
        // document and it's number should be set in Organization Document number
        if (!ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(document.getCustomerPaymentMediumCode()) && StringUtils.isBlank(refDocNumber)) {
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that user organization options has a valid value
     * 
     * @param document CashConrolDocument
     * @return true if valid, false otherwise
     */
    public boolean checkUserOrgOptions(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        ChartUser user = ValueFinderUtil.getCurrentChartUser();
        String chartCode = user.getChartOfAccountsCode();
        String orgCode = user.getUserOrganizationCode();
        OrganizationOptionsService service = SpringContext.getBean(OrganizationOptionsService.class);

        if (null == service.getByPrimaryKey(chartCode, orgCode)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ORGANIZATION_CODE, ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * @see org.kuali.module.ar.rule.AddCashControlDetailRule#processAddCashControlDetailBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.module.ar.bo.CashControlDetail)
     */
    public boolean processAddCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;
        success &= checkReferenceDocumentNumberNotGenerated(transactionalDocument);
        if (success) {
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
    private boolean validateCashControlDetail(CashControlDocument document, CashControlDetail cashControlDetail) {

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        checkLineAmount(document, cashControlDetail);

        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(cashControlDetail);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        return isValid;

    }

    /**
     * This method validates cash control document's details
     * 
     * @param cashControlDocument CashControldocument
     * @return true if valid, false otherwise
     */
    public boolean validateCashControlDetails(CashControlDocument cashControlDocument) {

        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {
            String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= validateCashControlDetail(cashControlDocument, cashControlDocument.getCashControlDetail(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

        // don't bother checking the total if some deposits are broken
        if (isValid && cashControlDocument.getTotalDollarAmount().isZero()) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CASH_CONTROL_DETAIL, CashReceipt.ERROR_ZERO_TOTAL, ArConstants.CASH_CTRL_DOC_TOTAL);
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks if reference document has been already generated
     * 
     * @param cashControlDocument the cash control document
     * @return true if it was not generated, false otherwise
     */
    public boolean checkReferenceDocumentNumberNotGenerated(CashControlDocument cashControlDocument) {

        boolean success = true;
        String referenceDocumentNumber = cashControlDocument.getReferenceFinancialDocumentNumber();
        if (!StringUtils.isBlank(referenceDocumentNumber)) {
            success = false;
            GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArConstants.ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_REF_DOC_GEN);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
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
            KualiWorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (!(workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal())) {
                allAppDocsApproved = false;

                String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
                GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                GlobalVariables.getErrorMap().addToErrorPath(propertyName);
                GlobalVariables.getErrorMap().put(ArConstants.CashControlDocumentFields.APPLICATION_DOC_STATUS, ArConstants.ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED);
                GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
                GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

                break;
            }

        }

        return allAppDocsApproved;

    }

    /**
     * @see org.kuali.module.ar.rule.DeleteCashControlDetailRule#processDeleteCashControlDetailBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.module.ar.bo.CashControlDetail)
     */
    public boolean processDeleteCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;
        success &= checkReferenceDocumentNumberNotGenerated(transactionalDocument);
        return success;

    }

    /**
     * @see org.kuali.module.ar.rule.GenerateReferenceDocumentRule#processGenerateReferenceDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    public boolean processGenerateReferenceDocumentBusinessRules(CashControlDocument transactionalDocument) {

        boolean success = true;
        success &= checkPaymentMedium(transactionalDocument);
        if (success) {
            success &= checkReferenceDocumentNumberNotGenerated(transactionalDocument);
        }
        return success;

    }

}
