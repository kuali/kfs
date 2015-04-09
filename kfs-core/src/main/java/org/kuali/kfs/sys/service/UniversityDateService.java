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
