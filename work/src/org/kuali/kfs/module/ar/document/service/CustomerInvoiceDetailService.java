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

import org.kuali.module.ar.bo.CustomerInvoiceDetail;

/**
 * This class provides services related to the customer invoice document
 */
public interface CustomerInvoiceDetailService {
    
    
    /**
     * This method returns a customer invoice detail with values populated from the specified invoice item code, chart of accounts code, and organization code.
     * If a corresponding invoice item code exists, this method populates the detail off of the invoice item code.  Otherwise, it populates
     * based off the organization accounting default for that chart and org.
     * 
     * @param invoiceItemCode
     * @return
     */
    public CustomerInvoiceDetail getLoadedCustomerInvoiceDetail( String invoiceItemCode, String chartOfAccountsCode, String organizationCode );
    
    
    /**
     * This method returns a customer invoice detail with values populated for current user
     * @param invoiceItemCode
     * @return
     */
    public CustomerInvoiceDetail getLoadedCustomerInvoiceDetailForCurrentUser( String invoiceItemCode );
    
    
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
     * This methon returns a customer invoice detail
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
    
    


}
