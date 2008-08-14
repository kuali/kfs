/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.web.struts;

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
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.batch.AssetBarcodeInventoryInputFileType;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryInputFileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.service.BatchInputFileSetService;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction;
import org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetForm;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.KeyLabelPair;


/**
 * 
 * Action class for the CAMS Barcode Inventory upload. 
 */
public class AssetBarCodeInventoryInputFileAction extends KualiBatchInputFileSetAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarCodeInventoryInputFileAction.class);

    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * 
     * Overridden because I needed to validate the file type is correct.
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchUpload batchUpload = ((AssetBarCodeInventoryInputFileForm) form).getBatchUpload();
        //BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        AssetBarcodeInventoryInputFileType batchType = (AssetBarcodeInventoryInputFileType)retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        
        Map<String, FormFile> uploadedFiles = ((KualiBatchInputFileSetForm) form).getUploadedFiles();
        
        String fileTypeExtension = ((AssetBarcodeInventoryInputFileType)batchType).getFileExtension();
        String fileName = uploadedFiles.get(fileTypeExtension.substring(1)).getFileName();
            
        //Validating the file type is the correct one before saving.
        if (!StringUtils.isBlank(fileName) && !fileName.endsWith(fileTypeExtension)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FILE_TYPE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }            
        
        //return super.save(mapping, form, request, response);
        
        String uploadDescription = ((AssetBarCodeInventoryInputFileForm) form).getUploadDescription();

        boolean requiredValuesForFilesMissing = false;
        if (StringUtils.isBlank(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SET_IDENTIFIER_SELECTED, new String[] {});
            requiredValuesForFilesMissing = true;
        }

        if (StringUtils.isBlank(uploadDescription)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            requiredValuesForFilesMissing = true;
        }
        
        
        
        //BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        AssetBarcodeInventoryInputFileService batchInputFileSetService = SpringContext.getBean(AssetBarcodeInventoryInputFileService.class);
        
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            requiredValuesForFilesMissing = true;
        }

        //Map<String, FormFile> uploadedFiles = ((KualiBatchInputFileSetForm) form).getUploadedFiles();
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
            Map<String, String> typeToSavedFileNames = batchInputFileSetService.save(GlobalVariables.getUserSession().getFinancialSystemUser(), batchType, batchUpload.getFileUserIdentifer(), typeToStreamMap, ((AssetBarCodeInventoryInputFileForm) form).isSupressDoneFileCreation(),uploadDescription);
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
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction#download(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * 
     * Overridden because the super class has a set of files and a second pulldown menu. BCI only has one file type and therefore one pulldown menu to select from.
     */
    @Override
    public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchInputFileSetForm kualiBatchInputFileSetForm = (KualiBatchInputFileSetForm) form;
        BatchUpload batchUpload = kualiBatchInputFileSetForm.getBatchUpload();

        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DOWNLOAD, new String[] {});
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
     * 
     * Builds list of filenames that the user has permission to manage, and populates the form member. Throws an exception if the
     * batch file set type is not active. Sets the title key from the batch input type. This method must be called before the action
     * handler to ensure proper authorization.
     * 
     */
    @Override    
    public void setupForm(KualiBatchInputFileSetForm form) {
        List<KeyLabelPair> userFiles = new ArrayList<KeyLabelPair>();

        UniversalUser user = GlobalVariables.getUserSession().getFinancialSystemUser();
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

        userFiles.add(new KeyLabelPair("", "Select a file identifier"));
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


    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction#delete(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchInputFileSetForm kualiBatchInputFileSetForm = (KualiBatchInputFileSetForm) form;
        BatchUpload batchUpload = kualiBatchInputFileSetForm.getBatchUpload();

        if (StringUtils.isBlank(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DELETE, new String[] {});
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        //BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);
        AssetBarcodeInventoryInputFileService batchInputFileSetService = SpringContext.getBean(AssetBarcodeInventoryInputFileService.class);
        
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getExistingFileName())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BatchInputFileSetType batchType = retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        try {
            boolean deleteSuccessful = batchInputFileSetService.delete(GlobalVariables.getUserSession().getFinancialSystemUser(), batchType, batchUpload.getExistingFileName());

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

    
}