/*
 * Copyright 2008-2009 The Kuali Foundation
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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceBillingOrgOptionValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    private BusinessObjectService businessObjectService;

    public boolean validate(AttributedDocumentEvent event) {

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", customerInvoiceDocument.getBillByChartOfAccountCode());
        criteria.put("organizationCode", customerInvoiceDocument.getBilledByOrganizationCode());
        OrganizationOptions organizationOptions = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNull(organizationOptions)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, ArKeyConstants.InvoiceItemCode.ORG_OPTIONS_DOES_NOT_EXIST_FOR_CHART_AND_ORG, new String[]{customerInvoiceDocument.getBillByChartOfAccountCode(), customerInvoiceDocument.getBilledByOrganizationCode()});
            return false;
        }
        else {
            if (!(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode().equalsIgnoreCase(organizationOptions.getProcessingChartOfAccountCode()) &&
                  customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode().equalsIgnoreCase(organizationOptions.getProcessingOrganizationCode()))) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, 
                        ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLING_PROCESSING_ORGANIZATION_IN_ORG_OPTIONS, 
                        new String[]{customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccCodeAndOrgCode(),
                        customerInvoiceDocument.getBilledByChartOfAccCodeAndOrgCode()});
                return false;
            }
        }
        return true; 
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
