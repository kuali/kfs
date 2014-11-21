/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
            
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
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
}
