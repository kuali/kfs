/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.service.KfsNotificationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.krad.service.MailService;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * implement the service methods defined in KFS notification service
 */
public class KfsNotificationServiceImpl implements KfsNotificationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KfsNotificationServiceImpl.class);

    public final String TO_ADDRESSES = "toAddresses";
    public final String FROM_ADDRESS = "fromAddress";
    public final String SUBJECT = "subject";
    public final String MESSAGE = "message";

    private VelocityEngine velocityEngine;
    private ConfigurationService configurationService;
    private MailService mailService;

    /**
     * @see org.kuali.kfs.sys.service.KfsNotificationService#generateNotificationContent(java.lang.String, java.util.Map)
     */
    @Override
    public String generateNotificationContent(String template, Map<String, Object> model) {
        return VelocityEngineUtils.mergeTemplateIntoString(this.getVelocityEngine(), template, model);
    }

    /**
     * @see org.kuali.kfs.sys.service.KfsNotificationService#sendNotificationByMail(org.kuali.rice.kns.mail.MailMessage)
     */
    @Override
    public void sendNotificationByMail(MailMessage mailMessage) {
        try {
            this.setupForNonProductionEnviroment(mailMessage);

            mailService.sendMessage(mailMessage);
        }
        catch (Exception iae) {
            String invalidMailAddressMessage = "[Invalid email address: " + mailMessage.getToAddresses() + "]";
            LOG.error(invalidMailAddressMessage, iae);

            mailMessage.addToAddress(this.getMailService().getBatchMailingList());
            mailMessage.addToAddress(mailMessage.getFromAddress());

            String returnMailSubject = mailMessage.getSubject() + invalidMailAddressMessage;
            mailMessage.setSubject(returnMailSubject);

            try {
                mailService.sendMessage(mailMessage);
            }
            catch (Exception e1) {
                LOG.error("Failed to send the email: \n" + this.mailMessageToString(mailMessage));
            }
        }
    }

    /**
     * setup some mail message properties for non-production environment
     */
    protected void setupForNonProductionEnviroment(MailMessage mailMessage) {
        String productionEnvironmentCode = this.getConfigurationService().getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = this.getConfigurationService().getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            LOG.info("This is the email to be sent in PRODUCTION: \n" + this.mailMessageToString(mailMessage));

            String nonProductionSubject = environmentCode + ": " + mailMessage.getSubject();
            mailMessage.setSubject(nonProductionSubject);

            // not sending any email to the real receivers, instead of testers
            Set<String> toAddresses = mailMessage.getToAddresses();
            toAddresses.clear();

            mailMessage.addToAddress(this.getMailService().getBatchMailingList());

            LOG.info("This is the email to be sent in TESTING: \n" + this.mailMessageToString(mailMessage));
        }
    }

    protected String mailMessageToString(MailMessage mailMessage){
        List<String> keyFields = new ArrayList<String>();
        keyFields.add(TO_ADDRESSES);
        keyFields.add(FROM_ADDRESS);
        keyFields.add(SUBJECT);
        keyFields.add(MESSAGE);

        return String.valueOf(ObjectUtil.buildPropertyMap(mailMessage, keyFields));
    }

    /**
     * Gets the velocityEngine attribute.
     * @return Returns the velocityEngine.
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * Sets the velocityEngine attribute value.
     * @param velocityEngine The velocityEngine to set.
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Gets the mailService attribute.
     * @return Returns the mailService.
     */
    public MailService getMailService() {
        return mailService;
    }

    /**
     * Sets the mailService attribute value.
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
