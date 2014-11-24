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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.springframework.core.io.UrlResource;

/**
 * This step will call the <code>PaymentService</code> to pick up incoming PDP payment files and process.
 */
public class LoadPaymentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadPaymentsStep.class);

    private PaymentFileService paymentFileService;
    private BatchInputFileType paymentInputFileType;

    /**
     * Picks up the required path from the batchInputFIleType as well as from the payment
     * file service
     *
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        List<String> requiredDirectoryList = new ArrayList<String>();
        requiredDirectoryList.add(paymentInputFileType.getDirectoryPath());
        requiredDirectoryList.addAll(paymentFileService.getRequiredDirectoryNames());

        return requiredDirectoryList;
    }

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");
        //check if payment.xsd exists. If not terminate at this point.
        if(paymentInputFileType instanceof XmlBatchInputFileTypeBase) {
            try {
                UrlResource schemaResource = new UrlResource(((XmlBatchInputFileTypeBase)paymentInputFileType).getSchemaLocation());
                if(!schemaResource.exists()) {
                    LOG.error(schemaResource.getFilename() + " file does not exist");
                    throw new RuntimeException("error getting schema stream from url: " + schemaResource.getFilename() + " file does not exist ");
                }
            }
            catch (MalformedURLException ex) {
                LOG.error("error getting schema url: " + ex.getMessage());
                throw new RuntimeException("error getting schema url:  " + ex.getMessage(), ex);
            }
        }

        paymentFileService.processPaymentFiles(paymentInputFileType);

        return true;
    }

    /**
     * Sets the paymentFileService attribute value.
     *
     * @param paymentFileService The paymentFileService to set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    /**
     * Sets the paymentInputFileType attribute value.
     *
     * @param paymentInputFileType The paymentInputFileType to set.
     */
    public void setPaymentInputFileType(BatchInputFileType paymentInputFileType) {
        this.paymentInputFileType = paymentInputFileType;
    }

}
