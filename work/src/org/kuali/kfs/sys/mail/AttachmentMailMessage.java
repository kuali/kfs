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
