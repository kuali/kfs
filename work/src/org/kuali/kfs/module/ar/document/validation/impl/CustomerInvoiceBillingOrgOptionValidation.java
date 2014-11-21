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
