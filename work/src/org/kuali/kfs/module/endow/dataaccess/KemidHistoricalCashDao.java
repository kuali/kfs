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

import java.sql.Date;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.rice.kns.util.KualiInteger;

public interface KemidHistoricalCashDao {

    List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger medId);

    /**
     * method to retrieve a list of END_HIST_CSH_T records for the given kemids and monthendids
     * @param kemid
     * @param beginningMed
     * @param endingMed
     */
    List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemid, KualiInteger beginningMed, KualiInteger endingMed);
}
