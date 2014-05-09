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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;

import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step of LetterOFCreditJob would create cash control documents and payment application document for CG Invoices per LOC fund
 */
public class LetterOfCreditByLOCFundBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditByLOCFundBatchStep.class);

    protected LetterOfCreditCreateService letterOfCreditCreateService;
    protected String batchFileDirectoryName;

    /**
     * This step of LetterOFCreditJob would create cash control documents and payment application document for CG Invoices per LOC
     * fund.
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        getLetterOfCreditCreateService().processLettersOfCreditByFund(batchFileDirectoryName);
        return true;
    }

    /**
     * Gets the letterOfCreditCreateService attribute.
     *
     * @return Returns the letterOfCreditCreateService.
     */
    public LetterOfCreditCreateService getLetterOfCreditCreateService() {
        return letterOfCreditCreateService;
    }

    /**
     * Sets the letterOfCreditCreateService attribute value.
     *
     * @param letterOfCreditCreateService The letterOfCreditCreateService to set.
     */
    public void setLetterOfCreditCreateService(LetterOfCreditCreateService letterOfCreditCreateService) {
        this.letterOfCreditCreateService = letterOfCreditCreateService;
    }

    /**
     * This method...
     * Indeed.  This method.  Remember all the times this method has been used?  Every time this step is run?  It's amazing, really.
     * Just a simple setter, making sure we can inject the correct batch file directory name.  Without out, though, there would be no
     * place to write out the results file for the step and none of us would know what had gone on.  So really, there's no better way
     * we could say it better.  This method...
     *
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
