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
package org.kuali.kfs.sys.service.impl;

import javax.mail.MessagingException;

import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.kfs.sys.mail.AttachmentMailer;
import org.kuali.kfs.sys.service.AttachmentMailService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.impl.MailServiceImpl;

/**
 * This class extends the Rice MailServiceImpl class to add support for attachments.
 */
public class AttachmentMailServiceImpl extends MailServiceImpl implements AttachmentMailService {

    protected AttachmentMailer mailer;

    @Override
    public void sendMessage(AttachmentMailMessage message) throws InvalidAddressException, MessagingException {
        mailer.sendEmail(composeMessage(message));
    }

    /**
     * This method composes an AttachmentMailMessage.
     *
     * @param message
     * @return
     */
    protected AttachmentMailMessage composeMessage(AttachmentMailMessage message){
        AttachmentMailMessage mm = new AttachmentMailMessage(super.composeMessage(message));
        mm.setContent(message.getContent());
        mm.setType(message.getType());
        mm.setFileName(message.getFileName());
        return mm;
    }

    public AttachmentMailer getMailer() {
        return mailer;
    }

    public void setMailer(AttachmentMailer mailer) {
        this.mailer = mailer;
    }

}
