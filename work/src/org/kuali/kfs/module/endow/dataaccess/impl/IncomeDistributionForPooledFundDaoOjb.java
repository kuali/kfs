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
import java.util.List;

import org.apache.ojb.broker.metadata.FieldHelper;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class IncomeDistributionForPooledFundDaoOjb extends PlatformAwareDaoBaseOjb implements IncomeDistributionForPooledFundDao {

    protected KEMService kemService;

//    public List<PooledFundValue> getPooledFundValues() {
//        Map<String, Object> fieldValues = new HashMap<String, Object>();
//        fieldValues.put("distributeIncomeOnDate", kemService.getCurrentDate());
//        fieldValues.put("incomeDistributionComplete", "N");
//        List<PooledFundValue> pooledFundValues = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);    
//    }
    
    public List<HoldingTaxLot> getHoldingTaxLotList() {
        
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
//        QueryByCriteria query = new QueryByCriteria(HoldingTaxLot.class, crit2);
//        query.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
//        query.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE);
//        query.addOrderBy(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR);
        
        return (List<HoldingTaxLot>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(HoldingTaxLot.class, crit2));
    }  
    
    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
        
}
