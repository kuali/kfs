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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

/**
 * OJB implementation of RequisitionDao.
 */
public class RequisitionDaoOjb extends PlatformAwareDaoBaseOjb implements RequisitionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao#getDocumentNumberForRequisitionId(java.lang.Integer)
     */
    public String getDocumentNumberForRequisitionId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(RequisitionDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        if (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                TransactionalServiceUtils.exhaustIterator(iter);
                throw new RuntimeException();
            }
            // at this part of the code, we know there's no more elements in iterator
            return (String) cols[0];
        }
        return null;
    }

}
