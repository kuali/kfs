/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch;

import java.util.Date;

import org.kuali.kfs.module.endow.batch.service.EndowmenteDocPostingService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class EndowmenteDocPostingStep extends AbstractStep {

    private EndowmenteDocPostingService endowmenteDocPostingService;
    
    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException 
    {
        return endowmenteDocPostingService.processDocumentPosting();
    }

    /**
     * Sets the endowmenteDocPostingService attribute value.
     * @param endowmenteDocPostingService The endowmenteDocPostingService to set.
     */
    public void setEndowmenteDocPostingService(EndowmenteDocPostingService endowmenteDocPostingService) 
    {
        this.endowmenteDocPostingService = endowmenteDocPostingService;
    }

}
