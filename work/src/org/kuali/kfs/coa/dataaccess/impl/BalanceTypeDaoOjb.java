/*
 * Copyright 2006 The Kuali Foundation
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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.dataaccess.BalanceTypeDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link BalanceTypeDao} data access methods using Ojb
 */
public class BalanceTypeDaoOjb extends PlatformAwareDaoBaseOjb implements BalanceTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypeDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.BalanceTypeDao#getEncumbranceBalanceTypes()
     * @return a list of {@link BalanceTyp} that hare tied to encumbrances
     */
    public Collection<BalanceType> getEncumbranceBalanceTypes() {
        LOG.debug("getEncumbranceBalanceTypes() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("finBalanceTypeEncumIndicator", true);

        Query q = QueryFactory.newQuery(BalanceType.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }
}
