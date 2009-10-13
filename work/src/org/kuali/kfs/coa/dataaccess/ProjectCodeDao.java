/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;

import org.kuali.kfs.coa.businessobject.ProjectCode;


/**
 * This interface defines basic methods that ProjectCode Dao's must provide
 */
public interface ProjectCodeDao {

    /**
     * Retrieves a ProjectCode object by primary key.
     * 
     * @param projectCode - primary key
     * @return {@link ProjectCode} by primary key
     */
    public ProjectCode getByPrimaryId(String projectCode);

    /**
     * Retrieves a ProjectCode object by primary name.
     * 
     * @param name
     * @return {@link ProjectCode} by name
     */
    public ProjectCode getByName(String name);

    /**
     * This method saves a given {@link ProjectCode}
     * 
     * @param projectCode - a populated ProjectCode object to be saved
     */
    public void save(ProjectCode projectCode);
}
