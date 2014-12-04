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
