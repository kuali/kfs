/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.test;

import java.util.Iterator;

import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.MailService;
import org.kuali.core.service.impl.MailServiceImpl;

/**
 * A mail service that's used to log messages instead of sending it out (useful for unit testing)
 */
public class NonSendingMailService extends MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NonSendingMailService.class);
    
    /**
     * @see org.kuali.core.service.impl.MailServiceImpl#sendMessage(org.kuali.core.mail.MailMessage)
     */
    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Email message");
            if (message.getToAddresses().isEmpty()) {
                LOG.info("No email to address specified");
            }
            for (Iterator i = message.getToAddresses().iterator(); i.hasNext();) {
                LOG.info("Email To: " + i.next());
            }
            LOG.info("Email To: " + message.getToAddresses());
            LOG.info("Email From: " + message.getFromAddress());
            LOG.info("Email subject: " + message.getSubject());
            LOG.info("Email message:\n" + message.getMessage());
        }        
    }

}
