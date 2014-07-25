/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import javax.mail.MessagingException;

import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.rice.krad.exception.InvalidAddressException;

/**
 * This service extends the KRAD MailMessage to add attachment support
 */
public interface AttachmentMailService extends org.kuali.rice.krad.service.MailService {

    /**
     * This method sends an email message with an attachment.
     *
     * @param message
     * @throws InvalidAddressException
     * @throws MessagingException
     */
    public void sendMessage(AttachmentMailMessage message) throws InvalidAddressException, MessagingException;

}
