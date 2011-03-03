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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;

public class SecurityReportingGroupDaoOjb {

//    public SecurityReportingGroup getSecurityReportGroupBySecurityId(String securityId) {
//        
//        Criteria criteria1 = new Criteria();
//        criteria1.addEqualTo(EndowPropertyConstants.SECURITY_ID, securityId);
//        QueryByCriteria qbc1 = QueryFactory.newQuery(SECURITY.class, criteria1);
//        
//        subQuery1.setAttributes(new String[] {EndowPropertyConstants.SECURITY_CLASS_CODE});
//                
//        Criteria subCrit2 = new Criteria();
//        subCrit2.addEqualTo(arg0, arg1)
//        subCrit1.addEqualTo(EndowPropertyConstants.SECURITY_ID, securityId);
//        ReportQueryByCriteria subQuery1 = QueryFactory.newReportQuery(SecurityReportingGroup.class, subCrit1); 
//        subQuery1.setAttributes(new String[] {EndowPropertyConstants.SECURITY_);
//        
//        criteria.addIn(EndowPropertyConstants.KEMID_TYP_PRIN_RESTR_CD, subQuery);
//        QueryByCriteria qbc = QueryFactory.newQuery(KEMID.class, criteria);
//        qbc.addOrderByAscending(EndowPropertyConstants.KEMID);
//        
//        return (List<KEMID>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);        
//    }
}
