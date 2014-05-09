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
 * group.
 */
public class LetterOfCreditByLOCFundGroupBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditByLOCFundGroupBatchStep.class);

    protected LetterOfCreditCreateService letterOfCreditCreateService;
    protected String batchFileDirectoryName;

    /**
     * This step of LetterOFCreditJob would create cash control documents and payment application document for CG Invoices per LOC
     * fund group.
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        getLetterOfCreditCreateService().processLettersOfCreditByFundGroup(batchFileDirectoryName);

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
     * This method is a setter.  For the batchFileDirectoryName.  That's what it is.
     *
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
