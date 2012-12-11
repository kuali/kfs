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
