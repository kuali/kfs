/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class HoldingTaxLotDaoOjb extends PlatformAwareDaoBaseOjb implements HoldingTaxLotDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getAllTaxLotsWithPositiveUnits(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Collection<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String incomePrincipalIndicator) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, incomePrincipalIndicator);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, 0);

        return (Collection<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getAllTaxLotsWithPositiveAmounts(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Collection<HoldingTaxLot> getAllTaxLotsWithPositiveCost(String kemid, String securityId, String registrationCode, String incomePrincipalIndicator) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, incomePrincipalIndicator);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_COST, 0);

        return (Collection<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(java.lang.String)
     */
    public Iterator getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(String securityId) {

        List result = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_ACRD_INC_DUE, 0);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(HoldingTaxLot.class, criteria);

        // set the selection attributes
        String attributeList[] = { EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, "sum(" + EndowPropertyConstants.HOLDING_TAX_LOT_ACRD_INC_DUE + ")" };
        query.setAttributes(attributeList);

        List groupByList = new ArrayList();

        groupByList.add(EndowPropertyConstants.KEMID);
        groupByList.add(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE);
        groupByList.add(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);


    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(java.lang.String)
     */
    public List<HoldingTaxLot> getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(String securityId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_COST, 0);

        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, criteria));
    }


}
