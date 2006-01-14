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
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntrySourceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author Laran Evans <lc278@cs.cornell.edu>
 * @version $Id: OriginEntrySourceDaoOjb.java,v 1.1 2006-01-14 19:35:43 abyrne Exp $
 * 
 */

public class OriginEntrySourceDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntrySourceDao {

	public OriginEntrySourceDaoOjb() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.OriginEntrySourceDao#findAll()
	 */
	public Collection findAll() {
	    QueryByCriteria query = QueryFactory.newQuery(OriginEntrySource.class, (Criteria) null);//"SELECT * FROM GL_ORIGIN_ENTRY_SRC_T");
	    Collection thawed = getPersistenceBrokerTemplate().getCollectionByQuery(query);
	    //Collection frozen = Collections.unmodifiableCollection(thawed);
	    return thawed;
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.OriginEntrySourceDao#findBySourceCode(java.lang.String)
	 */
	public OriginEntrySource findBySourceCode(String code) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code);
		QueryByCriteria query = QueryFactory.newQuery(OriginEntrySource.class, criteria);
		return (OriginEntrySource) getPersistenceBrokerTemplate().getObjectByQuery(query);
	}

}
