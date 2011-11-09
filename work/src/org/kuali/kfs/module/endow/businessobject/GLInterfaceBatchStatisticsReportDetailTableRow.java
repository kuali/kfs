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

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GLInterfaceBatchStatisticsReportDetailTableRow extends TransientBusinessObjectBase {
    private String documentType;
    private long gLEntriesGenerated;
    private long numberOfExceptions;

    public GLInterfaceBatchStatisticsReportDetailTableRow() {
        documentType = "";
        gLEntriesGenerated = 0;
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
    public long getGLEntriesGenerated() {
        return gLEntriesGenerated;
    }

    /**
     * Sets the gLEntriesGenerated attribute. 
     * @return Returns the gLEntriesGenerated.
     */    
    public void setGLEntriesGenerated(long gLEntriesGenerated) {
        this.gLEntriesGenerated = gLEntriesGenerated;
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
        this.gLEntriesGenerated++;
    }

    /**
     * method to increase the count of number of exceptions
     */
    public void increaseNumberOfExceptionsCount() {
        this.numberOfExceptions++;
    }
    
    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("documentType",this.getDocumentType());
        pks.put("gLEntriesGenerated",this.getGLEntriesGenerated());
        pks.put("numberOfExceptions",this.getNumberOfExceptions());
        
        return pks;
    }
}
