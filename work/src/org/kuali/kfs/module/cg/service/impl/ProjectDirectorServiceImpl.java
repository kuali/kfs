/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.cg.service.impl;

import org.kuali.module.cg.service.ProjectDirectorService;
import org.kuali.module.cg.bo.ProjectDirector;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.Cached;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of the ProjectDirector service.
 */
@Transactional
public class ProjectDirectorServiceImpl implements ProjectDirectorService {

    private UniversalUserService universalUserService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.cg.service.ProjectDirectorService#getByPersonUserIdentifier(String)
     */
    @Cached
    public ProjectDirector getByPersonUserIdentifier(String username) {
        try {
            UniversalUser user = universalUserService.getUniversalUserByAuthenticationUserId( username );
            return getByPrimaryId(user.getPersonUniversalIdentifier());
        } catch ( UserNotFoundException ex ) {
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
    @Cached
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
