/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.dataaccess.CorrectionCriteriaDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of CorrectionCriteriaDao
 */
public class CorrectionCriteriaDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionCriteriaDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionCriteriaDaoOjb.class);

    /**
     * Deletes a correction criterion
     * 
     * @param criterion the criterion to delete
     * @see org.kuali.kfs.gl.dataaccess.CorrectionCriteriaDao#delete(org.kuali.kfs.gl.businessobject.CorrectionCriteria)
     */
    public void delete(CorrectionCriteria criterion) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(criterion);
    }

    /**
     * Queries the database for a list of all the correction criteria associated with the given GLCP document and correction group
     * 
     * @param documentNumber the GLCP document number of correction criteria to find
     * @param correctionGroupLineNumber the correction group of correction criteria to find
     * @return a List of collection criteria
     * @see org.kuali.kfs.gl.dataaccess.CorrectionCriteriaDao#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String,
     *      java.lang.Integer)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String documentNumber, Integer correctionGroupLineNumber) {
        LOG.debug("findByDocumentNumberAndCorrectionGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", correctionGroupLineNumber);

        Class clazz = CorrectionCriteria.class;
        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        List returnList = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return returnList;
    }

}
