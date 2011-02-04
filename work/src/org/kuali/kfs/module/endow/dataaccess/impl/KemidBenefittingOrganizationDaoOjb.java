/*
 * Copyright 2011 The Kuali Foundation.
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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class KemidBenefittingOrganizationDaoOjb extends PlatformAwareDaoBaseOjb implements KemidBenefittingOrganizationDao {

    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            c.addEqualTo(attributeName, value.trim());
            criteria.addOrCriteria(c);
        }        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidBenefittingOrganization.class, criteria);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<String> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            kemids.add(result.next().toString());
        }
        
        return kemids;
    }
}
