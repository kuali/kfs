/*
 * Copyright 2007-2008 The Kuali Foundation
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
import org.kuali.kfs.module.bc.document.service.BudgetRequestImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;


/**
 * Handles Budget Construction Import Requests
 */
public class BudgetConstructionRequestImportAction extends BudgetConstructionImportExportAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRequestImportAction.class);

    public ActionForward start(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception {
        return arg0.findForward("import_export");
    }

    /**
     * Imports file
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionRequestImportForm budgetConstructionImportForm = (BudgetConstructionRequestImportForm) form;
        BudgetRequestImportService budgetRequestImportService = SpringContext.getBean(BudgetRequestImportService.class);
        Integer budgetYear = budgetConstructionImportForm.getUniversityFiscalYear();
        List<String> messageList = new ArrayList<String>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy ' ' HH:mm:ss", Locale.US);

        boolean isValid = validateFormData(budgetConstructionImportForm);

        if (!isValid) {
            return mapping.findForward(BCConstants.MAPPING_IMPORT_EXPORT);
        }

        Person user = GlobalVariables.getUserSession().getPerson();
        String principalId = user.getPrincipalId();
        Date startTime = new Date();
        messageList.add("Import run started " + dateFormatter.format(startTime));
        messageList.add(" ");
        messageList.add("Text file load phase - parsing");

        List<String> parsingErrors = budgetRequestImportService.processImportFile(budgetConstructionImportForm.getFile().getInputStream(), principalId, getFieldSeparator(budgetConstructionImportForm), getTextFieldDelimiter(budgetConstructionImportForm), budgetConstructionImportForm.getFileType(), budgetYear);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (!parsingErrors.isEmpty()) {
            messageList.addAll(parsingErrors);
            messageList.add("Import run finished at " + dateFormatter.getCalendar().getTime().toString());
            budgetRequestImportService.generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, BCConstants.REQUEST_IMPORT_OUTPUT_FILE);
            return null;
        }

        messageList.add("Text file load complete");
        messageList.add(" ");
        messageList.add("Validate data phase");

        List<String> dataValidationErrorList = budgetRequestImportService.validateData(budgetYear, principalId);

        if (!dataValidationErrorList.isEmpty()) {
            messageList.add("Errors found during data validation");
            messageList.addAll(dataValidationErrorList);
        }

        messageList.add("Validate data complete");
        messageList.add(" ");
        messageList.add("Update budget phase");

        List<String> updateErrorMessages = budgetRequestImportService.loadBudget(user, budgetConstructionImportForm.getFileType(), budgetYear);

        messageList.addAll(updateErrorMessages);
        messageList.add("Update budget complete");
        messageList.add(" ");
        Date endTime = new Date();
        messageList.add("Import run finished at " + dateFormatter.format(endTime));

        budgetRequestImportService.generatePdf(messageList, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, BCConstants.REQUEST_IMPORT_OUTPUT_FILE);
        return null;
    }

    /**
     *
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionImportExportAction#validateFormData(org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionImportExportForm)
     */
    @Override
    public boolean validateFormData(BudgetConstructionImportExportForm form) {
        boolean isValid = super.validateFormData(form);
        BudgetConstructionRequestImportForm requestImportForm = (BudgetConstructionRequestImportForm) form;
        MessageMap errorMap = GlobalVariables.getMessageMap();

        // can't create BC documents when in system view only mode
        // let the user know this up front
        if (!SpringContext.getBean(FiscalYearFunctionControlService.class).isBudgetUpdateAllowed(requestImportForm.getUniversityFiscalYear())) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.MESSAGE_BUDGET_SYSTEM_VIEW_ONLY);
            isValid &= false;
        }

        if ( requestImportForm.getFile() == null || requestImportForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_IS_REQUIRED);
            isValid = false;
        }
        if ( requestImportForm.getFile() != null && requestImportForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_EMPTY);
            isValid = false;
        }
        if (requestImportForm.getFile() != null && (StringUtils.isBlank(requestImportForm.getFile().getFileName())) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILENAME_REQUIRED);
            isValid = false;
        }

        //file type validation
        if ( StringUtils.isBlank(requestImportForm.getFileType()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_TYPE_IS_REQUIRED);
            isValid = false;
        }

        return isValid;
    }

}

