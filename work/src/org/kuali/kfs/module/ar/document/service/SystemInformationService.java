/*
 * Copyright 2008 The Kuali Foundation
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
