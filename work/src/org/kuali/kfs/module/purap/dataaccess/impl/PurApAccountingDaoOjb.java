/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PaymentRequestSummaryAccount;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.PurApAccountingDao;

/**
 * OJB Implementation of PurApAccountingDao.
 */
public class PurApAccountingDaoOjb extends PlatformAwareDaoBaseOjb implements PurApAccountingDao {

    /**
     * @see org.kuali.module.purap.dao.PurApAccountingDao#getAccountingLinesForItem(org.kuali.module.purap.bo.PurApItem)
     */
    public List getAccountingLinesForItem(PurApItem item) {
        String itemIdentifier = Integer.toString(item.getItemIdentifier());
        Class clazz = item.getAccountingLineClass();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("itemIdentifier", itemIdentifier);
        // if it's a purchaseOrderItem, we need to make sure we're getting for the right doc number
        if (item instanceof PurchaseOrderItem) {
            criteria.addEqualTo("documentNumber", ((PurchaseOrderItem) item).getDocumentNumber());
        }

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        Collection lines = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return new ArrayList(lines);

    }

    /**
     * @see org.kuali.module.purap.dao.PurApAccountingDao#deleteSummaryAccounts(java.lang.Integer)
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier) {

        if (purapDocumentIdentifier != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, purapDocumentIdentifier);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PaymentRequestSummaryAccount.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }
    
}
