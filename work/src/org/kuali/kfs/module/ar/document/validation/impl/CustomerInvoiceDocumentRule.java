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
import static org.kuali.kfs.KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.UnitOfMeasure;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.DiscountCustomerInvoiceDetailRule;
import org.kuali.module.ar.rule.RecalculateCustomerInvoiceDetailRule;
import org.kuali.module.ar.service.CustomerAddressService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.chart.bo.Chart;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerInvoiceDocumentRule extends AccountingDocumentRuleBase implements RecalculateCustomerInvoiceDetailRule<AccountingDocument>, DiscountCustomerInvoiceDetailRule<AccountingDocument> {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentRule.class);

    private CustomerInvoiceDocument customerInvoiceDocument = null;

    /**
     * Constructs a CustomerInvoiceDocumentRule instance.
     */
    public CustomerInvoiceDocumentRule() {
        // TODO Errors on the invoice details were showing twice. This seems to fix the problem. Need find true fix.
        setMaxDictionaryValidationDepth(0);
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document doc) {
        boolean success = true;
        customerInvoiceDocument = (CustomerInvoiceDocument) doc;
        GlobalVariables.getErrorMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        success &= defaultExistenceChecks(customerInvoiceDocument);
        success &= validateCustomerInvoiceDetails(customerInvoiceDocument);
        success &= validateCustomerAddresses(customerInvoiceDocument);

        GlobalVariables.getErrorMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
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

        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) financialDocument;
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) accountingLine;
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART.equals( receivableOffsetOption ) ){
            success &= doesChartCodeHaveRecivableObjectCode( customerInvoiceDetail );
        }
        success &= isCustomerInvoiceDetailUnitPriceValid(customerInvoiceDetail, customerInvoiceDocument);
        success &= isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail);
        success &= isValidUnitOfMeasure(customerInvoiceDetail);

        LOG.debug("processCustomAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");

        return success;

    }

    /**
     * This method returns true if all customer invoice details are valid. The accounting line validation is done through events and
     * not in this method
     * 
     * @param document
     * @return
     */
    private boolean validateCustomerInvoiceDetails(CustomerInvoiceDocument document) {

        boolean success = true;

        CustomerInvoiceDetail customerInvoiceDetail;
        for (int i = 0; i < document.getSourceAccountingLines().size(); i++) {
            customerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy((CustomerInvoiceDetail) document.getSourceAccountingLines().get(i));

            String propertyName = KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            success &= processRecalculateCustomerInvoiceDetailRules(customerInvoiceDocument, customerInvoiceDetail);
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

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

        // check if data dictionary validation document (i.e. required, format, etc);
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(doc);
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            return false;
        }

        success &= isValidAndActiveCustomerNumber(doc);
        success &= isValidBilledByChartOfAccountsCode(doc);
        success &= isValidBilledByOrganizationCode(doc);
        success &= isValidInvoiceDueDate(doc, new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        success &= hasAtLeastOneCustomerInvoiceDetail(doc);

        // validate receivable FAU line if system parameter for receivable is set to 3
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption)) {
            success &= isValidPaymentChartOfAccountsCode(doc);
            success &= isValidPaymentFinancialObjectCode(doc);
            success &= isValidPaymentFinancialSubObjectCode(doc);
            success &= isValidPaymentProjectCode(doc);
            success &= isValidPaymentAccountNumber(doc);
            success &= isValidPaymentSubAccountNumber(doc);
            success &= isValidPaymentProjectCode(doc);
        }

        return success;
    }


    /**
     * This method returns true if payment account number is provided and is valid.
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentAccountNumber(CustomerInvoiceDocument doc) {

        if (StringUtils.isEmpty(doc.getPaymentAccountNumber())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_ACCOUNT_NUMBER, ArConstants.ERROR_PAYMENT_ACCOUNT_NUMBER_REQUIRED);
            return false;
        }
        else {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_ACCOUNT);
            if (ObjectUtils.isNull(doc.getPaymentAccount())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER);
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns true if payment chart of accounts code is provided and is valid
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentChartOfAccountsCode(CustomerInvoiceDocument doc) {

        if (StringUtils.isEmpty(doc.getPaymentChartOfAccountsCode())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArConstants.ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;
        }
        else {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS);
            if (ObjectUtils.isNull(doc.getPaymentChartOfAccounts())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE);
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns true if payment financial object code is provided and is valid
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentFinancialObjectCode(CustomerInvoiceDocument doc) {
        if (StringUtils.isEmpty(doc.getPaymentFinancialObjectCode())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_OBJECT_CODE, ArConstants.ERROR_PAYMENT_OBJECT_CODE_REQUIRED);
            return false;
        }
        else {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_OBJECT);
            if (ObjectUtils.isNull(doc.getPaymentFinancialObject())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_OBJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE);
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns true if the payment sub account number is provided and true.
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentSubAccountNumber(CustomerInvoiceDocument doc) {

        if (StringUtils.isNotEmpty(doc.getPaymentSubAccountNumber())) {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_SUB_ACCOUNT);
            if (ObjectUtils.isNull(doc.getPaymentSubAccount())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_SUB_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER);
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns true if the payment project code is provided and true.
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentProjectCode(CustomerInvoiceDocument doc) {

        if (StringUtils.isNotEmpty(doc.getPaymentProjectCode())) {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_PROJECT);
            if (ObjectUtils.isNull(doc.getPaymentProject())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_PROJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE);
                return false;
            }
        }
        return true;
    }


    /**
     * This method returns true if the payment sub object code is provided and true.
     * 
     * @param doc
     * @return
     */
    private boolean isValidPaymentFinancialSubObjectCode(CustomerInvoiceDocument doc) {
        if (StringUtils.isNotEmpty(doc.getPaymentFinancialSubObjectCode())) {
            doc.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_SUB_OBJECT);
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
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.CUSTOMER_INVOICE_DETAILS, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_NO_CUSTOMER_INVOICE_DETAILS);
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

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceItemCode", customerInvoiceDetail.getInvoiceItemCode());
        criteria.put("chartOfAccountsCode", doc.getBillByChartOfAccountCode());
        criteria.put("organizationCode", doc.getBilledByOrganizationCode());
        CustomerInvoiceItemCode customerInvoiceItemCode = (CustomerInvoiceItemCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerInvoiceItemCode.class, criteria);

        if (ObjectUtils.isNotNull(customerInvoiceDetail)) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE);
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

        doc.getAccountsReceivableDocumentHeader().refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.CUSTOMER);
        Customer customer = doc.getAccountsReceivableDocumentHeader().getCustomer();
        if (ObjectUtils.isNull(customer) || !customer.isCustomerActiveIndicator()) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER);
            return false;
        }

        return true;
    }

    /**
     * This method returns true if invoice due date is less than or equal to 90 days from today's date because invoice due date
     * cannot be more than 90 days from invoice billing date
     * 
     * @param doc
     * @param creationDate passing creationDate
     * @return true if invoice due date is less than or equal to 90 days from today's date
     */
    protected boolean isValidInvoiceDueDate(CustomerInvoiceDocument doc, Timestamp billingDateTimestamp) {

        Timestamp dueDateTimestamp = new Timestamp(doc.getInvoiceDueDate().getTime());

        if (dueDateTimestamp.before(billingDateTimestamp) || dueDateTimestamp.equals(billingDateTimestamp)) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE);
            return false;
        }
        else {
            double diffInDays = DateUtils.getDifferenceInDays(billingDateTimestamp, dueDateTimestamp);
            int maxNumOfDaysAfterCurrentDateForInvoiceDueDate = Integer.parseInt(SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE));
            if (diffInDays >= maxNumOfDaysAfterCurrentDateForInvoiceDueDate) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS, maxNumOfDaysAfterCurrentDateForInvoiceDueDate + "");
                return false;
            }

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

        customerInvoiceDocument.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION);
        if (ObjectUtils.isNull(customerInvoiceDocument.getBilledByOrganization())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_ORGANIZATION_CODE);
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

        customerInvoiceDocument.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT);
        if (ObjectUtils.isNull(customerInvoiceDocument.getBillByChartOfAccount())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_CHART_OF_ACCOUNTS_CODE);
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
        
        if (StringUtils.isNotEmpty(detail.getInvoiceItemUnitOfMeasureCode())) {
            Map criteria = new HashMap();
            criteria.put("itemUnitOfMeasureCode", detail.getInvoiceItemUnitOfMeasureCode());
            if ( ObjectUtils.isNull(SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(UnitOfMeasure.class, criteria ) ) ){
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.UNIT_OF_MEASURE_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_UNIT_OF_MEASURE_CD);
                return false;
            }
        }
        return true;
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

        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) document;
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) accountingLine;
        KualiDecimal amount = customerInvoiceDetail.getAmount();

        // if amount is = 0
        if (KualiDecimal.ZERO.equals(amount)) {
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        else {
            // else if amount is greater than or less than zero

            if (customerInvoiceDocument.isInvoiceReversal()) {
                if (customerInvoiceDetail.isDiscountLine() && amount.isNegative()) {
                    GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && amount.isPositive()) {
                    GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
            else {
                if (customerInvoiceDetail.isDiscountLine() && amount.isPositive()) {
                    GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && amount.isNegative()) {
                    GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * This method returns true if invoice detail unit price is greater than zero. Boolean isDiscountAction is used to determine if
     * validation is coming from add source accounting line event or discount event.
     * 
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailUnitPriceValid(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {

        KualiDecimal unitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();

        // if amount is = 0
        if (ObjectUtils.isNull(unitPrice) || KualiDecimal.ZERO.equals(unitPrice)) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        else {
            // else if unit price is greater than or less than zero

            if (customerInvoiceDocument.isInvoiceReversal()) {
                // if invoice is reversal, discount should be positive, non-discounts should be negative
                if (customerInvoiceDetail.isDiscountLine() && unitPrice.isNegative()) {
                    GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && unitPrice.isPositive()) {
                    GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
            else {
                if (!customerInvoiceDetail.isDiscountLine() && unitPrice.isNegative()) {
                    GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
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

        BigDecimal quantity = customerInvoiceDetail.getInvoiceItemQuantity();

        if (ObjectUtils.isNull(quantity) || BigDecimal.ZERO.compareTo(quantity) == 0 || BigDecimal.ZERO.compareTo(quantity) > 0) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO);
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

        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) financialDocument;

        success &= isCustomerInvoiceDetailUnitPriceValid(customerInvoiceDetail, customerInvoiceDocument);
        success &= isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail);

        if (success) {
            if (customerInvoiceDetail.isDiscountLine()) {
                success &= isDiscountCustomerInvoiceGreaterThanParentAmount(customerInvoiceDetail, customerInvoiceDocument);
            }
            else if (customerInvoiceDetail.isDiscountLineParent()) {
                success &= isParentCustomerInvoiceDetailLessThanDiscountAmount(customerInvoiceDetail, customerInvoiceDocument);
            }
        }

        return success;
    }

    /**
     * This method returns true if the abs(amount) for the discount customer invoice detail line is greater than the abs(amount) of
     * its parent line
     * 
     * @param customerInvoiceDetail
     * @param customerInvoiceDocument
     * @return
     */
    private boolean isDiscountCustomerInvoiceGreaterThanParentAmount(CustomerInvoiceDetail discountCustomerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {

        boolean success = true;

        // make a copy to not mess up the existing reference
        CustomerInvoiceDetail parentCustomerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy(discountCustomerInvoiceDetail.getParentDiscountCustomerInvoiceDetail());

        // update amounts
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, parentCustomerInvoiceDetail);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, discountCustomerInvoiceDetail);

        // return true if abs(discount line amount) IS NOT greater than parent line
        if (discountCustomerInvoiceDetail.getAmount().abs().isGreaterThan(parentCustomerInvoiceDetail.getAmount().abs())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT);
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if the abs(amount) for the discount customer invoice detail line is greater than the amount of its
     * parent line
     * 
     * @param customerInvoiceDetail
     * @param customerInvoiceDocument
     * @return
     */
    private boolean isParentCustomerInvoiceDetailLessThanDiscountAmount(CustomerInvoiceDetail parentCustomerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {

        boolean success = true;

        // make a copy to not mess up the existing reference
        CustomerInvoiceDetail discountCustomerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy(parentCustomerInvoiceDetail.getDiscountCustomerInvoiceDetail());

        // update amounts
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, parentCustomerInvoiceDetail);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, discountCustomerInvoiceDetail);

        // return true if parent line amount is less THAN abs(discount line amount)
        if (parentCustomerInvoiceDetail.getAmount().abs().isLessThan(discountCustomerInvoiceDetail.getAmount().abs())) {
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT);
            success = false;
        }

        return success;
    }

    /**
     * @see org.kuali.module.ar.rule.DiscountCustomerInvoiceDetailRule#processDiscountCustomerInvoiceDetailRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public boolean processDiscountCustomerInvoiceDetailRules(AccountingDocument financialDocument, CustomerInvoiceDetail parentCustomerInvoiceDetail) {

        boolean success = true;
        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) financialDocument;
        success &= isCustomerInvoiceDetailUnitPriceValid(parentCustomerInvoiceDetail, customerInvoiceDocument);
        success &= isCustomerInvoiceDetailItemQuantityGreaterThanZero(parentCustomerInvoiceDetail);

        return success;
    }
    
    /**
     * This method validates the ship to and bill to customer address
     * 
     * @param customerInvoiceDocument
     * @return
     */
    public boolean validateCustomerAddresses( CustomerInvoiceDocument customerInvoiceDocument ){
        boolean success = true;
        String customerNumber = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();

        if( ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier()) ){
            success &= isCustomerAddressValid(customerNumber, customerInvoiceDocument.getCustomerShipToAddressIdentifier(), true);
        }        
        if( ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerBillToAddressIdentifier()) ){
            success &= isCustomerAddressValid(customerNumber, customerInvoiceDocument.getCustomerBillToAddressIdentifier(), false);
        }
        
        return success;
    }

    /**
     * This method validates if a customer address is valid
     * 
     * @param customerInvoiceDocument
     * @param isShipToAddress
     * @return
     */
    public boolean isCustomerAddressValid(String customerNumber, Integer customerAddressIdentifier, boolean isShipToAddress) {

        if (!SpringContext.getBean(CustomerAddressService.class).customerAddressExists(customerNumber, customerAddressIdentifier)) {
            if (isShipToAddress) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.SHIP_TO_ADDRESS_IDENTIFIER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SHIP_TO_ADDRESS_IDENTIFIER);
            } else {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.BILL_TO_ADDRESS_IDENTIFIER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILL_TO_ADDRESS_IDENTIFIER);
            }
            return false;

        }
        return true;
    }
    
    /**
     * This method returns true if chart code has receivable object code
     * 
     * @param customerInvoiceDetail
     * @return
     */
    public boolean doesChartCodeHaveRecivableObjectCode( CustomerInvoiceDetail customerInvoiceDetail ){
        
        customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.CHART);
        Chart chart = customerInvoiceDetail.getChart();
        if( ObjectUtils.isNotNull( chart ) ){
            if( StringUtils.isEmpty(chart.getFinAccountsReceivableObjCode()) ){
                GlobalVariables.getErrorMap().putError(CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CHART_WITH_NO_AR_OBJ_CD);
                return false;
            }
        }
        return true;
    }
}
