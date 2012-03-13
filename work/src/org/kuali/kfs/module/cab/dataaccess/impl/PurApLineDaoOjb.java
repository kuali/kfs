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
package org.kuali.kfs.module.cab.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PurApLineDaoOjb extends PlatformAwareDaoBaseOjb implements PurApLineDao {

    public Integer getMaxCabLineNumber(String documentNumber, Integer purApLineItemIdentifier) {
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, purApLineItemIdentifier);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PurchasingAccountsPayableItemAsset.class, criteria);
        query.setAttributes(new String[] { "max(" + CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAPITAL_ASSET_BUILDER_LINE_NUMBER+")" });
        
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        Integer maxCabLineNumber = Integer.valueOf(0);
        
        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxCabLineNumber = ((BigDecimal)data[0]).intValue();
            }
        }
        return maxCabLineNumber;
    }
}
