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
package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class BudgetConstructionRequestImport extends TransientBusinessObjectBase {
    private String fileName;
    private String fileType;
    private String fieldDelimiter;
    private String textFieldDelimiter;
    private String otherFieldDelimiter;
    private String otherTextFieldDelimiter;
    
    /**
     * Gets the field delimiter
     * 
     * @return
     */
    public String getFieldDelimiter() {
        return fieldDelimiter;
    }
    
    /**
     * Sets the field delimiter
     * 
     * @param fieldDelimiter
     */
    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }
    
    /**
     * Gets the file name to import
     * 
     * @return
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the file name to import
     * This method...
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Gets the file type
     * 
     * @return
     */
    public String getFileType() {
        return fileType;
    }
    
    /**
     * Sets the file type
     * 
     * @param fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    /**
     * Gets the text field delimiter
     * 
     * @return
     */
    public String getTextFieldDelimiter() {
        return textFieldDelimiter;
    }

    /**
     * Sets the text field delimiter
     * 
     * @param textFieldDelimiter
     */
    public void setTextFieldDelimiter(String textFieldDelimiter) {
        this.textFieldDelimiter = textFieldDelimiter;
    }

    /**
     * Gets Field delimiter
     * 
     * @return
     */
    public String getOtherFieldDelimiter() {
        return otherFieldDelimiter;
    }
    
    /**
     * Sets field delimiter
     * 
     * @param otherFieldDelimiter
     */
    public void setOtherFieldDelimiter(String otherFieldDelimiter) {
        this.otherFieldDelimiter = otherFieldDelimiter;
    }
    
    /**
     * gets text field delimiter
     * 
     * @return
     */
    public String getOtherTextFieldDelimiter() {
        return otherTextFieldDelimiter;
    }
    
    /**
     * Sets text field delimiter
     * This method...
     * @param otherTextFieldDelimiter
     */
    public void setOtherTextFieldDelimiter(String otherTextFieldDelimiter) {
        this.otherTextFieldDelimiter = otherTextFieldDelimiter;
    }
    
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("fileName", fileName);
        m.put("fileType", fileType);
        m.put("fieldDelimiter", fieldDelimiter);
        m.put("textFieldDelimiter", textFieldDelimiter);
        
        return m;
    }

}
