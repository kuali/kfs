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

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.service.CustomerInvoiceItemCodeService;
import org.kuali.module.ar.service.OrganizationAccountingDefaultService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceDetailServiceImpl implements CustomerInvoiceDetailService {

    private OrganizationAccountingDefaultService organizationAccountingDefaultService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private CustomerInvoiceItemCodeService customerInvoiceItemCodeService;
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getAddCustomerInvoiceDetail(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getAddLineCustomerInvoiceDetail(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        
        OrganizationAccountingDefault organizationAccountingDefault = organizationAccountingDefaultService.getByPrimaryKey(universityFiscalYear, chartOfAccountsCode, organizationCode);
        if( ObjectUtils.isNotNull( organizationAccountingDefault ) ){
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
    public CustomerInvoiceDetail getAddLineCustomerInvoiceDetailForCurrentUserAndYear() {
        Integer currentUniversityFiscalYear =  universityDateService.getCurrentFiscalYear();
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        return getAddLineCustomerInvoiceDetail(currentUniversityFiscalYear, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }    

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getLoadedCustomerInvoiceDetailFromCustomerInvoiceItemCode(java.lang.String, java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getLoadedCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String chartOfAccountsCode, String organizationCode) {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        
        CustomerInvoiceItemCode customerInvoiceItemCode = customerInvoiceItemCodeService.getByPrimaryKey(invoiceItemCode, chartOfAccountsCode, organizationCode);
        if( ObjectUtils.isNotNull( customerInvoiceItemCode ) ){
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
            
            //TODO set sales tax accordingly
            customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(0.00));
            
            //set amount = unit price * quantity
            customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
            
        } else {
           Integer universityFiscalYear =  universityDateService.getCurrentFiscalYear();
           customerInvoiceDetail = getAddLineCustomerInvoiceDetail(universityFiscalYear, chartOfAccountsCode, organizationCode);
        }

        return customerInvoiceDetail;
    }
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDetailService#getLoadedCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(java.lang.String)
     */
    public CustomerInvoiceDetail getLoadedCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(String invoiceItemCode) {
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        return getLoadedCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }    
    
    public OrganizationAccountingDefaultService getOrganizationAccountingDefaultService() {
        return organizationAccountingDefaultService;
    }

    public void setOrganizationAccountingDefaultService(OrganizationAccountingDefaultService organizationAccountingDefaultService) {
        this.organizationAccountingDefaultService = organizationAccountingDefaultService;
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

    public CustomerInvoiceItemCodeService getCustomerInvoiceItemCodeService() {
        return customerInvoiceItemCodeService;
    }

    public void setCustomerInvoiceItemCodeService(CustomerInvoiceItemCodeService customerInvoiceItemCodeService) {
        this.customerInvoiceItemCodeService = customerInvoiceItemCodeService;
    }


}
