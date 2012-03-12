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

import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateImportDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PayrateImportDaoOjb extends PlatformAwareDaoBaseOjb implements PayrateImportDao {

    public List<PendingBudgetConstructionAppointmentFunding> getFundingRecords(BudgetConstructionPayRateHolding holdingRecord, Integer budgetYear, Collection<String> objectCodeValues) {
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCodeValues);
        criteria.addEqualTo(KFSPropertyConstants.EMPLID, holdingRecord.getEmplid());
        criteria.addEqualTo(KFSPropertyConstants.POSITION_NUMBER, holdingRecord.getPositionNumber());
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, "Y");
        
        List<PendingBudgetConstructionAppointmentFunding> records = (List<PendingBudgetConstructionAppointmentFunding>)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PendingBudgetConstructionAppointmentFunding.class, criteria));
        
        return records;
    }
    
}
