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
