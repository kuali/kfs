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
package org.kuali.kfs.sys.service.impl;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.impl.MailServiceImpl;

/**
 * A mail service that's used to log messages instead of sending it out (useful for unit testing)
 */
public class TestMailService extends MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestMailService.class);
    
    /**
     * @see org.kuali.rice.kns.service.impl.MailServiceImpl#sendMessage(org.kuali.rice.kns.mail.MailMessage)
     */
    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException {
        if (StringUtils.isBlank(getBatchMailingList())) {
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
        else {
            StringBuilder buf = new StringBuilder();
            buf.append("Email To: ").append(message.getToAddresses()).append("\n");
            buf.append("Email CC: ").append(message.getCcAddresses()).append("\n");
            buf.append("Email BCC: ").append(message.getBccAddresses()).append("\n\n");
            buf.append(message.getMessage());
            
            message.getToAddresses().clear();
            message.addToAddress(getBatchMailingList());
            message.getBccAddresses().clear();
            message.getCcAddresses().clear();
            message.setMessage(buf.toString());
            
            super.sendMessage(message);
        }
    }
}
