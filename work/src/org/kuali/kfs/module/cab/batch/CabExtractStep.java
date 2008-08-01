/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.Parameter;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.service.CabBatchExtractService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.ParameterConstants;

public class CabExtractStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(CabExtractStep.class);
    private CabBatchExtractService cabBatchExtractService;
    private DateTimeService dateTimeService;

    /**
     * CAB Extract Steps
     * <li>Find all GL transactions created after last extract time matching CAB filter parameters</li>
     * <li>For each GL line perform the following steps</li>
     * <li>Check GL lines belongs to a PO or Not</li>
     * <li>If not PO related, then check for duplicate in CB_GL_ENTRY_T, if not duplicate insert into it</li>
     * <li>If PO related, check against PURAP Account Line History for respective PREQ or CM document</li>
     * <li>If sum of amounts matches grouped by fields "univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd,
     * fin_sub_obj_cd, univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr" then it is a valid one, else ignore the GL lines for the document
     * and write to the reconciliation report </li>
     * <li>If CB_PUR_DOC entry doesn't exist, insert new record else update active indicator</li>
     * <li>Insert into CB_PUR_ITM_AST_T when record not exists or inactive, if active exact match found then skip</li>
     * <li>Insert one/multiple entries into CB_PUR_LN_AST_ACCT_T, amount should match exact from account line history</li>
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        Timestamp startTs = dateTimeService.getCurrentTimestamp();
        LOG.info("CAB batch started at " + startTs);
        Collection<Entry> elgibleGLEntries = cabBatchExtractService.findElgibleGLEntries();
        if (elgibleGLEntries == null || elgibleGLEntries.size() == 0) {
            LOG.info("No GL entries found for extract.");
            return true;
        }
        List<Entry> fpLines = new ArrayList<Entry>();
        List<Entry> purapLines = new ArrayList<Entry>();
        // separate PO and non-PO lines
        cabBatchExtractService.separatePOLines(fpLines, purapLines, elgibleGLEntries);
        // process non-PO lines
        cabBatchExtractService.saveFPLines(fpLines);

        // TODO - Waiting for PURAP Account lines history

        // Update the last extract time stamp
        cabBatchExtractService.updateLastExtractTime(startTs);
        LOG.info("CAB batch finished at " + dateTimeService.getCurrentTimestamp());
        return true;
    }


    public CabBatchExtractService getCabBatchExtractService() {
        return cabBatchExtractService;
    }

    public void setCabBatchExtractService(CabBatchExtractService cabBatchExtractService) {
        this.cabBatchExtractService = cabBatchExtractService;
    }


    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


}
