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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
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
        FormFile fileToParse = budgetConstructionImportForm.getFile();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        //TODO: error checking?
        //TODO: text fields?
        //TODO: different parsing for different file types?
        
        boolean isValid = validateImportRequest(budgetConstructionImportForm);
        
        String basePath;
        String lookupUrl;
        
        if (isValid ) {
            basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
            lookupUrl = basePath + "/" + BCConstants.BC_SELECTION_ACTION + "?methodToCall=loadExpansionScreen";
        } else {
            basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
            lookupUrl = basePath + "/" + "budgetBudgetConstructionRequestImport.do";
        }
        
        
        return new ActionForward(lookupUrl, true);
      
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
    private boolean validateImportRequest(BudgetConstructionRequestImportForm form) {
        boolean isValid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        if ( form.getFile() == null) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_IS_REQUIRED);
            isValid = false;
        }
        if (form.getFile() != null && (form.getFile().getFileName() == null || form.getFile().getFileName().equalsIgnoreCase("")) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILENAME_REQUIRED);
            isValid = false;
        }
        if (form.getFieldDelimiter() == null || form.getFieldDelimiter().equalsIgnoreCase(""))  {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        } else if (form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.OTHER) && ( form.getOtherFieldDelimiter() == null || form.getOtherFieldDelimiter().equalsIgnoreCase("") ) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        }
        if (form.getTextFieldDelimiter() == null || form.getTextFieldDelimiter().equalsIgnoreCase("")) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
            isValid = false;
        } else if (form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.OTHER) && ( form.getOtherTextFieldDelimiter() == null || form.getOtherTextFieldDelimiter().equalsIgnoreCase("") ) ) {
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
        if ( separator.equals(BCConstants.RequestImportFieldSeparator.OTHER) ) separator = form.getOtherFieldDelimiter();
        
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
        if ( delimiter.equals(BCConstants.RequestImportTextFieldDelimiter.OTHER) ) delimiter = form.getOtherTextFieldDelimiter();
        
        return delimiter;
    }
}
