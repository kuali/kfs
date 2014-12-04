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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * The OJB implementation of CorrectionChangeGroupDao
 */
public class CorrectionChangeGroupDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionChangeGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeGroupDaoOjb.class);

    /**
     * Deletes an unlucky correction change group
     * 
     * @param group the group to delete
     * @see org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao#delete(org.kuali.kfs.gl.businessobject.CorrectionChangeGroup)
     */
    public void delete(CorrectionChangeGroup group) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(group);
    }

    /**
     * Finds all of the correction change groups associated with a document.
     * 
     * @param documentNumber the document number of a GLCP document
     * @return a Collection of CorrectionChangeGroup records
     * @see org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao#findByDocumentNumber(java.lang.String)
     */
    public Collection findByDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * Finds the specific group associated with the given document with the given group number
     * 
     * @param documentNumber the document number of the correction change group to retrieve
     * @param CorrectionChangeGroupNumber the number of the group to retrieve
     * @return the found CorrectionChangeGroup, or null if not found
     * @see org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String,
     *      java.lang.Integer)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String documentNumber, Integer CorrectionChangeGroupNumber) {
        LOG.debug("findByDocumentNumberAndCorrectionChangeGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", CorrectionChangeGroupNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return (CorrectionChangeGroup) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }
}
