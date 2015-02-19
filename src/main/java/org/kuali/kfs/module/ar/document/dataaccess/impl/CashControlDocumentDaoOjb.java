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
