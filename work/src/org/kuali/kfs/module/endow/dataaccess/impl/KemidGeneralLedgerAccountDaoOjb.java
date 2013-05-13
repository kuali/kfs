/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.dataaccess.KemidGeneralLedgerAccountDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

public class KemidGeneralLedgerAccountDaoOjb extends PlatformAwareDaoBaseOjb implements KemidGeneralLedgerAccountDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidGeneralLedgerAccountDaoOjb.class);

    /**
     * @see KemidGeneralLedgerAccountDao#getChartAndAccountNumber(String, String)
     */
    public Map<String, String> getChartAndAccountNumber(String kemid, String incomePrincipalIndicator) {
        KemidGeneralLedgerAccount kemidGeneralLedgerAccount = null;
        Map<String, String> chartAndAccountNumber = new HashMap<String, String>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_ID, incomePrincipalIndicator);
        criteria.addEqualTo(EndowPropertyConstants.KEMID_GL_ACCOUNT_ROW_ACTV_IND, EndowConstants.YES);

        QueryByCriteria query = QueryFactory.newQuery(KemidGeneralLedgerAccount.class, criteria);
        kemidGeneralLedgerAccount = (KemidGeneralLedgerAccount) getPersistenceBrokerTemplate().getObjectByQuery(query);

        if (ObjectUtils.isNotNull(kemidGeneralLedgerAccount)) {
            chartAndAccountNumber.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_CHART_CD, kemidGeneralLedgerAccount.getChartCode());
            chartAndAccountNumber.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_NBR, kemidGeneralLedgerAccount.getAccountNumber());
        }

        return chartAndAccountNumber;
    }
}
