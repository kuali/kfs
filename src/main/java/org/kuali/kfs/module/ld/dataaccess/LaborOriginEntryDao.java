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
package org.kuali.kfs.module.ld.dataaccess;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.dataaccess.OriginEntryDao;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;

/**
 * This is the data access object for labor origin entry.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborOriginEntry
 */
public interface LaborOriginEntryDao extends OriginEntryDao {

    /**
     * Get origin entries that belong to the given groups
     * 
     * @param groups the given origin entry groups
     * @return origin entries that belong to the given groups
     */
    Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups);

    /**
     * Get the origin entries that belong to the given group in either the consolidation manner
     * 
     * @param group the given group
     * @return the origin entries that belong to the given group in either the consolidation manner
     */
    Iterator<Object[]> getConsolidatedEntriesByGroup(OriginEntryGroup group);

    /**
     * get the count of the origin entry collection in the given groups
     * 
     * @param groups the given groups
     * @return the count of the origin entry collection in the given group
     */
    int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups);

    /**
     * This method should only be used in unit tests. It loads all the ld_lbr_origin_entry_t rows in memory into a collection. This
     * won't scale for production.
     * 
     * @return a set of labor origin entries
     */
    Collection<LaborOriginEntry> testingLaborGetAllEntries();

    /**
     * Return an iterator to all the entries in a group
     * 
     * @param oeg the given origin entry group
     * @return Iterator of entries in the specified group
     */
    Iterator<LaborOriginEntry> getLaborEntriesByGroup(OriginEntryGroup oeg, int sort);

    /**
     * Collection of entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     * @return collection of entries
     */
    Collection getMatchingEntriesByCollection(Map searchCriteria);

    /**
     * Return a collection to all the entries in the given group
     * 
     * @param group the given origin entry group
     * @return Collection of entries in the specified group
     */
    Collection<LaborOriginEntry> getEntryCollectionByGroup(OriginEntryGroup group);
    
    /**
     * Get all the Labor backup groups to scrub (ie, origin entry groups with source OriginEntrySource.LABOR_BACKUP)
     * 
     * @param groupDate the creation date of labor backup groups to find
     * @return a Collection of Labor backup groups
     */
    public Collection getLaborBackupGroups(Date groupDate);
    
    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate the date returned origin entry groups must have been created on or before
     * @return a Collection of Labor Origin Entry Groups to backup
     */
    public Collection getLaborGroupsToBackup(Date groupDate);
}
