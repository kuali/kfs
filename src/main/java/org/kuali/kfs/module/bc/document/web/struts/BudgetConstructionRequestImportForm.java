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
