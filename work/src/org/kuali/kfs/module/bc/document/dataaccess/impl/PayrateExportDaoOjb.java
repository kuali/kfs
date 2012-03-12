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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PayrateExportDaoOjb extends PlatformAwareDaoBaseOjb implements PayrateExportDao {

    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao#isValidPositionUnionCode(java.lang.String)
     */
    public boolean isValidPositionUnionCode(String positionUnionCode) {
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(BCPropertyConstants.POSITION_UNION_CODE, positionUnionCode);
        
        if (getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionPosition.class, criteria)).size() == 0) return false;
        
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao#buildPayRateHoldingRows(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public Integer buildPayRateHoldingRows(Integer budgetYear, String positionUnionCode, String principalId) {
        Integer rowsSaved = 0;
        
        Iterator<Object[]> payRateRows =  getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryForPayrateHoldingRecords(budgetYear, positionUnionCode));
        while (payRateRows.hasNext())
        {
            Object[] payRateRow = payRateRows.next();
            BudgetConstructionPayRateHolding payRateHolder = new BudgetConstructionPayRateHolding();
            payRateHolder.setAppointmentRequestedPayRate(new BigDecimal(0));
            payRateHolder.setEmplid((String) payRateRow[0]);
            payRateHolder.setPositionNumber((String) payRateRow[1]);
            payRateHolder.setName((String) payRateRow[2]);
            payRateHolder.setSetidSalary((String) payRateRow[3]);
            payRateHolder.setSalaryAdministrationPlan((String) payRateRow[4]);
            payRateHolder.setGrade((String) payRateRow[5]);
            payRateHolder.setUnionCode((String) payRateRow[6]);
            payRateHolder.setPrincipalId(principalId);
            
            getPersistenceBrokerTemplate().store(payRateHolder);
            rowsSaved = rowsSaved+1;
        }
        return rowsSaved;
    }

    /**
     * Selects the unique list of PendingBudgetConstructionAppointmentFunding to populate the payrate holding table for export
     * This method...
     * @param budgetYear
     * @param positionUnionCode
     * @return
     */
    protected ReportQueryByCriteria queryForPayrateHoldingRecords(Integer budgetYear, String positionUnionCode) {
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        criteria.addNotEqualTo(KFSPropertyConstants.EMPLID, BCConstants.VACANT_EMPLID);
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, "Y");
        criteria.addEqualTo(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_UNION_CODE, positionUnionCode);
        criteria.addEqualTo(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.CONFIDENTIAL_POSITION, "N");
        
        
        ReportQueryByCriteria queryId = new ReportQueryByCriteria(PendingBudgetConstructionAppointmentFunding.class, criteria, true);
        
        String[] selectList = new String[7];
        selectList[0] = KFSPropertyConstants.EMPLID;
        selectList[1] = KFSPropertyConstants.POSITION_NUMBER;
        selectList[2] = BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT + "." + KFSPropertyConstants.PERSON_NAME;
        selectList[3] = BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.SET_SALARY_ID;
        selectList[4] = BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_SALARY_PLAN_DEFAULT;
        selectList[5] = BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_GRADE_DEFAULT;
        selectList[6] = BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_UNION_CODE;
        
        queryId.setAttributes(selectList);
        
        return queryId;
    }
}

