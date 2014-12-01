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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class validates that a corresponding system information discount object code is set
 */
public class CustomerInvoiceDetailSystemInformationDiscountValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;

    public boolean validate(AttributedDocumentEvent event) {
        String processingChartOfAccountsCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrganizationCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("universityFiscalYear", universityFiscalYear);
        criteria.put("processingChartOfAccountCode", processingChartOfAccountsCode);
        criteria.put("processingOrganizationCode", processingOrganizationCode);

        SystemInformation systemInformation = (SystemInformation) businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);
        if (ObjectUtils.isNull(systemInformation) || StringUtils.isEmpty(systemInformation.getDiscountObjectCode())){
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_INVOICE_DETAILS, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_SYSTEM_INFORMATION_DISCOUNT_DOES_NOT_EXIST, new String[]{processingChartOfAccountsCode, processingOrganizationCode,universityFiscalYear.toString()});
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
}
