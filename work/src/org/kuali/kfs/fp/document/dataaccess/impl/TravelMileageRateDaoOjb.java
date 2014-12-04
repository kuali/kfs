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
package org.kuali.kfs.fp.document.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.fp.businessobject.TravelMileageRate;
import org.kuali.kfs.fp.document.dataaccess.TravelMileageRateDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the TravelMileageRate interface.
 */
public class TravelMileageRateDaoOjb extends PlatformAwareDaoBaseOjb implements TravelMileageRateDao {
    private static Logger LOG = Logger.getLogger(TravelMileageRateDaoOjb.class);

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.TravelMileageRateDao#retrieveMostEffectiveMileageRates(java.sql.Timestamp)
     */
    @Override
    public Collection<TravelMileageRate> retrieveMostEffectiveMileageRates(Date effectiveDate) {
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("disbursementVoucherMileageEffectiveDate", effectiveDate);

        QueryByCriteria queryByCriteria = new QueryByCriteria(TravelMileageRate.class, criteria);
        queryByCriteria.addOrderByDescending("disbursementVoucherMileageEffectiveDate");
        queryByCriteria.addOrderByDescending("mileageLimitAmount");

        Collection mostEffectiveRates = new ArrayList();
        Collection rates = getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
        Date mostEffectiveDate = ((TravelMileageRate) rates.iterator().next()).getDisbursementVoucherMileageEffectiveDate();
        for (Iterator iter = rates.iterator(); iter.hasNext();) {
            TravelMileageRate rate = (TravelMileageRate) iter.next();
            if (rate.getDisbursementVoucherMileageEffectiveDate().compareTo(mostEffectiveDate) == 0) {
                mostEffectiveRates.add(rate);
            }
        }

        return mostEffectiveRates;
    }


}
