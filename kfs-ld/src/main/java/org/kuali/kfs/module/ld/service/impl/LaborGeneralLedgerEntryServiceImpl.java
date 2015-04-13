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

import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborDao;
import org.kuali.kfs.module.ld.dataaccess.LaborGeneralLedgerEntryDao;
import org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements LaborGeneralLedgerEntryService to provide the access to labor general ledger entries in data stores.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry
 */
@Transactional
public class LaborGeneralLedgerEntryServiceImpl implements LaborGeneralLedgerEntryService {

    private LaborGeneralLedgerEntryDao laborGeneralLedgerEntryDao;
    private LaborDao laborDao;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService#getMaxSequenceNumber()
     */
    public Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry) {
        return laborGeneralLedgerEntryDao.getMaxSequenceNumber(laborGeneralLedgerEntry);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborGeneralLedgerEntryService#save(org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry)
     */
    public void save(LaborGeneralLedgerEntry laborGeneralLedgerEntry) {
        laborDao.insert(laborGeneralLedgerEntry);
    }

    /**
     * Sets the laborGeneralLedgerEntryDao attribute value.
     * 
     * @param laborGeneralLedgerEntryDao The laborGeneralLedgerEntryDao to set.
     */
    public void setLaborGeneralLedgerEntryDao(LaborGeneralLedgerEntryDao laborGeneralLedgerEntryDao) {
        this.laborGeneralLedgerEntryDao = laborGeneralLedgerEntryDao;
    }

    /**
     * Sets the laborDao attribute value.
     * 
     * @param laborDao The laborDao to set.
     */
    public void setLaborDao(LaborDao laborDao) {
        this.laborDao = laborDao;
    }

}
