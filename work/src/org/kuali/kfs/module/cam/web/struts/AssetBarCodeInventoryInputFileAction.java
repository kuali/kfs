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
package org.kuali.kfs.module.cam.web.struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.service.BatchInputFileSetService;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction;
import org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetForm;


public class AssetBarCodeInventoryInputFileAction extends KualiBatchInputFileSetAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarCodeInventoryInputFileAction.class);

    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiBatchInputFileSetAction#download(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
     * Builds list of filenames that the user has permission to manage, and populates the form member. Throws an exception if the
     * batch file set type is not active. Sets the title key from the batch input type. This method must be called before the action
     * handler to ensure proper authorization.
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
    
}
