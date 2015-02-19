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
