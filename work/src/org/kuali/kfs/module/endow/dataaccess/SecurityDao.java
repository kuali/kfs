/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.Security;

public interface SecurityDao {

    /**
     * Gets all the securities for which the next income pay date is current date.
     * 
     * @return
     */
    public List<Security> getAllSecuritiesWithNextPayDateEqualCurrentDate();

    /**
     * Gets securities whose next income pay date is equal to the current date and whose frequency code is valid
     * 
     * @return List<Security>
     */
    public List<Security> getSecuritiesWithNextPayDateEqualToCurrentDate();

    /**
     * Gets all securities with a class code in the list given as input and with the number of units greater than zero.
     * 
     * @param classCodes the list of class codes to use to retrieve securities
     * @return all securities that meet the criteria
     */
    public List<Security> getSecuritiesByClassCodeWithUnitsGreaterThanZero(List<String> classCodes);

    /**
     * Gets a collection of securities for a given securityclasscode (SEC_CLS_CD)
     * @param securityClassCode
     * @return Collection<Security>
     */
    public Collection<Security> getSecuritiesBySecurityClassCode(String securityClassCode);
    
}
