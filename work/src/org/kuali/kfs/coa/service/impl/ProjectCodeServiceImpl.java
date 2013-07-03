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
package org.kuali.kfs.coa.service.impl;


import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.dataaccess.ProjectCodeDao;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This class is the service implementation for the ProjectCode structure. This is the default implementation, that is delivered
 * with Kuali.
 */

@NonTransactional
public class ProjectCodeServiceImpl implements ProjectCodeService {
    
    private ProjectCodeDao projectCodeDao;
    
    /**
     *
     * @see org.kuali.kfs.coa.service.ProjectCodeService#getByPrimaryId(java.lang.String)
     */
    @Override
    //   KFSMI-2612
    @Cacheable(value=ProjectCode.CACHE_NAME, key="'projectCode='+#p0")
    public ProjectCode getByPrimaryId(String projectCode) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(ProjectCode.class, projectCode);
    }
    
    /**
     * @param projectDao The projectDao to set.
     */
    public void setProjectCodeDao(ProjectCodeDao projectCodeDao) {
        this.projectCodeDao = projectCodeDao;
    }

}
