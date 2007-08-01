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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.dao.LaborBaseFundsDao;
import org.kuali.module.labor.service.LaborBaseFundsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LaborBaseFundsServiceImpl implements LaborBaseFundsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBaseFundsServiceImpl.class);

    private LaborBaseFundsDao laborBaseFundsDao;

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findCSFTracker(java.util.Map, boolean)
     */
    public List<CalculatedSalaryFoundationTracker> findCSFTracker(Map fieldValues, boolean isConsolidated) {
        return laborBaseFundsDao.findCSFTrackers(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findCSFTrackersAsAccountStatusBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated) {
        return laborBaseFundsDao.findCSFTrackersAsAccountStatusBaseFunds(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findLaborBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findLaborBaseFunds(Map fieldValues, boolean isConsolidated) {
        return laborBaseFundsDao.findLaborBaseFunds(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBaseFundsService#findAccountStatusBaseFundsAndCSFTracker(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findAccountStatusBaseFundsWithCSFTracker(Map fieldValues, boolean isConsolidated) {
        List<AccountStatusBaseFunds> baseFundsCollection = this.findLaborBaseFunds(fieldValues, isConsolidated);
        List<AccountStatusBaseFunds> CSFTrackersCollection = this.findCSFTrackersAsAccountStatusBaseFunds(fieldValues, isConsolidated);

        for (AccountStatusBaseFunds CSFTracker : CSFTrackersCollection) {
            if(baseFundsCollection.contains(CSFTracker)){
                int index = baseFundsCollection.indexOf(CSFTracker);
                baseFundsCollection.get(index).setCsfAmount(CSFTracker.getCsfAmount());
            }
            else{
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
}
