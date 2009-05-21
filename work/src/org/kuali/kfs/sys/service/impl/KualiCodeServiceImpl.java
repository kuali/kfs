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
package org.kuali.kfs.sys.service.impl;

import java.util.Collection;

import org.kuali.kfs.sys.dataaccess.KualiCodeDao;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.util.spring.CacheNoCopy;

/**
 * This class is the service implementation for the KualiCodeBase structure. This is the default implementation, that is delivered
 * with Kuali.
 */

@NonTransactional
public class KualiCodeServiceImpl implements KualiCodeService {
    private KualiCodeDao kualiCodeDao;

    /**
     * Retrieves an KualiCodeBase object by a given code.
     * 
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param code - code to search for
     * @return KualiCodeBase
     */
    //   KFSMI-2612 
    @CacheNoCopy
    public KualiCode getByCode(Class queryClass, String code) {
        return kualiCodeDao.getByCode(queryClass, code);
    }

    public KualiCode getSystemCode(Class clazz, String code) {
        return kualiCodeDao.getSystemCode(clazz, code);
    }

    /**
     * Retrieves an KualiCodeBase object by a given exact name.
     * 
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param name - name to search for
     * @return KualiCodeBase
     */
    public KualiCode getByName(Class queryClass, String name) {
        return kualiCodeDao.getByName(queryClass, name);
    }

    /**
     * Pass the method a populated KualiCodeBase object, and it will be saved. - for testing only
     * 
     * @param kualiCode
     */
    @Deprecated
    public void save(KualiCode kualiCode) {
        // if no validation failures, commit the save
        kualiCodeDao.save(kualiCode);
    }

    /**
     * @see org.kuali.rice.kns.service.KualiCodeService#getAll(java.lang.Class)
     */
    public Collection getAll(Class queryClass) {
        return kualiCodeDao.getAll(queryClass);
    }

    /**
     * Pass the method a populated KualiCodeBase object, and it will be deleted.
     * 
     * @param kualiCode
     */
    public void delete(KualiCode kualiCode) {
        kualiCodeDao.delete(kualiCode);
    }

    /**
     * @return KualiCodeBase - Getter for the KualiCodeBase object.
     */
    public KualiCodeDao getKualiCodeDao() {
        return kualiCodeDao;
    }

    /**
     * @param kualiCodeDao - Setter for the KualiCodeBase object.
     */
    public void setKualiCodeDao(KualiCodeDao kualiCodeDao) {
        this.kualiCodeDao = kualiCodeDao;
    }
}
