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
package org.kuali.kfs.module.cg.service;

import java.util.Map;

/**
 * Service with methods related to Contracts & Grants Lookups.
 */
public interface ContractsAndGrantsLookupService {

    /**
     * Attempt to find a Principal using the user name passed in. If found, put the principal Id in the search fields
     * and remove the dummy attribute (user name).
     *
     * @param fieldValues
     * @param userNameField
     * @param universalUserIdField
     * @return true if user name was empty or principal was found, false if no principal was found
     */
    public boolean setupSearchFields(Map<String, String> fieldValues, String userNameField, String universalUserIdField);

}
