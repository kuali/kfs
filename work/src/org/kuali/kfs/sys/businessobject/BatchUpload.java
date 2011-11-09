/*
 * Copyright 2007 The Kuali Foundation
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
