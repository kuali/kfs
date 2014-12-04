/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
