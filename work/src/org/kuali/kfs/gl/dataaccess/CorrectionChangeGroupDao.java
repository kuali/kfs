/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.util.Collection;

import org.kuali.module.gl.bo.CorrectionChangeGroup;

/**
 * a DAO interface that declares methods needed for CorrectionChangeGroups to deal with the database 
 */
public interface CorrectionChangeGroupDao {
    /**
     * Saves a Correction Group to the database
     * 
     * @param group the group to save
     */
    void save(CorrectionChangeGroup group);

    /**
     * Deletes a CorrectionChangeGroup from the database
     * 
     * @param group the group to delete
     */
    void delete(CorrectionChangeGroup group);

    /**
     * Finds all CorrectionChange groups associated with a document
     * 
     * @param documentNumber the document number of a GLCP document
     * @return a Collection of CorrectionChangeGroup records
     */
    Collection findByDocumentNumber(String documentNumber);

    /**
     * Finds a correction change group, based on GLCP document number and the group number
     * 
     * @param documentNumber the document number of the correction change group to retrieve
     * @param CorrectionChangeGroupNumber the number of the group to retrieve
     * @return the found CorrectionChangeGroup, or null if not found
     */
    CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String documentNumber, Integer CorrectionChangeGroupNumber);
}
