/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.util;

/**
 * KRA Audit Error class.
 * 
 * @author KRA (kualidev@oncourse.iu.edu)
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
