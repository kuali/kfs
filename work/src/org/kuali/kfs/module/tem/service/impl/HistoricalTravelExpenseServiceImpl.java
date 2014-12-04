/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao;
import org.kuali.kfs.module.tem.service.HistoricalTravelExpenseService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HistoricalTravelExpenseServiceImpl implements HistoricalTravelExpenseService {

    protected HistoricalTravelExpenseDao historicalTravelExpenseDao;

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
