/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.SubObjectCodeDao;


/**
 * This class is the OJB implementation of the SubObjectCodeDao interface.
 */
public class SubObjectCodeDaoOjb extends PlatformAwareDaoBaseOjb implements SubObjectCodeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubObjectCodeDaoOjb.class);

    /**
     * Retrieves sub object code business object by primary key
     * 
     * @param universityFiscalYear - part of composite key
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param financialObjectCode - part of composite key
     * @param financialSubObjectCode - part of composite key
     * @return SubObjectCode
     * @see SubObjectCodeDao#getByPrimaryId(Integer, String, String, String, String)
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("financialObjectCode", financialObjectCode);
        criteria.addEqualTo("financialSubObjectCode", financialSubObjectCode);

        return (SubObjCd) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubObjCd.class, criteria));
    }
}
