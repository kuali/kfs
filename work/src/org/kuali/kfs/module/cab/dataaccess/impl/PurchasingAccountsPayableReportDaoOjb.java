/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.dataaccess.PurchasingAccountsPayableReportDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PurchasingAccountsPayableReportDaoOjb extends PlatformAwareDaoBaseOjb implements PurchasingAccountsPayableReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableReportDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.cab.dataaccess.PurchasingAccountsPayableReportDao#findPurchasingAccountsPayableDocuments(java.util.Map)
     */
    @Override
    public Collection findPurchasingAccountsPayableDocuments(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new PurchasingAccountsPayableDocument());
        QueryByCriteria query = QueryFactory.newQuery(PurchasingAccountsPayableDocument.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    /**
     * @see org.kuali.kfs.module.cab.dataaccess.PurchasingAccountsPayableReportDao#findGeneralLedgers(java.util.Map)
     */
    @Override
    public Iterator findGeneralLedgers(Map fieldValues) {
        LOG.debug("findGeneralLedgers started...");
        ReportQueryByCriteria query = getGeneralLedgerReportQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Generate Query object for GeneralLedgerEntry search.
     *
     * @param fieldValues
     * @return
     */
    protected ReportQueryByCriteria getGeneralLedgerReportQuery(Map fieldValues) {
        Collection docTypeCodes = getDocumentType(fieldValues);
        Collection activityStatusCodes = getActivityStatusCode(fieldValues);

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new GeneralLedgerEntry());

        // set document type code criteria
        if (!docTypeCodes.isEmpty()) {
            criteria.addIn(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, docTypeCodes);
        }
        // set activity status code criteria
        if (!activityStatusCodes.isEmpty()) {
            criteria.addIn(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE, activityStatusCodes);
        }
        ReportQueryByCriteria query = QueryFactory.newReportQuery(GeneralLedgerEntry.class, criteria);

        List attributeList = buildAttributeList(false);

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);
        return query;
    }


    /**
     * Get activity_statu_code
     *
     * @param fieldValues
     * @return
     */
    protected Collection getActivityStatusCode(Map fieldValues) {
        Collection activityStatusCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE)) {
            String fieldValue = (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
            if (KFSConstants.NON_ACTIVE_INDICATOR.equalsIgnoreCase(fieldValue)) {
                // when selected as 'N', search for active lines as 'M'-modified by CAB user,'N'- new
                activityStatusCodes.add(CabConstants.ActivityStatusCode.NEW);
                activityStatusCodes.add(CabConstants.ActivityStatusCode.MODIFIED);
            }
            fieldValues.remove(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        }
        return activityStatusCodes;
    }


    /**
     * Get Document type code selection
     *
     * @param fieldValues
     * @return
     */
    protected Collection getDocumentType(Map fieldValues) {
        Collection docTypeCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE)) {
            String fieldValue = (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
            if (StringUtils.isEmpty(fieldValue)) {
                docTypeCodes.add(CabConstants.PREQ);
                docTypeCodes.add(CabConstants.CM);
            }
            else {
                docTypeCodes.add(fieldValue);
            }
            // truncate the non-property filed
            fieldValues.remove(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        }

        return docTypeCodes;
    }


    /**
     * Build attribute list for select clause.
     *
     * @param isExtended
     * @return
     */
    protected List<String> buildAttributeList(boolean isExtended) {
        List attributeList = new ArrayList();

        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_DEBIT_CREDIT_CODE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.REFERENCE_FINANCIAL_DOCUMENT_NUMBER);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_DATE);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_SUBMIT_AMOUNT);
        attributeList.add(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        return attributeList;
    }

}
