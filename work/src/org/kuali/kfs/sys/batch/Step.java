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
package org.kuali.kfs.batch;

public interface Step {
    /**
     * Perform this step of a batch job.
     * 
     * @return true if successful and continue the job, false if successful and stop the job
     * @throws Throwable if unsuccessful
     */
    public boolean execute() throws InterruptedException;

    /**
     * Return id of this step spring bean.
     * 
     * @return The name of this step.
     */
    public String getName();
    
    /**
     * Call to attempt to interrupt a step in the middle of processing.  Note that this only has an 
     * effect if the step in question checks its interrupted status.
     * 
     */
    public void interrupt();
    
    public boolean isInterrupted();
    
    public void setInterrupted(boolean interrupted);
}
