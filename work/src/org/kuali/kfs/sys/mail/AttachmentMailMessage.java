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

import org.kuali.rice.core.api.mail.MailMessage;

/**
 * This class extends MailMessage to add attachment support
 */
public class AttachmentMailMessage extends MailMessage {

    private byte[] content = null;
    private String type;
    private String fileName;

    public AttachmentMailMessage() {
        super();
    }

    public AttachmentMailMessage(MailMessage message) {
        this.setToAddresses(message.getToAddresses());
        this.setBccAddresses(message.getBccAddresses());
        this.setCcAddresses(message.getCcAddresses());
        this.setSubject(message.getSubject());
        this.setMessage(message.getMessage());
        this.setFromAddress(message.getFromAddress());
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
