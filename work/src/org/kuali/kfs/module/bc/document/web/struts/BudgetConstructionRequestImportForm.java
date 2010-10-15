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

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestImport;


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
    
    public BudgetConstructionRequestImportForm() {
        super();
        this.budgetConstructionRequestImport = new BudgetConstructionRequestImport();
        setFileType(BCConstants.RequestImportFileType.ANNUAL.toString());
        this.setTitle("Budget Construction Request Import Tool");
        this.setReportMode("requestImport");
    }
    
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

}
