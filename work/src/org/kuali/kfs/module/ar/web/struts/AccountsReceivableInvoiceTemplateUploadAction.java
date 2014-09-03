/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.TemplateBase;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Action Class for Accounts Receivable Invoice Template Upload Action.
 */
public class AccountsReceivableInvoiceTemplateUploadAction extends AccountsReceivableTemplateUploadAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableInvoiceTemplateUploadAction.class);

    @Override
    protected void performAdditionalAuthorizationChecks(TemplateBase template) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        final ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, ArConstants.AR_NAMESPACE_CODE);

        if (!StringUtils.equals(template.getBillByChartOfAccountCode(), chartOrg.getChartOfAccountsCode()) ||
                !StringUtils.equals(template.getBilledByOrganizationCode(), chartOrg.getOrganizationCode())) {
            GlobalVariables.getMessageMap().putError(ArConstants.INVOICE_TEMPLATE_UPLOAD, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_USER_NOT_AUTHORIZED);
        }
    }

}
