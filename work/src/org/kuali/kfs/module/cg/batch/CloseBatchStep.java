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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.MailService;

/**
 * @see CloseService#close()
 */
public class CloseBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CloseBatchStep.class);
    private static String MAIL_RECIPIENTS_GROUP_NAME = "CG_CFDA_BATCH_NOTIFY";
    private static String STATUS_SUBJECT = "Close Batch Step: ";

    private CloseService closeService;
    private MailService mailService;
    private GroupService kimGroupService;
    private org.kuali.rice.kim.service.PersonService personService;

    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
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

            KimGroup workgroup = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, MAIL_RECIPIENTS_GROUP_NAME);
            if (workgroup == null) {
                LOG.fatal("Couldn't find workgroup to send notification to.");
                return true;
            }
            List<String> principalIds = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupMemberPrincipalIds(workgroup.getGroupId());
            for (String id : principalIds) {                
                Person user = personService.getPerson(id);
                if (user != null) {
                    String address = user.getEmailAddress();
                    if (null != address && !StringUtils.isEmpty(address)) {
                        message.addToAddress(address);
                    }
                } else {
                    LOG.info("User " + id + " doesn't exist.");
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
    public void setCloseService(org.kuali.kfs.module.cg.service.CloseService closeService) {
        this.closeService = closeService;
    }

    /**
     * Sets the {@link GroupService}. For use by Spring.
     * 
     * @param kimGroupService The service to be assigned.
     */
    public void setGroupService(GroupService kimGroupService) {
        this.kimGroupService = kimGroupService;
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
     * Sets the {@link org.kuali.rice.kim.service.PersonService}. For use by Spring.
     * 
     * @param personService The service to be assigned.
     */
    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
    }

}

