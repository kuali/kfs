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
 * @author Kuali Nervous System Team ()
 */
public class ResponsibilityCenter extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public ResponsibilityCenter() {

    }

    private String responsibilityCenterCode;
    private String responsibilityCenterName;
    private String responsibilityCenterShortName;
    private boolean responsibilityCenterActiveIndicator;

    /**
     * Gets the responsibilityCenterCode attribute.
     * 
     * @return - Returns the responsibilityCenterCode
     * 
     */
    public String getResponsibilityCenterCode() {
        return responsibilityCenterCode;
    }

    /**
     * Sets the responsibilityCenterCode attribute.
     * 
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     * 
     */
    public void setResponsibilityCenterCode(String responsibilityCenterCode) {
        this.responsibilityCenterCode = responsibilityCenterCode;
    }

    /**
     * Gets the responsibilityCenterName attribute.
     * 
     * @return - Returns the responsibilityCenterName
     * 
     */
    public String getResponsibilityCenterName() {
        return responsibilityCenterName;
    }

    /**
     * Sets the responsibilityCenterName attribute.
     * 
     * @param responsibilityCenterName The responsibilityCenterName to set.
     * 
     */
    public void setResponsibilityCenterName(String responsibilityCenterName) {
        this.responsibilityCenterName = responsibilityCenterName;
    }

    /**
     * Gets the responsibilityCenterShortName attribute.
     * 
     * @return - Returns the responsibilityCenterShortName
     * 
     */
    public String getResponsibilityCenterShortName() {
        return responsibilityCenterShortName;
    }

    /**
     * Sets the responsibilityCenterShortName attribute.
     * 
     * @param responsibilityCenterShortName The responsibilityCenterShortName to set.
     * 
     */
    public void setResponsibilityCenterShortName(String responsibilityCenterShortName) {
        this.responsibilityCenterShortName = responsibilityCenterShortName;
    }

    /**
     * Gets the responsibilityCenterActiveIndicator attribute.
     * 
     * @return - Returns the responsibilityCenterActiveIndicator
     * 
     */
    public boolean getResponsibilityCenterActiveIndicator() {
        return responsibilityCenterActiveIndicator;
    }

    /**
     * Sets the _responsibilityCenterActiveIndicator_ attribute.
     * 
     * @param _responsibilityCenterActiveIndicator_ The _responsibilityCenterActiveIndicator_ to set.
     * 
     */
    public void setResponsibilityCenterActiveIndicator(boolean responsibilityCenterActiveIndicator) {
        this.responsibilityCenterActiveIndicator = responsibilityCenterActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("responsibilityCenterCode", this.responsibilityCenterCode);

        return m;
    }
}
