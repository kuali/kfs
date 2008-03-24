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

import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.ZERO;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;
import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.RecalculateCustomerInvoiceDetailRule;
import org.kuali.module.ar.service.CustomerInvoiceItemCodeService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

public class CustomerInvoiceDocumentRule extends AccountingDocumentRuleBase implements RecalculateCustomerInvoiceDetailRule<AccountingDocument> {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentRule.class);

    private CustomerInvoiceDocument customerInvoiceDocument = null;
    
    /**
     * Constructs a CustomerInvoiceDocumentRule instance.
     */
    public CustomerInvoiceDocumentRule() {        
        //TODO Errors on the invoice details were showing twice.  This seems to fix the problem. Need find true fix.
        setMaxDictionaryValidationDepth(0);
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document doc) {
        boolean success = true;
        customerInvoiceDocument = (CustomerInvoiceDocument) doc;
        GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);
        success &= defaultExistenceChecks(customerInvoiceDocument);
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);
        return success;
    }

    /**
     * Validate information specific to customerInvoiceDetail
     * 
     * @param financialDocument
     * @param accountingLine
     * @return boolean
     */
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("processCustomAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - start");

        boolean success = true;

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) accountingLine;

        // success &= isCustomerInvoiceItemCodeValid(customerInvoiceDetail);
        success &= isCustomerInvoiceDetailItemUnitPriceGreaterThanZero(customerInvoiceDetail);
        success &= isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail);
        success &= isValidUnitOfMeasure(customerInvoiceDetail);

        LOG.debug("processCustomAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");

        return success;

    }

    /**
     * This method returns true if default existence checks for general customer invoice information is true
     * 
     * @param doc
     * @return
     */
    private boolean defaultExistenceChecks(CustomerInvoiceDocument doc) {
        boolean success = true;
        
        //check if data dictionary validation document (i.e. required, format, etc);
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(doc);
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            return false;
        }

        success &= isValidAndActiveCustomerNumber(doc);
        success &= isValidBilledByChartOfAccountsCode(doc);
        success &= isValidBilledByOrganizationCode(doc);
        success &= isValidInvoiceDueDate(doc, doc.getDocumentHeader().getWorkflowDocument().getCreateDate());
        success &= hasAtLeastOneCustomerInvoiceDetail(doc);
        
        //TODO Only check if System Parameter GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD == 3
        /*
        if(true){ 
            success &= isValidPaymentChartOfAccountsCode(doc);
            success &= isValidPaymentFinancialObjectCode(doc);
            success &= isValidPaymentFinancialSubObjectCode(doc);
            success &= isValidPaymentProjectCode(doc);
            success &= isValidPaymentAccountNumber(doc);
            success &= isValidPaymentSubAccountNumber(doc);
            success &= isValidPaymentProjectCode(doc);
        }*/

        return success;
    }

    /**
     * This method returns true if payment account number is provided and is valid.
     * @param doc
     * @return
     */
    private boolean isValidPaymentAccountNumber(CustomerInvoiceDocument doc) {
        
        if( StringUtils.isEmpty(doc.getPaymentAccountNumber() ) ){
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER_REQUIRED);
            return false;            
        } else {
            doc.refreshReferenceObject("paymentAccount");
            if (ObjectUtils.isNull(doc.getPaymentAccount())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER);
                return false;
            }
        }

        return true;
    }
    
    /**
     * This method returns true if payment chart of accounts code is provided and is valid
     * @param doc
     * @return
     */
    private boolean isValidPaymentChartOfAccountsCode(CustomerInvoiceDocument doc) {
        
        if( StringUtils.isEmpty(doc.getPaymentChartOfAccountsCode() ) ){
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;            
        } else {
            doc.refreshReferenceObject("paymentChartOfAccounts");
            if (ObjectUtils.isNull(doc.getPaymentChartOfAccounts())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE);
                return false;
            }
        }
        
        return true;
    }

    /**
     * This method returns true if payment financial object code is provided and is valid
     * @param doc
     * @return
     */
    private boolean isValidPaymentFinancialObjectCode(CustomerInvoiceDocument doc) {
        if( StringUtils.isEmpty(doc.getPaymentFinancialObjectCode() ) ){
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_OBJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE_REQUIRED);
            return false;            
        } else {
            doc.refreshReferenceObject("paymentFinancialObject");
            if (ObjectUtils.isNull(doc.getPaymentFinancialObject())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_OBJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE);
                return false;
            }
        }
        
        return true;
    }

    /**
     * This method returns true if the payment sub account number is provided and true.
     * @param doc
     * @return
     */
    private boolean isValidPaymentSubAccountNumber(CustomerInvoiceDocument doc) {
        
        if( StringUtils.isNotEmpty(doc.getPaymentSubAccountNumber())){
            doc.refreshReferenceObject("paymentSubAccount");
            if (ObjectUtils.isNull(doc.getPaymentSubAccount())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_SUB_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER);
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns true if the payment project code is provided and true.
     * @param doc
     * @return
     */
    private boolean isValidPaymentProjectCode(CustomerInvoiceDocument doc) {
        
        if( StringUtils.isNotEmpty(doc.getPaymentProjectCode())){
            doc.refreshReferenceObject("paymentProject");
            if (ObjectUtils.isNull(doc.getPaymentProject())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_PROJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE);
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * This method returns true if the payment sub object code is provided and true.
     * @param doc
     * @return
     */    
    private boolean isValidPaymentFinancialSubObjectCode(CustomerInvoiceDocument doc) {
        if( StringUtils.isNotEmpty(doc.getPaymentFinancialSubObjectCode())){
            doc.refreshReferenceObject("paymentFinancialSubObject");
            if (ObjectUtils.isNull(doc.getPaymentFinancialSubObject())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_SUB_OBJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE);
                return false;
            }
        }
        return true;
    }    

    /**
     * This method returns true if there exists at least 1 customer invoice detail
     * 
     * @param doc
     * @return
     */
    protected boolean hasAtLeastOneCustomerInvoiceDetail(CustomerInvoiceDocument doc) {

        if (doc.getSourceAccountingLines().size() == 0) {
            GlobalVariables.getErrorMap().putError("accountingLines", ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_NO_CUSTOMER_INVOICE_DETAILS);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if invoice code is provided and is valid.
     * 
     * @param customerInvoiceDetail
     * @return
     */
    private boolean isCustomerInvoiceItemCodeValid(CustomerInvoiceDocument doc, CustomerInvoiceDetail customerInvoiceDetail) {

        CustomerInvoiceItemCode customerInvoiceItemCode = SpringContext.getBean(CustomerInvoiceItemCodeService.class).getByPrimaryKey(customerInvoiceDetail.getInvoiceItemCode(), doc.getBillByChartOfAccountCode(), doc.getBilledByOrganizationCode());
        if (ObjectUtils.isNotNull(customerInvoiceDetail)) {
            GlobalVariables.getErrorMap().putError("invoiceItemCode", ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE);
            return false;
        }

        return true;
    }


    /**
     * This method returns true the customer number provided is valid and active
     * 
     * @param doc
     * @return
     */
    protected boolean isValidAndActiveCustomerNumber(CustomerInvoiceDocument doc) {

        doc.getAccountsReceivableDocumentHeader().refreshReferenceObject("customer");
        Customer customer = doc.getAccountsReceivableDocumentHeader().getCustomer();
        if (ObjectUtils.isNull(customer) || !customer.isCustomerActiveIndicator()) {
            GlobalVariables.getErrorMap().putError("accountsReceivableDocumentHeader.customerNumber", ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if invoice due date is less than or equal to 90 days from today's date because invoice due date
     * cannot be more than 90 days from invoice creation date
     * 
     * @param doc
     * @param creationDate passing creationDate
     * @return true if invoice due date is less than or equal to 90 days from today's date
     */
    protected boolean isValidInvoiceDueDate(CustomerInvoiceDocument doc, Timestamp creationDate) {

        Timestamp invoiceDueDateTime = new Timestamp(doc.getInvoiceDueDate().getTime());

        // TODO should the # of valid days be a system parameter?
        double diffInDays = DateUtils.getDifferenceInDays(creationDate, invoiceDueDateTime);
        if (diffInDays > ArConstants.VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE) {
            GlobalVariables.getErrorMap().putError("invoiceDueDate", ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE);
            return false;
        }

        return true;

    }

    /**
     * This method returns true if organization code is valid. Assumes that both billed by chart of accounts code and billed by
     * organization are provided
     * 
     * @param customerInvoiceDocument
     * @return
     */
    protected boolean isValidBilledByOrganizationCode(CustomerInvoiceDocument customerInvoiceDocument) {

        customerInvoiceDocument.refreshReferenceObject("billedByOrganization");
        if (ObjectUtils.isNull(customerInvoiceDocument.getBilledByOrganization())) {
            GlobalVariables.getErrorMap().putError("billedByOrganizationCode", ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_ORGANIZATION_CODE);
            return false;
        }

        return true;
    }


    /**
     * This method returns true if billed by chart of accounts code is valid. Assumes that chart of accounts code is provided.
     * 
     * @param customerInvoiceDocument
     * @return
     */
    protected boolean isValidBilledByChartOfAccountsCode(CustomerInvoiceDocument customerInvoiceDocument) {

        customerInvoiceDocument.refreshReferenceObject("billByChartOfAccount");
        if (ObjectUtils.isNull(customerInvoiceDocument.getBillByChartOfAccount())) {
            GlobalVariables.getErrorMap().putError("billByChartOfAccountCode", ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_CHART_OF_ACCOUNTS_CODE);
            return false;
        }

        return true;
    }

    /**
     * This method validates that the unit of measure for a customer invoice detail line is a valid UOM.
     * 
     * @param detail
     * @return
     */
    protected boolean isValidUnitOfMeasure(CustomerInvoiceDetail detail) {
        boolean success = true;
        // this most likely will be a parameter service call
        if (!StringUtils.isEmpty(detail.getInvoiceItemUnitOfMeasureCode())) {
            // validate through parameter service
        }
        return success;
    }

    /**
     * Overriding AccountingDocumentRuleBase.isValid() because the error message doesn't make sense for the CustomerInvoiceDocument.
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        LOG.debug("isAmountValid(AccountingDocument, AccountingLine) - start");

        KualiDecimal amount = accountingLine.getAmount();

        if (ZERO.compareTo(amount) == 0 || ZERO.compareTo(amount) > 0) { // amount == 0 or amount < 0
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if invoice detail unit price is greater than zero
     * 
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailItemUnitPriceGreaterThanZero(CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal amount = customerInvoiceDetail.getInvoiceItemUnitPrice();

     // amount == 0 or amount < 0
        if (ObjectUtils.isNull(amount) || ZERO.compareTo(amount) == 0 || ZERO.compareTo(amount) > 0) { 
            GlobalVariables.getErrorMap().putError("invoiceItemUnitPrice", ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        return true;
    }

    /**
     * This method returns true if invoice detail unit price is greater than zero
     * 
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailItemQuantityGreaterThanZero(CustomerInvoiceDetail customerInvoiceDetail) {

        BigDecimal amount = customerInvoiceDetail.getInvoiceItemQuantity();

        //amount == 0 or amount < 0
        if (ObjectUtils.isNull(amount) || BigDecimal.ZERO.compareTo(amount) == 0 || BigDecimal.ZERO.compareTo(amount) > 0) { 
            GlobalVariables.getErrorMap().putError("invoiceItemQuantity", ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.module.ar.rule.RecalculateCustomerInvoiceDetailRule#processRecalculateCustomerInvoiceDetailRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public boolean processRecalculateCustomerInvoiceDetailRules(AccountingDocument financialDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        boolean success = true;
        
        success &= isCustomerInvoiceDetailItemUnitPriceGreaterThanZero(customerInvoiceDetail);
        success &= isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail);
        
        return success;
    }
   
}
