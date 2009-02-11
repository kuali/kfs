/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.batch;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Parses data from a government web page listing the valid CFDA codes. The codes are then compared with what's in the CFDA table in
 * Kuali. Codes set to be managed automatically are reconciled with what's on the web page. Codes managed manually are left alone.
 * Finally an email containing a summary of what was done by the step execution is sent to the member of the CG_CFDA_BATCH_NOTIFY workgroup.
 */
public class CfdaBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CfdaBatchStep.class);

    private CfdaService cfdaService;
    private MailService mailService;
    private ParameterService parameterService;
    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        MailMessage message = new MailMessage();

        try {
            CfdaUpdateResults results = cfdaService.update();

            String listserv = parameterService.getParameterValue(CfdaBatchStep.class, CGConstants.RESULT_SUMMARY_TO_EMAIL_ADDRESSES);
            if (listserv == null) {
                LOG.fatal("Couldn't find address to send notification to.");
                return true;
            }
            
            
            System.out.println(listserv);
            message.addToAddress(listserv);

            // TODO this message should come from some config file.
            StringBuilder builder = new StringBuilder();
            builder.append("The CFDA batch script is complete.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
            builder.append(" records were deactivated because they are no longer on the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsInKfsDatabase());
            builder.append(" records were in the KFS database.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsNewlyAddedFromWebSite());
            builder.append(" records were newly added from the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsNotUpdatedBecauseManual());
            builder.append(" records were not updated because they are manual.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsReActivated());
            builder.append(" records were re-activated.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsRetrievedFromWebSite());
            builder.append(" records were retrieved from the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsUpdatedBecauseAutomatic());
            builder.append(" records were updated because they are automatic.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecrodsNotUpdatedForHistoricalPurposes());
            builder.append(" records were not updated for historical reasons.\n");
            builder.append(" - Message\n");
            builder.append(null != results.getMessage() ? results.getMessage() : "");

            message.setMessage(builder.toString());
            mailService.sendMessage(message);

        }
        catch (IOException ioe) {
            LOG.warn("Exception while updating CFDA codes.", ioe);
            return false;
        }
        catch (InvalidAddressException iae) {

           LOG.warn("The email address for "+CfdaBatchStep.class+":"+CGConstants.RESULT_SUMMARY_TO_EMAIL_ADDRESSES+" is invalid.", iae);
            return true;
        }
        return true;
    }

    /**
     * Sets the {@link CfdaService}. For use by Spring.
     * 
     * @param cfdaService The service to be assigned.
     */
    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }

    /**
     * Set the {@link MailService}. For use by Spring.
     * 
     * @param mailService The service to be assigned.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
    
    /**
     * Sets the {@link ParameterService}. For use by Spring.
     * 
     * @param parameterService The service to be assigned.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}

