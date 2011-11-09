/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.event;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

public final class AttributedAssignSensitiveDataEvent extends AttributedDocumentEventBase {    

    private String sensitiveDataAssignmentReason;
    private List<SensitiveData> sensitiveDatasAssigned;
    
    /**
     * Constructs an AssignSensitiveDataEvent with the given errorPathPrefix, document, and sensitive data list.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param sensitiveDatasAssigned the sensitive data list to be checked for assignment
     */
    public AttributedAssignSensitiveDataEvent(String errorPathPrefix, Document document, String sensitiveDataAssignmentReason, List<SensitiveData> sensitiveDatasAssigned) {
        super("Assign sensitive data to purchase order " + getDocumentId(document), errorPathPrefix, document);
        this.sensitiveDataAssignmentReason = sensitiveDataAssignmentReason;
        this.sensitiveDatasAssigned = sensitiveDatasAssigned;
    }
    
    public String getSensitiveDataAssignmentReason() {
        return sensitiveDataAssignmentReason;
    }

    public void setSensitiveDataAssignmentReason(String sensitiveDataAssignmentReason) {
        this.sensitiveDataAssignmentReason = sensitiveDataAssignmentReason;
    }

    public List<SensitiveData> getSensitiveDatasAssigned() {
        return sensitiveDatasAssigned;
    }

    public void setSensitiveDatasAssigned(List<SensitiveData> sensitiveDatasAssigned) {
        this.sensitiveDatasAssigned = sensitiveDatasAssigned;
    }
    
}
