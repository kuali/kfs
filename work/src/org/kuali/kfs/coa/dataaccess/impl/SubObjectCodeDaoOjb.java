/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.dataaccess.SubObjectCodeDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;


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
    public SubObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("financialObjectCode", financialObjectCode);
        criteria.addEqualTo("financialSubObjectCode", financialSubObjectCode);

        return (SubObjectCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubObjectCode.class, criteria));
    }
}
