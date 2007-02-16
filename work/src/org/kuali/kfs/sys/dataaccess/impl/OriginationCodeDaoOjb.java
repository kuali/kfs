/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.kfs.dao.ojb;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.dao.OriginationCodeDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class OriginationCodeDaoOjb extends PersistenceBrokerDaoSupport implements OriginationCodeDao {
    private static Logger LOG = Logger.getLogger(OriginationCodeDaoOjb.class);

    public OriginationCodeDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.dao.OriginationCodeDao#delete(org.kuali.core.bo.OriginationCode)
     */
    public void delete(OriginationCode code) {
        getPersistenceBrokerTemplate().delete(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.dao.OriginationCodeDao#findByCode(java.lang.String)
     */
    public OriginationCode findByCode(String originationCode) {
        // TODO Auto-generated method stub
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FS_ORIGIN_CD", originationCode);

        QueryByCriteria query = QueryFactory.newQuery(OriginationCode.class, criteria);
        return (OriginationCode) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.dao.OriginationCodeDao#save(org.kuali.core.bo.OriginationCode)
     */
    public void save(OriginationCode code) {
        getPersistenceBrokerTemplate().store(code);
    }


}
