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
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestImport;

public class BudgetConstructionImportExportForm extends BudgetExpansionForm {
    
    private String fieldDelimiter;
    private String textFieldDelimiter;
    private String otherFieldDelimiter;
    private String otherTextFieldDelimiter;
    private String title;
    private String reportMode;
    private BudgetConstructionRequestImport budgetConstructionRequestImport;
    
    public BudgetConstructionImportExportForm() {
        super();
        this.budgetConstructionRequestImport = new BudgetConstructionRequestImport();
        setFieldDelimiter(BCConstants.RequestImportFieldSeparator.COMMA.getSeparator());
        setTextFieldDelimiter(BCConstants.RequestImportTextFieldDelimiter.QUOTE.getDelimiter());
    }
    
    /**
     * Gets the field delimiter
     * 
     * @return
     */
    public String getFieldDelimiter() {
        return this.budgetConstructionRequestImport.getFieldDelimiter();
    }
    
    /**
     * Sets the field delimiter
     * 
     * @param fieldDelimiter
     */
    public void setFieldDelimiter(String fieldDelimiter) {
        this.budgetConstructionRequestImport.setFieldDelimiter(fieldDelimiter);
    }
    
    /**
     * Gets the text field delimiter
     * 
     * @return
     */
    public String getTextFieldDelimiter() {
        return this.budgetConstructionRequestImport.getTextFieldDelimiter();
    }

    /**
     * Sets the text field delimiter
     * 
     * @param textFieldDelimiter
     */
    public void setTextFieldDelimiter(String textFieldDelimiter) {
        this.budgetConstructionRequestImport.setTextFieldDelimiter(textFieldDelimiter);
    }
    
    /**
     * Gets Field delimiter
     * 
     * @return
     */
    public String getOtherFieldDelimiter() {
        return this.budgetConstructionRequestImport.getOtherFieldDelimiter();
    }
    
    /**
     * Sets field delimiter
     * 
     * @param otherFieldDelimiter
     */
    public void setOtherFieldDelimiter(String otherFieldDelimiter) {
        this.budgetConstructionRequestImport.setOtherFieldDelimiter(otherFieldDelimiter);
    }
    
    /**
     * gets text field delimiter
     * 
     * @return
     */
    public String getOtherTextFieldDelimiter() {
        return this.budgetConstructionRequestImport.getOtherTextFieldDelimiter();
    }
    
    /**
     * Sets text field delimiter
     * 
     * @param otherTextFieldDelimiter
     */
    public void setOtherTextFieldDelimiter(String otherTextFieldDelimiter) {
        this.budgetConstructionRequestImport.setOtherTextFieldDelimiter(otherTextFieldDelimiter);
    }
    
    /**
     * 
     * 
     * @return
     */
    public BudgetConstructionRequestImport getBudgetConstructionRequestImport() {
        return budgetConstructionRequestImport;
    }

    /**
     * 
     * 
     * @param budgetConstructionRequestImport
     */
    public void setBudgetConstructionRequestImport(BudgetConstructionRequestImport budgetConstructionRequestImport) {
        this.budgetConstructionRequestImport = budgetConstructionRequestImport;
    }
    
    /**
     * Sets form title
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets form title
     * 
     * @param operatingModeTitle
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets operating mode
     * 
     * @return
     */
    public String getReportMode() {
        return reportMode;
    }
    
    /**
     * Sets operating mode
     * 
     * @param operatingMode
     */
    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }
    
}
