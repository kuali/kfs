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
