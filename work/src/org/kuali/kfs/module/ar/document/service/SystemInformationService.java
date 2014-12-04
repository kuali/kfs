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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.SystemInformation;

public interface SystemInformationService {
    
    /**
     * Get system information by lockbox number for current fiscal year
     * 
     * @param lockboxNumber
     * @return system information
     */
    public SystemInformation getByLockboxNumberForCurrentFiscalYear(String lockboxNumber);
    
    /**
     * Get system information by lock box number
     * 
     * @param lockboxNumber
     * @param universityFiscalYear
     * @return system information
     */
    public SystemInformation getByLockboxNumber(String lockboxNumber, Integer universityFiscalYear);
    
    /**
     * Get system information from chart code, org code and fiscal year.
     * 
     * @param chartCode
     * @param orgCode
     * @param fiscalYear
     * @return system information
     */
    public SystemInformation getByProcessingChartOrgAndFiscalYear(String chartCode, String orgCode, Integer fiscalYear);
    
    /**
     * Get count from chartCode, orgCode and lockbox number
     * 
     * @param chartCode
     * @param orgCode
     * @param lockboxNumber
     * @return
     */
    public int getCountByChartOrgAndLockboxNumber(String chartCode, String orgCode, String lockboxNumber);
}
