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
package org.kuali.kfs.batch;

import org.kuali.kfs.KFSConstants;


/**
 * Base class for BatchInputFileType implementations.
 */
public abstract class BatchInputFileTypeBase implements BatchInputFileType {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchInputFileTypeBase.class);

    private String directoryPath;
    private String fileExtension;
    private String digestorRulesFileName;
    private String schemaLocation;

    /**
     * Constructs a BatchInputFileTypeBase.java.
     */
    public BatchInputFileTypeBase() {
        super();
    }

    /**
     * Gets the digestorRulesFileName attribute.
     */
    public String getDigestorRulesFileName() {
        return digestorRulesFileName;
    }

    /**
     * Sets the digestorRulesFileName attribute value.
     */
    public void setDigestorRulesFileName(String digestorRulesFileName) {
        this.digestorRulesFileName = digestorRulesFileName;
    }

    /**
     * Gets the directoryPath attribute.
     */
    public String getDirectoryPath() {
        return directoryPath;
    }

    /**
     * Sets the directoryPath attribute value.
     */
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Gets the fileExtension attribute.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Sets the fileExtension attribute value.
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Gets the schemaLocation attribute. 
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the schemaLocation attribute value.
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getWorkgroupParameterName() {
        return KFSConstants.SystemGroupParameterNames.FILE_TYPE_WORKGROUP_PARAMETER_NAME;
    }

}
