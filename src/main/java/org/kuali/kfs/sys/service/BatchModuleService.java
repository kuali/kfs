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
package org.kuali.kfs.sys.service;


/**
 * This class defines methods needed for batch to run successfully when there are dependencies on jobs that are run on external systems.
 * Implementations of this interface are able to keep track of external job status and report it to the scheduler service.
 * 
 */
public interface BatchModuleService {

    /**
     * This method returns whether a job is run on an external system. 
     *  
     * @param jobName a job name, such that calling {@link #isResponsibleForJob(String)} with this job name would return true
     * @return whether this job runs from an external system
     */
    public boolean isExternalJob(String jobName);
    
    /**
     * This method returns the status of the given external job.
     * 
     * @param jobName a job name, such that calling {@link #isResponsibleForJob(String)} with this job name would return true
     * @return one of the status code constants defined in {@link org.kuali.kfs.sys.batch.service.SchedulerService}
     */
    public String getExternalJobStatus(String jobName);
    
}
