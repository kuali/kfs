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
package org.kuali.kfs.module.bc.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.WebUtils;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.service.PayrateExportService;
import org.kuali.kfs.module.bc.service.PayrateImportService;
import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;

public class PayrateImportExportAction extends BudgetExpansionAction {
    
    public ActionForward performImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayrateImportExportForm payrateImportExportForm = (PayrateImportExportForm) form;
        PayrateImportService payrateImportService = SpringContext.getBean(PayrateImportService.class);
        List<ExternalizedMessageWrapper> messageList = new ArrayList<ExternalizedMessageWrapper>();
        Integer budgetYear = payrateImportExportForm.getUniversityFiscalYear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy ' ' HH:mm:ss", Locale.US);
        
        boolean isValid = validateFormData(payrateImportExportForm);
        
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        //get start date for log file
        Date startTime = new Date();
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_LOG_FILE_HEADER_LINE, dateFormatter.format(startTime)));
        
        //parse file
        List<ExternalizedMessageWrapper> parsingErrors = payrateImportService.importFile(payrateImportExportForm.getFile().getInputStream());
        
        if (!parsingErrors.isEmpty()) {
            messageList.addAll(parsingErrors);
            payrateImportService.generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, "exportMessages.pdf");
            return null;
        }
        if (payrateImportService.getImportCount() == 0 ) messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_NO_IMPORT_RECORDS));
        else  messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_COUNT, String.valueOf(payrateImportService.getImportCount())));
        
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_COMPLETE));
        
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        
        //perform updates
        List<ExternalizedMessageWrapper> updateMessages = payrateImportService.update(budgetYear, user);
        
        messageList.addAll(updateMessages);
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_UPDATE_COMPLETE, String.valueOf(payrateImportService.getUpdateCount())));
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_LOG_FILE_FOOTER, dateFormatter.format(new Date())));
        
        //write messages to log file
        payrateImportService.generatePdf(messageList, baos);
        
        //TODO: how to update form since response is set to null?
        payrateImportExportForm.setImportCount(payrateImportService.getImportCount());
        payrateImportExportForm.setUpdateCount(payrateImportService.getUpdateCount());
        
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, "exportMessages.pdf");
        
        return null;
    }
    
    public ActionForward performExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayrateImportExportForm payrateImportExportForm = (PayrateImportExportForm) form;
        PayrateExportService payrateExportService = SpringContext.getBean(PayrateExportService.class);
        Integer budgetYear = payrateImportExportForm.getUniversityFiscalYear();
        
        //TODO: form validation
        
        //TODO: position union code validation
        
        StringBuilder fileContents = payrateExportService.buildExportFile(budgetYear, payrateImportExportForm.getPositionUnionCode());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(fileContents.toString().getBytes());
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.TEXT_MIME_TYPE, baos, "payrateExport.txt");
        
        return null;
    }
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Performs form validation
     * 
     * @param form
     * @return
     */
    public boolean validateFormData(PayrateImportExportForm form) {
        boolean isValid = true;
        PayrateImportExportForm importForm = (PayrateImportExportForm) form;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        boolean budgetUpdatesAllowed = fiscalYearFunctionControlService.isBudgetUpdateAllowed(form.getUniversityFiscalYear());
        
        if ( importForm.getFile() == null || importForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_IS_REQUIRED);
            isValid = false;
        }
        if ( importForm.getFile() != null && importForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_EMPTY);
            isValid = false;
        }
        if (importForm.getFile() != null && (StringUtils.isBlank(importForm.getFile().getFileName())) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILENAME_REQUIRED);
            isValid = false;
        }
        if ( !budgetUpdatesAllowed ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_IMPORT_UPDATE_NOT_ALLOWED);
            isValid = false;
        }
        
        
        return isValid;
    }
    
}
