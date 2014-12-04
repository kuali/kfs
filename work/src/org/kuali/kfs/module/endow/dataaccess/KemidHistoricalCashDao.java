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

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.rice.core.api.util.type.KualiInteger;

public interface KemidHistoricalCashDao {

    /**
     * Gets a collection of historical cash object
     * 
     * @param kemids
     * @param medId
     * @return
     */
    List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger medId);

    /**
     * Gets a collection of historical cash
     * 
     * @param kemid
     * @param beginningMed
     * @param endingMed
     * @return
     */
    List<KemidHistoricalCash> getKemidsFromHistoryCash(String kemid, KualiInteger beginningMed, KualiInteger endingMed);
    
    /**
     * Gets a collection of historical cash
     * 
     * @param beginningMed
     * @param endingMed
     * @return
     */
    List<KemidHistoricalCash> getKemidsFromHistoryCash(KualiInteger beginningMed, KualiInteger endingMed);
    
    /**
     * method to retrieve a list of END_HIST_CSH_T records for the given kemids and monthendids
     * @param kemid
     * @param beginningMed
     * @param endingMed
     */
    List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemid, KualiInteger beginningMed, KualiInteger endingMed);
}
