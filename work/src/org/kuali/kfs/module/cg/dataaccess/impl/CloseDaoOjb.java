/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.dao.CloseDao;
import org.kuali.Constants;
import org.springmodules.orm.ojb.PersistenceBrokerTemplate;

import java.util.Iterator;
import java.util.Collection;

public class CloseDaoOjb extends PlatformAwareDaoBaseOjb implements CloseDao {

    public Close getMaxApprovedClose() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", Constants.DocumentStatusCodes.APPROVED);
        QueryByCriteria query = QueryFactory.newQuery(Close.class, criteria);
        query.addOrderByDescending("documentNumber");
        PersistenceBrokerTemplate template = getPersistenceBrokerTemplate();
        Iterator i = template.getIteratorByQuery(query);
        if(null != i) {
            if(i.hasNext()) {
                Close close = (Close) i.next();
                if(null == close.getAwardClosedCount()) {
                    close.setAwardClosedCount(0L);
                }
                if(null == close.getProposalClosedCount()) {
                    close.setProposalClosedCount(0L);
                }
                return close;
            }
        }
        return null;
    }

    public void save(Close close) {
        getPersistenceBrokerTemplate().store(close);
    }
    
}
