/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.dao.ojb;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.dao.AssetComponentDao;

public class AssetComponentDaoOjb extends PlatformAwareDaoBaseOjb implements AssetComponentDao {

    public Integer getMaxSquenceNumber(AssetComponent assetComponent) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(CamsConstants.CAPITAL_ASSET_NUMBER, assetComponent.getCapitalAssetNumber());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(assetComponent.getClass(), criteria);
        query.setAttributes(new String[] { "max(" + CamsConstants.COMPONENT_NUMBER + ")" });
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        Integer maxSequenceNumber = Integer.valueOf(0);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxSequenceNumber = ((BigDecimal) data[0]).intValue();
            }
        }
        return maxSequenceNumber;
    }

}
