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
