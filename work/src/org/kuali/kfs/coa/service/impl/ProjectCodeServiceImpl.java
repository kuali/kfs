/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;


import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.dataaccess.ProjectCodeDao;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.util.spring.CacheNoCopy;

/**
 * This class is the service implementation for the ProjectCode structure. This is the default implementation, that is delivered
 * with Kuali.
 */

@NonTransactional
public class ProjectCodeServiceImpl implements ProjectCodeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeServiceImpl.class);

    private ProjectCodeDao projectCodeDao;

    /**
     * 
     * @see org.kuali.kfs.coa.service.ProjectCodeService#getByPrimaryId(java.lang.String)
     */
    //   KFSMI-2612
    @CacheNoCopy
    public ProjectCode getByPrimaryId(String projectCode) {
        return projectCodeDao.getByPrimaryId(projectCode);
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.ProjectCodeService#getByName(java.lang.String)
     */
    public ProjectCode getByName(String name) {
        return projectCodeDao.getByName(name);
    }

    /**
     * @param projectDao The projectDao to set.
     */
    public void setProjectCodeDao(ProjectCodeDao projectCodeDao) {
        this.projectCodeDao = projectCodeDao;
    }

}
