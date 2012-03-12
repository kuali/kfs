/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.sys.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * The OJB implementation of the UniversityDateDao
 */
public class UniversityDateDaoOjb extends PlatformAwareDaoBaseOjb implements UniversityDateDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversityDateDaoOjb.class);

    /**
     * Converts a java.util.Date to a java.sql.Date
     * 
     * @param date a java.util.Date to convert
     * @return a java.sql.Date
     */
    protected java.sql.Date convertDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    /**
     * Returns the last university date for a given fiscal year
     * 
     * @param fiscalYear the fiscal year to find the last date for
     * @return a UniversityDate record for the last day in the given fiscal year, or null if nothing can be found
     * @see org.kuali.kfs.sys.dataaccess.UniversityDateDao#getLastFiscalYearDate(java.lang.Integer)
     */
    @Override
    public UniversityDate getLastFiscalYearDate(Integer fiscalYear) {
        ReportQueryByCriteria subQuery;
        Criteria subCrit = new Criteria();
        Criteria crit = new Criteria();

        subCrit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
        subQuery.setAttributes(new String[] { "max(univ_dt)" });

        crit.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_DATE, subQuery);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Returns the first university date for a given fiscal year
     * 
     * @param fiscalYear the fiscal year to find the first date for
     * @return a UniversityDate record for the first day of the given fiscal year, or null if nothing can be found
     * @see org.kuali.kfs.sys.dataaccess.UniversityDateDao#getFirstFiscalYearDate(java.lang.Integer)
     */
    @Override
    public UniversityDate getFirstFiscalYearDate(Integer fiscalYear) {
        ReportQueryByCriteria subQuery;
        Criteria subCrit = new Criteria();
        Criteria crit = new Criteria();

        subCrit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
        subQuery.setAttributes(new String[] { "min(univ_dt)" });

        crit.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_DATE, subQuery);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

}
