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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserChartValueFinder;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserOrgValueFinder;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action Class for Contracts Grants Invoice Template Upload Action.
 */
public class AccountsReceivableInvoiceTemplateUploadAction extends KualiAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBatchInputFileSetAction.class);
    private static CurrentUserChartValueFinder currentUserChartValueFinder;
    private static CurrentUserOrgValueFinder currentUserOrgValueFinder;
    private ModuleConfiguration systemConfiguration;
    private ConfigurationService kualiConfigurationService;
    private BusinessObjectService boService;

    /**
     * Constructs a ContractsGrantsInvoiceTemplateUploadAction.java.
     */
    public AccountsReceivableInvoiceTemplateUploadAction() {
        super();
        boService = SpringContext.getBean(BusinessObjectService.class);
        kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-CG").getModuleConfiguration();
        currentUserOrgValueFinder = new CurrentUserOrgValueFinder();
        currentUserChartValueFinder = new CurrentUserChartValueFinder();
    }

    /**
     * Gets the boService attribute.
     * 
     * @return Returns the boService.
     */
    public BusinessObjectService getBoService() {
        return boService;
    }

    /**
     * Sets the boService attribute value.
     * 
     * @param boService The boService to set.
     */
    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm,
     *      java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

    }

    /**
     * Forwards to the batch upload JSP. Initial request.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sends the uploaded file contents to the templateService for storage. If errors were encountered, messages will be in
     * GlobalVariables.errorMap, which is checked and set for display by the request processor.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsReceivableInvoiceTemplateUploadForm newForm = (AccountsReceivableInvoiceTemplateUploadForm) form;
        FormFile uploadedFile = newForm.getUploadedFile();

        // validations performed on the required values for saving the template
        boolean isRequiredValuesForFilesMissing = false;
        InvoiceTemplate document = boService.findBySinglePrimaryKey(InvoiceTemplate.class, newForm.getInvoiceTemplateCode());

        // check uploaded file
        if (uploadedFile == null || uploadedFile.getInputStream() == null || uploadedFile.getInputStream().available() == 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Please check the Template being uploaded.");
            isRequiredValuesForFilesMissing = true;
        }

        // check template code for null and being empty, and check if org code and COAcode exists for the template
        if (ObjectUtils.isNull(newForm.getInvoiceTemplateCode()) || StringUtils.isEmpty(newForm.getInvoiceTemplateCode())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Please select an Invoice Type.");
            isRequiredValuesForFilesMissing = true;
        }
        else {
            if (ObjectUtils.isNotNull(document)) {
                if (ObjectUtils.isNull(document.getBillByChartOfAccountCode()) || ObjectUtils.isNull(document.getBilledByOrganizationCode())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Current User is not authorized to upload Template");
                    isRequiredValuesForFilesMissing = true;
                }
                else if (!document.getBillByChartOfAccountCode().equals(currentUserChartValueFinder.getValue()) || !document.getBilledByOrganizationCode().equals(currentUserOrgValueFinder.getValue())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Current User is not authorized to upload Template");
                    isRequiredValuesForFilesMissing = true;
                }
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Invoice Document Not Available.");
                isRequiredValuesForFilesMissing = true;
            }
        }
        // check for valid pdf template file
        if (!"application/pdf".equals(URLConnection.guessContentTypeFromName(uploadedFile.getFileName()))) {
            GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, "Please upload a valid file type.");
            isRequiredValuesForFilesMissing = true;
        }
        if (isRequiredValuesForFilesMissing) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // set filename for the template
        document.setFilename("Invoice_Template_" + newForm.getInvoiceTemplateCode().replaceAll("[\\/]", "-") + ".pdf");
        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
        /*
         * Uncomment this section if the above property is working... String destinationFolderPath =
         * kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMPLATES_DIRECTORY_KEY)+"/cg";
         */
        String destinationPath = destinationFolderPath + File.separator + document.getFilename();
        File destinationFolder = new File(destinationFolderPath);
        File destinationFile = new File(destinationPath);
        // upload the file and save the document
        try {
            if (!destinationFolder.exists())
                destinationFolder.mkdirs();
            if (destinationFile.exists())
                destinationFile.delete();
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.writeInputStreamToFileStorage(uploadedFile.getInputStream(), destinationFile);
            document.setFilepath(destinationFile.getAbsolutePath());
            document.setDate(sdf.format(dt));
            boService.save(document);
        }
        catch (FileStorageException e) {
            LOG.error("Error occured while trying to save file set (probably tried to save a file that already exists).", e);
            GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM, new String[] { e.getMessage() });
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        catch (ValidationException e) {
            LOG.error("Error occured while trying to validate file set.", e);
            GlobalVariables.getMessageMap().putError(KFSConstants.INVOICE_TEMPLATE_UPLOAD, KFSKeyConstants.ERROR_CUSTOM);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        AccountsReceivableInvoiceTemplateUploadForm fileAdminForm = (AccountsReceivableInvoiceTemplateUploadForm) form;
        String filePath = fileAdminForm.getFilePath();
        File file = new File(filePath).getAbsoluteFile();
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength((int) file.length());
        InputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, response.getOutputStream());
        response.getOutputStream().flush();
        return null;
    }

    /**
     * This method writes the contents from the input stream to the destination storage place.
     * 
     * @param fileContents
     * @param destinationFile
     * @throws IOException
     */
    private void writeInputStreamToFileStorage(InputStream fileContents, File destinationFile) throws IOException {
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(destinationFile);
            bufferedStreamOut = new BufferedOutputStream(streamOut);
            int c;
            while ((c = fileContents.read()) != -1) {
                bufferedStreamOut.write(c);
            }
        }
        finally {
            bufferedStreamOut.close();
            streamOut.close();
        }
    }
}
