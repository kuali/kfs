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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceWriteoffChartOfAccountsCodeValidation extends GenericValidation {

    private CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument;
    private BusinessObjectService businessObjectService;

    public boolean validate(AttributedDocumentEvent event) {
    
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        String billByChartOfAccountCode = customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBillByChartOfAccountCode();
        String billedByOrganizationCode = customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBilledByOrganizationCode();
        
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("chartOfAccountsCode", billByChartOfAccountCode);
        criteria.put("organizationCode", billedByOrganizationCode);
        
        OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
        
        if (ObjectUtils.isNull(organizationAccountingDefault)){
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_MUST_EXIST,new String[]{currentFiscalYear.toString(),billByChartOfAccountCode,billedByOrganizationCode});
            return false;
        } else {
            if (StringUtils.isEmpty(organizationAccountingDefault.getWriteoffChartOfAccountsCode())) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_CHART_MUST_EXIST, new String[] { organizationAccountingDefault.getUniversityFiscalYear().toString(), organizationAccountingDefault.getChartOfAccountsCode(), organizationAccountingDefault.getOrganizationCode() });
                return false;
            }
        }
        return true;
    }

    public CustomerInvoiceWriteoffDocument getCustomerInvoiceWriteoffDocument() {
        return customerInvoiceWriteoffDocument;
    }
    public void setCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        this.customerInvoiceWriteoffDocument = customerInvoiceWriteoffDocument;
    }
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
