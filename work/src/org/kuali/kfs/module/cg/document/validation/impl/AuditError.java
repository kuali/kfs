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


public class AuditError {
    
    private String key;
    private String[] params;
    private String link;
    
    public AuditError(String key, String link) {
        this.setKey(key);
        this.params = new String[5];
        this.setLink(link);
    }
    
    public AuditError(String key, String[] params, String link) {
        this(key, link);
        for (int i = 0; i < params.length; i++) {
            this.params[i] = params[i];
        }
    }

    /**
     * Gets the link attribute. 
     * @return Returns the link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link attribute value.
     * @param link The link to set.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets the key attribute. 
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key attribute value.
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the params attribute. 
     * @return Returns the params.
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Sets the params attribute value.
     * @param params The params to set.
     */
    public void setParams(String[] params) {
        this.params = params;
    }
}
