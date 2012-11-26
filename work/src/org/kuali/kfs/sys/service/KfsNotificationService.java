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
