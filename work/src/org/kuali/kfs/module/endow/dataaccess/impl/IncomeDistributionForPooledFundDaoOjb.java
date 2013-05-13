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

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class IncomeDistributionForPooledFundDaoOjb extends PlatformAwareDaoBaseOjb implements IncomeDistributionForPooledFundDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getIncomeEntraCode(java.lang.String)
     */
    public String getIncomeEntraCode(String securityId) {
        // get the security
        Security security = (Security) getPersistenceBrokerTemplate().getObjectById(Security.class, securityId);        
        // get the transaction post code, using security
        ClassCode classCode = (ClassCode) getPersistenceBrokerTemplate().getObjectById(ClassCode.class, security.getSecurityClassCode());

        return classCode.getSecurityIncomeEndowmentTransactionPostCode();
    }
     
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao#getSecurityForIncomeDistribution()
     */
    public List<PooledFundValue> getPooledFundValueForIncomeDistribution(Date currentDate) {
        
        // get pooledFundValue query for each security id with DSTRB_PROC_ON_DT = current date, DSTRB_PROC_CMPLT = 'N', and the most recent value effective date
        Criteria subCrit = new Criteria();
        subCrit.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, false);
        subCrit.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, currentDate);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(PooledFundValue.class, subCrit);        
        subQuery.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID, "max(" + EndowPropertyConstants.VALUE_EFFECTIVE_DATE + ")"});
        subQuery.addGroupBy(EndowPropertyConstants.POOL_SECURITY_ID);      
        Iterator<?> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(subQuery);        
        if (!result.hasNext()) {
            return null;
        }
        
        Criteria crit = new Criteria();
        while (result.hasNext()) {
            Criteria c = new Criteria();
            Object[] data = (Object[]) result.next();
            String securityId = data[0].toString();
            String effectiveDate = data[1].toString();
            c.addEqualTo(EndowPropertyConstants.POOL_SECURITY_ID, securityId);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = df.parse(effectiveDate);
                c.addEqualTo(EndowPropertyConstants.VALUE_EFFECTIVE_DATE, new Date(date.getTime()));
            } catch (Exception e) {
                continue;
            }            
            crit.addOrCriteria(c);
        }
        
        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundValue.class, crit));
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
  
}
