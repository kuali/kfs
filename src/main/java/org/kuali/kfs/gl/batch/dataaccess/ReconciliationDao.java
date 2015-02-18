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
package org.kuali.kfs.gl.batch.dataaccess;

import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;

/**
 * This class is used to support the enterprise feeder reconciliation process
 */
public interface ReconciliationDao {
    /**
     * Converts a list of DB column names to a list of java attribute names. The returned list is the same size as arrap parameter
     * 
     * @param clazz a class for the OriginEntryFull class
     * @param columnNames an array of database columns
     * @param caseInsensitive whether to do matching
     * @return for every valid index in the return value and the array, the value in the array is the db column name, and the value
     *         in the list is the java attribute name
     */
    public List<String> convertDBColumnNamesToJavaName(Class<? extends OriginEntryFull> clazz, String[] columnNames, boolean caseInsensitive);
}
