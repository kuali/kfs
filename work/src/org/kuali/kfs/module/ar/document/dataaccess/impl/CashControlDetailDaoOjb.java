/*
 * Copyright 2009 The Kuali Foundation
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
