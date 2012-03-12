/*
 * Copyright 2009 The Kuali Foundation
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
