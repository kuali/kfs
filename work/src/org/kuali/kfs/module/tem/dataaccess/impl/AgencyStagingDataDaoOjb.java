/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.dataaccess.AgencyStagingDataDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

public class AgencyStagingDataDaoOjb extends PlatformAwareDaoBaseOjb implements AgencyStagingDataDao {

    @Override
    public Collection<AgencyStagingData> checkForDuplicates(AgencyStagingData agencyData) {

        Criteria orCriteria = new Criteria();
        if (StringUtils.isNotEmpty(agencyData.getTripId())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.TRIP_ID, agencyData.getTripId());
            orCriteria.addOrCriteria(criteria);
        }
        if (StringUtils.isNotEmpty(agencyData.getCreditCardOrAgencyCode())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, agencyData.getCreditCardOrAgencyCode());
            orCriteria.addOrCriteria(criteria);
        }
        if (ObjectUtils.isNotNull(agencyData.getTransactionPostingDate())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyData.getTransactionPostingDate());
            orCriteria.addOrCriteria(criteria);
        }
        if (ObjectUtils.isNotNull(agencyData.getTripExpenseAmount())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyData.getTripExpenseAmount());
            orCriteria.addOrCriteria(criteria);
        }
        if (StringUtils.isNotEmpty(agencyData.getAirTicketNumber())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.AIR_TICKET_NUMBER, agencyData.getAirTicketNumber());
            orCriteria.addOrCriteria(criteria);
        }
        if (StringUtils.isNotEmpty(agencyData.getLodgingItineraryNumber())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.LODGING_ITINERARY_NUMBER, agencyData.getLodgingItineraryNumber());
            orCriteria.addOrCriteria(criteria);
        }
        if (StringUtils.isNotEmpty(agencyData.getRentalCarItineraryNumber())) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(TemPropertyConstants.RENTAL_CAR_ITINERARY_NUMBER, agencyData.getRentalCarItineraryNumber());
            orCriteria.addOrCriteria(criteria);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(AgencyStagingData.class, orCriteria));


    }

}
