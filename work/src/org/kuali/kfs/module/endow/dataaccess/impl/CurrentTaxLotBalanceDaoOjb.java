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

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class CurrentTaxLotBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements CurrentTaxLotBalanceDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao#getAllCurrentTaxLotBalanceEntriesForSecurity(java.lang.String)
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalanceEntriesForSecurity(String securityId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID, securityId);

        return (Collection<CurrentTaxLotBalance>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CurrentTaxLotBalance.class, criteria));
    }

}
