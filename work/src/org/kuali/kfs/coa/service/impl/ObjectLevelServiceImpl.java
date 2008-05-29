/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service.impl;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.dao.ObjectLevelDao;
import org.kuali.module.chart.service.ObjectLevelService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service implementation is the default implementation of the ObjLevel service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectLevelServiceImpl implements ObjectLevelService {
    private ObjectLevelDao objectLevelDao;

    /**
     * @see org.kuali.module.chart.service.ObjectLevelService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjLevel getByPrimaryId(String chartOfAccountsCode, String objectLevelCode) {
        return objectLevelDao.getByPrimaryId(chartOfAccountsCode, objectLevelCode);
    }

    /**
     * @return Returns the objectLevelDao.
     */
    public ObjectLevelDao getObjectLevelDao() {
        return objectLevelDao;
    }

    /**
     * @param objectLevelDao The objectLevelDao to set.
     */
    public void setObjectLevelDao(ObjectLevelDao objectLevelDao) {
        this.objectLevelDao = objectLevelDao;
    }
}