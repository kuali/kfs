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
package org.kuali.kfs.service;

import java.util.Collection;

import org.kuali.core.bo.KualiCode;

/**
 * This interface defines methods that an KualiCodeBase Service must provide
 * 
 * 
 */
public interface KualiCodeService {

    /**
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param code - code to search for
     * @return KualiCodeBase
     * 
     * Retrieves an KualiCodeBase object by a given code.
     */
    public KualiCode getByCode(Class queryClass, String code);

    public KualiCode getSystemCode(Class clazz, String code);

    /**
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param name - name to search for
     * @return KualiCodeBase
     * 
     * Retrieves an KualiCodeBase object by a given exact name.
     */
    public KualiCode getByName(Class queryClass, String name);

    /**
     * @param kualiCode - KualiCodeBase (or subclass) to be saved
     * 
     * Pass the method a populated KualiCodeBase object, and it will be saved.
     */
    public void save(KualiCode kualiCode);

    /**
     * This method retrieves all objects that extend KualiCodes by a class name.
     * 
     * @param className
     * @return
     */
    public Collection getAll(Class queryClass);
}
