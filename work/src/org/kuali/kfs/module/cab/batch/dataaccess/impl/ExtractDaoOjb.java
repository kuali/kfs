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
package org.kuali.kfs.module.cab.batch.dataaccess.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;

public class ExtractDaoOjb extends PlatformAwareDaoBaseOjb implements ExtractDao {
    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findMatchingGLEntries(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<Entry> findMatchingGLEntries(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.Entry.TRANSACTION_DATE_TIME_STAMP, batchParameters.getLastRunTime());

        if (!batchParameters.getExcludedChartCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.Entry.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());

        if (!batchParameters.getExcludedSubFundCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.Entry.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());

        if (!batchParameters.getIncludedFinancialBalanceTypeCodes().isEmpty())
            criteria.addIn(CabPropertyConstants.Entry.FINANCIAL_BALANCE_TYPE_CODE, batchParameters.getIncludedFinancialBalanceTypeCodes());

        if (!batchParameters.getIncludedFinancialObjectSubTypeCodes().isEmpty())
            criteria.addIn(CabPropertyConstants.Entry.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());

        if (!batchParameters.getExcludedFiscalPeriods().isEmpty())
            criteria.addNotIn(CabPropertyConstants.Entry.UNIVERSITY_FISCAL_PERIOD_CODE, batchParameters.getExcludedFiscalPeriods());

        if (!batchParameters.getExcludedDocTypeCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.Entry.FINANCIAL_DOCUMENT_TYPE_CODE, batchParameters.getExcludedDocTypeCodes());

        QueryByCriteria query = new QueryByCriteria(Entry.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.Entry.DOCUMENT_NUMBER);
        query.addOrderByAscending(CabPropertyConstants.Entry.TRANSACTION_DATE_TIME_STAMP);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findPreTaggablePOAccounts(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<PurchaseOrderAccount> findPreTaggablePOAccounts(BatchParameters batchParameters) {
        Criteria statusCodeCond1 = new Criteria();
        statusCodeCond1.addEqualTo(CabPropertyConstants.PreTagExtract.PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE, CabConstants.CAPITAL_ASSET_SYSTEM_STATE_CODE_NEW);

        Criteria statusCodeOrCond = new Criteria();
        statusCodeOrCond.addIsNull(CabPropertyConstants.PreTagExtract.PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE);
        statusCodeOrCond.addOrCriteria(statusCodeCond1);

        Criteria criteria = new Criteria();
        Timestamp lastRunTimestamp = new Timestamp(((java.util.Date) batchParameters.getLastRunDate()).getTime());
        criteria.addGreaterThan(CabPropertyConstants.PreTagExtract.PO_INITIAL_OPEN_TIMESTAMP, lastRunTimestamp);
        criteria.addAndCriteria(statusCodeOrCond);
        criteria.addGreaterOrEqualThan(CabPropertyConstants.PreTagExtract.PURAP_ITEM_UNIT_PRICE, batchParameters.getCapitalizationLimitAmount());

        if (!batchParameters.getExcludedChartCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.PreTagExtract.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());

        if (!batchParameters.getExcludedSubFundCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.PreTagExtract.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());

        if (!batchParameters.getIncludedFinancialObjectSubTypeCodes().isEmpty())
            criteria.addIn(CabPropertyConstants.PreTagExtract.FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());

        QueryByCriteria query = new QueryByCriteria(PurchaseOrderAccount.class, criteria);
        Collection<PurchaseOrderAccount> purchaseOrderAccounts = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        
        List<String> docNumbersAwaitingPurchaseOrderStatus = getDocumentsNumbersAwaitingPurchaseOrderOpenStatus();
        Collection<PurchaseOrderAccount> purchaseOrderAcctsAwaitingPOOpenStatus = new ArrayList<PurchaseOrderAccount>();
        for (PurchaseOrderAccount purchaseOrderAccount : purchaseOrderAccounts) {
            if (docNumbersAwaitingPurchaseOrderStatus.contains(purchaseOrderAccount.getDocumentNumber())) {
                purchaseOrderAcctsAwaitingPOOpenStatus.add(purchaseOrderAccount);
            }
        }
        return purchaseOrderAcctsAwaitingPOOpenStatus;
    }

    protected List<String> getDocumentsNumbersAwaitingPurchaseOrderOpenStatus() {
        List<String> receivingDocumentNumbers = new ArrayList<String>();
             
        DocumentSearchCriteria.Builder documentSearchCriteriaDTO = DocumentSearchCriteria.Builder.create();
        documentSearchCriteriaDTO.setDocumentTypeName(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
        documentSearchCriteriaDTO.setApplicationDocumentStatus(CabConstants.PO_STATUS_CODE_OPEN);
        
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(null, documentSearchCriteriaDTO.build());

        String documentHeaderId = null;

        for (DocumentSearchResult result : results.getSearchResults()) {
            receivingDocumentNumbers.add(result.getDocument().getDocumentId());            
        }
        
        return receivingDocumentNumbers;
    }
    
    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findCreditMemoAccountHistory(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<CreditMemoAccountRevision> findCreditMemoAccountRevisions(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.CreditMemoAccountRevision.ACCOUNT_REVISION_TIMESTAMP, batchParameters.getLastRunTime());

        if (!batchParameters.getExcludedChartCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.CreditMemoAccountRevision.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());

        if (!batchParameters.getExcludedSubFundCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.CreditMemoAccountRevision.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());

        if (!batchParameters.getIncludedFinancialObjectSubTypeCodes().isEmpty())
            criteria.addIn(CabPropertyConstants.CreditMemoAccountRevision.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());

        QueryByCriteria query = new QueryByCriteria(CreditMemoAccountRevision.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.CreditMemoAccountRevision.ACCOUNT_REVISION_TIMESTAMP);
        Collection<CreditMemoAccountRevision> historyRecs = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return historyRecs;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findPaymentRequestAccountHistory(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<PaymentRequestAccountRevision> findPaymentRequestAccountRevisions(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.PaymentRequestAccountRevision.ACCOUNT_REVISION_TIMESTAMP, batchParameters.getLastRunTime());

        if (!batchParameters.getExcludedChartCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.PaymentRequestAccountRevision.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());

        if (!batchParameters.getExcludedSubFundCodes().isEmpty())
            criteria.addNotIn(CabPropertyConstants.PaymentRequestAccountRevision.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());

        if (!batchParameters.getIncludedFinancialObjectSubTypeCodes().isEmpty())
            criteria.addIn(CabPropertyConstants.PaymentRequestAccountRevision.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());

        QueryByCriteria query = new QueryByCriteria(PaymentRequestAccountRevision.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.PaymentRequestAccountRevision.ACCOUNT_REVISION_TIMESTAMP);
        Collection<PaymentRequestAccountRevision> historyRecs = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return historyRecs;
    }
}
