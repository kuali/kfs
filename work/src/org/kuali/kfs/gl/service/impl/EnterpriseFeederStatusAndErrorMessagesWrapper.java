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
package org.kuali.kfs.gl.service.impl;

import java.io.File;
import java.util.List;

import org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus;
import org.kuali.kfs.sys.Message;

/**
 * This class serves as a wrapper containing references to the feeder status and error messages list. This works around java's
 * inability to return a value and throw an exception at the same time. Exceptions in KFS are generally needed to force the
 * framework to rollback a transaction.
 */
public class EnterpriseFeederStatusAndErrorMessagesWrapper {
    private List<Message> errorMessages;
    private EnterpriseFeederStatus status;
    private String doneFileName;
    private String reconFileName;
    private String dataFileName;

    /**
     * Constructs a EnterpriseFeederStatusAndErrorMessagesWrapper, initializing values to null
     */
    public EnterpriseFeederStatusAndErrorMessagesWrapper() {
        errorMessages = null;
        status = null;
    }

    /**
     * Gets the errorMessages attribute.
     * 
     * @return Returns the errorMessages.
     */
    public List<Message> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets the errorMessages attribute value.
     * 
     * @param errorMessages The errorMessages to set.
     */
    public void setErrorMessages(List<Message> errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * Gets the status attribute.
     * 
     * @return Returns the status.
     */
    public EnterpriseFeederStatus getStatus() {
        return status;
    }

    /**
     * Sets the status attribute value.
     * 
     * @param status The status to set.
     */
    public void setStatus(EnterpriseFeederStatus status) {
        this.status = status;
    }

    public void setFileNames(File dataFile, File reconFile, File doneFile) {
        if (dataFile != null) {
            dataFileName = dataFile.getName();
        }
        if (reconFile != null) {
            reconFileName = reconFile.getName();
        }
        if (doneFile != null) {
            doneFileName = doneFile.getName();
        }
    }

    /**
     * Gets the doneFileName attribute. 
     * @return Returns the doneFileName.
     */
    public String getDoneFileName() {
        return doneFileName;
    }

    /**
     * Gets the reconFileName attribute. 
     * @return Returns the reconFileName.
     */
    public String getReconFileName() {
        return reconFileName;
    }

    /**
     * Gets the dataFileName attribute. 
     * @return Returns the dataFileName.
     */
    public String getDataFileName() {
        return dataFileName;
    }
}
