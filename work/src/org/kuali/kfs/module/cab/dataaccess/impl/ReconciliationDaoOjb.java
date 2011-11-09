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
package org.kuali.kfs.module.cab.dataaccess.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.dataaccess.ReconciliationDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for ReconciliationService
 * 
 * @see org.kuali.kfs.module.cab.dataaccess.impl.ReconciliationDaoOjb
 */
public class ReconciliationDaoOjb extends PlatformAwareDaoBaseOjb implements ReconciliationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReconciliationDaoOjb.class);

   
    public boolean isDuplicateEntry(Entry glEntry) {
        // find matching entry from CB_GL_ENTRY_T
        Map<String, Object> glKeys = new LinkedHashMap<String, Object>();
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR, glEntry.getUniversityFiscalYear());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE, glEntry.getChartOfAccountsCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER, glEntry.getAccountNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.SUB_ACCOUNT_NUMBER, glEntry.getSubAccountNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE, glEntry.getFinancialObjectCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SUB_OBJECT_CODE, glEntry.getFinancialSubObjectCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_BALANCE_TYPE_CODE, glEntry.getFinancialBalanceTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_TYPE_CODE, glEntry.getFinancialObjectTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE, glEntry.getUniversityFiscalPeriodCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, glEntry.getFinancialDocumentTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SYSTEM_ORIGINATION_CODE, glEntry.getFinancialSystemOriginationCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, glEntry.getDocumentNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, glEntry.getTransactionLedgerEntrySequenceNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.PROJECT_CD, glEntry.getProjectCode());

        Criteria criteria = OJBUtility.buildCriteriaFromMap(glKeys, new GeneralLedgerEntry());
        // is equal to or is null criteria
        Criteria tempCriteria1 = new Criteria();
        Criteria tempCriteria2 = new Criteria();
        tempCriteria1.addEqualTo(CabPropertyConstants.GeneralLedgerEntry.ORGNIZATION_REFERENCE_ID,  glEntry.getOrganizationReferenceId());
        if (StringUtils.isEmpty(glEntry.getOrganizationReferenceId())) {
            tempCriteria2.addIsNull(CabPropertyConstants.GeneralLedgerEntry.ORGNIZATION_REFERENCE_ID);
            tempCriteria1.addOrCriteria(tempCriteria2);
        }
        criteria.addAndCriteria(tempCriteria1);
        Query query = QueryFactory.newQuery(GeneralLedgerEntry.class, criteria);
        Collection<GeneralLedgerEntry> matchingEntries = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        // if not found, return false
        if (matchingEntries == null || matchingEntries.isEmpty()) {
            return false;
        }
        return true;
    }
}
