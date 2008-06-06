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
package org.kuali.module.ar.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.impl.CustomerInvoiceDetailServiceImpl;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * This class provides services related to the customer invoice document
 */
public interface CustomerInvoiceDetailService {
    
    /**
     * This method returns a customer invoice detail for use on the CustomerInvoiceDocumentAction.  If corresponding
     * organization accounting default exists for billing chart and org, then the customer invoice detail is defaulted
     * using the organization accounting default values.
     * 
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefault(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);
    
    /**
     * This method returns a customer invoice detail for current user and current fiscal year for use on the CustomerInvoiceDocumentAction.  If corresponding
     * organization accounting default exists for billing chart and org, then the customer invoice detail is defaulted
     * using the organization accounting default values.
     * 
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear();
    
    
    /**
     * This method returns a customer invoice detail from a customer invoice item code for a current user
     * @param invoiceItemCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser( String invoiceItemCode );
    
    /**
     * This method...
     * @param invoiceItemCode
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode( String invoiceItemCode, String chartOfAccountsCode, String organizationCode);
    
    
    /**
     * This method returns a discount customer invoice detail based on a customer invoice detail, the chart of accounts code
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail( CustomerInvoiceDetail customerInvoiceDetail, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode );
    
    
    /**
     * This method returns a discount customer invoice detail for the current year
     * 
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetailForCurrentYear( CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument );
 
    /**
     * This method returns a customer invoice detail for the current year
     * 
     * @param documentNumber
     * @param sequenceNumber
     * @return
     */    
    public CustomerInvoiceDetail getCustomerInvoiceDetail(String documentNumber,Integer sequenceNumber);
    
    /**
     * This method returns a customer invoice detail open item amount
     * 
     * @param documentNumber
     * @param invoiceItemCode
     * @return
     */  
    public KualiDecimal getOpenAmount(Integer invoiceItemNumber,CustomerInvoiceDetail customerInvoiceDetail);
    
    /**
     * This method is used to recalculate a customer invoice detail based on updated values
     * @param customerInvoiceDetail
     */
    public void recalculateCustomerInvoiceDetail( CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method is used to update account for corresponding discount line based on parent line's account
     * @param parentDiscountCustomerInvoiceDetail
     */
    public void updateAccountsForCorrespondingDiscount( CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail );
    
    /**
     * This method is used update the accounts receivable object code if receivable options 1 or 2 are selected.
     * @param customerInvoiceDetail
     */
    public void updateAccountsReceivableObjectCode( CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method returns the correct accounts receivable object code based on a receivable parameter.
     * @return
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter( CustomerInvoiceDetail customerInvoiceDetail);
    
    /**
     * This method is used make sure the amounts are calculated correctly and the correct AR object code is in place
     * @param customerInvoiceDetail
     */
    public void prepareCustomerInvoiceDetailForAdd( CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument );
}
