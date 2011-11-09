/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject;

import java.util.Arrays;
import java.util.Collection;

import org.kuali.kfs.integration.ld.SegmentedBusinessObject;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Labor business object for LedgerBalanceForBenefitExpenseTransfer
 */
public class LedgerBalanceForBenefitExpenseTransfer extends LedgerBalance implements SegmentedBusinessObject {

    /**
     * Constructs a LedgerBalanceForBenefitExpenseTransfer.java.
     */
    public LedgerBalanceForBenefitExpenseTransfer() {
        super();
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#isLookupResultsSegmented()
     */
    public boolean isLookupResultsSegmented() {
        return true;
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getSegmentedPropertyNames()
     * @return a collection
     */
    public Collection<String> getSegmentedPropertyNames() {
        return (Collection<String>) Arrays.asList(AccountingPeriodProperties.namesToArray());
    }

    /**
     * Adds the period amount to the return string. Since the return string cannot have string, multiplies the amount by 100 so the
     * decimal places are not lost.
     * 
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getAdditionalReturnData(java.lang.String)
     */
    public String getAdditionalReturnData(String segmentedPropertyName) {
        String periodCode = LaborConstants.periodCodeMapping.get(segmentedPropertyName);
        KualiDecimal periodAmount = getAmount(periodCode);  

        return KRADUtils.convertDecimalIntoInteger(periodAmount);
    }
}
