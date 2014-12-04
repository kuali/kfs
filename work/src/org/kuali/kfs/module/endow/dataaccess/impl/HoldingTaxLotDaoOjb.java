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
