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
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GLInterfaceBatchStatisticsReportDetailTableRow extends TransientBusinessObjectBase {
    protected String documentType;
    protected long glEntriesGenerated;
    protected long numberOfExceptions;

    public GLInterfaceBatchStatisticsReportDetailTableRow() {
        documentType = "";
        glEntriesGenerated = 0;
        numberOfExceptions = 0;
    }

    /**
     * Gets the documentType attribute.
     * @return Returns the documentType.
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute.
     * @return Returns the documentType.
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the gLEntriesGenerated attribute.
     * @return Returns the gLEntriesGenerated.
     */
    public long getGlEntriesGenerated() {
        return glEntriesGenerated;
    }

    /**
     * Sets the gLEntriesGenerated attribute.
     * @return Returns the gLEntriesGenerated.
     */
    public void setGlEntriesGenerated(long glEntriesGenerated) {
        this.glEntriesGenerated = glEntriesGenerated;
    }

    /**
     * Gets the numberOfExceptions attribute.
     * @return Returns the numberOfExceptions.
     */
    public long getNumberOfExceptions() {
        return numberOfExceptions;
    }

    /**
     * Sets the numberOfExceptions attribute.
     * @return Returns the numberOfExceptions.
     */
    public void setNumberOfExceptions(long numberOfExceptions) {
        this.numberOfExceptions = numberOfExceptions;
    }

    /**
     * method to increase the count of number of GL entries
     */
    public void increaseGLEntriesGeneratedCount() {
        this.glEntriesGenerated++;
    }

    /**
     * method to increase the count of number of exceptions
     */
    public void increaseNumberOfExceptionsCount() {
        this.numberOfExceptions++;
    }
}
