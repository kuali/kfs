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
package org.kuali.kfs.module.cab.batch.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.dataaccess.PurchasingAccountsPayableItemAssetDao;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PurchasingAccountsPayableItemAssetDaoOjb extends PlatformAwareDaoBaseOjb implements PurchasingAccountsPayableItemAssetDao {

    public Integer findMaxCabLineNumber(String documentNumber, Integer accountsPayableLineItemIdentifier) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, accountsPayableLineItemIdentifier);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(PurchasingAccountsPayableItemAsset.class, criteria);

        query.setAttributes(new String[] { "max(" + CabPropertyConstants.PurchasingAccountsPayableItemAsset.CAPITAL_ASSET_BUILDER_LINE_NUMBER + ")" });
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
