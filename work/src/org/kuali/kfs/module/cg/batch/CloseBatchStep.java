/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.cg.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CloseService;

/**
 * This class...
 * 
 * @author Laran Evans <lc278@cornell.edu>
 * @since Apr 6, 2007 12:58:58 PM
 */
public class CloseBatchStep extends AbstractStep {

    private CloseService closeService;

    /**
     * 
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName) {
        closeService.close();
        return true;
    }

    /**
     * 
     * This method is a simple setter used to assign a value to local attribute.
     * @param closeService The value to be used to assign to the local attribute <code>closeService</code>.
     */
    public void setCloseService(org.kuali.module.cg.service.CloseService closeService) {
        this.closeService = closeService;
    }

}
