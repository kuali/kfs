/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class GrantDescription extends BusinessObjectBase {

    private String grantDescriptionCode;
    private String grantDescription;
    private String grantDescriptionActiveCode;

    /**
     * Default constructor.
     */
    public GrantDescription() {

    }

    /**
     * Gets the grantDescriptionCode attribute.
     * 
     * @return - Returns the grantDescriptionCode
     * 
     */
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    /**
     * Sets the grantDescriptionCode attribute.
     * 
     * @param grantDescriptionCode The grantDescriptionCode to set.
     * 
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }


    /**
     * Gets the grantDescription attribute.
     * 
     * @return - Returns the grantDescription
     * 
     */
    public String getGrantDescription() {
        return grantDescription;
    }

    /**
     * Sets the grantDescription attribute.
     * 
     * @param grantDescription The grantDescription to set.
     * 
     */
    public void setGrantDescription(String grantDescription) {
        this.grantDescription = grantDescription;
    }


    /**
     * Gets the grantDescriptionActiveCode attribute.
     * 
     * @return - Returns the grantDescriptionActiveCode
     * 
     */
    public String getGrantDescriptionActiveCode() {
        return grantDescriptionActiveCode;
    }

    /**
     * Sets the grantDescriptionActiveCode attribute.
     * 
     * @param grantDescriptionActiveCode The grantDescriptionActiveCode to set.
     * 
     */
    public void setGrantDescriptionActiveCode(String grantDescriptionActiveCode) {
        this.grantDescriptionActiveCode = grantDescriptionActiveCode;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("grantDescriptionCode", this.grantDescriptionCode);
        return m;
    }
}
