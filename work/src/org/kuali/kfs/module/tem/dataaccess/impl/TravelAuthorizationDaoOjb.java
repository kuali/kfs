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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelAuthorizationDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class TravelAuthorizationDaoOjb extends PlatformAwareDaoBaseOjb implements TravelAuthorizationDao {



    @Override
    public List<TravelAuthorizationDocument> findTravelAuthorizationByTraveler(Integer temProfileId) {

        final Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TEM_PROFILE_ID, temProfileId);
        criteria.addNotIn(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, Arrays.asList(DocumentStatusCodes.INITIATED));
        return  (List<TravelAuthorizationDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(TravelAuthorizationDocument.class, criteria));

    }

}
