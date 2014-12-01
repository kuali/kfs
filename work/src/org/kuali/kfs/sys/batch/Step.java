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
package org.kuali.kfs.sys.batch;

import java.util.Date;

public interface Step {
    /**
     * Perform this step of a batch job.
     * 
     * @param jobName the name of the job running the step
     * @param jobRunDate the time/date the job is executed
     * @return true if successful and continue the job, false if successful and stop the job
     * @throws Throwable if unsuccessful
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException;

    /**
     * Return id of this step spring bean.
     * 
     * @return The name of this step.
     */
    public String getName();

    /**
     * Call to attempt to interrupt a step in the middle of processing. Note that this only has an effect if the step in question
     * checks its interrupted status.
     */
    public void interrupt();

    public boolean isInterrupted();

    public void setInterrupted(boolean interrupted);
}
