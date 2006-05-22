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
import org.springframework.orm.ojb.PersistenceBrokerTemplate;

public class CorrectionChangeGroupDaoOjb extends PersistenceBrokerTemplate implements CorrectionChangeGroupDao {

	public CorrectionChangeGroupDaoOjb() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#delete(org.kuali.module.gl.bo.CorrectionChangeGroup)
	 */
	public void delete(CorrectionChangeGroup group) {
		super.delete(group);
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentNumber(java.lang.String)
	 */
	public Collection findByDocumentNumber(String documentNumber) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("DOC_HDR_ID", documentNumber);
		
		Class clazz = CorrectionChangeGroup.class;
		QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
		
		Collection groups = getCollectionByQuery(query);
		return groups;
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentHeaderIdAndCorrectionChangeGroupId(java.lang.Integer, java.lang.Integer)
	 */
	public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String documentNumber, Integer CorrectionChangeGroupNumber) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("DOC_HDR_ID", documentNumber);
		criteria.addEqualTo("GL_COR_CHG_GRP_LN_NBR", CorrectionChangeGroupNumber);
		
		Class clazz = CorrectionChangeGroup.class;
		QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
		
		CorrectionChangeGroup group = (CorrectionChangeGroup) getObjectByQuery(query);
		return group;
	}

	/* (non-Javadoc)
	 * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#save(org.kuali.module.gl.bo.CorrectionChangeGroup)
	 */
	public void save(CorrectionChangeGroup group) {
		super.store(group);
	}
	
}
