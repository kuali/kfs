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

import java.util.Map;

import org.kuali.rice.core.api.mail.MailMessage;

public interface KfsNotificationService {

    /**
     * send a notification by email
     *
     * @param mailMessage the given mail message
     */
    void sendNotificationByMail(MailMessage mailMessage);

    /**
     * generate notification content from the given template and data stored in the model
     *
     * @param template the given template
     * @param model the given model that contains the data
     * @return notification content generated from the given template and data stored in the model
     */
    String generateNotificationContent(String template, Map<String, Object> model);
}
