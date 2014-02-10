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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.dataaccess.ObjectCodeDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;


/**
 * This class is the OJB implementation of the ObjectCodeDao interface.
 */
public class ObjectCodeDaoOjb extends PlatformAwareDaoBaseOjb implements ObjectCodeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodeDaoOjb.class);


    /**
     * @see org.kuali.kfs.coa.dataaccess.ObjectCodeDao#getYearList(java.lang.String, java.lang.String)
     */
    @Override
    public List getYearList(String chartOfAccountsCode, String financialObjectCode) {

        List returnList = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("financialObjectCode", financialObjectCode);
        Collection years = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ObjectCode.class, criteria));
        for (Iterator iter = years.iterator(); iter.hasNext();) {
            ObjectCode o = (ObjectCode) iter.next();
            if (o != null) {
                returnList.add(o.getUniversityFiscalYear());
            }
        }
        return returnList;
    }

    @Override
    public Collection<ObjectCode> getObjectCodesByLevelCodes(List<String> levelCodes) {

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCodes);
        QueryByCriteria qbc = QueryFactory.newQuery(ObjectLevel.class, criteria);
        Collection<ObjectCode> objectCodes = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return objectCodes;
    }
}
