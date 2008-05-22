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
package org.kuali.module.budget.web.struts.form;

import org.apache.struts.upload.FormFile;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionRequestImport;


/**
 * Form for Budget Construction Import
 */
public class BudgetConstructionRequestImportForm extends BudgetConstructionImportExportForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRequestImportForm.class);

    private FormFile file;
    private String fileType;
    private String fieldDelimiter;
    private String textFieldDelimiter;
    private String otherFieldDelimiter;
    private String otherTextFieldDelimiter;
    private BudgetConstructionRequestImport budgetConstructionRequestImport;
    private Integer universityFiscalYear;
    
    public BudgetConstructionRequestImportForm() {
        super();
        this.budgetConstructionRequestImport = new BudgetConstructionRequestImport();
        setFileType(BCConstants.RequestImportFileType.ANNUAL.toString());
        this.setTitle("BC Request Import Tool");
        this.setOperatingMode("requestImport");
    }
    
    /**
     * Gets the field delimiter
     * 
     * @return
     */
    /*public String getFieldDelimiter() {
        return this.budgetConstructionRequestImport.getFieldDelimiter();
    }*/
    
    /**
     * Sets the field delimiter
     * 
     * @param fieldDelimiter
     */
    /*public void setFieldDelimiter(String fieldDelimiter) {
        this.budgetConstructionRequestImport.setFieldDelimiter(fieldDelimiter);
    }*/
    
    /**
     * Gets the file name to import
     * 
     * @return
     */
    public FormFile getFile() {
        return this.file;
    }
    
    /**
     * Sets the file name to import
     * This method...
     * @param fileName
     */
    public void setFile(FormFile file) {
        this.file = file;
        this.budgetConstructionRequestImport.setFileName(file.getFileName());
    }
    
    /**
     * Gets the file type
     * 
     * @return
     */
    public String getFileType() {
        return this.budgetConstructionRequestImport.getFileType();
    }
    
    /**
     * Sets the file type
     * 
     * @param fileType
     */
    public void setFileType(String fileType) {
        this.budgetConstructionRequestImport.setFileType(fileType);
    }
    
    public String getHtmlFormAction() {
        return "budgetBudgetConstructionRequestImport";
    }
    
    /**
     * Gets the text field delimiter
     * 
     * @return
     */
    /*public String getTextFieldDelimiter() {
        return this.budgetConstructionRequestImport.getTextFieldDelimiter();
    }*/

    /**
     * Sets the text field delimiter
     * 
     * @param textFieldDelimiter
     */
    /*public void setTextFieldDelimiter(String textFieldDelimiter) {
        this.budgetConstructionRequestImport.setTextFieldDelimiter(textFieldDelimiter);
    }*/
    
    /**
     * Gets Field delimiter
     * 
     * @return
     */
    /*public String getOtherFieldDelimiter() {
        return this.budgetConstructionRequestImport.getOtherFieldDelimiter();
    }*/
    
    /**
     * Sets field delimiter
     * 
     * @param otherFieldDelimiter
     */
    /*public void setOtherFieldDelimiter(String otherFieldDelimiter) {
        this.budgetConstructionRequestImport.setOtherFieldDelimiter(otherFieldDelimiter);
    }*/
    
    /**
     * gets text field delimiter
     * 
     * @return
     */
    /*public String getOtherTextFieldDelimiter() {
        return this.budgetConstructionRequestImport.getOtherTextFieldDelimiter();
    }*/
    
    /**
     * Sets text field delimiter
     * 
     * @param otherTextFieldDelimiter
     */
    /*public void setOtherTextFieldDelimiter(String otherTextFieldDelimiter) {
        this.budgetConstructionRequestImport.setOtherTextFieldDelimiter(otherTextFieldDelimiter);
    }*/
    
    /**
     * 
     * 
     * @return
     */
    /*public BudgetConstructionRequestImport getBudgetConstructionRequestImport() {
        return budgetConstructionRequestImport;
    }*/

    /**
     * 
     * 
     * @param budgetConstructionRequestImport
     */
    /*public void setBudgetConstructionRequestImport(BudgetConstructionRequestImport budgetConstructionRequestImport) {
        this.budgetConstructionRequestImport = budgetConstructionRequestImport;
    }*/
    
    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    /*public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }*/
    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    /*public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }*/

}
