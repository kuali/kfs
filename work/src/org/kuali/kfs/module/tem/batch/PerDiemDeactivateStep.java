/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch;

import java.util.Date;

import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kns.util.spring.Logged;

public class PerDiemDeactivateStep extends AbstractStep {
    
    private PerDiemService perDiemService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    @Logged
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        this.getPerDiemService().deactivateAndSavePerDiem();
        
        return true;
    }

    /**
     * Gets the perDiemService attribute. 
     * @return Returns the perDiemService.
     */
    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    /**
     * Sets the perDiemService attribute value.
     * @param perDiemService The perDiemService to set.
     */
    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }
}
