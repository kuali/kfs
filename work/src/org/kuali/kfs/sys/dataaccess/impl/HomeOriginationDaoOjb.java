/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.bo.HomeOrigination;
import org.kuali.kfs.dao.HomeOriginationDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the HomeOriginationDao interface.
 * 
 * 
 */
public class HomeOriginationDaoOjb extends PersistenceBrokerDaoSupport implements HomeOriginationDao {

    private static Logger LOG = Logger.getLogger(HomeOriginationDaoOjb.class);


    /**
     * @see org.kuali.core.dao.HomeOriginationDao#getHomeOrigination()
     */
    public HomeOrigination getHomeOrigination() {
        HomeOrigination homeOrigination = null;

        homeOrigination = (HomeOrigination) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(HomeOrigination.class, new Criteria()));

        return homeOrigination;
    }
}
