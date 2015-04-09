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
package org.kuali.kfs.module.cam.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetLock;
import org.kuali.kfs.module.cam.dataaccess.CapitalAssetLockDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class CapitalAssetLockDaoOjb extends PlatformAwareDaoBaseOjb implements CapitalAssetLockDao {

    public List<String> getLockingDocumentNumbers(Collection capitalAssetNumbers, Collection documentTypeNames, String documentNumber) {
        // build the query criteria
        Criteria criteria = new Criteria();
        criteria.addIn(CabPropertyConstants.CapitalAssetLock.CAPITAL_ASSET_NUMBER,capitalAssetNumbers);

        
        if (documentTypeNames != null && !documentTypeNames.isEmpty()) {
            criteria.addIn(CabPropertyConstants.CapitalAssetLock.DOCUMENT_TYPE_NAME, documentTypeNames);
        }

        // if a docHeaderId is specified, then it will be excluded from the
        // locking representation test.
        if (StringUtils.isNotBlank(documentNumber)) {
            criteria.addNotEqualTo(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }

        // attempt to retrieve a document based off this criteria
        Collection<AssetLock> assetLocks = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AssetLock.class, criteria));

        List<String> documentNumbers = new ArrayList();
        if (!assetLocks.isEmpty()) {
            for (AssetLock assetLock : assetLocks) {
                documentNumbers.add( assetLock.getDocumentNumber());
            }
        }
        return documentNumbers;
    }

}
