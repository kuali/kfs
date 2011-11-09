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
import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * The OJB implementation of the CorrectionChangeDao
 */
public class CorrectionChangeDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionChangeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeDaoOjb.class);

    /**
     * Delete a CorrectionChange from the database
     * 
     * @param spec the CorrectionChange to delete
     * @see org.kuali.kfs.gl.dataaccess.CorrectionChangeDao#delete(org.kuali.kfs.gl.businessobject.CorrectionChange)
     */
    public void delete(CorrectionChange spec) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(spec);
    }

    /**
     * Query the database to find qualifying CorrectionChange records
     * 
     * @param documentHeaderId the document number of a GLCP document
     * @param correctionGroupLineNumber the line number of the group within the GLCP document to find correction chagnes for
     * @return a List of correction changes
     * @see org.kuali.kfs.gl.dataaccess.CorrectionChangeDao#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String,
     *      java.lang.Integer)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String documentNumber, Integer correctionGroupLineNumber) {
        LOG.debug("findByDocumentHeaderIdAndCorrectionGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", correctionGroupLineNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChange.class, criteria);

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

}
