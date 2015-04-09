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
