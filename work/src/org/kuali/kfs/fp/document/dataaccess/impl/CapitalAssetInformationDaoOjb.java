/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.dataaccess.CapitalAssetInformationDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springmodules.orm.ojb.PersistenceBrokerTemplate;

public class CapitalAssetInformationDaoOjb extends PlatformAwareDaoBaseOjb implements CapitalAssetInformationDao {
    protected static final Logger LOG = Logger.getLogger(CapitalAssetInformation.class);

    @Override
    public int getNextCapitalAssetLineNumber(String financialDocumentNumber) {
        final PersistenceBrokerTemplate temp = getPersistenceBrokerTemplate();
        final String queryString = "";
        final Criteria cri = new Criteria();
        final ReportQueryByCriteria query = new ReportQueryByCriteria(CapitalAssetInformation.class, new String[]{"max(capitalAssetLineNumber)"}, cri );
        cri.addEqualTo("documentNumber", financialDocumentNumber);
        query.addGroupBy("documentNumber");

        final Iterator<Object> it = temp.getReportQueryIteratorByQuery(query);
        if (!it.hasNext()){
            return -1;
        }
        final Object[] values = (Object[])it.next();
        if (values.length == 0){
            return -1;
        }
        if (it.hasNext() || values.length > 1){
            /*I don't think throwing here is the right idea...*/
            LOG.warn("There were more than one return from a report that should have returned only 1! Continuing.");
        }
        BigDecimal value = (BigDecimal)values[0];
        /*do the add in BigDecimal so that we can accurately check for lost information *after* the add*/
        value = value.add(new BigDecimal(1));
        int ret = value.intValueExact();
        return ret;
    }

}
