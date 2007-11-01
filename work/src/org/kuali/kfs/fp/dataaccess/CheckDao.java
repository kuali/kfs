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
package org.kuali.module.financial.dao;

import java.util.List;

import org.kuali.module.financial.bo.Check;

/**
 * The data access interface for persisting Check objects.
 */
public interface CheckDao {
    /**
     * Saves a Check to the DB.
     * 
     * @param check
     * @return the Check that was just saved
     */
    public Check save(Check check);

    /**
     * Delete a check from the DB.
     * 
     * @param line
     */
    public void deleteCheck(Check check);

    /**
     * Retrieves a list of checks associated with a given document.
     * 
     * @param documentHeaderId
     * @return List of checks
     */
    public List findByDocumentHeaderId(String documentHeaderId);
}