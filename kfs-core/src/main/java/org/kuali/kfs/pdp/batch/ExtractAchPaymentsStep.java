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
package org.kuali.kfs.pdp.batch;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.pdp.batch.service.ExtractPaymentService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class ExtractAchPaymentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractAchPaymentsStep.class);

    private ExtractPaymentService extractPaymentService;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#prepareStepDirectory()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return extractPaymentService.getRequiredDirectoryNames();
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");

        extractPaymentService.extractAchPayments();

        return true;
    }

    public void setExtractPaymentService(ExtractPaymentService eps) {
        extractPaymentService = eps;
    }
}
