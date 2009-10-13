/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.KualiInteger;


/**
 * 
 */
public class ProcessDaoOjb extends PlatformAwareDaoBaseOjb implements ProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessDaoOjb.class);

    private org.kuali.rice.kim.service.PersonService userService;

    public void setPersonService(org.kuali.rice.kim.service.PersonService u) {
        userService = u;
    }

    public ProcessDaoOjb() {
        super();
    }

    public PaymentProcess createProcessToRun(Integer procId) {
        LOG.debug("createProcess() started");

        Date d = new Date();
        Timestamp now = new Timestamp(d.getTime());

        PaymentProcess p = new PaymentProcess();
        p.setProcessTimestamp(now);
        p.setId(new KualiInteger(procId));
        p.setExtractedInd(false);
        
        getPersistenceBrokerTemplate().store(p);
        
        return p;
    }
    
    public List<PaymentProcess> getAllExtractsToRun() {
        Criteria c = new Criteria();
        c.addEqualTo(PdpPropertyConstants.PaymentProcess.EXTRACTED_IND, false);
        c.addEqualTo(PdpPropertyConstants.PaymentProcess.FORMATTED_IND, true);
        return (List<PaymentProcess>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentProcess.class, c));
    }
    
    public void setExtractProcessAsComplete(PaymentProcess paymentProcess) {
        paymentProcess.setExtractedInd(true);
        getPersistenceBrokerTemplate().store(paymentProcess);
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

