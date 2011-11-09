/*
 * Copyright 2008 The Kuali Foundation
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
