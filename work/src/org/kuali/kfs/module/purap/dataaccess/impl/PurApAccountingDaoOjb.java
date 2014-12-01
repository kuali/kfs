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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * OJB Implementation of PurApAccountingDao.
 */
public class PurApAccountingDaoOjb extends PlatformAwareDaoBaseOjb implements PurApAccountingDao {

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#getAccountingLinesForItem(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public List getAccountingLinesForItem(PurApItem item) {
        Class clazz = item.getAccountingLineClass();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("itemIdentifier", item.getItemIdentifier());

        // if it's a purchaseOrderItem, we need to make sure we're getting for the right doc number
        if (item instanceof PurchaseOrderItem) {
            criteria.addEqualTo("documentNumber", ((PurchaseOrderItem) item).getDocumentNumber());
        }

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        Collection lines = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return new ArrayList(lines);

    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#deleteSummaryAccountsbyPaymentRequestIdentifier(java.lang.Integer)
     */
    public void deleteSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        if (paymentRequestIdentifier != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PurapPropertyConstants.PAYMENT_REQUEST_ID, paymentRequestIdentifier);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(AccountsPayableSummaryAccount.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#deleteSummaryAccountsbyCreditMemoIdentifier(java.lang.Integer)
     */
    public void deleteSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier) {
        if (creditMemoIdentifier != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PurapPropertyConstants.CREDIT_MEMO_ID, creditMemoIdentifier);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(AccountsPayableSummaryAccount.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

}
