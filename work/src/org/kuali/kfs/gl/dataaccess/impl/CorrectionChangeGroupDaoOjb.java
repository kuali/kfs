/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class CorrectionChangeGroupDaoOjb extends PersistenceBrokerDaoSupport implements CorrectionChangeGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeGroupDaoOjb.class);

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#delete(org.kuali.module.gl.bo.CorrectionChangeGroup)
     */
    public void delete(CorrectionChangeGroup group) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(group);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentNumber(java.lang.String)
     */
    public Collection findByDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("financialDocumentNumber", documentNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String, java.lang.Integer)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String documentNumber, Integer CorrectionChangeGroupNumber) {
        LOG.debug("findByDocumentNumberAndCorrectionChangeGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("financialDocumentNumber", documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", CorrectionChangeGroupNumber);
     
        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return (CorrectionChangeGroup) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#save(org.kuali.module.gl.bo.CorrectionChangeGroup)
     */
    public void save(CorrectionChangeGroup group) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(group);
    }
}
