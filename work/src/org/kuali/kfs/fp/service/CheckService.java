/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.module.financial.bo.Check;

/**
 * This service interface defines methods that a Check service implementation must provide.
 * 
 * 
 */
public interface CheckService {
    
    /**
     * Retrieves a list of checks for the given document id.
     * 
     * @param documentHeaderId The document header id.
     * @return A list of Check instances associated with the doc header id provided.
     */
    public List getByDocumentHeaderId(String documentHeaderId);

    /**
     * Saves a check.
     * 
     * @param check The check to be saved.
     * @return The saved Check instance.
     */
    public Check save(Check check);


    /**
     * Deletes the given Check.
     * 
     * @param check The check to be deleted.
     */
    public void deleteCheck(Check check);
}