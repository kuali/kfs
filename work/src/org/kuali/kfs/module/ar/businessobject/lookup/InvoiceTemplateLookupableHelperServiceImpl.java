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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Helper service class for Invoice Template lookup
 */
public class InvoiceTemplateLookupableHelperServiceImpl extends TemplateLookupableHelperServiceImplBase {

    protected FinancialSystemUserService financialSystemUserService;

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        boolean isValid = false;
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        // check for KFS-SYS User Role's membership
        final ChartOrgHolder chartOrg = getFinancialSystemUserService().getPrimaryOrganization(currentUser, ArConstants.AR_NAMESPACE_CODE);

        if (ObjectUtils.isNotNull(invoiceTemplate.getBillByChartOfAccountCode()) && ObjectUtils.isNotNull(invoiceTemplate.getBilledByOrganizationCode())) {
            if (StringUtils.equals(invoiceTemplate.getBillByChartOfAccountCode(), chartOrg.getChartOfAccountsCode()) && StringUtils.equals(invoiceTemplate.getBilledByOrganizationCode(), chartOrg.getOrganizationCode())) {
                isValid = true;
            }
        }

        if (isValid) {
            if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
            }
            if (allowsMaintenanceNewOrCopyAction()) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
            }
            if (isTemplateValidForUser(invoiceTemplate, currentUser)) {
                htmlDataList.add(getTemplateUploadUrl(ArPropertyConstants.InvoiceTemplateFields.INVOICE_TEMPLATE_CODE, invoiceTemplate.getInvoiceTemplateCode()));
            }
            if (StringUtils.isNotBlank(invoiceTemplate.getFilename()) && templateFileExists(invoiceTemplate.getFilename())) {
                htmlDataList.add(getTemplateDownloadUrl(invoiceTemplate.getFilename()));
            }
        }

        return htmlDataList;
    }

    /**
     * Determines if the given invoice template can be utilized by the given current user
     *
     * @param invoiceTemplate the invoice template to check
     * @param user the user to check if they can utilize the template
     * @return true if the user can utilize the template, false otherwise
     */
    protected boolean isTemplateValidForUser(InvoiceTemplate invoiceTemplate, Person user) {
        final ChartOrgHolder userChartOrg = getFinancialSystemUserService().getPrimaryOrganization(user, ArConstants.AR_NAMESPACE_CODE);

        if (!StringUtils.isBlank(invoiceTemplate.getBillByChartOfAccountCode()) && !StringUtils.isBlank(invoiceTemplate.getBilledByOrganizationCode())) {
            return StringUtils.equals(invoiceTemplate.getBillByChartOfAccountCode(), userChartOrg.getChartOfAccountsCode()) && StringUtils.equals(invoiceTemplate.getBilledByOrganizationCode(),userChartOrg.getOrganizationCode());
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.businessobject.lookup.TemplateLookupableHelperServiceImplBase#getAction()
     */
    @Override
    protected String getAction() {
        return ArConstants.UrlActions.ACCOUNTS_RECEIVABLE_INVOICE_TEMPLATE_UPLOAD;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

}
