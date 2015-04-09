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
package org.kuali.kfs.sys.dataaccess.impl;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.dataaccess.OriginationCodeDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class OriginationCodeDaoOjb extends PlatformAwareDaoBaseOjb implements OriginationCodeDao {
    private static Logger LOG = Logger.getLogger(OriginationCodeDaoOjb.class);

    public OriginationCodeDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.krad.dao.OriginationCodeDao#delete(org.kuali.rice.krad.bo.OriginationCode)
     */
    public void delete(OriginationCode code) {
        getPersistenceBrokerTemplate().delete(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.krad.dao.OriginationCodeDao#findByCode(java.lang.String)
     */
    public OriginationCode findByCode(String originationCode) {
        // TODO Auto-generated method stub
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FS_ORIGIN_CD", originationCode);

        QueryByCriteria query = QueryFactory.newQuery(OriginationCode.class, criteria);
        return (OriginationCode) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

}
