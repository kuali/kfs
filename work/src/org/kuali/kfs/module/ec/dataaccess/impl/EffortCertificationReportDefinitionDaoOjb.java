/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
