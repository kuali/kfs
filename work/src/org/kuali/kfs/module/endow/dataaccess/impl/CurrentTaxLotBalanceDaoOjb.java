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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CurrentTaxLotBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements CurrentTaxLotBalanceDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentTaxLotBalanceDaoOjb.class);

    protected SecurityDao securityDao;

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao#getAllCurrentTaxLotBalanceEntriesForSecurity(java.lang.String)
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalanceEntriesForSecurity(String securityId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID, securityId);

        return (Collection<CurrentTaxLotBalance>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CurrentTaxLotBalance.class, criteria));
    }

    /**
     * Prepares the criteria and selects the records from END_CRNT_TAX_LOT_BAL_T table
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalances(FeeMethod feeMethod, Collection securityClassCodes, Collection securityIds) {
        Collection<CurrentTaxLotBalance> currentTaxLotBalance = new ArrayList();

        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);

        Criteria criteria = new Criteria();

        if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL)) {
            criteria.addIn(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);
        }
        else {
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME)) {
                criteria.addEqualTo(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
            }

            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL)) {
                criteria.addEqualTo(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
            }
        }

        if (feeMethod.getFeeByClassCode() && feeMethod.getFeeBySecurityCode()) {
            securityIds.addAll(securityClassCodes);
            if (securityIds.size() > 0) {
                criteria.addIn(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID, securityIds);
            }
        }
        else {
            if (feeMethod.getFeeByTransactionType()) {
                if (securityClassCodes.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID, securityClassCodes);
                }
            }

            if (feeMethod.getFeeByETranCode()) {
                if (securityIds.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID, securityIds);
                }
            }
        }

        QueryByCriteria query = QueryFactory.newQuery(CurrentTaxLotBalance.class, criteria);

        currentTaxLotBalance = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return currentTaxLotBalance;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao#getSecurityClassCodes(java.lang.String)
     */
    public Collection getSecurityClassCodes(String feeMethodCode) {
        Collection securityClassCodes = new ArrayList();
        Collection<FeeClassCode> feeClassCodes = new ArrayList();

        if (StringUtils.isNotBlank(feeMethodCode)) {
            Map<String, String> crit = new HashMap<String, String>();

            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_CLASS_CODE_INCLUDE, EndowConstants.YES);

            QueryByCriteria query = QueryFactory.newQuery(FeeClassCode.class, criteria);

            feeClassCodes = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeClassCode feeClassCode : feeClassCodes) {
                Collection<Security> securities = securityDao.getSecuritiesBySecurityClassCode(feeClassCode.getFeeClassCode());
                for (Security security : securities) {
                    securityClassCodes.add(security.getId());
                }
            }
        }

        return securityClassCodes;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao#getSecurityIds(java.lang.String)
     */
    public Collection getSecurityIds(String feeMethodCode) {
        Collection securityIds = new ArrayList();
        Collection<FeeSecurity> feeSecuritys = new ArrayList();

        if (StringUtils.isNotBlank(feeMethodCode)) {
            Map<String, String> crit = new HashMap<String, String>();

            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_SECURITY_INCLUDE, EndowConstants.YES);

            QueryByCriteria query = QueryFactory.newQuery(FeeSecurity.class, criteria);

            feeSecuritys = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeSecurity feeSecurity : feeSecuritys) {
                securityIds.add(feeSecurity.getSecurityCode());
            }
        }

        return securityIds;
    }

    /**
     * Gets the securityDao attribute.
     * 
     * @return Returns the securityDao.
     */
    protected SecurityDao getSecurityDao() {
        return securityDao;
    }

    /**
     * Sets the securityDao attribute value.
     * 
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }
}
