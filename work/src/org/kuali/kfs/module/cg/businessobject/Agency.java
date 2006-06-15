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
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.bo.AgencyExtension;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Agency extends BusinessObjectBase {

    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String agencyTypeCode;
    private String reportsToAgencyNumber;
    private KualiDecimal indirectAmount;
    private boolean historicalIndicator;
    private boolean inStateIndicator;
    private Agency reportsToAgency;
    private AgencyType agencyType;
    private AgencyExtension agencyExtension;

    /**
     * Gets the agencyTypeCode attribute.
     * 
     * @return Returns the agencyTypeCode.
     */
    public String getAgencyTypeCode() {
        return agencyTypeCode;
    }

    /**
     * Sets the agencyTypeCode attribute value.
     * 
     * @param agencyTypeCode The agencyTypeCode to set.
     */
    public void setAgencyTypeCode(String agencyTypeCode) {
        this.agencyTypeCode = agencyTypeCode;
    }

    /**
     * Gets the reportsToAgencyNumber attribute.
     * 
     * @return Returns the reportsToAgencyNumber.
     */
    public String getReportsToAgencyNumber() {
        return reportsToAgencyNumber;
    }

    /**
     * Sets the reportsToAgencyNumber attribute value.
     * 
     * @param reportsToAgencyNumber The reportsToAgencyNumber to set.
     */
    public void setReportsToAgencyNumber(String reportsToAgencyNumber) {
        this.reportsToAgencyNumber = reportsToAgencyNumber;
    }

    /**
     * Default no-arg constructor.
     */
    public Agency() {

    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return - Returns the agencyNumber
     * 
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     * 
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the reportingName attribute.
     * 
     * @return - Returns the reportingName
     * 
     */
    public String getReportingName() {
        return reportingName;
    }

    /**
     * Sets the reportingName attribute.
     * 
     * @param reportingName The reportingName to set.
     * 
     */
    public void setReportingName(String reportingName) {
        this.reportingName = reportingName;
    }

    /**
     * Gets the fullName attribute.
     * 
     * @return - Returns the fullName
     * 
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the fullName attribute.
     * 
     * @param fullName The fullName to set.
     * 
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the indirectAmount attribute.
     * 
     * @return - Returns the indirectAmount
     * 
     */
    public KualiDecimal getIndirectAmount() {
        return indirectAmount;
    }

    /**
     * Sets the indirectAmount attribute.
     * 
     * @param indirectAmount The indirectAmount to set.
     * 
     */
    public void setIndirectAmount(KualiDecimal indirectAmount) {
        this.indirectAmount = indirectAmount;
    }

    /**
     * Gets the historicalIndicator attribute.
     * 
     * @return - Returns the historicalIndicator
     * 
     */
    public boolean isHistoricalIndicator() {
        return historicalIndicator;
    }

    /**
     * Sets the historicalIndicator attribute.
     * 
     * @param historicalIndicator The historicalIndicator to set.
     * 
     */
    public void setHistoricalIndicator(boolean historicalIndicator) {
        this.historicalIndicator = historicalIndicator;
    }

    /**
     * Gets the inStateIndicator attribute.
     * 
     * @return - Returns the inStateIndicator
     * 
     */
    public boolean isInStateIndicator() {
        return inStateIndicator;
    }

    /**
     * Sets the inStateIndicator attribute.
     * 
     * @param inStateIndicator The inStateIndicator to set.
     * 
     */
    public void setInStateIndicator(boolean inStateIndicator) {
        this.inStateIndicator = inStateIndicator;
    }

    /**
     * Gets the reportsToAgency attribute.
     * 
     * @return - Returns the reportsToAgency
     * 
     */
    public Agency getReportsToAgency() {
        return reportsToAgency;
    }

    /**
     * Sets the reportsToAgency attribute.
     * 
     * @param reportsToAgency The reportsToAgency to set.
     * @deprecated
     */
    public void setReportsToAgency(Agency reportsToAgencyNumber) {
        this.reportsToAgency = reportsToAgencyNumber;
    }

    /**
     * Gets the agencyType attribute.
     * 
     * @return - Returns the agencyType
     * 
     */
    public AgencyType getAgencyType() {
        return agencyType;
    }

    /**
     * Sets the agencyType attribute.
     * 
     * @param agencyType The agencyType to set.
     * @deprecated
     */
    public void setAgencyType(AgencyType agencyType) {
        this.agencyType = agencyType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("agencyNumber", getAgencyNumber());
        return m;
    }

    /**
     * Gets the agencyExtension attribute.
     * 
     * @return Returns the agencyExtension.
     */
    public AgencyExtension getAgencyExtension() {
        return agencyExtension;
    }

    /**
     * Sets the agencyExtension attribute value.
     * 
     * @param agencyExtension The agencyExtension to set.
     */
    public void setAgencyExtension(AgencyExtension agencyExtension) {
        this.agencyExtension = agencyExtension;
    }
}
