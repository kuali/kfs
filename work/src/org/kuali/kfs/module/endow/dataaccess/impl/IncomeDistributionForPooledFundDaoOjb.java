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
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;

public class IncomeDistributionForPooledFundDaoOjb extends PlatformAwareDaoBaseOjb implements IncomeDistributionForPooledFundDao {

    protected KEMService kemService;
    protected BusinessObjectService businessObjectService;

//    public List<PooledFundValue> getPooledFundValues() {
//        Map<String, Object> fieldValues = new HashMap<String, Object>();
//        fieldValues.put("distributeIncomeOnDate", kemService.getCurrentDate());
//        fieldValues.put("incomeDistributionComplete", "N");
//        List<PooledFundValue> pooledFundValues = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);    
//    }
    
    public String getIncomeEntraCode(String securityId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("securityId", securityId);
        String classCode = ((Security)businessObjectService.findByPrimaryKey(Security.class, fieldValues)).getSecurityClassCode();
        fieldValues.clear();
        fieldValues.put("code", classCode);
        return ((ClassCode)businessObjectService.findByPrimaryKey(ClassCode.class, fieldValues)).getSecurityIncomeEndowmentTransactionPostCode();        
    }
    
    public BigDecimal getDistributionAmount(String securityId) {
        BigDecimal totalDistributionAmount = new BigDecimal(0);        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("pooledSecurityID", securityId);
        List<PooledFundValue> pooledFundValueList = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);
        for (PooledFundValue pooledFundValue : pooledFundValueList) {
            totalDistributionAmount.add(pooledFundValue.getIncomeDistributionPerUnit());
        }        
        return totalDistributionAmount;
    }
    
    /**
     * 
     * Get the 
     * @param securityId
     * @return
     */
    public List<BigDecimal> getHoldingTaxLotListGroupedBy(String securityId) {
        Criteria crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.SECURITY_ID, securityId);
        QueryByCriteria query = new QueryByCriteria(HoldingTaxLot.class, crit);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE);
        query.addGroupBy(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR);
        
        return (List<BigDecimal>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * select * from end_hldg_tax_lot_t h
     * where sec_id in (
     * select sec_id from end_sec_t
     *    where sec_id in (
     *      select pool_sec_id from end_pool_fnd_val_t
     *        where dstrb_proc_on_dt in (
     *          select max(dstrb_proc_on_dt) from end_pool_fnd_val_t
     *            group by pool_sec_id
     *          )
     *          and dstrb_proc_cmplt = 'N';   
     *    )  
     * )
     * and hldg_units > 0;
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
 
    /**
     * @deprecated
     */
    public List<HoldingTaxLot> getHoldingTaxLotList2() {
        
        Criteria subCri = new Criteria();
        //subCri.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, kemService.getCurrentDate());
        //subCri.addLessOrEqualThan(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, kemService.getCurrentDate());
        //subCri.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, "N");        
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(PooledFundValue.class, subCri);
        subQuery.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID});
        Criteria crit1 = new Criteria();
        crit1.addIn(EndowPropertyConstants.SECURITY_ID, subQuery);
        List<Security> SecurityRecords = (List<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Security.class, crit1));
                
        Criteria crit2 = new Criteria();
        crit2.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, new BigDecimal(0));
        Criteria crit3 = new Criteria();
        for (Security security : SecurityRecords) {
            Criteria c = new Criteria();
            c.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, security.getId());
            crit3.addOrCriteria(c);
        }
        crit2.addAndCriteria(crit3);
        crit2.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
        crit2.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE);
        crit2.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR);        
        
        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, crit2));
    } 
    
    public List<KemidPayoutInstruction> getKemidPayoutInstructionForECT(String kemid) {
        Criteria crit1 = new Criteria();
        Criteria crit2 = new Criteria();
        crit1.addLessOrEqualThan(EndowPropertyConstants.KEMID_PAY_INC_START_DATE, kemService.getCurrentDate());
        crit1.addGreaterThan(EndowPropertyConstants.KEMID_PAY_INC_END_DATE, kemService.getCurrentDate());        
        crit2.addLessOrEqualThan(EndowPropertyConstants.KEMID_PAY_INC_START_DATE, kemService.getCurrentDate());
        crit2.addColumnIsNull(EndowPropertyConstants.KEMID_PAY_INC_END_DATE);                
        crit1.addOrCriteria(crit2);
        
        Criteria crit3 = new Criteria();
        crit3.addNotEqualToField(EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID, kemid);
        crit1.addAndCriteria(crit3);
        
        return (List<KemidPayoutInstruction>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(KemidPayoutInstruction.class, crit1));
    }
    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
  
}
