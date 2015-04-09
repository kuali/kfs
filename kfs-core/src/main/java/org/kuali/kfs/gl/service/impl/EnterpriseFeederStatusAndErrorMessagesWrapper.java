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
