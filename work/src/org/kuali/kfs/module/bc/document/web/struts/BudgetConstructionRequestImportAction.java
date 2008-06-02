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
package org.kuali.module.budget.web.struts.action;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.util.WebUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.ReportGeneration;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionImportExportForm;
import org.kuali.module.budget.web.struts.form.BudgetConstructionRequestImportForm;


/**
 * Handles Budget Construction Import Requests
 */
public class BudgetConstructionRequestImportAction extends BudgetConstructionImportExportAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRequestImportAction.class);
    
    public ActionForward start(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception {
        //super.execute(arg0, arg1, arg2, arg3);
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
            //TODO: add path to constants
            return mapping.findForward("import_export");
        }
        
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        String personUniversalIdentifier = user.getPersonUniversalIdentifier();
        Date startTime = new Date();
        messageList.add("Import run started " + dateFormatter.format(startTime));
        messageList.add(" ");
        messageList.add("Text file load phase - parsing");
        
        List<String> parsingErrors = budgetRequestImportService.processImportFile(budgetConstructionImportForm.getFile().getInputStream(), personUniversalIdentifier, getFieldSeparator(budgetConstructionImportForm), getTextFieldDelimiter(budgetConstructionImportForm), budgetConstructionImportForm.getFileType(), budgetYear);
     
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
        
        List<String> dataValidationErrorList = budgetRequestImportService.validateData(budgetYear, personUniversalIdentifier);

        if (!dataValidationErrorList.isEmpty()) {
            messageList.add("Errors found during data validation");
            messageList.addAll(dataValidationErrorList);
        }
        
        messageList.add("Validate data compete");
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
     * returns to budget construction selection page without importing the file
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        String lookupUrl = basePath + "/" + BCConstants.BC_SELECTION_ACTION + "?methodToCall=loadExpansionScreen";
        
        return new ActionForward(lookupUrl, true);*/
        
        BudgetConstructionRequestImportForm budgetConstructionImportForm = (BudgetConstructionRequestImportForm) form;
        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, budgetConstructionImportForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, budgetConstructionImportForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.REQUEST_IMPORT_REFRESH_CALLER);
        
        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_SELECTION_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }
    
    /**
     * 
     * @see org.kuali.module.budget.web.struts.action.BudgetConstructionImportExportAction#validateFormData(org.kuali.module.budget.web.struts.form.BudgetConstructionImportExportForm)
     */
    public boolean validateFormData(BudgetConstructionImportExportForm form) {
        boolean isValid = super.validateFormData(form);
        BudgetConstructionRequestImportForm requestImportForm = (BudgetConstructionRequestImportForm) form;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
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
        /*if (!StringUtils.isBlank(requestImportForm.getFileType()) && 
                !requestImportForm.getFileType().equalsIgnoreCase(BCConstants.RequestImportFileType.ANNUAL.toString()) &&
                !requestImportForm.getFileType().equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_TYPE_IS_REQUIRED);
            isValid = false;
        }*/
        
        return isValid;
    }
    
}
