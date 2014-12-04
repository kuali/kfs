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
