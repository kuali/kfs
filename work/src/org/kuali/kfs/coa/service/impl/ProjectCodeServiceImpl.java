/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
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
package org.kuali.module.chart.service.impl;

import org.kuali.core.service.KualiUserService;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.dao.ProjectCodeDao;
import org.kuali.module.chart.service.ProjectCodeService;

/**
 * This class is the service implementation for the ProjectCode structure. This is the default implementation, that is delivered
 * with Kuali.
 * 
 * @author Kuali Nervous System Team ()
 */
public class ProjectCodeServiceImpl implements ProjectCodeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeServiceImpl.class);

    private ProjectCodeDao projectCodeDao;
    private KualiUserService kualiUserService;
    
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

    public void setKualiUserService(KualiUserService kualiUserService) {
        this.kualiUserService = kualiUserService;
    }  

}