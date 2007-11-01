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
package org.kuali.module.cg.service;

import org.kuali.module.cg.bo.ProjectDirector;

/**
 * Services for ProjectDirectors
 */
public interface ProjectDirectorService {

    /**
     * Finds a ProjectDirector by username. That's a secondary key to UniversalUser, used to get the ProjectDirector's primary key,
     * the universal user ID number.
     * 
     * @param username the person user identifier of the ProjectDirector to get
     * @return the corresponding ProjectDirector, or null if none
     */
    public ProjectDirector getByPersonUserIdentifier(String username);

    /**
     * Finds a ProjectDirector by universal user ID number. That's the primary key to ProjectDirector, and coincidentally to
     * UniversalUser too.
     * 
     * @param universalIdentifier the universal user ID number of the ProjectDirector to get
     * @return the corresponding ProjectDirector, or null if none
     */
    public ProjectDirector getByPrimaryId(String universalIdentifier);

    /**
     * Checks for the existence of a ProjectDirector by universal user ID number. That's the primary key to ProjectDirector, and
     * coincidentally to UniversalUser too.
     * 
     * @param universalIdentifier the universal user ID number of the ProjectDirector to get
     * @return whether the corresponding ProjectDirector exists
     */
    public boolean primaryIdExists(String universalIdentifier);
}
