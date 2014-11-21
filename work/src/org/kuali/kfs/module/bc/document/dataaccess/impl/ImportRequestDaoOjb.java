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

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.util.ObjectModification;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.kfs.module.bc.document.dataaccess.ImportRequestDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.BusinessObject;

public class ImportRequestDaoOjb extends PlatformAwareDaoBaseOjb  implements ImportRequestDao {
   
    public void save(BusinessObject businessObject, boolean isUpdate) {
        getPersistenceBroker(true).store(businessObject, isUpdate ? ObjectModification.UPDATE : ObjectModification.INSERT); 
    }


    /**
     * 
     * @see org.kuali.kfs.module.bc.document.dataaccess.ImportRequestDao#findAllNonErrorCodeRecords()
     */
    public List<BudgetConstructionRequestMove> findAllNonErrorCodeRecords(String principalId) {
        Criteria criteria = new Criteria();
        criteria.addIsNull("requestUpdateErrorCode");
        criteria.addEqualToColumn("principalId", principalId);
        
        List<BudgetConstructionRequestMove> records = new ArrayList<BudgetConstructionRequestMove>(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionRequestMove.class, criteria)));
        
        return records;
    }

    
    public BudgetConstructionHeader getHeaderRecord(BudgetConstructionRequestMove record, Integer budgetYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, record.getSubAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        BudgetConstructionHeader header = (BudgetConstructionHeader)getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionHeader.class, criteria));
        
        return header;
    }

}

