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
package org.kuali.module.ar.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.ReceivableAccountingLineService;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.service.UniversityDateService;

public class ReceivableAccountingLineServiceImpl implements ReceivableAccountingLineService {
    
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;

    public void setReceivableAccountingLineForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {

        Integer currentUniverisityFiscalYear = universityDateService.getCurrentFiscalYear();
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentUniverisityFiscalYear);
        criteria.put("chartOfAccountsCode", currentUser.getChartOfAccountsCode());
        criteria.put("organizationCode", currentUser.getOrganizationCode());
        
        OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault)businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
        if( ObjectUtils.isNotNull( organizationAccountingDefault) ){
            customerInvoiceDocument.setPaymentChartOfAccountsCode(organizationAccountingDefault.getDefaultPaymentChartOfAccountsCode());
            customerInvoiceDocument.setPaymentAccountNumber(organizationAccountingDefault.getDefaultPaymentAccountNumber());
            customerInvoiceDocument.setPaymentSubAccountNumber(organizationAccountingDefault.getDefaultPaymentSubAccountNumber());
            customerInvoiceDocument.setPaymentFinancialObjectCode(organizationAccountingDefault.getDefaultPaymentFinancialObjectCode());
            customerInvoiceDocument.setPaymentFinancialSubObjectCode(organizationAccountingDefault.getDefaultPaymentFinancialSubObjectCode());
            customerInvoiceDocument.setPaymentProjectCode(organizationAccountingDefault.getDefaultPaymentProjectCode());
            customerInvoiceDocument.setPaymentOrganizationReferenceIdentifier(organizationAccountingDefault.getDefaultPaymentOrganizationReferenceIdentifier());
        }
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
