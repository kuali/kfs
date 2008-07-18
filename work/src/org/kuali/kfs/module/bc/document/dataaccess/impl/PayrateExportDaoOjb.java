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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class PayrateExportDaoOjb extends PlatformAwareDaoBaseOjb implements PayrateExportDao {

    public List<PendingBudgetConstructionAppointmentFunding> getFundingRecords(Integer budgetYear, String positionUnionCode) {
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        criteria.addNotEqualTo(KFSPropertyConstants.EMPLID, BCConstants.VACANT_EMPLID);
        criteria.addEqualTo(BCPropertyConstants.APPOINTMENT_FUNDING_DELETE_INDICATOR, "N");
        criteria.addEqualTo(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_UNION_CODE, positionUnionCode);
        criteria.addEqualTo(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.CONFIDENTIAL_POSITION, "N");
        
        List<PendingBudgetConstructionAppointmentFunding> records = (List<PendingBudgetConstructionAppointmentFunding>)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PendingBudgetConstructionAppointmentFunding.class, criteria, true));
        
        return records;
    }
    
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

}
