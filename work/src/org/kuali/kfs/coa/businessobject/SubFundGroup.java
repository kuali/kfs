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
public class SubFundGroup extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public SubFundGroup() {

    }

    private static final long serialVersionUID = 3304324942061886270L;

    private String subFundGroupCode;
    private String subFundGroupDescription;
    private boolean subfundgrpActivityIndicator;
    private String subFundGroupTypeCode;
    private String financialReportingSortCode;
    private boolean subFundGroupWagesIndicator;
    private String fundGroupCode;
    private String fundGroupBudgetAdjustmentRestrictionLevelCode;
    private String accountRestrictedStatusCode;
    
    private FundGroup fundGroup;
    private SubFundGroupType subFundGroupType;
    
    /**
     * Gets the subFundGroupCode attribute.
     * 
     * @return - Returns the subFundGroupCode
     * 
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode attribute.
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    /**
     * Gets the subFundGroupDescription attribute.
     * 
     * @return - Returns the subFundGroupDescription
     * 
     */
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    /**
     * Sets the subFundGroupDescription attribute.
     * 
     * @param subFundGroupDescription The subFundGroupDescription to set.
     * 
     */
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    /**
     * Gets the _SubfundgrpActivityIndicator_ attribute.
     * 
     * @return - Returns the _SubfundgrpActivityIndicator_
     * 
     */
    public boolean getSubfundgrpActivityIndicator() {
        return subfundgrpActivityIndicator;
    }

    /**
     * Sets the _SubfundgrpActivityIndicator_ attribute.
     * 
     * @param _SubfundgrpActivityIndicator_ The _SubfundgrpActivityIndicator_ to set.
     * 
     */
    public void setSubfundgrpActivityIndicator(boolean _SubfundgrpActivityIndicator_) {
        this.subfundgrpActivityIndicator = _SubfundgrpActivityIndicator_;
    }

    /**
     * Gets the subFundGroupTypeCode attribute.
     * 
     * @return - Returns the subFundGroupTypeCode
     * 
     */
    public String getSubFundGroupTypeCode() {
        return subFundGroupTypeCode;
    }

    /**
     * Sets the subFundGroupTypeCode attribute.
     * 
     * @param subFundGroupTypeCode The subFundGroupTypeCode to set.
     * 
     */
    public void setSubFundGroupTypeCode(String subFundGroupTypeCode) {
        this.subFundGroupTypeCode = subFundGroupTypeCode;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     * 
     * @return - Returns the financialReportingSortCode
     * 
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     * 
     * @param financialReportingSortCode The financialReportingSortCode to set.
     * 
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
    }

    /**
     * Gets the subFundGroupWagesIndicator attribute.
     * 
     * @return - Returns the subFundGroupWagesIndicator
     * 
     */
    public boolean isSubFundGroupWagesIndicator() {
        return subFundGroupWagesIndicator;
    }

    /**
     * Sets the subFundGroupWagesIndicator attribute.
     * 
     * @param subFundGroupWagesIndicator The subFundGroupWagesIndicator to set.
     * 
     */
    public void setSubFundGroupWagesIndicator(boolean subFundGroupWagesIndicator) {
        this.subFundGroupWagesIndicator = subFundGroupWagesIndicator;
    }

    /**
     * @return Returns the fundGroup.
     */
    public FundGroup getFundGroup() {
        return fundGroup;
    }


    /**
     * @param fundGroup The fundGroup to set.
     */
    public void setFundGroup(FundGroup fundGroup) {
        this.fundGroup = fundGroup;
    }


    /**
     * @return Returns the fundGroupCode.
     */
    public String getFundGroupCode() {
        return fundGroupCode;
    }

    /**
     * @param fundGroupCode The fundGroupCode to set.
     */
    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    /**
     * Gets the fundGroupBudgetAdjustmentRestrictionLevelCode attribute. 
     * @return Returns the fundGroupBudgetAdjustmentRestrictionLevelCode.
     */
    public String getFundGroupBudgetAdjustmentRestrictionLevelCode() {
        return fundGroupBudgetAdjustmentRestrictionLevelCode;
    }

    /**
     * Sets the fundGroupBudgetAdjustmentRestrictionLevelCode attribute value.
     * @param fundGroupBudgetAdjustmentRestrictionLevelCode The fundGroupBudgetAdjustmentRestrictionLevelCode to set.
     */
    public void setFundGroupBudgetAdjustmentRestrictionLevelCode(String fundGroupBudgetAdjustmentRestrictionLevelCode) {
        this.fundGroupBudgetAdjustmentRestrictionLevelCode = fundGroupBudgetAdjustmentRestrictionLevelCode;
    }

    /**
     * Gets the accountRestrictedStatusCode attribute. 
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * Sets the accountRestrictedStatusCode attribute value.
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * Gets the subFundGroupType attribute. 
     * @return Returns the subFundGroupType.
     */
    public SubFundGroupType getSubFundGroupType() {
        return subFundGroupType;
    }

    /**
     * Sets the subFundGroupType attribute value.
     * @param subFundGroupType The subFundGroupType to set.
     * @deprecated
     */
    public void setSubFundGroupType(SubFundGroupType subFundGroupType) {
        this.subFundGroupType = subFundGroupType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("subFundGroupCode", this.subFundGroupCode);


        return m;
    }    
    
}