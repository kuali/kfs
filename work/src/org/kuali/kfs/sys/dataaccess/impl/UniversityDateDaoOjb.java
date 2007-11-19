/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;

/**
 * The OJB implementation of the UniversityDateDao
 */
public class UniversityDateDaoOjb extends PlatformAwareDaoBaseOjb implements UniversityDateDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversityDateDaoOjb.class);

    /**
     * Returns a university date record based on a given java.sql.Date.
     * 
     * @param date a Date to find the corresponding University Date record
     * @return a University Date record if found, null if not
     * @see org.kuali.module.gl.dao.UniversityDateDao#getByPrimaryKey(java.sql.Date)
     */
    @Cached
    public UniversityDate getByPrimaryKey(Date date) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_DATE, date);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Returns a university date record based on java.util.Date
     * 
     * @param date a java.util.Date to find the corresponding University Date record
     * @return a University Date record if found, null if not
     * @see org.kuali.module.gl.dao.UniversityDateDao#getByPrimaryKey(java.sql.Date)
     */
    @Cached
    public UniversityDate getByPrimaryKey(java.util.Date date) {
        return getByPrimaryKey(convertDate(date));
    }

    /**
     * Converts a java.util.Date to a java.sql.Date
     * 
     * @param date a java.util.Date to convert
     * @return a java.sql.Date
     */
    private java.sql.Date convertDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    /**
     * Returns the last university date for a given fiscal year
     * 
     * @param fiscalYear the fiscal year to find the last date for
     * @return a UniversityDate record for the last day in the given fiscal year, or null if nothing can be found
     * @see org.kuali.module.gl.dao.UniversityDateDao#getLastFiscalYearDate(java.lang.Integer)
     */
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
     * @see org.kuali.module.gl.dao.UniversityDateDao#getFirstFiscalYearDate(java.lang.Integer)
     */
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

    /**
     * Returns all distinct accounting period codes from the table
     * 
     * @return a Collection of all distinct accounting period codes represented by UniversityDate records in the database
     * @see org.kuali.module.gl.dao.UniversityDateDao#getAccountingPeriodCode()
     */
    public Collection getAccountingPeriodCode() {
        Criteria criteria = new Criteria();

        ReportQueryByCriteria query = QueryFactory.newReportQuery(UniversityDate.class, criteria);
        query.setAttributes(new String[] { "distinct " + KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD });
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
