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
package org.kuali.module.budget.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.web.struts.form.BudgetConstructionImportExportForm;
import org.kuali.module.budget.web.struts.form.BudgetConstructionRequestImportForm;

public class BudgetConstructionImportExportAction extends KualiAction {
    
    /**
     * checks form values against business rules
     * 
     * @param form
     * @return
     */
    public boolean validateFormData(BudgetConstructionImportExportForm form) {
        boolean isValid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        if (form.getUniversityFiscalYear() ==  null) {
            throw new RuntimeException(BCKeyConstants.ERROR_BUDGET_YEAR_REQUIRED);
        }
        //field separator validations
        if ( StringUtils.isBlank(form.getFieldDelimiter()) )  {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        } else if (form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.OTHER.toString()) && StringUtils.isBlank(form.getOtherFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        } /*else if (!form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.COMMA.getSeparator()) &&
                    !form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.TAB.toString()) &&
                    !form.getFieldDelimiter().equals(BCConstants.RequestImportFieldSeparator.OTHER.toString()) ) {
                        //user did not pick a valid field separator value
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
                        isValid = false;    
        }*/
        
        //text delimiter validations
        if ( StringUtils.isBlank(form.getTextFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
            isValid = false;
        } else if (form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) && StringUtils.isBlank(form.getOtherTextFieldDelimiter()) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
            isValid = false;
        } /*else if (!form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.QUOTE.getDelimiter()) &&
                !form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.NOTHING.getDelimiter()) &&
                !form.getTextFieldDelimiter().equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) ) {
                    //user did not pick a valid field separator value
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_TEXT_DELIMITER_REQUIRED);
                    isValid = false;    
        }*/
        
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
    public String getFieldSeparator(BudgetConstructionImportExportForm form) {
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
    public String getTextFieldDelimiter(BudgetConstructionImportExportForm form) {
        String delimiter = form.getTextFieldDelimiter();
        if ( delimiter.equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString()) ) delimiter = form.getOtherTextFieldDelimiter();
        
        return delimiter;
    }
}
