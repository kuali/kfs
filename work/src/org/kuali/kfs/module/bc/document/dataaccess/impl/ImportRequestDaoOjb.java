/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.ojb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.ObjectModification;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.ImportRequestDao;

public class ImportRequestDaoOjb extends PlatformAwareDaoBaseOjb  implements ImportRequestDao {
    
   /**
    * 
    * @see org.kuali.module.budget.dao.ImportRequestDao#getBudgetConstructionMonthlyVersionNumber(org.kuali.module.budget.bo.BudgetConstructionMonthly)
    */
    public Long getBudgetConstructionMonthlyVersionNumber(BudgetConstructionMonthly pendingEntry) {
        Long versionNumber = null;             
        
        Criteria criteriaID = new Criteria();          
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, pendingEntry.getUniversityFiscalYear());          
        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, pendingEntry.getChartOfAccountsCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, pendingEntry.getAccountNumber());          
        criteriaID.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, pendingEntry.getSubAccountNumber());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, pendingEntry.getFinancialObjectCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, pendingEntry.getFinancialSubObjectCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, pendingEntry.getFinancialBalanceTypeCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, pendingEntry.getFinancialObjectTypeCode());             
        criteriaID.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, pendingEntry.getDocumentNumber());  
        
        String[] queryAttr = {KFSPropertyConstants.VERSION_NUMBER};             
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(BudgetConstructionMonthly.class, queryAttr, criteriaID, true);             
        Iterator results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);             
        
        if (results.hasNext()) {            
            BigDecimal temp = (BigDecimal) ((Object[])results.next()) [0];      
            versionNumber = new Long (temp.toString());     
        }             
        return versionNumber;  
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.ImportRequestDao#getPendingBudgetConstructionGeneralLedgerVersionNumber(org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger)
     */
    public Long getPendingBudgetConstructionGeneralLedgerVersionNumber(PendingBudgetConstructionGeneralLedger pendingEntry) {
        Long versionNumber = null;             
        
        Criteria criteriaID = new Criteria();          
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, pendingEntry.getUniversityFiscalYear());          
        criteriaID.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, pendingEntry.getChartOfAccountsCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, pendingEntry.getAccountNumber());          
        criteriaID.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, pendingEntry.getSubAccountNumber());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, pendingEntry.getFinancialObjectCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, pendingEntry.getFinancialSubObjectCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, pendingEntry.getFinancialBalanceTypeCode());          
        criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, pendingEntry.getFinancialObjectTypeCode());             
        criteriaID.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, pendingEntry.getDocumentNumber());  
        
        String[] queryAttr = {KFSPropertyConstants.VERSION_NUMBER};             
        ReportQueryByCriteria queryID = new ReportQueryByCriteria(PendingBudgetConstructionGeneralLedger.class, queryAttr, criteriaID, true);             
        Iterator results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);             
        
        if (results.hasNext()) {            
            BigDecimal temp = (BigDecimal) ((Object[])results.next()) [0];   
            versionNumber = new Long (temp.toString());
        }             
        return versionNumber;  
    }


    public void save(BusinessObject businessObject, boolean isUpdate) {
        getPersistenceBroker(true).store(businessObject, isUpdate ? ObjectModification.UPDATE : ObjectModification.INSERT); 
    }


    /**
     * 
     * @see org.kuali.module.budget.dao.ImportRequestDao#findAllNonErrorCodeRecords()
     */
    public List<BudgetConstructionRequestMove> findAllNonErrorCodeRecords() {
        Criteria criteria = new Criteria();
        criteria.addIsNull("requestUpdateErrorCode");
        
        List<BudgetConstructionRequestMove> records = new ArrayList<BudgetConstructionRequestMove>(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionRequestMove.class, criteria)));
        
        return records;
    }

    
    public BudgetConstructionHeader getHeaderRecord(BudgetConstructionRequestMove record, Integer budgetYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", record.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", record.getAccountNumber());
        criteria.addEqualTo("subAccountNumber", record.getSubAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        BudgetConstructionHeader header = (BudgetConstructionHeader)getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionHeader.class, criteria));
        
        return header;
    }

}
