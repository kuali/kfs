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
package org.kuali.kfs.module.ld.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.dataaccess.LaborBaseFundsDao;
import org.kuali.kfs.module.ld.service.LaborBaseFundsService;
import org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * This class provides its clients with access to labor base fund entries in the backend data store.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds
 */

@NonTransactional
public class LaborBaseFundsServiceImpl implements LaborBaseFundsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBaseFundsServiceImpl.class);

    private LaborBaseFundsDao laborBaseFundsDao;
    private LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBaseFundsService#findLaborBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findLaborBaseFunds(Map fieldValues, boolean isConsolidated) {
        return laborBaseFundsDao.findLaborBaseFunds(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBaseFundsService#findAccountStatusBaseFundsAndCSFTracker(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findAccountStatusBaseFundsWithCSFTracker(Map fieldValues, boolean isConsolidated) {
        List<AccountStatusBaseFunds> baseFundsCollection = this.findLaborBaseFunds(fieldValues, isConsolidated);
        List<AccountStatusBaseFunds> CSFTrackersCollection = laborCalculatedSalaryFoundationTrackerService.findCSFTrackersAsAccountStatusBaseFunds(fieldValues, isConsolidated);

        for (AccountStatusBaseFunds CSFTracker : CSFTrackersCollection) {
            if (baseFundsCollection.contains(CSFTracker)) {
                int index = baseFundsCollection.indexOf(CSFTracker);
                baseFundsCollection.get(index).setCsfAmount(CSFTracker.getCsfAmount());
            }
            else {
                baseFundsCollection.add(CSFTracker);
            }
        }
        return baseFundsCollection;
    }

    /**
     * Sets the laborBaseFundsDao attribute value.
     * 
     * @param laborBaseFundsDao The laborBaseFundsDao to set.
     */
    public void setLaborBaseFundsDao(LaborBaseFundsDao laborBaseFundsDao) {
        this.laborBaseFundsDao = laborBaseFundsDao;
    }

    /**
     * Sets the laborCalculatedSalaryFoundationTrackerService attribute value.
     * 
     * @param laborCalculatedSalaryFoundationTrackerService The laborCalculatedSalaryFoundationTrackerService to set.
     */
    public void setLaborCalculatedSalaryFoundationTrackerService(LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService) {
        this.laborCalculatedSalaryFoundationTrackerService = laborCalculatedSalaryFoundationTrackerService;
    }
}
