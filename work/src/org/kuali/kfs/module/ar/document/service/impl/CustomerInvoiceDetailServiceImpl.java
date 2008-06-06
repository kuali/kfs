/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.bo.InvoicePaidApplied;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.service.UniversityDateService;

public class CustomerInvoiceDetailServiceImpl implements CustomerInvoiceDetailService {

    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getAddCustomerInvoiceDetail(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefault(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();

        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", universityFiscalYear);
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);

        OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
        if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
            customerInvoiceDetail.setChartOfAccountsCode(organizationAccountingDefault.getDefaultInvoiceChartOfAccountsCode());
            customerInvoiceDetail.setAccountNumber(organizationAccountingDefault.getDefaultInvoiceAccountNumber());
            customerInvoiceDetail.setSubAccountNumber(organizationAccountingDefault.getDefaultInvoiceSubAccountNumber());
            customerInvoiceDetail.setFinancialObjectCode(organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode());
            customerInvoiceDetail.setFinancialSubObjectCode(organizationAccountingDefault.getDefaultInvoiceFinancialSubObjectCode());
            customerInvoiceDetail.setProjectCode(organizationAccountingDefault.getDefaultInvoiceProjectCode());
            customerInvoiceDetail.setOrganizationReferenceId(organizationAccountingDefault.getDefaultInvoiceOrganizationReferenceIdentifier());
        }

        customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(0.00));
        customerInvoiceDetail.setInvoiceItemQuantity(new BigDecimal(1));
        customerInvoiceDetail.setInvoiceItemUnitOfMeasureCode(ArConstants.CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT);
        customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());

        return customerInvoiceDetail;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getAddLineCustomerInvoiceDetailForCurrentUserAndYear()
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear() {
        Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        return getCustomerInvoiceDetailFromOrganizationAccountingDefault(currentUniversityFiscalYear, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailFromCustomerInvoiceItemCode(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String chartOfAccountsCode, String organizationCode) {

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceItemCode", invoiceItemCode);
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        CustomerInvoiceItemCode customerInvoiceItemCode = (CustomerInvoiceItemCode) businessObjectService.findByPrimaryKey(CustomerInvoiceItemCode.class, criteria);

        CustomerInvoiceDetail customerInvoiceDetail = null;
        if (ObjectUtils.isNotNull(customerInvoiceItemCode)) {

            customerInvoiceDetail = new CustomerInvoiceDetail();
            customerInvoiceDetail.setChartOfAccountsCode(customerInvoiceItemCode.getDefaultInvoiceChartOfAccountsCode());
            customerInvoiceDetail.setAccountNumber(customerInvoiceItemCode.getDefaultInvoiceAccountNumber());
            customerInvoiceDetail.setSubAccountNumber(customerInvoiceItemCode.getDefaultInvoiceSubAccountNumber());
            customerInvoiceDetail.setFinancialObjectCode(customerInvoiceItemCode.getDefaultInvoiceFinancialObjectCode());
            customerInvoiceDetail.setFinancialSubObjectCode(customerInvoiceItemCode.getDefaultInvoiceFinancialSubObjectCode());
            customerInvoiceDetail.setProjectCode(customerInvoiceItemCode.getDefaultInvoiceProjectCode());

            customerInvoiceDetail.setOrganizationReferenceId(customerInvoiceItemCode.getDefaultInvoiceOrganizationReferenceIdentifier());
            customerInvoiceDetail.setInvoiceItemCode(customerInvoiceItemCode.getInvoiceItemCode());
            customerInvoiceDetail.setInvoiceItemDescription(customerInvoiceItemCode.getInvoiceItemDescription());
            customerInvoiceDetail.setInvoiceItemUnitPrice(customerInvoiceItemCode.getItemDefaultPrice());
            customerInvoiceDetail.setInvoiceItemUnitOfMeasureCode(customerInvoiceItemCode.getDefaultUnitOfMeasureCode());
            customerInvoiceDetail.setInvoiceItemQuantity(customerInvoiceItemCode.getItemDefaultQuantity());

            customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());

            // TODO set sales tax accordingly
            customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(0.00));

            // set amount = unit price * quantity
            customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();

        }

        return customerInvoiceDetail;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(String invoiceItemCode) {
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        return getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getDiscountCustomerInvoiceDetail(org.kuali.module.ar.bo.CustomerInvoiceDetail,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {

        CustomerInvoiceDetail discountCustomerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy(customerInvoiceDetail);
        discountCustomerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        discountCustomerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
        discountCustomerInvoiceDetail.setInvoiceItemDescription(ArConstants.DISCOUNT_PREFIX + StringUtils.trimToEmpty(customerInvoiceDetail.getInvoiceItemDescription()));

        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", universityFiscalYear);
        criteria.put("processingChartOfAccountCode", chartOfAccountsCode);
        criteria.put("processingOrganizationCode", organizationCode);

        SystemInformation systemInformation = (SystemInformation) businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);
        if (ObjectUtils.isNotNull(systemInformation)) {
            discountCustomerInvoiceDetail.setFinancialObjectCode(systemInformation.getDiscountObjectCode());
        }

        return discountCustomerInvoiceDetail;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getDiscountCustomerInvoiceDetailForCurrentYear(org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetailForCurrentYear(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {
        Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
        String processingChartOfAccountsCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrganizationCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        return getDiscountCustomerInvoiceDetail(customerInvoiceDetail, currentUniversityFiscalYear, processingChartOfAccountsCode, processingOrganizationCode);
    }


    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#recalculateCustomerInvoiceDetail(org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public void recalculateCustomerInvoiceDetail(CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail) {

        // make sure amounts are negative when they are supposed to be
        if (!document.isInvoiceReversal() && customerInvoiceDetail.isDiscountLine()) {
            customerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        }
        else if (document.isInvoiceReversal() && !customerInvoiceDetail.isDiscountLine()) {
            customerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        }
        customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#updateAccountsForCorrespondingDiscount(org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public void updateAccountsForCorrespondingDiscount(CustomerInvoiceDetail parent) {

        CustomerInvoiceDetail discount = parent.getDiscountCustomerInvoiceDetail();
        if (ObjectUtils.isNotNull(discount)) {
            if (StringUtils.isNotEmpty(parent.getAccountNumber())) {
                discount.setAccountNumber(parent.getAccountNumber());
            }
            if (StringUtils.isNotEmpty(parent.getSubAccountNumber())) {
                discount.setSubAccountNumber(parent.getSubAccountNumber());
            }
        }
    }


    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getCustomerInvoiceDetail(java.lang.String, java.lang.Integer)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetail(String documentNumber, Integer sequenceNumber) {
        Map criteria = new HashMap();
        criteria.put("documentNumber", documentNumber);
        criteria.put("sequenceNumber", sequenceNumber);

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) businessObjectService.findByPrimaryKey(CustomerInvoiceDetail.class, criteria);

        return customerInvoiceDetail;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getOpenAmount(java.lang.Integer,
     *      org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public KualiDecimal getOpenAmount(Integer invoiceItemNumber, CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal totalAppliedAmount = KualiDecimal.ZERO;
        KualiDecimal appliedAmount;
        KualiDecimal openInvoiceAmount;
        KualiDecimal invoiceItemAmount;

        String documentNumber = customerInvoiceDetail.getDocumentNumber();

        Map criteria = new HashMap();
        criteria.put("documentNumber", documentNumber);
        criteria.put("invoiceItemNumber", invoiceItemNumber);

        Collection<InvoicePaidApplied> results = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        for (InvoicePaidApplied invoicePaidApplied : results) {
            appliedAmount = invoicePaidApplied.getInvoiceItemAppliedAmount();
            if (ObjectUtils.isNotNull(appliedAmount))
                totalAppliedAmount.add(appliedAmount);
        }
        invoiceItemAmount = customerInvoiceDetail.getAmount();
        openInvoiceAmount = invoiceItemAmount.subtract(totalAppliedAmount);

        return openInvoiceAmount;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#updateAccountsReceivableObjectCode(org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public void updateAccountsReceivableObjectCode(CustomerInvoiceDetail customerInvoiceDetail) {
        String receivableOffsetOption = parameterService.getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        String accountsReceivableObjectCode = null;
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART.equals(receivableOffsetOption)) {
            if (StringUtils.isNotEmpty(customerInvoiceDetail.getChartOfAccountsCode())) {
                customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.CHART);
                accountsReceivableObjectCode = customerInvoiceDetail.getChart().getFinAccountsReceivableObj().getFinancialObjectCode();
            }
        }
        else if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND.equals(receivableOffsetOption)) {
            if (StringUtils.isNotEmpty(customerInvoiceDetail.getAccountNumber())) {
                customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                accountsReceivableObjectCode = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND, customerInvoiceDetail.getAccount().getSubFundGroupCode());
            }
        }
        customerInvoiceDetail.setAccountsReceivableObjectCode(accountsReceivableObjectCode);
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#prepareCustomerInvoiceDetailForAdd(org.kuali.module.ar.bo.CustomerInvoiceDetail, org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public void prepareCustomerInvoiceDetailForAdd(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {
        recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);
        updateAccountsReceivableObjectCode(customerInvoiceDetail);
    }


    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
