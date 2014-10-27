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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.TemplateBase;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Base class for Accounts Receivable Template Upload Actions.
 */
public class AccountsReceivableTemplateUploadAction extends KualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableTemplateUploadAction.class);

    private static volatile BusinessObjectService boService;
    private static volatile ConfigurationService kualiConfigurationService;
    private static volatile DateTimeService dateTimeService;
    private static volatile FinancialSystemModuleConfiguration financialSystemModuleConfiguration;

    /**
     * Forwards to the upload JSP. Initial request.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sends the uploaded file contents to the templateService for storage. If errors were encountered, messages will be in
     * GlobalVariables.errorMap, which is checked and set for display by the request processor.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsReceivableTemplateUploadForm newForm = (AccountsReceivableTemplateUploadForm)form;
        FormFile uploadedFile = newForm.getUploadedFile();

        // validations performed on the required values for saving the template
        String templateCode = newForm.getTemplateCode();
        TemplateBase template = getBoService().findBySinglePrimaryKey(newForm.getTemplateClass(), templateCode);
        String errorPropertyName = newForm.getErrorPropertyName();
        String templateType = newForm.getTemplateType();

        // check uploaded file
        if (ObjectUtils.isNull(uploadedFile) || ObjectUtils.isNull(uploadedFile.getInputStream()) || uploadedFile.getInputStream().available() == 0) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_NO_TEMPLATE);
        } else if (!StringUtils.equals(KFSConstants.ReportGeneration.PDF_MIME_TYPE, URLConnection.guessContentTypeFromName(uploadedFile.getFileName()))) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_INVALID_FILE_TYPE);
        }

        // check template code for null and being empty, and check if org code and COAcode exists for the template
        if (StringUtils.isBlank(templateCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_NO_TEMPLATE_TYPE);
        } else {
            if (ObjectUtils.isNotNull(template)) {
                if (StringUtils.isBlank(template.getBillByChartOfAccountCode()) || StringUtils.isBlank(template.getBilledByOrganizationCode())) {
                    GlobalVariables.getMessageMap().putError(errorPropertyName, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_USER_NOT_AUTHORIZED);
                } else {
                    performAdditionalAuthorizationChecks(template);
                }
            } else {
                GlobalVariables.getMessageMap().putError(errorPropertyName, ArKeyConstants.TemplateUploadErrors.ERROR_TEMPLATE_UPLOAD_TEMPLATE_NOT_AVAILABLE);
            }
        }

        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            // set filename for the template
            template.setFilename(newForm.getNewFileNamePrefix() + newForm.getTemplateCode().replaceAll("[\\/]", "-") + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

            String destinationFolderPath = getFinancialSystemModuleConfiguration().getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
            String destinationPath = destinationFolderPath + File.separator + template.getFilename();
            File destinationFolder = new File(destinationFolderPath);
            File destinationFile = new File(destinationPath);

            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            if (destinationFile.exists()) {
                destinationFile.delete();
            }
            SimpleDateFormat sdf = new SimpleDateFormat(ArConstants.YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS_DATE_FORMAT);
            writeInputStreamToFileStorage(uploadedFile.getInputStream(), destinationFile);
            template.setUploadDate(getDateTimeService().getCurrentTimestamp());
            boService.save(template);
            KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Can be overridden in order to perform additional checks and indicate if the validation failed or not.
     */
    protected void performAdditionalAuthorizationChecks(TemplateBase template) {
        // nothing to do here, move along
    }

    /**
     * This method enables user to download the template
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsReceivableTemplateUploadForm newForm = (AccountsReceivableTemplateUploadForm)form;
        String templateFolderPath = getFinancialSystemModuleConfiguration().getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
        String filePath = templateFolderPath + File.separator + newForm.getFileName();
        File file = new File(filePath).getAbsoluteFile();
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }

        WebUtils.saveMimeInputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, new FileInputStream(file), file.getName(), new BigDecimal(file.length()).intValueExact());

        return null;
    }

    /**
     * This method writes the contents from the input stream to the destination storage place.
     *
     * @param fileContents
     * @param destinationFile
     * @throws IOException
     */
    protected void writeInputStreamToFileStorage(InputStream fileContents, File destinationFile) throws IOException {
        FileOutputStream streamOut = null;
        try {
            streamOut = new FileOutputStream(destinationFile);
            IOUtils.copy(fileContents, streamOut);
        }
        finally {
            if (streamOut != null) {
                streamOut.close();
            }
        }
    }

    /**
     * Gets the boService attribute.
     *
     * @return Returns the boService.
     */
    public BusinessObjectService getBoService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     *
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        if (kualiConfigurationService == null) {
            kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return kualiConfigurationService;
    }

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    public FinancialSystemModuleConfiguration getFinancialSystemModuleConfiguration() {
        if (financialSystemModuleConfiguration == null) {
            financialSystemModuleConfiguration = (FinancialSystemModuleConfiguration)SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        }
        return financialSystemModuleConfiguration;
    }

}
