/*
 * Copyright 2011 The Kuali Foundation.
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

import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for CashControlDocumentDao
 */
public class CashControlDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CashControlDocumentDao {


    /**
     * Retrieves UNFINAL cash control document by criteria.
     *
     * @see org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao#getCashControlDocumentByCriteria(org.apache.ojb.broker.query.Criteria)
     */
    @Override
    public CashControlDocument getCashControlDocument(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new CashControlDocument());
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(CashControlDocument.class, criteria);

        CashControlDocument cashControlDocument = (CashControlDocument) getPersistenceBrokerTemplate().getObjectByQuery(qbc);

        return cashControlDocument;
    }
}
