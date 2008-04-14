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
package org.kuali.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.cg.bo.ProjectDirector;
import org.kuali.module.cg.service.ProjectDirectorService;

/**
 * Implementation of the ProjectDirector service.
 */
public class ProjectDirectorServiceImpl implements ProjectDirectorService {

    private UniversalUserService universalUserService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.cg.service.ProjectDirectorService#getByPersonUserIdentifier(String)
     */
    @Cached
    public ProjectDirector getByPersonUserIdentifier(String username) {
        try {
            UniversalUser user = universalUserService.getUniversalUserByAuthenticationUserId(username);
            return getByPrimaryId(user.getPersonUniversalIdentifier());
        }
        catch (UserNotFoundException ex) {
            return null;
        }
    }

    /**
     * @see org.kuali.module.cg.service.ProjectDirectorService#getByPrimaryId(String)
     */
    @Cached
    public ProjectDirector getByPrimaryId(String universalIdentifier) {
        return (ProjectDirector) businessObjectService.findByPrimaryKey(ProjectDirector.class, mapPrimaryKeys(universalIdentifier));
    }

    /**
     * @see org.kuali.module.cg.service.ProjectDirectorService#primaryIdExists(String)
     */
    @CacheNoCopy
    public boolean primaryIdExists(String universalIdentifier) {
        return businessObjectService.countMatching(ProjectDirector.class, mapPrimaryKeys(universalIdentifier)) > 0;
    }

    private Map<String, Object> mapPrimaryKeys(String universalIdentifier) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, universalIdentifier == null ? null : universalIdentifier.trim());
        return primaryKeys;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
