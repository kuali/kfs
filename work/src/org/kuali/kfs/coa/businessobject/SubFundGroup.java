/*
 * Copyright 2005 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class SubFundGroup extends PersistableBusinessObjectBase implements MutableInactivatable {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "SubFundGroup";

    /**
     * Default no-arg constructor.
     */
    public SubFundGroup() {

    }

    private static final long serialVersionUID = 3304324942061886270L;

    private String subFundGroupCode;
    private String subFundGroupDescription;
    private boolean active;
    private String subFundGroupTypeCode;
    private String financialReportingSortCode;
    private boolean subFundGroupWagesIndicator;
    private String fundGroupCode;
    private String fundGroupBudgetAdjustmentRestrictionLevelCode;
    private String accountRestrictedStatusCode;

    private FundGroup fundGroup;
    private SubFundGroupType subFundGroupType;
    private RestrictedStatus accountRestrictedStatus;

    /**
     * Gets the subFundGroupCode attribute.
     * 
     * @return Returns the subFundGroupCode
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
     * @return Returns the subFundGroupDescription
     */
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    /**
     * Sets the subFundGroupDescription attribute.
     * 
     * @param subFundGroupDescription The subFundGroupDescription to set.
     */
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    /**
     * Gets the _active_ attribute.
     * 
     * @return Returns the _active_
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the _active_ attribute.
     * 
     * @param _active_ The _active_ to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the subFundGroupTypeCode attribute.
     * 
     * @return Returns the subFundGroupTypeCode
     */
    public String getSubFundGroupTypeCode() {
        return subFundGroupTypeCode;
    }

    /**
     * Sets the subFundGroupTypeCode attribute.
     * 
     * @param subFundGroupTypeCode The subFundGroupTypeCode to set.
     */
    public void setSubFundGroupTypeCode(String subFundGroupTypeCode) {
        this.subFundGroupTypeCode = subFundGroupTypeCode;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     * 
     * @return Returns the financialReportingSortCode
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     * 
     * @param financialReportingSortCode The financialReportingSortCode to set.
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
    }

    /**
     * Gets the subFundGroupWagesIndicator attribute.
     * 
     * @return Returns the subFundGroupWagesIndicator
     */
    public boolean isSubFundGroupWagesIndicator() {
        return subFundGroupWagesIndicator;
    }

    /**
     * Sets the subFundGroupWagesIndicator attribute.
     * 
     * @param subFundGroupWagesIndicator The subFundGroupWagesIndicator to set.
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
     * 
     * @return Returns the fundGroupBudgetAdjustmentRestrictionLevelCode.
     */
    public String getFundGroupBudgetAdjustmentRestrictionLevelCode() {
        return fundGroupBudgetAdjustmentRestrictionLevelCode;
    }

    /**
     * Sets the fundGroupBudgetAdjustmentRestrictionLevelCode attribute value.
     * 
     * @param fundGroupBudgetAdjustmentRestrictionLevelCode The fundGroupBudgetAdjustmentRestrictionLevelCode to set.
     */
    public void setFundGroupBudgetAdjustmentRestrictionLevelCode(String fundGroupBudgetAdjustmentRestrictionLevelCode) {
        this.fundGroupBudgetAdjustmentRestrictionLevelCode = fundGroupBudgetAdjustmentRestrictionLevelCode;
    }

    /**
     * Gets the accountRestrictedStatusCode attribute.
     * 
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * Sets the accountRestrictedStatusCode attribute value.
     * 
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * Gets the subFundGroupType attribute.
     * 
     * @return Returns the subFundGroupType.
     */
    public SubFundGroupType getSubFundGroupType() {
        return subFundGroupType;
    }

    /**
     * Sets the subFundGroupType attribute value.
     * 
     * @param subFundGroupType The subFundGroupType to set.
     * @deprecated
     */
    public void setSubFundGroupType(SubFundGroupType subFundGroupType) {
        this.subFundGroupType = subFundGroupType;
    }


    /**
     * Gets the accountRestrictedStatus attribute.
     * 
     * @return Returns the accountRestrictedStatus.
     */
    public RestrictedStatus getAccountRestrictedStatus() {
        return accountRestrictedStatus;
    }

    /**
     * Sets the accountRestrictedStatus attribute value.
     * 
     * @param accountRestrictedStatus The accountRestrictedStatus to set.
     */
    public void setAccountRestrictedStatus(RestrictedStatus accountRestrictedStatus) {
        this.accountRestrictedStatus = accountRestrictedStatus;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getSubFundGroupCode() + " - " + getSubFundGroupDescription();
        return theString;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("subFundGroupCode", this.subFundGroupCode);


        return m;
    }

}
