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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDetailItemCodeValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetail customerInvoiceDetail;
    private BusinessObjectService businessObjectService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceItemCode", customerInvoiceDetail.getInvoiceItemCode());
        criteria.put("chartOfAccountsCode", customerInvoiceDocument.getBillByChartOfAccountCode());
        criteria.put("organizationCode", customerInvoiceDocument.getBilledByOrganizationCode());
        CustomerInvoiceItemCode customerInvoiceItemCode = (CustomerInvoiceItemCode) businessObjectService.findByPrimaryKey(CustomerInvoiceItemCode.class, criteria);

        if (ObjectUtils.isNull(customerInvoiceItemCode)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE);
            return false;
        }

        return true;
    }    

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
