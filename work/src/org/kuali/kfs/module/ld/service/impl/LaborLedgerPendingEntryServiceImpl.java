/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.labor.dao.LaborLedgerPendingEntryDao;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;

/**
 * This class is the service implementation for the GeneralLedgerPendingEntry structure. This is the default implementation, that is
 * delivered with Kuali.
 * 
 * 
 */
public class LaborLedgerPendingEntryServiceImpl implements LaborLedgerPendingEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryServiceImpl.class);

    private LaborLedgerPendingEntryDao laborLedgerPendingEntryDao;
    private KualiRuleService kualiRuleService;
    private ChartService chartService;
    private OptionsService optionsService;
    private KualiConfigurationService kualiConfigurationService;
    private BalanceTypService balanceTypeService;


    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findApprovedPendingLedgerEntries()
     */
    public Iterator findApprovedPendingLedgerEntries() {
        LOG.debug("findApprovedPendingLedgerEntries() started");

        return laborLedgerPendingEntryDao.findApprovedPendingLedgerEntries();
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(org.kuali.module.gl.bo.Encumbrance,
     *      boolean)
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved) {
        LOG.debug("findPendingLedgerEntries() started");

        return laborLedgerPendingEntryDao.findPendingLedgerEntries(encumbrance, isApproved);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#hasPendingGeneralLedgerEntry(org.kuali.module.chart.bo.Account)
     */
    public boolean hasPendingGeneralLedgerEntry(Account account) {
        LOG.debug("hasPendingGeneralLedgerEntry() started");

        return laborLedgerPendingEntryDao.countPendingLedgerEntries(account) > 0;
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntries(Balance, boolean, boolean)
     */
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated) {
        LOG.debug("findPendingLedgerEntries() started");

        return laborLedgerPendingEntryDao.findPendingLedgerEntries(balance, isApproved, isConsolidated);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForEntry(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEntry() started");

        return laborLedgerPendingEntryDao.findPendingLedgerEntriesForEntry(fieldValues, isApproved);
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries() started");

        return laborLedgerPendingEntryDao.findPendingEntries(fieldValues, isApproved);
    }

    public void setlaborLedgerPendingEntryDao(LaborLedgerPendingEntryDao laborLedgerPendingEntryDao) {
        this.laborLedgerPendingEntryDao = laborLedgerPendingEntryDao;
    }
}