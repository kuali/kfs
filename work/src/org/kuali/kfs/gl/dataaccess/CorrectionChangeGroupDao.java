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
package org.kuali.kfs.gl.dataaccess;

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;

/**
 * a DAO interface that declares methods needed for CorrectionChangeGroups to deal with the database 
 */
public interface CorrectionChangeGroupDao {

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
