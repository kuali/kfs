/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

/**
 * This class...
 */
public class FilePurgeCustomAge {

    String directory;
    String parameterPrefix;
    String fileNameStart;
    
    /**
     * Gets the directory attribute. 
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }
    /**
     * Sets the directory attribute value.
     * @param directory The directory to set.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    /**
     * Gets the parameterPrefix attribute. 
     * @return Returns the parameterPrefix.
     */
    public String getParameterPrefix() {
        return parameterPrefix;
    }
    /**
     * Sets the parameterPrefix attribute value.
     * @param parameterPrefix The parameterPrefix to set.
     */
    public void setParameterPrefix(String parameterPrefix) {
        this.parameterPrefix = parameterPrefix;
    }
    /**
     * Gets the fileNameStart attribute. 
     * @return Returns the fileNameStart.
     */
    public String getFileNameStart() {
        return fileNameStart;
    }
    /**
     * Sets the fileNameStart attribute value.
     * @param fileNameStart The fileNameStart to set.
     */
    public void setFileNameStart(String fileNameStart) {
        this.fileNameStart = fileNameStart;
    }
    
}
