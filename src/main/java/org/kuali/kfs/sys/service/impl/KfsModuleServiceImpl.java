/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.batch.service.impl.SchedulerServiceImpl;
import org.kuali.kfs.sys.service.BatchModuleService;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;

/**
 * 
 * This class is the KFS implementation of a module service. It also implements the batch related methods
 * 
 */
public class KfsModuleServiceImpl extends ModuleServiceBase implements BatchModuleService {

    /***
     * @see org.kuali.kfs.sys.service.BatchModuleService#hasJobStatus(java.lang.String)
     */
    public boolean isExternalJob(String jobName){
        return false;
    }

    /***
     * @see org.kuali.kfs.sys.service.BatchModuleService#getJobStatus(java.lang.String)
     */
    public String getExternalJobStatus(String jobName) {
        if(isExternalJob(jobName))
            return SchedulerServiceImpl.SUCCEEDED_JOB_STATUS_CODE;
        return null;
    }

}
