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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.service.BatchInputFileSetService;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class is the struts action for the batch upload screen that supports file sets
 */
public class KualiBatchInputFileSetAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBatchInputFileSetAction.class);
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
        setupForm((KualiBatchInputFileSetForm) form);
        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        BatchUpload batchUpload = ((KualiBatchInputFileSetForm) form).getBatchUpload();
        BatchInputFileSetType batchInputFileType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
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
        BatchUpload batchUpload = ((KualiBatchInputFileSetForm) form).getBatchUpload();
        BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sends the uploaded file contents, requested file name, and batch type to the BatchInputTypeService for storage. If errors
     * were encountered, messages will be in GlobalVariables.errorMap, which is checked and set for display by the request
     * processor.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchUpload batchUpload = ((KualiBatchInputFileSetForm) form).getBatchUpload();
        BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());

        boolean requiredValuesForFilesMissing = false;
        if (StringUtils.isBlank(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SET_IDENTIFIER_SELECTED, new String[] {});
            requiredValuesForFilesMissing = true;
        }

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            requiredValuesForFilesMissing = true;
        }

        Map<String, FormFile> uploadedFiles = ((KualiBatchInputFileSetForm) form).getUploadedFiles();
        Map<String, InputStream> typeToStreamMap = new HashMap<String, InputStream>();

        for (String fileType : uploadedFiles.keySet()) {
            FormFile uploadedFile = uploadedFiles.get(fileType);
            if (uploadedFile == null || uploadedFile.getInputStream() == null || uploadedFile.getInputStream().available() == 0) {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE_FOR_FILE_TYPE, new String[] { batchType.getFileTypeDescription().get(fileType) });
                requiredValuesForFilesMissing = true;
            }
            else {
                typeToStreamMap.put(fileType, uploadedFile.getInputStream());
            }
        }

        if (requiredValuesForFilesMissing) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        try {
            Map<String, String> typeToSavedFileNames = batchInputFileSetService.save(GlobalVariables.getUserSession().getPerson(), batchType, batchUpload.getFileUserIdentifer(), typeToStreamMap);
        }
        catch (FileStorageException e) {
            LOG.error("Error occured while trying to save file set (probably tried to save a file that already exists).", e);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SAVE_ERROR, new String[] { e.getMessage() });
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        catch (ValidationException e) {
            LOG.error("Error occured while trying to validate file set.", e);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_VALIDATION_ERROR);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves a BatchInputFileType implementation from Spring based on the given name.
     */
    public BatchInputFileSetType retrieveBatchInputFileSetTypeImpl(String batchInputTypeName) {
        BatchInputFileSetType batchInputType = BatchSpringContext.getBatchInputFileSetType(batchInputTypeName);
        if (batchInputType == null) {
            LOG.error("Batch input type implementation not found for id " + batchInputTypeName);
            throw new RuntimeException(("Batch input type implementation not found for id " + batchInputTypeName));
        }

        return batchInputType;
    }

    /**
     * Builds list of filenames that the user has permission to manage, and populates the form member. Throws an exception if the
     * batch file set type is not active. Sets the title key from the batch input type. This method must be called before the action
     * handler to ensure proper authorization.
     */
    public void setupForm(KualiBatchInputFileSetForm form) {
        BatchInputFileSetType batchInputFileSetType = retrieveBatchInputFileSetTypeImpl(form.getBatchUpload().getBatchInputTypeName());

        if (batchInputFileSetType == null) {
            LOG.error("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
            throw new RuntimeException("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
        }

        if (!SpringContext.getBean(BatchInputFileSetService.class).isBatchInputTypeActive(batchInputFileSetType)) {
            throw new RuntimeException("Batch input file set type is not active.");
        }
        form.setBatchInputFileSetType(batchInputFileSetType);

        // set title key
        form.setTitleKey(batchInputFileSetType.getTitleKey());
    }

}

