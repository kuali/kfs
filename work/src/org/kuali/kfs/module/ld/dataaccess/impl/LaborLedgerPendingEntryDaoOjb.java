/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerPendingEntryDao;
import org.kuali.kfs.sys.dataaccess.impl.GeneralLedgerPendingEntryDaoOjb;

/**
 * OJB Implementation of LaborLedgerPendingEntryDao.
 */
public class LaborLedgerPendingEntryDaoOjb extends GeneralLedgerPendingEntryDaoOjb implements LaborLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryDaoOjb.class);
    protected final static String FINANCIAL_DOCUMENT_APPROVED_CODE = "financialDocumentApprovedCode";

    /**
     * @see org.kuali.kfs.sys.dataaccess.impl.GeneralLedgerPendingEntryDaoOjb#getEntryClass()
     */
    @Override
    public Class getEntryClass() {
        return LaborLedgerPendingEntry.class;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerPendingEntryDao#findPendingLedgerEntriesForLedgerBalance(java.util.Map,
     *      boolean)
     */
    public Iterator<LaborLedgerPendingEntry> findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved, String currentFYPeriodCode, int currentFY, List<String> encumbranceBalanceTypes) {
        return this.findPendingEntries(fieldValues, isApproved, currentFYPeriodCode, currentFY, encumbranceBalanceTypes).iterator();
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerPendingEntryDao#hasPendingLaborLedgerEntry(java.util.Map,
     *      java.lang.Object)
     */
    public Collection<LaborLedgerPendingEntry> hasPendingLaborLedgerEntry(Map fieldValues, Object businessObject) {
        LOG.debug("hasPendingLaborLedgerEntry() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (element.equals("documentNumber")) {
                criteria.addNotEqualTo(element, fieldValues.get(element));
            }
            else {
                criteria.addEqualTo(element, fieldValues.get(element));
            }
        }

        QueryByCriteria qbc = QueryFactory.newQuery(getEntryClass(), criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.sys.dataaccess.impl.GeneralLedgerPendingEntryDaoOjb#deleteByFinancialDocumentApprovedCode(java.lang.String)
     */
    @Override
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(FINANCIAL_DOCUMENT_APPROVED_CODE, financialDocumentApprovedCode);

        QueryByCriteria qbc = QueryFactory.newQuery(this.getEntryClass(), criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);
        getPersistenceBrokerTemplate().clearCache();
    }


    /**
     * @see org.kuali.kfs.sys.dataaccess.impl.GeneralLedgerPendingEntryDaoOjb#findPendingEntries(java.util.Map, boolean,
     *      java.lang.String, int)
     */
    @Override
    public Collection findPendingEntries(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingEntries(Map, boolean) started");

        Collection<LaborLedgerPendingEntry> pendingEntries = super.findPendingEntries(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);

        for (LaborLedgerPendingEntry pendingEntry : pendingEntries) {

            String periodCode = pendingEntry.getUniversityFiscalPeriodCode();
            if (StringUtils.isEmpty(periodCode)) {
                pendingEntry.setUniversityFiscalPeriodCode(currentFiscalPeriodCode);
            }

            Integer fiscalYear = pendingEntry.getUniversityFiscalYear();
            if (fiscalYear == null || StringUtils.isEmpty(fiscalYear.toString())) {
                pendingEntry.setUniversityFiscalYear(currentFiscalYear);
            }
        }

        return pendingEntries;
    }
}
