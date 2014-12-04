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
package org.kuali.kfs.module.endow.dataaccess;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.Security;

public interface SecurityDao {

    /**
     * Gets all the securities for which the next income pay date is current date.
     * 
     * @param currentDate the current date
     * @return
     */
    public List<Security> getAllSecuritiesWithNextPayDateEqualCurrentDate(Date currentDate);

    /**
     * Gets securities whose next income pay date is equal to the current date and whose frequency code is valid
     * 
     * @param currentDate the currentDate
     * @return List<Security>
     */
    public List<Security> getSecuritiesWithNextPayDateEqualToCurrentDate(Date currentDate);

    /**
     * Gets all securities with a class code in the list given as input and with the number of units greater than zero.
     * 
     * @param classCodes the list of class codes to use to retrieve securities
     * @return all securities that meet the criteria
     */
    public List<Security> getSecuritiesByClassCodeWithUnitsGreaterThanZero(List<String> classCodes);

    /**
     * Gets a collection of securities for a given securityclasscode (SEC_CLS_CD)
     * 
     * @param securityClassCode
     * @return Collection<Security>
     */
    public Collection<Security> getSecuritiesBySecurityClassCode(String securityClassCode);

}
