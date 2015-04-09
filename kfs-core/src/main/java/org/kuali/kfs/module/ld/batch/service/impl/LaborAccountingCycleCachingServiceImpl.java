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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.kuali.kfs.gl.batch.service.impl.AccountingCycleCachingServiceImpl;
import org.kuali.kfs.module.ld.batch.dataaccess.LedgerPreparedStatementCachingDao;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;

public class LaborAccountingCycleCachingServiceImpl extends AccountingCycleCachingServiceImpl implements LaborAccountingCycleCachingService {
    protected LedgerPreparedStatementCachingDao laborLedgerDao;
    
    public void initialize() {
        super.initialize();
        laborLedgerDao.initialize();
        previousValueCache.put(LedgerBalance.class, new PreviousValueReference<LedgerBalance>());
    }

    public LaborObject getLaborObject(final Integer fiscalYear, final String chartCode, final String objectCode) {
        return new ReferenceValueRetriever<LaborObject>() {
            @Override
            protected LaborObject useDao() {
                return laborLedgerDao.getLaborObject(fiscalYear, chartCode, objectCode);
            }
            @Override
            protected void retrieveReferences(LaborObject laborObject) {}
        }.get(LaborObject.class, fiscalYear, chartCode, objectCode);
    }

    public int getMaxLaborSequenceNumber(LedgerEntry t) {
        return laborLedgerDao.getMaxLaborSequenceNumber(t);
    }

    public LedgerBalance getLedgerBalance(final LedgerBalance ledgerBalance) {
        return new PreviousValueRetriever<LedgerBalance>() {
            @Override
            protected LedgerBalance useDao() {
                return laborLedgerDao.getLedgerBalance(ledgerBalance);
            }            
        }.get(LedgerBalance.class, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void insertLedgerBalance(LedgerBalance ledgerBalance) {
        laborLedgerDao.insertLedgerBalance(ledgerBalance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(LedgerBalance.class).update(ledgerBalance, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void updateLedgerBalance(LedgerBalance ledgerBalance) {
        laborLedgerDao.updateLedgerBalance(ledgerBalance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(LedgerBalance.class).update(ledgerBalance, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void insertLedgerEntry(LedgerEntry ledgerEntry) {
        laborLedgerDao.insertLedgerEntry(ledgerEntry);
    }

    public void setLaborLedgerDao(LedgerPreparedStatementCachingDao laborLedgerDao) {
        this.laborLedgerDao = laborLedgerDao;
    }
}
