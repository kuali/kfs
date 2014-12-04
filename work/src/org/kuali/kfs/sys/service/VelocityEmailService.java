/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
     * customize cc email receiver in PROD environment
     *
     * @return
     */
    Collection<String> getCcEmailReceivers();

    /**
     * customize bcc email receiver in PROD environment
     *
     * @return
     */
    Collection<String> getBccEmailReceivers();

    /**
     * retrieve the template file for velocity parsing
     *
     * @return
     */
    String getTemplateUrl();
}
