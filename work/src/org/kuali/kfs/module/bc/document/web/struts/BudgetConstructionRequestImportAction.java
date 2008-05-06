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
import java.util.ArrayList;
import java.util.List;

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
import org.kuali.core.util.Timer;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.ReportGeneration;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionRequestImportForm;


/**
 * Handles Budget Construction Import Requests
 */
public class BudgetConstructionRequestImportAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRequestImportAction.class);
    
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
    public ActionForward importFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionRequestImportForm budgetConstructionImportForm = (BudgetConstructionRequestImportForm) form;
        BudgetRequestImportService budgetRequestImportService = SpringContext.getBean(BudgetRequestImportService.class);
        Integer budgetYear = budgetConstructionImportForm.getUniversityFiscalYear();
        
        Timer t = new Timer("validateFormData");
        
        boolean isValid = validateFormData(budgetConstructionImportForm);
        t.log();
        
        String basePath;
        String lookupUrl;
        
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        String personUniversalIdentifier = user.getPersonUniversalIdentifier();
        t = new Timer("processImportFile");
        List<String> parsingErrors = budgetRequestImportService.processImportFile(budgetConstructionImportForm.getFile().getInputStream(), personUniversalIdentifier, getFieldSeparator(budgetConstructionImportForm), getTextFieldDelimiter(budgetConstructionImportForm), budgetConstructionImportForm.getFileType(), budgetYear);
        t.log();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if (!parsingErrors.isEmpty()) {
            budgetRequestImportService.generatePdf(parsingErrors, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, "budgetImportLog.pdf");
            return null;
        }
        t = new Timer("validateData");
        List<String> dataValidationErrorList = budgetRequestImportService.validateData(budgetYear);
        t.log();
        List<String> messageList = new ArrayList<String>();
        
        if (!dataValidationErrorList.isEmpty()) {
            messageList.add("Fatal error during data validation");
            messageList.addAll(dataValidationErrorList);
        }
        t = new Timer("loadBudget");
        List<String> updateErrorMessages = budgetRequestImportService.loadBudget(user, budgetConstructionImportForm.getFileType(), budgetYear);
        t.log();
        messageList.addAll(updateErrorMessages);
        
        if ( !messageList.isEmpty() ) {
            budgetRequestImportService.generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, "budgetImportLog.pdf");
            return null;
        }
        t.log();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
      
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
    public ActionForward cancelImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        String lookupUrl = basePath + "/" + BCConstants.BC_SELECTION_ACTION + "?methodToCall=loadExpansionScreen";
        
        return new ActionForward(lookupUrl, true);

    }
    
    /**
     * checks form values against business rules
     * 
     * @param form
     * @return
     */
    private boolean validateFormData(BudgetConstructionRequestImportForm form) {
        boolean isValid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        if ( form.getFile() == null || form.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_IS_REQUIRED);
            isValid = false;
        }
        if (form.getFile() != null && (StringUtils.isBlank(form.getFile().getFileName())) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILENAME_REQUIRED);
            isValid = false;
        }
        if (form.getUniversityFiscalYear() ==  null) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_YEAR_REQUIRED);
            isValid = false;
        }
        //file type validation
        if ( StringUtils.isBlank(form.getFileType()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_TYPE_IS_REQUIRED);
            isValid = false;
        }
        if (!StringUtils.isBlank(form.getFileType()) && 
                !form.getFileType().equalsIgnoreCase(BCConstants.RequestImportFileType.ANNUAL.toString()) &&
                !form.getFileType().equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_TYPE_IS_REQUIRED);
            isValid = false;
        }
        //field separator validations
        if ( StringUtils.isBlank(form.getFieldDelimiter()) )  {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        } else if (form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.OTHER.toString()) && StringUtils.isBlank(form.getOtherFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        } else if (!form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.COMMA.getSeparator()) &&
                    !form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.TAB.toString()) &&
                    !form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.OTHER.toString()) ) {
                        //user did not pick a valid field separator value
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
                        isValid = false;    
        }
        
        //text delimiter validations
        if ( StringUtils.isBlank(form.getTextFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
            isValid = false;
        } else if (form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) && StringUtils.isBlank(form.getOtherTextFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
            isValid = false;
        } else if (!form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.QUOTE.getDelimiter()) &&
                !form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.NOTHING.getDelimiter()) &&
                !form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) ) {
                    //user did not pick a valid field separator value
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
                    isValid = false;    
        }
        
        if (isValid && getFieldSeparator(form).equalsIgnoreCase(getTextFieldDelimiter(form))) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_DISTINCT_DELIMITERS_REQUIRED);
            isValid = false;
        }
         
        return isValid;
    }
    
    /**
     * Returns the field separator
     * 
     * @param form
     * @return
     */
    private String getFieldSeparator(BudgetConstructionRequestImportForm form) {
        String separator = form.getFieldDelimiter();
        
        if ( separator.equals(BCConstants.RequestImportFieldSeparator.OTHER.toString()) ) separator = form.getOtherFieldDelimiter();
        if ( separator.endsWith(BCConstants.RequestImportFieldSeparator.TAB.toString()) ) separator = BCConstants.RequestImportFieldSeparator.TAB.getSeparator();
        
        return separator;
    }
    
    /**
     * Returns the text field delimiter
     * 
     * @param form
     * @return
     */
    private String getTextFieldDelimiter(BudgetConstructionRequestImportForm form) {
        String delimiter = form.getTextFieldDelimiter();
        if ( delimiter.equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) ) delimiter = form.getOtherTextFieldDelimiter();
        
        return delimiter;
    }
}
