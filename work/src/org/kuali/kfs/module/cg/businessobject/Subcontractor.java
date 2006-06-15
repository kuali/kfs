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
public class Subcontractor extends BusinessObjectBase {

    private String subcontractorNumber;
    private String subcontractorName;
    private String subcontractorAddressLine1;
    private String subcontractorAddressLine2;
    private String subcontractorCity;
    private String subcontractorStateCode;
    private String subcontractorZipCode;
    private String subcontractorCountryCode;

    /**
     * Default no-arg constructor.
     */
    public Subcontractor() {

    }

    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return - Returns the subcontractorNumber
     * 
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     * 
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }

    /**
     * Gets the subcontractorName attribute.
     * 
     * @return - Returns the subcontractorName
     * 
     */
    public String getSubcontractorName() {
        return subcontractorName;
    }

    /**
     * Sets the subcontractorName attribute.
     * 
     * @param subcontractorName The subcontractorName to set.
     * 
     */
    public void setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
    }

    /**
     * Gets the subcontractorAddressLine1 attribute.
     * 
     * @return - Returns the subcontractorAddressLine1
     * 
     */
    public String getSubcontractorAddressLine1() {
        return subcontractorAddressLine1;
    }

    /**
     * Sets the subcontractorAddressLine1 attribute.
     * 
     * @param subcontractorAddressLine1 The subcontractorAddressLine1 to set.
     * 
     */
    public void setSubcontractorAddressLine1(String subcontractorAddressLine1) {
        this.subcontractorAddressLine1 = subcontractorAddressLine1;
    }

    /**
     * Gets the subcontractorAddressLine2 attribute.
     * 
     * @return - Returns the subcontractorAddressLine2
     * 
     */
    public String getSubcontractorAddressLine2() {
        return subcontractorAddressLine2;
    }

    /**
     * Sets the subcontractorAddressLine2 attribute.
     * 
     * @param subcontractorAddressLine2 The subcontractorAddressLine2 to set.
     * 
     */
    public void setSubcontractorAddressLine2(String subcontractorAddressLine2) {
        this.subcontractorAddressLine2 = subcontractorAddressLine2;
    }

    /**
     * Gets the subcontractorCity attribute.
     * 
     * @return - Returns the subcontractorCity
     * 
     */
    public String getSubcontractorCity() {
        return subcontractorCity;
    }

    /**
     * Sets the subcontractorCity attribute.
     * 
     * @param subcontractorCity The subcontractorCity to set.
     * 
     */
    public void setSubcontractorCity(String subcontractorCity) {
        this.subcontractorCity = subcontractorCity;
    }

    /**
     * Gets the subcontractorStateCode attribute.
     * 
     * @return Returns the subcontractorStateCode.
     */
    public String getSubcontractorStateCode() {
        return subcontractorStateCode;
    }

    /**
     * Sets the subcontractorStateCode attribute value.
     * 
     * @param subcontractorStateCode The subcontractorStateCode to set.
     */
    public void setSubcontractorStateCode(String subcontractorStateCode) {
        this.subcontractorStateCode = subcontractorStateCode;
    }

    /**
     * Gets the subcontractorZipCode attribute.
     * 
     * @return - Returns the subcontractorZipCode
     * 
     */
    public String getSubcontractorZipCode() {
        return subcontractorZipCode;
    }

    /**
     * Sets the subcontractorZipCode attribute.
     * 
     * @param subcontractorZipCode The subcontractorZipCode to set.
     * 
     */
    public void setSubcontractorZipCode(String subcontractorZipCode) {
        this.subcontractorZipCode = subcontractorZipCode;
    }

    /**
     * Gets the subcontractorCountryCode attribute.
     * 
     * @return - Returns the subcontractorCountryCode
     * 
     */
    public String getSubcontractorCountryCode() {
        return subcontractorCountryCode;
    }

    /**
     * Sets the subcontractorCountryCode attribute.
     * 
     * @param subcontractorCountryCode The subcontractorCountryCode to set.
     * 
     */
    public void setSubcontractorCountryCode(String subcontractorCountryCode) {
        this.subcontractorCountryCode = subcontractorCountryCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("subcontractorNumber", this.getSubcontractorNumber());
        return m;
    }
}
