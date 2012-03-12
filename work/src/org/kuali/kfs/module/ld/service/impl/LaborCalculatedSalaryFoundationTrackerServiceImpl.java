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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.July1PositionFunding;
import org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.ld.dataaccess.LaborCalculatedSalaryFoundationTrackerDao;
import org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.krad.service.LookupService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides its clients with access to CSF tracker entries in the backend data store.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker
 */
@Transactional
public class LaborCalculatedSalaryFoundationTrackerServiceImpl implements LaborCalculatedSalaryFoundationTrackerService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerServiceImpl.class);

    private LaborCalculatedSalaryFoundationTrackerDao laborCalculatedSalaryFoundationTrackerDao;
    private LookupService lookupService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBaseFundsService#findCSFTracker(java.util.Map, boolean)
     */
    public List<LaborCalculatedSalaryFoundationTracker> findCSFTracker(Map fieldValues, boolean isConsolidated) {
        LOG.debug("start findCSFTracker()");
        return laborCalculatedSalaryFoundationTrackerDao.findCSFTrackers(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService#findCSFTrackerWithJuly1(java.util.Map,
     *      boolean)
     */
    public List<LaborCalculatedSalaryFoundationTracker> findCSFTrackerWithJuly1(Map fieldValues, boolean isConsolidated) {
        LOG.debug("start findCSFTrackerWithJuly1()");

        List<LaborCalculatedSalaryFoundationTracker> CSFTrackerCollection = this.findCSFTracker(fieldValues, isConsolidated);
        Collection<July1PositionFunding> july1PositionFundings = lookupService.findCollectionBySearch(July1PositionFunding.class, fieldValues);
        for (July1PositionFunding july1PositionFunding : july1PositionFundings) {
            LaborCalculatedSalaryFoundationTracker CSFTracker = this.findCSFTracker(CSFTrackerCollection, july1PositionFunding);

            if (CSFTracker != null) {
                CSFTracker.setJuly1BudgetAmount(CSFTracker.getJuly1BudgetAmount().add(july1PositionFunding.getJuly1BudgetAmount()));
                CSFTracker.setJuly1BudgetFteQuantity(CSFTracker.getJuly1BudgetFteQuantity().add(july1PositionFunding.getJuly1BudgetFteQuantity()));
                CSFTracker.setJuly1BudgetTimePercent(CSFTracker.getJuly1BudgetTimePercent().add(july1PositionFunding.getJuly1BudgetTimePercent()));
            }
            else {
                CSFTracker = new LaborCalculatedSalaryFoundationTracker();
                ObjectUtil.buildObject(CSFTracker, july1PositionFunding);
                CSFTrackerCollection.add(CSFTracker);
            }
        }
        return CSFTrackerCollection;
    }

    /**
     * Check if there is a CSF track in the given set that matches the given object
     * 
     * @param csfTrackerCollection the given set of CSF trackers
     * @param anotherObject the object to be searched
     * @return the CSF tracker if there is a CSF track in the given set that matches the given object
     */
    protected LaborCalculatedSalaryFoundationTracker findCSFTracker(List<LaborCalculatedSalaryFoundationTracker> CSFTrackerCollection, Object anotherObject) {
        for (LaborCalculatedSalaryFoundationTracker CSFTracker : CSFTrackerCollection) {
            boolean found = ObjectUtil.equals(CSFTracker, anotherObject, CSFTracker.getKeyFieldList());
            if (found) {
                return CSFTracker;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBaseFundsService#findCSFTrackersAsAccountStatusBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated) {
        LOG.debug("start findCSFTrackersAsAccountStatusBaseFunds()");
        return laborCalculatedSalaryFoundationTrackerDao.findCSFTrackersAsAccountStatusBaseFunds(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService#findCSFTrackersAsEmployeeFunding(java.util.Map,
     *      boolean)
     */
    public List<EmployeeFunding> findCSFTrackersAsEmployeeFunding(Map fieldValues, boolean isConsolidated) {
        LOG.debug("start findCSFTrackersAsEmployeeFunding()");
        return laborCalculatedSalaryFoundationTrackerDao.findCSFTrackersAsEmployeeFunding(fieldValues, isConsolidated);
    }

    /**
     * Sets the laborCalculatedSalaryFoundationTrackerDao attribute value.
     * 
     * @param laborCalculatedSalaryFoundationTrackerDao The laborCalculatedSalaryFoundationTrackerDao to set.
     */
    public void setLaborCalculatedSalaryFoundationTrackerDao(LaborCalculatedSalaryFoundationTrackerDao laborCalculatedSalaryFoundationTrackerDao) {
        this.laborCalculatedSalaryFoundationTrackerDao = laborCalculatedSalaryFoundationTrackerDao;
    }

    /**
     * Sets the lookupService attribute value.
     * 
     * @param lookupService The lookupService to set.
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }
}
