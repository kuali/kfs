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
