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

import java.util.Date;
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
import org.kuali.module.cg.service.CloseService;

/**
 * @see CloseService#close()
 */
public class CloseBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CloseBatchStep.class);
    private static String MAIL_RECIPIENTS_GROUP_NAME = "CG_CFDA_BATCH_NOTIFY";
    private static String STATUS_SUBJECT = "Close Batch Step: ";

    private CloseService closeService;
    private MailService mailService;
    private KualiGroupService kualiGroupService;
    private UniversalUserService universalUserService;

    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {

        MailMessage message = new MailMessage();

        try {

            // TODO this message should come from some config file.
            StringBuilder builder = new StringBuilder();

            try {
                closeService.close();
                message.setSubject(STATUS_SUBJECT + "SUCCEEDED");
                builder.append("The Close batch script ran successfully.\n");
            }
            catch (Exception e) {
                message.setSubject(STATUS_SUBJECT + "FAILED");
                builder.append("An Exception was encountered when running the close job.\n");
                builder.append("The exception message is: ").append(e.getMessage()).append("\n");
                builder.append("The message from the exception cause is: ").append(e.getCause().getMessage()).append("\n");
                builder.append("Please see the log for more details.\n");

                LOG.error("The following exception was encountered during the close batch process.", e);
            }

            KualiGroup workgroup = kualiGroupService.getByGroupName(MAIL_RECIPIENTS_GROUP_NAME);
            List<String> memberNetworkIds = workgroup.getGroupUsers();
            for (String id : memberNetworkIds) {
                try {
                    AuthenticationUserId authId = new AuthenticationUserId(id.toUpperCase());
                    UniversalUser user = universalUserService.getUniversalUser(authId);
                    String address = user.getPersonEmailAddress();
                    if (null != address && !StringUtils.isEmpty(address)) {
                        message.addToAddress(address);
                    }
                }
                catch (UserNotFoundException unfe) {
                    LOG.info("User " + id + " doesn't exist.", unfe);
                }
            }

            // Don't send it if no recipients were specified.
            if (0 != message.getToAddresses().size()) {
                message.setMessage(builder.toString());
                String from = mailService.getBatchMailingList();
                if (null != from) {
                    message.setFromAddress(from);
                }
                mailService.sendMessage(message);
            }

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
     * Sets the {@link CloseService}. For use by Spring.
     * 
     * @param closeService The value to be used to assign to the local attribute <code>closeService</code>.
     */
    public void setCloseService(org.kuali.module.cg.service.CloseService closeService) {
        this.closeService = closeService;
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
     * Set the {@link MailService}. For use by Spring.
     * 
     * @param mailService The service to be assigned.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
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
