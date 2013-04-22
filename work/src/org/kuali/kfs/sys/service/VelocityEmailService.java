package org.kuali.kfs.sys.service;

import java.util.Collection;
import java.util.Map;

public interface VelocityEmailService {
    /**
     * Send email notification with provided template variables being initialized and instantiated.
     *
     * @param templateVariables
     */
    void sendEmailNotification(final Map<String, Object> templateVariables);

    /**
     * customize email subject
     *
     * @return
     */
    String getEmailSubject();

    /**
     * customize email receiver in PROD environment
     *
     * @return
     */
    Collection<String> getProdEmailReceivers();

    /**
     * retrieve the template file for velocity parsing
     *
     * @return
     */
    String getTemplateUrl();
}
