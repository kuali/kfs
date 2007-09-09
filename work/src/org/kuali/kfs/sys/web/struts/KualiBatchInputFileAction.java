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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.batch.BatchSpringContext;
import org.kuali.kfs.bo.BatchUpload;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.exceptions.XMLParseException;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.kfs.util.KFSUtils;
import org.kuali.kfs.web.struts.form.KualiBatchInputFileForm;

/**
 * Handles actions from the batch upload screen.
 */
public class KualiBatchInputFileAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiBatchInputFileAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        setupForm((KualiBatchInputFileForm) form);
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
        BatchUpload batchUpload = ((KualiBatchInputFileForm) form).getBatchUpload();
        BatchInputFileType batchInputFileType = retrieveBatchInputFileTypeImpl(batchUpload.getBatchInputTypeName());

        AuthorizationType defaultAuthorizationType = new AuthorizationType.Default(batchInputFileType.getClass());
        if (!getKualiModuleService().isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), defaultAuthorizationType)) {
            LOG.error("User not authorized for lookup action for this object: " + batchInputFileType.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), defaultAuthorizationType, getKualiModuleService().getResponsibleModule(batchInputFileType.getClass()));
        }

        boolean isAuthorizedForType = SpringContext.getBean(BatchInputFileService.class).isUserAuthorizedForBatchType(batchInputFileType, GlobalVariables.getUserSession().getUniversalUser());
        if (!isAuthorizedForType) {
            LOG.error("User " + GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier() + " is not authorized for batch type " + batchInputFileType.getFileTypeIdentifer());
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), "upload", batchInputFileType.getFileTypeIdentifer());
        }
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
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        if (!batchInputFileService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_USER_IDENTIFIER_BAD_FORMAT, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        InputStream fileContents = ((KualiBatchInputFileForm) form).getUploadFile().getInputStream();
        byte[] fileByteContent = IOUtils.toByteArray(fileContents);
        
        Object parsedObject = null;
        try {
            parsedObject = batchInputFileService.parse(batchType, fileByteContent);
        }
        catch (XMLParseException e) {
            LOG.error("errors parsing xml " + e.getMessage(), e);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e.getMessage() });
        }

        if (parsedObject != null && GlobalVariables.getErrorMap().isEmpty()) {
            boolean validateSuccessful = batchInputFileService.validate(batchType, parsedObject);

            if (validateSuccessful && GlobalVariables.getErrorMap().isEmpty()) {
                try {
                    InputStream saveStream = new ByteArrayInputStream(fileByteContent);

                    String savedFileName = batchInputFileService.save(GlobalVariables.getUserSession().getUniversalUser(), batchType, batchUpload.getFileUserIdentifer(), saveStream, parsedObject);
                    GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL);
                }
                catch (FileStorageException e1) {
                    LOG.error("errors saving xml " + e1.getMessage(), e1);
                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { e1.getMessage() });
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes an existing batch file. If errors were encountered, messages will be in GlobalVariables.errorMap, which is checked
     * and set for display by the request processor.
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchInputFileForm kualiBatchInputFileForm = (KualiBatchInputFileForm) form;
        BatchUpload batchUpload = kualiBatchInputFileForm.getBatchUpload();
        
        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DELETE, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileType batchType = retrieveBatchInputFileTypeImpl(batchUpload.getBatchInputTypeName());
        try {
            boolean deleteSuccessful = SpringContext.getBean(BatchInputFileService.class).delete(GlobalVariables.getUserSession().getUniversalUser(), batchType, batchUpload.getExistingFileName());

            if (deleteSuccessful) {
                GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_BATCH_UPLOAD_DELETE_SUCCESSFUL);
            }
            // if not successful, the delete method is responsible for populating the error map with the reason why deletion failed
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
        KualiBatchInputFileForm kualiBatchInputFileForm = (KualiBatchInputFileForm) form;
        BatchUpload batchUpload = kualiBatchInputFileForm.getBatchUpload();

        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DOWNLOAD, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        BatchInputFileType batchType = retrieveBatchInputFileTypeImpl(batchUpload.getBatchInputTypeName());
        File batchInputFile = null;
        try {
            batchInputFile = SpringContext.getBean(BatchInputFileService.class).download(GlobalVariables.getUserSession().getUniversalUser(), batchType, batchUpload.getExistingFileName());
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
        List<KeyLabelPair> userFiles = new ArrayList();

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        BatchInputFileType batchInputFileType = retrieveBatchInputFileTypeImpl(form.getBatchUpload().getBatchInputTypeName());

        if (batchInputFileType == null) {
            LOG.error("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName());
            throw new RuntimeException(("Batch input type implementation not found for id " + form.getBatchUpload().getBatchInputTypeName()));
        }
        
        BatchInputFileService batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        List<String> userFileNames = batchInputFileService.listBatchTypeFilesForUser(batchInputFileType, user);
        
        userFiles.add(new KeyLabelPair("", ""));
        for (int i = 0; i < userFileNames.size(); i++) {
            String fileName = userFileNames.get(i);
            // do NOT expose the full path name to the browser
            String key = fileName;
            String label = fileName;
            if (batchInputFileService.hasBeenProcessed(batchInputFileType, fileName)) {
                label = label + " (processed)";
            }
            else {
                label = label + " (ready to process)";
            }
            userFiles.add(new KeyLabelPair(key, label));
        }

        form.setUserFiles(userFiles);

        // set title key
        form.setTitleKey(batchInputFileType.getTitleKey());
    }

}
