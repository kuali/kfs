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

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.MailService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.service.CfdaUpdateResults;

/**
 * Parses data from a government web page listing the valid CFDA codes. The codes are then compared with what's in the CFDA table in
 * Kuali. Codes set to be managed automatically are reconciled with what's on the web page. Codes managed manually are left alone.
 * Finally an email containing a summary of what was done by the step execution is sent to the member of the CG_CFDA_BATCH_NOTIFY workgroup.
 */
public class CfdaBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CfdaBatchStep.class);
    private static String MAIL_RECIPIENTS_GROUP_NAME = "CG_CFDA_BATCH_NOTIFY";

    private CfdaService cfdaService;
    private MailService mailService;
    private KualiGroupService kualiGroupService;
    private UniversalUserService universalUserService;

    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName) throws InterruptedException {
        MailMessage message = new MailMessage();

        try {
            CfdaUpdateResults results = cfdaService.update();

            KualiGroup workgroup = kualiGroupService.getByGroupName(MAIL_RECIPIENTS_GROUP_NAME);
            List<String> memberNetworkIds = workgroup.getGroupUsers();
            for (String id : memberNetworkIds) {
                try {
                    AuthenticationUserId authId = new AuthenticationUserId(id.toUpperCase());
                    UniversalUser user = universalUserService.getUniversalUser(authId);
                    String address = user.getPersonEmailAddress();
                    if (!StringUtils.isEmpty(address)) {
                        message.addToAddress(address);
                    }
                }
                catch (UserNotFoundException unfe) {
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
            builder.append(" - Message\n");
            builder.append(null != results.getMessage() ? results.getMessage() : "");

            message.setMessage(builder.toString());
            mailService.sendMessage(message);

        }
        catch (IOException ioe) {
            LOG.warn("Exception while updating CFDA codes.", ioe);
            return false;
        }
        catch (GroupNotFoundException gnfe) {
            LOG.fatal("Couldn't find workgroup to send notification to.", gnfe);
            return true;
        }
        catch (InvalidAddressException iae) {
            LOG.warn("The email address for one or more of the members of the " + MAIL_RECIPIENTS_GROUP_NAME + " workgroup is invalid.", iae);
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
     * Sets the {@link KualiGroupService}. For use by Spring.
     * 
     * @param kualiGroupService The service to be assigned.
     */
    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }

    /**
     * Sets the {@link UniversalUserService}. For use by Spring.
     * 
     * @param universalUserService The service to be assigned.
     */
    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

}
