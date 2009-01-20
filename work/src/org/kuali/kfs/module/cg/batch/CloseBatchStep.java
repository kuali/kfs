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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.CFDAClose;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;

/**
 * @see CloseService#close()
 */
public class CloseBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CloseBatchStep.class);

    private static String MAIL_RECIPIENTS_GROUP_NAME = "CG_CFDA_BATCH_NOTIFY";

    private CloseService closeService;
    private MailService mailService;
    private PersonService<Person> personService;

    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        String messageSubject = StringUtils.EMPTY;
        String messageBody = StringUtils.EMPTY;

        try {
            closeService.close();

            messageSubject = kualiConfigurationService.getPropertyString(CGKeyConstants.SUBJECT_CLOSE_JOB_SUCCEEDED);
            messageBody = kualiConfigurationService.getPropertyString(CGKeyConstants.MESSAGE_CLOSE_JOB_SUCCEEDED);
        }
        catch (Exception e) {
            messageSubject = kualiConfigurationService.getPropertyString(CGKeyConstants.SUBJECT_CLOSE_JOB_FAILED);

            String messageProperty = kualiConfigurationService.getPropertyString(CGKeyConstants.ERROR_CLOSE_JOB_FAILED);
            messageBody = MessageFormat.format(messageProperty, e.getMessage(), e.getCause().getMessage());

            LOG.error(messageBody, e);
        }

        // add a note onto the close document
        closeService.addDocumentNoteAfterClosing(messageBody);

        // get the email addresses of all message recipients
        List<String> recipientEmailAddresses = this.getRecipientEmailAddresses();

        // send a message to the message recipients about the status of the process
        this.sendMessage(recipientEmailAddresses, messageSubject, messageBody);

        return true;
    }

    // get the email addresses of all message recipients, including CG mail group and ad hoc route recipients of the current
    // document
    private List<String> getRecipientEmailAddresses() {
        List<String> recipientIds = new ArrayList<String>();
// TODO fix for kim
//        // retrieve the pricipal ids of CG CFDA batch mail recipient group
//        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
//        KimGroup workgroup = identityManagementService.getGroupByName(KFSConstants.KFS_GROUP_NAMESPACE, MAIL_RECIPIENTS_GROUP_NAME);
//        if (workgroup == null) {
//            recipientIds.addAll(identityManagementService.getGroupMemberPrincipalIds(workgroup.getGroupId()));
//        }

        // retrieve the pricipal ids of the ad hoc route route person of the current document
        CFDAClose closeDocument = closeService.getMostRecentClose();
        List<AdHocRoutePerson> adHocRoutePersons = closeDocument.getAdHocRoutePersons();
        for (AdHocRoutePerson adHocRoutePerson : adHocRoutePersons) {
            recipientIds.add(adHocRoutePerson.getId());
        }
     // TODO fix for kim

//        // retrieve the pricipal ids in the ad hoc route route workgroups of the current document
//        List<AdHocRouteRecipient> adHocRouteWorkgroups = closeDocument.getAdHocRouteWorkgroups();
//        for (AdHocRouteRecipient adHocRouteWorkgroup : adHocRouteWorkgroups) {
//            recipientIds.addAll(identityManagementService.getGroupMemberPrincipalIds(adHocRouteWorkgroup.getId()));
//        }

        // get the email addresses of all recipients
        List<String> recipientEmailAddresses = new ArrayList<String>();
        for (String pricinpalId : recipientIds) {
            Person user = personService.getPerson(pricinpalId);

            if (user != null) {
                String address = user.getEmailAddress();

                if (StringUtils.isNotBlank(address)) {
                    recipientEmailAddresses.add(address);
                }
            }
            else {
                LOG.info("User " + pricinpalId + " doesn't exist.");
            }
        }

        return recipientEmailAddresses;
    }

    // send the given message to the given recipients
    private void sendMessage(List<String> recipientEmailAddresses, String messageSubject, String messageBody) {
        if (StringUtils.isBlank(messageSubject) || StringUtils.isBlank(messageBody)) {
            LOG.warn("The email cannot be sent out with blank subject or message body.");
        }

        MailMessage message = new MailMessage();
        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(messageSubject);
        message.setMessage(messageBody);

        for (String email : recipientEmailAddresses) {
            message.addToAddress(email);
        }

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException iae) {
            LOG.warn("The email address for one or more of the members of the " + MAIL_RECIPIENTS_GROUP_NAME + " workgroup or ad hoc route recipients is invalid.", iae);
        }
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
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
