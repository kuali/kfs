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
package org.kuali.kfs.sys.mail;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.kuali.rice.core.mail.MailerImpl;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

/**
 * This class extends the Rice MailerImpl to add attachment support.
 */
public class AttachmentMailerImpl extends MailerImpl implements AttachmentMailer {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttachmentMailerImpl.class);

    protected JavaMailSenderImpl mailSender;

    /**
     * Construct and a send mime email message from an Attachment Mail Message.
     *
     * @param message the Attachement Mail Message
     * @throws MessagingException
     */
    @Override
    public void sendEmail(AttachmentMailMessage message) throws MessagingException {
        // Construct a mime message from the Attachment Mail Message

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeBodyPart body = new MimeBodyPart();
        body.setText(message.getMessage());

        MimeBodyPart attachment = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(body);
        ByteArrayDataSource ds = new ByteArrayDataSource(message.getContent(), message.getType());
        attachment.setDataHandler(new DataHandler(ds));
        attachment.setFileName(message.getFileName());
        multipart.addBodyPart(attachment);
        mimeMessage.setContent(multipart);

        MimeMailMessage mmm = new MimeMailMessage(mimeMessage);

        mmm.setTo( (String[])message.getToAddresses().toArray(new String[message.getToAddresses().size()]) );
        mmm.setBcc( (String[])message.getBccAddresses().toArray(new String[message.getBccAddresses().size()]) );
        mmm.setCc( (String[])message.getCcAddresses().toArray(new String[message.getCcAddresses().size()]) );
        mmm.setSubject(message.getSubject());
        mmm.setFrom(message.getFromAddress());

        try {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "sendEmail() - Sending message: " + mmm.toString() );
            }
            mailSender.send(mmm.getMimeMessage());
        }
        catch (Exception e) {
            LOG.error("sendEmail() - Error sending email.", e);
            throw new RuntimeException(e);
        }
    }

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    @Override
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

}
