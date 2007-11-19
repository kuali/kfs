/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao;

import java.util.List;
import java.util.Map;

import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.LaborCalculatedSalaryFoundationTracker;

/**
 * This is the data access object for calculated salary foundation tracker
 * 
 * @see org.kuali.module.labor.bo.CalculatedSalaryFoundationTracker
 */
public interface LaborCalculatedSalaryFoundationTrackerDao {

    /**
     * This method finds the CSF trackers according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of CSF trackers
     */
    List<LaborCalculatedSalaryFoundationTracker> findCSFTrackers(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the CSF trackers according to input fields and values and converts the trackers into AccountStatusBaseFunds
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of base budgets for Labor objects
     */
    List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the CSF trackers according to input fields and values and converts the trackers into EmployeeFunding
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of CSF trackers as EmployeeFunding
     */
    List<EmployeeFunding> findCSFTrackersAsEmployeeFunding(Map fieldValues, boolean isConsolidated);
}
