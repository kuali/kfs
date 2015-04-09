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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CashControlDetailDaoOjb extends PlatformAwareDaoBaseOjb implements CashControlDetailDao {

    /**
     * 
     * @see org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao#getCashControlDetailByRefDocNumber(java.lang.String)
     */
    public CashControlDetail getCashControlDetailByRefDocNumber(String referenceDocumentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.CashControlDetailFields.REFERENCE_FINANCIAL_DOC_NBR, referenceDocumentNumber);
        
        Query query = QueryFactory.newQuery(CashControlDetail.class, criteria);
        return (CashControlDetail) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

}
