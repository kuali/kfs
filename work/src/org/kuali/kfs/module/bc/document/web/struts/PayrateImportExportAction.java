/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
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
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.document.service.PayrateExportService;
import org.kuali.kfs.module.bc.document.service.PayrateImportService;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PayrateImportExportAction extends BudgetExpansionAction {
    
    public ActionForward performImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayrateImportExportForm payrateImportExportForm = (PayrateImportExportForm) form;
        PayrateImportService payrateImportService = SpringContext.getBean(PayrateImportService.class);
        List<ExternalizedMessageWrapper> messageList = new ArrayList<ExternalizedMessageWrapper>();
        Integer budgetYear = payrateImportExportForm.getUniversityFiscalYear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy ' ' HH:mm:ss", Locale.US);
        
        boolean isValid = validateImportFormData(payrateImportExportForm);
        
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        //get start date for log file
        Date startTime = new Date();
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_LOG_FILE_HEADER_LINE, dateFormatter.format(startTime)));
        
        //parse file
        if (!payrateImportService.importFile(payrateImportExportForm.getFile().getInputStream(), messageList, principalId) ) {
            payrateImportService.generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, BCConstants.PAYRATE_IMPORT_LOG_FILE);
            return null;
        }
        
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_COMPLETE));
        
        Person user = GlobalVariables.getUserSession().getPerson();
        
        //perform updates
        payrateImportService.update(budgetYear, user, messageList, principalId);
        
        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_LOG_FILE_FOOTER, dateFormatter.format(new Date())));
        
        //write messages to log file
        payrateImportService.generatePdf(messageList, baos);
        
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, BCConstants.PAYRATE_IMPORT_LOG_FILE);
        
        return null;
    }
    
    public ActionForward performExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayrateImportExportForm payrateImportExportForm = (PayrateImportExportForm) form;
        PayrateExportService payrateExportService = SpringContext.getBean(PayrateExportService.class);
        HumanResourcesPayrollService payrollPerimeterService = SpringContext.getBean(HumanResourcesPayrollService.class);
        Integer budgetYear = payrateImportExportForm.getUniversityFiscalYear();
        String positionUnionCode = payrateImportExportForm.getPositionUnionCode();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        
        //form validation
        boolean isValidPositionUnionCode = validateExportFormData(payrateImportExportForm);
        if (!isValidPositionUnionCode) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        //position union code validation
        isValidPositionUnionCode = payrollPerimeterService.validatePositionUnionCode(positionUnionCode);
        if (!isValidPositionUnionCode) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_EXPORT_INVALID_POSITION_UNION_CODE, positionUnionCode);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        StringBuilder fileContents = payrateExportService.buildExportFile(budgetYear, positionUnionCode, payrateImportExportForm.getCsfFreezeDateFormattedForExportFile(), principalId);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(fileContents.toString().getBytes());
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.TEXT_MIME_TYPE, baos, BCConstants.PAYRATE_EXPORT_FILE);
        
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
    public boolean validateImportFormData(PayrateImportExportForm form) {
        boolean isValid = true;
        PayrateImportExportForm importForm = (PayrateImportExportForm) form;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        
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
    
    /**
     * Performs export form validation
     * 
     * @param form
     * @return
     */
    public boolean validateExportFormData(PayrateImportExportForm form) {
        boolean isValid = true;
        PayrateImportExportForm importForm = (PayrateImportExportForm) form;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        PayrateExportService payrateExportService = SpringContext.getBean(PayrateExportService.class);

        if (ObjectUtils.isNull(importForm.getPositionUnionCode()) || StringUtils.isBlank(importForm.getPositionUnionCode())) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_EXPORT_POSITION_UNION_CODE_REQUIRED);
            isValid = false;
        }
        else {
            if (!payrateExportService.isValidPositionUnionCode(form.getPositionUnionCode())) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_EXPORT_INVALID_POSITION_UNION_CODE, form.getPositionUnionCode());
                isValid = false;
            }
        }
        if (ObjectUtils.isNull(importForm.getCsfFreezeDate()) || StringUtils.isBlank(importForm.getCsfFreezeDate())) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_EXPORT_CSF_FREEZE_DATE_REQUIRED);
            isValid = false;
        }
        else {
            SimpleDateFormat validDateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            SimpleDateFormat exportFileFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
            try {
                Date validDate = validDateFormatter.parse(form.getCsfFreezeDate());
                importForm.setCsfFreezeDateFormattedForExportFile(exportFileFormat.format(validDate));
            }
            catch (ParseException e) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_PAYRATE_EXPORT_CSF_FREEZE_DATE_INCORRECT_FORMAT);
                isValid = false;
            }
        }
        return isValid;
    }
}

