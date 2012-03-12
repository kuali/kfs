/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ec.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao
 */
public class EffortCertificationReportDefinitionDaoOjb extends PlatformAwareDaoBaseOjb implements EffortCertificationReportDefinitionDao {

    /**
     * 
     * @see org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao#getAll()
     */
    public List<EffortCertificationReportDefinition> getAll() {
        return (List<EffortCertificationReportDefinition>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, new Criteria()));
    }

    /**
     * @see org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao#getOverlappingReportDefinitions(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public List<EffortCertificationReportDefinition> getAllOtherActiveByType(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_TYPE_CODE, effortCertificationReportDefinition.getEffortCertificationReportTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        
        Criteria subCriteria = new Criteria();
        Criteria subCriteriaReportNumber = new Criteria();
        
        subCriteria.addNotEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, effortCertificationReportDefinition.getUniversityFiscalYear());
        subCriteriaReportNumber.addNotEqualTo(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, effortCertificationReportDefinition.getEffortCertificationReportNumber());
        
        subCriteria.addOrCriteria(subCriteriaReportNumber);
        criteria.addAndCriteria(subCriteria);
        
        Collection col = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, criteria));

        Iterator i = col.iterator();
        List<EffortCertificationReportDefinition> overlappingReportDefinitions = new ArrayList();

        while (i.hasNext()) {
            EffortCertificationReportDefinition temp = (EffortCertificationReportDefinition) i.next();
            overlappingReportDefinitions.add(temp);
        }

        return overlappingReportDefinitions;
    }

    /**
     * @see org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao#getAllByYearAndPositionCode(java.lang.Integer, java.lang.String)
     */
    public List<EffortCertificationReport> getAllByYearAndPositionCode(Integer fiscalYear, String positionObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_BEGIN_FISCAL_YEAR, fiscalYear);
        
        Criteria criteria2 = new Criteria();
        criteria2.addEqualTo(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR, fiscalYear);
        criteria.addOrCriteria(criteria2);
        
        criteria.addEqualTo(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITIONS + "." + EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITION_OBJECT_GROUP_CODE, positionObjectCode);
        
        return (List<EffortCertificationReport>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, criteria));
    }
}
