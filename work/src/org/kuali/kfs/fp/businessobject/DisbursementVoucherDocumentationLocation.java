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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentationLocation extends BusinessObjectBase {

    private String disbursementVoucherDocumentationLocationCode;
    private String disbursementVoucherDocumentationLocationName;
    private String disbursementVoucherDocumentationLocationAddress;

    /**
     * Default constructor.
     */
    public DisbursementVoucherDocumentationLocation() {

    }

    /**
     * Gets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @return - Returns the disbursementVoucherDocumentationLocationCode
     * 
     */
    public String getDisbursementVoucherDocumentationLocationCode() {
        return disbursementVoucherDocumentationLocationCode;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @param disbursementVoucherDocumentationLocationCode The disbursementVoucherDocumentationLocationCode to set.
     * 
     */
    public void setDisbursementVoucherDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.disbursementVoucherDocumentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }


    /**
     * Gets the disbursementVoucherDocumentationLocationName attribute.
     * 
     * @return - Returns the disbursementVoucherDocumentationLocationName
     * 
     */
    public String getDisbursementVoucherDocumentationLocationName() {
        return disbursementVoucherDocumentationLocationName;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationName attribute.
     * 
     * @param disbursementVoucherDocumentationLocationName The disbursementVoucherDocumentationLocationName to set.
     * 
     */
    public void setDisbursementVoucherDocumentationLocationName(String disbursementVoucherDocumentationLocationName) {
        this.disbursementVoucherDocumentationLocationName = disbursementVoucherDocumentationLocationName;
    }


    /**
     * Gets the disbursementVoucherDocumentationLocationAddress attribute.
     * 
     * @return - Returns the disbursementVoucherDocumentationLocationAddress
     * 
     */
    public String getDisbursementVoucherDocumentationLocationAddress() {
        return disbursementVoucherDocumentationLocationAddress;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationAddress attribute.
     * 
     * @param disbursementVoucherDocumentationLocationAddress The disbursementVoucherDocumentationLocationAddress to set.
     * 
     */
    public void setDisbursementVoucherDocumentationLocationAddress(String disbursementVoucherDocumentationLocationAddress) {
        this.disbursementVoucherDocumentationLocationAddress = disbursementVoucherDocumentationLocationAddress;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("disbursementVoucherDocumentationLocationCode", this.disbursementVoucherDocumentationLocationCode);
        return m;
    }
}
