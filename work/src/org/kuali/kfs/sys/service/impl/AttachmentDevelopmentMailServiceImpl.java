/*
 * Copyright 2012 The Kuali Foundation.
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
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.krad.exception.InvalidAddressException;

/**
 * This class is useful in development environments where it will log rather than send email messages.
 * This version supports attachments.
 */
public class AttachmentDevelopmentMailServiceImpl extends AttachmentMailServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttachmentDevelopmentMailServiceImpl.class);

    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
        LOG.info( "*********************** EMAIL SEND *****************************");
        LOG.info( "FROM : " + message.getToAddresses() );
        LOG.info( "TO   : " + message.getFromAddress() );
        LOG.info( "CC   : " + message.getCcAddresses() );
        LOG.info( "BCC  : " + message.getBccAddresses() );
        LOG.info( "SUBJECT : " + message.getSubject() );
        LOG.info( "MESSAGE : \n" + message.getMessage() );

        LOG.info( "*********************** END EMAIL  *****************************");
    }

    @Override
    public void sendMessage(AttachmentMailMessage message) throws InvalidAddressException, MessagingException {
        LOG.info( "*********************** EMAIL SEND *****************************");
        LOG.info( "FROM : " + message.getToAddresses() );
        LOG.info( "TO   : " + message.getFromAddress() );
        LOG.info( "CC   : " + message.getCcAddresses() );
        LOG.info( "BCC  : " + message.getBccAddresses() );
        LOG.info( "SUBJECT : " + message.getSubject() );
        LOG.info( "MESSAGE : \n" + message.getMessage() );
        LOG.info( "ATTACHMENT: \n");
        LOG.info( "\t FILE NAME : " + message.getFileName());
        LOG.info( "\t SIZE      : " + message.getContent().length);
        LOG.info( "\t MIME TYPE : " + message.getType());

        LOG.info( "*********************** END EMAIL  *****************************");
    }
}
