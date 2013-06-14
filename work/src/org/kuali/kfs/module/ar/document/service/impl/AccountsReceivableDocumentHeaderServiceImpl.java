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
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class AccountsReceivableDocumentHeaderServiceImpl implements AccountsReceivableDocumentHeaderService {

    private BusinessObjectService businessObjectService;
    private UniversityDateService universityDateService;
    private SystemInformationService sysInfoService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeader(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String chartOfAccountsCode, String organizationCode) {
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();

        //  we try to get the processing org directly, which we'll get if the initiating user is an AR Processor
        SystemInformation processingOrg = getProcessingOrgIfExists(chartOfAccountsCode, organizationCode);
        if (processingOrg != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(processingOrg.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(processingOrg.getProcessingOrganizationCode());
            return accountsReceivableDocumentHeader;
        }

        //  next we try to get the processing org through the initiating user's billing org, if that exists
        OrganizationOptions orgOptions = getOrgOptionsIfExists(chartOfAccountsCode, organizationCode);
        if (orgOptions != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(orgOptions.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgOptions.getProcessingOrganizationCode());
            return accountsReceivableDocumentHeader;
        }

        //  If we get here, then we didnt find a matching BillingOrg or ProcessingOrg, so we have
        // no way to retrieve the document processing org.  This should never happen if the authorization
        // is working right, so we're going to puke and die right here.
        throw new UnsupportedOperationException("Unable to create AR Document Header due to incomplete configuration. Missing or inactive SystemInformation and/or OrganizationOptions for: "+ chartOfAccountsCode +"-"+ organizationCode );
    }

    protected OrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    protected SystemInformation getProcessingOrgIfExists(String chartOfAccountsCode, String organizationCode) {
        return sysInfoService.getByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, universityDateService.getCurrentFiscalYear());
    }
    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeaderForCurrentUser()
     */
    @Override
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeaderForCurrentUser() {
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
        AccountsReceivableDocumentHeader arDocHeader = new AccountsReceivableDocumentHeader();
        arDocHeader.setProcessingChartOfAccountCode(currentUser.getChartOfAccountsCode());
        arDocHeader.setProcessingOrganizationCode(currentUser.getOrganizationCode());
        return arDocHeader;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setSysInfoService(SystemInformationService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

}
