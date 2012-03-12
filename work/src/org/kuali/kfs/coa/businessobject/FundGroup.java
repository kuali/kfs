/*
 * Copyright 2005-2006 The Kuali Foundation
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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Fund Group Business Object
 */
public class FundGroup extends KualiCodeBase implements MutableInactivatable {
    private static final long serialVersionUID = 6940405128416948259L;
    private String financialReportingSortCode;
    private String fundGroupBudgetAdjustmentRestrictionLevelCode;

    /**
     * This method...
     * 
     * @return
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * This method...
     * 
     * @param financialReportingSortCode
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
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
}
