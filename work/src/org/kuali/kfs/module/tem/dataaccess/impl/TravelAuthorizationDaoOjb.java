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

import java.util.Arrays;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelAuthorizationDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class TravelAuthorizationDaoOjb extends PlatformAwareDaoBaseOjb implements TravelAuthorizationDao {



    @Override
    public List<TravelAuthorizationDocument> findTravelAuthorizationByTraveler(Integer temProfileId) {

        final Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TEM_PROFILE_ID, temProfileId);
        criteria.addNotIn("documentHeader.financialDocumentStatusCode", Arrays.asList(DocumentStatusCodes.INITIATED));
        return  (List<TravelAuthorizationDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(TravelAuthorizationDocument.class, criteria));

    }

}
