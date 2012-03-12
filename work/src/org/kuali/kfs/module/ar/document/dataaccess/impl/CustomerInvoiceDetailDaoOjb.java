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

import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDetailDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class CustomerInvoiceDetailDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerInvoiceDetailDao{

    /**
     * This method retrieves all CustomerInvoiceDetail objects for the accountNumber if invoiceNumber is from the invoiceNumberList
     * @return CustomerInvoiceDetail objects
     */
    public Collection getCustomerInvoiceDetailsByAccountNumberByInvoiceDocumentNumbers(String accountNumber,List documentNumbers) {
        Criteria criteria = new Criteria();
        criteria.addIn(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumbers);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.ACCOUNT_NUMBER, accountNumber);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CustomerInvoiceDetail.class, criteria));
    }
}
