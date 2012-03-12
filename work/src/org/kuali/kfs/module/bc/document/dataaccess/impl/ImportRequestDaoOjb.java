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

