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

import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.OrganizationOptionsService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountsReceivableDocumentHeaderServiceImpl implements AccountsReceivableDocumentHeaderService {
    
    private OrganizationOptionsService organizationOptionsService;

    /**
     * @see org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeader(java.lang.String, java.lang.String)
     */
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String chartOfAccountsCode, String organizationCode) {
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        
        OrganizationOptions organizationOptions = organizationOptionsService.getByPrimaryKey(chartOfAccountsCode, organizationCode);
        if( organizationOptions != null ){
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(organizationOptions.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(organizationOptions.getProcessingOrganizationCode());
        }

        return accountsReceivableDocumentHeader;
    }

    /**
     * @see org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService#getNewAccountsReceivableDocumentHeaderForCurrentUser()
     */
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeaderForCurrentUser() {
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        return getNewAccountsReceivableDocumentHeader(currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    public OrganizationOptionsService getOrganizationOptionsService() {
        return organizationOptionsService;
    }

    public void setOrganizationOptionsService(OrganizationOptionsService organizationOptionsService) {
        this.organizationOptionsService = organizationOptionsService;
    }

}
