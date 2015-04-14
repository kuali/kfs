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
package org.kuali.kfs.module.cg.document.validation.impl;

/**
 * KRA Audit Error class.
 */
public class AuditError {

    private String errorKey;
    private String messageKey;
    private String link;
    private String[] params;

    public AuditError(String errorKey, String messageKey, String link) {
        this.setErrorKey(errorKey);
        this.setMessageKey(messageKey);
        this.setLink(link);
        this.params = new String[5]; // bean:message takes up to 5 tokenized parameters
    }

    public AuditError(String errorKey, String messageKey, String link, String[] params) {
        this(errorKey, messageKey, link);
        this.setParams(params);
    }

    /**
     * Gets the errorKey attribute.
     * 
     * @return Returns the errorKey.
     */
    public String getErrorKey() {
        return errorKey;
    }

    /**
     * Sets the errorKey attribute value.
     * 
     * @param errorKey The errorKey to set.
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    /**
     * Gets the link attribute.
     * 
     * @return Returns the link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link attribute value.
     * 
     * @param link The link to set.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets the key attribute.
     * 
     * @return Returns the key.
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Sets the key attribute value.
     * 
     * @param key The key to set.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Gets the params attribute.
     * 
     * @return Returns the params.
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Sets the params attribute value.
     * 
     * @param params The params to set.
     */
    public void setParams(String[] params) {
        this.params = params;
    }
}
