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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;


/**
 * 
 */
public class ProcessDaoOjb extends PlatformAwareDaoBaseOjb implements ProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessDaoOjb.class);

    public ProcessDaoOjb() {
        super();
    }
    
    public List<PaymentProcess> getAllExtractsToRun() {
        Criteria c = new Criteria();
        c.addEqualTo(PdpPropertyConstants.PaymentProcess.EXTRACTED_IND, false);
        c.addEqualTo(PdpPropertyConstants.PaymentProcess.FORMATTED_IND, true);
        return (List<PaymentProcess>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentProcess.class, c));
    }
    
    public PaymentProcess get(Integer procId) {
        LOG.debug("get() started");

        Criteria c = new Criteria();
        c.addEqualTo("id", procId);

        PaymentProcess p = (PaymentProcess) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PaymentProcess.class, c));
        if (p != null) {
            
            return p;
        }
        else {
            return null;
        }
    }

}

