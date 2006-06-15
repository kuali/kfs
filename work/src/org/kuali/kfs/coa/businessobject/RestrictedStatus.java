package org.kuali.module.chart.bo;

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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RestrictedStatus extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public RestrictedStatus() {

    }

    private String accountRestrictedStatusCode;
    private String accountRestrictedStatusName;

    /**
     * Gets the accountRestrictedStatusCode attribute.
     * 
     * @return - Returns the accountRestrictedStatusCode
     * 
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * Sets the accountRestrictedStatusCode attribute.
     * 
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     * 
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * Gets the accountRestrictedStatusName attribute.
     * 
     * @return - Returns the accountRestrictedStatusName
     * 
     */
    public String getAccountRestrictedStatusName() {
        return accountRestrictedStatusName;
    }

    /**
     * Sets the accountRestrictedStatusName attribute.
     * 
     * @param accountRestrictedStatusName The accountRestrictedStatusName to set.
     * 
     */
    public void setAccountRestrictedStatusName(String accountRestrictedStatusName) {
        this.accountRestrictedStatusName = accountRestrictedStatusName;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("accountRestrictedStatusCode", this.accountRestrictedStatusCode);

        return m;
    }
}
