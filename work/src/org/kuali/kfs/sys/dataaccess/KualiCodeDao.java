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
package org.kuali.kfs.dao;

import java.util.Collection;

import org.kuali.core.bo.KualiCode;
import org.springframework.dao.DataAccessException;

/**
 * This interface defines the Accounting Code DAO...
 * 
 * 
 */

public interface KualiCodeDao {

    /**
     * 
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param code - the short/abbreviated code of the code/name pair to search on
     * @return KualiCodeBase - the populated KualiCodeBase object
     */
    public KualiCode getByCode(Class queryClass, String code);

    public KualiCode getSystemCode(Class clazz, String code);

    /**
     * 
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param name - the long name code of the code/name pair to search on
     * @return KualiCodeBase - the populated KualiCodeBase object
     */
    public KualiCode getByName(Class queryClass, String name);

    /**
     * 
     * @param kualiCode - a populated KualiCodeBase object to be saved
     */
    public void save(KualiCode kualiCode) throws DataAccessException;

    /**
     * Deletes the object-record passed in.
     * 
     * @param kualiCode
     */
    public void delete(KualiCode kualiCode);

    /**
     * This method retrieves all active objects that extend KualiCode, by class name.
     * 
     * @param className The class name of the object type to retrieve.
     * @return
     */
    public Collection getAllActive(Class queryClass);

    /**
     * This method retrieves all objects that extend KualiCode, by class name.
     * 
     * @param className The class name of the object type to retrieve.
     * @return
     */
    public Collection getAll(Class queryClass);

}
