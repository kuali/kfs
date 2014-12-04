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
