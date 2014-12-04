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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CollectionEvent;

/**
 * DAO interface for Collection Event.
 */
public interface CollectionEventDao {

    /**
     * Gets the events based on the field values given in the map and documentNumberToExclude.
     *
     * @param fieldValues The map containing field values to filter out the collection events.
     * @param documentNumberToExclude Document number that will be filtered out of the results.
     *
     * @return Returns the collection of collection events which match the given map and indicator.
     */
    public Collection<CollectionEvent> getMatchingEventsByCollection(Map fieldValues, String documentNumberToExclude);

}
