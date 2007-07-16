/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.RequisitionDao;
import org.kuali.module.purap.document.RequisitionDocument;


/**
 * This class is the OJB implementation of the ProjectCodeDao interface.
 */
public class RequisitionDaoOjb extends PlatformAwareDaoBaseOjb implements RequisitionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDaoOjb.class);

    public RequisitionDocument getRequisitionById(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        RequisitionDocument r = (RequisitionDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(RequisitionDocument.class, criteria));
        if (r != null) {
            r.refreshNonUpdateableReferences();
        }
        return r;
      }
    
    public String getDocumentNumberForRequisitionId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        RequisitionDocument r = (RequisitionDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(RequisitionDocument.class, criteria));
        if (r != null) {
            return r.getDocumentNumber();
        }
        return null;
    }

}
