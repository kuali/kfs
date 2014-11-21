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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.dataaccess.InvoiceRecurrenceDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class InvoiceRecurrenceDaoOjb extends PlatformAwareDaoBaseOjb implements InvoiceRecurrenceDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceDaoOjb.class);

    public Iterator<InvoiceRecurrence> getAllActiveInvoiceRecurrences() {
        LOG.debug("getAllActiveInvoiceRecurrences() started"); 

        Criteria criteria = new Criteria();
        criteria.addEqualTo("active", true);

        QueryByCriteria query = new QueryByCriteria(InvoiceRecurrence.class, criteria);
        query.addOrderByAscending("invoiceNumber");
        
        return (Iterator<InvoiceRecurrence>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
    
    public Iterator<InvoiceRecurrence> getAllInvoiceRecurrences() {
        LOG.debug("getAllInvoiceRecurrencees() started");
        Criteria criteria = new Criteria();
        QueryByCriteria query = new QueryByCriteria(InvoiceRecurrence.class, criteria);
        query.addOrderByAscending("invoiceNumber");
        
        return (Iterator<InvoiceRecurrence>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
        
    }

}
