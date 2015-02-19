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

import org.kuali.kfs.gl.businessobject.OriginEntrySource;

/**
 * An interface for helping OriginEntrySource objects interact with the database
 */

public interface OriginEntrySourceDao {
    /**
     * Fetches all origin entry full records in the database
     * 
     * @return a Collection of all origin entry source records
     */
    public Collection findAll();

    /**
     * Finds an origin entry source record based on its source code
     * 
     * @param code the code of the origin entry source record to find
     * @return an Origin Entry Source record if found, or null if not found
     * @return
     */
    public OriginEntrySource findBySourceCode(String code);
}
