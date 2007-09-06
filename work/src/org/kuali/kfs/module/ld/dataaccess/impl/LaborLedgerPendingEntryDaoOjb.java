/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao.ojb;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.dao.ojb.GeneralLedgerPendingEntryDaoOjb;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.dao.LaborLedgerPendingEntryDao;

public class LaborLedgerPendingEntryDaoOjb extends GeneralLedgerPendingEntryDaoOjb implements LaborLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryDaoOjb.class);
    private final static String FINANCIAL_DOCUMENT_APPROVED_CODE = "financialDocumentApprovedCode";
    
    /**
     * @see org.kuali.kfs.dao.ojb.GeneralLedgerPendingEntryDaoOjb#getEntryClass()
     */
    @Override
    public Class getEntryClass() {
        return LaborLedgerPendingEntry.class;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerPendingEntryDao#findPendingLedgerEntriesForLedgerBalance(java.util.Map, boolean)
     */
    public Iterator<LaborLedgerPendingEntry> findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved) {
        return this.findPendingLedgerEntriesForBalance(fieldValues, isApproved);
    }
    
    
    
    public Collection<LaborLedgerPendingEntry> hasPendingLaborLedgerEntry(Map fieldValues, Object businessObject){
        LOG.debug("hasPendingLaborLedgerEntry() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (element.equals("documentNumber")) {
                criteria.addNotEqualTo(element, fieldValues.get(element));
            } else {
                criteria.addEqualTo(element, fieldValues.get(element));
            }
        }

        QueryByCriteria qbc = QueryFactory.newQuery(getEntryClass(), criteria);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(FINANCIAL_DOCUMENT_APPROVED_CODE, financialDocumentApprovedCode);

        QueryByCriteria qbc = QueryFactory.newQuery(this.getEntryClass(), criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);
        getPersistenceBrokerTemplate().clearCache();
    }
}
