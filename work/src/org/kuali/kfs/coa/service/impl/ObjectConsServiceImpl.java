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
package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.dataaccess.ObjectConsDao;
import org.kuali.kfs.coa.service.ObjectConsService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectConsServiceImpl implements ObjectConsService {
    private ObjectConsDao objectConsDao;

    /**
     * @see org.kuali.kfs.coa.service.ObjectConsService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjectConsolidation getByPrimaryId(String chartOfAccountsCode, String objectConsCode) {
        return objectConsDao.getByPrimaryId(chartOfAccountsCode, objectConsCode);
    }

    /**
     * @return Returns the objectLevelDao.
     */
    public ObjectConsDao getObjectConsDao() {
        return objectConsDao;
    }

    /**
     * @param objectLevelDao The objectLevelDao to set.
     */
    public void setObjectConsDao(ObjectConsDao objectConsDao) {
        this.objectConsDao = objectConsDao;
    }
}
