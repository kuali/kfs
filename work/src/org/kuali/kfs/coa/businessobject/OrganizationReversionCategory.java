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

package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationReversionCategory extends BusinessObjectBase {

    private String organizationReversionCategoryCode;
    private String organizationReversionCategoryName;
    private String organizationReversionSortCode;

    /**
     * Default constructor.
     */
    public OrganizationReversionCategory() {

    }

    /**
     * Gets the organizationReversionCategoryCode attribute.
     * 
     * @return - Returns the organizationReversionCategoryCode
     * 
     */
    public String getOrganizationReversionCategoryCode() {
        return organizationReversionCategoryCode;
    }

    /**
     * Sets the organizationReversionCategoryCode attribute.
     * 
     * @param organizationReversionCategoryCode The organizationReversionCategoryCode to set.
     * 
     */
    public void setOrganizationReversionCategoryCode(String organizationReversionCategoryCode) {
        this.organizationReversionCategoryCode = organizationReversionCategoryCode;
    }


    /**
     * Gets the organizationReversionCategoryName attribute.
     * 
     * @return - Returns the organizationReversionCategoryName
     * 
     */
    public String getOrganizationReversionCategoryName() {
        return organizationReversionCategoryName;
    }

    /**
     * Sets the organizationReversionCategoryName attribute.
     * 
     * @param organizationReversionCategoryName The organizationReversionCategoryName to set.
     * 
     */
    public void setOrganizationReversionCategoryName(String organizationReversionCategoryName) {
        this.organizationReversionCategoryName = organizationReversionCategoryName;
    }


    /**
     * Gets the organizationReversionSortCode attribute.
     * 
     * @return - Returns the organizationReversionSortCode
     * 
     */
    public String getOrganizationReversionSortCode() {
        return organizationReversionSortCode;
    }

    /**
     * Sets the organizationReversionSortCode attribute.
     * 
     * @param organizationReversionSortCode The organizationReversionSortCode to set.
     * 
     */
    public void setOrganizationReversionSortCode(String organizationReversionSortCode) {
        this.organizationReversionSortCode = organizationReversionSortCode;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        return m;
    }
}
