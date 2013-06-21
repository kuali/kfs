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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ReceivableAccountingLineService;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class ReceivableAccountingLineServiceImpl implements ReceivableAccountingLineService {

    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.impl.ReceivableAccountingLineService#setReceivableAccountingLineForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setReceivableAccountingLineForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {

        Integer currentUniverisityFiscalYear = universityDateService.getCurrentFiscalYear();
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);

        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentUniverisityFiscalYear);
        criteria.put("chartOfAccountsCode", currentUser.getChartOfAccountsCode());
        criteria.put("organizationCode", currentUser.getOrganizationCode());

        OrganizationAccountingDefault organizationAccountingDefault = businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
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

