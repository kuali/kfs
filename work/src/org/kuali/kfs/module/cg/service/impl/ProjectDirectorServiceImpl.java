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
package org.kuali.kfs.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cg.businessobject.ProjectDirector;
import org.kuali.kfs.module.cg.service.ProjectDirectorService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.spring.CacheNoCopy;
import org.kuali.rice.kns.util.spring.Cached;

/**
 * Implementation of the ProjectDirector service.
 */
public class ProjectDirectorServiceImpl implements ProjectDirectorService {

    private org.kuali.rice.kim.service.PersonService personService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.ProjectDirectorService#getByPersonUserIdentifier(String)
     */
    @Cached
    public ProjectDirector getByPersonUserIdentifier(String username) {
        Person user = personService.getPersonByPrincipalName(username);
        if (user != null) {
            return getByPrimaryId(user.getPrincipalId());
        } else {
            return null;
        }
    }

    /**
     * @see org.kuali.kfs.module.cg.service.ProjectDirectorService#getByPrimaryId(String)
     */
    @Cached
    public ProjectDirector getByPrimaryId(String universalIdentifier) {
        return (ProjectDirector) businessObjectService.findByPrimaryKey(ProjectDirector.class, mapPrimaryKeys(universalIdentifier));
    }

    /**
     * @see org.kuali.kfs.module.cg.service.ProjectDirectorService#primaryIdExists(String)
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

    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

