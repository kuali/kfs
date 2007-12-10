/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.effort.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao;

public class EffortCertificationReportDefinitionDaoOjb extends PlatformAwareDaoBaseOjb implements EffortCertificationReportDefinitionDao {
    
    public List<EffortCertificationReportDefinition> getOverlappingReportDefinitions(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("effortCertificationReportTypeCode", effortCertificationReportDefinition.getEffortCertificationReportTypeCode());
        Collection col = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, criteria));
        //Iterator i = col.iterator();
        List<EffortCertificationReportDefinition> overlappingReportDefinitions = new ArrayList();
       /* //TODO: can i do this with the ojb criteria?
        while (i.hasNext()) {
            EffortCertificationReportDefinition temp = (EffortCertificationReportDefinition) i.next();
            if (isOverlapping(temp, effortCertificationReportDefinition)) overlappingReportDefinitions.add(temp);
        }*/
        return overlappingReportDefinitions;
    }

    private boolean isOverlapping(EffortCertificationReportDefinition oldRecord, EffortCertificationReportDefinition newRecord) {
        //is old's start date before (inclusive) new start && old end after (inclusive) new's start
        if ( Integer.parseInt(oldRecord.getEffortCertificationReportBeginPeriodCode()) <= Integer.parseInt(newRecord.getEffortCertificationReportBeginPeriodCode()) &&
             Integer.parseInt(oldRecord.getEffortCertificationReportEndPeriodCode())  >=  Integer.parseInt(newRecord.getEffortCertificationReportBeginPeriodCode())) return true;
        
        //new start's before (inclusive) old start && new end's after (inclusive) old starts
        if ( Integer.parseInt(newRecord.getEffortCertificationReportBeginPeriodCode()) <= Integer.parseInt(oldRecord.getEffortCertificationReportBeginPeriodCode()) &&
                Integer.parseInt(newRecord.getEffortCertificationReportEndPeriodCode())  >=  Integer.parseInt(oldRecord.getEffortCertificationReportBeginPeriodCode())) return true;
        return false;
    }
    
}
