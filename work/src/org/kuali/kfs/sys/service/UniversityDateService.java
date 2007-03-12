/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.service;

import org.kuali.module.gl.bo.UniversityDate;

public interface UniversityDateService {
    
    /**
     * Returns the current university date
     * 
     * @return current university date
     */
    public UniversityDate getCurrentUniversityDate();
    
    /**
     * Given a Date, returns the fiscal year corresponding to that date
     * 
     * @return fiscal year for the given Date
     * @throws IllegalArgumentException if the given Date is null
     */
    public Integer getFiscalYear(java.util.Date date);
    

    /**
     * Returns the first date of the specified Fiscal Year
     * 
     * @param fiscalYear
     * @return java.util.Date
     */
    public java.util.Date getFirstDateOfFiscalYear(Integer fiscalYear);
    

    /**
     * Returns the last date of the specified Fiscal Year
     * 
     * @param fiscalYear
     * @return java.util.Date
     */
    public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear);
    

    /**
     * Returns the current fiscal year
     * 
     * @return current fiscal year
     */
    public Integer getCurrentFiscalYear();

}
