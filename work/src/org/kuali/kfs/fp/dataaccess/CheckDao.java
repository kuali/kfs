/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.dataaccess;

import java.util.Collection;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;

/**
 * The data access interface for persisting Check objects.
 */
public interface CheckDao {

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
    public Collection<CheckBase> findByDocumentHeaderId(String documentHeaderId);
}
