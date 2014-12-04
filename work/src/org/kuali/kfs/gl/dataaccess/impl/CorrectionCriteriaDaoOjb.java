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
