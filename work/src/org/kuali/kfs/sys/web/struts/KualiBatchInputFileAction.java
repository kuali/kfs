/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.web.struts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Handles actions from the batch upload screen.
 */
public class KualiBatchInputFileAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBatchInputFileAction.class);
    private static IdentityManagementService identityManagementService;
    private IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        setupForm((KualiBatchInputFileForm) form);
        return forward;
    }

    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        BatchUpload batchUpload = ((KualiBatchInputFileForm) form).getBatchUpload();
        BatchInputFileType batchInputFileType = retrieveBatchInputFileTypeImpl(batchUpload.getBatchInputTypeName());
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, getNamespaceCode(batchInputFileType.getClass()));
        permissionDetails.put(KimConstants.AttributeConstants.BEAN_NAME, batchUpload.getBatchInputTypeName());
        if (!getIdentityManagementService().isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KFSConstants.PermissionTemplate.UPLOAD_BATCH_INPUT_FILES.name, permissionDetails, null)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), methodToCall, batchUpload.getBatchInputTypeName());
        }
    }

    protected static String getNamespaceCode(Class<? extends Object> clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService == null) {
            return KimConstants.KIM_TYPE_DEFAULT_NAMESPACE;
        }
        return moduleService.getModuleConfiguration().getNamespaceCode();
    }

    /**
     * Forwards to the batch upload JSP. Initial request.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sends the uploaded file contents, requested file name, and batch type to the BatchInputTypeService for storage. If errors
     * were encountered, messages will be in GlobalVariables.errorMap, which is checked and set for display by the request
     * processor.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchUpload batchUpload = ((KualiBatchInputFileForm) form).getBatchUpload();
        BatchInputFileType batchType = retrieveBatchInputFileTypeImpl(batchUpload.getBatchInputTypeName());

        BatchInputFileService batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        FormFile uploadedFile = ((KualiBatchInputFileForm) form).getUploadFile();

        if (uploadedFile == null || uploadedFile.getInputStream() == null || uploadedFile.getInputStream().available() == 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        if (!batchInputFileService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_USER_IDENTIFIER_BAD_FORMAT, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        InputStream fileContents = ((KualiBatchInputFileForm) form).getUploadFile().getInputStream();
        byte[] fileByteContent = IOUtils.toByteArray(fileContents);
        
        Object parsedObject = null;
        try {
            parsedObject = batchInputFileService.parse(batchType, fileByteContent);
        }
        catch (ParseException e) {
            LOG.error("errors parsing xml " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e.getMessage() });
        }

        if (parsedObject != null && GlobalVariables.getMessageMap().hasNoErrors()) {
            boolean validateSuccessful = batchInputFileService.validate(batchType, parsedObject);

            if (validateSuccessful && GlobalVariables.getMessageMap().hasNoErrors()) {
                try {
                    InputStream saveStream = new ByteArrayInputStream(fileByteContent);

                    String savedFileName = batchInputFileService.save(GlobalVariables.getUserSession().getPerson(), batchType, batchUpload.getFileUserIdentifer(), saveStream, parsedObject);
                    KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);
                }
                catch (FileStorageException e1) {
                    LOG.error("errors saving xml " + e1.getMessage(), e1);
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { e1.getMessage() });
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves a BatchInputFileType implementation from Spring based on the given name.
     */
    private BatchInputFileType retrieveBatchInputFileTypeImpl(String batchInputTypeName) {
        BatchInputFileType batchInputType = BatchSpringContext.getBatchInputFileType(batchInputTypeName);
        if (batchInputType == null) {
            LOG.error("Batch input type implementation not found for id " + batchInputTypeName);
            throw new RuntimeException(("Batch input type implementation not found for id " + batchInputTypeName));
        }

        return batchInputType;
    }

    /**
     * Builds list of filenames that the user has permission to manage, and populates the form member. Sets the title key from the
     * batch input type.
     */
    private void setupForm(KualiBatchInputFileForm form) {
        BatchInputFileType batchInputFileType = retrieveBatchInputFileTypeImpl(form.getBatchUpload().getBatchInputTypeName());

        if (batchInputFileType == null) {
            LOG.error("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
            throw new RuntimeException(("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName()));
        }
        ParameterService parmeterService =  SpringContext.getBean(ParameterService.class);
        String url = parmeterService.getSubParameterValueAsString(BatchUpload.class,KFSConstants.BATCH_UPLOAD_HELP_SYS_PARAM_NAME, batchInputFileType.getFileTypeIdentifer());
        form.setUrl(url);
          // set title key
        form.setTitleKey(batchInputFileType.getTitleKey());
    }

}

