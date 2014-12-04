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

import java.util.List;

import org.kuali.kfs.gl.businessobject.CorrectionChange;

/**
 * A DAO interface for CorrectionChange business objects to interact with the databse
 */
public interface CorrectionChangeDao {

    /**
     * Surprisingly, this method deletes a GLCP correction
     * 
     * @param spec the GLCP correction to delete
     */
    void delete(CorrectionChange spec);

    /**
     * Finds CorrectionChanges associated with the given document and group
     * 
     * @param documentHeaderId the document number of a GLCP document
     * @param correctionGroupLineNumber the line number of the group within the GLCP document to find correction chagnes for
     * @return a List of correction changes
     */
    List findByDocumentHeaderIdAndCorrectionGroupNumber(String documentHeaderId, Integer correctionGroupLineNumber);
}
