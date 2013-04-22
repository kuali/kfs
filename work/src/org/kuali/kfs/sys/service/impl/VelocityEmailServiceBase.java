package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.EmailBcList;
import org.kuali.rice.core.api.mail.EmailBody;
import org.kuali.rice.core.api.mail.EmailCcList;
import org.kuali.rice.core.api.mail.EmailFrom;
import org.kuali.rice.core.api.mail.EmailSubject;
import org.kuali.rice.core.api.mail.EmailToList;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

public abstract class VelocityEmailServiceBase implements VelocityEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VelocityEmailServiceBase.class);
    protected MailService mailService;
    protected ConfigurationService configurationService;
    protected ParameterService parameterService;
    protected VelocityEngine velocityEngine;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.service.VelocityEmailService#sendEmailNotification(java.util.Map)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendEmailNotification(final Map<String, Object> templateVariables) {
        String body = "";
        // Allow template variables can be retrieved from extending class
        try {
            final MailMessage mailMessage = constructMailMessage(templateVariables);
            body = VelocityEngineUtils.mergeTemplateIntoString(getVelocityEngine(), getTemplateUrl(), templateVariables);
            List<String> toList = new ArrayList<String>(mailMessage.getToAddresses());
            List<String> ccList = new ArrayList<String>(mailMessage.getCcAddresses());
            List<String> bccList = new ArrayList<String>(mailMessage.getBccAddresses());
            CoreApiServiceLocator.getMailer().sendEmail(new EmailFrom(mailMessage.getFromAddress()), new EmailToList(toList), new EmailSubject(mailMessage.getSubject()), new EmailBody(body), new EmailCcList(ccList), new EmailBcList(bccList), true);
        }
        catch (Exception ex) {
            LOG.error("Exception received when send email ", ex);
            LOG.error(body);
        }
    }

    /**
     * get the testing email address
     *
     * @return
     */
    protected Collection<String> getTestingEmailAddress() {
        // retrieve from system parameter.
        Collection<String> address = getParameterService().getParameterValuesAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.ACTION_LIST_DETAIL_TYPE, KewApiConstants.ACTIONLIST_EMAIL_TEST_ADDRESS);

        return address;
    }

    /**
     * Set message receiver email address if there are multiple
     *
     * @param emailReceiver
     * @param message
     */
    protected void setAndSplitEmailAddress(Collection<String> emailReceiver, MailMessage message) {
        // split email addresses
        for (String receiver : emailReceiver) {
            message.addToAddress(receiver);
        }
    }

    /**
     * Add BCC email address
     *
     * @param message
     * @param bccEmailReceivers
     */
    protected void setAndSplitBccEmailReceivers(MailMessage message, String bccEmailReceivers) {
        if (StringUtils.isNotBlank(bccEmailReceivers)) {
            // split email addresses
            String[] emailAddresses = bccEmailReceivers.split(";");
            for (int i = 0; i < emailAddresses.length; i++) {
                message.addBccAddress(emailAddresses[i]);
            }
        }
    }

    /**
     * Set up MailMessage
     *
     * @return
     */
    protected MailMessage constructMailMessage(final Map<String, Object> templateVariables) {
        MailMessage message = new MailMessage();
        // from...
        message.setFromAddress(mailService.getBatchMailingList());

        // to..subject...
        String productionEnvironmentCode = getConfigurationService().getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = getConfigurationService().getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);


        boolean isProduction = StringUtils.equals(productionEnvironmentCode, environmentCode);
        Collection<String> emailReceiver;
        if (isProduction) {
            message.setSubject(getEmailSubject());
            emailReceiver = getProdEmailReceivers();

        }
        else {
            message.setSubject(environmentCode + ": " + getEmailSubject());
            emailReceiver = getTestingEmailAddress();
        }

        setAndSplitEmailAddress(emailReceiver, message);

        String body = VelocityEngineUtils.mergeTemplateIntoString(getVelocityEngine(), getTemplateUrl(), templateVariables);
        message.setMessage(body);
        return message;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    /**
     * Sets the mailService attribute.
     *
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * ParameterService
     *
     * @return
     */
    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    /**
     * ParameterService
     *
     * @return
     */
    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    /**
     * Sets the velocityEngine attribute value.
     *
     * @param velocityEngine The velocityEngine to set.
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }
}
