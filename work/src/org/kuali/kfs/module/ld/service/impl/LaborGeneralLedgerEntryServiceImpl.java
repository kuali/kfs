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
