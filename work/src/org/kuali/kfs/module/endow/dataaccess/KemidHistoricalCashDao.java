/*
 * Copyright 2011 The Kuali Foundation.
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
