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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.validation.ContinueCustomerInvoiceWriteoffDocumentRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceWriteoffDocumentRule extends TransactionalDocumentRuleBase implements ContinueCustomerInvoiceWriteoffDocumentRule<TransactionalDocument> {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean success = super.processCustomSaveDocumentBusinessRules(document);

        GlobalVariables.getMessageMap().addToErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;
        success &= validateCustomerNote(customerInvoiceWriteoffDocument.getCustomerNote());
        success &= validateWriteoffGLPEGenerationInformation(customerInvoiceWriteoffDocument);
        GlobalVariables.getMessageMap().removeFromErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        return success;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean success = super.processCustomSaveDocumentBusinessRules(document);

        GlobalVariables.getMessageMap().addToErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;
        success &= validateCustomerNote(customerInvoiceWriteoffDocument.getCustomerNote());
        success &= validateWriteoffGLPEGenerationInformation(customerInvoiceWriteoffDocument);
        success &= doesCustomerInvoiceDocumentHaveValidBalance(customerInvoiceWriteoffDocument);

        GlobalVariables.getMessageMap().removeFromErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME);

        return success;
    }

    protected boolean validateCustomerNote(String customerNote) {
        boolean success = true;
        if (StringUtils.isNotEmpty(customerNote) && (customerNote.trim().length() < 5)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NOTE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_CUSTOMER_NOTE_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method validates any writeoff GLPE required information
     *
     * @param customerInvoiceWriteoffDocument
     * @return
     */
    protected boolean validateWriteoffGLPEGenerationInformation(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        boolean success = true;

        String writeoffGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);

        if (ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_CHART.equals(writeoffGenerationOption)) {
            for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts()) {
                success &= doesChartCodeHaveCorrespondingWriteoffObjectCode(customerInvoiceDetail);
            }
        }
        else if (ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffGenerationOption)) {
            success &= doesOrganizationAccountingDefaultHaveWriteoffInformation(customerInvoiceWriteoffDocument);
        }

        String writeoffTaxGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND);
        if (ArConstants.ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND_NO.equals(writeoffTaxGenerationOption)) {
            success &= doesOrganizationAccountingDefaultHaveWriteoffInformation(customerInvoiceWriteoffDocument);
        }

        return success;
    }

    /**
     * This method checks if the chart object code using on the invoice detail has a corresponding
     *
     * @param customerInvoiceDetail
     * @return TODO
     */
    protected boolean doesChartCodeHaveCorrespondingWriteoffObjectCode(CustomerInvoiceDetail customerInvoiceDetail) {
        boolean success = true;

        String writeoffObjectCode = SpringContext.getBean(ParameterService.class).getSubParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_OBJECT_CODE_BY_CHART, customerInvoiceDetail.getChartOfAccountsCode());
        if (StringUtils.isBlank(writeoffObjectCode)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST, customerInvoiceDetail.getChartOfAccountsCode());
            success = false;
        }

        return success;
    }

    protected boolean doesOrganizationAccountingDefaultHaveWriteoffInformation(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        boolean success = true;
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        String billByChartOfAccountCode = customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBillByChartOfAccountCode();
        String billedByOrganizationCode = customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBilledByOrganizationCode();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("chartOfAccountsCode", billByChartOfAccountCode);
        criteria.put("organizationCode", billedByOrganizationCode);

        OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

        if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
            success &= doesWriteoffAccountNumberExist(organizationAccountingDefault);
            success &= doesWriteoffChartOfAccountsCodeExist(organizationAccountingDefault);
            success &= doesWriteoffFinancialObjectCodeExist(organizationAccountingDefault);
        }
        else {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_MUST_EXIST, new String[] { currentFiscalYear.toString(), billByChartOfAccountCode, billedByOrganizationCode });
            success = false;
        }

        return success;

    }

    /**
     * This method returns true if payment account number is provided and is valid.
     *
     * @param doc
     * @return
     */
    protected boolean doesWriteoffAccountNumberExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isBlank(organizationAccountingDefault.getWriteoffAccountNumber())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_ACCOUNT_MUST_EXIST, new String[] { organizationAccountingDefault.getUniversityFiscalYear().toString(), organizationAccountingDefault.getChartOfAccountsCode(), organizationAccountingDefault.getOrganizationCode() });
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment chart of accounts code is provided and is valid
     *
     * @param doc
     * @return
     */
    protected boolean doesWriteoffChartOfAccountsCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {

        if (StringUtils.isBlank(organizationAccountingDefault.getWriteoffChartOfAccountsCode())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_CHART_MUST_EXIST, new String[] { organizationAccountingDefault.getUniversityFiscalYear().toString(), organizationAccountingDefault.getChartOfAccountsCode(), organizationAccountingDefault.getOrganizationCode() });
            return false;
        }

        return true;
    }

    /**
     * This method returns true if payment financial object code is provided and is valid
     *
     * @param doc
     * @return
     */
    protected boolean doesWriteoffFinancialObjectCodeExist(OrganizationAccountingDefault organizationAccountingDefault) {
        if (StringUtils.isBlank(organizationAccountingDefault.getWriteoffFinancialObjectCode())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_OBJECT_CODE_MUST_EXIST, new String[] { organizationAccountingDefault.getUniversityFiscalYear().toString(), organizationAccountingDefault.getChartOfAccountsCode(), organizationAccountingDefault.getOrganizationCode() });
            return false;
        }

        return true;
    }

    /**
     * This method returns true if customer invoice document for writeoff does not have a credit balance (i.e. a open amount less
     * than 0).
     *
     * @param customerInvoiceWriteoffDocument
     * @return
     */
    protected boolean doesCustomerInvoiceDocumentHaveValidBalance(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        if (KualiDecimal.ZERO.isGreaterEqual(customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getOpenAmount())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_INVOICE_HAS_CREDIT_BALANCE);
            return false;
        }
        return true;
    }

    /**
     * This method checks if there is no another CRM in route for the invoice not in route if CRM status is one of the following:
     * processed, cancelled, or disapproved
     *
     * @param invoice
     * @return
     */
    public boolean checkIfThereIsNoAnotherCRMInRouteForTheInvoice(String invoiceDocumentNumber) {
        CustomerInvoiceWriteoffDocumentService service = SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);
        boolean success = service.checkIfThereIsNoAnotherCRMInRouteForTheInvoice(invoiceDocumentNumber);
        if (!success) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE);
        }

        return success;
    }

    /**
     * This method checks if there is no another writeoff in route for the invoice not in route if CRM status is one of the
     * following: processed, cancelled, or disapproved
     *
     * @param invoice
     * @return
     */
    public boolean checkIfThereIsNoAnotherWriteoffInRouteForTheInvoice(String invoiceDocumentNumber) {
        CustomerInvoiceWriteoffDocumentService service = SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);
        boolean success = service.checkIfThereIsNoAnotherWriteoffInRouteForTheInvoice(invoiceDocumentNumber);
        if (!success) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_ONE_WRITEOFF_IN_ROUTE_PER_INVOICE);
        }

        return success;
    }

    /*
     * @see org.kuali.kfs.module.ar.document.validation.ContinueCustomerInvoiceWriteoffDocumentRule#processContinueCustomerInvoiceWriteoffDocumentRules(org.kuali.kfs.sys.document.AccountingDocument)
     */

    @Override
    public boolean processContinueCustomerInvoiceWriteoffDocumentRules(TransactionalDocument document) {
        boolean success;
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;

        success = checkIfInvoiceNumberIsValid(customerInvoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber());
        if (success) {
            success = doesCustomerInvoiceDocumentHaveValidBalance(customerInvoiceWriteoffDocument);
        }
        if (success) {
            success = checkIfThereIsNoAnotherCRMInRouteForTheInvoice(customerInvoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber());
        }
        if (success) {
            success = checkIfThereIsNoAnotherWriteoffInRouteForTheInvoice(customerInvoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber());
        }

        return success;
    }

    public boolean checkIfInvoiceNumberIsValid(String invDocumentNumber) {
        boolean success = true;

        if (ObjectUtils.isNull(invDocumentNumber) || StringUtils.isBlank(invDocumentNumber)) {
            success = false;
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED);
        }
        else {
            CustomerInvoiceDocumentService service = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument customerInvoiceDocument = service.getInvoiceByInvoiceDocumentNumber(invDocumentNumber);

            if (ObjectUtils.isNull(customerInvoiceDocument)) {
                success = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER);
            }
            else if (!SpringContext.getBean(CustomerInvoiceDocumentService.class).checkIfInvoiceNumberIsFinal(invDocumentNumber)) {
                success = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_NOT_FINAL);
            }

        }
        return success;
    }
}
