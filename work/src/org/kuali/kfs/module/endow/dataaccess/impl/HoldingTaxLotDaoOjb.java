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

import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

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
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(java.lang.String)
     */
    public List<HoldingTaxLot> getTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(String securityId) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_ACRD_INC_DUE, 0);

        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao#getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(java.lang.String)
     */
    public List<HoldingTaxLot> getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(String securityId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, 0);

        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, criteria));
    }
}
