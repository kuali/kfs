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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class BudgetConstructionImportExportAction extends BudgetExpansionAction {

    /**
     * checks form values against business rules
     * 
     * @param form
     * @return
     */
    public boolean validateFormData(BudgetConstructionImportExportForm form) {
        boolean isValid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();

        if (form.getUniversityFiscalYear() == null) {
            throw new RuntimeException(BCKeyConstants.ERROR_BUDGET_YEAR_REQUIRED);
        }

        // field separator validations
        if (this.getFieldSeparator(form).equals("")) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FIELD_SEPARATOR_REQUIRED);
            isValid = false;
        }

        // no text delimiter specific validations needed since getTectFieldDelimter() handles null case
        // and the delimiter can be blank

        // separator and delimiter can't be the same
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

        if (separator.equals(BCConstants.RequestImportFieldSeparator.OTHER.toString())) {
            if (form.getOtherFieldDelimiter() == null) {
                separator = "";
            }
            else {
                separator = form.getOtherFieldDelimiter();
            }
        }
        if (separator.endsWith(BCConstants.RequestImportFieldSeparator.TAB.toString())) {
            separator = BCConstants.RequestImportFieldSeparator.TAB.getSeparator();
        }
        if (separator.endsWith(BCConstants.RequestImportFieldSeparator.COMMA.toString())) {
            separator = BCConstants.RequestImportFieldSeparator.COMMA.getSeparator();
        }

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

        if (delimiter == null || delimiter.equals(BCConstants.RequestImportTextFieldDelimiter.NOTHING.toString())) {
            delimiter = "";
        }
        if (delimiter.equals(BCConstants.RequestImportTextFieldDelimiter.OTHER.toString())) {
            if (form.getOtherTextFieldDelimiter() == null) {
                delimiter = "";
            }
            else {
                delimiter = form.getOtherTextFieldDelimiter();
            }
        }

        return delimiter;
    }
}
