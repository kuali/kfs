/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.util;

/**
 * KRA Audit Error class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
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
