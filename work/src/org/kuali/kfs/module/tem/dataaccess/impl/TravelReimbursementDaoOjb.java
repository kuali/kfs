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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelReimbursementDao;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class TravelReimbursementDaoOjb extends PlatformAwareDaoBaseOjb implements TravelReimbursementDao {
    @Override
    public List<TravelReimbursementDocument> findMatchingTrips (Integer temProfileId ,Timestamp tripBegin, Timestamp tripEnd, Integer primaryDestinationId)  {


        final Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TEM_PROFILE_ID, temProfileId);
        criteria.addEqualTo(TemPropertyConstants.TRIP_BEGIN_DT, tripBegin);
        criteria.addEqualTo(TemPropertyConstants.TRIP_END_DT, tripEnd);
        criteria.addEqualTo(TemPropertyConstants.PRIMARY_DESTINATION_ID, primaryDestinationId);
        criteria.addIn("documentHeader.financialDocumentStatusCode", Arrays.asList(DocumentStatusCodes.APPROVED,DocumentStatusCodes.ENROUTE));

        Collection<? extends TravelReimbursementDocument> documents = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(TravelReimbursementDocument.class, criteria));
        return (List<TravelReimbursementDocument>)documents;

    }
}
