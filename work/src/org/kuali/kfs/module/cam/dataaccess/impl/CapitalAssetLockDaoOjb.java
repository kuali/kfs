/*
 * Copyright 2008-2009 The Kuali Foundation
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
