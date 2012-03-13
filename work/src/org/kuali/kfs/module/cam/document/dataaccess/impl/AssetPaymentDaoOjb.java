/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.dataaccess.AssetPaymentDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class AssetPaymentDaoOjb extends PlatformAwareDaoBaseOjb implements AssetPaymentDao {

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.dataaccess.AssetPaymentDao#getMaxSquenceNumber(java.lang.Long)
     */
    public Integer getMaxSquenceNumber(Long capitalAssetNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER, capitalAssetNumber);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AssetPayment.class, criteria);
        
        query.setAttributes(new String[] { "max(" + CamsPropertyConstants.AssetPayment.PAYMENT_SEQ_NUMBER + ")" });
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
