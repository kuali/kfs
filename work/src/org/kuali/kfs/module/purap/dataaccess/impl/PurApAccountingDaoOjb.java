/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * OJB Implementation of PurApAccountingDao.
 */
public class PurApAccountingDaoOjb extends PlatformAwareDaoBaseOjb implements PurApAccountingDao {

    private BusinessObjectService businessObjectService;
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#getAccountingLinesForItem(org.kuali.kfs.module.purap.businessobject.PurApItem)
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
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#getSummaryAccountsbyPaymentRequestIdentifier(java.lang.Integer)
     */
    public List getSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        if (paymentRequestIdentifier != null) {
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.PAYMENT_REQUEST_ID, paymentRequestIdentifier);
            return new ArrayList(businessObjectService.findMatching(AccountsPayableSummaryAccount.class, fieldValues));
        }
        return null;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurApAccountingDao#getSummaryAccountsbyCreditMemoIdentifier(java.lang.Integer)
     */
    public List getSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier) {
        if (creditMemoIdentifier != null) {
            Map fieldValues = new HashMap();
            fieldValues.put(PurapPropertyConstants.CREDIT_MEMO_ID, creditMemoIdentifier);
            return new ArrayList(businessObjectService.findMatching(AccountsPayableSummaryAccount.class, fieldValues));
        }
        return null;
    }
    
}
