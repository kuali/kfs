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
package org.kuali.module.cg.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.service.CfdaUpdateResults;
import org.kuali.core.service.MailService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * This class...
 * 
 * @author Laran Evans <lc278@cornell.edu>
 * @since May 8, 2007 5:42:16 PM
 */
public class CfdaBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CfdaBatchStep.class);
    private static String MAIL_RECIPIENTS_GROUP_NAME = "KUALI_CGCFDA";

    private CfdaService cfdaService;
    private MailService mailService;
    private KualiGroupService kualiGroupService;
    private UniversalUserService universalUserService;
    
    /**
     * This method implements an interface method and performs specific cfda related methods to carry out a batch step action.  
     * 
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName) throws InterruptedException {
        MailMessage message = new MailMessage();

        try {
            CfdaUpdateResults results = cfdaService.update();

            KualiGroup workgroup = kualiGroupService.getByGroupName(MAIL_RECIPIENTS_GROUP_NAME);
            List<String> memberNetworkIds = workgroup.getGroupUsers();
            for(String id : memberNetworkIds) {
                try {
                    UniversalUser user = universalUserService.getUniversalUser(id.toUpperCase());
                    message.addToAddress(user.getPersonEmailAddress());
                    // TODO Must remember to take this out.
                    message.addToAddress("lc278@cornell.edu");
                } catch(UserNotFoundException unfe) {
                    LOG.info("User " + id + " doesn't exist.", unfe);
                }
            }

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
            
            message.setMessage(builder.toString());
            mailService.sendMessage(message);

        } catch(IOException ioe) {
            LOG.warn("Exception while updating CFDA codes.", ioe);
            return false;
        } catch(GroupNotFoundException gnfe) {
            LOG.fatal("Couldn't find workgroup to send notification to.", gnfe);
            return true;
        } catch(InvalidAddressException iae) {
            LOG.warn("The email address for one or more of the members of the " + MAIL_RECIPIENTS_GROUP_NAME + " workgroup is invalid.", iae);
            return true;
        }
        return true;
    }

    /**
     * 
     * This method is a simple setter used to set the local cfdaService attribute to the value provided.
     * @param cfdaService The service to be assigned.
     */
    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }
    
    /**
     * 
     * This method is a simple setter used to set the local mailService attribute to the value provided.
     * @param mailService The service to be assigned.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
    
    /**
     * 
     * This method is a simple setter used to set the local kualiGroupService attribute to the value provided.
     * @param kualiGroupService The service to be assigned.
     */
    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }

    /**
     * 
     * This method is a simple setter used to set the local universalUserService attribute to the value provided.
     * @param universalUserService The service to be assigned.
     */
    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
}
