/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.web.struts.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.batch.BatchSpringContext;
import org.kuali.kfs.bo.BatchUpload;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.service.BatchInputFileSetService;
import org.kuali.kfs.web.struts.form.KualiBatchInputFileSetForm;

/**
 * This class is the struts action for the batch upload screen that supports file sets
 */
public class KualiBatchInputFileSetAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBatchInputFileSetAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        setupForm((KualiBatchInputFileSetForm) form);
        return forward;
    }

    /**
     * First verifies the user is active for the module in which the batch input type is in, then calls batch input service to
     * authorize the user for the specific batch input type.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        BatchUpload batchUpload = ((KualiBatchInputFileSetForm) form).getBatchUpload();
        BatchInputFileSetType batchInputFileType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());

        AuthorizationType defaultAuthorizationType = new AuthorizationType.Default(batchInputFileType.getClass());
        if (!getKualiModuleService().isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), defaultAuthorizationType)) {
            LOG.error("User not authorized for lookup action for this object: " + batchInputFileType.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), defaultAuthorizationType, getKualiModuleService().getResponsibleModule(batchInputFileType.getClass()));
        }

        boolean isAuthorizedForType = SpringContext.getBean(BatchInputFileSetService.class).isUserAuthorizedForBatchType(batchInputFileType, GlobalVariables.getUserSession().getUniversalUser());
        if (!isAuthorizedForType) {
            LOG.error("User " + GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier() + " is not authorized for batch type " + batchInputFileType.getFileSetTypeIdentifer());
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), "upload", batchInputFileType.getFileSetTypeIdentifer());
        }
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
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SET_IDENTIFIER_SELECTED, new String[] {});
            requiredValuesForFilesMissing = true;
        }

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            requiredValuesForFilesMissing = true;
        }

        Map<String, FormFile> uploadedFiles = ((KualiBatchInputFileSetForm) form).getUploadedFiles();
        Map<String, InputStream> typeToStreamMap = new HashMap<String, InputStream>();

        for (String fileType : uploadedFiles.keySet()) {
            FormFile uploadedFile = uploadedFiles.get(fileType);
            if (uploadedFile == null || uploadedFile.getInputStream() == null || uploadedFile.getInputStream().available() == 0) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE_FOR_FILE_TYPE, new String[] { batchType.getFileTypeDescription().get(fileType) });
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
            Map<String, String> typeToSavedFileNames = batchInputFileSetService.save(GlobalVariables.getUserSession().getUniversalUser(), batchType, batchUpload.getFileUserIdentifer(), typeToStreamMap, ((KualiBatchInputFileSetForm) form).isSupressDoneFileCreation());
        }
        catch (FileStorageException e) {
            LOG.error("Error occured while trying to save file set (probably tried to save a file that already exists).", e);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SAVE_ERROR, new String[] { e.getMessage() });
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        catch (ValidationException e) {
            LOG.error("Error occured while trying to validate file set.", e);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_VALIDATION_ERROR);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes an existing batch file set. If errors were encountered, messages will be in GlobalVariables.errorMap, which is
     * checked and set for display by the request processor.
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchInputFileSetForm kualiBatchInputFileSetForm = (KualiBatchInputFileSetForm) form;
        BatchUpload batchUpload = kualiBatchInputFileSetForm.getBatchUpload();

        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DELETE, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        try {
            boolean deleteSuccessful = batchInputFileSetService.delete(GlobalVariables.getUserSession().getUniversalUser(), batchType, batchUpload.getExistingFileName());

            if (deleteSuccessful) {
                GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_DELETE_SUCCESSFUL);
            }
        }
        catch (FileNotFoundException e1) {
            LOG.error("errors deleting file " + e1.getMessage(), e1);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_DELETE, new String[] { e1.getMessage() });
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves the contents of an uploaded batch file. If errors were encountered, messages will be in GlobalVariables.errorMap,
     * which is checked and set for display by the request processor.
     */
    public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchInputFileSetForm kualiBatchInputFileSetForm = (KualiBatchInputFileSetForm) form;
        BatchUpload batchUpload = kualiBatchInputFileSetForm.getBatchUpload();

        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DOWNLOAD, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        if (StringUtils.isBlank(kualiBatchInputFileSetForm.getDownloadFileType())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_TYPE_SELECTED_DOWNLOAD, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        File batchInputFile = null;
        try {
            batchInputFile = batchInputFileSetService.download(GlobalVariables.getUserSession().getUniversalUser(), batchType, kualiBatchInputFileSetForm.getDownloadFileType(), batchUpload.getExistingFileName());
        }
        catch (FileNotFoundException e1) {
            LOG.error("errors downloading file " + e1.getMessage(), e1);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_DOWNLOAD, new String[] { e1.getMessage() });

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        WebUtils.saveMimeInputStreamAsFile(response, "text/plain", new FileInputStream(batchInputFile), batchInputFile.getName(), new Long(batchInputFile.length()).intValue());

        return null;
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
    private void setupForm(KualiBatchInputFileSetForm form) {
        List<KeyLabelPair> userFiles = new ArrayList<KeyLabelPair>();

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        
        
        BatchInputFileSetType batchInputFileSetType = retrieveBatchInputFileSetTypeImpl(form.getBatchUpload().getBatchInputTypeName());

        if (batchInputFileSetType == null) {
            LOG.error("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
            throw new RuntimeException("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
        }

        if (!SpringContext.getBean(BatchInputFileSetService.class).isBatchInputTypeActive(batchInputFileSetType)) {
            throw new RuntimeException("Batch input file set type is not active.");
        }
        form.setBatchInputFileSetType(batchInputFileSetType);

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        Set<String> fileUserIdentifiers = batchInputFileSetService.listBatchTypeFileUserIdentifiersForUser(batchInputFileSetType, user);

        userFiles.add(new KeyLabelPair("", "Select a File Set Identifier"));
        for (String fileUserIdentifier : fileUserIdentifiers) {
            String label = fileUserIdentifier;
            if (batchInputFileSetService.hasBeenProcessed(user, batchInputFileSetType, fileUserIdentifier)) {
                label = label + " (processed)";
            }
            else {
                label = label + " (ready to process)";
            }
            userFiles.add(new KeyLabelPair(fileUserIdentifier, label));
        }
        form.setFileUserIdentifiers(userFiles);

        List<KeyLabelPair> fileTypes = new ArrayList<KeyLabelPair>();
        fileTypes.add(new KeyLabelPair("", "Select a file type to download"));
        for (String fileAlias : batchInputFileSetType.getFileTypes()) {
            fileTypes.add(new KeyLabelPair(fileAlias, batchInputFileSetType.getFileTypeDescription().get(fileAlias)));
        }
        form.setFileTypes(fileTypes);

        // set title key
        form.setTitleKey(batchInputFileSetType.getTitleKey());
    }

}
