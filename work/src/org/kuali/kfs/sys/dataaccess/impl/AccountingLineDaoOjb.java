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
package org.kuali.kfs.sys.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.dataaccess.AccountingLineDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 */

public class AccountingLineDaoOjb extends PlatformAwareDaoBaseOjb implements AccountingLineDao {
    private static final Logger LOG = Logger.getLogger(AccountingLineDaoOjb.class);

    /**
     * Deletes an accounting line from the DB using OJB.
     */
    @Override
    public void deleteAccountingLine(AccountingLine line) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(line);
    }

    /**
     * Retrieves accounting lines associate with a given document header ID using OJB.
     *
     * @param classname
     * @param id
     * @return
     */
    @Override
    public ArrayList findByDocumentHeaderId(Class clazz, String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);
        if (MetadataManager.getInstance().getRepository().getDescriptorFor(clazz).getFieldDescriptorByName("financialDocumentLineTypeCode") != null) {
            if (SourceAccountingLine.class.isAssignableFrom(clazz)) {
                criteria.addEqualTo("FDOC_LN_TYP_CD", KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE);
            }
            else if (TargetAccountingLine.class.isAssignableFrom(clazz)) {
                criteria.addEqualTo("FDOC_LN_TYP_CD", KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
            }
        }

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        Collection lines = findCollection(query);

        return new ArrayList(lines);
    }


    /**
     * Retrieves accounting lines associated with the given document header ID and line type code
     * @see org.kuali.kfs.sys.dataaccess.AccountingLineDao#findByDocumentHeaderIdAndLineType(java.lang.String, java.lang.String)
     */
    @Override
    public List findByDocumentHeaderIdAndLineType(Class clazz, String documentHeaderId, String lineType) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);
        criteria.addEqualTo("FDOC_LN_TYP_CD", lineType);
        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        Collection lines = findCollection(query);

        return new ArrayList(lines);
    }

    /**
     * Retrieve a Collection of Document instances found by a query.
     *
     * @param query
     * @return
     */
    protected Collection findCollection(Query query) throws DataAccessException {
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
