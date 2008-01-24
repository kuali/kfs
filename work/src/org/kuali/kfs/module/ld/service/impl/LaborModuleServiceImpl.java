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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.kfs.bo.LaborLedgerEntry;
import org.kuali.kfs.bo.LaborLedgerObject;
import org.kuali.kfs.bo.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.service.LaborModuleService;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborLedgerEntryService;

/**
 * This implements the service methods that may be used by outside of labor module
 */
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    private LaborLedgerEntryService laborLedgerEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    private Class<? extends LaborLedgerBalance> laborLedgerBalanceClass;
    private Class<? extends LaborLedgerEntry> laborLedgerEntryClass;
    private Class<? extends LaborLedgerObject> laborLedgerObjectClass;
    private Class<? extends LaborLedgerPositionObjectGroup> laborLedgerPositionObjectGroupClass;

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#createLaborLedgerBalance()
     */
    public LaborLedgerBalance createLaborLedgerBalance() {
        return this.createLaborBusinessObject(laborLedgerBalanceClass);
    }

    /**
     * @see org.kuali.module.effort.service.LaborModuleService#createLaborLedgerObject()
     */
    public LaborLedgerObject createLaborLedgerObject() {
        return this.createLaborBusinessObject(laborLedgerObjectClass);
    }

    /**
     * @see org.kuali.module.effort.service.LaborModuleService#createLaborLedgerPositionObjectGroup()
     */
    public LaborLedgerPositionObjectGroup createLaborLedgerPositionObjectGroup() {
        return this.createLaborBusinessObject(laborLedgerPositionObjectGroupClass);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findEmployeesWithPayType(java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return laborLedgerEntryService.findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findLedgerBalances(java.util.Map, java.util.Map,
     *      java.util.Set, java.util.List, java.util.List)
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>(); 
        
        Collection<LedgerBalance> ledgerBalances = laborLedgerBalanceService.findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for(LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    /**
     * create an object of the specified type
     * 
     * @param clazz the specified type of the object
     * @return an object of the specified type
     */
    private <T> T createLaborBusinessObject(Class<T> clazz) {
        T businessObject = null;

        try {
            businessObject = clazz.newInstance();
        }
        catch (InstantiationException e) {
            LOG.error(e);
        }
        catch (IllegalAccessException e) {
            LOG.error(e);
        }

        return businessObject;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerObjectClass()
     */
    public Class<? extends LaborLedgerObject> getLaborLedgerObjectClass() {
        return laborLedgerObjectClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerPositionObjectGroupClass()
     */
    public Class<? extends LaborLedgerPositionObjectGroup> getLaborLedgerPositionObjectGroupClass() {
        return this.laborLedgerPositionObjectGroupClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerBalanceClass()
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceClass() {
        return this.laborLedgerBalanceClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerEntryClass()
     */
    public Class<? extends LaborLedgerEntry> getLaborLedgerEntryClass() {
        return this.laborLedgerEntryClass;
    }

    /**
     * Sets the laborLedgerEntryService attribute value.
     * 
     * @param laborLedgerEntryService The laborLedgerEntryService to set.
     */
    public void setLaborLedgerEntryService(LaborLedgerEntryService laborLedgerEntryService) {
        this.laborLedgerEntryService = laborLedgerEntryService;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the laborLedgerObjectClass attribute value.
     * 
     * @param laborLedgerObjectClass The laborLedgerObjectClass to set.
     */
    public void setLaborLedgerObjectClass(Class<? extends LaborLedgerObject> laborLedgerObjectClass) {
        this.laborLedgerObjectClass = laborLedgerObjectClass;
    }

    /**
     * Sets the laborLedgerPositionObjectGroupClass attribute value.
     * 
     * @param laborLedgerPositionObjectGroupClass The laborLedgerPositionObjectGroupClass to set.
     */
    public void setLaborLedgerPositionObjectGroupClass(Class<? extends LaborLedgerPositionObjectGroup> laborLedgerPositionObjectGroupClass) {
        this.laborLedgerPositionObjectGroupClass = laborLedgerPositionObjectGroupClass;
    }

    /**
     * Sets the laborLedgerBalanceClass attribute value.
     * 
     * @param laborLedgerBalanceClass The laborLedgerBalanceClass to set.
     */
    public void setLaborLedgerBalanceClass(Class<? extends LaborLedgerBalance> laborLedgerBalanceClass) {
        this.laborLedgerBalanceClass = laborLedgerBalanceClass;
    }

    /**
     * Sets the laborLedgerEntryClass attribute value.
     * 
     * @param laborLedgerEntryClass The laborLedgerEntryClass to set.
     */
    public void setLaborLedgerEntryClass(Class<? extends LaborLedgerEntry> laborLedgerEntryClass) {
        this.laborLedgerEntryClass = laborLedgerEntryClass;
    }
}
