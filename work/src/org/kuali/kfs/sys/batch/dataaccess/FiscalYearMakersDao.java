/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
