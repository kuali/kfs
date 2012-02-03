/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.sys.dataaccess;

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.sys.businessobject.UniversityDate;

/**
 * An DAO interface declaring methods needed by UniversityDates to interact with the database
 */
public interface UniversityDateDao {

    /**
     * Returns the last university date for a given fiscal year
     * 
     * @param fiscalYear the fiscal year to find the last date for
     * @return a UniversityDate record for the last day in the given fiscal year, or null if nothing can be found
     */
    public UniversityDate getLastFiscalYearDate(Integer fiscalYear);

    /**
     * Returns the first university date for a given fiscal year
     * 
     * @param fiscalYear the fiscal year to find the first date for
     * @return a UniversityDate record for the first day of the given fiscal year, or null if nothing can be found
     */
    public UniversityDate getFirstFiscalYearDate(Integer fiscalYear);
}
