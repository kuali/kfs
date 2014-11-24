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
package org.kuali.kfs.sys.batch.dataaccess;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;

/**
 * Provides data access methods for the fiscal year maker process
 */
public interface FiscalYearMakersDao {

    /**
     * Clears out records for the new year and object being copied
     * 
     * @param baseYear fiscal year that we are copying
     * @param objectFiscalYearMaker FiscalYearMaker implementation for the object we are copying
     */
    public void deleteNewYearRows(Integer baseYear, FiscalYearMaker objectFiscalYearMaker);

    /**
     * Populates records for the new year and object
     * 
     * @param baseYear fiscal year that we are copying
     * @param objectFiscalYearMaker FiscalYearMaker implementation for the object we are copying
     * @param replaceMode indicates whether records found for the new year should be replaced or left alone
     * @param parentKeysWritten Map that contains class as key and Set of of primary key strings representing records written
     * @param isParentClass indicates whether the class being copied is a parent to another FYM class that will be copied
     * @return Collection of error messages encountered while attempting to copy a record
     */
    public Collection<String> createNewYearRows(Integer baseYear, FiscalYearMaker objectFiscalYearMaker, boolean replaceMode, Map<Class<? extends FiscalYearBasedBusinessObject>, Set<String>> parentKeysWritten, boolean isParentClass) throws Exception;

}
