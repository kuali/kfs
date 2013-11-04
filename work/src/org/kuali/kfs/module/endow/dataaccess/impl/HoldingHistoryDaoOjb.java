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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class HoldingHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements HoldingHistoryDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryDaoOjb.class);
    protected SecurityDao securityDao;

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryForBlance(org.kuali.kfs.module.endow.businessobject.FeeMethod, java.lang.String, java.lang.String)
     */
    public Collection<HoldingHistory> getHoldingHistoryForBlance(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds) {
        Collection<HoldingHistory> holdingHistory = new ArrayList();

        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);

        Criteria criteria = new Criteria();

        if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL)) {
            criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);
        }
        else {
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
            }

            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
            }
        }

        Collection securityClassCodes = new ArrayList();
        Collection securityIds = new ArrayList();

        if (feeMethod.getFeeByClassCode() && feeMethod.getFeeBySecurityCode()) {
            securityClassCodes = getSecurityClassCodes(feeMethodCodeForSecurityClassCodes);
            securityIds = getSecurityIds(feeMethodCodeForSecurityIds);

            securityIds.addAll(securityClassCodes);
            if (securityIds.size() > 0) {
                criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
            }
        }
        else {
            if (feeMethod.getFeeByClassCode()) {
                securityClassCodes = getSecurityClassCodes(feeMethodCodeForSecurityClassCodes);
                if (securityClassCodes.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityClassCodes);
                }
            }

            if (feeMethod.getFeeBySecurityCode()) {
                securityIds = getSecurityIds(feeMethodCodeForSecurityIds);
                if (securityIds.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
                }
            }
        }

        QueryByCriteria query = QueryFactory.newQuery(HoldingHistory.class, criteria);

        holdingHistory = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return holdingHistory;
    }

    /**
     * Gets the security codes for a given securityClassCode in END_FEE_CLS_CD_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return securityCodes
     */
    protected Collection getSecurityClassCodes(String feeMethodCode) {
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
     * Gets the security ids for a given securityClassCode in END_FEE_SEC_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return securityIds
     */
    protected Collection getSecurityIds(String feeMethodCode) {
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
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistory(java.lang.String, java.lang.String)
     */
    public List<HoldingHistory> getHoldingHistory(String kemid, KualiInteger medId) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, medId);
        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);

        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryByKemid(java.lang.String)
     */
    public List<HoldingHistory> getHoldingHistoryByKemid(String kemid) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);

        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public List<HoldingHistory> getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(String kemid, KualiInteger monthEndId, String ipInd) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, monthEndId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, BigDecimal.ZERO);
        if (ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME) || ipInd.equalsIgnoreCase(IncomePrincipalIndicator.PRINCIPAL)) {
            criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, ipInd);
        }

        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.KEMID);

        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

    }

    /**
     * Gets the sum of the specified attribute values
     * 
     * @param kemid
     * @param medId
     * @param securityId
     * @param ipInd
     * @param attributeName
     * @return
     */
    public BigDecimal getSumOfHoldginHistoryAttribute(String attributeName, String kemid, KualiInteger medId, String securityId, String ipInd) {

        BigDecimal total = BigDecimal.ZERO;

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, medId);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, BigDecimal.ZERO);
        if (ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME) || ipInd.equalsIgnoreCase(IncomePrincipalIndicator.PRINCIPAL)) {
            criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, ipInd);
        }

        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(HoldingHistory.class, criteria);
        rqbc.setAttributes(new String[] { "sum(" + attributeName + ")" });
        rqbc.addGroupBy(attributeName);
        Iterator<?> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);

        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            total = total.add((BigDecimal) data[0]);
        }

        return total;
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
