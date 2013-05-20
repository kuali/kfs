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
package org.kuali.kfs.module.cg.service.impl;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.batch.UpcomingMilestoneNotificationStep;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.Milestone;
import org.kuali.kfs.module.cg.service.CGEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.mail.Mailer;
import org.kuali.rice.kew.mail.service.impl.DefaultEmailService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.core.mail.MailMessage;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class implements the services in CGEmailservice.
 */
public class CGEmailServiceImpl extends DefaultEmailService implements CGEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CGEmailServiceImpl.class);

    protected MailService mailService;
    private ConfigurationService kualiConfigurationService;
    protected ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private Mailer mailer;

    /**
     * This method sends out emails for upcoming milestones.
     * 
     * @see org.kuali.kfs.module.cg.service.CGEmailService#sendEmail(java.util.List, org.kuali.kfs.module.cg.businessobject.Award)
     */
    public void sendEmail(List<Milestone> milestones, Award award) {
        LOG.debug("sendEmail() starting");

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(CGConstants.CGEmailConstants.AWARD_MILESTONE_REMINDER_EMAIL_SUBJECT));
        message.getToAddresses().add(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress());
        StringBuffer body = new StringBuffer();


        String messageKey = kualiConfigurationService.getPropertyValueAsString(CGKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_1);
        body.append(MessageFormat.format(messageKey, new Object[] { null }));

        body.append(award.getProposalNumber() + ".\n\n");

        for (Milestone milestone : milestones) {

            String milestoneNumber = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneNumber");
            String milestoneDescription = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneDescription");
            String milestoneAmount = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneAmount");
            String milestoneExpectedCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneExpectedCompletionDate");
            String milestoneActualCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneActualCompletionDate");


            body.append(milestoneNumber + ": " + milestone.getMilestoneNumber() + " \n");
            body.append(milestoneDescription + ": " + milestone.getMilestoneDescription() + " \n");
            body.append(milestoneAmount + ": " + milestone.getMilestoneAmount() + " \n");
            body.append(milestoneExpectedCompletionDate + ": " + milestone.getMilestoneExpectedCompletionDate() + " \n");

            body.append("\n\n");
        }
        body.append("\n\n");

        messageKey = kualiConfigurationService.getPropertyValueAsString(CGKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_2);
        body.append(MessageFormat.format(messageKey, new Object[] { null }) + "\n\n");

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendEmail() Invalid email address.  Message not sent", e);
        }
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     * 
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        String subject = parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, subjectParmaterName);

        String productionEnvironmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }

        return subject;
    }

    /**
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
