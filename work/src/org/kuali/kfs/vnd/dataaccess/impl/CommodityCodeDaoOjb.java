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
package org.kuali.kfs.vnd.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.dataaccess.CommodityCodeDao;

/**
 * OJB implementation of CommodityCodeDao.
 */
public class CommodityCodeDaoOjb extends PlatformAwareDaoBaseOjb implements CommodityCodeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CommodityCodeDaoOjb.class);

    /**
     * @see org.kuali.kfs.vnd.dataaccess.CommodityCodeDao#wildCardCommodityCodeExists(java.lang.String)
     */
    public boolean wildCardCommodityCodeExists(String wildCardCommodityCode) {
        String commodityCodeString = StringUtils.replace(wildCardCommodityCode, KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN);
        Criteria criteria = new Criteria();
        criteria.addLike(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, commodityCodeString);        
        int count =  getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(CommodityCode.class, criteria));
        boolean exists = ((count > 0) ? true : false);
        return exists;
    }
}
