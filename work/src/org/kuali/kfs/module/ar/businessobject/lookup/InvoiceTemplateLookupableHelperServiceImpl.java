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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Helper service class for Invoice Template lookup
 */
public class InvoiceTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceTemplateLookupableHelperServiceImpl.class);

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
        boolean isValid = true;

        if (invoiceTemplate.isAccessRestrictedIndicator()) {
            Person currentUser = GlobalVariables.getUserSession().getPerson();
            // check for KFS-SYS User Role's membership
            Map<String, String> userOrg = getOrgAndChartForUser(currentUser.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE);
            if(userOrg == null) {
                userOrg = getOrgAndChartForUser(currentUser.getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS);
                if(userOrg == null) {
                  ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE);
                  userOrg =new HashMap<String, String>();
                  userOrg.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
                  userOrg.put(KfsKimAttributes.ORGANIZATION_CODE,chartOrg.getOrganizationCode());
                }
            }

            if (ObjectUtils.isNotNull(invoiceTemplate.getBillByChartOfAccountCode()) && ObjectUtils.isNotNull(invoiceTemplate.getBilledByOrganizationCode())) {
                if (invoiceTemplate.getBillByChartOfAccountCode().equals(userOrg.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) && invoiceTemplate.getBilledByOrganizationCode().equals(userOrg.get(KfsKimAttributes.ORGANIZATION_CODE))) {
                    isValid = true;
                }
                isValid = false;
            }
        }

        if (isValid) {
            if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
            }
            if (allowsMaintenanceNewOrCopyAction()) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
            }
            if (invoiceTemplate.isValidOrganization()) {
                htmlDataList.add(getInvoiceTemplateUploadUrl(businessObject));
            }
            if (ObjectUtils.isNotNull(invoiceTemplate.getFilename()) && StringUtils.isNotBlank(invoiceTemplate.getFilename())) {
                htmlDataList.add(getInvoiceTemplateDownloadUrl(businessObject));
            }
        }
        return htmlDataList;
    }

    /**
     * This method helps in uploading the invoice templates.
     *
     * @param bo
     * @return
     */
    private AnchorHtmlData getInvoiceTemplateUploadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        String href = "../arAccountsReceivableInvoiceTemplateUpload.do" + "?&methodToCall=start&invoiceTemplateCode=" + invoiceTemplate.getInvoiceTemplateCode() + "&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_UPLOAD);
    }

    /**
     * This method helps in downloading the invoice templates.
     *
     * @param bo
     * @return
     */
    private AnchorHtmlData getInvoiceTemplateDownloadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        Properties parameters = new Properties();
        if (ObjectUtils.isNotNull(invoiceTemplate.getFilename()) && ObjectUtils.isNotNull(invoiceTemplate.getFilepath())) {
            parameters.put("filePath", invoiceTemplate.getFilepath());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "download");
        }
        String href = UrlFactory.parameterizeUrl("../arAccountsReceivableInvoiceTemplateUpload.do", parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_DOWNLOAD);
    }

    private Map<String, String> getOrgAndChartForUser(String principalId, String namespaceCode) {
        Map<String, String> chartAndOrg = new HashMap<String, String>();
        if (StringUtils.isBlank(principalId)) {
            return null;
        }
        Map<String, String> qualification = new HashMap<String, String>(2);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        RoleService roleService = KimApiServiceLocator.getRoleService();
        List<Map<String, String>> roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            int count = 0;
            while (count < roleQualifiers.size()) {
                if (!StringUtils.isBlank(roleQualifiers.get(count).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) && !StringUtils.isBlank(roleQualifiers.get(count).get(KfsKimAttributes.ORGANIZATION_CODE))) {
                    chartAndOrg.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE,roleQualifiers.get(count).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
                    chartAndOrg.put(KfsKimAttributes.ORGANIZATION_CODE,roleQualifiers.get(count).get(KfsKimAttributes.ORGANIZATION_CODE));
                    return chartAndOrg;
                }
                count += 1;
            }
        }
        return null;
    }
}
