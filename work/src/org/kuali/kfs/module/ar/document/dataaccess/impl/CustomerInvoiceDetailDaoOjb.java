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
