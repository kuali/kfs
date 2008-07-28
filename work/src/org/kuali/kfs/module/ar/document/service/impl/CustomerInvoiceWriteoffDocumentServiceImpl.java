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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;

public class CustomerInvoiceWriteoffDocumentServiceImpl implements CustomerInvoiceWriteoffDocumentService {
    
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private FinancialSystemUserService financialSystemUserService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService#setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument)
     */
    public void setupDefaultValuesForNewCustomerInvoiceWriteoffDocument( CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        
        //update status
        customerInvoiceWriteoffDocument.setStatusCode(ArConstants.CustomerInvoiceWriteoffStatuses.IN_PROCESS);
        
        //if writeoffs are generated based on organization accounting default, populate those fields now
        
        String writeoffGenerationOption = parameterService.getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        boolean isUsingOrgAcctDefaultWriteoffFAU = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals( writeoffGenerationOption ); 
        if( isUsingOrgAcctDefaultWriteoffFAU ){
            
            Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
            ChartOrgHolder currentUser = financialSystemUserService.getOrganizationByModuleId(KFSConstants.Modules.CHART);
            
            Map<String,Object> criteria = new HashMap<String,Object>();
            criteria.put("universityFiscalYear", currentUniversityFiscalYear);
            criteria.put("chartOfAccountsCode", currentUser.getChartOfAccountsCode());
            criteria.put("organizationCode", currentUser.getOrganizationCode());
            
            OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault)businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
            if( ObjectUtils.isNotNull( organizationAccountingDefault ) ){
                customerInvoiceWriteoffDocument.setChartOfAccountsCode(organizationAccountingDefault.getChartOfAccountsCode());
                customerInvoiceWriteoffDocument.setAccountNumber(organizationAccountingDefault.getWriteoffAccountNumber());
                customerInvoiceWriteoffDocument.setSubAccountNumber(organizationAccountingDefault.getWriteoffSubAccountNumber());
                customerInvoiceWriteoffDocument.setFinancialObjectCode(organizationAccountingDefault.getWriteoffFinancialObjectCode());
                customerInvoiceWriteoffDocument.setFinancialSubObjectCode(organizationAccountingDefault.getWriteoffFinancialSubObjectCode());
                customerInvoiceWriteoffDocument.setProjectCode(organizationAccountingDefault.getWriteoffProjectCode());
                customerInvoiceWriteoffDocument.setOrganizationReferenceIdentifier(organizationAccountingDefault.getWriteoffOrganizationReferenceIdentifier());
            }
        } 
    }
    
    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }    

}
