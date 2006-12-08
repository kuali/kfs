/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/impl/ProjectCodeServiceImpl.java,v $
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
package org.kuali.module.chart.service.impl;


import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.dao.ProjectCodeDao;
import org.kuali.module.chart.service.ProjectCodeService;

/**
 * This class is the service implementation for the ProjectCode structure. This is the default implementation, that is delivered
 * with Kuali.
 * 
 * 
 */
public class ProjectCodeServiceImpl implements ProjectCodeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeServiceImpl.class);

    private ProjectCodeDao projectCodeDao;
    
    /**
     * Retrieves an ProjectCode object based on primary key.
     * 
     * @param projectCode - Project Code
     * @return ProjectCode
     */
    public ProjectCode getByPrimaryId(String projectCode) {
        return projectCodeDao.getByPrimaryId(projectCode);
    }

    /**
     * Retrieves an ProjectCode object based on primary key
     * 
     * @param projectCode - Project Code
     * @return ProjectCode
     */
    public ProjectCode getByName(String name) {
        return projectCodeDao.getByName(name);
    }

    /**
     * @see org.kuali.module.chart.service.ProjectCodeService#save(org.kuali.bo.ProjectCode)
     */
    public void save(ProjectCode projectCode) {
        projectCodeDao.save(projectCode);
    }

    /**
     * @param projectDao The projectDao to set.
     */
    public void setProjectCodeDao(ProjectCodeDao projectCodeDao) {
        this.projectCodeDao = projectCodeDao;
    }

}