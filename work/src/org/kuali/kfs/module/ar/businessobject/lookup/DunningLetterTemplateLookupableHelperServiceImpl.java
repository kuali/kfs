/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class DunningLetterTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterTemplateLookupableHelperServiceImpl.class);

    private KualiModuleService kualiModuleService;

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (letterTemplate.isValidOrganization() || !letterTemplate.isAccessRestrictedInd()) {
            // can upload file and do changes.
            htmlDataList.add(getDunningLetterTemplateUploadUrl(businessObject));
        }
        if (StringUtils.isNotBlank(letterTemplate.getFilename()) && templateFileExists(letterTemplate.getFilename())) {
            htmlDataList.add(getDunningLetterTemplateDownloadUrl(businessObject));
        }
        return htmlDataList;
    }

    /**
     * This method helps in uploading the dunning letter templates.
     *
     * @param bo
     * @return
     */
    private AnchorHtmlData getDunningLetterTemplateUploadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        String href = "../arAccountsReceivableLetterTemplateUpload.do" + "?&methodToCall=start&letterTemplateCode=" + letterTemplate.getLetterTemplateCode() + "&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_UPLOAD);
    }

    /**
     * Check if the template file actually exists, even though we have a fileName
     *
     * @param fileName name of template file
     * @return true if template file exists, false otherwise
     */
    private boolean templateFileExists(String fileName) {
        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String templateFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
        String filePath = templateFolderPath + File.separator + fileName;
        File file = new File(filePath).getAbsoluteFile();
        if (file.exists() && file.isFile()) {
            return true;
        }

        return false;
    }

    /**
     * This method helps in downloading the dunning letter templates.
     *
     * @param bo
     * @return
     */
    private AnchorHtmlData getDunningLetterTemplateDownloadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        Properties parameters = new Properties();
        String fileName = letterTemplate.getFilename();
        if (ObjectUtils.isNotNull(fileName) && templateFileExists(fileName)) {
            parameters.put("fileName", fileName);
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "download");
        }
        String href = UrlFactory.parameterizeUrl("../arAccountsReceivableLetterTemplateUpload.do", parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_DOWNLOAD);
    }

    /**
     * Gets the kualiModuleService attribute.
     *
     * @return Returns the kualiModuleService
    */

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    /**
     * Sets the kualiModuleService attribute.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
