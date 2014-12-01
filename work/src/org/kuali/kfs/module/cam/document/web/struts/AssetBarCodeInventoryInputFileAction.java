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
package org.kuali.kfs.module.cam.document.web.struts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;


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
        AssetBarcodeInventoryInputFileType batchType = (AssetBarcodeInventoryInputFileType)retrieveBatchInputFileSetTypeImpl(batchUpload.getBatchInputTypeName());
        
        Map<String, FormFile> uploadedFiles = ((KualiBatchInputFileSetForm) form).getUploadedFiles();
        
        String fileTypeExtension = ((AssetBarcodeInventoryInputFileType)batchType).getFileExtension();
        String fileName = uploadedFiles.get(fileTypeExtension.substring(1)).getFileName();
            
        //Validating the file type is the correct one before saving.
        if (!StringUtils.isBlank(fileName) && !fileName.endsWith(fileTypeExtension)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FILE_TYPE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }            
        
        //return super.save(mapping, form, request, response);
        
        String uploadDescription = ((AssetBarCodeInventoryInputFileForm) form).getUploadDescription();

        boolean requiredValuesForFilesMissing = false;
        if (StringUtils.isBlank(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_NO_FILE_SET_IDENTIFIER_SELECTED, new String[] {});
            requiredValuesForFilesMissing = true;
        }

        if (StringUtils.isBlank(uploadDescription)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            requiredValuesForFilesMissing = true;
        }
        
        
        
        AssetBarcodeInventoryInputFileService batchInputFileSetService = SpringContext.getBean(AssetBarcodeInventoryInputFileService.class);
        
        if (!batchInputFileSetService.isFileUserIdentifierProperlyFormatted(batchUpload.getFileUserIdentifer())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT);
            requiredValuesForFilesMissing = true;
        }

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
            //Map<String, String> typeToSavedFileNames =  batchInputFileSetService.save(GlobalVariables.getUserSession().getPerson(), batchType, batchUpload.getFileUserIdentifer(), typeToStreamMap, ((AssetBarCodeInventoryInputFileForm) form).isSupressDoneFileCreation(),uploadDescription);
            Map<String, String> typeToSavedFileNames =  batchInputFileSetService.save(GlobalVariables.getUserSession().getPerson(), batchType, batchUpload.getFileUserIdentifer(), typeToStreamMap, ((AssetBarCodeInventoryInputFileForm) form));            
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
     * 
     * Builds list of filenames that the user has permission to manage, and populates the form member. Throws an exception if the
     * batch file set type is not active. Sets the title key from the batch input type. This method must be called before the action
     * handler to ensure proper authorization.
     * 
     */
    @Override    
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

        BatchInputFileSetService batchInputFileSetService = SpringContext.getBean(BatchInputFileSetService.class);

        List<KeyValue> fileTypes = new ArrayList<KeyValue>();
        fileTypes.add(new ConcreteKeyValue("", "Select a file type to download"));
        
        for (String fileAlias : batchInputFileSetType.getFileTypes()) {
            fileTypes.add(new ConcreteKeyValue(fileAlias, batchInputFileSetType.getFileTypeDescription().get(fileAlias)));
        }
        form.setFileTypes(fileTypes);
        
        // set title key
        form.setTitleKey(batchInputFileSetType.getTitleKey());
    }
}
