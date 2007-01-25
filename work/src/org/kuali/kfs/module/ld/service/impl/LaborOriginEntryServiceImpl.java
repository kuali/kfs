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
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.util.LaborLedgerUnitOfWork;
import org.kuali.module.labor.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements LaborOriginEntryService to provide the access to labor origin entries in data stores.
 */
@Transactional
public class LaborOriginEntryServiceImpl implements LaborOriginEntryService {

    private LaborOriginEntryDao laborOriginEntryDao;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group) {
        return laborOriginEntryDao.getEntriesByGroup(group);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroups(java.util.Collection)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups) {
        return laborOriginEntryDao.getEntriesByGroups(groups);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup,
     *      boolean)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group, boolean isConsolidated) {
        if (!isConsolidated) {
            return this.getEntriesByGroup(group);
        }

        Collection<LaborOriginEntry> entryCollection = new ArrayList<LaborOriginEntry>();
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();

        Iterator<Object[]> consolidatedEntries = laborOriginEntryDao.getConsolidatedEntriesByGroup(group);
        while (consolidatedEntries.hasNext()) {
            LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
            Object[] oneEntry = consolidatedEntries.next();
            ObjectUtil.buildObject(laborOriginEntry, oneEntry, LaborConstants.consolidationAttributesOfOriginEntry());

            if (laborLedgerUnitOfWork.canContain(laborOriginEntry)) {
                laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
            }
            else {
                entryCollection.add(laborLedgerUnitOfWork.getWorkingEntry());
                laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
            }
        }
        return entryCollection.iterator();
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the laborOriginEntryDao attribute value.
     * @param laborOriginEntryDao The laborOriginEntryDao to set.
     */
    public void setLaborOriginEntryDao(LaborOriginEntryDao laborOriginEntryDao) {
        this.laborOriginEntryDao = laborOriginEntryDao;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}