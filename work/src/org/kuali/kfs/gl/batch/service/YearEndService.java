/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.batch.closing.year.service;

import java.util.Map;

import org.kuali.module.gl.batch.closing.year.service.impl.helper.BalanceForwardRuleHelper;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.OriginEntryGroup;


/**
 */

public interface YearEndService {
    /**
     * 
     * Year End Forward Encumbrances Job
     */
    public void forwardEncumbrances(OriginEntryGroup originEntryGroup, Map jobParameters, Map<String, Integer> forwardEncumbrancesCounts);
    
    /**
     * 
     * This method generates the report for the forward encumbrances job
     * @param originEntryGroup
     * @param jobParameters
     * @param forwardEncumbrancesCounts
     */
    public void generateForwardEncumbrancesReports(OriginEntryGroup originEntryGroup, Map jobParameters, Map<String, Integer> forwardEncumbrancesCounts);

    /**
     * 
     * Year End Forward Balances Job
     */
    public void forwardBalances(OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup, OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup, BalanceForwardRuleHelper balanceForwardRuleHelper);
    
    /**
     * 
     * This method generates the report for a forward balances job
     * @param balanceForwardsUnclosedPriorYearAccountGroup
     * @param balanceForwardsClosedPriorYearAccountGroup
     * @param balanceForwardRuleHelper
     */
    public void generateForwardBalanceReports(OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup, OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup, BalanceForwardRuleHelper balanceForwardRuleHelper);

    /**
     * 
     * Year End Close Nominal Activity Job
     */
    public void closeNominalActivity(OriginEntryGroup nominalClosingOriginEntryGroup, Map nominalClosingJobParameters, Map<String, Integer> nominalClosingCounts);
    
    /**
     * 
     * This method determines if a given balance should be selected to be processed by the close nominal activity job
     * @param balance a balance - an every day, quiet balance
     * @return ture to process, false if not
     */
    public boolean selectBalanceForClosingOfNominalActivity(Balance balance);
    
    /**
     * 
     * This method generates the report for a nominal activity closing job
     * @param nominalClosingOriginEntryGroup
     * @param nominalClosingJobParameters
     * @param nominalClosingCounts
     */
    public void generateCloseNominalActivityReports(OriginEntryGroup nominalClosingOriginEntryGroup, Map nominalClosingJobParameters, Map<String, Integer> nominalClosingCounts);
}
