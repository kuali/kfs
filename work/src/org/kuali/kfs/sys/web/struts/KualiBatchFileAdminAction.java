/*
 * Copyright 2009 The Kuali Foundation
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

public class KualiBatchFileAdminAction extends KualiAction {
    public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchFileAdminForm fileAdminForm = (KualiBatchFileAdminForm) form;
        String filePath = BatchFileUtils.resolvePathToAbsolutePath(fileAdminForm.getFilePath());
        File file = new File(filePath).getAbsoluteFile();
        
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        File containingDirectory = file.getParentFile();
        if (!BatchFileUtils.isDirectoryAccessible(containingDirectory.getAbsolutePath())) {
            throw new RuntimeException("Error: inaccessible directory provided");
        }
        
        BatchFile batchFile = new BatchFile();
        batchFile.setFile(file);
        if (!SpringContext.getBean(BatchFileAdminAuthorizationService.class).canDownload(batchFile, GlobalVariables.getUserSession().getPerson())) {
            throw new RuntimeException("Error: not authorized to download file");
        }
        
        response.setContentType("application/octet-stream");
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
    
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchFileAdminForm fileAdminForm = (KualiBatchFileAdminForm) form;
        String filePath = BatchFileUtils.resolvePathToAbsolutePath(fileAdminForm.getFilePath());
        File file = new File(filePath).getAbsoluteFile();
        
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        File containingDirectory = file.getParentFile();
        if (!BatchFileUtils.isDirectoryAccessible(containingDirectory.getAbsolutePath())) {
            throw new RuntimeException("Error: inaccessible directory provided");
        }
        
        BatchFile batchFile = new BatchFile();
        batchFile.setFile(file);
        if (!SpringContext.getBean(BatchFileAdminAuthorizationService.class).canDelete(batchFile, GlobalVariables.getUserSession().getPerson())) {
            throw new RuntimeException("Error: not authorized to delete file");
        }
        
        String displayFileName = BatchFileUtils.pathRelativeToRootDirectory(file.getAbsolutePath());
        
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) {
            String questionText = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.QUESTION_BATCH_FILE_ADMIN_DELETE_CONFIRM);
            questionText = MessageFormat.format(questionText, displayFileName);
            return performQuestionWithoutInput(mapping, fileAdminForm, request, response, "confirmDelete", questionText,
                    KRADConstants.CONFIRMATION_QUESTION, "delete", fileAdminForm.getFilePath());
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ("confirmDelete".equals(question)) {
                String status = null;
                if (ConfirmationQuestion.YES.equals(buttonClicked)) {
                    try {
                        file.delete();
                        status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_SUCCESSFUL);
                        status = MessageFormat.format(status, displayFileName);
                    }
                    catch (SecurityException e) {
                        status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_ERROR);
                        status = MessageFormat.format(status, displayFileName);
                    }
                }
                else if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                    status = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_ADMIN_DELETE_CANCELLED);
                    status = MessageFormat.format(status, displayFileName);
                }
                if (status != null) {
                    request.setAttribute("status", status);
                    return mapping.findForward(RiceConstants.MAPPING_BASIC);
                }
            }
            throw new RuntimeException("Unrecognized question: " + question + " or response: " + buttonClicked);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        // do nothing... authorization is integrated into action handler
    }
}
