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

    protected AttachmentMailer attachmentMailer;

    @Override
    public void sendMessage(AttachmentMailMessage message) throws InvalidAddressException, MessagingException {
        attachmentMailer.sendEmail(composeMessage(message));
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

    public AttachmentMailer getAttachmentMailer() {
        return attachmentMailer;
    }

    public void setAttachmentMailer(AttachmentMailer attachmentMailer) {
        this.attachmentMailer = attachmentMailer;
    }

}
