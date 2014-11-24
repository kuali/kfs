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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Object that contains properties used on the batch upload screen.
 */
public class BatchUpload extends TransientBusinessObjectBase {
    private String batchInputTypeName;
    private String fileUserIdentifer;
    private String existingFileName;

    /**
     * Default no-arg constructor.
     */
    public BatchUpload() {

    }


    /**
     * Gets the batchInputTypeName attribute.
     * 
     * @return Returns the batchInputTypeName.
     */
    public String getBatchInputTypeName() {
        return batchInputTypeName;
    }


    /**
     * Sets the batchInputTypeName attribute value.
     * 
     * @param batchInputTypeName The batchInputTypeName to set.
     */
    public void setBatchInputTypeName(String batchInputTypeName) {
        this.batchInputTypeName = batchInputTypeName;
    }


    /**
     * Gets the existingFileName attribute.
     * 
     * @return Returns the existingFileName.
     */
    public String getExistingFileName() {
        return existingFileName;
    }


    /**
     * Sets the existingFileName attribute value.
     * 
     * @param existingFileName The existingFileName to set.
     */
    public void setExistingFileName(String deleteFile) {
        this.existingFileName = deleteFile;
    }


    /**
     * Gets the fileUserIdentifer attribute.
     * 
     * @return Returns the fileUserIdentifer.
     */
    public String getFileUserIdentifer() {
        return fileUserIdentifer;
    }


    /**
     * Sets the fileUserIdentifer attribute value.
     * 
     * @param fileUserIdentifer The fileUserIdentifer to set.
     */
    public void setFileUserIdentifer(String fileRename) {
        this.fileUserIdentifer = fileRename;
    }


    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("batchInputType", this.batchInputTypeName);
        return m;
    }

}
