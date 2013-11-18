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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountsReceivableTaxServiceImpl implements AccountsReceivableTaxService {
    
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    protected CustomerAddressService customerAddressService;
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService#isCustomerInvoiceDetailTaxable(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument, org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public boolean isCustomerInvoiceDetailTaxable(CustomerInvoiceDocument customerInvoiceDocument, CustomerInvoiceDetail customerInvoiceDetail) {

        //check if sales tax is enabled
        if( !parameterService.getParameterValueAsBoolean(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) ){
            return false;
        }

        //check if customer is tax exempt
        if( ObjectUtils.isNotNull(customerInvoiceDocument.getCustomer() ) ){
            if( customerInvoiceDocument.getCustomer().isCustomerTaxExemptIndicator()){
                return false;
            }
        }
        
        //check item if the taxable indicator is checked
        if (!customerInvoiceDetail.isTaxableIndicator()) {
            return false;
        }
        
        //check if the shipping address has Postal Code in the same country and state as the Billing Org. If not, the item is not taxable.
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getShippingZipCode()) && 
            StringUtils.equals(customerInvoiceDocument.getShippingCountryCode(), customerInvoiceDocument.getBillingCountryCode()) &&
            !StringUtils.equals(customerInvoiceDocument.getShippingStateCode(), customerInvoiceDocument.getBillingStateCode())) {               
            return false;
        }
        
        return true;
    }
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService#getPostalCodeForTaxation(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public String getPostalCodeForTaxation(CustomerInvoiceDocument document) {
        
        String postalCode = null;
        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Integer shipToAddressIdentifier = document.getCustomerShipToAddressIdentifier();
        
        //if customer number or ship to address id isn't provided, go to org options
        if (ObjectUtils.isNotNull(shipToAddressIdentifier) && StringUtils.isNotEmpty(customerNumber) ) {
            
            CustomerAddress customerShipToAddress = customerAddressService.getByPrimaryKey(customerNumber, shipToAddressIdentifier);
            if( ObjectUtils.isNotNull(customerShipToAddress) ){
                postalCode = customerShipToAddress.getCustomerZipCode();
            }
        }
        else {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
            criteria.put("organizationCode", document.getBilledByOrganizationCode());
            OrganizationOptions organizationOptions = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

            if (ObjectUtils.isNotNull(organizationOptions)) {
                postalCode = organizationOptions.getOrganizationPostalZipCode();
            }

           
        }
        return postalCode;
    }        

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }
}
