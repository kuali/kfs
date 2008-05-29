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
package org.kuali.kfs.service.impl;

import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.bo.HomeOrigination;
import org.kuali.kfs.dao.HomeOriginationDao;
import org.kuali.kfs.service.HomeOriginationService;
import org.springframework.transaction.annotation.Transactional;

@NonTransactional
public class HomeOriginationServiceImpl implements HomeOriginationService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HomeOriginationServiceImpl.class);

    private HomeOriginationDao homeOriginationDao;

    /**
     * Retrieves a HomeOrigination object. Currently, there is only a single, unique HomeOriginationCode record in the database.
     */
    @CacheNoCopy
    public HomeOrigination getHomeOrigination() {
        return getHomeOriginationDao().getHomeOrigination();
    }

    /**
     * @return Returns the homeOriginationDao.
     */
    public HomeOriginationDao getHomeOriginationDao() {
        return homeOriginationDao;
    }

    /**
     * @param homeOriginationDao The homeOriginationDao to set.
     */
    public void setHomeOriginationDao(HomeOriginationDao homeOriginationDao) {
        this.homeOriginationDao = homeOriginationDao;
    }

}
