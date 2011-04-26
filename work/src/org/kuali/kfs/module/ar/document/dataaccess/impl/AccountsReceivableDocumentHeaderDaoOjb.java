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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.dataaccess.AccountsReceivableDocumentHeaderDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DocumentService;

public class AccountsReceivableDocumentHeaderDaoOjb extends PlatformAwareDaoBaseOjb implements AccountsReceivableDocumentHeaderDao {
    
    public Collection getARDocumentHeadersByCustomerNumber(String customerNumber){
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER,customerNumber);
 
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }
    
    public Collection getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(String customerNumber, String processingChartOfAccountCode, String processingOrganizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER,customerNumber);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_COA_CODE,processingChartOfAccountCode);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_ORGANIZATION_CODE,processingOrganizationCode);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }
}
