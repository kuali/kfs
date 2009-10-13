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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.UniversityDate;

/**
 * 
 * This service interface defines methods that a UniversityDateService implementation must provide.
 * 
 */
public interface UniversityDateService {
    
    /**
     * Returns the current university date.
     * 
     * @return The current university date.
     */
    public UniversityDate getCurrentUniversityDate();
    
    /**
     * Given a Date, returns the fiscal year corresponding to that date.
     * 
     * @return Fiscal year associated with the given Date.
     * @throws IllegalArgumentException if the given Date is null.
     */
    public Integer getFiscalYear(java.util.Date date);

    /**
     * Returns the first date of the specified Fiscal Year.
     * 
     * @param fiscalYear The fiscal year to retrieve the first date for.
     * @return java.util.Date The first date of the fiscal year given.
     */
    public java.util.Date getFirstDateOfFiscalYear(Integer fiscalYear);

    /**
     * Returns the last date of the specified Fiscal Year.
     * 
     * @param fiscalYear The fiscal year to retrieve the last date for.
     * @return java.util.Date The last date of the fiscal year given.
     */
    public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear);

    /**
     * Returns the current fiscal year.
     * 
     * @return The current fiscal year
     */
    public Integer getCurrentFiscalYear();

}
