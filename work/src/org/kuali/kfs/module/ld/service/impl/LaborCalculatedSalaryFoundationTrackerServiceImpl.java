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
package org.kuali.module.labor.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.dao.LaborCalculatedSalaryFoundationTrackerDao;
import org.kuali.module.labor.service.LaborCalculatedSalaryFoundationTrackerService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LaborCalculatedSalaryFoundationTrackerServiceImpl implements LaborCalculatedSalaryFoundationTrackerService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerServiceImpl.class);

    private LaborCalculatedSalaryFoundationTrackerDao laborCalculatedSalaryFoundationTrackerDao;

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findCSFTracker(java.util.Map, boolean)
     */
    public List<CalculatedSalaryFoundationTracker> findCSFTracker(Map fieldValues, boolean isConsolidated) {
        return laborCalculatedSalaryFoundationTrackerDao.findCSFTrackers(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findCSFTrackersAsAccountStatusBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated) {
        return laborCalculatedSalaryFoundationTrackerDao.findCSFTrackersAsAccountStatusBaseFunds(fieldValues, isConsolidated);
    }

    /**
     * Sets the laborCalculatedSalaryFoundationTrackerDao attribute value.
     * 
     * @param laborCalculatedSalaryFoundationTrackerDao The laborCalculatedSalaryFoundationTrackerDao to set.
     */
    public void setLaborCalculatedSalaryFoundationTrackerDao(LaborCalculatedSalaryFoundationTrackerDao laborCalculatedSalaryFoundationTrackerDao) {
        this.laborCalculatedSalaryFoundationTrackerDao = laborCalculatedSalaryFoundationTrackerDao;
    }
}
