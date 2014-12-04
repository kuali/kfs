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
