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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;

public class AccountsReceivableDocumentHeaderServiceImpl implements AccountsReceivableDocumentHeaderService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeader(java.lang.String,
     *      java.lang.String)
     */
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String chartOfAccountsCode, String organizationCode) {
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);
        
        if (organizationOptions != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(organizationOptions.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(organizationOptions.getProcessingOrganizationCode());
        }

        return accountsReceivableDocumentHeader;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeaderForCurrentUser()
     */
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeaderForCurrentUser() {
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        return getNewAccountsReceivableDocumentHeader(currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
