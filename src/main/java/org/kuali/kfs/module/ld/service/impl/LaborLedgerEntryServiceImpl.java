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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao;
import org.kuali.kfs.module.ld.service.LaborLedgerEntryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements LaborLedgerEntryService to provide the access to labor ledger entries in data stores.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry
 */
@Transactional
public class LaborLedgerEntryServiceImpl implements LaborLedgerEntryService {

    private LaborLedgerEntryDao laborLedgerEntryDao;
    private LaborAccountingCycleCachingService laborAccountingCycleCachingService;
    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#save(org.kuali.kfs.module.ld.businessobject.LedgerEntry)
     */
    public void save(LedgerEntry ledgerEntry) {
        laborAccountingCycleCachingService.insertLedgerEntry(ledgerEntry);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#getMaxSquenceNumber(org.kuali.kfs.module.ld.businessobject.LedgerEntry)
     */
    public Integer getMaxSequenceNumber(LedgerEntry ledgerEntry) {
        return laborAccountingCycleCachingService.getMaxLaborSequenceNumber(ledgerEntry);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#find(java.util.Map)
     */
    public Iterator<LedgerEntry> find(Map<String, String> fieldValues) {
        return laborLedgerEntryDao.find(fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#findEmployeesWithPayType(java.util.Map, java.util.List, java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return laborLedgerEntryDao.findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }
    
    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#isEmployeeWithPayType(java.lang.String, java.util.Map, java.util.List, java.util.Map)
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return laborLedgerEntryDao.isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }
    
    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerEntryService#deleteLedgerEntriesPriorToYear(java.lang.Integer, java.lang.String)
     */
    public void deleteLedgerEntriesPriorToYear(Integer fiscalYear, String chartOfAccountsCode) {
        laborLedgerEntryDao.deleteLedgerEntriesPriorToYear(fiscalYear, chartOfAccountsCode);       
    }
    
    /**
     * Sets the laborLedgerEntryDao attribute value.
     * 
     * @param laborLedgerEntryDao The laborLedgerEntryDao to set.
     */
    public void setLaborLedgerEntryDao(LaborLedgerEntryDao laborLedgerEntryDao) {
        this.laborLedgerEntryDao = laborLedgerEntryDao;
    }

    public void setLaborAccountingCycleCachingService(LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
    }
}
