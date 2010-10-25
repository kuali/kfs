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
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;

public class IncomeDistributionForPooledFundDaoOjb extends PlatformAwareDaoBaseOjb implements IncomeDistributionForPooledFundDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getIncomeEntraCode(java.lang.String)
     */
    public String getIncomeEntraCode(String securityId) {
        // get the security
        Criteria crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.SECURITY_ID, securityId);
        Security security = (Security) getPersistenceBrokerTemplate().getObjectById(Security.class, crit);
        
        // get the transaction post code,using security
        crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.SECURITY_CLASS_CODE, security.getClassCode());
        return((ClassCode) getPersistenceBrokerTemplate().getObjectById(ClassCode.class, crit)).getSecurityIncomeEndowmentTransactionPostCode();
    }
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getHoldingTaxLotListGroupedBySecurityId(java.lang.String)
     */
    public List<BigDecimal> getHoldingTaxLotListGroupedBySecurityId(String securityId) {
        Criteria crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.SECURITY_ID, securityId);
        QueryByCriteria query = new QueryByCriteria(HoldingTaxLot.class, crit);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR);
        
        return (List<BigDecimal>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
 
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getSecurityForIncomeDistribution()
     */
    public List<PooledFundValue> getPooledFundValueForIncomeDistribution(Date currentDate) {
        
        // get pooledFundValue query with current date and the latest distribution income date with the most recent value effective date
        Criteria subCrit = new Criteria();
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(PooledFundValue.class, subCrit);        
        subQuery.setAttributes(new String[] {"max(" + EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE + ")"});
        subQuery.addGroupBy(EndowPropertyConstants.POOL_SECURITY_ID);
      
        // find PooledFundValue whose distribute income on date is the current date and income distribution complete is 'N' 
        Criteria crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, false);
        crit.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, currentDate);
        crit.addIn(EndowPropertyConstants.VALUE_EFFECTIVE_DATE, subQuery);
        
        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundValue.class, crit));
        
        //ReportQueryByCriteria subQuery1 = QueryFactory.newReportQuery(PooledFundValue.class, subCrit1);
        //subQuery1.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID});

        // get security query with the same security ids in pooledFundValue query above
        //Criteria crit = new Criteria();
        //crit.addIn(EndowPropertyConstants.SECURITY_ID, subQuery1);                
        //QueryByCriteria query = new QueryByCriteria(Security.class, crit);
        
        //return (List<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }     

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getKemidPayoutInstructionForECT(java.lang.String)
     */
    public List<KemidPayoutInstruction> getKemidPayoutInstructionForECT(String kemid, Date currentDate) {
        Criteria crit = new Criteria();
        Criteria crit2 = new Criteria();
        Criteria crit21 = new Criteria();
        
        crit.addEqualTo(EndowPropertyConstants.KEMID_PAY_INC_KEMID, kemid);
        crit.addNotEqualTo(EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID, kemid);
        crit.addLessOrEqualThan(EndowPropertyConstants.KEMID_PAY_INC_START_DATE, currentDate);
        crit2.addGreaterThan(EndowPropertyConstants.KEMID_PAY_INC_END_DATE, currentDate);        
        crit21.addIsNull(EndowPropertyConstants.KEMID_PAY_INC_END_DATE);                
        crit2.addOrCriteria(crit21);        
        crit.addAndCriteria(crit2);
                        
        return (List<KemidPayoutInstruction>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(KemidPayoutInstruction.class, crit));
    }

    /**
     * @deprecated
     */
//    public BigDecimal getDistributionAmount(String securityId) {
//        BigDecimal totalDistributionAmount = new BigDecimal(0);        
//        Map<String, Object> fieldValues = new HashMap<String, Object>();
//        fieldValues.put("pooledSecurityID", securityId);
//        List<PooledFundValue> pooledFundValueList = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);
//        for (PooledFundValue pooledFundValue : pooledFundValueList) {
//            totalDistributionAmount.add(pooledFundValue.getIncomeDistributionPerUnit());
//        }        
//        return totalDistributionAmount;
//    }
    
    /**
     * @deprecated
     */
    public List<HoldingTaxLot> getHoldingTaxLotForIncomeDistribution() {

        // get pooledFundValue query with current date and the latest distribution income date
//        Criteria subCrit0 = new Criteria();
//        ReportQueryByCriteria subQuery0 = QueryFactory.newReportQuery(PooledFundValue.class, subCrit0);        
//        subQuery0.setAttributes(new String[] {"max(" + EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE + ")"});
//        subQuery0.addGroupBy(EndowPropertyConstants.POOL_SECURITY_ID);
        
        Criteria subCrit1 = new Criteria();
        //subCrit1.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, "N");
        //subCrit1.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, kemService.getCurrentDate());
        //subCrit1.addIn(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, subQuery0);
        ReportQueryByCriteria subQuery1 = QueryFactory.newReportQuery(PooledFundValue.class, subCrit1);
        subQuery1.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID});

        // get security query with the same security ids in pooledFundValue query
        Criteria subCrit2 = new Criteria();
        subCrit2.addIn(EndowPropertyConstants.SECURITY_ID, subQuery1);
        ReportQueryByCriteria subQuery2 = QueryFactory.newReportQuery(Security.class, subCrit2);
        subQuery2.setAttributes(new String[] {EndowPropertyConstants.SECURITY_ID});
                
        // get holding tax lot query with the same security ids in security query
        Criteria crit = new Criteria();
        crit.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, new BigDecimal(0));
        crit.addIn(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, subQuery2);
        QueryByCriteria query = new QueryByCriteria(HoldingTaxLot.class, crit);
        
        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }  
  
}
