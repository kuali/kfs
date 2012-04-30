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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.Date;
import java.util.Map;

/**
 * An interface to methods needed to join transaction related tables to create records
 */
public interface StatusCodeAndDescriptionForPurapDocumentsDao {
    
    /**
     * Retrieves the status code and status description for requistions.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getRequisitionDocumentStatuses();
    
    /**
     * The workflowdocument is updated with new application document status and
     * gets set with new application document status modified date.
     * 
     * @param documentNumber
     * @param applicationDocumentStatus
     * @param applicationDocumentStatusModifiedDate
     * @return true if successful else return false
     */
    public boolean updateAndSaveMigratedApplicationDocumentStatuses(String documentNumber, String applicationDocumentStatus, Date applicationDocumentStatusModifiedDate);
    
}
