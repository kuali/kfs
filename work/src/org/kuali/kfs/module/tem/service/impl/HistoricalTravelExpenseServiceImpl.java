/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao;
import org.kuali.kfs.module.tem.service.HistoricalTravelExpenseService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HistoricalTravelExpenseServiceImpl implements HistoricalTravelExpenseService {

    private HistoricalTravelExpenseDao historicalTravelExpenseDao;

    /**
     * @see org.kuali.kfs.module.tem.service.HistoricalTravelExpenseService#getImportedExpesnesToBeNotified()
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified() {
        return this.getHistoricalTravelExpenseDao().getImportedExpesnesToBeNotified();
    }

    /**
     * @see org.kuali.kfs.module.tem.service.HistoricalTravelExpenseService#getImportedExpesnesToBeNotified(java.lang.Integer)
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified(Integer travelerProfileId) {
        return this.getHistoricalTravelExpenseDao().getImportedExpesnesToBeNotified(travelerProfileId);
    }

    /**
     * Gets the historicalTravelExpenseDao attribute.
     * @return Returns the historicalTravelExpenseDao.
     */
    public HistoricalTravelExpenseDao getHistoricalTravelExpenseDao() {
        return historicalTravelExpenseDao;
    }

    /**
     * Sets the historicalTravelExpenseDao attribute value.
     * @param historicalTravelExpenseDao The historicalTravelExpenseDao to set.
     */
    public void setHistoricalTravelExpenseDao(HistoricalTravelExpenseDao historicalTravelExpenseDao) {
        this.historicalTravelExpenseDao = historicalTravelExpenseDao;
    }

}
