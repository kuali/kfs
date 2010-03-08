/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.AutoDisapproveEDocsService;

/**
 * Runs the batch job that gathers all EDocs that are in ENROUTE status and cancels them.
 */
public class AutoDisapproveEDocsStep extends AbstractStep {
    private AutoDisapproveEDocsService autoDisapproveEDocsService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveEDocsStep.class);
    
    /**
     * This step will auto disapprove the EDocs that are in ENROUTE status.
     * 
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        
        if (canAutoDisapproveJobRun()) {
            autoDisapproveEDocsService.autoDisapproveEDocsInEnrouteStatus();
            return true;            
        }
        else {
            return false;
        }
    }

    /**
     * This method will compare today's date to the system parameter for year end auto disapproval run date
     * @return true if today's date equals to the system parameter run date
     */
    protected boolean canAutoDisapproveJobRun() {
      boolean autoDisapproveCanRun = true;
      
      // IF trunc(SYSDATE - 14/24) = v_yec_cncl_doc_run_dt THEN
      String yearEndAutoDisapproveRunDate = getParameterService().getParameterValue(AutoDisapproveEDocsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_EDOCS_STEP_RUN_DATE);
      String today = getDateTimeService().toDateString(getDateTimeService().getCurrentDate());
      
      if (!yearEndAutoDisapproveRunDate.equals(today)) {
          LOG.warn("Automatic disapproval bypassed - date test failed. Sysem Parameter Date: " + yearEndAutoDisapproveRunDate + " does not equal to Current Date: " + today);
          autoDisapproveCanRun = false;
      }
      
      return autoDisapproveCanRun;
    }
        
    /**
     * Sets the autoDisapproveEDocsService attribute value.
     * 
     * @param autoDisapproveEDocsService The autoDisapproveEDocsService to set.
     * @see org.kuali.kfs.sys.service.AutoDisapproveEDocsService
     */
    public void setAutoDisapproveEDocsService(AutoDisapproveEDocsService autoDisapproveEDocsService) {
        this.autoDisapproveEDocsService = autoDisapproveEDocsService;
    }
}
