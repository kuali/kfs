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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.dataaccess.ProjectCodeDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;


/**
 * This class is the OJB implementation of the ProjectCodeDao interface.
 */
public class ProjectCodeDaoOjb extends PlatformAwareDaoBaseOjb implements ProjectCodeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeDaoOjb.class);

    /**
     * Retrieves project code business object by primary key
     * 
     * @param projectCode - part of composite key
     * @return Project
     * @see ProjectCodeDao#getByPrimaryId(String)
     */
    public ProjectCode getByPrimaryId(String projectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("code", projectCode);

        return (ProjectCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(ProjectCode.class, criteria));
    }

    /**
     * Retrieves project code business object by project name
     * 
     * @param name - part of composite key
     * @return Project
     * @see ProjectCodeDao#getByName(String)
     */
    public ProjectCode getByName(String name) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", name);

        return (ProjectCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(ProjectCode.class, criteria));
    }

    /**
     * @param projectCode - a populated ProjectCode object to be saved
     * @throws IllegalObjectStateException
     * @throws ValidationErrorList
     * @see ProjectCodeDaoOjb#save(ProjectCode)
     */
    public void save(ProjectCode projectCode) {
        getPersistenceBrokerTemplate().store(projectCode);
    }

}
