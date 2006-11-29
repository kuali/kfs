/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/impl/CorrectionCriteriaDaoOjb.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.dao.ojb;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.PropertyConstants;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class CorrectionCriteriaDaoOjb extends PersistenceBrokerDaoSupport implements CorrectionCriteriaDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionCriteriaDaoOjb.class);

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionCriteriaDao#delete(org.kuali.module.gl.bo.CorrectionCriteria)
     */
    public void delete(CorrectionCriteria criterion) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(criterion);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionCriteriaDao#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String, java.lang.Integer)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String documentNumber, Integer correctionGroupLineNumber) {
        LOG.debug("findByDocumentNumberAndCorrectionGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", correctionGroupLineNumber);

        Class clazz = CorrectionCriteria.class;
        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        List returnList = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return returnList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.CorrectionCriteriaDao#save(org.kuali.module.gl.bo.CorrectionCriteria)
     */
    public void save(CorrectionCriteria criterion) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(criterion);
    }
}
