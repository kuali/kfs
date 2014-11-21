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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
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
    @SuppressWarnings("unchecked")
    public void setReceivableAccountingLineForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {

        Integer currentUniverisityFiscalYear = universityDateService.getCurrentFiscalYear();
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
        
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

