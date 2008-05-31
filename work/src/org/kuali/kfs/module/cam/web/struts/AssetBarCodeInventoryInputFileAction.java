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
package org.kuali.module.cams.web.struts.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.WebUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.batch.BatchSpringContext;
import org.kuali.kfs.bo.BatchUpload;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.kfs.service.BatchInputFileSetService;
import org.kuali.kfs.web.struts.action.KualiBatchInputFileAction;
import org.kuali.kfs.web.struts.action.KualiBatchInputFileSetAction;
import org.kuali.kfs.web.struts.form.KualiBatchInputFileForm;
import org.kuali.kfs.web.struts.form.KualiBatchInputFileSetForm;


public class AssetBarCodeInventoryInputFileAction extends KualiBatchInputFileSetAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarCodeInventoryInputFileAction.class);

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiBatchInputFileSetAction#download(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
     * Retrieves a BatchInputFileType implementation from Spring based on the given name.
     *
    private BatchInputFileSetType retrieveBatchInputFileSetTypeImpl(String batchInputTypeName) {
        BatchInputFileSetType batchInputType = BatchSpringContext.getBatchInputFileSetType(batchInputTypeName);
        if (batchInputType == null) {
            LOG.error("Batch input type implementation not found for id " + batchInputTypeName);
            throw new RuntimeException(("Batch input type implementation not found for id " + batchInputTypeName));
        }
        return batchInputType;
    }*/
}