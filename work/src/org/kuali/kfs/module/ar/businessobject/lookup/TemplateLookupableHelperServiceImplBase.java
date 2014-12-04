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

import java.io.File;
import java.util.Properties;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public abstract class TemplateLookupableHelperServiceImplBase extends KualiLookupableHelperServiceImpl {

    protected KualiModuleService kualiModuleService;

    /**
     * This method builds the upload URL for the template.
     *
     * @param templateCodeKey
     * @param templateCodeValue
     * @param action
     * @return
     */
    protected AnchorHtmlData getTemplateUploadUrl(String templateCodeKey, String templateCodeValue) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(templateCodeKey, templateCodeValue);
        parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
        String href = UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/" + getAction(), parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArConstants.UPLOAD_METHOD);
    }

    /**
     * Check if the template file actually exists, even though we have a fileName
     *
     * @param fileName name of template file
     * @return true if template file exists, false otherwise
     */
    protected boolean templateFileExists(String fileName) {
        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String templateFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
        String filePath = templateFolderPath + File.separator + fileName;
        File file = new File(filePath).getAbsoluteFile();
        return file.exists() && file.isFile();
    }

    /**
     * This method builds the download URL for the template.
     *
     * @param fileName
     * @param action
     * @return
     */
    protected AnchorHtmlData getTemplateDownloadUrl(String fileName) {
        Properties parameters = new Properties();
        if (ObjectUtils.isNotNull(fileName) && templateFileExists(fileName)) {
            parameters.put(KFSPropertyConstants.FILE_NAME, fileName);
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, ArConstants.DOWNLOAD_METHOD);
        }
        String href = UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/" + getAction(), parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArConstants.DOWNLOAD_METHOD);
    }

    /**
     * Return the action used to build the URLs for the template. Implemented by child classes.
     * @return
     */
    protected abstract String getAction();

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
