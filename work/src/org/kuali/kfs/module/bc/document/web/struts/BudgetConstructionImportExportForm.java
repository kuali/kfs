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
package org.kuali.module.budget.web.struts.form;

import org.apache.struts.upload.FormFile;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionRequestImport;

public class BudgetConstructionImportExportForm extends KualiForm {
    
    private String fieldDelimiter;
    private String textFieldDelimiter;
    private String otherFieldDelimiter;
    private String otherTextFieldDelimiter;
    private String title;
    private String reportMode;
    private BudgetConstructionRequestImport budgetConstructionRequestImport;
    private Integer universityFiscalYear;
    
//  passed parms
    private String returnAnchor;
    private String returnFormKey;
    
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
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
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
    
    /**
     * Gets the returnAnchor attribute.
     * 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * 
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute.
     * 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * 
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }
}
